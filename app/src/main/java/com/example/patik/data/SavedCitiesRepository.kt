package com.example.patik.data

import android.content.Context
import com.example.patik.model.City
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Kullanıcının seçtiği şehirler.
 *
 * @param mainCity Ana şehir. Seçiliyse konumdan bağımsız olarak hep bu şehir gösterilir.
 * @param extraCities Karşılaştırma için eklenen şehirler (en fazla [MAX_EXTRA_CITIES]).
 */
data class SavedCities(
    val mainCity: City? = null,
    val extraCities: List<City> = emptyList()
) {
    val canAddMore: Boolean
        get() = extraCities.size < MAX_EXTRA_CITIES

    companion object {
        const val MAX_EXTRA_CITIES = 3
    }
}

enum class AddCityResult { ADDED, ALREADY_EXISTS, LIMIT_REACHED }

/** Kayıtlı şehirlerin tek kapısı. Uygulama kapanıp açılınca da seçimler korunur. */
interface SavedCitiesRepository {
    val savedCities: StateFlow<SavedCities>
    fun setMainCity(city: City?)
    fun addExtraCity(city: City): AddCityResult
    fun removeExtraCity(cityId: Long)
}

/**
 * Seçimleri `SharedPreferences` içinde tek bir JSON metni olarak saklar.
 * Veri küçük olduğu için Room gibi bir veritabanına gerek yoktur.
 */
class PreferencesSavedCitiesRepository(
    context: Context,
    private val gson: Gson = Gson()
) : SavedCitiesRepository {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _savedCities = MutableStateFlow(read())
    override val savedCities: StateFlow<SavedCities> = _savedCities.asStateFlow()

    override fun setMainCity(city: City?) {
        val current = _savedCities.value
        // Ana şehir yapılan şehir, eklenenler listesinde tekrar görünmesin.
        write(current.copy(mainCity = city, extraCities = current.extraCities.filterNot { it.id == city?.id }))
    }

    override fun addExtraCity(city: City): AddCityResult {
        val current = _savedCities.value
        return when {
            current.mainCity?.id == city.id || current.extraCities.any { it.id == city.id } ->
                AddCityResult.ALREADY_EXISTS
            !current.canAddMore -> AddCityResult.LIMIT_REACHED
            else -> {
                write(current.copy(extraCities = current.extraCities + city))
                AddCityResult.ADDED
            }
        }
    }

    override fun removeExtraCity(cityId: Long) {
        val current = _savedCities.value
        write(current.copy(extraCities = current.extraCities.filterNot { it.id == cityId }))
    }

    private fun read(): SavedCities = runCatching {
        prefs.getString(KEY_SAVED_CITIES, null)?.let { gson.fromJson(it, SavedCities::class.java) }
    }.getOrNull() ?: SavedCities()

    private fun write(saved: SavedCities) {
        prefs.edit().putString(KEY_SAVED_CITIES, gson.toJson(saved)).apply()
        _savedCities.value = saved
    }

    private companion object {
        const val PREFS_NAME = "patik_prefs"
        const val KEY_SAVED_CITIES = "saved_cities"
    }
}
