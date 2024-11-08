package com.example.skyhigh_prototype.View

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ButtonDefaults
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenu
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenuItem
//noinspection UsingMaterialAndMaterial3Libraries,UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.Data.CustomArea
import com.example.skyhigh_prototype.Data.Reports
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.example.skyhigh_prototype.R
import com.example.skyhigh_prototype.updateAppLocale

@Composable
fun NavOption(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    databaseHandler: DatabaseHandler
) {
    val rememberNav = rememberNavController()

    NavHost(navController = rememberNav, startDestination = "setting") {
        composable("setting") {
            Settings(rememberNav)
        }
        composable("general") {
            navigateToGeneralSettings(rememberNav, isDarkTheme, onThemeChange, databaseHandler)
        }
        composable("customArea") {
            NavigateToCustomAreas(rememberNav, databaseHandler)
        }
        composable("Report") {
            navigateToReport(rememberNav, databaseHandler)
        }
        composable("App_info") {
            navigateToAppInfo(rememberNav)
        }
        composable(route = "DeleteUserAccount") {
            DeleteUserAccount()
        }
        composable(route = "UpdatePasswordDialog") {

            UpdatePasswordDialog(onDismiss = { })
        }
    }
}

@Composable
fun Settings(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start= 40.dp, top = 140.dp )
    ) {

        Text(text = "Settings", textAlign = TextAlign.Start, fontSize = 30.sp, fontWeight = FontWeight.Bold)


        OutlinedButton(
            onClick = {
                navController.navigate("general")
            },
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.Blue),
            modifier = Modifier.width(300.dp).padding(top = 30.dp)
        ) {
            Text(text = stringResource(R.string.general), textAlign = TextAlign.Start)
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                navController.navigate("customArea")
            },
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.Blue),
            modifier = Modifier.width(300.dp)
        ) {
            Text(text = stringResource(R.string.custom_areas))
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                navController.navigate("Report")
            },
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.Blue),
            modifier = Modifier.width(300.dp)
        ) {
            Text(text = stringResource(R.string.report))
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                navController.navigate("App_info")
            },
            shape = RectangleShape,
            border = BorderStroke(1.dp, Color.Blue),
            modifier = Modifier.width(300.dp)
        ) {
            Text(text = stringResource(R.string.app_info))
        }
    }
}

// General Settings Screen
@SuppressLint("ComposableNaming")
@Composable
fun navigateToGeneralSettings(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    databaseHandler: DatabaseHandler
) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(false) }
    //variable for metric system change
    var useKilometers by remember { mutableStateOf(true) }
    var chosenLanguage by remember { mutableStateOf("") }
    var themeChosen by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 120.dp)
    ) {

        // Notification Settings Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.notification_settings),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = notificationsEnabled, onCheckedChange = {
                notificationsEnabled = it
                if (notificationsEnabled) {
                    createNotificationChannel(context)
                    sendNotification(
                        context, context.getString(R.string.app_name), "Notifications enabled!"
                    )
                } else {
                    sendNotification(
                        context, context.getString(R.string.app_name), "Notifications disabled!"
                    )
                }


                databaseHandler.updateSettings(notification = notificationsEnabled, onSuccess = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Log.e("Updating error on settings: ", it.message.toString())
                })
            })
        }


        //distance section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Distance unit section : Metric or imperial
            Text(
                text = "Distance",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(2.8f)
            )

            Text(
                text = if (useKilometers) "Kilometers" else "Miles",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Switch(checked = useKilometers, onCheckedChange = {
                useKilometers = it
                databaseHandler.updateSettings(unitOfDistance = useKilometers, onSuccess = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Log.e("Updating error on settings: ", it.message.toString())
                })
            })
        }

        Spacer(modifier = Modifier.height(20.dp))
        // Theme Settings Section
        Text(
            text = stringResource(R.string.theme_settings),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                onThemeChange(!isDarkTheme)  // Toggle the theme between Light and Dark
                themeChosen = if (isDarkTheme) {
                    R.string.switch_to_light_mode
                } else {

                    R.string.switch_to_dark_mode

                }
                databaseHandler.updateSettings(theme = themeChosen, onSuccess = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Log.e("Updating error on settings: ", it.message.toString())
                })
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isDarkTheme) stringResource(R.string.switch_to_light_mode) else stringResource(
                    R.string.switch_to_dark_mode
                ), textAlign = TextAlign.Center
            )


        }
        Spacer(modifier = Modifier.height(20.dp))

        // Language Settings Section
        Text(
            text = stringResource(R.string.language_settings),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(10.dp))
        LanguageSelection(onLanguageSelected = { selectedLanguage ->
            updateAppLocale(context, selectedLanguage)
            sendNotification(
                context, "Language Changed", "App language changed to $selectedLanguage"
            )
            chosenLanguage =
                selectedLanguage//this variable will be used for the updating to the database
        })

