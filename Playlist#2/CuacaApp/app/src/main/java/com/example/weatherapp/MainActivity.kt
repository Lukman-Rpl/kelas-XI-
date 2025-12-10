package com.example.weatherapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cityInput: EditText
    private lateinit var searchBtn: Button
    private lateinit var tempText: TextView
    private lateinit var conditionText: TextView
    private lateinit var background: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityInput = findViewById(R.id.cityInput)
        searchBtn = findViewById(R.id.searchBtn)
        tempText = findViewById(R.id.tempText)
        conditionText = findViewById(R.id.conditionText)
        background = findViewById(R.id.bgImage)

        searchBtn.setOnClickListener {
            val city = cityInput.text.toString()
            if (city.isNotEmpty()) getWeather(city)
        }
    }

    private fun getWeather(city: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getWeatherByCity(city)
                tempText.text = "${response.main.temp}°C"
                conditionText.text = response.weather[0].main
                updateBackground(response.weather[0].main)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Kota tidak ditemukan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateBackground(condition: String) {
        when (condition.lowercase()) {
            "rain" -> background.setImageResource(R.drawable.bg_rain)
            "clear" -> background.setImageResource(R.drawable.bg_sunny)
            "clouds" -> background.setImageResource(R.drawable.bg_cloudy)
            else -> background.setImageResource(R.drawable.bg_default)
        }
    }
}
