package com.example.skyhigh_prototype.Data

// Data class for retrieving feed data
data class BirdFeed(
    val feed_id: Int = 0,
    val feed_name: String,
    val feed_grown: List<String> = emptyList(),
    val description: List<String>,
    val species_specific: List<String>,
    val feed_images: List<String> = emptyList(),
    val location: List<Location> = emptyList(),
)
