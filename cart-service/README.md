# Cart Service Microservice

## 📋 Descripción

Microservicio responsable de gestionar los carritos de compra en la plataforma Sofkify. Implementa arquitectura hexagonal y sirve como componente central para el flujo de comercio electrónico, integrándose con product-service y order-service.

## 🏗️ Arquitectura

### **Capa de Dominio (Domain)**
- **Models**: `Cart`, `CartItem`, `CartStatus`
- **Ports**: Use cases y repository interfaces
- **Exceptions**: Validaciones de negocio

### **Capa de Aplicación (Application)**
- **Services**: Implementación de use cases
- **DTOs**: `AddItemRequest`, `UpdateQuantityRequest`, `CartResponse`, `CartItemResponse`

### **Capa de Infraestructura (Infrastructure)**
- **REST Controllers**: Endpoints HTTP
- **Persistence**: JPA entities y repositories
- **HTTP Client**: Integración con product-service y user-service
- **Migration**: Flyway para gestión de esquema

## 🛠️ Tecnologías

- **Java 17** - Lenguaje principal
- **Spring Boot 4.0.2** - Framework
- **PostgreSQL** - Base de datos relacional
- **Flyway** - Migraciones de base de datos
- **Jackson** - Serialización JSON
- **JUnit 5** - Testing

## 🚀 Endpoints API

### **Agregar Item al Carrito**
```http
POST /api/carts/items
Headers:
X-Customer-Id: {customerId}
Content-Type: application/json

{
  "productId": "uuid",
  "quantity": 2
}

Response:
201 Created
{
  "id": "cart-uuid",
  "customerId": "customer-uuid",
  "status": "ACTIVE",
  "items": [
    {
      "id": "item-uuid",
      "productId": "product-uuid",
      "productName": "Laptop Gaming Pro",
      "productPrice": 1299.99,
      "quantity": 2,
      "subtotal": 2599.98,
      "createdAt": "2026-02-08T17:53:58",
      "updatedAt": "2026-02-08T17:53:58"
    }
  ],
  "total": 2599.98,
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Consultar Carrito por Cliente**
```http
GET /api/carts
Headers:
X-Customer-Id: {customerId}

