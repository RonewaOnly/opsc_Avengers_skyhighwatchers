@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Logout(navController: NavController){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(onClick = { /*TODO*/ }) {
            Text(text = "Logout")
        }
        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()
        OutlinedButton(onClick = { /*TODO*/ }) {
            Text(text = "Delete Accounts")
        }
        TextButton(onClick = { /*TODO*/ }) {
            Text(text = "pause account")
        }
    }
}