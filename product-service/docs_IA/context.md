# Contexto del Sistema – Ecommerce Asíncrono

## 1. Propósito del Documento

Este documento define el **contexto funcional y de negocio** del sistema Ecommerce Asíncrono. Es la **fuente de verdad del dominio** y debe ser utilizado por desarrolladores humanos y por sistemas de IA para comprender:

* Qué hace el sistema
* Qué conceptos de negocio existen
* Qué reglas gobiernan el dominio
* Qué aspectos están explícitamente fuera de alcance

Este archivo **NO define decisiones técnicas ni de implementación**. Dichas decisiones se documentan en `architecture.md`.

---

## 2. Propósito del Sistema

El sistema permite gestionar el proceso de compra de productos desde la consulta hasta la generación de una orden, actuando como núcleo de negocio para la creación de órdenes y la emisión de eventos asociados.

La creación de una orden representa un **hecho de negocio** y desencadena procesamiento asíncrono mediante eventos.

---

### 3. Alcance Funcional del Sistema

El sistema Ecommerce Asíncrono provee las siguientes capacidades funcionales:

* Registro y autenticación de Customers.
* Consulta de productos disponibles para compra.
* Gestión de categorías y productos por parte de administradores.
* Gestión de stock de productos y validación de disponibilidad.
* Creación y modificación de un carrito de compra por Customer.
* Confirmación de un carrito para generar una orden.
* Cancelación de órdenes creadas.
* Notificación al Customer ante la creación de una orden.

Notificación básica al Customer mediante correo electrónico cuando se crea una Order.

---

## 4. Actores del Negocio

### 4.1 Customer

Concepto de negocio que representa a la persona que realiza compras dentro del sistema.

Características:

* Es un concepto distinto de Administrator.
* Debe registrarse explícitamente en el sistema.
* Puede autenticarse para operar.

Responsabilidades:

* Consultar productos disponibles.
* Crear y modificar un carrito.
* Confirmar un carrito.
* Consultar órdenes creadas.

Restricciones:

* Un Customer eliminado lógicamente no puede crear carritos ni órdenes.

---

### 4.2 Administrator

Concepto de negocio que representa a un usuario con permisos administrativos.

Características:

* Es un concepto distinto de Customer.
* Tiene permisos totales sobre la gestión del sistema.

Responsabilidades:

* Crear, actualizar y desactivar productos.
* Crear y administrar categorías.

---

## 5. Lenguaje Ubicuo (Conceptos del Dominio)

Esta sección define **los conceptos oficiales del dominio** y su significado.

Los conceptos aquí descritos:

* No son modelos técnicos
* No son esquemas de base de datos
* No definen estructuras de persistencia

Definen **responsabilidades, reglas e invariantes de negocio**. Toda implementación debe respetar estos significados.

---

### Customer

Entidad de negocio que representa a una persona registrada para realizar compras.

Responsabilidad principal:

* Ser el titular de un carrito y de una orden.

Reglas de dominio:

* El Customer debe estar registrado para operar.
* El Customer puede autenticarse.
* El Customer se elimina únicamente de forma lógica (soft delete).
* El identificador documental del Customer es único dentro del sistema.

---

### Category

Entidad de negocio utilizada para clasificar productos.

Responsabilidad principal:

* Organizar productos para su administración y consulta.

Reglas de dominio:

* Una categoría puede existir sin productos asociados.
* Un producto puede existir sin categorías asociadas.
* La categoría no participa en el flujo de compra.

---

### Product

Entidad de negocio que representa un artículo vendible.

Responsabilidad principal:

* Ser ofrecido a los clientes y seleccionado para una compra.

Reglas de dominio:

* Todo Product tiene stock asociado.
* Solo productos activos y con stock disponible pueden agregarse a un Cart.
* El precio del Product debe ser mayor que cero.
* Un Product eliminado lógicamente no es visible ni seleccionable.

---

### Cart

Agregado de negocio que representa la **intención de compra** de un Customer.

