# Asclepius — Context-Aware Wearable Health Monitoring

Asclepius is a lightweight, context-aware wearable health monitoring system designed to detect physiological anomalies by combining continuous sensor telemetry with contextual baseline tracking. Built as an end-to-end student prototype, it streams heart rate and motion data from a Wear OS smartwatch to a FastAPI service, evaluates deviations against personalized baseline profiles using scikit-learn, persists records in SQLite, and visualizes real-time metrics and alerts via a Streamlit dashboard.

## Current Status

**MVP in development**

## High-Level Architecture

The system consists of four primary components:

1. **Wear OS App (`wear-app/`)**: Gathers biometric sensor data (heart rate, motion/accelerometer) on-device using Jetpack Compose and streams telemetry payloads over HTTP.
2. **Backend Service (`backend/`)**: FastAPI application providing lightweight ingestion and query endpoints backed by SQLite for local persistence.
3. **ML & Anomaly Module (`ml/`)**: Extracts features from incoming sensor readings and computes personalized baselines using scikit-learn anomaly detection algorithms.
4. **Monitoring Dashboard (`dashboard/`)**: Streamlit web dashboard providing real-time telemetry charts, anomaly highlights, and health trend summaries.
