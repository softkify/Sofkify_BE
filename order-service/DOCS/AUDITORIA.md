# AUDITORIA.md

# Auditoría Técnica del Sistema - Order Service

## 1. Información General

Proyecto: Softkify

Repositorio: https://github.com/softkify/Sofkify_BE

Rama Evaluada:  

Commit Base (Snapshot): audit: snapshot post-mvp  

Fecha de Auditoría: 12/02/2026

Equipo Auditor: Backend

---

## 2. Contexto y Alcance

Documento correspondiente a la Fase 1 del reto técnico: Diagnóstico y Snapshot Arquitectónico.

El objetivo es evaluar el estado estructural del sistema posterior al MVP, identificando:

- Violaciones a principios SOLID.
- Antipatrones y code smells relevantes.
- Riesgos arquitectónicos.
- Impacto en escalabilidad, mantenibilidad y testabilidad.

Este documento no contempla refactorización, sino diagnóstico técnico fundamentado.

---

## 3. Observación General de Arquitectura

El order-service presenta una arquitectura hexagonal bien estructurada con clara separación de responsabilidades:

- **Organización por capas:** Estructura limpia con domain, application, e infrastructure claramente separados
- **Ports and Adapters:** Implementación correcta del patrón con use cases (input) y repository ports (output)
- **Dominio rico:** Los modelos Order y OrderItem contienen lógica de negocio y validaciones adecuadas
- **Inyección de dependencias:** Uso correcto de Spring para gestión de dependencias
- **Separación de concerns:** Controller, services y repository bien delimitados

El servicio sigue principios de DDD con agregados bien definidos y lógica de negocio contenida en el dominio.

---

## 4. Metodología de Evaluación

### 4.1 Criterios de Análisis

1. Revisión estructural por capas.
2. Cumplimiento de principios SOLID.
3. Evaluación de cohesión y acoplamiento.
4. Identificación de duplicación de lógica.
5. Análisis de dirección de dependencias.
6. Identificación de riesgos de escalabilidad.

### 4.2 Formato de Registro de Hallazgos

Cada hallazgo incluye:

- Archivo
- Línea(s)
- Principio vulnerado
- Descripción del problema
- Impacto técnico
- Riesgo arquitectónico
- Recomendación técnica

---

## 5. Mapa de Riesgo Técnico

| Hallazgo | Categoría | Severidad | Impacto | Probabilidad | Prioridad |
|----------|------------|-----------|---------|--------------|------------|
| SRP-01   | Diseño     | Media     | Media    | Alta         | Media      |
| SRP-02   | Datos      | Alta      | Alta     | Alta         | Alta       |
| SRP-03   | Datos      | Media     | Media    | Media        | Media      |
| OCP-01   | Diseño     | Media     | Media    | Alta         | Media      |
| ISP-01   | Diseño     | Baja      | Baja     | Baja         | Baja       |
| DIP-01   | Arquitectura | Media   | Media    | Media        | Media      |

---

Criterios:

- Severidad: Magnitud estructural.
- Impacto: Consecuencia operativa o evolutiva.
- Probabilidad: Frecuencia esperada.
- Prioridad: Urgencia de intervención.

---

## 6. Hallazgos

### 6.1 Single Responsibility Principle (SRP)

#### Hallazgo SRP-01

Archivo: OrderController.java  
Línea(s): 117-141  
Principio vulnerado: SRP (Single Responsibility Principle)  

Descripción del problema: El controller contiene dos métodos privados de transformación (toOrderResponse y toOrderItemResponse) que mezclan responsabilidad de presentación con transformación de datos.

Impacto técnico: Dificulta el testing unitario, acopla la capa de presentación con transformación de datos.

Riesgo arquitectónico: Violación de separación de concerns, posible introducción de errores en la capa incorrecta.

Recomendación técnica: Extraer la lógica de mapeo a una clase OrderDtoMapper separada.

#### Hallazgo SRP-02

Archivo: OrderJpaEntity.java  
Línea(s): 31-35  
Principio vulnerado: SRP (Single Responsibility Principle)  

Descripción del problema: La entidad JPA tiene campos duplicados para el mismo concepto: totalAmount (línea 31) y totalAmountField (línea 34). Esto indica confusión en el diseño y posible evolución del modelo.

