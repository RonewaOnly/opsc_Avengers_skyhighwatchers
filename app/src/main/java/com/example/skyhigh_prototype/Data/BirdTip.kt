package com.example.skyhigh_prototype.Data

import com.example.skyhigh_prototype.R
import com.example.skyhigh_prototype.View.Birds

// Main class that processes the information taken by the user
data class BirdTip(
    val card_id: String,
    val card_name: String,
    val card_description: String,
    val card_category: String,
    val card_cover_img: String? = R.drawable.bird2.toString(),
    val content: List<Birds> = emptyList()
)