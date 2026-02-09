# User Service Microservice

## 📋 Descripción

Microservicio responsable de gestionar la identidad y autenticación de usuarios en la plataforma Sofkify. Implementa arquitectura hexagonal y provee servicios de gestión de usuarios con roles y autenticación básica.

## 🏗️ Arquitectura

### **Capa de Dominio (Domain)**
- **Models**: `User`, `UserRole`, `UserStatus`
- **Ports**: Use cases y repository interfaces
- **Exceptions**: Validaciones de negocio

### **Capa de Aplicación (Application)**
- **Services**: Implementación de use cases
- **DTOs**: `CreateUserRequest`, `UpdateUserRequest`, `UserResponse`, `LoginRequest`, `LoginResponse`

### **Capa de Infraestructura (Infrastructure)**
- **REST Controllers**: Endpoints HTTP
- **Persistence**: JPA entities y repositories
- **Security**: Autenticación básica (sin Spring Security por ahora)

## 🛠️ Tecnologías

- **Java 21** - Lenguaje principal
- **Spring Boot 4.0.2** - Framework
- **PostgreSQL** - Base de datos relacional
- **Lombok** - Reducción de código boilerplate
- **Jackson** - Serialización JSON
- **JUnit 5** - Testing

## 🚀 Endpoints API

### **Crear Usuario**
```http
POST /api/users
Content-Type: application/json

{
  "email": "usuario@ejemplo.com",
  "password": "password123",
  "name": "Usuario Ejemplo"
}

Response:
201 Created
{
  "id": "uuid",
  "email": "usuario@ejemplo.com",
  "name": "Usuario Ejemplo",
  "role": "USER",
  "status": "ACTIVE",
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Consultar Usuario por ID**
```http
GET /api/users/{userId}

Response:
200 OK
{
  "id": "uuid",
  "email": "usuario@ejemplo.com",
  "name": "Usuario Ejemplo",
  "role": "USER",
  "status": "ACTIVE",
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Consultar Usuario por Email**
```http
GET /api/users/email/{email}

Response:
200 OK
{
  "id": "uuid",
  "email": "usuario@ejemplo.com",
  "name": "Usuario Ejemplo",
  "role": "USER",
  "status": "ACTIVE",
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Actualizar Usuario**
```http
PUT /api/users/{userId}
Content-Type: application/json

{
  "name": "Nombre Actualizado",
  "email": "nuevo@ejemplo.com"
}

Response:
200 OK
{
  "id": "uuid",
  "email": "nuevo@ejemplo.com",
  "name": "Nombre Actualizado",
  "role": "USER",
  "status": "ACTIVE",
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Promover a Administrador**
```http
POST /api/users/{userId}/promote

Response:
200 OK
{
  "id": "uuid",
  "email": "usuario@ejemplo.com",
  "name": "Usuario Ejemplo",
  "role": "ADMIN",
  "status": "ACTIVE",
  "createdAt": "2026-02-08T17:53:58",
  "updatedAt": "2026-02-08T17:53:58"
}
```

### **Desactivar Usuario**
```http
DELETE /api/users/{userId}

Response:
204 No Content
```

### **Login de Usuario**
```http
POST /api/users/auth/login
Content-Type: application/json

{
  "email": "usuario@ejemplo.com",
  "password": "password123"
}

Response:
200 OK
{
  "success": true,
  "message": "Login exitoso",
  "userId": "uuid",
  "email": "usuario@ejemplo.com",
  "name": "Usuario Ejemplo",
  "role": "USER"
}

Response (Error):
401 Unauthorized
{
  "success": false,
  "message": "Credenciales inválidas"
}
```

## 🗄️ Base de Datos

### **Tabla Principal**
```sql
-- Usuarios
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('USER', 'ADMIN')),
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_role ON users(role);
```

## ⚙️ Configuración

### **Variables de Entorno**
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sofkify_users
    username: postgres
    password: root
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
  
  jpa:
    hibernate:
      ddl-auto: update
      show-sql: true
      properties:
        hibernate:
          dialect: org.hibernate.dialect.PostgreSQLDialect
          format_sql: true
```

## 🚀 Instalación

### **Prerrequisitos**
- Java 21+
- PostgreSQL 13+
- Gradle 7+

### **Ejecución**
```bash
# Clonar repositorio
git clone <repository-url>
cd user-service

# Construir proyecto
./gradlew build

# Ejecutar servicio
./gradlew bootRun
```

### **Docker**
```bash
# Construir imagen
docker build -t user-service .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sofkify_users \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=root \
  user-service

# Ejecutar en modo detached
docker run -d -p 8080:8080 \
  --name user-service \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sofkify_users \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=root \
  user-service
```

### **Docker Compose (Recomendado)**
```bash
# Crear docker-compose.yml con todos los servicios
version: '3.8'
services:
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
```

## 🧪 Testing

### **Endpoints de Prueba**
```bash
# Crear usuario
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "password": "password123",
    "name": "Usuario Ejemplo"
  }'

# Consultar usuario
curl http://localhost:8080/api/users/user-uuid

# Consultar por email
curl http://localhost:8080/api/users/email/usuario@ejemplo.com

# Actualizar usuario
curl -X PUT http://localhost:8080/api/users/user-uuid \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nombre Actualizado",
    "email": "nuevo@ejemplo.com"
  }'

# Login
curl -X POST http://localhost:8080/api/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "password": "password123"
  }'
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
- **Propósito**: Validar existencia de clientes
- **Endpoint**: `GET /api/users/{customerId}`
- **URL**: `http://localhost:8083/api`

### **Order Service**
- **Propósito**: Validar clientes al crear órdenes
- **Endpoint**: `GET /api/users/{customerId}`
- **URL**: `http://localhost:8082`

## 📊 Estados y Roles de Usuario

### **Roles**
| Rol       | Descripción | Permisos |
|-----------|-------------|-----------|
| `CLIENTE` | Cliente regular | Comprar, ver carritos, crear órdenes |

### **Estados**
| Estado | Descripción | Uso |
|--------|-------------|------|
| `ACTIVE` | Usuario activo | Login permitido |


## 🛡️ Manejo de Errores

### **Códigos de Error**
- `400` - Datos inválidos (email duplicado, datos faltantes)
- `401` - Credenciales inválidas
- `404` - Usuario no encontrado
- `500` - Error interno del servidor

### **Excepciones de Dominio**
- `UserNotFoundException` - Usuario no encontrado
- `EmailAlreadyExistsException` - Email ya registrado
- `InvalidCredentialsException` - Credenciales inválidas

## 🔐 Seguridad

### **Autenticación Actual**
- **Login básico** con email y password
- **Contraseñas hasheadas** (recomendado usar BCrypt)
- **Tokens JWT** (futuro implementación)
- **Spring Security** (futura integración)

### **Mejoras Futuras**
- Implementar JWT para autenticación stateless
- Integrar con OAuth2 (Google, Facebook)
- Agregar 2FA (Two-Factor Authentication)
- Implementar refresh tokens

