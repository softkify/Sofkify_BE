# DEUDA_TECNICA.md

Microservicio: product-service  
Arquitectura: Hexagonal (Ports & Adapters)  
Base de datos: PostgreSQL  
Mensajería: RabbitMQ (solo consumo de order-service)  
Fecha: 2026-02-15

---

# 1. INTRODUCCIÓN

Este documento registra la deuda técnica identificada en el microservicio product-service como parte del proceso de auditoría y evolución arquitectónica.

La deuda técnica se clasifica según el Cuadrante de Martin Fowler:

- Prudente / Deliberada
- Prudente / Inadvertida
- Imprudente / Deliberada
- Imprudente / Inadvertida

El objetivo no es listar pendientes, sino documentar decisiones técnicas conscientes y riesgos asumidos para su posterior priorización.

---

# 2. RESUMEN EJECUTIVO

El microservicio presenta:

Fortalezas:
- Arquitectura hexagonal correctamente aplicada.
- Dominio desacoplado de infraestructura.
- Validaciones ubicadas en el dominio.
- Mapeo manual explícito (sin magia oculta).
- Uso de PostgreSQL real.

Áreas de deuda técnica:
- Ausencia total de pruebas automatizadas.
- Falta de seguridad (Spring Security).
- Ausencia de migraciones versionadas.
- Consumo de mensajería sin resiliencia explícita.
- Falta de observabilidad y health checks formales.
- Configuración Docker incompleta (sin compose).

---

# 3. REGISTRO DE DEUDA TÉCNICA

---

## DT-01: Ausencia de pruebas automatizadas

Clasificación: Prudente / Deliberada

Descripción:
Actualmente no existen pruebas unitarias ni de integración en el microservicio.

Justificación:
Se priorizó la implementación funcional y validación manual del flujo.

Impacto:
- Alto riesgo de regresión.
- Difícil refactorización futura.
- Mayor costo de mantenimiento.

Riesgo Arquitectónico:
Alto.

Costo estimado de corrección:
Medio.

Prioridad:
Alta.

Plan de mitigación:
- Implementar pruebas unitarias en casos de uso.
- Agregar integración con @SpringBootTest.
- Validar consumo RabbitMQ.

---

## DT-02: Ausencia de Spring Security

Clasificación: Prudente / Deliberada

Descripción:
Todos los endpoints están expuestos sin autenticación ni autorización.

Justificación:
Se decidió postergar seguridad hasta estabilizar la arquitectura base.

Impacto:
- No apto para entorno productivo.
- Riesgo de acceso no autorizado.
- Exposición de datos.

Riesgo Arquitectónico:
Muy Alto.

Costo estimado:
Alto (requiere integración con auth-service o JWT).

Prioridad:
Alta.

Plan de mitigación:
- Implementar Spring Security.
- Integrar validación JWT.
- Definir roles explícitos.

---

## DT-03: Falta de migraciones versionadas (Flyway/Liquibase)

Clasificación: Prudente / Inadvertida

Descripción:
No existe versionado formal del esquema de base de datos.

Impacto:
- Riesgo en despliegues.
- Inconsistencias entre entornos.
- Falta de trazabilidad estructural.

Riesgo Arquitectónico:
Medio-Alto.

Costo estimado:
Medio.

Prioridad:
Alta antes de producción.

Plan de mitigación:
- Incorporar Flyway.
- Versionar esquema inicial.
- Integrar migraciones al pipeline.

---

## DT-04: Consumo RabbitMQ sin validación de resiliencia

Clasificación: Prudente / Inadvertida

Descripción:
El microservicio consume eventos desde order-service, pero no se ha validado:

- Retry policy.
- Dead Letter Queue (DLQ).
- Idempotencia.
- Confirmación de procesamiento.

Impacto:
- Pérdida silenciosa de mensajes.
- Procesamiento duplicado.
- Inconsistencias de estado.

Riesgo Arquitectónico:
Alto en entorno distribuido.

Costo estimado:
Medio.

Prioridad:
Media-Alta.

Plan de mitigación:
- Configurar DLQ.
- Implementar control de idempotencia.
- Agregar pruebas de integración con broker real.

---

## DT-05: Ausencia de Docker Compose

Clasificación: Prudente / Deliberada

Descripción:
Existe Dockerfile, pero no hay orquestación mediante docker-compose.

Impacto:
- Dificultad para levantar entorno completo.
- Dependencia manual de infraestructura.

Riesgo Arquitectónico:
Medio.

Costo estimado:
Bajo.

Prioridad:
Media.

---

## DT-06: Falta de Observabilidad Formal

Clasificación: Prudente / Inadvertida

Descripción:
No se encuentra habilitado:

- Spring Boot Actuator.
- Health checks formales.
- Métricas (Prometheus).
- Logs estructurados JSON.

Impacto:
- Baja visibilidad operacional.
- Dificultad para monitoreo en producción.

Riesgo Arquitectónico:
Medio.

Costo estimado:
Bajo-Medio.

Prioridad:
Media.

---

# 4. ELEMENTOS QUE NO CONSTITUYEN DEUDA

Se identifican aciertos arquitectónicos que NO representan deuda:

- Uso correcto de arquitectura hexagonal.
- Dominio libre de dependencias Spring.
- Validaciones encapsuladas en modelo de dominio.
- Uso de puertos para persistencia.
- Mapeo manual explícito (controlado).

Estos elementos demuestran alineación con principios SOLID y buen diseño.

---

# 5. PRIORIZACIÓN ESTRATÉGICA

Orden recomendado de abordaje:

1. Implementación de pruebas unitarias.
2. Incorporación de seguridad.
3. Migraciones versionadas.
4. Resiliencia en mensajería.
5. Observabilidad.
6. Docker Compose.

---

# 6. CONCLUSIÓN

El microservicio product-service presenta una base arquitectónica sólida alineada con arquitectura hexagonal y principios SOLID.

La deuda técnica identificada no corresponde a malas prácticas graves, sino a decisiones conscientes de priorización funcional propias de un MVP evolucionando hacia grado profesional.

El siguiente paso natural en su madurez es fortalecer:

- Calidad automatizada.
- Seguridad.
- Resiliencia distribuida.
- Observabilidad.

---

Versión: 1.0  
Estado: Auditoría de Deuda Técnica
