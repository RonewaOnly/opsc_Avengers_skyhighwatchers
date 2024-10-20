package com.example.skyhigh_prototype.Data

import java.util.Locale.LanguageRange

data class Settings(
    val notification: Boolean,
    val distance: String,
    val languageRange: LanguageRange,
    val theme: String
)
