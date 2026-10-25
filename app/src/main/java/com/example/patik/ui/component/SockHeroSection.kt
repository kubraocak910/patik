package com.example.patik.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patik.model.City
import com.example.patik.model.Forecast
import com.example.patik.model.SockRecommendation
import com.example.patik.model.WeatherCondition
import kotlin.math.roundToInt

/**
 * Bir şehrin hava durumu ve çorap önerisini içeren model.
 */
data class CityWeatherItem(
    val city: City,
    val forecast: Forecast,
    val isMainCity: Boolean = false,
    val isCurrentLocation: Boolean = false
) {
    val condition: WeatherCondition
        get() = WeatherCondition.fromWeatherCode(forecast.current.weatherCode)

    val sockAdvice: SockRecommendation
        get() = SockRecommendation.calculate(forecast.current.temperatureC, condition)
}

/**
 * 1 ile 4 arasındaki şehri aynı sabit yükseklikteki (~350dp) alana sığdıran Hero bölümü.
 * - 1 Şehir: Tam boyutlu, detaylı zengin hero kartı.
 * - 2 Şehir: Yan yana 2 kart.
 * - 3 Şehir: Üstte 1 ana kart, altta yan yana 2 kart.
 * - 4 Şehir: 2x2 grid şeklinde dördü de aynı alana sığar.
 */
@Composable
fun SockHeroSection(
    items: List<CityWeatherItem>,
    selectedCityId: Long?,
    onCitySelected: (City) -> Unit,
    onRemoveExtraCity: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .animateContentSize()
    ) {
        when (items.size) {
            0 -> Unit
            1 -> {
                val item = items.first()
                HeroSingleCard(
                    item = item,
                    isSelected = true,
                    onClick = { onCitySelected(item.city) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            2 -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items.forEach { item ->
                        HeroCompactCard(
                            item = item,
                            isSelected = item.city.id == selectedCityId,
                            onClick = { onCitySelected(item.city) },
                            onRemove = if (!item.isMainCity && !item.isCurrentLocation) {
                                { onRemoveExtraCity(item.city.id) }
                            } else null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
            3 -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val first = items[0]
                    HeroCompactCard(
                        item = first,
                        isSelected = first.city.id == selectedCityId,
                        onClick = { onCitySelected(first.city) },
                        onRemove = if (!first.isMainCity && !first.isCurrentLocation) {
                            { onRemoveExtraCity(first.city.id) }
                        } else null,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 1..2) {
                            val subItem = items[i]
                            HeroCompactCard(
                                item = subItem,
                                isSelected = subItem.city.id == selectedCityId,
                                onClick = { onCitySelected(subItem.city) },
                                onRemove = { onRemoveExtraCity(subItem.city.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
            else -> {
                // 4 Şehir: 2x2 Grid, tam olarak 1 tanesinin sığdığı 350dp'ye 4'ü de sığar!
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeroCompactCard(
                            item = items[0],
                            isSelected = items[0].city.id == selectedCityId,
                            onClick = { onCitySelected(items[0].city) },
                            onRemove = if (!items[0].isMainCity && !items[0].isCurrentLocation) {
                                { onRemoveExtraCity(items[0].city.id) }
                            } else null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        HeroCompactCard(
                            item = items[1],
                            isSelected = items[1].city.id == selectedCityId,
                            onClick = { onCitySelected(items[1].city) },
                            onRemove = { onRemoveExtraCity(items[1].city.id) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeroCompactCard(
                            item = items[2],
                            isSelected = items[2].city.id == selectedCityId,
                            onClick = { onCitySelected(items[2].city) },
                            onRemove = { onRemoveExtraCity(items[2].city.id) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        HeroCompactCard(
                            item = items[3],
                            isSelected = items[3].city.id == selectedCityId,
                            onClick = { onCitySelected(items[3].city) },
                            onRemove = { onRemoveExtraCity(items[3].city.id) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tek şehir varken gösterilen tam boy zengin kart.
 */
@Composable
private fun HeroSingleCard(
    item: CityWeatherItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val advice = item.sockAdvice
    val drawableRes = advice.sockType.toDrawableRes()

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = advice.sockType.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Okunabilirlik için modern degrade karartma
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.20f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // İçerik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Üst Kısım: Şehir Adı ve Rozetler
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (item.isMainCity) Icons.Default.Star else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (item.isMainCity) Color(0xFFFACC15) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.city.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            if (item.isMainCity) {
                                Text(
                                    text = " (Ana)",
                                    color = Color(0xFFFDE047),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Derece & Durum Rozeti
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.condition.emoji,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${item.forecast.current.temperatureC.roundToInt()}°C",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Alt Kısım: Çorap Havası Başlığı ve Tavsiyesi
                Column {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = advice.sockType.headline,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = advice.tipMessage,
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Küçük Detaylar: Hissedilen, Nem, Rüzgar
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.forecast.current.apparentTemperatureC?.let { feelsLike ->
                            DetailChip(label = "Hissedilen", value = "${feelsLike.roundToInt()}°")
                        }
                        item.forecast.current.humidityPercent?.let { hum ->
                            DetailChip(label = "Nem", value = "%$hum")
                        }
                        item.forecast.current.windSpeedKmh?.let { wind ->
                            DetailChip(label = "Rüzgar", value = "${wind.roundToInt()} km/s")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2, 3 veya 4 şehir olduğunda o alana sığan kompakt kart.
 */
@Composable
private fun HeroCompactCard(
    item: CityWeatherItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onRemove: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val advice = item.sockAdvice
    val drawableRes = advice.sockType.toDrawableRes()

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = advice.sockType.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Karartma degradesi
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // İçerik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Üst Satır: Şehir Adı + Derece + Silme butonu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = item.city.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.forecast.current.temperatureC.roundToInt()}°",
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }

                    if (onRemove != null) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable(onClick = onRemove),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kaldır",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Alt Satır: Çorap Havası Başlığı
                Column {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = advice.sockType.headline,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.condition.label,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailChip(label: String, value: String) {
    Surface(
        color = Color.Black.copy(alpha = 0.45f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
