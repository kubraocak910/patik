package com.example.patik.model

import com.google.gson.annotations.SerializedName

/**
 * Open-Meteo geocoding servisinin şehir arama yanıtı.
 *
 * Sonuç bulunamazsa servis `results` alanını hiç göndermez; bu yüzden nullable'dır.
 */
data class CitySearchResponse(
    val results: List<City>?
)

/**
 * Bir şehir. Hem ağ yanıtının modeli hem de ekranların kullandığı modeldir.
 *
 * @param id Geocoding servisinin şehir kimliği. Konumdan gelen şehir için [CURRENT_LOCATION_ID].
 * @param latitude Enlem; tahmin isteği bu koordinatla yapılır.
 * @param longitude Boylam.
 */
data class City(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("country_code")
    val countryCode: String? = null,
    val country: String? = null,
    val admin1: String? = null
) {
    /** Kartlarda gösterilen alt yazı: "Ankara, Türkiye". */
    val subtitle: String
        get() = listOfNotNull(admin1, country).distinct().joinToString(", ")

    companion object {
        /** Cihaz konumundan üretilen şehrin sabit kimliği; aramadan gelen kimliklerle çakışmaz. */
        const val CURRENT_LOCATION_ID = -1L

        /** Konum izni yoksa veya konum alınamazsa gösterilen şehir. */
        val DEFAULT = City(
            id = 323786,
            name = "Ankara",
            latitude = 39.91987,
            longitude = 32.85427,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Ankara"
        )
    }
}
