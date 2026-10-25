package com.example.patik.data

import com.example.patik.model.CurrentWeather
import com.example.patik.model.DailyForecast
import com.example.patik.model.Forecast
import com.example.patik.model.HourlyForecast
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * `/v1/forecast` yanıtının ham hali. Servis veriyi **sütun** biçiminde gönderir:
 * `hourly.time[i]` ile `hourly.temperature_2m[i]` aynı saate aittir.
 * [toForecast] bu sütunları satırlara çevirir.
 */
data class ForecastResponse(
    val current: CurrentResponse? = null,
    val hourly: HourlyResponse? = null,
    val daily: DailyResponse? = null
)

data class CurrentResponse(
    val time: String,
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("apparent_temperature") val apparentTemperature: Double? = null,
    @SerializedName("relative_humidity_2m") val relativeHumidity: Int? = null,
    @SerializedName("wind_speed_10m") val windSpeed: Double? = null
)

data class HourlyResponse(
    val time: List<String>? = null,
    @SerializedName("temperature_2m") val temperature: List<Double?>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int?>? = null,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int?>? = null
)

data class DailyResponse(
    val time: List<String>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int?>? = null,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double?>? = null,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double?>? = null,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int?>? = null
)

/** `current` bloğu gelmemişse yanıt bozuktur; ViewModel bunu "Error" durumuna çevirir. */
fun ForecastResponse.toForecast(): Forecast {
    val currentResponse = current
        ?: throw IllegalStateException("Forecast response has no current block")
    return Forecast(
        current = CurrentWeather(
            temperatureC = currentResponse.temperature,
            weatherCode = currentResponse.weatherCode,
            observedAt = LocalDateTime.parse(currentResponse.time),
            apparentTemperatureC = currentResponse.apparentTemperature,
            humidityPercent = currentResponse.relativeHumidity,
            windSpeedKmh = currentResponse.windSpeed
        ),
        hourly = hourly.toHourlyForecasts(),
        daily = daily.toDailyForecasts()
    )
}

/** Zorunlu alanı eksik satır atlanır; tek bozuk kayıt bütün ekranı düşürmez. */
private fun HourlyResponse?.toHourlyForecasts(): List<HourlyForecast> {
    val times = this?.time ?: return emptyList()
    return times.indices.mapNotNull { index ->
        val temperature = temperature?.getOrNull(index) ?: return@mapNotNull null
        val code = weatherCode?.getOrNull(index) ?: return@mapNotNull null
        HourlyForecast(
            time = LocalDateTime.parse(times[index]),
            temperatureC = temperature,
            weatherCode = code,
            precipitationProbability = precipitationProbability?.getOrNull(index)
        )
    }
}

private fun DailyResponse?.toDailyForecasts(): List<DailyForecast> {
    val times = this?.time ?: return emptyList()
    return times.indices.mapNotNull { index ->
        val max = temperatureMax?.getOrNull(index) ?: return@mapNotNull null
        val min = temperatureMin?.getOrNull(index) ?: return@mapNotNull null
        val code = weatherCode?.getOrNull(index) ?: return@mapNotNull null
        DailyForecast(
            date = LocalDate.parse(times[index]),
            minTemperatureC = min,
            maxTemperatureC = max,
            weatherCode = code,
            precipitationProbability = precipitationProbabilityMax?.getOrNull(index)
        )
    }
}
