package com.example.skyhigh_prototype.Data

import com.google.firebase.Timestamp


data class Reports(
    val reportId: String="",
    var reportName: String="",
    var reportDescription: String="",
    val reportDate: Timestamp = Timestamp.now(),
)
