package com.example.skyhigh_prototype.Model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.skyhigh_prototype.Intent.BirdObservation
import com.example.skyhigh_prototype.Intent.Country
import com.example.skyhigh_prototype.Intent.EBirdApiClient
import com.example.skyhigh_prototype.Intent.Hotspot
import com.example.skyhigh_prototype.Intent.Region
import com.example.skyhigh_prototype.Intent.Taxonomy
import com.example.skyhigh_prototype.Room.BirdObservationDao
import com.example.skyhigh_prototype.Room.BirdObservationDatabase
import com.example.skyhigh_prototype.Room.BirdObservationEntity
import com.example.skyhigh_prototype.Room.CountryDao
import com.example.skyhigh_prototype.Room.HotspotDao
import com.example.skyhigh_prototype.Room.RegionDao
import com.example.skyhigh_prototype.Room.TaxonomyDao
import com.example.skyhigh_prototype.Room.TopBirdsDao
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONArray

class BirdViewModel(application: Application) : AndroidViewModel(application) {

    private val _observations = MutableStateFlow<List<BirdObservation>>(emptyList())
    val observations: StateFlow<List<BirdObservation>> = _observations

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    private val _topBirds = MutableStateFlow<List<BirdObservation>>(emptyList())
    val topBirds: StateFlow<List<BirdObservation>> = _topBirds

//    private val _hotspot = MutableStateFlow<Hotspot?>(null)
//    val hotspot: StateFlow<Hotspot?> = _hotspot
    private val _hotspot = MutableStateFlow<List<Point>>(emptyList())
    val hotspot: StateFlow<List<Point>> = _hotspot.asStateFlow()
    private val _taxonomy = MutableStateFlow<List<Taxonomy>>(emptyList())
    val taxonomy: StateFlow<List<Taxonomy>> = _taxonomy

    private val _region = MutableStateFlow<Region?>(null)
    val region: StateFlow<Region?> = _region

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val client = OkHttpClient()


    private var currentOffset = 0
    private val limit = 20
    private val birdObservationDao: BirdObservationDao
    private val topbirdDao: TopBirdsDao
    private val countryDao: CountryDao
    private val hotspotDao: HotspotDao
    private val taxonomyDao: TaxonomyDao
    private val regionDao: RegionDao



    init {
        // Initialize the Room database
        val db = Room.databaseBuilder(
            application,
            BirdObservationDatabase::class.java,
            "bird_observations_db"
        ).fallbackToDestructiveMigration().build()
        birdObservationDao = db.birdObservationDao()
        countryDao = db.countryDao()
        topbirdDao = db.topBirdsDao()
        hotspotDao = db.hotspotDao()
        taxonomyDao = db.taxonomyDao()
        regionDao = db.regionDao()
    }
    fun getRecentBirdObservations(lat: Double, lng: Double, apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Check local cache first
                val cachedObservations = birdObservationDao.getAllObservations()
                if (cachedObservations.isNotEmpty()) {
                    _observations.value = cachedObservations.map { it.toBirdObservation() }
                }

                // Fetch fresh data from the API
                val response = EBirdApiClient.apiService.getRecentObservations(lat, lng, apiKey)
                _observations.value = response

                // Cache the fresh data in the local database
                birdObservationDao.clearAllObservations()
                birdObservationDao.insertObservations(response.map { it.toBirdObservationEntity() })
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching bird data: ${e.message}"
                e.message?.let { Log.e("Error fetching bird data:", it) }
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun getCountries(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = EBirdApiClient.apiService.getCountryList(apiKey)
                _countries.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching countries: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getTopBirds(regionCode: String, apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = EBirdApiClient.apiService.getTop100Birds(regionCode, apiKey)
                _topBirds.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching top birds: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getHotspotByLocation(latitude: Double, longitude: Double, apiKey: String) {
        _isLoading.value = true
        val url = "https://api.ebird.org/v2/ref/hotspot/geo?lat=$latitude&lng=$longitude"

        val request = Request.Builder()
            .url(url)
            .header("X-eBirdApiToken", apiKey)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                _isLoading.value = false
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { jsonResponse ->
                    val hotspots = parseHotspots(jsonResponse)
                    _hotspot.value = hotspots
                    _isLoading.value = false
                }
            }
        })
    }

    private fun parseHotspots(jsonResponse: String): List<Point> {
        // Parse JSON response to extract hotspots and their coordinates
        val hotspots = mutableListOf<Point>()

        val jsonArray = JSONArray(jsonResponse)
        for (i in 0 until jsonArray.length()) {
            val hotspot = jsonArray.getJSONObject(i)
            val lat = hotspot.getDouble("lat")
            val lng = hotspot.getDouble("lng")
            hotspots.add(Point.fromLngLat(lng, lat))
        }

        return hotspots
    }

    fun getRoute(origin: Point, destination: Point, callback: (List<Point>) -> Unit) {
        viewModelScope.launch {
            // Use Mapbox Directions API or similar service to get the route
            // This is a placeholder implementation
//            val route = mapboxDirectionsClient.getRoute(origin, destination)
//            callback(route.geometry().coordinates())
        }
    }
    fun getTaxonomy(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = EBirdApiClient.apiService.getTaxonomy(apiKey)
                _taxonomy.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching taxonomy: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getRegionInfo(regionCode: String, apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = EBirdApiClient.apiService.getRegionInfo(regionCode, apiKey)
                _region.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching region info: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun loadMore(lat: Double, lng: Double, apiKey: String) {
        if (!_isLoading.value) {
            getRecentBirdObservations(lat, lng, apiKey)
        }
    }

}
// Extension function to map BirdObservation to BirdObservationEntity
fun BirdObservation.toBirdObservationEntity(): BirdObservationEntity {
    return BirdObservationEntity(
        speciesCode = this.speciesCode,
        comName = this.comName,
        sciName = this.sciName,
        locName = this.locName,
        obsDt = this.obsDt
    )
}

// Extension function to map BirdObservationEntity to BirdObservation
fun BirdObservationEntity.toBirdObservation(): BirdObservation {
    return BirdObservation(
        speciesCode = this.speciesCode,
        comName = this.comName,
        sciName = this.sciName,
        locName = this.locName,
        obsDt = this.obsDt
    )
}