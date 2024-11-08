@file:Suppress("PackageName")

package com.example.skyhigh_prototype.Data

data class UserDetails(
    var firstname: String? = "",
    var username : String?  = "",
    var lastname: String? = "",
    var email: String? ="",
    val bio: String? ="",
    val location: String? = "",
    var settings: List<Settings?> = emptyList(),
    var customArea: List<CustomArea?> = emptyList(),
    var sightingsCount:Double = 0.0,
    var favoriteBirds: List<String> = emptyList(),
    var reports: List<Reports?> = emptyList(),
    var achievements: List<Achievement?> = emptyList(),
    var profilePic: String?= "https://th.bing.com/th/id/R.7861dadc7807c489531e672f28d0ecec?rik=uqbbCnsZ0OUbjw&pid=ImgRaw&r=0"
)


