package com.example.patik.model

/**
 * Open-Meteo'nun gönderdiği WMO 4677 `weather_code` değerinin ekrandaki karşılığı.
 * Tanınmayan bir kod gelirse uygulama çökmez, [UNKNOWN] ile devam eder.
 */
enum class WeatherCondition(val emoji: String, val label: String) {
    CLEAR("☀️", "Açık"),
    MAINLY_CLEAR("🌤️", "Az bulutlu"),
    PARTLY_CLOUDY("⛅", "Parçalı bulutlu"),
    OVERCAST("☁️", "Kapalı"),
    FOG("🌫️", "Sisli"),
    DRIZZLE("🌦️", "Çisenti"),
    RAIN("🌧️", "Yağmurlu"),
    SNOW("🌨️", "Karlı"),
    RAIN_SHOWERS("🌦️", "Sağanak"),
    SNOW_SHOWERS("🌨️", "Kar sağanağı"),
    THUNDERSTORM("⛈️", "Fırtına"),
    UNKNOWN("🌡️", "Bilinmiyor");

    /** Çorap kararında "ıslak zemin" sayılan durumlar. */
    val isWet: Boolean
        get() = this in setOf(DRIZZLE, RAIN, RAIN_SHOWERS, THUNDERSTORM)

    /** Çorap kararında "kar/buz" sayılan durumlar. */
    val isSnowy: Boolean
        get() = this in setOf(SNOW, SNOW_SHOWERS)

    companion object {
        fun fromWeatherCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR
            1 -> MAINLY_CLEAR
            2 -> PARTLY_CLOUDY
            3 -> OVERCAST
            45, 48 -> FOG
            51, 53, 55, 56, 57 -> DRIZZLE
            61, 63, 65, 66, 67 -> RAIN
            71, 73, 75, 77 -> SNOW
            80, 81, 82 -> RAIN_SHOWERS
            85, 86 -> SNOW_SHOWERS
            95, 96, 99 -> THUNDERSTORM
            else -> UNKNOWN
        }
    }
}
