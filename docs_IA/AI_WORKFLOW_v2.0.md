# AI_WORKFLOW_v2.0

## 1. Fase 1: Diagnóstico y "Snapshot" (Análisis de Antipatrones)

**Objetivo:** Realizar una auditoría completa del estado actual del código base para identificar deudas técnicas y antipatrones antes de continuar con el desarrollo.

**Rol de la IA:** Auditor Técnico

### 1.1 Auditoría de Deuda e Inconsistencias

La IA actuará como auditor técnico para inspeccionar sistemáticamente el código base:

**🔍 Áreas de Auditoría:**

1. **Violaciones de Principios SOLID:**
    - **SRP (Single Responsibility Principle):** Identificar clases con múltiples responsabilidades
    - **OCP (Open/Closed Principle):** Detectar código que requiere modificación para extenderse
    - **LSP (Liskov Substitution Principle):** Encontrar jerarquías que rompen sustitución
    - **ISP (Interface Segregation Principle):** Identificar interfaces con responsabilidades múltiples
    - **DIP (Dependency Inversion Principle):** Detectar dependencias directas sobre implementaciones

2. **Code Smells Críticos:**
    - **Acoplamiento Rígido:** Módulos que dependen fuertemente de otros
    - **Lógica Duplicada:** Código repetido entre diferentes componentes
    - **Falta de Abstracción:** Ausencia de capas de abstracción adecuadas
    - **Clases Dios:** Objetos con demasiadas responsabilidades
    - **Nombres Confusos:** Métodos y variables con nombres poco descriptivos

3. **Problemas Arquitectónicos:**
    - **Violaciones de Arquitectura Hexagonal:** Lógica de negocio en capas de infraestructura
    - **Fugas de Dominio:** Reglas de negocio escapando del dominio
    - **Dependencias Circulares:** Módulos que se referencian mutuamente
    - **Monolitos Ocultos:** Servicios que deberían estar separados pero están acoplados

### 1.2 Metodología de Auditoría IA

**📋 Proceso Sistemático:**

1. **Análisis Automatizado por Microservicio:**
    - La IA examina cada microservicio independientemente
    - Identifica patrones y antipatrones específicos de cada servicio
    - Genera reportes detallados por componente

2. **Validación Cruzada:**
    - Verifica consistencia entre servicios
    - Identifica duplicación de lógica entre microservicios
    - Analiza contratos y comunicación asíncrona

3. **Priorización de Hallazgos:**
    - **Críticos:** Violaciones que afectan la escalabilidad o mantenibilidad
    - **Medios:** Code smells que impactan la legibilidad
    - **Bajos:** Mejoras cosméticas o de estilo

### 1.3 Entregables

**📄 Documento AUDITORIA.md: (Esta documento se le entregara a la IA en cada interaccion con el fin de otorgarle una estructura estandarizada entre auditorias**

Para cada hallazgo se debe documentar:

1. **Identificación del Problema:**
    - Archivo y línea específica
    - Componente afectado
    - Tipo de antipatrón

2. **Principio Vulnerado:**
    - Principio SOLID o regla arquitectónica
    - Explicación técnica de la violación

3. **Impacto en el Sistema:**
    - Efecto en la escalabilidad
    - Impacto en la mantenibilidad
    - Riesgos técnicos asociados

4. **Recomendación de Mejora:**
    - Propuesta de refactoring 
    - Prioridad de corrección
    - Esfuerzo estimado

### 1.4 Reglas de Auditoría IA

- **No modificar código** durante esta fase
- **Solo reportar y documentar** hallazgos
- **Ser objetivo** y basarse en principios establecidos
- **Priorizar impacto** sobre preferencias personales
- **Validar con arquitecto** antes de finalizar reporte
