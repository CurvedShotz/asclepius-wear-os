"""FastAPI application entrypoint and route definitions."""

import logging

from fastapi import FastAPI

from backend.schemas import Reading

app = FastAPI(title="Asclepius API")
logger = logging.getLogger(__name__)
received_readings: list[Reading] = []


@app.get("/health")
def health() -> dict[str, str]:
	return {"status": "ok"}


@app.post("/readings")
def receive_reading(reading: Reading) -> dict[str, int | str | bool]:
	received_readings.append(reading)
	logger.info("Received reading: %s", reading.model_dump_json())
	return {
		"received": True,
		"heart_rate": reading.heart_rate,
		"activity_context": reading.activity_context,
		"scenario": reading.scenario,
	}


@app.get("/readings", response_model=list[Reading])
def get_readings() -> list[Reading]:
	return received_readings
