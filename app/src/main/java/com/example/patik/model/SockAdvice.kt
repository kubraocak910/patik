package com.example.patik.model

/**
 * Patik'in çorap/ayakkabı kataloğu.
 *
 * @param headline Görselin üstüne yazılan kısa başlık: "Bot & Kalın Çorap Havası".
 * @param title Önerinin tam adı.
 */
enum class SockType(
    val headline: String,
    val title: String,
    val emoji: String
) {
    BABET_INCE("Babet Çorap Havası", "Babet & ince çorap", "🧦✨"),
    BABET_SPOR("Kısa Çorap Havası", "Babet / kısa spor çorap", "🧦👟"),
    YAGMUR_INCE("Su Geçirmez Ayakkabı Havası", "İnce çorap + su geçirmez ayakkabı", "👟💧"),
    YAGMUR_CIZME("Çizme & Kalın Çorap Havası", "Kalın çorap + yağmur çizmesi", "👢🌧️"),
    KLASIK_PAMUKLU("Pamuklu Çorap Havası", "Klasik pamuklu çorap", "🧦🍂"),
    KALIN_BOGAZLI("Bot & Kalın Çorap Havası", "Kalın boğazlı çorap + bot", "🥾🧣"),
    YUN_PATIK_BOT("Yün Patik & Kışlık Bot Havası", "Yün patik / termal çorap + kışlık bot", "🧦❄️")
}

/** Bir hava durumu için verilen öneri: çorap tipi + kullanıcıya gösterilen not. */
data class SockRecommendation(
    val sockType: SockType,
    val tipMessage: String
) {
    companion object {
        private const val FREEZING_C = 5.0
        private const val COLD_C = 11.0
        private const val WARM_C = 19.0
        private const val HOT_C = 25.0

        /**
         * Önce hava koşulu (kar, yağmur), sonra sıcaklık değerlendirilir.
         * 19°C üstü kuru havada babet/ince çorap önerilir; çıplak ayak önerilmez.
         */
        fun calculate(temperatureC: Double, condition: WeatherCondition): SockRecommendation = when {
            condition.isSnowy || temperatureC < FREEZING_C -> SockRecommendation(
                SockType.YUN_PATIK_BOT,
                "Hava buz gibi! Yün patik ya da termal çorabı kışlık botla giy."
            )
            condition.isWet && temperatureC >= WARM_C -> SockRecommendation(
                SockType.YAGMUR_INCE,
                "Hava ılık ama yerler ıslak. Babet su çeker; su geçirmez ayakkabı + ince çorap seç."
            )
            condition.isWet -> SockRecommendation(
                SockType.YAGMUR_CIZME,
                "Hem serin hem yağışlı. Çizme ve kalın çorapla ayaklarını kuru tut."
            )
            temperatureC > HOT_C -> SockRecommendation(
                SockType.BABET_INCE,
                "Güneş tepede! Nefes alan babet ya da en ince çorabını giy."
            )
            temperatureC >= WARM_C -> SockRecommendation(
                SockType.BABET_SPOR,
                "Tam kıvamında bir hava. Spor ayakkabının içine babet ya da kısa çorap yeter."
            )
            temperatureC >= COLD_C -> SockRecommendation(
                SockType.KLASIK_PAMUKLU,
                "Hafif serin. Klasik pamuklu çorap tam bu hava için."
            )
            else -> SockRecommendation(
                SockType.KALIN_BOGAZLI,
                "Ayaz var. Boğazlı kalın çorabı bota giy, bilekleri açık bırakma."
            )
        }
    }
}
