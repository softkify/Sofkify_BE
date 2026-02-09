# Sofkify Backend E-commerce

## 📋 Descripción del Proyecto

**Sofkify_BE** es una plataforma de e-commerce implementada con microservicios siguiendo patrones de arquitectura hexagonal. El proyecto demuestra mejores prácticas de desarrollo de software con Spring Boot, comunicación asíncrona con RabbitMQ, y contenerización con Docker.

## 🎯 MVP (Producto Mínimo Viable)

### **Funcionalidades Principales:**
- ✅ **Gestión de Usuarios** - Registro, login, perfiles
- ✅ **Catálogo de Productos** - CRUD completo con gestión de stock
- ✅ **Carritos de Compra** - Agregar/actualizar/eliminar items
- ✅ **Gestión de Órdenes** - Creación desde carrito con estados
- ✅ **Comunicación Asíncrona** - Decremento automático de stock
- ✅ **Arquitectura Escalable** - Microservicios desacoplados

### **Flujo de Usuario Completo:**
1. **Registro/Login** → User Service valida credenciales
2. **Navegación Productos** → Product Service muestra catálogo
3. **Agrega al Carrito** → Cart Service gestiona items
4. **Confirma Compra** → Order Service crea orden
5. **Procesamiento Automático** → Product Service decrementa stock
6. **Seguimiento** → Order Service actualiza estados

## 🏗️ Arquitectura General

### **Microservicios Implementados:**

#### **🔐 User Service** (Puerto 8080)
- **Propósito**: Gestión de identidad y autenticación
- **Endpoints**: 7 endpoints (CRUD + login + promoción)
- **Base de Datos**: `sofkify_users`
- **Tecnologías**: Java 21, Spring Boot, PostgreSQL, Lombok

#### **🛒 Cart Service** (Puerto 8083)
- **Propósito**: Gestión de carritos de compra
- **Endpoints**: 5 endpoints (CRUD completo)
- **Base de Datos**: `sofkify_cars_bd`
- **Tecnologías**: Java 17, Spring Boot, PostgreSQL, Flyway

#### **📦 Product Service** (Puerto 8081)
- **Propósito**: Catálogo de productos y gestión de inventario
- **Endpoints**: 3 endpoints (CRUD básico)
- **Base de Datos**: `sofkify_products_bd`
- **Tecnologías**: Java 17, Spring Boot, PostgreSQL, RabbitMQ

#### **📋 Order Service** (Puerto 8082)
- **Propósito**: Gestión del ciclo de vida de órdenes
- **Endpoints**: 4 endpoints (CRUD + creación desde carrito)
- **Base de Datos**: `sofkify_orders_bd`
- **Tecnologías**: Java 17, Spring Boot, PostgreSQL, RabbitMQ

### **🔄 Comunicación entre Servicios:**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   User Service   │    │  Cart Service   │    │ Product Service  │
│   (8080)       │    │   (8083)       │    │   (8081)       │
│                 │    │                 │    │                 │
│  Autenticación   │    │  Gestión       │    │  Catálogo       │
│  Perfiles       │◄──►│  Carritos       │◄──►│  Inventario      │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         │                        │                        │
         │                        ▼                        ▼
         │              ┌─────────────────┐    ┌─────────────────┐
         │              │  Order Service  │    │   RabbitMQ     │
         │              │   (8082)       │    │  Message Broker │
         │              │                 │    │                 │
         └──────────────►│  Creación       │◄──►│  Eventos       │
                        │  Órdenes       │    │  Asíncronos     │
                        │                 │    │                 │
                        └─────────────────┘    └─────────────────┘
```

## 🛠️ Stack Tecnológico

### **Backend:**
- **Java**: 17-21 (dependiendo del servicio)
- **Spring Boot**: 4.0.2 (framework principal)
- **PostgreSQL**: Base de datos relacional
- **RabbitMQ**: Message broker para comunicación asíncrona
- **Gradle**: Gestión de dependencias y build
- **JUnit 5**: Framework de testing
- **Lombok**: Reducción de código boilerplate
- **Jackson**: Serialización/deserialización JSON
- **Flyway**: Migraciones de base de datos

### **Infraestructura:**
- **Docker**: Contenerización de todos los servicios
- **Docker Compose**: Orquestación completa
- **GitHub**: Control de versiones y CI/CD

## 📊 Alcance del MVP

### **Características Implementadas:**
- ✅ **Autenticación y Autorización** básica
- ✅ **Gestión de Catálogo de Productos** completa
- ✅ **Carritos de Compra** funcionales
- ✅ **Procesamiento de Órdenes** end-to-end
- ✅ **Comunicación Asíncrona** entre servicios
- ✅ **Validaciones de Negocio** robustas
- ✅ **Manejo de Errores** consistente
- ✅ **Logging** y monitoreo básico

### **Limitaciones Actuales:**
- ⚠️ **Autenticación sin JWT** (solo login básico)
- ⚠️ **Sin integración con pasarelas de pago**
- ⚠️ **Sin notificaciones por email/SMS**
- ⚠️ **Sin panel de administración**
- ⚠️ **Sin analytics o reportes**
- ⚠️ **Carritos no se limpian automáticamente**

## 🚀 Próximas Implementaciones

1. **🔐 Mejoras de Seguridad**
   - Implementar JWT para autenticación stateless
   - Integrar Spring Security
   - Agregar refresh tokens
   - Implementar 2FA opcional

2. **🛒 Limpieza Automática de Carritos**
   - Event-driven cleanup con RabbitMQ
   - Carritos abandonados por tiempo
   - Política de retención

3. **📋 Mejoras en Órdenes**
   - Integración con pasarelas de pago (Stripe, PayPal)
   - Notificaciones de estado por email
   - Cancelación automática por tiempo

### **Mediano Plazo**
4. **📦 Gestión Avanzada de Inventario**
   - Categorías de productos
   - Búsqueda y filtrado avanzado
   - Gestión de proveedores
   - Alertas de stock bajo

5. **📊 Analytics y Reportes**
   - Dashboard de ventas
   - Reportes de productos más vendidos
   - Métricas de usuario
   - Exportación de datos

6. **🔔 Sistema de Notificaciones**
   - Email transaccional
   - Notificaciones push (WebSocket)
   - Preferencias de usuario
   - Historial de notificaciones

### **Largo Plazo:**
7. **🛒 Carritos Avanzados**
   - Listas de deseos (wishlists)
   - Carritos compartidos
   - Descuentos y promociones
   - Recomendaciones de productos

8. **🌐 Expansión Multi-tenant**
   - Soporte para múltiples tiendas
   - Configuración por tenant
   - Aislamiento de datos
   - White-labeling

## 🐳 Dockerización Completa

### **Todos los servicios son Dockerizables:**
```bash
# Construir todos los servicios
./order-service/gradlew build
./product-service/gradlew build
./cart-service/gradlew build
./user-service/gradlew build

