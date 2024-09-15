package com.example.skyhigh_prototype.Intent

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Define the eBird API base URL
private const val BASE_URL = "https://api.ebird.org/v2/"

// Retrofit service interface for eBird API
interface EBirdApiService {

    // Recent observations (already implemented)
    @GET("data/obs/geo/recent")
    suspend fun getRecentObservations(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("key") apiKey: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<BirdObservation>

    // Top 100 birds by region
    @GET("product/top100")
    suspend fun getTop100Birds(
        @Query("regionCode") regionCode: String,
        @Query("key") apiKey: String
    ): List<BirdObservation>

    // Country list
    @GET("ref/geo/country/list")
    suspend fun getCountryList(
        @Query("key") apiKey: String
    ): List<Country>

    // Hotspot details by hotspot ID
    @GET("ref/hotspot/{hotspotId}")
    suspend fun getHotspotDetails(
        @Path("hotspotId") hotspotId: String,
        @Query("key") apiKey: String
    ): Hotspot

    // Taxonomy data for bird species
    @GET("ref/taxonomy/ebird")
    suspend fun getTaxonomy(
        @Query("key") apiKey: String
    ): List<Taxonomy>

    // Region info by region code
    @GET("ref/region/{regionCode}/info")
    suspend fun getRegionInfo(
        @Path("regionCode") regionCode: String,
        @Query("key") apiKey: String
    ): Region
}


// Bird Observation (used for various endpoints)
data class BirdObservation(
    val speciesCode: String,
    val comName: String,
    val sciName: String,
    val locName: String,
    val obsDt: String
)

// Top 100 birds for a region
data class TopBirds(
    val speciesCode: String,
    val comName: String,
    val sciName: String,
    val obsDt: String
)

// Country for ref/geo/country/list
data class Country(
    val code: String,
    val name: String
)

// Hotspot details
data class Hotspot(
    val locId: String,
    val locName: String,
    val lat: Double,
    val lng: Double
)

// Taxonomy list for bird species
data class Taxonomy(
    val speciesCode: String,
    val comName: String,
    val sciName: String,
    val familyComName: String
)

// Region info
data class Region(
    val regionCode: String,
    val regionName: String,
    val regionType: String
)

object EBirdApiClient {

    // Create OkHttp client with logging interceptor
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Build Retrofit instance
    val apiService: EBirdApiService by lazy {
        val gson = GsonBuilder().create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(EBirdApiService::class.java)
    }
}
