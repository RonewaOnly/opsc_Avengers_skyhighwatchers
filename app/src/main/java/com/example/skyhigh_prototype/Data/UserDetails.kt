package com.example.skyhigh_prototype.Data

data class UserDetails(
    var firstname: String,
    var lastname: String,
    var email: String,
    var password: String,
    val bio: String? ="",
    val location: String? = "",
    var settings: List<Settings?> = emptyList(),
    var customArea: List<CustomArea?> = emptyList(),
    val favoriteBirds: List<String> = emptyList(),
    var reports: List<Reports?> = emptyList(),
    var achievements: List<Achievement?> = emptyList(),
    var profilePic: String?= "https://th.bing.com/th/id/R.7861dadc7807c489531e672f28d0ecec?rik=uqbbCnsZ0OUbjw&pid=ImgRaw&r=0"
)


