package com.example.skyhigh_prototype.Data

import androidx.compose.ui.res.stringResource
import com.example.skyhigh_prototype.R

data class Settings(
    var notification: Boolean = false,
    var distance: Boolean = true,
    var languageRange: String = "en",
    var theme: Int = R.string.switch_to_light_mode
)
