package com.example.patik.data

import com.example.patik.model.Forecast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Hava tahmininin tek kapısı. Ekranlar Retrofit'i veya JSON'u bilmez. */
interface WeatherRepository {
    suspend fun getForecast(latitude: Double, longitude: Double): Forecast
}

class OpenMeteoWeatherRepository(
    private val api: ForecastApi = ApiClient.forecastApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherRepository {

    override suspend fun getForecast(latitude: Double, longitude: Double): Forecast =
        withContext(ioDispatcher) {
            api.getForecast(latitude = latitude, longitude = longitude).toForecast()
        }
}
