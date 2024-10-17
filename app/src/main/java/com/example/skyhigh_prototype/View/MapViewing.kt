package com.example.skyhigh_prototype.View

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skyhigh_prototype.Model.BirdViewModel
import com.example.skyhigh_prototype.Model.MapScreen
import com.example.skyhigh_prototype.Model.MapboxViewModel
import com.example.skyhigh_prototype.Model.Maps

@Composable
fun MapOption(navController: NavController,birdViewModel: BirdViewModel){
    //val map = MapboxViewModel()

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { navController.navigate("homepage") }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }
        HorizontalDivider()
        //Maps()

        MapScreen()
    }
}