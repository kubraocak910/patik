package com.example.patik.data

import com.example.patik.model.City
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Şehir aramanın tek kapısı. ViewModel verinin nereden geldiğini bilmez.
 * Arayüz olarak tanımlıdır ki testlerde sahte bir uygulama verilebilsin.
 */
interface CityRepository {
    /** İsme göre şehir arar. Sonuç yoksa boş liste döner. */
    suspend fun search(query: String): List<City>
}

class OpenMeteoCityRepository(
    private val api: CityApi = ApiClient.cityApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CityRepository {

    override suspend fun search(query: String): List<City> = withContext(ioDispatcher) {
        api.search(query.trim()).results.orEmpty()
    }
}
