package com.example.skyhigh_prototype.View

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.skyhigh_prototype.Data.UserDetails
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.google.firebase.Timestamp

@Composable
fun Profile(navController: NavController, databaseHandler: DatabaseHandler){
    val context = LocalContext.current


    Text(text = "Profile")
    //Testing data
    val userProfile = UserProfile(
        userId = "user-123456",
        username = "BirdLover99",
        email = "birdlover99@example.com",
        profilePictureUrl = "https://th.bing.com/th/id/OIP.sg1wzZN-FkYl9mFfJ4DQmQHaE8?rs=1&pid=ImgDetMain",
        bio = "Avid bird watcher from the Pacific Northwest, love spotting rare species!",
        location = "Seattle, WA",
        //joinedDate = "March 20, 2023",
        favoriteBirds = listOf("Bald Eagle", "Peregrine Falcon", "Snowy Owl"),
        sightingsCount = 145,
        customAreas = listOf(
            CustomArea(
                areaId = "area-001",
                name = "Greenwood Park",
                locationDescription = "A small park with a dense population of migratory birds",
                sightingsInArea = 52
            ),
            CustomArea(
                areaId = "area-002",
                name = "Lake Union",
                locationDescription = "Urban lake with diverse bird species",
                sightingsInArea = 35
            )
        ),
        achievements = listOf(
            Achievement(
                achievementId = "ach-100",
                name = "100 Sightings",
                description = "Logged 100 bird sightings",
                dateAchieved = "April 15, 2024"
            ),
            Achievement(
                achievementId = "ach-200",
                name = "Rare Bird Spotter",
                description = "Spotted a rare bird species",
                dateAchieved = "June 10, 2024"
            )
        )
    )
    var userView  by remember { mutableStateOf(UserDetails()) }

    databaseHandler.fetchUserDetails(onSuccess = {
        userView = it
    }, onFailure = {
        Toast.makeText(context,"",Toast.LENGTH_SHORT).show()
    })
    UserProfileScreen(
        userProfile = userView,
        onEditCustomArea = {},
       // onDeleteCustomArea = {databaseHandler.deleteCustomArea()},
        onDeleteCustomArea = {
            databaseHandler.deleteCustomArea(
                areaId = "",
                onSuccess = {
                    println("Custom area deleted successfully.")
                },
                onFailure = { error ->
                    println("Failed to delete custom area: ${error.message}")
                }
            )
        },
        onAddCustomArea = { },
        onSettingsClick = {  navController.navigate("settings") },
        onLogoutClick = { navController.navigate("logout")}
    )


}
@Composable
fun UserProfileScreen(
    userProfile: UserDetails,
    onEditCustomArea: (com.example.skyhigh_prototype.Data.CustomArea?) -> Unit,
    onDeleteCustomArea: (com.example.skyhigh_prototype.Data.CustomArea?) -> Unit,
    onAddCustomArea: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    //val scroll = rememberScrollState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top= 120.dp)
    ) {
        item {
            // Profile Header
            ProfileHeader(userProfile)

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Statistics Section
            StatisticsSection(userProfile)

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Custom Areas Section
            CustomAreasSection(
                customAreas = userProfile.customArea,
                onEdit = onEditCustomArea,
                onDelete = onDeleteCustomArea,
                onAdd = onAddCustomArea
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Recent Sightings Section
            RecentSightingsSection(userProfile.sightingsCount)

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Achievements Section
            AchievementsSection(userProfile.achievements)

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Settings & Logout
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onSettingsClick) {
                    Text(text = "Settings")
                }
                TextButton(onClick = onLogoutClick) {
                    Text(text = "Logout", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(userProfile: UserDetails) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = rememberImagePainter(userProfile.profilePic),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = userProfile.firstname?: "Name ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = userProfile.lastname?: "Last Name ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun StatisticsSection(userProfile: UserDetails) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Total Sightings", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        //Text(text = userProfile.sightingsCount.toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            userProfile.favoriteBirds.forEach { bird ->
                Icon(
                    painter = rememberImagePainter(bird),
                    contentDescription = bird,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun CustomAreasSection(
    customAreas: List<com.example.skyhigh_prototype.Data.CustomArea?>,
    onEdit: (com.example.skyhigh_prototype.Data.CustomArea?) -> Unit,
    onDelete: (com.example.skyhigh_prototype.Data.CustomArea?) -> Unit,
    onAdd: () -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Custom Bird-Watching Areas", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        }
        customAreas.forEach { area ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "${area?.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${area?.locationDescription}", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "Sightings: ${area?.sightingsInArea}", fontSize = 14.sp)
                    Row {
                        TextButton(onClick = { onEdit(area) }) {
                            Text(text = "Edit")
                        }
                        TextButton(onClick = { onDelete(area) }) {
                            Text(text = "Delete", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSightingsSection(sightingsCount: Double) {
    Column {
        Text(text = "Recent Sightings", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        // Assuming a list of recent sightings would be displayed here
        // For now, just displaying a placeholder
        if (sightingsCount > 0) {
            // Display the list of sightings
            LazyColumn(modifier = Modifier.height(200.dp)) {
                items(sightingsCount.toInt()) { sighting ->
                    Text(text = sighting.toString()) // Customize with more details
                }
            }
        } else {
            Text(text = "No recent sightings.", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AchievementsSection(achievements: List<com.example.skyhigh_prototype.Data.Achievement?>) {
    Column {
        Text(text = "Achievements", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        LazyVerticalGrid(columns = GridCells.Fixed(3),modifier = Modifier.height(200.dp)) {
            items(achievements) { achievement ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                    Icon(
                        painter = rememberImagePainter("https://cdn0.iconfinder.com/data/icons/business-vol-2-16/74/13-1024.png"),
                        contentDescription = achievement?.name,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = "${achievement?.name}", fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

//Data classes for testing
data class UserProfile(
    val userId: String,
    val username: String,
    val email: String,
    val profilePictureUrl: String?,
    val bio: String,
    val location: String,
    val joinedDate: Timestamp = Timestamp.now(),
    val favoriteBirds: List<String>,
    val sightingsCount: Int,
    val customAreas: List<CustomArea>,
    val achievements: List<Achievement>
)

data class CustomArea(
    val areaId: String,
    val name: String,
    val locationDescription: String,
    val sightingsInArea: Int
)

data class Achievement(
    val achievementId: String,
    val name: String,
    val description: String,
    val dateAchieved: String
)
