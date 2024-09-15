package com.example.skyhigh_prototype.Room

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomWarnings
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [
    BirdObservationEntity::class,
    CountryEntity::class,
    TopBirdEntity::class,
    HotspotEntity::class,
    TaxonomyEntity::class,
    RegionEntity::class
                     ], version = 2, exportSchema = false)
abstract class BirdObservationDatabase : RoomDatabase() {
    abstract fun birdObservationDao(): BirdObservationDao
    abstract fun countryDao(): CountryDao
    abstract fun topBirdsDao(): TopBirdsDao
    abstract fun hotspotDao(): HotspotDao
    abstract fun taxonomyDao(): TaxonomyDao
    abstract fun regionDao(): RegionDao

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Define the schema changes here
            // For example, adding a new table or column
            database.execSQL("ALTER TABLE your_table_name ADD COLUMN new_column_name INTEGER")
        }
    }
}

@Entity(tableName = "bird_observations")
data class BirdObservationEntity(
    @PrimaryKey val speciesCode: String,
    val comName: String,
    val sciName: String,
    val locName: String,
    val obsDt: String
)

@Dao
interface BirdObservationDao {
    @Query("SELECT * FROM bird_observations")
    suspend fun getAllObservations(): List<BirdObservationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservations(observations: List<BirdObservationEntity>)

    @Query("DELETE FROM bird_observations")
    suspend fun clearAllObservations(): Int
}

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val code: String,
    val name: String
)

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries")
    suspend fun getAllCountries(): List<CountryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Query("DELETE FROM countries")
    suspend fun clearAllCountries(): Int
}

@Entity(tableName = "top_birds")
data class TopBirdEntity(
    @PrimaryKey val speciesCode: String,
    val comName: String,
    val sciName: String,
    val obsDt: String
)

@Dao
interface TopBirdsDao {
    @Query("SELECT * FROM top_birds")
    suspend fun getAllTopBirds(): List<TopBirdEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopBirds(birds: List<TopBirdEntity>)

    @Query("DELETE FROM top_birds")
    suspend fun clearAllTopBirds(): Int
}

@Entity(tableName = "hotspots")
data class HotspotEntity(
    @PrimaryKey val locId: String,
    val locName: String,
    val lat: Double,
    val lng: Double
)

@Dao
interface HotspotDao {

    @Query("SELECT * FROM hotspots WHERE locId = :hotspotId LIMIT 1")
    suspend fun getHotspot(hotspotId: String): HotspotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotspot(hotspot: HotspotEntity)

    @Query("DELETE FROM hotspots")
    suspend fun clearAllHotspots(): Int
}

@Entity(tableName = "taxonomy")
data class TaxonomyEntity(
    @PrimaryKey val speciesCode: String,
    val comName: String,
    val sciName: String,
    val familyComName: String
)

@Dao
interface TaxonomyDao {

    @Query("SELECT * FROM taxonomy")
    suspend fun getAllTaxonomy(): List<TaxonomyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxonomy(taxonomy: List<TaxonomyEntity>)

    @Query("DELETE FROM taxonomy")
    suspend fun clearAllTaxonomy(): Int
}

@Entity(tableName = "regions")
data class RegionEntity(
    @PrimaryKey val regionCode: String,
    val regionName: String,
    val regionType: String
)

@Dao
interface RegionDao {

    @Query("SELECT * FROM regions WHERE regionCode = :regionCode LIMIT 1")
    suspend fun getRegion(regionCode: String): RegionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegion(region: RegionEntity)

    @Query("DELETE FROM regions")
    suspend fun clearAllRegions(): Int
}