Response:
200 OK
{
  "id": "cart-uuid",
  "customerId": "customer-uuid",
  "status": "ACTIVE",
  "items": [...],
  "total": 2599.98,
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Consultar Carrito por ID**
```http
GET /api/carts/{cartId}

Response:
200 OK
{
  "id": "cart-uuid",
  "customerId": "customer-uuid",
  "status": "ACTIVE",
  "items": [...],
  "total": 2599.98,
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Actualizar Cantidad de Item**
```http
PUT /api/carts/items/{cartItemId}
Headers:
X-Customer-Id: {customerId}
Content-Type: application/json

{
  "quantity": 5
}

Response:
200 OK
{
  "id": "cart-uuid",
  "customerId": "customer-uuid",
  "status": "ACTIVE",
  "items": [
    {
      "id": "item-uuid",
      "productId": "product-uuid",
      "productName": "Laptop Gaming Pro",
      "productPrice": 1299.99,
      "quantity": 5,
      "subtotal": 6499.95,
      "createdAt": "2026-02-08T17:53:58",
      "updatedAt": "2026-02-08T17:53:58"
    }
  ],
  "total": 6499.95,
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Eliminar Item del Carrito**
```http
DELETE /api/carts/items/{cartItemId}
Headers:
X-Customer-Id: {customerId}

Response:
200 OK
{
  "id": "cart-uuid",
  "customerId": "customer-uuid",
  "status": "ACTIVE",
  "items": [...],
  "total": 1299.99,
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

## 🗄️ Base de Datos

### **Tablas Principales**
```sql
-- Carritos
CREATE TABLE carts (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'ABANDONED', 'PROCESSED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Items del carrito
CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID REFERENCES carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    subtotal DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_carts_customer_id ON carts(customer_id);
CREATE INDEX idx_carts_status ON carts(status);
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);
```

## ⚙️ Configuración

### **Variables de Entorno**
```yaml
server:
  port: 8083
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sofkify_cars_bd
    username: postgres
    password: root
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  flyway:
    enabled: true
    baseline-on-migrate: true

# Integración con otros servicios
product:
  service:
    url: http://localhost:8081/api

user:
  service:
    url: http://localhost:8080/api
```

## 🚀 Instalación

### **Prerrequisitos**
- Java 17+
- PostgreSQL 13+
- Gradle 7+

### **Ejecución**
```bash
# Clonar repositorio
git clone <repository-url>
cd cart-service

# Construir proyecto
./gradlew build

# Ejecutar servicio
./gradlew bootRun
```

### **Docker**
```bash
# Construir imagen
docker build -t cart-service .

# Ejecutar contenedor
docker run -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sofkify_cars_bd \
  -e PRODUCT_SERVICE_URL=http://host.docker.internal:8081/api \
  -e USER_SERVICE_URL=http://host.docker.internal:8080/api \
  cart-service

# Ejecutar en modo detached
docker run -d -p 8083:8083 \
  --name cart-service \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sofkify_cars_bd \
  -e PRODUCT_SERVICE_URL=http://host.docker.internal:8081/api \
  -e USER_SERVICE_URL=http://host.docker.internal:8080/api \
  cart-service
```

### **Docker Compose (Recomendado)**
```bash
# Crear docker-compose.yml con todos los servicios
version: '3.8'
services:
  cart-service:
    build: ./cart-service
    ports:
      - "8083:8083"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_cars_bd
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
      - PRODUCT_SERVICE_URL=http://product-service:8081/api
      - USER_SERVICE_URL=http://user-service:8080/api
    depends_on:
      - postgres
      - product-service
      - user-service
```

## 🧪 Testing

### **Endpoints de Prueba**
```bash
# Agregar item al carrito
curl -X POST http://localhost:8083/api/carts/items \
  -H "X-Customer-Id: customer-uuid" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "product-uuid",
    "quantity": 2
  }'

# Consultar carrito
curl http://localhost:8083/api/carts \
  -H "X-Customer-Id: customer-uuid"

# Consultar carrito por ID
curl http://localhost:8083/api/carts/cart-uuid

# Actualizar cantidad
curl -X PUT http://localhost:8083/api/carts/items/item-uuid \
  -H "X-Customer-Id: customer-uuid" \
  -H "Content-Type: application/json" \
  -d '{"quantity": 5}'

# Eliminar item
curl -X DELETE http://localhost:8083/api/carts/items/item-uuid \
  -H "X-Customer-Id: customer-uuid"
```

### **Unit Tests**
```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con cobertura
./gradlew test jacocoTestReport
```

## 🔗 Integración con otros Servicios

### **Product Service**
- **Propósito**: Validar productos y obtener precios
- **Endpoint**: `GET /api/products/{productId}`
- **URL**: `http://localhost:8081/api`

### **User Service**
- **Propósito**: Validar existencia de clientes
- **Endpoint**: `GET /api/users/{customerId}`
- **URL**: `http://localhost:8080/api`

### **Order Service**
- **Propósito**: Proporcionar carritos para creación de órdenes
- **Endpoint**: `GET /api/carts/{cartId}`
- **URL**: `http://localhost:8082`

## 📊 Estados de Carrito

| Estado | Descripción | Uso |
|--------|-------------|------|
| `ACTIVE` | Carrito en uso activo | Estado por defecto |

## 🛡️ Manejo de Errores

### **Códigos de Error**
- `400` - Datos inválidos (cantidad negativa, UUID inválido)
- `404` - Carrito, item o cliente no encontrado
- `500` - Error interno del servidor

### **Excepciones de Dominio**
- `CartNotFoundException` - Carrito no encontrado
- `CartItemNotFoundException` - Item no encontrado
- `InvalidQuantityException` - Cantidad inválida
- `ProductNotFoundException` - Producto no encontrado
- `CustomerNotFoundException` - Cliente no encontrado

## 🔄 Flujo de Compra

1. **Cliente agrega productos** → Cart items creados
2. **Cart Service valida** → Productos existen y están activos
3. **Cliente confirma compra** → Order Service consulta carrito
4. **Order Service crea orden** → Cart Service marca como PROCESSED
5. **Cart Service limpia** → Items eliminados (futuro con RabbitMQ)

