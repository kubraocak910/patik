package com.example.patik.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.patik.data.AndroidLocationRepository
import com.example.patik.data.CityRepository
import com.example.patik.data.LocationRepository
import com.example.patik.data.OpenMeteoCityRepository
import com.example.patik.data.OpenMeteoWeatherRepository
import com.example.patik.data.PreferencesSavedCitiesRepository
import com.example.patik.data.SavedCities
import com.example.patik.data.SavedCitiesRepository
import com.example.patik.data.WeatherRepository
import com.example.patik.model.City
import com.example.patik.model.Forecast
import com.example.patik.model.Season
import com.example.patik.ui.LoadState
import com.example.patik.ui.component.CityWeatherItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val weatherRepo: WeatherRepository = OpenMeteoWeatherRepository()
    private val cityRepo: CityRepository = OpenMeteoCityRepository()
    private val locationRepo: LocationRepository = AndroidLocationRepository(application)
    private val savedCitiesRepo: SavedCitiesRepository = PreferencesSavedCitiesRepository(application)

    // Tema tercihlerini kalıcı saklayan hafıza
    private val prefs = application.getSharedPreferences("patik_theme_prefs", Context.MODE_PRIVATE)

    private val defaultAnkara = City.DEFAULT

    // Kayıtlı tema ayarlarını oku:
    private val savedIsAuto = prefs.getBoolean("is_auto_season", true)
    private val savedSeasonName = prefs.getString("manual_season", null)

    private val _isAutoSeason = MutableStateFlow(savedIsAuto)
    val isAutoSeason: StateFlow<Boolean> = _isAutoSeason.asStateFlow()

    private val _season = MutableStateFlow(
        if (!savedIsAuto && savedSeasonName != null) {
            try { Season.valueOf(savedSeasonName) } catch (_: Exception) { Season.current() }
        } else {
            Season.current()
        }
    )
    val season: StateFlow<Season> = _season.asStateFlow()

    // Arama Durumu
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<City>>(emptyList())
    val searchResults: StateFlow<List<City>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    // Kayıtlı Şehirler
    val savedCities: StateFlow<SavedCities> = savedCitiesRepo.savedCities

    // Ana Şehir Hava Durumu
    private val _mainWeather = MutableStateFlow<LoadState<CityWeatherItem>>(LoadState.Idle)
    val mainWeather: StateFlow<LoadState<CityWeatherItem>> = _mainWeather.asStateFlow()

    // Ekstra Şehirlerin Hava Durumu
    private val _extraWeathers = MutableStateFlow<List<CityWeatherItem>>(emptyList())
    val extraWeathers: StateFlow<List<CityWeatherItem>> = _extraWeathers.asStateFlow()

    // Seçili Şehir
    private val _selectedCity = MutableStateFlow<City?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()

    private val _selectedCityForecast = MutableStateFlow<Forecast?>(null)
    val selectedCityForecast: StateFlow<Forecast?> = _selectedCityForecast.asStateFlow()

    /** Manuel mevsim seçildiğinde hafızaya yazılır (Asla sıfırlanmaz) */
    fun setManualSeason(newSeason: Season) {
        _isAutoSeason.value = false
        _season.value = newSeason
        prefs.edit()
            .putBoolean("is_auto_season", false)
            .putString("manual_season", newSeason.name)
            .apply()
    }

    /** Otomatik moda dönüldüğünde hafızaya otomatik kaydedilir */
    fun setAutoSeason() {
        _isAutoSeason.value = true
        prefs.edit()
            .putBoolean("is_auto_season", true)
            .apply()
        _selectedCity.value?.let { city ->
            _season.value = Season.forCity(city.latitude)
        }
    }

    fun start(hasLocationPermission: Boolean) {
        viewModelScope.launch {
            val saved = savedCities.value
            val targetCity: City = if (saved.mainCity != null) {
                saved.mainCity
            } else if (hasLocationPermission) {
                locationRepo.currentCity() ?: defaultAnkara
            } else {
                defaultAnkara
            }

            loadMainCityWeather(targetCity, isMain = saved.mainCity != null)
            loadExtraCitiesWeather(saved.extraCities)
        }
    }

    private suspend fun loadMainCityWeather(city: City, isMain: Boolean) {
        _mainWeather.value = LoadState.Loading
        try {
            val forecast = weatherRepo.getForecast(city.latitude, city.longitude)
            val item = CityWeatherItem(
                city = city,
                forecast = forecast,
                isMainCity = isMain,
                isCurrentLocation = !isMain && city.id == City.CURRENT_LOCATION_ID
            )
            _mainWeather.value = LoadState.Success(item)

            if (_selectedCity.value == null || _selectedCity.value?.id == city.id) {
                _selectedCity.value = city
                _selectedCityForecast.value = forecast

                // Sadece kullanıcı Otomatik moddaysa şehrin mevsimini uygula
                if (_isAutoSeason.value) {
                    _season.value = Season.forCity(city.latitude)
                }
            }
        } catch (e: Exception) {
            _mainWeather.value = LoadState.Error(e.localizedMessage ?: "Hava durumu alınamadı")
        }
    }

    private suspend fun loadExtraCitiesWeather(cities: List<City>) {
        if (cities.isEmpty()) {
            _extraWeathers.value = emptyList()
            return
        }

        val deferreds = cities.map { city ->
            viewModelScope.async {
                try {
                    val forecast = weatherRepo.getForecast(city.latitude, city.longitude)
                    CityWeatherItem(city = city, forecast = forecast)
                } catch (_: Exception) {
                    null
                }
            }
        }
        val items = deferreds.awaitAll().filterNotNull()
        _extraWeathers.value = items
    }

    fun onCityCardClicked(city: City) {
        _selectedCity.value = city

        // Kullanıcı Otomatik moddaysa seçtiği şehrin mevsim rengine bürünür
        if (_isAutoSeason.value) {
            _season.value = Season.forCity(city.latitude)
        }

        val main = (_mainWeather.value as? LoadState.Success)?.data
        if (main != null && main.city.id == city.id) {
            _selectedCityForecast.value = main.forecast
            return
        }
        val extra = _extraWeathers.value.find { it.city.id == city.id }
        if (extra != null) {
            _selectedCityForecast.value = extra.forecast
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            delay(350)
            _isSearching.value = true
            try {
                val results = cityRepo.search(query)
                _searchResults.value = results
            } catch (_: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun setAsMainCity(city: City) {
        savedCitiesRepo.setMainCity(city)
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        viewModelScope.launch {
            loadMainCityWeather(city, isMain = true)
        }
    }

    fun addAsExtraCity(city: City) {
        savedCitiesRepo.addExtraCity(city)
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        viewModelScope.launch {
            loadExtraCitiesWeather(savedCities.value.extraCities)
        }
    }

    fun removeExtraCity(cityId: Long) {
        savedCitiesRepo.removeExtraCity(cityId)
        val currentSelected = _selectedCity.value
        if (currentSelected?.id == cityId) {
            val main = (_mainWeather.value as? LoadState.Success)?.data
            if (main != null) {
                _selectedCity.value = main.city
                _selectedCityForecast.value = main.forecast
                if (_isAutoSeason.value) {
                    _season.value = Season.forCity(main.city.latitude)
                }
            }
        }
        viewModelScope.launch {
            loadExtraCitiesWeather(savedCities.value.extraCities)
        }
    }

    suspend fun getCityForecast(city: City): Forecast {
        return weatherRepo.getForecast(city.latitude, city.longitude)
    }
}
