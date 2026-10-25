package com.example.patik.model

import java.time.LocalDate

enum class Season(val label: String, val emoji: String) {
    WINTER("Kış", "❄️"),
    SPRING("İlkbahar", "🌸"),
    SUMMER("Yaz", "☀️"),
    AUTUMN("Sonbahar", "🍂");

    fun next(): Season = entries[(ordinal + 1) % entries.size]

    companion object {
        fun fromMonth(month: Int): Season = when (month) {
            12, 1, 2 -> WINTER
            3, 4, 5 -> SPRING
            6, 7, 8 -> SUMMER
            else -> AUTUMN
        }

        fun current(): Season = fromMonth(LocalDate.now().monthValue)

        /**
         * Şehrin enlemine (Kuzey/Güney yarımküre) ve aya göre o şehirde hangi mevsim olduğunu bulur.
         */
        fun forCity(latitude: Double, month: Int = LocalDate.now().monthValue): Season {
            val isNorthernHemisphere = latitude >= 0.0
            return when (month) {
                12, 1, 2 -> if (isNorthernHemisphere) WINTER else SUMMER
                3, 4, 5 -> if (isNorthernHemisphere) SPRING else AUTUMN
                6, 7, 8 -> if (isNorthernHemisphere) SUMMER else WINTER
                else -> if (isNorthernHemisphere) AUTUMN else SPRING
            }
        }
    }
}
