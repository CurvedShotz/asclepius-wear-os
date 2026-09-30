"""Pydantic data models for request and response validation."""

from datetime import datetime
from typing import Literal

from pydantic import BaseModel


class Reading(BaseModel):
	heart_rate: int
	activity_context: Literal["RESTING", "WALKING", "EXERCISE", "RECOVERY"]
	timestamp: datetime
	scenario: str
