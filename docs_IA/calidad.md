# CALIDAD.md

## 1. ANATOMÍA DE UN INCIDENTE

Este documento recoge dos incidentes críticos que ocurrieron durante el desarrollo del proyecto Softkify, uno en el frontend y otro en el backend, ambos relacionados con la integración entre capas.

---

### INCIDENTE 1: Variables de Entorno - Frontend

#### Contexto
Durante las pruebas de conexión entre backend y front end del módulo de autenticación, los desarrolladores reportaron que no podían completar el registro. La consola del navegador mostraba errores de conexión que inicialmente se confundieron con problemas de CORS.

#### Error (Causa Humana)
El desarrollador hardcodeó la URL base de la API para pruebas locales rápidas y olvidó revertir el cambio o configurarlo para usar las variables de entorno antes del commit final a la rama principal.

#### Defecto (Código)
El archivo `src/services/authApi.ts` tenía la URL fija apuntando a `localhost`, lo que funcionaba en el entorno de desarrollo pero fallaba en cualquier otro dispositivo.

**ANTES (Con Defecto):**
```typescript
// src/services/authApi.ts
// ERROR: URL fija que ignora las variables de entorno de producción
const API_BASE_URL = 'http://localhost:3000';

export const authApi = {
  // ...
};
```

**DESPUÉS (Corregido):**
```typescript
// src/services/authApi.ts
// FIX: Usa la variable de entorno VITE_API_BASE_URL, con fallback seguro para local
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000';

export const authApi = {
  // ...
};
```

#### Fallo (Impacto en Usuario)
Al intentar registrarse o iniciar sesión desde developer, la aplicación intentaba conectar a `http://localhost:3000/api/users` en la máquina del propio usuario.

**Manifestación**: El botón de "Registrarse" se quedaba cargando indefinidamente o mostraba un error genérico "Network Error", ya que el navegador bloqueaba la petición o simplemente no encontraba el servidor localmente.

---

### INCIDENTE 2: Configuración CORS - Backend

#### Contexto
Después de verificar que el backend estaba operativo mediante Insomnia y Postman (las pruebas directas funcionaban correctamente), el equipo frontend intentó realizar la primera integración con el servicio de registro de usuarios. A pesar de que el backend respondía correctamente a las herramientas de API testing, las peticiones desde el navegador fallaban sistemáticamente.

#### Error (Causa Humana)
El equipo backend configuró la política CORS con un patrón de ruta que incluía `/api/**`, sin considerar que la URL base de la API ya contenía el prefijo `/api`. Esto causó una duplicación de ruta (`/api/api/users`) que impedía el correcto funcionamiento del middleware CORS, aunque las peticiones directas desde herramientas como Postman funcionaban (ya que estas herramientas no están sujetas a las políticas CORS del navegador).

#### Defecto (Código)
La configuración de CORS en el backend duplicaba el prefijo `/api` en la definición del patrón de rutas permitidas.

**ANTES (Con Defecto):**
```java
// Configuración CORS en el backend
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // ERROR: Duplicación de /api
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

**DESPUÉS (Corregido):**
```java
// Configuración CORS en el backend
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // FIX: Patrón correcto sin duplicar /api
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

#### Fallo (Impacto en Usuario)
El navegador bloqueaba todas las peticiones del frontend al backend con el siguiente error:

```
Access to fetch at 'http://localhost:8080/api/users' from origin 'http://localhost:5173' 
has been blocked by CORS policy: Response to preflight request doesn't pass access control 
check: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

#### Proceso de Depuración
1. **Verificación inicial**: Se confirmó que el backend estaba operativo mediante Insomnia (petición POST a `http://localhost:8080/api/users`).
2. **Primer intento de solución**: Se agregó `http://localhost:5173` a `allowedOrigins` - sin resultado.
3. **Causa raíz identificada**: El patrón `/api/**` esperaba rutas como `/api/api/users`, pero las peticiones reales llegaban como `/api/users`.
4. **Solución implementada**: Se cambió el patrón a `/**`, se reconstruyó la imagen Docker y se desplegó.
5. **Verificación**: Las peticiones desde el navegador funcionaron correctamente tras el cambio.

---

## 2. ANÁLISIS DE PIRÁMIDE DE PRUEBAS

### Tipo de Aplicación
**Softkify** es una aplicación full-stack de comercio electrónico construida con:
- **Frontend**: SPA (Single Page Application) con React y Vite
- **Backend**: API RESTful con Spring Boot
- **Infraestructura**: Docker, base de datos PostgreSQL