Responsabilidad principal:

* Permitir construir una compra antes de su confirmación.

Reglas de dominio:

* El Cart se crea cuando el Customer agrega el primer producto.
* Un Customer solo puede tener un Cart activo.
* Un Cart activo puede modificarse.
* Al confirmarse, el Cart deja de existir como agregado y da origen a una Order.
* El sistema no gestiona carritos abandonados.

---

### CartItem

Elemento que compone un Cart.

Responsabilidad principal:

* Representar un producto seleccionado con una cantidad específica.

Reglas de dominio:

* La cantidad debe ser mayor que cero.
* La cantidad no puede exceder el stock disponible al momento de la selección.

---

### Order

Agregado de negocio que representa una **compra confirmada**.

Responsabilidad principal:

* Registrar de forma definitiva una compra realizada por el Customer.

Reglas de dominio:

* Una Order se crea únicamente a partir de un Cart confirmado.
* Un Customer solo puede tener una Order activa.
* La Order es inmutable una vez creada.
* La Order se crea con estado inicial CREATED.
* La Order puede ser cancelada.

---

### OrderItem

Elemento que compone una Order.

Responsabilidad principal:

* Registrar el detalle de cada producto comprado.

Reglas de dominio:

* El OrderItem contiene un snapshot del producto al momento de la compra.
* Cambios posteriores en el Product no afectan a la Order.

---

## 6. Reglas Generales del Negocio

Las siguientes reglas son **invariantes del dominio** y deben cumplirse en cualquier implementación.

### Customer

* Un Customer debe estar registrado para operar.
* Un Customer eliminado lógicamente no puede crear Cart ni Order.
* El identificador documental del Customer es único.

### Product

* El precio debe ser mayor que cero.
* El stock no puede ser negativo.
* Solo productos activos y con stock pueden agregarse a un Cart.

### Cart

* Un Customer solo puede tener un Cart activo.
* Un Cart activo puede modificarse.
* Un Cart confirmado no puede modificarse.

### Order

* Una Order se crea únicamente desde un Cart confirmado.
* Una Order es inmutable.
* Un Customer solo puede tener una Order activa.
* Una Order puede cancelarse.

---

## 7. Flujo de Negocio Principal

1. El Customer se encuentra registrado y autenticado.
2. El Customer consulta los productos disponibles.
3. El Customer agrega productos al Cart.
4. El sistema valida stock al agregar productos.
5. El Customer confirma el Cart.
6. El sistema crea una Order.
7. El sistema emite un evento de negocio `OrderCreated`.
8. Componentes externos reaccionan al evento de forma asíncrona.

---

## 8. Eventos de Negocio

### OrderCreated

Evento que representa el hecho de que una Order ha sido creada exitosamente.

Efectos secundarios esperados:

* Envío de una notificación por correo electrónico al Customer.

Semántica:

* El evento describe algo que ya ocurrió.
* El productor no espera respuesta.
* Otros componentes reaccionan de forma asíncrona.

---

## 9. Consistencia del Sistema

El sistema opera bajo un modelo de **consistencia eventual**.

* La creación de la Order es consistente de forma inmediata.
* Procesos secundarios se ejecutan de manera asíncrona.
* Se prioriza desacoplamiento y escalabilidad.

---

## 10. Fuera de Alcance

Las siguientes funcionalidades no forman parte del alcance actual:

* Pagos
* Envíos
* Facturación
* Gestión avanzada de usuarios
* Notificaciones avanzadas (SMS, push, multicanal)
* Gestión de reintentos o fallos de notificación
* Plantillas configurables de correo

---

## 11. Relación con Otros Documentos

Este documento se complementa con:

* `architecture.md`: decisiones técnicas y estructurales.
* `events.md`: contratos de eventos.
* `HU-*.md`: historias técnicas por microservicio.
* `AI_WORKFLOW.md`: estrategia de uso de IA.

Este archivo debe mantenerse consistente a medida que evoluciona el dominio.
