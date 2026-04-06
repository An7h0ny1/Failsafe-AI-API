# Failsafe AI API 🛡️

API de clasificación de tickets de soporte con resiliencia enterprise para sistemas de IA no-deterministas.

## ¿Qué problema resuelve?

Las empresas pierden confianza y dinero cuando un LLM falla, tarda demasiado o devuelve respuestas inválidas.
Este proyecto demuestra cómo hacer que la IA sea **confiable en producción** usando patrones enterprise de Java.

## Arquitectura

```text
Cliente HTTP
│
▼
TicketController (POST /api/v1/tickets/clasificar)
│
▼
TicketService
│
├─── Circuit Breaker (Resilience4j) ──────────────────────┐
│         │                                                │
│    [CERRADO]                                        [ABIERTO]
│         │                                                │
▼         ▼                                                ▼
│    ChatClient (Groq / Llama 3.3)          FallbackEngine (Determinista)
│         │                                                │
│    [timeout > 3s o error]                   keywords: robado, perdido...
│         │                                                │
└─────────┴────────────────────────────────────────────────┘
│
▼
TicketResponse
{ category, provider, status }
```
## Flujo de resiliencia

| Situación | Comportamiento |
|---|---|
| Groq responde OK | Categoría devuelta por el LLM |
| Groq tarda > 3s | TimeLimiter corta, activa fallback |
| 50% de llamadas fallan | Circuit Breaker se **abre** |
| Circuito abierto | Fallback determinista instantáneo |
| Después de 10s | Circuito pasa a **medio abierto** para probar |

## Stack técnico

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 | Virtual Threads para concurrencia eficiente |
| Spring Boot | 3.3+ | Framework base |
| Spring AI | 1.0+ | Abstracción sobre LLMs |
| Groq + Llama 3.3 | 70b | Inferencia rápida |
| Resilience4j | 2.x | Circuit Breaker + TimeLimiter |
| Micrometer | - | Métricas de tokens y latencia |

## Endpoints

### Clasificar ticket
```http
POST /api/v1/tickets/clasificar
Content-Type: application/json

{
  "description": "Mi paquete fue robado esta mañana"
}
```

**Respuesta normal (LLM activo):**
```json
{
  "category": "URGENTE",
  "provider": "llama-3.3-70b-versatile",
  "status": "OK"
}
```

**Respuesta en modo emergencia (fallback activo):**
```json
{
  "category": "URGENTE",
  "provider": "DETERMINISTIC_FALLBACK_ENGINE",
  "status": "EMERGENCY_MODE"
}
```

## Cómo correrlo
```bash
# 1. Clona el repositorio
git clone https://github.com/An7h0ny1/Failsafe-AI-API.git

# 2. Copia el archivo de configuración
cp src/main/resources/application.properties.example src/main/resources/application.properties

# 3. Edita application.properties y reemplaza YOUR_GROQ_API_KEY_HERE
# Obtén tu API key gratis en: https://console.groq.com

# 4. Corre el proyecto
./mvnw spring-boot:run
```

## Monitoreo del Circuit Breaker
```bash
# Estado del circuit breaker
GET /actuator/health

# Métricas de clasificación
GET /actuator/metrics/tickets.classification.ai.calls
GET /actuator/metrics/tickets.classification.fallback.calls
```

## Tests
```bash
./mvnw test
```

5 tests unitarios que validan la lógica del fallback determinista,
incluyendo tests parametrizados para todas las keywords críticas.

## ¿Por qué Java y no Python?

Con Java 21 Virtual Threads, cada llamada al LLM (que puede tardar 1-5s)
se maneja sin bloquear el pool de Tomcat. Esto permite manejar
**miles de clasificaciones concurrentes** con un costo de infraestructura
mucho menor que soluciones equivalentes en Python.