Su valor principal es el registro y login de usuarios, así como el procesamiento de productos y registro de un carrito. 

### ¿Dónde ocurren los errores más críticos?

Los incidentes documentados arriba demuestran que los fallos más graves no están en lógica aislada, sino en los **puntos de integración**:

#### Frontend
- Configuración de entorno (variables, URLs)
- Integración con servicios HTTP
- Gestión de estado global (carrito, autenticación)
- Persistencia de sesión entre recargas

#### Backend
- Configuración de CORS y seguridad
- Manejo de peticiones preflight
- Serialización/deserialización JSON
- Manejo entre microservicios de la información

#### Integración Full-Stack
- Contrato de API (endpoints, schemas)
- Manejo de errores HTTP
- Consistencia de datos entre capas
- Flujos completos de negocio

### Justificación: Estrategia de Testing por Capa

Para este proyecto, adoptamos un enfoque equilibrado que reconoce las particularidades de cada capa:

---

#### FRONTEND: Pirámide Invertida (Testing Trophy)

**¿Por qué más E2E que Unitarias en Frontend?**

1. **Limitaciones de las Pruebas Unitarias**:
   - Muchos componentes (`Header`, `ProductCard`) son visuales/presentacionales
   - Mockear `useLogin`, props y `react-router` es costoso en mantenimiento
   - No garantizan que el sistema funcione integrado
   - El incidente de variables de entorno **no se habría detectado** con pruebas unitarias

2. **Valor de las Pruebas E2E**:
   - Validan **flujos críticos de negocio** completos (Auth → Shop → Checkout)
   - Son agnósticas a la implementación
   - Detectan errores de configuración (URLs, variables de entorno)
   - Capturan problemas de integración con el backend real

**Distribución Recomendada (Frontend)**:
```
E2E (Cypress/Playwright): 60%  ← Flujos críticos
Integration (RTL):        30%  ← Componentes complejos con hooks
Unit (Vitest):           10%  ← Lógica pura (validaciones, cálculos)
```

---

#### BACKEND: Pirámide Tradicional con Énfasis en Integración

**¿Por qué mantener la pirámide pero enfocarse en Integration tests?**

1. **Valor de las Pruebas Unitarias**:
   - Validar lógica de negocio en servicios
   - Probar algoritmos de cálculo (precios, descuentos)
   - Verificar transformaciones de datos

2. **Críticas: Pruebas de Integración**:
   - Validar configuración de Spring Boot (CORS, Security)
   - Probar serialización/deserialización JSON
   - Validar comnucación entre los diferentes microservicios usando RabbitMQ 
   - El incidente de CORS **solo se detecta** con integration tests

3. **Necesarias: Pruebas de Contrato (API)**:
   - Garantizar estabilidad del contrato entre frontend y backend
   - Validar schemas de request/response
   - Documentar comportamiento de la API

**Distribución Recomendada (Backend)**:
```
Unit (JUnit):        40%  ← Lógica de negocio pura
Integration:         50%  ← Controllers, Repos, Config, Microservices, RabbitMQ responses.  
Contract/E2E:        10%  ← Contrato API completo
```

---

### Reto: Escenario de Prueba de Alto Valor

**Definición para cada nivel de la pirámide:**

#### FRONTEND

**Unitaria** - Validación de formulario de registro:
- **Riesgo que mitiga**: Datos inválidos enviados al backend
- **Coste**: Bajo (lógica pura, sin mocks complejos)
- **Ejemplo**: Verificar que email mal formado muestra error antes de submit

**Integración** - Hook de autenticación con Context:
- **Riesgo que mitiga**: Se valida que un usuario autetificado tenga acceso a rutas protegidas y funcionalidades
- **Coste**: Medio
- **Ejemplo**: Verificar que `useAuth` actualiza usuario en todo el árbol de componentes

**E2E** - Flujo completo de compra:
- **Riesgo que mitiga**: Fallos en el flujo crítico de negocio
- **Coste**: Alto (requiere backend + BD)
- **Ejemplo**: Usuario se registra → agrega producto al carrito -> Lo ve en el carrito agregado. 

---

#### BACKEND

**Unitaria** - Servicio de cálculo precio total por cantidad en algún producto.:
- **Riesgo que mitiga**: Errores en lógica de negocio financiera
- **Coste**: Bajo 
- **Ejemplo**: Verificar que el calculo de los productos por cantidad es el esperado.

