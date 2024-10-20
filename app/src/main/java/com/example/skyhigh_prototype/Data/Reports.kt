package com.example.skyhigh_prototype.Data

import com.google.firebase.Timestamp


data class Reports(
    val report_id: String,
    val report_name: String,
    val report_Descrition: String,
    val report_date: Timestamp = Timestamp.now(),
)
