package com.example.skyhigh_prototype.Data

import com.google.firebase.Timestamp

data class Achievement(
    val achievementId:String?,
    var name:String?,
    var description:String?,
    var  dateAchieved: Timestamp = Timestamp.now()

)

object Badges{
    const val badgeOne = "https://cdn0.iconfinder.com/data/icons/business-vol-2-16/74/13-1024.png"
    const val badgeTwo = "https://cdn2.iconfinder.com/data/icons/flat-game-ui-buttons-icons-1/512/17-512.png"
    const val badgeThree ="https://img.freepik.com/premium-psd/achievement-badge-3d-icon_525483-381.jpg"
}