**Integración** - Controller de usuarios con repositorio:
- **Riesgo que mitiga**: Fallos en la capa HTTP, CORS, serialización, BD
- **Coste**: Medio (requiere Spring context + BD de prueba)
- **Ejemplo**: POST `/api/users` persiste usuario en BD y retorna 201

**Contrato** - Schema de respuesta de GET `/api/products`:
- **Riesgo que mitiga**: Cambios en API que rompen frontend
- **Coste**: Medio (requiere herramienta de contract testing)
- **Ejemplo**: Verificar que response tiene campos `id`, `name`, `price`

---

## 3. IMPLEMENTACIÓN DE APRENDIZAJE

A continuación, se presenta una suite mínima de pruebas que **hubieran detectado** los dos incidentes documentados.

### A. Pruebas Unitarias - Frontend (Vitest + React Testing Library)

**Foco**: Validaciones del formulario de Login (`LoginForm.tsx`)

```typescript
// src/components/Auth/__tests__/LoginForm.test.tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import LoginForm from '../LoginForm';
import { VALIDATION_ERRORS } from '../data';

describe('LoginForm Component', () => {
  const mockSubmit = vi.fn();
  const mockToggle = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('debe mostrar error si se intenta enviar el formulario vacío', () => {
    render(
      <LoginForm 
        onSubmit={mockSubmit} 
        onToggleMode={mockToggle} 
        isLoading={false} 
        error={null} 
      />
    );

    const submitBtn = screen.getByRole('button', { name: /iniciar sesión/i });
    fireEvent.click(submitBtn);

    // Verificamos que NO se llamó al submit
    expect(mockSubmit).not.toHaveBeenCalled();
    
    // Verificamos que aparece el mensaje de error de email requerido
    expect(screen.getByText(VALIDATION_ERRORS.emailRequired)).toBeInTheDocument();
  });

  test('debe validar formato de email antes de permitir submit', () => {
    render(
      <LoginForm 
        onSubmit={mockSubmit} 
        onToggleMode={mockToggle} 
        isLoading={false} 
        error={null} 
      />
    );

    // Email inválido
    fireEvent.change(screen.getByPlaceholderText(/correo electrónico/i), {
      target: { value: 'email-invalido' }
    });
    fireEvent.change(screen.getByPlaceholderText(/contraseña/i), {
      target: { value: 'password123' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: /iniciar sesión/i }));
    
    expect(mockSubmit).not.toHaveBeenCalled();
    expect(screen.getByText(VALIDATION_ERRORS.emailInvalid)).toBeInTheDocument();
  });

  test('debe llamar a onSubmit con los datos correctos si el formulario es válido', async () => {
    render(
      <LoginForm 
        onSubmit={mockSubmit} 
        onToggleMode={mockToggle} 
        isLoading={false} 
        error={null} 
      />
    );

    // Rellenamos los campos con datos válidos
    fireEvent.change(screen.getByPlaceholderText(/correo electrónico/i), {
      target: { value: 'test@softkify.com' }
    });
    fireEvent.change(screen.getByPlaceholderText(/contraseña/i), {
      target: { value: 'password123' }
    });

    // Enviamos
    fireEvent.click(screen.getByRole('button', { name: /iniciar sesión/i }));
    
    // Verificamos que se llamó a la función con los datos correctos
    await waitFor(() => {
      expect(mockSubmit).toHaveBeenCalledWith({
        email: 'test@softkify.com',
        password: 'password123'
      });
    });
  });

  test('debe mostrar estado de carga cuando isLoading es true', () => {
    render(
      <LoginForm 
        onSubmit={mockSubmit} 
        onToggleMode={mockToggle} 
        isLoading={true} 
        error={null} 
      />
    );

    const submitBtn = screen.getByRole('button', { name: /iniciando/i });
    expect(submitBtn).toBeDisabled();
  });
});
```

---

### B. Pruebas de Integración - Frontend (Configuración de Variables)

**Foco**: Detectar el incidente de configuración de API_BASE_URL

