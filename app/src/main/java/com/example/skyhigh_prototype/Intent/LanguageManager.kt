package com.example.skyhigh_prototype.Intent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class LanguageManager {
    var currentLanguage by mutableStateOf("en")  // Default language (English)
}

// Provide LanguageManager to the whole app
@Composable
fun rememberLanguageManager(): LanguageManager {
    return remember { LanguageManager() }
}