//        databaseHandler.updateSettings(notificationsEnabled,useKilometers,chosenLanguage,themeChosen ,onSuccess = {
//            Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
//        }, onFailure = {
//            Log.e("Updating error on settings: ",it.message.toString())
//        })

        Spacer(modifier = Modifier.height(20.dp))
        //delete account section
        Button(
            onClick = {

                //navigate to delete account function
                navController.navigate("UpdatePasswordDialog")
            },
            modifier = Modifier

                .width(250.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
        ) {
            Text(text = "Update Password", fontSize = 20.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(20.dp))
        //delete account section
        Button(
            onClick = {

                //navigate to delete account function
                navController.navigate("DeleteUserAccount")
            },
            modifier = Modifier
                .background(colorResource(id = R.color.red))
                .width(250.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text(text = "Delete Account", fontSize = 20.sp, color = Color.White)
        }

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
            onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Change Language: $selectedLanguage", textAlign = TextAlign.Center)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification =
        NotificationCompat.Builder(context, "birdwatching_notifications").setContentTitle(title)
            .setContentText(message).setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
    notificationManager.notify(1, notification)
}

// Function to navigate to Custom Areas
@Composable
fun NavigateToCustomAreas(navController: NavController, databaseHandler: DatabaseHandler) {
    var customAreas by remember { mutableStateOf(listOf<CustomArea>()) }
    var newAreaName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 160.dp)
    ) {
        Text(text = stringResource(R.string.define_custom_areas), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(value = newAreaName,
            onValueChange = { newAreaName = it },
            label = { Text(stringResource(R.string.new_area_name)) })
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(onClick = {
            if (newAreaName.isNotBlank()) {
                databaseHandler.createCustomArea(newAreaName, onSuccess = {}, onFailure = {
                    Log.e("Custom Area creation error: ", "${it.message}")
                })
//                customAreas = customAreas + newAreaName
//                newAreaName = ""
            }else{
                Toast.makeText(context, "Area Name can be empty", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = stringResource(R.string.add_new_area))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(R.string.manage_existing_areas), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))

        databaseHandler.fetchCustomAreas(onSuccess = {
            customAreas = it
        }, onFailure = {
            Log.e("Fetching the name of the area failure:  ", "${it.message}")
        })
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(customAreas) { area ->
                Text(text = "Area Name: ${area.name}")
                Text(text = "Sightings in ${area.name} : ${area.sightingsInArea}")
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(onClick = {
                    customAreas = customAreas - area
                }) {
                    Text(text = stringResource(R.string.delete))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun navigateToReport(navController: NavController, databaseHandler: DatabaseHandler) {
    var reports by remember { mutableStateOf(listOf<Reports>()) }
    var newReport by remember { mutableStateOf("") }
    var selectedReport by remember { mutableStateOf<Reports?>(null) } // Hold the selected report for the dialog
    val context = LocalContext.current

    // Fetching reports on first composition
    LaunchedEffect(Unit) {
        databaseHandler.fetchReports(onSuccess = {
            reports = it
        }, onFailure = {
            Log.e("Error from fetching reports", "${it.message}")
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start= 20.dp, top = 160.dp)
    ) {
        Text(text = stringResource(R.string.report_bird_sighting), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        // Input field for creating a new report
        OutlinedTextField(
            value = newReport,
            onValueChange = { newReport = it },
            label = { Text(stringResource(R.string.enter_bird_details)) }
        )

        Spacer(modifier = Modifier.height(10.dp))
        // Button to submit a new report
        OutlinedButton(
            onClick = {
                if (newReport.isNotBlank()) {
                    databaseHandler.createReport(
                        reportName = newReport,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Created a report successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Fetch updated reports after creation
                            databaseHandler.fetchReports(
                                onSuccess = { reports = it },
                                onFailure = { Log.e("Error", it.message ?: "Unknown error") })
                        },
                        onFailure = {
                            Log.e("Creating a report", "${it.message}")
                        }
                    )
                    newReport = ""
                }
            }
        ) {
            Text(text = stringResource(R.string.new_sighting))
        }

        Spacer(modifier = Modifier.height(40.dp))
        // View past reports
        Text(text = stringResource(R.string.view_past_reports), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        // LazyColumn to display all reports
        LazyColumn {
            items(reports) { report ->
                Column {
                    Text(text = "Title: ${report.reportName}")
                    Text(text = "Description: ${report.reportDescription}" )
                    Text(text = "Time: ${report.reportDate.toDate()}")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(onClick = {
                        selectedReport = report // Set the selected report to show in the dialog
                    }) {
                        Text(text = stringResource(R.string.view_details))
                    }
                }
            }
        }
    }

    // If a report is selected, show the dialog
    selectedReport?.let { report ->
        viewReport(
            id = report.reportId ?: "",
            report = report,
            onClose = { closeDialog ->
                if (closeDialog) {
                    selectedReport = null // Close the dialog by resetting the selected report
                }
            },
            databaseHandler = databaseHandler
        )
    }
}

@Composable
fun viewReport(
    id: String,
    report: Reports,
    onClose: (Boolean) -> Unit,
    databaseHandler: DatabaseHandler
) {
    val context = LocalContext.current
    var reportName by remember { mutableStateOf(report.reportName ?: "") }
    var reportDescription by remember { mutableStateOf(report.reportDescription ?: "") }

    Dialog(
        onDismissRequest = { onClose(true) }
    ) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = reportName,
                    placeholder = { Text("Enter report name: ") },
                    onValueChange = { reportName = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = reportDescription,
                    placeholder = { Text("Enter report description") },
                    onValueChange = { reportDescription = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Button to edit and save the report details
                OutlinedButton(onClick = {
                    // Update report details in the database
                    databaseHandler.updateReport(
                        reportId = report.reportId ?: "",
                        newReportName = reportName,
                        newReportDescription = reportDescription,
                        onSuccess = {
                            Toast.makeText(context, "Report updated", Toast.LENGTH_SHORT).show()
                            onClose(true) // Close the dialog
                        },
                        onFailure = {
                            Log.e("Updating report", it.message ?: "Unknown error")
                        }
                    )
                }) {
                    Text(text = "Edit Report")
                }
            }
        }
    }
}


// Function to navigate to App Info
@SuppressLint("ComposableNaming")
@Composable
fun navigateToAppInfo(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 160.dp)
    ) {
        Text(text = "App Version: 1.0.0", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Developed by: Birdwatching Team", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "License: Open Source", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Contact: support@birdwatchingapp.com", fontSize = 18.sp)
    }
}


