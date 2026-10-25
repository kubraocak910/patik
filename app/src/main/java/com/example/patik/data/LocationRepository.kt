package com.example.patik.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper
import com.example.patik.model.City
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/** Cihazın bulunduğu şehre erişimin tek kapısı. */
interface LocationRepository {
    /** İzin yoksa veya konum alınamazsa `null` döner; çağıran taraf varsayılan şehre düşer. */
    suspend fun currentCity(): City?
}

/**
 * Android'in kendi `LocationManager`'ı ile konum alır (ek kütüphane gerekmez).
 *
 * Önce son bilinen konuma bakılır (anında gelir); yoksa tek seferlik yeni konum istenir.
 * Koordinat, `Geocoder` ile "Ankara" gibi bir şehir adına çevrilir.
 */
class AndroidLocationRepository(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocationRepository {

    private val appContext = context.applicationContext
    private val locationManager = appContext.getSystemService(LocationManager::class.java)

    override suspend fun currentCity(): City? {
        if (!hasPermission()) return null
        val location = lastKnownLocation() ?: freshLocation() ?: return null
        val place = withContext(ioDispatcher) { resolvePlace(location) }
        return City(
            id = City.CURRENT_LOCATION_ID,
            name = place?.city ?: "Konumum",
            latitude = location.latitude,
            longitude = location.longitude,
            countryCode = null,
            country = place?.country,
            admin1 = place?.district
        )
    }

    private fun hasPermission(): Boolean = LOCATION_PERMISSIONS.any {
        appContext.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun lastKnownLocation(): Location? = locationManager.getProviders(true)
        .mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
        .maxByOrNull { it.time }

    @SuppressLint("MissingPermission")
    private suspend fun freshLocation(): Location? {
        val provider = preferredProvider() ?: return null
        return withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine<Location?> { continuation ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val signal = CancellationSignal()
                    continuation.invokeOnCancellation { signal.cancel() }
                    locationManager.getCurrentLocation(provider, signal, appContext.mainExecutor) { location ->
                        if (continuation.isActive) continuation.resume(location)
                    }
                } else {
                    val listener = SingleLocationListener { location ->
                        if (continuation.isActive) continuation.resume(location)
                    }
                    continuation.invokeOnCancellation { locationManager.removeUpdates(listener) }
                    @Suppress("DEPRECATION")
                    locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                }
            }
        }
    }

    private fun preferredProvider(): String? {
        val enabled = locationManager.getProviders(true)
        return listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
            .firstOrNull { it in enabled }
            ?: enabled.firstOrNull()
    }

    /** Geocoder internet ister ve her cihazda olmayabilir; hata olursa ad yerine "Konumum" yazılır. */
    @Suppress("DEPRECATION")
    private fun resolvePlace(location: Location): Place? = runCatching {
        if (!Geocoder.isPresent()) return null
        val address = Geocoder(appContext, Locale.forLanguageTag("tr"))
            .getFromLocation(location.latitude, location.longitude, 1)
            ?.firstOrNull()
            ?: return null
        Place(
            city = address.adminArea ?: address.locality,
            district = address.subAdminArea ?: address.locality,
            country = address.countryName
        )
    }.getOrNull()

    private data class Place(val city: String?, val district: String?, val country: String?)

    /** Android 11 öncesi için tek seferlik dinleyici; eski sürümlerde tüm metotlar zorunludur. */
    private class SingleLocationListener(
        private val onLocation: (Location) -> Unit
    ) : LocationListener {
        override fun onLocationChanged(location: Location) = onLocation(location)

        @Deprecated("Android 10 ve öncesi için gerekli")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

        override fun onProviderEnabled(provider: String) = Unit

        override fun onProviderDisabled(provider: String) = Unit
    }

    companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val LOCATION_TIMEOUT_MS = 8_000L
    }
}
