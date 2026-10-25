package com.example.patik.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * İnternete giden isteklerin tek kapısı (singleton).
 * `by lazy`: Retrofit ilk istek atıldığında kurulur. Open-Meteo API anahtarı istemez.
 */
object ApiClient {

    private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
    private const val FORECAST_BASE_URL = "https://api.open-meteo.com/"

    val cityApi: CityApi by lazy { createApi(GEOCODING_BASE_URL) }

    val forecastApi: ForecastApi by lazy { createApi(FORECAST_BASE_URL) }

    private inline fun <reified T> createApi(baseUrl: String): T = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(T::class.java)
}