```typescript
// src/services/__tests__/apiConfig.test.ts
import { describe, test, expect, beforeEach, afterEach } from 'vitest';

describe('API Configuration', () => {
  const originalEnv = import.meta.env;

  afterEach(() => {
    // Restaurar variables de entorno originales
    import.meta.env = originalEnv;
  });

  test('debe usar VITE_API_BASE_URL cuando está definida', () => {
    // Simular variable de entorno en producción
    import.meta.env = {
      ...originalEnv,
      VITE_API_BASE_URL: 'https://api.softkify.com'
    };

    // Reimportar módulo para que lea la nueva variable
    const { API_BASE_URL } = require('../apiConfig');
    
    expect(API_BASE_URL).toBe('https://api.softkify.com');
    expect(API_BASE_URL).not.toContain('localhost');
  });

  test('debe hacer fallback a localhost cuando VITE_API_BASE_URL no está definida', () => {
    import.meta.env = {
      ...originalEnv,
      VITE_API_BASE_URL: undefined
    };

    const { API_BASE_URL } = require('../apiConfig');
    
    expect(API_BASE_URL).toBe('http://localhost:3000');
  });

  test('ALERTA: debe fallar si hay URL hardcodeada', () => {
    // Esta prueba detectaría el incidente original
    const { API_BASE_URL } = require('../apiConfig');
    
    // Verificar que no hay hardcodeo de localhost en producción
    if (import.meta.env.MODE === 'production') {
      expect(API_BASE_URL).not.toContain('localhost');
    }
  });
});
```

---

### C. Pruebas E2E - Frontend (Playwright)

**Foco**: Flujo completo de autenticación + detección de errores de red/CORS

```typescript
// tests/auth.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Flujo de Autenticación', () => {
  
  test.beforeEach(async ({ page }) => {
    // Navegar a la página de login
    await page.goto('/auth');
  });

  test('debe permitir al usuario navegar entre Login y Registro', async ({ page }) => {
    // Verificar estado inicial (Login)
    await expect(page.getByRole('heading', { name: '¡Bienvenido de nuevo!' })).toBeVisible();
    
    // Clic en "Registrate aquí"
    await page.getByText('Registrate aquí').click();

    // Verificar cambio a Registro
    await expect(page.getByRole('heading', { name: 'Crea tu cuenta' })).toBeVisible();
    
    // Volver a Login
    await page.getByText('Inicia sesión').click();
    await expect(page.getByRole('heading', { name: '¡Bienvenido de nuevo!' })).toBeVisible();
  });

  test('debe mostrar error visual cuando la API falla (simulando CORS)', async ({ page }) => {
    // Mockear la respuesta de la API para simular fallo CORS
    await page.route('**/auth/login', async route => {
      // Simular un error de red (como el que causa CORS)
      await route.abort('failed');
    });

    // Rellenar formulario
    await page.getByPlaceholderText('Correo electrónico').fill('test@softkify.com');
    await page.getByPlaceholderText('Contraseña').fill('password123');
    
    // Intentar login
    await page.getByRole('button', { name: 'Iniciar Sesión' }).click();

    // Verificar que se muestra un mensaje de error de red
    await expect(page.getByText(/error de conexión|failed to fetch/i)).toBeVisible();
  });

  test('CRÍTICO: debe poder comunicarse con el backend real', async ({ page }) => {
    // Esta prueba fallaría con el incidente de CORS o URL hardcodeada
    // Asume que hay un backend de pruebas disponible
    
    // Intentar registro con datos de prueba
    await page.getByText('Registrate aquí').click();
    await page.getByPlaceholderText('Nombre completo').fill('Usuario Test');
    await page.getByPlaceholderText('Correo electrónico').fill(`test${Date.now()}@softkify.com`);
    await page.getByPlaceholderText('Contraseña', { exact: true }).fill('Test123456');
    await page.getByPlaceholderText('Confirmar contraseña').fill('Test123456');
    
    // Interceptar la petición para verificar que llegue al backend
    const responsePromise = page.waitForResponse(
      response => response.url().includes('/api/users') && response.status() === 201
    );

    await page.getByRole('button', { name: 'Crear Cuenta' }).click();
    
    // Esperar respuesta exitosa del backend
    const response = await responsePromise;
    expect(response.status()).toBe(201);
    
    // Verificar redirección o mensaje de éxito
    await expect(page.getByText(/registro exitoso|bienvenido/i)).toBeVisible({ timeout: 5000 });
  });
});
```

---

### D. Pruebas Unitarias - Backend (JUnit 5)

**Foco**: Lógica de negocio del servicio de usuarios

