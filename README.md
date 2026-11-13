# Noticias — app Android

[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room-offline--first-3DDC84?logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![GPL-3.0](https://img.shields.io/badge/Licencia-GPL--3.0-blue)](LICENSE)

[Español](#español) · [English](#english)

---

## Español

### Índice

- [Descripción](#descripción)
- [Características](#características)
- [Stack tecnológico](#stack-tecnológico)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Configuración](#configuración)
- [Compilación y ejecución](#compilación-y-ejecución)
- [Limitaciones](#limitaciones)
- [Licencia](#licencia)

### Descripción

Aplicación Android de noticias escrita en **Kotlin** con **Jetpack Compose** y **Material 3**. Descarga un CSV publicado desde **Google Sheets**, lo guarda en **Room** para poder leerlo sin conexión y lo mantiene al día con **WorkManager**.

### Características

- **Contenido dinámico**: lee las noticias de una hoja de cálculo publicada como CSV.
- **Multilingüe**: textos de la interfaz en español, inglés y francés según el idioma del dispositivo.
- **Persistencia local** con Room para consultar las noticias offline.
- **Sincronización en segundo plano** mediante WorkManager.
- **UI adaptativa** con diseño de doble panel en tablets.
- **Búsqueda y orden**: por fecha o por importancia.
- **Benchmark asíncrono** para medir la respuesta de la interfaz con cargas pesadas.

### Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Arquitectura | MVVM |
| Base de datos | Room |
| Tareas en segundo plano | WorkManager |
| Imágenes | Coil |
| Red | `HttpURLConnection` y parser CSV propio |

### Estructura del proyecto

```text
Noticias_android/
├── MyApplication/
│   ├── app/src/main/java/com/example/myapplication/
│   │   ├── MainActivity.kt         # Entrada y navegación
│   │   ├── DetailActivity.kt       # Detalle de la noticia
│   │   ├── NewsItem.kt             # Modelo de noticia
│   │   ├── NewsWorker.kt           # Sincronización con WorkManager
│   │   ├── data/                   # Entidad, DAO, base de datos y repositorio
│   │   ├── ui/                     # Pantallas Compose, adaptadores y ViewModel
│   │   └── AsyncBenchmark.kt       # Herramienta de medición
│   └── app/src/main/res/           # Recursos y temas
├── LICENSE
└── README.md
```

### Configuración

La fuente de datos son hojas de Google publicadas como CSV. Las URLs están en `data/NewsRepository.kt`: sustitúyelas por las de tu propia hoja si quieres usar contenido distinto.

Para publicar una hoja: **Archivo → Compartir → Publicar en la web → CSV**. Comprueba que el CSV tenga una fila de cabecera y que las columnas coincidan con las que espera el parser.

### Compilación y ejecución

1. Abre `MyApplication/` en **Android Studio Ladybug** o posterior.
2. Sincroniza Gradle.
3. Ejecuta en un emulador o dispositivo con **API 24 o superior**.

```bash
cd MyApplication
./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
```

La APK de depuración queda en `app/build/outputs/apk/debug/`.

### Limitaciones

Es una **demo técnica**: no hay backend propio, autenticación ni moderación de contenido, y depende de que la hoja publicada siga accesible. Google puede limitar o cambiar el formato de las hojas publicadas, así que para producción conviene una API con caché y control de errores.

### Licencia

**GNU General Public License v3.0 (GPL-3.0)** — consulta [LICENSE](LICENSE).

---

## English

### Table of contents

- [Overview](#overview)
- [Features](#features)
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Configuration](#configuration)
- [Build and run](#build-and-run)
- [Limitations](#limitations)
- [License](#license)

### Overview

An Android news app written in **Kotlin** with **Jetpack Compose** and **Material 3**. It downloads a CSV published from **Google Sheets**, stores it in **Room** for offline reading and keeps it fresh with **WorkManager**.

### Features

- **Dynamic content**: reads news from a spreadsheet published as CSV.
- **Multilingual**: UI strings in Spanish, English and French following the device locale.
- **Local persistence** with Room for offline reading.
- **Background sync** through WorkManager.
- **Adaptive UI** with a two-pane layout on tablets.
- **Search and sort**: by date or importance.
- **Async benchmark** to measure UI responsiveness under heavy work.

### Tech stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM |
| Database | Room |
| Background work | WorkManager |
| Images | Coil |
| Network | `HttpURLConnection` and a custom CSV parser |

### Project structure

```text
Noticias_android/
├── MyApplication/
│   ├── app/src/main/java/com/example/myapplication/
│   │   ├── MainActivity.kt         # Entry point and navigation
│   │   ├── DetailActivity.kt       # News detail
│   │   ├── NewsItem.kt             # News model
│   │   ├── NewsWorker.kt           # WorkManager sync
│   │   ├── data/                   # Entity, DAO, database and repository
│   │   ├── ui/                     # Compose screens, adapter and ViewModel
│   │   └── AsyncBenchmark.kt       # Measurement tool
│   └── app/src/main/res/           # Resources and themes
├── LICENSE
└── README.md
```

### Configuration

The data source is Google Sheets published as CSV. The URLs live in `data/NewsRepository.kt`: replace them with your own sheet if you want different content.

To publish a sheet: **File → Share → Publish to the web → CSV**. Make sure the CSV has a header row and that its columns match what the parser expects.

### Build and run

1. Open `MyApplication/` in **Android Studio Ladybug** or newer.
2. Sync Gradle.
3. Run on an emulator or device with **API 24 or later**.

```bash
cd MyApplication
./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
```

The debug APK lands in `app/build/outputs/apk/debug/`.

### Limitations

This is a **technical demo**: there is no backend, authentication or content moderation, and it relies on the published sheet staying reachable. Google may rate-limit or change the published format, so production use should sit behind an API with caching and error handling.

### License

**GNU General Public License v3.0 (GPL-3.0)** — see [LICENSE](LICENSE).
