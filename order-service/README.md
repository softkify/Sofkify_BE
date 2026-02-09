# Order Service Microservice

## 📋 Descripción

Microservicio responsable de gestionar el ciclo de vida de órdenes en la plataforma Sofkify. Implementa arquitectura hexagonal con comunicación asíncrona vía RabbitMQ para garantizar desacoplamiento y escalabilidad.

## 🏗️ Arquitectura

### **Capa de Dominio (Domain)**
- **Models**: `Order`, `OrderItem`, `OrderStatus`
- **Ports**: Use cases y repository interfaces
- **Events**: `OrderCreatedEvent` para comunicación asíncrona

### **Capa de Aplicación (Application)**
- **Services**: Implementación de use cases
- **DTOs**: `OrderResponse`, `OrderItemResponse`, `UpdateOrderStatusRequest`

### **Capa de Infraestructura (Infrastructure)**
- **REST Controllers**: Endpoints HTTP
- **Persistence**: JPA entities y repositories
- **Messaging**: RabbitMQ publisher/consumer
- **HTTP Client**: Integración con cart-service

## 🛠️ Tecnologías

- **Java 17** - Lenguaje principal
- **Spring Boot 4.0.2** - Framework
- **PostgreSQL** - Base de datos relacional
- **RabbitMQ** - Message broker
- **Jackson** - Serialización JSON
- **JUnit 5** - Testing

## 🚀 Endpoints API

### **Crear Orden desde Carrito**
```http
POST /orders/from-cart/{cartId}
Content-Type: application/json

Response:
201 Created
{
  "id": "uuid",
  "cartId": "uuid",
  "customerId": "uuid",
  "status": "PENDING_PAYMENT",
  "items": [...],
  "totalAmount": 1299.99,
  "createdAt": "2026-02-08T17:53:58"
}
```

### **Consultar Orden por ID**
```http
GET /orders/{orderId}

Response:
200 OK
{
  "id": "uuid",
  "cartId": "uuid",
  "customerId": "uuid",
  "status": "PENDING_PAYMENT",
  "items": [...],
  "totalAmount": 1299.99,
  "createdAt": "2026-02-08T17:53:58"
}
```

### **Listar Órdenes por Cliente**
```http
GET /orders/customer/{customerId}

Response:
200 OK
[
  {
    "id": "uuid",
    "cartId": "uuid",
    "customerId": "uuid",
    "status": "PENDING_PAYMENT",
    "items": [...],
    "totalAmount": 1299.99,
    "createdAt": "2026-02-08T17:53:58"
  }
]
```

### **Actualizar Estado de Orden**
```http
PUT /orders/{orderId}/status
Content-Type: application/json

{
  "status": "PAID"
}

Response:
200 OK
{
  "id": "uuid",
  "cartId": "uuid",
  "customerId": "uuid",
  "status": "PAID",
  "items": [...],
  "totalAmount": 1299.99,
  "createdAt": "2026-02-08T17:53:58"
}
```

## 🔄 Eventos RabbitMQ

### **Publicado por Order Service**
```json
OrderCreatedEvent {
  "orderId": "uuid",
  "customerId": "uuid",
  "cartId": "uuid",
  "items": [
    {
      "productId": "uuid",
      "productName": "Laptop Gaming Pro",
      "quantity": 1,
      "unitPrice": 1299.99,
      "totalPrice": 1299.99
    }
  ],
  "totalAmount": 1299.99,
  "createdAt": "2026-02-08T17:53:58"
}
```

**Exchange**: `order.exchange`  
**Routing Key**: `order.created`  
**Queue**: `product.stock.decrement.queue`

## 🗄️ Base de Datos

### **Tablas Principales**
```sql
-- Órdenes
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    cart_id UUID UNIQUE NOT NULL,
    customer_id UUID NOT NULL,
    status VARCHAR(20) CHECK (status IN (
        'PENDING_PAYMENT', 'PAID', 'CONFIRMED', 
        'SHIPPED', 'DELIVERED', 'CANCELLED', 'FAILED'
    )),
    total DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Items de orden
CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## ⚙️ Configuración

### **Variables de Entorno**
```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sofkify_orders_bd
    username: postgres
    password: root
  
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

cart:
  service:
    url: http://localhost:8083/api

rabbitmq:
  exchanges:
    order: order.exchange
  queues:
    stock-decrement: product.stock.decrement.queue
    order-status: order.status.queue
  routing-keys:
    order-created: order.created
    stock-decremented: stock.decremented
    order-failed: order.failed
```

## 🚀 Instalación

### **Prerrequisitos**
- Java 17+
- PostgreSQL 13+
- RabbitMQ 3.8+
- Gradle 7+

### **Ejecución**
```bash
# Clonar repositorio
git clone <repository-url>
cd order-service

# Construir proyecto
./gradlew build

# Ejecutar servicio
./gradlew bootRun
```

### **Docker**
```bash
# Construir imagen
docker build -t order-service .

# Ejecutar contenedor
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sofkify_orders_bd \
  -e SPRING_RABBITMQ_HOST=host.docker.internal \
  order-service

# Ejecutar en modo detached
docker run -d -p 8082:8082 \
  --name order-service \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sofkify_orders_bd \
  -e SPRING_RABBITMQ_HOST=host.docker.internal \
  order-service
```

### **Docker Compose (Recomendado)**
```bash
# Crear docker-compose.yml con todos los servicios
version: '3.8'
services:
  order-service:
    build: ./order-service
    ports:
      - "8082:8082"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_orders_bd
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
      - SPRING_RABBITMQ_HOST=rabbitmq
    depends_on:
      - postgres
      - rabbitmq
```

## 🧪 Testing

### **Endpoints de Prueba**
```bash
# Crear orden
curl -X POST http://localhost:8082/orders/from-cart/ce5f901c-e33c-4b5b-84f8-eafc578f73ed

# Consultar orden
curl http://localhost:8082/orders/{order-id}

# Listar órdenes por cliente
curl http://localhost:8082/orders/customer/f95b0bc9-c77c-4618-a2af-130dba3e0409

# Actualizar estado
curl -X PUT http://localhost:8082/orders/{order-id}/status \
  -H "Content-Type: application/json" \
  -d '{"status": "PAID"}'
```

### **Unit Tests**
```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con cobertura
./gradlew test jacocoTestReport
```

## 🔗 Integración con otros Servicios

### **Cart Service**
- **Propósito**: Obtener detalles del carrito para crear órdenes
- **Endpoint**: `GET /api/carts/{cartId}`
- **URL**: `http://localhost:8083/api`

### **Product Service**
- **Propósito**: Notificar decremento de stock
- **Evento**: `OrderCreatedEvent`
- **Exchange**: `order.exchange`

## 📊 Estados de Orden

| Estado | Descripción | Transición desde |
|--------|-------------|------------------|
| `PENDING_PAYMENT` | Orden creada, esperando pago | - ||

## 🛡️ Manejo de Errores

### **Códigos de Error**
- `400` - Carrito inválido o vacío
- `404` - Carrito u orden no encontrada
- `409` - Ya existe orden para el carrito
- `500` - Error interno del servidor

### **Excepciones de Dominio**
- `CartNotFoundException` - Carrito no encontrado
- `InvalidCartException` - Carrito inválido
- `OrderAlreadyExistsException` - Orden duplicada
- `OrderNotFoundException` - Orden no encontrada
