# Asclepius — Context-Aware Wearable Health Monitoring

Asclepius is a lightweight, context-aware wearable health monitoring system designed to detect physiological anomalies by combining continuous sensor telemetry with contextual baseline tracking. Built as an end-to-end student prototype, it streams heart rate and motion data from a Wear OS smartwatch to a FastAPI service, evaluates deviations against personalized baseline profiles using scikit-learn, persists records in SQLite, and visualizes real-time metrics and alerts via a Streamlit dashboard.

## Current Status

**MVP in development**

## Local Development: Simulated Readings

Start the backend from the repository root:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r backend/requirements.txt
python -m uvicorn backend.main:app --host 0.0.0.0 --port 8000 --reload
```

Check `http://localhost:8000/health` or open `http://localhost:8000/docs` to send a test reading with an ISO-8601 `timestamp` and a `scenario`. `GET /readings` shows readings received since the backend started; history is in memory and is cleared when the server stops. The Wear OS emulator reaches the computer running the backend at `10.0.2.2`, so the app posts to `http://10.0.2.2:8000/readings`. Keep the backend running while testing. The app's cleartext HTTP setting is for local development only; production should use HTTPS.

To build and launch the app, open `wear-app/` in Android Studio, sync Gradle, and run the `app` configuration on a Wear OS emulator. Tap the scenario name to switch between **Normal Day** and **Resting Anomaly**, then tap **Start Simulation**. Each reading is sent in order with a one-second delay; confirm the watch reaches `Connected - complete` and inspect `GET /readings` or the backend terminal. The project currently has no Gradle wrapper, so use Android Studio's bundled Gradle to build it.

## High-Level Architecture

The system consists of four primary components:

1. **Wear OS App (`wear-app/`)**: Gathers biometric sensor data (heart rate, motion/accelerometer) on-device using Jetpack Compose and streams telemetry payloads over HTTP.
2. **Backend Service (`backend/`)**: FastAPI application providing lightweight ingestion and query endpoints backed by SQLite for local persistence.
3. **ML & Anomaly Module (`ml/`)**: Extracts features from incoming sensor readings and computes personalized baselines using scikit-learn anomaly detection algorithms.
4. **Monitoring Dashboard (`dashboard/`)**: Streamlit web dashboard providing real-time telemetry charts, anomaly highlights, and health trend summaries.
