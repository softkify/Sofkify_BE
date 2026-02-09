# Product Service Microservice

## 📋 Descripción

Microservicio responsable de gestionar el catálogo de productos y el inventario en la plataforma Sofkify. Implementa arquitectura hexagonal con comunicación asíncrona vía RabbitMQ para procesar decrementos de stock cuando se crean órdenes.

## 🏗️ Arquitectura

### **Capa de Dominio (Domain)**
- **Models**: `Product`, `ProductStatus`
- **Ports**: Use cases y repository interfaces
- **Exceptions**: Validaciones de negocio

### **Capa de Aplicación (Application)**
- **Services**: Implementación de use cases
- **DTOs**: `CreateProductRequest`, `ProductResponse`, `OrderCreatedEventDTO`

### **Capa de Infraestructura (Infrastructure)**
- **REST Controllers**: Endpoints HTTP
- **Persistence**: JPA entities y repositories
- **Messaging**: RabbitMQ consumer
- **Web**: Controllers y mappers

## 🛠️ Tecnologías

- **Java 17** - Lenguaje principal
- **Spring Boot 4.0.2** - Framework
- **PostgreSQL** - Base de datos relacional
- **RabbitMQ** - Message broker
- **Jackson** - Serialización JSON
- **JUnit 5** - Testing

## 🚀 Endpoints API

### **Crear Producto**
```http
POST /api/products
Content-Type: application/json

{
  "name": "Laptop Gaming Pro",
  "description": "Laptop de alto rendimiento para gaming",
  "price": 1299.99,
  "stock": 50
}

Response:
201 Created
{
  "id": "uuid",
  "name": "Laptop Gaming Pro",
  "description": "Laptop de alto rendimiento para gaming",
  "price": 1299.99,
  "stock": 50,
  "status": "ACTIVE",
  "createdAt": "2026-02-08T17:53:58"
}
```

### **Listar Productos**
```http
GET /api/products
GET /api/products?status=ACTIVE
GET /api/products?status=INACTIVE
GET /api/products?status=DELETED

Response:
200 OK
[
  {
    "id": "uuid",
    "name": "Laptop Gaming Pro",
    "description": "Laptop de alto rendimiento para gaming",
    "price": 1299.99,
    "stock": 50,
    "status": "ACTIVE",
    "createdAt": "2026-02-08T17:53:58"
  }
]
```

### **Consultar Producto por ID**
```http
GET /api/products/{productId}

Response:
200 OK
{
  "id": "uuid",
  "name": "Laptop Gaming Pro",
  "description": "Laptop de alto rendimiento para gaming",
  "price": 1299.99,
  "stock": 50,
  "status": "ACTIVE",
  "createdAt": "2026-02-08T17:53:58"
}
```

## 🔄 Eventos RabbitMQ

### **Consumido por Product Service**
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

### **Tabla Principal**
```sql
-- Productos
CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name ON products(name);
```

## ⚙️ Configuración

### **Variables de Entorno**
```yaml
server:
  port: 8081
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sofkify_products_bd
    username: postgres
    password: root
  
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

rabbitmq:
  exchanges:
    order: order.exchange
  queues:
    stock-decrement: product.stock.decrement.queue
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
cd product-service

# Construir proyecto
./gradlew build

# Ejecutar servicio
./gradlew bootRun
```

### **Docker**
```bash
# Construir imagen
docker build -t product-service .

# Ejecutar contenedor
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sofkify_products_bd \
  -e SPRING_RABBITMQ_HOST=host.docker.internal \
  product-service

# Ejecutar en modo detached
docker run -d -p 8081:8081 \
  --name product-service \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sofkify_products_bd \
  -e SPRING_RABBITMQ_HOST=host.docker.internal \
  product-service
```

### **Docker Compose (Recomendado)**
```bash
# Crear docker-compose.yml con todos los servicios
version: '3.8'
services:
  product-service:
    build: ./product-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_products_bd
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
# Crear producto
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Gaming Pro",
    "description": "Laptop de alto rendimiento para gaming",
    "price": 1299.99,
    "stock": 50
  }'

# Listar productos
curl http://localhost:8081/api/products

# Listar productos activos
curl http://localhost:8081/api/products?status=ACTIVE

# Consultar producto
curl http://localhost:8081/api/products/{product-id}
```

### **Unit Tests**
```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con cobertura
./gradlew test jacocoTestReport
```

## 🔗 Integración con otros Servicios

### **Order Service**
- **Propósito**: Recibir eventos de creación de órdenes
- **Evento**: `OrderCreatedEvent`
- **Exchange**: `order.exchange`
- **Acción**: Decrementar stock automáticamente

## 📊 Estados de Producto

| Estado | Descripción | Uso |
|--------|-------------|------|
| `ACTIVE` | Producto disponible para venta | Listado por defecto |

## 🛡️ Manejo de Errores

### **Códigos de Error**
- `400` - Datos inválidos (precio negativo, stock negativo)
- `404` - Producto no encontrado
- `500` - Error interno del servidor

### **Excepciones de Dominio**
- `InvalidProductPriceException` - Precio inválido
- `InvalidProductStockException` - Stock inválido
- `ProductNotFoundException` - Producto no encontrado
- `InsufficientStockException` - Stock insuficiente para decremento

## 📈 Monitoreo

### **Logs**
```yaml
logging:
  level:
    com.sofkify.productservice: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.springframework.amqp: DEBUG
```

### **Métricas**
- Health checks disponibles en `/actuator/health`
- Métricas en `/actuator/metrics`

## 🔄 Flujo de Decremento de Stock

1. **Order Service** crea orden → Publica `OrderCreatedEvent`
2. **Product Service** consume evento → Valida stock disponible
3. **Product Service** decrementa stock → Actualiza base de datos
4. **Product Service** loguea éxito → Auditoría completa

