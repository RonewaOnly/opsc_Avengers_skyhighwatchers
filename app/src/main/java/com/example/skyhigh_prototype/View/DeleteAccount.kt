@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View

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

@Composable
fun DeleteUserAccount(navController: NavController) {

    //alert dialog variable
    var deleteAlertDialog by remember { mutableStateOf(true) }

    //auth and firestore instances
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser: FirebaseUser? = auth.currentUser

    //to show dialog
    if (deleteAlertDialog) {
        AlertDialog(onDismissRequest = { deleteAlertDialog = false },
            title = { Text(text = "Confirm Delete Account") },
            text = { Text(text = "Are you sure you want to delete account?") },
            confirmButton = {
                TextButton(onClick = {

                    currentUser?.let { user ->
                        try {
                            //deleting account from firestore
                            firestore.collection("Users").document(user.uid).delete()

                            //deleting account from authentication
                            user.delete()
                            navController.navigate("login"
                            )

                        } catch (e: Exception) {
                            Log.e("", "")
                        }
                    } ?: Log.e("", "")

                    //closes dialog
                    deleteAlertDialog = false
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    //closes dialog
                    deleteAlertDialog = false

                }) {
                    Text(text = "No")
                }
            })
    }//end

}//end end