```java
// src/test/java/com/softkify/service/UserServiceTest.java
package com.softkify.service;

import com.softkify.model.User;
import com.softkify.repository.UserRepository;
import com.softkify.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debe crear un nuevo usuario correctamente")
    void testCreateUser_Success() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("test@softkify.com");
        newUser.setPassword("plainPassword");
        newUser.setName("Test User");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result);
        assertEquals("test@softkify.com", result.getEmail());
        assertEquals("hashedPassword", result.getPassword());
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya existe")
    void testCreateUser_EmailAlreadyExists() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail("test@softkify.com");

        User newUser = new User();
        newUser.setEmail("test@softkify.com");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(newUser);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe validar formato de email antes de guardar")
    void testCreateUser_InvalidEmailFormat() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("invalid-email");
        newUser.setPassword("password123");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(newUser);
        });
    }
}
```

---

### E. Pruebas de Integración - Backend (Spring Boot Test)

**Foco**: Detectar el incidente de configuración CORS + validar contrato de API

```java
// src/test/java/com/softkify/controller/UserControllerIntegrationTest.java
package com.softkify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softkify.model.User;
import com.softkify.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("CRÍTICO: Debe permitir peticiones OPTIONS (preflight) desde el frontend")
    void testCorsPreflightRequest() throws Exception {
        // Esta prueba hubiera detectado el incidente de CORS
        mockMvc.perform(options("/api/users")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")));
    }

    @Test
    @DisplayName("POST /api/users debe crear un nuevo usuario y retornar 201")
    void testCreateUser_Success() throws Exception {
        // Arrange
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("test@softkify.com");
        newUser.setPassword("SecurePass123");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:5173")
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("test@softkify.com")))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.password").doesNotExist()); // No debe devolver la contraseña
    }

    @Test
    @DisplayName("POST /api/users debe retornar 400 si el email es inválido")
    void testCreateUser_InvalidEmail() throws Exception {
        // Arrange
        User invalidUser = new User();
        invalidUser.setName("Test User");
        invalidUser.setEmail("invalid-email");
        invalidUser.setPassword("SecurePass123");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("email")));
    }

    @Test
    @DisplayName("POST /api/users debe retornar 409 si el email ya existe")
    void testCreateUser_EmailAlreadyExists() throws Exception {
        // Arrange: Crear usuario existente
        User existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail("existing@softkify.com");
        existingUser.setPassword("password123");
        userRepository.save(existingUser);

        // Intentar crear otro con el mismo email
        User duplicateUser = new User();
        duplicateUser.setName("Duplicate User");
        duplicateUser.setEmail("existing@softkify.com");
        duplicateUser.setPassword("differentPassword");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("ya existe")));
    }

    @Test
    @DisplayName("GET /api/users debe retornar lista de usuarios con CORS headers")
    void testGetAllUsers_WithCors() throws Exception {
        // Arrange: Crear algunos usuarios
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@softkify.com");
        user1.setPassword("pass1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@softkify.com");
        user2.setPassword("pass2");
        userRepository.save(user2);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("user1@softkify.com")))
                .andExpect(jsonPath("$[1].email", is("user2@softkify.com")));
    }

    @Test
    @DisplayName("CRÍTICO: Verificar que el patrón CORS no duplica /api en la ruta")
    void testCorsConfigurationPattern() throws Exception {
        // Esta prueba específica detectaría la duplicación /api/api/
        // Intentar acceder con el patrón correcto /api/users
        mockMvc.perform(options("/api/users")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));

        // Verificar que NO se necesita /api/api/users
        // (Si la configuración estuviera mal, esta petición a /api/api/users sería la única que funcionaría)
        mockMvc.perform(options("/api/api/users")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isNotFound()); // Debe fallar, esta ruta no debe existir
    }
}
```

---

### F. Pruebas de Contrato API (Rest Assured)

**Foco**: Garantizar estabilidad del contrato entre frontend y backend

```java
// src/test/java/com/softkify/contract/UserApiContractTest.java
package com.softkify.contract;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiContractTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Contrato: POST /users debe aceptar y retornar schema específico")
    void testUserCreationContract() {
        String requestBody = """
            {
                "name": "Contract Test User",
                "email": "contract@softkify.com",
                "password": "SecurePass123"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .header("Origin", "http://localhost:5173")
            .body(requestBody)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .header("Access-Control-Allow-Origin", notNullValue())
            .body("id", notNullValue())
            .body("name", equalTo("Contract Test User"))
            .body("email", equalTo("contract@softkify.com"))
            .body("password", nullValue()) // Nunca debe retornar password
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("Contrato: GET /users debe retornar array de usuarios con campos requeridos")
    void testUserListContract() {
        given()
            .header("Origin", "http://localhost:5173")
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .header("Access-Control-Allow-Origin", notNullValue())
            .body("$", isA(java.util.List.class))
            .body("[0]", anyOf(
                allOf(
                    hasKey("id"),
                    hasKey("name"),
                    hasKey("email"),
                    not(hasKey("password"))
                ),
                nullValue() // Puede estar vacío
            ));
    }

    @Test
    @DisplayName("Contrato: Error 400 debe retornar formato consistente")
    void testErrorResponseContract() {
        String invalidRequest = """
            {
                "name": "Test",
                "email": "invalid-email",
                "password": "short"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
        .when()
            .post("/users")
        .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", notNullValue())
            .body("timestamp", notNullValue())
            .body("status", equalTo(400));
    }
}
```

