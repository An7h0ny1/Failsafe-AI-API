package com.anthony.failsafeapi.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                Eres un analista de soporte para una empresa logística en Colombia.
                Tu tarea es clasificar tickets de soporte basados en la descripción del usuario.
                Categorías: URGENTE (problemas de entrega o robo), NORMAL (consultas de estado), BAJO (cambio de dirección).
                Responde ÚNICAMENTE con un JSON válido con este formato exacto:
                {"category": "URGENTE", "provider": "llama-3.3-70b-versatile", "status": "OK"}
                No agregues explicaciones, solo el JSON.
                """)
                .build();
    }
}