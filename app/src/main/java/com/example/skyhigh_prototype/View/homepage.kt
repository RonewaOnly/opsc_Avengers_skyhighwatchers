package com.example.skyhigh_prototype.View

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.Model.MapboxViewModel
import com.example.skyhigh_prototype.R
import com.example.skyhigh_prototype.R.color.dark_blue
import kotlinx.coroutines.launch


@Composable
fun Main(){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val rememberNavController = rememberNavController()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { NavDrawer(navController = rememberNavController) }
        },
    ) {
        Scaffold(
            topBar = {
                ExtendedFloatingActionButton(
                    text = { Text("") },
                    icon = { Icon(Icons.Filled.Menu, contentDescription = "") },
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 6.dp
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(text="") },
                    icon = { Icon(painter = painterResource(id = R.drawable.globe_solid), contentDescription = "world icon",modifier = Modifier.size(35.dp)) },
                    onClick = { rememberNavController.navigate("ViewMap") })
            }
        ) { contentPadding ->
            // Screen content
            NavHost(navController = rememberNavController, startDestination = "homepage") {
                composable("homepage"){
                    Homepage()
                }
                composable("settings"){
                    navOption()
                }
                composable("profile"){
                    Profile(rememberNavController)
                }
                composable("collection"){
                    PersonalCollection()
                }
                composable("ViewMap"){
                    MapOption(rememberNavController )
                }
                composable("logout"){
                    Logout(rememberNavController)
                }
            }
            Spacer(modifier = Modifier.padding(contentPadding))
        }
    }
}

@Composable
fun NavDrawer(navController: NavController){
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Image(painter = painterResource(id = R.drawable.logo), modifier = Modifier.fillMaxWidth() ,contentDescription =null, contentScale = ContentScale.FillWidth, colorFilter = ColorFilter.tint(Color.Red,
            BlendMode.Difference))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(10.dp)
                .background(Color.LightGray)
                .fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Filled.Home, contentDescription = null)
            OutlinedButton(onClick = { navController.navigate("Homepage")},modifier = Modifier.fillMaxWidth()
                , shape = RectangleShape,
                colors = ButtonColors(containerColor = Color.Transparent, contentColor = Color.Black, disabledContainerColor = Color.DarkGray, disabledContentColor = Color.Yellow),
                border = BorderStroke(0.dp,Color.Transparent)) {
                Text(text = "Home")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(10.dp)
                .background(Color.LightGray)
                .fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = null)
            OutlinedButton(onClick = { navController.navigate("settings") },modifier = Modifier.fillMaxWidth()
                , shape = RectangleShape,
                colors = ButtonColors(containerColor = Color.Transparent, contentColor = Color.Black, disabledContainerColor = Color.DarkGray, disabledContentColor = Color.Yellow),
                border = BorderStroke(0.dp,Color.Transparent)) {
                Text(text = "Settings")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(10.dp)
                .background(Color.LightGray)
                .fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null)
            OutlinedButton(onClick = { navController.navigate("profile") },modifier = Modifier.fillMaxWidth()
                , shape = RectangleShape,
                colors = ButtonColors(containerColor = Color.Transparent, contentColor = Color.Black, disabledContainerColor = Color.DarkGray, disabledContentColor = Color.Yellow),
                border = BorderStroke(0.dp,Color.Transparent)) {
                Text(text = "Profile")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(10.dp)
                .background(Color.LightGray)
                .fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null)
            OutlinedButton(onClick = { navController.navigate("collection") },modifier = Modifier.fillMaxWidth()
                , shape = RectangleShape,
                colors = ButtonColors(containerColor = Color.Transparent, contentColor = Color.Black, disabledContainerColor = Color.DarkGray, disabledContentColor = Color.Yellow),
                border = BorderStroke(0.dp,Color.Transparent)) {
                Text(text = "Collection")
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()
        OutlinedButton(onClick = { navController.navigate("logout") },modifier = Modifier
            .fillMaxWidth()
            , shape = RectangleShape,
            colors = ButtonColors(containerColor = Color.LightGray, contentColor = Color.White, disabledContainerColor = Color.DarkGray, disabledContentColor = Color.Yellow),
            border = BorderStroke(0.dp,Color.Transparent)
            ) {
            Text(text = "Logout")
        }
    }
}
@SuppressLint("ResourceAsColor")
@Composable
fun Homepage(){
    var enabled by remember { mutableStateOf(true) }

    Scaffold {PaddingValue ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(dark_blue))
                    .height(230.dp)
                    .padding(PaddingValue),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Welcome to Sky High ", color = Color.White)
            }
            LazyColumn {
                items(30) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Green)
                            .padding(bottom = 10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bird1),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                        )
                        Text(text = "Bird Name")
                        LazyRow {
                            items(3){
                                if (!enabled) return@items
                                InputChip(
                                    onClick = {
                                        enabled = !enabled
                                    },
                                    label = { Text("hi") },
                                    selected = enabled,
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Localized description",
                                            Modifier.size(InputChipDefaults.AvatarSize)
                                        )
                                    },
                                )
                                Spacer(modifier = Modifier.padding(20.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(20.dp))

                }

            }
        }
    }
}
