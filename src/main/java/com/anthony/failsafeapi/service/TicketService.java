package com.anthony.failsafeapi.service;

import com.anthony.failsafeapi.model.TicketResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final ChatClient chatClient;
    private final MeterRegistry meterRegistry;

    private static final String BREAKER_INSTANCE = "aiClassification";

    @CircuitBreaker(name = BREAKER_INSTANCE, fallbackMethod = "fallbackClassifyTicket")
    public TicketResponse classifyTicket(String description) {
        log.info("Calling Grok AI for description: {}", description);
        meterRegistry.counter("tickets.classification.ai.calls").increment();

        return chatClient.prompt()
                .user(description)
                .call()
                .entity(TicketResponse.class);
    }

    /**
     * Este método se ejecutará si Google Gemini:
     * 1. Lanza una excepción (error 500, API Key inválida, etc.)
     * 2. Tarda más de lo configurado (Timeout)
     * 3. El circuito está abierto
     */
    public TicketResponse fallbackClassifyTicket(String description, Throwable t) {
        log.warn("=== MODO EMERGENCIA ACTIVADO ===");
        log.warn("Motivo del fallo: {}", t.getMessage());
        meterRegistry.counter("tickets.classification.fallback.calls").increment();

        String emergencyCategory = determineEmergencyCategory(description);

        log.warn("Categoría asignada por fallback: {} | Descripción: {}", emergencyCategory, description);

        return new TicketResponse(
                emergencyCategory,
                "DETERMINISTIC_FALLBACK_ENGINE",
                "EMERGENCY_MODE"
        );
    }

    private String determineEmergencyCategory(String description) {
        String lower = description.toLowerCase();

        if (lower.contains("robado") || lower.contains("asaltado")
                || lower.contains("accidente") || lower.contains("perdido")
                || lower.contains("no llegó") || lower.contains("no llego")) {
            return "URGENTE";
        }

        if (lower.contains("dirección") || lower.contains("direccion")
                || lower.contains("cambio") || lower.contains("modificar")) {
            return "BAJO";
        }

        return "NORMAL";
    }
}