# Construir imágenes Docker
docker build -t order-service ./order-service
docker build -t product-service ./product-service
docker build -t cart-service ./cart-service
docker build -t user-service ./user-service
```

### **Docker Compose para Desarrollo:**
```yaml
version: '3.8'
services:
  # Base de datos
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: sofkify_ecommerce
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  # Message broker
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

  # Microservicios
  user-service:
    build: ./user-service
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_users
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
    depends_on:
      - postgres

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

  order-service:
    build: ./order-service
    ports:
      - "8082:8082"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/sofkify_orders_bd
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=root
      - SPRING_RABBITMQ_HOST=rabbitmq
      - CART_SERVICE_URL=http://cart-service:8083/api
    depends_on:
      - postgres
      - rabbitmq
      - cart-service

volumes:
  postgres_data:
  rabbitmq_data:
```

## 🚀 Instalación y Ejecución

### **Prerrequisitos:**
- Java 17+ (para user-service se necesita Java 21)
- Docker y Docker Compose
- PostgreSQL 13+ (si se ejecuta sin Docker)
- RabbitMQ 3.8+ (si se ejecuta sin Docker)

### **Ejecución con Docker Compose (Recomendado):**
```bash
# Clonar repositorio
git clone <repository-url>
cd Sofkify_BE

# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### **Ejecución Local:**
```bash
# Cada servicio en su propia terminal
cd user-service && ./gradlew bootRun &
cd product-service && ./gradlew bootRun &
cd cart-service && ./gradlew bootRun &
cd order-service && ./gradlew bootRun &
```

## 📊 Arquitectura y Patrones

### **Patrones Implementados:**
- ✅ **Arquitectura Hexagonal** - Desacoplamiento de negocio
- ✅ **Domain-Driven Design** - Lógica de negocio centralizada
- ✅ **CQRS** (parcial) - Separación de lectura/escritura
- ✅ **Event-Driven Architecture** - Comunicación asíncrona
- ✅ **Repository Pattern** - Abstracción de persistencia
- ✅ **Dependency Injection** - Inversión de control
- ✅ **DTO Pattern** - Transferencia de datos limpia

### **Principios SOLID:**
- ✅ **S** - Responsabilidad única
- ✅ **O** - Abierto a extensión
- ✅ **L** - Sustitución de Liskov
- ✅ **I** - Segregación de interfaces
- ✅ **D** - Inversión de dependencias

## 🧪 Testing

### **Estrategia de Testing:**
- **Unit Tests**: Pruebas de lógica de negocio
- **Integration Tests**: Pruebas de integración entre capas
- **API Tests**: Pruebas de endpoints REST
- **Contract Tests**: Pruebas de contratos entre servicios

### **Comandos de Testing:**
```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con cobertura
./gradlew test jacocoTestReport

# Ejecutar tests específicos
./order-service/gradlew test
./product-service/gradlew test
./cart-service/gradlew test
./user-service/gradlew test
```

## 🔗 Documentación de APIs

### **Documentación por Servicio:**
- **[User Service](./user-service/README.md)** - Gestión de usuarios y autenticación
- **[Product Service](./product-service/README.md)** - Catálogo e inventario
- **[Cart Service](./cart-service/README.md)** - Carritos de compra
- **[Order Service](./order-service/README.md)** - Gestión de órdenes

### **API Gateway (Futuro):**
- **Endpoint Unificado**: `http://localhost:8080/api-gateway`
- **Documentación Swagger**: `/swagger-ui.html`
- **Rate Limiting** por cliente
- **Circuit Breaker** para resiliencia

## 🎉 Estado Actual del Proyecto

**Sofkify_BE es un MVP funcional con arquitectura enterprise-ready:**

- ✅ **4 microservicios** completamente funcionales
- ✅ **Comunicación asíncrona** implementada
- ✅ **Dockerización** completa
- ✅ **Documentación exhaustiva**
- ✅ **Testing integrado**
- ✅ **Base para escalar** y evolucionar

**¡Listo para producción y próximos desarrollos!** 🚀