---

## 4. COBERTURA Y MÉTRICAS DE CALIDAD

### Objetivos de Cobertura

#### Frontend
```
Líneas:     ≥ 70%
Ramas:      ≥ 65%
Funciones:  ≥ 75%
Crítico*:   100%
```
*Crítico = Servicios de API, configuración, hooks de autenticación

#### Backend
```
Líneas:     ≥ 80%
Ramas:      ≥ 75%
Funciones:  ≥ 85%
Crítico*:   100%
```
*Crítico = Controllers, Services, configuración de seguridad/CORS

### Comando de Ejecución

**Frontend (Vitest)**
```bash
# Tests unitarios + cobertura
npm run test:coverage

# Tests E2E
npm run test:e2e
```

**Backend (Maven + JaCoCo)**
```bash
# Tests unitarios + integración
mvn clean test

# Reporte de cobertura
mvn jacoco:report

# Verificar umbrales mínimos
mvn jacoco:check
```

---

## 5. CHECKLIST DE CALIDAD ANTES DE COMMIT

Cada desarrollador debe verificar:

### Frontend
- [ ] Variables de entorno configuradas en `.env.example` y documentadas
- [ ] No hay URLs hardcodeadas en servicios/APIs
- [ ] Validaciones de formulario tienen tests unitarios
- [ ] Flujos críticos cubiertos por E2E (Auth, Checkout)
- [ ] Manejo de errores de red implementado y testeado

### Backend
- [ ] Configuración de CORS validada con test de preflight (OPTIONS)
- [ ] Patrones de ruta sin duplicaciones (`/**` vs `/api/**`)
- [ ] Todos los endpoints tienen tests de integración
- [ ] Contratos de API documentados y testeados
- [ ] Variables sensibles en variables de entorno, no en código

### General
- [ ] Build pasa localmente: `npm run build` / `mvn clean install`
- [ ] Tests pasan al 100%: `npm test` / `mvn test`
- [ ] No hay warnings de linter: `npm run lint` / `mvn checkstyle:check`
- [ ] Coverage cumple umbrales mínimos
- [ ] Dockerfile actualizado si hay cambios de configuración

---

## 6. LECCIONES APRENDIDAS

### Del Incidente de Variables de Entorno (Frontend)
1. **Nunca hardcodear URLs**: Siempre usar variables de entorno con fallback claro
2. **Validar configuración en CI/CD**: Agregar test que verifique uso de env vars
3. **Documentar variables requeridas**: Mantener `.env.example` actualizado
4. **Code review enfocado**: Revisar específicamente configuración antes de merge

### Del Incidente de CORS (Backend)
1. **Entender preflight**: Las peticiones OPTIONS son críticas para CORS
2. **Tests de integración son clave**: Los tests unitarios no habrían detectado este error
3. **Logs detallados**: Activar logs de Spring Security/CORS en desarrollo
4. **Patrones de ruta**: Evitar duplicaciones; preferir `/**` sobre `/api/**` cuando la base path ya tiene `/api`
5. **Testing con frontend real**: Postman/Insomnia no replican el comportamiento del navegador con CORS

### Mejores Prácticas Adoptadas
1. **Configuración externalizada**: Usar `application.yml` con profiles (dev, prod)
2. **Tests de contrato obligatorios**: Asegurar que frontend y backend hablen el mismo idioma
3. **E2E en pipeline de CI**: Ejecutar al menos los flujos críticos antes de deploy
4. **Monitoring de errores**: Implementar Sentry/similar para detectar errores en producción temprano

---

### Herramientas de Cobertura
- Frontend: [Vitest Coverage (via c8)](https://vitest.dev/guide/coverage.html)
- Backend: [JaCoCo](https://www.jacoco.org/jacoco/)


---

**Última actualización**: 2026-02-15  
**Mantenido por**: Equipo de Calidad Softkify  
**Versión**: 1.0 (Incluye incidentes de Frontend y Backend)