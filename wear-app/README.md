# Asclepius Wear OS Application

A lightweight Wear OS companion app built with Kotlin and Jetpack Compose for Wear OS.

## Overview

The Wear OS app is responsible for:
- Collecting real-time biometric telemetry (heart rate, step counts, accelerometer).
- Displaying current vital status and active monitoring indicators on the watch face.
- Packaging and transmitting sensor payloads to the Asclepius backend API over HTTP.

## Project Structure

```
wear-app/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/asclepius/
│           │   └── presentation/
│           │       ├── MainActivity.kt
│           │       └── theme/
│           │           └── Theme.kt
│           └── res/
│               └── values/
│                   └── strings.xml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## Getting Started

1. Open the `wear-app` directory in Android Studio (Giraffe / Iguana / Koala or newer).
2. Sync the project with Gradle files.
3. Deploy to a Wear OS emulator or physical Wear OS smartwatch running Wear OS 3.0+ (API level 30+).
