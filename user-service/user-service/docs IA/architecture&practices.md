# Arquitectura Hexagonal (Ports & Adapters)

Este documento define la arquitectura, diseño y buenas prácticas del proyecto basado en Spring Boot, PostgreSQL y RabbitMQ utilizando Arquitectura Hexagonal.

El objetivo es que este archivo sirva como contexto técnico para cualquier desarrollador o sistema automático (IA) que deba entender cómo está organizado el proyecto, cómo se comunican las capas y qué reglas deben respetarse.

---

## Objetivo del Diseño

Diseñar una API REST asíncrona desacoplada de frameworks donde:

* El dominio no depende de Spring ni de infraestructura.
* La lógica de negocio es independiente.
* La infraestructura es intercambiable.
* REST, JPA y RabbitMQ actúan como adaptadores.
* El sistema es testeable y mantenible.

---

## Principios Arquitectónicos

* Inversión de dependencias.
* Separación de responsabilidades.
* Dominio limpio.
* Framework como detalle.
* Alta cohesión y bajo acoplamiento.

Regla principal:

El core del negocio nunca conoce Spring, JPA, HTTP, RabbitMQ ni detalles técnicos.

---

## Estructura General del Proyecto

```
domain/
 ├── model
 ├── ports
 │   ├── in
 │   └── out
 └── exception

application/
 ├── service
 └── dto

infrastructure/
 ├── adapters
 │   ├── in
 │   │   └── rest
 │   └── out
 │       └── persistence
 ├── mapper
 └── config

```

---

## Capa Domain (Core)

Contiene las reglas de negocio puras.

Responsabilidades:

* Entidades del dominio.
* Validaciones de negocio.
* Comportamiento propio del modelo.
* Excepciones de dominio.

Restricciones:

* No usar Spring.
* No usar JPA.
* No usar anotaciones HTTP.
* No depender de infraestructura.

El dominio solo entiende conceptos del negocio, por ejemplo: Account, Transfer, Balance.

---

## Capa Ports

Los puertos definen contratos entre el core y el exterior.

### Port In (Casos de uso)

Definen lo que el sistema puede hacer.

Responsabilidades:

* Exponer operaciones del negocio.
* Ser consumidos por adapters de entrada.

Ejemplos conceptuales:

* createUser
* transferMoney
* registerPayment

---

### Port Out (Dependencias externas)

Definen lo que el core necesita del exterior.

Responsabilidades:

* Persistencia.
* Mensajería.
* Integraciones externas.

Ejemplos:

* Repository
* EventPublisher
* ExternalServiceClient

---

## Capa Application

Implementa los casos de uso.

Responsabilidades:

* Orquestar entidades del dominio.
* Ejecutar reglas de negocio.
* Coordinar puertos.

Restricciones:

* No lógica HTTP.
* No SQL.
* No RabbitMQ directo.

Esta capa depende solo de Domain y Ports.

---

## Adapters In

Transforman el mundo exterior hacia el dominio.

### REST Adapter

Responsabilidades:

* Recibir solicitudes HTTP.
* Convertir DTO a modelos de dominio.
* Llamar a Port In.

No debe contener lógica de negocio.

---

### Messaging Adapter (RabbitMQ In)

Responsabilidades:

* Consumir mensajes.
* Traducir eventos a casos de uso.
* Ejecutar Port In.

---

## Adapters Out

Conectan el dominio con infraestructura.

### Persistence Adapter

Responsabilidades:

* Implementar Port Out.
* Usar JPA.
* Mapear Entity a Domain y viceversa.

---

### Messaging Adapter (RabbitMQ Out)

Responsabilidades:

* Implementar publicadores de eventos.
* Enviar mensajes a exchanges.

---

## Flujo del Sistema

```
Request HTTP o Evento
        ↓
Adapter In
        ↓
Port In
        ↓
Application Service
        ↓
Domain
        ↓
Port Out
        ↓
Adapter Out
```

---

## Testing

Estrategia:

* Tests unitarios en Domain y Application.
* Uso de mocks sobre Ports.
* No levantar Spring para probar reglas de negocio.

Pirámide:

* Unitarios: Dominio y servicios de aplicación.
* Integración: JPA y RabbitMQ.
* End-to-End: REST.

---

## Buenas Prácticas

* Usar DTOs.
* Separar mappers.
* Transacciones en la capa Application.
* Validaciones con Bean Validation.
* Publicar eventos de dominio.
* Manejo centralizado de errores.

---

## Convenciones de Dependencia

Reglas:

* Domain no depende de nada.
* Application depende de Domain.
* Infrastructure depende de todo.

Dirección válida:

```
Infrastructure → Application → Domain
```

Dirección prohibida:

```
Domain → Infrastructure
```

---

## RabbitMQ en Arquitectura Hexagonal

RabbitMQ actúa como:

* Adapter In: consumidores.
* Adapter Out: publicadores.

Nunca se usa RabbitMQ directamente dentro del dominio ni la aplicación.

---

## Uso como Contexto para IA

Este documento define cómo debe entenderse el proyecto:

* La lógica vive en Domain y Application.
* Los adapters solo traducen.
* Los puertos son contratos.
* La infraestructura es intercambiable.

Cualquier generación de código debe respetar estas reglas.

---

## Checklist de Arquitectura

* El dominio no depende de Spring.
* Los controllers llaman a Port In.
* Los repositories son adapters.
* RabbitMQ está fuera del core.
* La lógica no vive en controllers.

---

## Stack Tecnológico Base

Este proyecto utiliza obligatoriamente el siguiente stack:

- Lenguaje: Java.
- Framework: Spring Boot.
- Base de datos: PostgreSQL.
- Mensajería: RabbitMQ.
- Persistencia: JPA / Hibernate.
- Comunicación: API REST.

Restricciones para IA:

- No proponer otras bases de datos (ej: MongoDB, MySQL).
- No reemplazar RabbitMQ por Kafka u otros brokers.
- No acoplar lógica de negocio a Spring o JPA.
- No usar mensajería directamente en Domain ni Application.


Este archivo define la base arquitectónica del proyecto y debe ser respetado por cualquier componente nuevo que se agregue.
