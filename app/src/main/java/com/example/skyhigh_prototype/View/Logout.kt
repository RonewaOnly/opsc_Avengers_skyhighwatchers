@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View

//noinspection UsingMaterialAndMaterial3Libraries

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.skyhigh_prototype.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

private var auth = FirebaseAuth.getInstance()
@SuppressLint("StaticFieldLeak")
private var firestore = FirebaseFirestore.getInstance()

@Composable
fun Logout(navController: NavController) {

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
                    @Suppress("DEPRECATION") Handler().postDelayed({
                        navController.navigate("logout"
                        )
                    }, 2000)


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
