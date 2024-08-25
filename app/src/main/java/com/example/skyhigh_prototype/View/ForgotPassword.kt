@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ForgotPassword(navController: NavController){
    var email by remember {
        mutableStateOf("")
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Magenta, RectangleShape),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Text must be placed here.")
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = {
                    Text(text = "Enter your email")
                }
            )
            Button(onClick = { navController.navigate("login") }) {
                Text(text = "confirm")
            }
        }
}
