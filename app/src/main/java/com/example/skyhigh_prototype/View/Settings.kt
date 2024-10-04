package com.example.skyhigh_prototype.View

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.R
import com.example.skyhigh_prototype.updateAppLocale

@Composable
fun NavOption(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val rememberNav = rememberNavController()

    NavHost(navController = rememberNav, startDestination = "setting") {
        composable("setting") {
            Settings(rememberNav)
        }
        composable("general") {
            navigateToGeneralSettings(rememberNav, isDarkTheme, onThemeChange)
        }
        composable("customArea") {
            navigateToCustomAreas(rememberNav)
        }
        composable("Report") {
            navigateToReport(rememberNav)
        }
        composable("App_info") {
            navigateToAppInfo(rememberNav)
        }
    }
}

@Composable
fun Settings(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp)
    ) {
        OutlinedButton(
            onClick = {
                navController.navigate("general")
            },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.general), textAlign = TextAlign.Start)
        }
        OutlinedButton(
            onClick = {
                navController.navigate("customArea")
            },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.custom_areas))
        }
        OutlinedButton(
            onClick = {
                navController.navigate("Report")
            },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.report))
        }
        OutlinedButton(
            onClick = {
                navController.navigate("App_info")
            },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.app_info))
        }
    }
}

// General Settings Screen
@Composable
fun navigateToGeneralSettings(navController: NavController, isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }
    //variable for metric system change
    var useKilometers by remember {mutableStateOf(true)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
    ) {
        // Notification Settings Section
        Text(text = stringResource(R.string.notification_settings), style = MaterialTheme.typography.titleSmall)
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = {
                notificationsEnabled = it
                if (notificationsEnabled) {
                    createNotificationChannel(context)
                    sendNotification(context, context.getString(R.string.app_name), "Notifications enabled!")
                } else {
                    sendNotification(context, context.getString(R.string.app_name), "Notifications disabled!")
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Theme Settings Section
        Text(text = stringResource(R.string.theme_settings), style = MaterialTheme.typography.titleSmall)
        OutlinedButton(
            onClick = {
                onThemeChange(!isDarkTheme)  // Toggle the theme between Light and Dark
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isDarkTheme) stringResource(R.string.switch_to_light_mode) else stringResource(R.string.switch_to_dark_mode),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Distance unit section : Metric or imperial
        Text(text = "Distance", style = MaterialTheme.typography.titleSmall)
        Switch(
            checked = useKilometers,
            onCheckedChange = { useKilometers = it }
        )
        Text(
            text = if (useKilometers) "Kilometers" else "Miles",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Language Settings Section
        Text(text = stringResource(R.string.language_settings), style = MaterialTheme.typography.titleSmall)
        LanguageSelection(onLanguageSelected = { selectedLanguage ->
            updateAppLocale(context, selectedLanguage)
            sendNotification(context, "Language Changed", "App language changed to $selectedLanguage")
        })
    }
}

// Language Selection Dropdown
@Composable
fun LanguageSelection(onLanguageSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Change Language: $selectedLanguage", textAlign = TextAlign.Center)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                selectedLanguage = "English"
                expanded = false
                onLanguageSelected("en")
                updateAppLocale(context, "en")  // Change app locale to English
            }) {
                Text("English")
            }
            DropdownMenuItem(onClick = {
                selectedLanguage = "Spanish"
                expanded = false
                onLanguageSelected("es")
                updateAppLocale(context, "es")  // Change app locale to Spanish
            }) {
                Text("Spanish")
            }
            DropdownMenuItem(onClick = {
                selectedLanguage = "French"
                expanded = false
                onLanguageSelected("fr")
                updateAppLocale(context, "fr")  // Change app locale to French
            }) {
                Text("French")
            }
        }
    }
}

// Function to create Notification Channel
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "birdwatching_notifications",
            "Birdwatching Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for new bird sightings"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

// Function to send notifications
fun sendNotification(context: Context, title: String, message: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(context, "birdwatching_notifications")
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(R.drawable.ic_notification)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
    notificationManager.notify(1, notification)
}

// Function to navigate to Custom Areas
@Composable
fun navigateToCustomAreas(navController: NavController) {
    var customAreas by remember { mutableStateOf(listOf("Area 1", "Area 2")) }
    var newAreaName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp)
    ) {
        Text(text = stringResource(R.string.define_custom_areas))

        OutlinedTextField(
            value = newAreaName,
            onValueChange = { newAreaName = it },
            label = { Text(stringResource(R.string.new_area_name)) }
        )

        OutlinedButton(onClick = {
            if (newAreaName.isNotBlank()) {
                customAreas = customAreas + newAreaName
                newAreaName = ""
            }
        }) {
            Text(text = stringResource(R.string.add_new_area))
        }

        Text(text = stringResource(R.string.manage_existing_areas))

        LazyColumn {
            items(customAreas) { area ->
                Text(text = area)
                OutlinedButton(onClick = {
                    customAreas = customAreas - area
                }) {
                    Text(text = stringResource(R.string.delete))
                }
            }
        }
    }
}

// Function to navigate to Report
@Composable
fun navigateToReport(navController: NavController) {
    var reports by remember { mutableStateOf(listOf<String>()) }
    var newReport by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp)
    ) {
        Text(text = stringResource(R.string.report_bird_sighting))

        OutlinedTextField(
            value = newReport,
            onValueChange = { newReport = it },
            label = { Text(stringResource(R.string.enter_bird_details)) }
        )

        OutlinedButton(onClick = {
            if (newReport.isNotBlank()) {
                reports = reports + newReport
                newReport = ""
            }
        }) {
            Text(text = stringResource(R.string.new_sighting))
        }

        Text(text = stringResource(R.string.view_past_reports))

        LazyColumn {
            items(reports) { report ->
                Text(text = report)
                OutlinedButton(onClick = {
                    // Code to view report details
                }) {
                    Text(text = stringResource(R.string.view_details))
                }
            }
        }
    }
}

// Function to navigate to App Info
@Composable
fun navigateToAppInfo(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp)
    ) {
        Text(text = "App Version: 1.0.0")
        Text(text = "Developed by: Birdwatching Team")
        Text(text = "License: Open Source")
        Text(text = "Contact: support@birdwatchingapp.com")
    }

}
