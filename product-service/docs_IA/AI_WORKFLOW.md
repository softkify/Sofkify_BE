# AI_WORKFLOW – Estrategia de Trabajo AI-First

## 1. Propósito del Documento

Este documento define **cómo se utiliza la Inteligencia Artificial durante el desarrollo del proyecto Ecommerce Asíncrono**.

Establece un **flujo de trabajo controlado**, donde la IA actúa como **asistente técnico** bajo la dirección del arquitecto del proyecto.

Este archivo **no define reglas de negocio ni arquitectura**.  
Su objetivo es garantizar **coherencia, calidad y control** en el uso de IA.

---

## 2. Rol de la IA en el Proyecto

La IA actúa como:

* Desarrollador técnico asistido
* Generador de propuestas de diseño
* Acelerador de implementación y pruebas
* Soporte para documentación técnica

La IA **NO** actúa como:

* Arquitecto del sistema
* Dueño del dominio
* Autoridad de decisiones técnicas finales
* Sustituto de validación humana

Toda decisión final es responsabilidad del arquitecto.

---

## 3. Fuentes de Verdad del Proyecto

Antes de cualquier interacción con la IA, deben existir y usarse como contexto obligatorio:

1. `context.md`  
   → Fuente de verdad del dominio y reglas de negocio.

2. `architecture.md`  
   → Fuente de verdad técnica y estructural.

3. `HU-*.md`  
   → Historias de usuario técnicas que delimitan el alcance.

La IA **no puede contradecir** estos documentos.

### Precedencia en caso de conflicto

En caso de conflicto entre documentos, se aplica el siguiente orden
de precedencia obligatoria:

1. `context.md` – reglas de negocio y dominio
2. `architecture.md` – estructura y decisiones técnicas
3. `HU-*.md` – alcance específico de implementación
4. Código existente aprobado

La IA debe **detenerse y pedir aclaración** si detecta inconsistencias.

---

## 4. Principios de Uso de IA

* La IA **no trabaja sin contexto**.
* La IA **no implementa sin una historia de usuario**.
* La IA **no redefine arquitectura**.
* La IA **no introduce lógica de negocio fuera del dominio**.
* La IA **siempre justifica su propuesta** cuando diseña.

El uso de IA es **guiado, iterativo y validado**.

---

## 5. Flujo de Trabajo AI-First

### 5.1 Planificación

Objetivo: construir entendimiento del sistema.

Actividades:

* Definición o refinamiento del dominio.
* Redacción del contexto del sistema.
* Definición de flujos de negocio.
* Creación o ajuste de historias de usuario.
* Identificación de requisitos funcionales y no funcionales.

Uso de IA:

* Ayuda a estructurar ideas.
* Refina redacciones.
* Propone alternativas conceptuales.

Validación:

* El arquitecto revisa y aprueba.
* Ningún documento se considera definitivo sin validación humana.

Entregables:

* `context.md`
* `HU-*.md`
* `architecture.md` (solo si se toman nuevas decisiones estructurales)

---

### 5.2 Desarrollo

Objetivo: implementar historias de usuario de forma controlada.

Flujo por historia:

1. El arquitecto entrega a la IA:
   * Historia de usuario
   * Contexto relevante
   * Lineamientos de arquitectura

2. La IA genera un **Plan de Implementación**, que incluye:
   * Casos de uso involucrados
   * Capas afectadas
   * Componentes a crear o modificar
   * Flujo técnico esperado

3. El arquitecto valida el plan.

4. La IA genera el código alineado al plan aprobado.

5. El arquitecto revisa:
   * Diseño
   * Ubicación del código
   * Cumplimiento de arquitectura
   * Claridad y limpieza

6. La IA ajusta según feedback.

7. Solo entonces se integra el código.

Regla clave:
> La IA **no escribe código directamente sin un plan validado**.

---

### 5.3 QA

Objetivo: validar calidad y corrección funcional.

Uso de IA:

* Proponer casos de prueba.
* Generar tests unitarios.
* Proponer escenarios de error.
* Ayudar a detectar edge cases.

Validación humana:

* Ejecución de flujos funcionales.
* Revisión de manejo de errores.
* Validación de integración con mensajería.

Una historia **no se considera completa** hasta pasar QA.

---

### 5.4 Despliegue / Instalación

Objetivo: asegurar que el sistema pueda ejecutarse correctamente.

Uso de IA:

* Generar guías de instalación.
* Proponer configuración de entornos.
* Ayudar con Dockerización y scripts.

Validación:

* El arquitecto prueba el proceso en entorno limpio.
* Se documenta el procedimiento final.

### Gestión de Documentación

* Los archivos `context.md` y `architecture.md` solo se modifican
  durante la fase de planificación o por decisión explícita del arquitecto.
* Las historias de usuario pueden evolucionar durante el desarrollo.
* La IA **no modifica documentos base** sin instrucción explícita.

---

## 6. Reglas Obligatorias de Interacción

* Toda solicitud a la IA debe incluir:
  * Qué se está construyendo
  * En qué capa
  * Qué restricciones aplicar
  * Qué ya existe

* Ningún código entra sin revisión humana.
* Ninguna historia se marca como finalizada sin validación.
* La IA no toma decisiones finales.

---

## 7. Formato Esperado de Solicitudes a la IA

Ejemplo correcto:

> “Con base en la HU-ORDER-01, el `context.md` y `architecture.md`, genera el plan de implementación del caso de uso CreateOrderUseCase siguiendo arquitectura hexagonal.”

Ejemplo incorrecto:

> “Haz el servicio de órdenes.”

---

## 8. Validación y Aprobación

El ciclo de validación es:

Arquitecto define → IA propone → Arquitecto revisa → IA ajusta → Arquitecto aprueba → Se integra.

Este ciclo es obligatorio.

---

## 9. Antipatrones (Prohibido)

* Pedir código sin historia de usuario.
* Pedir soluciones genéricas.
* Permitir que la IA redefina arquitectura.
* Mezclar reglas de negocio en infraestructura.
* Integrar código sin revisión.

---

Este documento es **vinculante** para el uso de IA durante todo el proyecto.
