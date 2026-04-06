package com.anthony.failsafeapi.service;

import com.anthony.failsafeapi.model.TicketResponse;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TicketServiceTest {

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        ticketService = new TicketService(null, meterRegistry);
    }

    @Test
    @DisplayName("Fallback: descripción con robo debe retornar URGENTE")
    void fallback_whenDescriptionContainsRobo_returnsUrgente() {
        TicketResponse response = ticketService.fallbackClassifyTicket(
                "Mi paquete fue robado ayer",
                new RuntimeException("AI timeout")
        );

        assertThat(response.category()).isEqualTo("URGENTE");
        assertThat(response.provider()).isEqualTo("DETERMINISTIC_FALLBACK_ENGINE");
        assertThat(response.status()).isEqualTo("EMERGENCY_MODE");
    }

    @Test
    @DisplayName("Fallback: descripción con cambio de dirección debe retornar BAJO")
    void fallback_whenDescriptionContainsDireccion_returnsBajo() {
        TicketResponse response = ticketService.fallbackClassifyTicket(
                "Necesito cambiar la dirección de entrega",
                new RuntimeException("Circuit open")
        );

        assertThat(response.category()).isEqualTo("BAJO");
    }

    @Test
    @DisplayName("Fallback: descripción genérica debe retornar NORMAL")
    void fallback_whenDescriptionIsGeneric_returnsNormal() {
        TicketResponse response = ticketService.fallbackClassifyTicket(
                "¿Cuál es el estado de mi pedido?",
                new RuntimeException("AI unavailable")
        );

        assertThat(response.category()).isEqualTo("NORMAL");
    }

    @ParameterizedTest
    @DisplayName("Fallback: todas las keywords de URGENTE")
    @CsvSource({
            "mi paquete fue robado",
            "el conductor tuvo un accidente",
            "el paquete se perdido en camino",
            "el pedido no llego nunca",
            "fui asaltado al recibir"
    })
    void fallback_urgentKeywords_returnUrgente(String description) {
        TicketResponse response = ticketService.fallbackClassifyTicket(
                description,
                new RuntimeException("test")
        );

        assertThat(response.category()).isEqualTo("URGENTE");
    }

    @ParameterizedTest
    @DisplayName("Fallback: todas las keywords de BAJO")
    @CsvSource({
            "quiero cambiar la dirección",
            "necesito modificar mi pedido",
            "cambio de direccion urgente"
    })
    void fallback_bajoKeywords_returnBajo(String description) {
        TicketResponse response = ticketService.fallbackClassifyTicket(
                description,
                new RuntimeException("test")
        );

        assertThat(response.category()).isEqualTo("BAJO");
    }
}