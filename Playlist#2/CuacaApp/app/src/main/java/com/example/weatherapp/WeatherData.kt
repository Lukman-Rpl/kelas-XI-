package com.example.weatherapp

data class WeatherData(
    val weather: List<Weather>,
    val main: Main,
    val name: String
)

data class Weather(val main: String, val description: String, val icon: String)
data class Main(val temp: Double)
