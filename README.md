# Android News App - "Noticias"

## 📑 Índice / Index

- [Español (Spanish)](#español)
    - [Características](#características)
    - [Stack Tecnológico](#stack-tecnológico)
    - [Estructura del Proyecto](#estructura-del-proyecto)
    - [Configuración](#configuración)
    - [Cómo Ejecutar](#cómo-ejecutar)
- [English](#english)
    - [Features](#features)
    - [Tech Stack](#tech-stack)
    - [Project Structure](#project-structure)
    - [Configuration](#configuration)
    - [How to Run](#how-to-run)

---

<a name="español"></a>
## 🇪🇸 Español

Una aplicación Android moderna construida con Kotlin y Jetpack Compose que obtiene datos de noticias de Google Sheets, los persiste localmente usando Room y proporciona actualizaciones periódicas en segundo plano mediante WorkManager.

### Características
- **Contenido Dinámico**: Obtiene noticias en tiempo real desde una salida CSV de Google Sheets.
- **Soporte Multilingüe**: Soporta noticias en español, inglés y francés según el idioma del dispositivo o la preferencia del usuario.
- **Persistencia Local**: Utiliza **Room Database** para almacenar noticias y permitit su lectura offline.
- **Actualizaciones en Segundo Plano**: Implementa **WorkManager** para la sincronización periódica en segundo plano.
- **UI Responsiva**: Construida con **Jetpack Compose** y **Material 3**, con un diseño de doble panel para tablets.
- **Búsqueda y Filtro**: Permite buscar entre las noticias y ordenar por fecha o importancia.
- **Benchmark Asíncrono**: Incluye una herramienta de benchmark integrada para probar la respuesta de la UI durante procesos pesados.

### Stack Tecnológico
- **Lenguaje**: Kotlin
- **Framework de UI**: Jetpack Compose / Material 3
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Base de Datos**: Room
- **Tareas en Segundo Plano**: WorkManager
- **Carga de Imágenes**: Coil
- **Red**: HttpURLConnection (Parser CSV personalizado)

### Estructura del Proyecto
```text
MyApplication/
├── app/
│   ├── src/main/java/com/example/myapplication/
│   │   ├── data/          # Entidades Room, DAOs y Repositorios
│   │   ├── ui/            # Pantallas Compose, ViewModels y lógica de UI
│   │   ├── MainActivity   # Punto de entrada principal con lógica de Fragmentos/Views
│   │   └── NewsWorker     # Implementación de WorkManager para notificaciones
│   └── res/               # Recursos de Android (layouts, strings, drawables)
├── build.gradle.kts       # Configuración de construcción a nivel de proyecto
└── settings.gradle.kts    # Configuración de módulos
```

### Configuración
La aplicación utiliza Google Sheets como fuente de datos principal. Las URLs están definidas en `NewsRepository.kt`.

### Cómo Ejecutar
1. Clona el repositorio.
2. Abre el proyecto en **Android Studio (Ladybug o posterior)**.
3. Sincroniza el proyecto con los archivos Gradle.
4. Ejecuta en un emulador o dispositivo físico (API 24+).

---

<a name="english"></a>
## 🇺🇸 English

A modern Android application built with Kotlin and Jetpack Compose that fetches news data from Google Sheets, persists it locally using Room, and provides periodic background updates using WorkManager.

### Features
- **Dynamic Content**: Fetches news in real-time from a Google Sheets CSV output.
- **Multilingual Support**: Supports news in Spanish, English, and French based on device locale or user preference.
- **Local Persistence**: Uses **Room Database** to store news for offline viewing.
- **Background Updates**: Implements **WorkManager** for periodic background synchronization.
- **Responsive UI**: Built with **Jetpack Compose** and **Material 3**, featuring a two-pane layout for tablets.
- **Search & Sort**: Search through news and sort by date or importance.
- **Asynchronous Benchmark**: Includes a built-in benchmark tool to test UI responsiveness during heavy processing.

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose / Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room
- **Background Tasks**: WorkManager
- **Image Loading**: Coil
- **Network**: HttpURLConnection (Custom CSV Parser)

### Project Structure
```text
MyApplication/
├── app/
│   ├── src/main/java/com/example/myapplication/
│   │   ├── data/          # Room entities, DAOs, and Repositories
│   │   ├── ui/            # Compose screens, ViewModels, and UI logic
│   │   ├── MainActivity   # Main entry point with Fragment/View logic
│   │   └── NewsWorker     # WorkManager implementation for notifications
│   └── res/               # Android resources (layouts, strings, drawables)
├── build.gradle.kts       # Project-level build configuration
└── settings.gradle.kts    # Module settings
```

### Configuration
The app currently uses Google Sheets as its primary data source. The URLs are defined in `NewsRepository.kt`.

### How to Run
1. Clone the repository.
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync project with Gradle files.
4. Run on an Emulator or Physical Device (API 24+).

---
*Created as part of a technical demonstration for Android Development.*
