package com.example.skyhigh_prototype.Data

import com.example.skyhigh_prototype.R

// Main class that processes the information taken by the user
data class BirdTip(
    var card_id: String? = null,              // Firebase document ID
    val card_name: String = "",               // Default value for card_name
    val card_description: String = "",        // Default value for card_description
    val card_category: String = "",           // Default value for card_category
    val card_cover_img: String? = R.drawable.bird2.toString(),  // Default cover image
    val content: List<Birds> = emptyList()    // Default empty list for content
) {
    // No-argument constructor required by Firebase Firestore
    constructor() : this(null, "", "", "", R.drawable.bird2.toString(), emptyList())
}
