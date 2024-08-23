package com.example.skyhigh_prototype.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun navOption(){
    val rememberNav = rememberNavController()

    NavHost(navController = rememberNav, startDestination = "setting") {
        composable("setting"){
            Settings(rememberNav)
        }
        composable("general"){
            navigateToGeneralSettings(rememberNav)
        }
        composable("customArea"){
            navigateToCustomAreas(rememberNav)
        }
        composable("Report"){
            navigateToReport(rememberNav)
        }
        composable("App_info"){
            navigateToAppInfo(rememberNav)
        }

    }
}
@Composable
fun Settings(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp)
    ) {
        OutlinedButton(onClick = {
            navController.navigate("general")
        },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()
            ) {
            Text(text = "General", textAlign = TextAlign.Start)
        }
        OutlinedButton(onClick = {
            navController.navigate("customArea")
        },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Custom Areas")
        }
        OutlinedButton(onClick = {
            navController.navigate("Report")
        },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()

        ) {
            Text(text = "Report")
        }
        OutlinedButton(onClick = {
            navController.navigate("App_info")
        },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "App info")
        }
    }
}

// Function to navigate to General Settings
@Composable
fun navigateToGeneralSettings(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp)
    ) {
        Text(text = "Notification Settings")
        Switch(checked = true, onCheckedChange = { /* Handle toggle */ })
        Text(text = "Theme Settings")
        // Add options for Light/Dark mode
        OutlinedButton(onClick = { /* Handle theme change */ }
            ,
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Change Theme")
        }
        Text(text = "Language Settings")
        // Add language dropdown or selection options
        OutlinedButton(onClick = { /* Handle language change */ },
            shape = RectangleShape,
            border = BorderStroke(2.dp, Color.Blue),
            modifier = Modifier.fillMaxWidth()) {
            Text(text = "Change Language")
        }
    }
}

// Function to navigate to Custom Areas
@Composable
fun navigateToCustomAreas(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp)
    ) {
        Text(text = "Define Custom Bird-Watching Areas")
        OutlinedButton(onClick = {
            // Code to add a new custom area
        }) {
            Text(text = "Add New Area")
        }
        Text(text = "Manage Existing Areas")
        // Code to list and manage existing custom areas
        LazyColumn {
            items(2) { area ->
                Text(text = "Cape town")
                OutlinedButton(onClick = {
                    // Code to edit this area
                }) {
                    Text(text = "Edit")
                }
                OutlinedButton(onClick = {
                    // Code to delete this area
                }) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

// Function to navigate to Report
@Composable
fun navigateToReport(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 60.dp)
    ) {
        Text(text = "Report a Bird Sighting")
        OutlinedButton(onClick = {
            // Code to open bird sighting report form
        }) {
            Text(text = "New Sighting")
        }
        Text(text = "View Past Reports")
        // Code to list past sighting reports
        LazyColumn {
            items(3) { report ->
                Text(text = report.toString())
                OutlinedButton(onClick = {
                    // Code to view detailed report
                }) {
                    Text(text = "View Details")
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
        Text(text = "Developed by: Our team")
        Text(text = "License: Open Source")
        Text(text = "Contact: support@birdwatchingapp.com")
        // Add more detailed app info if necessary
    }
}
