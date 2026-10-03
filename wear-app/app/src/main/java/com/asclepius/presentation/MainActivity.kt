package com.asclepius.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

private const val BACKEND_URL = "http://10.0.2.2:8000"
private const val READING_DELAY_MS = 1000L

private data class SimulatedReading(
    val heartRate: Int,
    val activityContext: String
)

private data class SimulationScenario(
    val id: String,
    val name: String,
    val readings: List<SimulatedReading>
)

private val SIMULATION_SCENARIOS = listOf(
    SimulationScenario(
        id = "normal_day",
        name = "Normal Day",
        readings = listOf(
            SimulatedReading(71, "RESTING"),
            SimulatedReading(73, "RESTING"),
            SimulatedReading(72, "RESTING"),
            SimulatedReading(74, "RESTING"),
            SimulatedReading(70, "RESTING"),
            SimulatedReading(86, "WALKING"),
            SimulatedReading(92, "WALKING"),
            SimulatedReading(98, "WALKING"),
            SimulatedReading(103, "WALKING"),
            SimulatedReading(99, "WALKING"),
            SimulatedReading(118, "EXERCISE"),
            SimulatedReading(132, "EXERCISE"),
            SimulatedReading(144, "EXERCISE"),
            SimulatedReading(151, "EXERCISE"),
            SimulatedReading(147, "EXERCISE"),
            SimulatedReading(133, "RECOVERY"),
            SimulatedReading(116, "RECOVERY"),
            SimulatedReading(102, "RECOVERY"),
            SimulatedReading(91, "RECOVERY"),
            SimulatedReading(82, "RECOVERY")
        )
    ),
    SimulationScenario(
        id = "resting_anomaly",
        name = "Resting Anomaly",
        readings = listOf(
            SimulatedReading(72, "RESTING"),
            SimulatedReading(73, "RESTING"),
            SimulatedReading(71, "RESTING"),
            SimulatedReading(74, "RESTING"),
            SimulatedReading(136, "RESTING"),
            SimulatedReading(139, "RESTING"),
            SimulatedReading(134, "RESTING"),
            SimulatedReading(76, "RESTING")
        )
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    var connectionStatus by remember { mutableStateOf("Not connected") }
    var isRunning by remember { mutableStateOf(false) }
    var selectedScenarioIndex by remember { mutableStateOf(0) }
    var currentReading by remember { mutableStateOf<SimulatedReading?>(null) }
    var readingNumber by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val selectedScenario = SIMULATION_SCENARIOS[selectedScenarioIndex]

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.clickable(enabled = !isRunning) {
                    selectedScenarioIndex = (selectedScenarioIndex + 1) % SIMULATION_SCENARIOS.size
                    currentReading = null
                    readingNumber = 0
                    connectionStatus = "Not connected"
                },
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(text = "Asclepius", style = MaterialTheme.typography.title2)
                Text(
                    text = "Scenario: ${selectedScenario.name}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption2
                )
            }
            Text(
                text = "${currentReading?.heartRate ?: "--"} BPM",
                style = MaterialTheme.typography.title1
            )
            Text(
                text = currentReading?.activityContext ?: "Ready",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Reading $readingNumber / ${selectedScenario.readings.size}",
                style = MaterialTheme.typography.caption2
            )
            Text(
                text = connectionStatus,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                enabled = !isRunning,
                onClick = {
                    coroutineScope.launch {
                        isRunning = true
                        readingNumber = 0
                        connectionStatus = "Sending..."
                        try {
                            selectedScenario.readings.forEachIndexed { index, reading ->
                                currentReading = reading
                                readingNumber = index + 1
                                connectionStatus = "Sending..."
                                try {
                                    withContext(Dispatchers.IO) {
                                        sendReading(reading, selectedScenario.id)
                                    }
                                } catch (error: Exception) {
                                    connectionStatus =
                                        "Failed: ${error.localizedMessage ?: "Request error"}"
                                    return@launch
                                }
                                connectionStatus = if (index == selectedScenario.readings.lastIndex) {
                                    "Connected - complete"
                                } else {
                                    "Connected"
                                }
                                if (index < selectedScenario.readings.lastIndex) {
                                    delay(READING_DELAY_MS)
                                }
                            }
                        } finally {
                            isRunning = false
                        }
                    }
                }
            ) {
                Text(
                    text = if (isRunning) "Sending..." else "Start\nSimulation",
                    textAlign = TextAlign.Center,
                    fontSize = 8.sp,
                    lineHeight = 9.sp,
                    maxLines = 2
                )
            }
        }
    }
}

private fun sendReading(reading: SimulatedReading, scenario: String) {
    val connection = URL("$BACKEND_URL/readings").openConnection() as HttpURLConnection
    try {
        connection.requestMethod = "POST"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")

        val readingJson = JSONObject()
            .put("heart_rate", reading.heartRate)
            .put("activity_context", reading.activityContext)
            .put("timestamp", Instant.now().toString())
            .put("scenario", scenario)
        connection.outputStream.use { outputStream ->
            outputStream.write(readingJson.toString().toByteArray(Charsets.UTF_8))
        }

        if (connection.responseCode !in 200..299) {
            throw IOException("Backend returned HTTP ${connection.responseCode}")
        }
    } finally {
        connection.disconnect()
    }
}
