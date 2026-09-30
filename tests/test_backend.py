"""Tests for the simulated reading API."""

from fastapi.testclient import TestClient

from backend.main import app, received_readings

client = TestClient(app)


def test_health_returns_ok() -> None:
	response = client.get("/health")

	assert response.status_code == 200
	assert response.json() == {"status": "ok"}


def test_reading_schema_and_history_preserve_order() -> None:
	scenario_groups = [
		("normal_day", "RESTING", [71, 73, 72, 74, 70]),
		("normal_day", "WALKING", [86, 92, 98, 103, 99]),
		("normal_day", "EXERCISE", [118, 132, 144, 151, 147]),
		("normal_day", "RECOVERY", [133, 116, 102, 91, 82]),
		("resting_anomaly", "RESTING", [72, 73, 71, 74, 136, 139, 134, 76]),
	]
	readings = []
	for scenario, activity_context, heart_rates in scenario_groups:
		for heart_rate in heart_rates:
			readings.append(
				{
					"heart_rate": heart_rate,
					"activity_context": activity_context,
					"timestamp": f"2026-09-30T00:00:{len(readings):02d}Z",
					"scenario": scenario,
				}
			)
	starting_count = len(received_readings)

	for reading in readings:
		response = client.post("/readings", json=reading)

		assert response.status_code == 200
		assert response.json() == {
			"received": True,
			"heart_rate": reading["heart_rate"],
			"activity_context": reading["activity_context"],
			"scenario": reading["scenario"],
		}

	history = client.get("/readings")

	assert history.status_code == 200
	assert history.json()[starting_count:] == readings
