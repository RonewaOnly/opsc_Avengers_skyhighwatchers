@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View

//noinspection UsingMaterialAndMaterial3Libraries

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private var auth = FirebaseAuth.getInstance()
@SuppressLint("StaticFieldLeak")
private var firestore = FirebaseFirestore.getInstance()


@Composable
fun Logout(navController: NavController, auth: FirebaseAuth, firestore: FirebaseFirestore) {

    //alert dialog variable
    var showAlertDialog by remember { mutableStateOf(true) }


    //to show dialog
    if (showAlertDialog) {
        AlertDialog(onDismissRequest = { showAlertDialog = false },
            title = { Text(text = "Confirm Logout") },
            text = { Text(text = "Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    //sign out user and clears log in details
                    firestore.clearPersistence()
                    auth.signOut()

                    //to delay intent to login page
                    navController.navigate("sky_high")

                    //closes dialog
                    showAlertDialog = false
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    //closes dialog
                    showAlertDialog = false

                }) {
                    Text(text = "No")
                }
            })
    }//end



}