Impacto técnico: Confusión en el modelo de datos, posible inconsistencia entre campos, desperdicio de espacio de almacenamiento.

Riesgo arquitectónico: Modelo de datos inconsistente, posibles errores de sincronización entre campos duplicados.

Recomendación técnica: Eliminar el campo duplicado y mantener solo un campo para el total amount.

#### Hallazgo SRP-03

Archivo: OrderItemJpaEntity.java  
Línea(s): 31-32  
Principio vulnerado: SRP (Single Responsibility Principle)  

Descripción del problema: La entidad JPA tiene un campo totalAmount que es redundante para items individuales, ya que cada item ya tiene su subtotal. Esto crea confusión en el modelo de datos.

Impacto técnico: Campo redundante en base de datos, posible inconsistencia entre totalAmount y subtotal, desperdicio de almacenamiento.

Riesgo arquitectónico: Modelo de datos confuso, posible corrupción de datos si los campos no se mantienen sincronizados.

Recomendación técnica: Eliminar el campo totalAmount de OrderItemJpaEntity ya que es redundante con el campo subtotal.

---

### 6.2 Open/Closed Principle (OCP)

#### Hallazgo OCP-01

Archivo: OrderController.java  
Línea(s): 117-141  
Principio vulnerado: OCP (Open/Closed Principle)  

Descripción del problema: Los métodos de transformación toOrderResponse (líneas 117-128) y toOrderItemResponse (líneas 131-140) están definidos directamente en el controller. Si se necesitan nuevas representaciones, se debe modificar el controller.

Impacto técnico: Acopla la capa de presentación con la transformación de DTOs, dificulta la reutilización y testing de las transformaciones.

Riesgo arquitectónico: Violación de separación de responsabilidades, posible duplicación de lógica de mapeo en otros controllers.

Recomendación técnica: Extraer la lógica de mapeo a una clase OrderDtoMapper separada con métodos específicos para cada transformación.

---

### 6.3 Liskov Substitution Principle (LSP)

No se encontraron violaciones significativas al principio LSP en el código analizado. Las jerarquías existentes son simples y bien definidas.

---

### 6.4 Interface Segregation Principle (ISP)

#### Hallazgo ISP-01

Archivo: CartServicePort.java  
Línea(s): 6-8  
Principio vulnerado: ISP (Interface Segregation Principle)  

Descripción del problema: La interfaz CartServicePort es muy específica pero podría beneficiarse de segregación si se agregan más operaciones de cart en el futuro.

Impacto técnico: Bajo - la interfaz actual es adecuada para sus necesidades actuales.

Riesgo arquitectónico: Bajo - no afecta funcionalmente pero podría requerir refactorización futura.

Recomendación técnica: Mantener la interfaz actual pero considerar segregación si se agregan más operaciones.

---

### 6.5 Dependency Inversion Principle (DIP)

#### Hallazgo DIP-01

Archivo: CreateOrderService.java  
Línea(s): 3-4  
Principio vulnerado: DIP (Dependency Inversion Principle)  

Descripción del problema: El servicio de aplicación depende directamente de DTOs de la capa de aplicación (CartItemResponse y CartResponse) a través de CartServicePort, creando acoplamiento entre bounded contexts.

Impacto técnico: Acoplamiento directo entre servicios, dificultad para testear y reemplazar implementaciones.

Riesgo arquitectónico: Rompe la autonomía de microservicios, crea dependencias directas entre bounded contexts.

Recomendación técnica: Utilizar eventos de dominio o mensajería asíncrona para comunicarse con otros servicios en lugar de dependencias directas.

---

## 7. Code Smells Relevantes

### 7.1 Complejidad elevada

No se detectaron métodos con complejidad ciclomática elevada. Los métodos mantienen un nivel de complejidad adecuado.

### 7.2 Duplicación de lógica

**Archivo:** OrderController.java  
**Línea(s):** 117-141  
**Problema:** El controller contiene métodos de transformación manual que deberían estar centralizados en una clase mapper dedicada.

**Impacto:** Violación de separación de responsabilidades, el controller no debe realizar transformaciones de datos. Dificulta la reutilización y testing de la lógica de mapeo.

### 7.3 Lógica de negocio en capa incorrecta

