package com.example.skyhigh_prototype.Data

import com.google.firebase.Timestamp


data class Reports(
    val reportId: String?="",
    val reportName: String?="",
    val reportDescription: String?="",
    val reportDate: Timestamp = Timestamp.now(),
)
