package com.mad.quiz_241121

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.use
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var temperature by remember { mutableStateOf("Unknown") }

            LaunchedEffect(Unit) {
                launch(Dispatchers.IO) {
                    try {
                        // NOAA's forecast New Orleans' temperature
                        temperature = JSONObject(
                            run(
                                JSONObject(
                                    run("https://api.weather.gov/points/29.9511,-90.0715")
                                )
                                    .getJSONObject("properties")
                                    .getString("forecast")
                            )
                        )
                            .getJSONObject("properties")
                            .getJSONArray("periods")
                            .getJSONObject(0)
                            .getInt("temperature")
                            .toString()
                    } catch (e: Exception) {
                        temperature = e.localizedMessage?.toString() ?: "Error"
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "New Orleans Temperature")
                Text(text = "${LocalDate.now()}")
                Text(text = "Temperature: $temperature° F")
            }
        }
    }
}

fun run(url: String): String {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .header("User-Agent", "NOLA xavier@xula.edu")
        .build()

    client.newCall(request).execute().use { response ->
        return response.body?.string() ?: throw IOException("Unexpected empty response body")
    }
}