No se detectó lógica de negocio en capas incorrectas. La lógica está bien contenida en el dominio.

### 7.4 Validaciones duplicadas

**Archivo:** OrderItem.java  
**Línea(s):** 23-31  
**Problema:** Las validaciones en el constructor son adecuadas pero podrían extraerse a métodos privados reutilizables si se agregan más validaciones en el futuro.

**Impacto:** Bajo - las validaciones actuales son correctas y no están duplicadas.

### 7.5 Manejo inadecuado de excepciones en infraestructura

**Archivo:** CartServiceAdapter.java  
**Línea(s):** 33  
**Problema:** El adapter lanza RuntimeException genérica en lugar de excepciones específicas del dominio, perdiendo contexto del error.

**Impacto:** Pérdida de contexto del error, manejo genérico de excepciones, dificultad para debugging y tracing.

**Recomendación:** Lanzar excepciones específicas del dominio o crear excepciones de infraestructura apropiadas.

### 7.6 Configuración incompleta

**Archivo:** HttpClientConfig.java  
**Línea(s):** 13-16  
**Problema:** La configuración del RestTemplate está incompleta. El comentario indica que se deberían configurar timeouts e interceptores, pero no se implementa.

**Impacto:** Posibles timeouts por defecto inadecuados, falta de configuración de seguridad o logging, comportamiento impredecible en producción.

**Recomendación:** Implementar configuración completa de timeouts, retry, y otros aspectos necesarios para producción.

### 7.8 Campo redundante en entidad de item

**Archivo:** OrderItemJpaEntity.java  
**Línea(s):** 31-32  
**Problema:** Campo totalAmount redundante en items de orden, ya que cada item ya tiene su campo subtotal.

**Impacto:** Campo redundante en base de datos, posible inconsistencia entre totalAmount y subtotal, desperdicio de almacenamiento.

**Recomendación:** Eliminar campo totalAmount ya que es redundante con el campo subtotal existente.

---

## 8. Aciertos Arquitectónicos

### 8.1 Buen uso de abstracciones

- **Ports and Adapters:** Implementación correcta del patrón hexagonal con interfaces bien definidas
- **Dominio rico:** Los modelos Order y OrderItem contienen validaciones y comportamiento de negocio adecuado
- **Inyección de dependencias:** Uso correcto de constructor injection en todas las clases

### 8.2 Separación de capas

- **Estructura limpia:** Separación clara entre domain, application e infrastructure
- **DTOs bien definidos:** Separación adecuada entre modelos de dominio y de transporte
- **Use Cases:** Implementación correcta de patrones de casos de uso en el dominio

### 8.3 Buenas prácticas observadas

- **Inmutabilidad:** Uso correcto de campos finales en modelos de dominio
- **Validaciones:** Separación adecuada entre validaciones de dominio y de aplicación
- **Transaccionalidad:** Uso correcto de @Transactional en servicios de aplicación
- **Eventos de dominio:** Implementación correcta de eventos para desacoplamiento

---

## 9. Conclusión

El order-service presenta una arquitectura sólida con una base hexagonal bien implementada y clara separación de responsabilidades. La mayoría de los principios SOLID se cumplen adecuadamente y el código sigue buenas prácticas de DDD.

**Hallazgos principales:**
- **Arquitectura robusta:** Estructura hexagonal correctamente implementada
- **Dominio bien modelado:** Lógica de negocio contenida en los modelos Order y OrderItem
- **Bajo acoplamiento:** Uso correcto de interfaces e inyección de dependencias
- **Eventos de dominio:** Buen uso de eventos para desacoplamiento

**Oportunidades de mejora identificadas:**
- **Diseño:** Extraer lógica de mapeo a clases especializadas
- **Datos:** Eliminar campos duplicados y redundantes en entidades JPA
- **Arquitectura:** Revisar acoplamiento entre microservicios
- **Configuración:** Completar configuración de clientes HTTP
- **Manejo de errores:** Mejorar excepciones en infraestructura

**Impacto general:** Los hallazgos detectados son de baja a media severidad y no comprometen la funcionalidad actual. Las mejoras sugeridas incrementarán la mantenibilidad y testabilidad del sistema a largo plazo.

Este documento constituye la línea base para la fase de refactorización dirigida del order-service.