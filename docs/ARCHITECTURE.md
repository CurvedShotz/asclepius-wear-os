# Architecture

- Wear OS client collecting and streaming wearable sensor metrics over HTTP.
- FastAPI backend serving ingestion endpoints and persisting records to SQLite.
- Scikit-learn anomaly engine computing baseline metrics and scoring deviations.
- Streamlit dashboard querying backend APIs for telemetry charts and alert logs.
