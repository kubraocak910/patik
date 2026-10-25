package com.example.patik.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.patik.model.Season

// ==========================================
// KIŞ PALETİ (Buz Mavisi, Soğuk Gri, Beyaz)
// ==========================================
val WinterPrimary = Color(0xFF0284C7)       // Buz mavisi
val WinterOnPrimary = Color(0xFFFFFFFF)
val WinterPrimaryContainer = Color(0xFFE0F2FE)
val WinterOnPrimaryContainer = Color(0xFF0369A1)
val WinterSecondary = Color(0xFF64748B)     // Soğuk gri
val WinterSecondaryContainer = Color(0xFFF1F5F9)
val WinterBackground = Color(0xFFF8FAFC)    // Buz beyazı
val WinterSurface = Color(0xFFFFFFFF)
val WinterCardGradient = listOf(Color(0xFFBAE6FD), Color(0xFFE0F2FE), Color(0xFFFFFFFF))

// ==========================================
// İLKBAHAR PALETİ (Yeşil, Soft Pembe)
// ==========================================
val SpringPrimary = Color(0xFF059669)       // Taze yeşil
val SpringOnPrimary = Color(0xFFFFFFFF)
val SpringPrimaryContainer = Color(0xFFD1FAE5)
val SpringOnPrimaryContainer = Color(0xFF065F46)
val SpringSecondary = Color(0xFFDB2777)     // Soft pembe
val SpringSecondaryContainer = Color(0xFFFCE7F3)
val SpringBackground = Color(0xFFF0FDF4)    // Bahar esintisi
val SpringSurface = Color(0xFFFFFFFF)
val SpringCardGradient = listOf(Color(0xFFA7F3D0), Color(0xFFFCE7F3), Color(0xFFFFFFFF))

// ==========================================
// YAZ PALETİ (Sarı, Turuncu, Canlı Yeşil)
// ==========================================
val SummerPrimary = Color(0xFFEA580C)       // Sıcak turuncu
val SummerOnPrimary = Color(0xFFFFFFFF)
val SummerPrimaryContainer = Color(0xFFFFEDD5)
val SummerOnPrimaryContainer = Color(0xFF9A3412)
val SummerSecondary = Color(0xFFCA8A04)     // Canlı sarı
val SummerSecondaryContainer = Color(0xFFFEF08A)
val SummerBackground = Color(0xFFFFFBEB)    // Güneşli gün
val SummerSurface = Color(0xFFFFFFFF)
val SummerCardGradient = listOf(Color(0xFFFED7AA), Color(0xFFFEF08A), Color(0xFFFFFFFF))

// ==========================================
// SONBAHAR PALETİ (Kahverengi, Bordo)
// ==========================================
val AutumnPrimary = Color(0xFF881337)       // Bordo
val AutumnOnPrimary = Color(0xFFFFFFFF)
val AutumnPrimaryContainer = Color(0xFFFFE4E6)
val AutumnOnPrimaryContainer = Color(0xFF4C0519)
val AutumnSecondary = Color(0xFF92400E)     // Sıcak kahve
val AutumnSecondaryContainer = Color(0xFFFEF3C7)
val AutumnBackground = Color(0xFFFAF5EF)    // Sonbahar yaprağı
val AutumnSurface = Color(0xFFFFFFFF)
val AutumnCardGradient = listOf(Color(0xFFFECDD3), Color(0xFFFED7AA), Color(0xFFFFFFFF))

fun getSeasonColorScheme(season: Season): ColorScheme = when (season) {
    Season.WINTER -> lightColorScheme(
        primary = WinterPrimary,
        onPrimary = WinterOnPrimary,
        primaryContainer = WinterPrimaryContainer,
        onPrimaryContainer = WinterOnPrimaryContainer,
        secondary = WinterSecondary,
        secondaryContainer = WinterSecondaryContainer,
        background = WinterBackground,
        surface = WinterSurface,
        onBackground = Color(0xFF0F172A),
        onSurface = Color(0xFF0F172A)
    )
    Season.SPRING -> lightColorScheme(
        primary = SpringPrimary,
        onPrimary = SpringOnPrimary,
        primaryContainer = SpringPrimaryContainer,
        onPrimaryContainer = SpringOnPrimaryContainer,
        secondary = SpringSecondary,
        secondaryContainer = SpringSecondaryContainer,
        background = SpringBackground,
        surface = SpringSurface,
        onBackground = Color(0xFF064E3B),
        onSurface = Color(0xFF064E3B)
    )
    Season.SUMMER -> lightColorScheme(
        primary = SummerPrimary,
        onPrimary = SummerOnPrimary,
        primaryContainer = SummerPrimaryContainer,
        onPrimaryContainer = SummerOnPrimaryContainer,
        secondary = SummerSecondary,
        secondaryContainer = SummerSecondaryContainer,
        background = SummerBackground,
        surface = SummerSurface,
        onBackground = Color(0xFF431407),
        onSurface = Color(0xFF431407)
    )
    Season.AUTUMN -> lightColorScheme(
        primary = AutumnPrimary,
        onPrimary = AutumnOnPrimary,
        primaryContainer = AutumnPrimaryContainer,
        onPrimaryContainer = AutumnOnPrimaryContainer,
        secondary = AutumnSecondary,
        secondaryContainer = AutumnSecondaryContainer,
        background = AutumnBackground,
        surface = AutumnSurface,
        onBackground = Color(0xFF3B0716),
        onSurface = Color(0xFF3B0716)
    )
}

fun getSeasonCardGradient(season: Season): List<Color> = when (season) {
    Season.WINTER -> WinterCardGradient
    Season.SPRING -> SpringCardGradient
    Season.SUMMER -> SummerCardGradient
    Season.AUTUMN -> AutumnCardGradient
}
