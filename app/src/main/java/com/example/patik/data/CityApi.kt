package com.example.patik.data

import com.example.patik.model.CitySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Şehir arama servisi.
 * Örnek: `GET https://geocoding-api.open-meteo.com/v1/search?name=Ankara&count=10&language=tr`
 */
interface CityApi {

    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "tr"
    ): CitySearchResponse
}
