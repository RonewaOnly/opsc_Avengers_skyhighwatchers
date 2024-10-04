package com.example.skyhigh_prototype.Data


import com.google.firebase.Timestamp

// Bird species card details class
data class Birds(
    val bird_name: String = "",
    val bird_species: List<String> = emptyList(),
    val gender: String = "",
    val color: List<String> = emptyList(),
    val location: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    var images: List<String> = emptyList(),
    var videos: List<String> = emptyList(),
    val feed: List<BirdFeed> = emptyList(),
    val bird_description: List<String> = emptyList(),
    val relatedSpecies: List<Birds> = emptyList(),
    val hotspots: List<Location> = emptyList()
)