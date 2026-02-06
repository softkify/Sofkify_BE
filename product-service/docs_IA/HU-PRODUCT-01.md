HU-PRODUCT-01 – Crear Producto
1. Identificación

ID: HU-PRODUCT-01

Nombre: Crear producto

Microservicio: product-service

Agregado principal: Product

Tipo: Backend

Prioridad: Alta

2. Descripción Funcional

Como Administrator,
quiero crear un producto en el sistema,
para que pueda ser ofrecido a los Customers y posteriormente seleccionado en un carrito de compra.

La creación de un producto es una operación administrativa y no forma parte del flujo de compra del Customer.

3. Actor(es)
   Actor principal

Administrator

Actores secundarios

Ninguno

4. Contexto de Negocio (extraído de context.md)

Un Product es un artículo vendible.

Todo Product:

Tiene precio

Tiene stock

Puede o no tener categorías

Solo productos activos pueden ser seleccionados para un Cart.

Un Product eliminado lógicamente:

No es visible

No puede ser seleccionado

5. Reglas de Negocio

Las siguientes reglas son obligatorias y deben implementarse en el dominio:

El precio del producto debe ser mayor que cero.

El stock inicial no puede ser negativo.

El producto se crea con estado inicial ACTIVE.

El identificador del producto es único (generado por el sistema).

Un producto puede existir sin categorías asociadas.

Las categorías asociadas deben existir (validación por ID).

La creación del producto no publica eventos de negocio.

6. Flujo Principal

El Administrator envía una solicitud para crear un producto.

El sistema valida los datos de entrada.

El sistema crea el agregado Product aplicando las reglas de dominio.

El producto se persiste en la base de datos del product-service.

El sistema retorna la representación del producto creado.

7. Flujo Alternativo / Errores
   7.1 Precio inválido

Condición: precio ≤ 0

Resultado: error de negocio

7.2 Stock inválido

Condición: stock < 0

Resultado: error de negocio

7.3 Categoría inexistente

Condición: una o más categorías no existen

Resultado: error de negocio

8. Alcance Técnico (Qué SÍ hace)

Implementa un Use Case explícito para creación de producto.

Aplica reglas de negocio en el dominio.

Usa arquitectura hexagonal.

Persiste el producto en la base de datos del microservicio.

Expone un endpoint REST para la operación.

9. Fuera de Alcance (Qué NO hace)

No gestiona imágenes de productos.

No publica eventos.

No notifica a otros servicios.

No gestiona descuentos.

No gestiona impuestos.

No gestiona auditoría avanzada.

10. Casos de Uso Involucrados
    Use Case principal

CreateProductUseCase

11. Capas Afectadas (según architecture.md)
    Domain

Product (agregado)

ProductStatus (enum)

Excepciones de dominio (ej. InvalidProductPriceException)

Application

CreateProductUseCase (Port In)

CreateProductService (implementación)

ProductPersistencePort (Port Out)

Infrastructure

Controller REST

DTOs de request / response (por agregado Product)

Adaptador de persistencia (JPA)

Mapper dominio ↔ entidad

12. Contrato de API (Nivel Conceptual)
    Endpoint
    POST /products

Request (conceptual)

name

description

price

stock

categoryIds (opcional)

Response (conceptual)

productId

name

price

stock

status

categories

⚠️ Nota:
La definición exacta de DTOs pertenece a la capa Infrastructure y debe respetar la organización:

/dto/product/request
/dto/product/response

13. Consideraciones de Arquitectura

El controller NO contiene lógica de negocio.

El Use Case coordina la creación del producto.

El dominio valida invariantes.

La persistencia se realiza mediante Port Out.

No se permite acceso directo a repositorios desde Application.

14. Criterios de Aceptación

Se puede crear un producto con datos válidos.

No se permite crear un producto con precio ≤ 0.

No se permite crear un producto con stock negativo.

El producto se crea con estado ACTIVE.

El producto persiste correctamente en la base de datos.

La estructura del código cumple arquitectura hexagonal.

15. Relación con Documentos Base

context.md → reglas de dominio del Product

architecture.md → estructura técnica obligatoria

AI_WORKFLOW.md → proceso de implementación con IA

16. Instrucción Explícita para la IA

La IA debe:

Generar primero un Plan de Implementación

Respetar estrictamente la arquitectura hexagonal

No introducir lógica de negocio fuera del dominio

No publicar eventos

No asumir dependencias no declaradas