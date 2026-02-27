# VeterinariaApp - Actividad Sumativa 3 (Semana 8)
## Validando y publicando tu aplicación Android

### 📝 Descripción de la Actividad
Este proyecto corresponde a la fase final de consolidación de la **VeterinariaApp**. Se ha refactorizado la arquitectura bajo el patrón **MVVM**, implementado componentes modernos de **Jetpack**, y diseñado una suite de pruebas robusta (unitarias y funcionales) para asegurar la calidad del software antes de su empaquetado y publicación.

---

### 🏗️ Arquitectura y Componentes Aplicados
Se ha estructurado el proyecto siguiendo las mejores prácticas de modularidad y separación de capas:

1.  **Capa de Vista (UI):** 
    *   Desarrollada íntegramente con **Jetpack Compose**.
    *   **Coil (Image Loading):** Carga asíncrona de imágenes de red para veterinarios e iconos de mascotas.
    *   **Accesibilidad:** Soporte para modo oscuro y escalado de fuentes adaptable (Optimizado para Samsung S24).
2.  **Capa de Lógica (ViewModel):** 
    *   Uso de `ViewModel` y `StateFlow` para una reactividad de datos eficiente.
3.  **Capa de Datos (Repository & Local):** 
    *   **Room Database:** Persistencia local de datos.
    *   **Retrofit:** Consumo de API REST.
    *   **Repository Pattern:** Abstracción de datos para facilitar pruebas.
4.  **Navegación:** 
    *   **Navigation Compose** para flujos entre pantallas con transiciones animadas.

---

### 🧪 Detalle de Pruebas Implementadas
Para garantizar la estabilidad, se han aplicado pruebas en dos niveles:

#### 1. Pruebas Unitarias (JUnit 4 + MockK + Turbine)
*   **Lógica de Negocio:** Validación de entradas en `ValidationUtils`.
*   **Gestión de Estados:** Pruebas en `RegistroViewModel` (carrito, stock y limpieza de datos).

#### 2. Pruebas Funcionales e Instrumentadas (Compose Test / Espresso)
*   **Flujos de UI:** Simulación de Login, navegación a la Agenda y proceso de Registro de mascotas.
*   **Robustez:** Manejo de esperas asíncronas (`waitUntil`) y permisos (`GrantPermissionRule`).

---

### 🚀 Instrucciones para Ejecución

#### Requisitos
*   Android Studio Iguana o superior.
*   Dispositivo físico o emulador con API 24 o superior.

#### Pasos para ejecutar la App
1.  Importar el proyecto en Android Studio.
2.  Sincronizar Gradle.
3.  Presionar el botón **Run 'app'** para instalar en modo Debug, o instalar el **APK firmado** incluido en la entrega.

#### Pasos para ejecutar las Pruebas
1.  **Pruebas Unitarias:** Click derecho en `app/src/test` -> *Run 'Tests in cl.duoc.veterinaria'*.
2.  **Pruebas de UI:** Iniciar un emulador y ejecutar las pruebas en `app/src/androidTest`.

---

### 📸 Evidencias de Validación

#### 1. Resultado Pruebas Unitarias
Validación exitosa de la lógica interna.
![Resultado Pruebas Unitarias](screenshots/unit_tests_results.png)

#### 2. Resultado Pruebas de UI
Validación de navegación y flujos de usuario.
![Resultado Pruebas de UI](screenshots/android_tests_results.png)

---

### 🚀 Cierre Técnico del Proyecto
*   **Refactorización:** Código limpio bajo MVVM y desacoplado.
*   **Optimización:** Configuración de versión **1.1 (Build 2)** y diseño responsivo para dispositivos modernos.
*   **Publicación:** Generación de **APK firmado** funcional.

---
**Desarrollado por:** Liliana Tapia  
**Asignatura:** Desarrollo de Aplicaciones Móviles II  
**Institución:** DUOC UC
