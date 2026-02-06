# Arquitectura del Sistema – Ecommerce Asíncrono

## 1. Visión General

El sistema se implementa como un **ecosistema de microservicios** independientes, orientados al dominio, que se comunican mediante **APIs síncronas** cuando es estrictamente necesario y **eventos asíncronos** para desacoplar procesos de negocio y lograr **consistencia eventual**.

Cada microservicio es **autónomo**, posee su **propia base de datos**, y encapsula su **modelo de dominio** sin compartir estado con otros servicios.

### Alcance técnico del documento:

Este documento define la arquitectura interna de los microservicios y sus patrones de comunicación.

### No se incluyen en el alcance actual:

* Service Discovery (Eureka)
* Config Server / Config Repo
* Spring Cloud Bus

La comunicación síncrona entre microservicios se realiza mediante endpoints REST conocidos (URLs directas), definidos como Ports Out en la capa Application.

---

## 2. Principios Arquitectónicos

Los siguientes principios son obligatorios para todos los microservicios:

* **Arquitectura Hexagonal (Ports & Adapters)**
* **Clean Architecture**
* **DDD táctico** (agregados, entidades, reglas)
* **Principios SOLID**
* **Bajo acoplamiento / Alta cohesión**
* **Comunicación explícita mediante contratos** (APIs y eventos)

---

## 3. Arquitectura Interna de un Microservicio

Esta sección define la **estructura canónica** que debe seguir **cada microservicio** del sistema.

> Esta estructura es la referencia para la IA y para los desarrolladores humanos.

---

### 3.1 Capas del Microservicio

Cada microservicio se divide en las siguientes capas:

```
┌──────────────────────────────┐
│        Infrastructure        │  ← Frameworks, Web, Persistence, Messaging
├──────────────────────────────┤
│         Application          │  ← Casos de uso / Orquestación
├──────────────────────────────┤
│            Domain            │  ← Núcleo del negocio
└──────────────────────────────┘
```

Las **dependencias siempre apuntan hacia el dominio**.

---

### 3.2 Capa Domain

La capa **Domain** representa el núcleo del negocio.

Contiene:

* Entidades y Agregados
* Value Objects (cuando aplique)
* Enumeraciones del dominio
* Excepciones de negocio
* Reglas e invariantes
* Domain Events

#### Reglas clave

* El dominio **no depende** de Spring, JPA, RabbitMQ, ni frameworks.
* Un agregado **no modifica** directamente otro agregado.
* Las reglas de negocio viven aquí.

Ejemplo de estructura:

```
/domain
 ├── model
 ├── enums
 ├── exception
 └── event
```

---

### 3.3 Domain Events

Los **Domain Events** representan hechos de negocio que **ya ocurrieron**.

Ejemplos:

* `OrderCreated`
* `OrderCancelled`

Características:

* Se generan **dentro del dominio**.
* No conocen quién los consume.
* No contienen lógica técnica.

> Los Domain Events no se exponen directamente como eventos de integración.
> La infraestructura puede mapear un Domain Event a un Integration Event antes de publicarlo.
> El dominio solo expresa el evento; la publicación técnica se realiza fuera de él.

---

### 3.4 Capa Application

La capa **Application** coordina el flujo del sistema mediante **casos de uso explícitos**.

#### Casos de uso

* Cada **Use Case es una clase explícita**.
* Representan una **intención del negocio**.
* Pueden interactuar con múltiples agregados.

Ejemplos:

* `CreateOrderUseCase`
* `AddProductToCartUseCase`

#### Reglas clave

* Un use case puede modificar más de un agregado.
* El agregado **no coordina** otros agregados.
* El Application Service orquesta.

#### Puertos

* **Ports In**: interfaces de entrada (casos de uso).
* **Ports Out**: interfaces hacia infraestructura externa.

Ejemplo:

```
/application
 ├── port
 │   ├── in
 │   └── out
 └── service
```

Los servicios implementan explícitamente los puertos:

```
CreateOrderService implements CreateOrderUseCase
```

---

### 3.5 Publicación de Eventos

El flujo correcto es:

1. El dominio genera un Domain Event.
2. El Use Case lo recoge.
3. El Use Case invoca un **EventPublisher Port**.
4. La infraestructura publica el evento mediante el mecanismo de mensajería configurado (ej: RabbitMQ).


Esto garantiza:

* Dominio puro
* Infraestructura desacoplada
* Testabilidad

---

### 3.6 Capa Infrastructure

La capa **Infrastructure** contiene todas las implementaciones técnicas y no define reglas de negocio ni decisiones de dominio.

Incluye:

* Controladores REST
* DTOs (por agregado)
* Adaptadores de persistencia
* Implementaciones de mensajería
* Configuración técnica

Ejemplo:

```
/infrastructure
 ├── web
 ├── persistence
 ├── messaging
 └── exception
```

#### DTOs

* Los DTOs se organizan **por agregado**.
* Separación clara entre:

  * request
  * response

---

### 3.7 Persistencia

* Cada microservicio tiene su **propia base de datos**.
* No existen joins entre servicios.
* Los repositorios implementan **ports out**.

---

### 3.8 Comunicación entre Microservicios

* **Síncrona (REST)**:

  * Las llamadas síncronas se realizan siempre desde un Use Case hacia un Port Out que representa al servicio externo.

* **Asíncrona (Eventos)**:

  * Procesos derivados
  * Notificaciones
  * Desacoplamiento

---

### 3.9 Consistencia

El sistema opera bajo **consistencia eventual**.

* La operación principal es inmediata.
* Los efectos secundarios son asíncronos.

---

## 4. Convenciones de Nombres

* UseCases: `Verb + Noun + UseCase`
* Services: `Verb + Noun + Service`
* Ports In: `<Entity>UseCase`
* Ports Out: `<Entity><Purpose>Port`
* Domain Events: `<Entity><Action>Event`

---

## 5. Relación con Otros Documentos

* `context.md`: reglas y lenguaje del dominio
* `events.md`: contratos de eventos
* `HU-*.md`: historias técnicas
* `AI_WORKFLOW.md`: uso de IA

Este archivo define el **cómo** técnico; el dominio define el **qué**.

## 6. Documentación por Microservicio

Cada microservicio:

* Comparte el `architecture.md` general como referencia estructural.
* No define una arquitectura propia.
* Se documenta mediante:
  * Historias de usuario (`HU-*.md`)
  * Contratos de eventos (`events.md` cuando aplique)

Esto evita duplicación y divergencia arquitectónica.