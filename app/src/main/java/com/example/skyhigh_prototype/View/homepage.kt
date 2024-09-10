@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.Model.MapboxViewModel
import com.example.skyhigh_prototype.R
import com.example.skyhigh_prototype.R.color.dark_blue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun Main(@Suppress("UNUSED_PARAMETER") mapViewModel: MapboxViewModel = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val rememberNavController = rememberNavController()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { NavDrawer(navController = rememberNavController, drawerState = drawerState, scope = rememberCoroutineScope()) }
        },
    ) {
        Scaffold(

            topBar = {
                ExtendedFloatingActionButton(
                    text = { Text("Stamp") },
                    icon = { Icon(Icons.Filled.Menu, contentDescription = "", tint = Color.Black) },
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
                    text = { Text(text = "World") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.globe_solid),
                            contentDescription = "world icon",
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    onClick = { rememberNavController.navigate("ViewMap") })
            }
        ) { contentPadding ->
            // Screen content
            NavHost(navController = rememberNavController, startDestination = "homepage") {
                composable("homepage") {
                    Homepage()
                }
                composable("settings") {
                    navOption()
                }
                composable("profile") {
                    Profile(rememberNavController)
                }
                composable("collection") {
                    PersonalCollection()
                }
                composable("ViewMap") {
                    MapOption(rememberNavController)
                }
                composable("logout") {
                    Logout(rememberNavController)
                }
            }
            Spacer(modifier = Modifier.padding(contentPadding))
        }
    }
}

@Composable
fun NavDrawer(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {

    //column for entire page
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //box for logo image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .background(Color.LightGray)
        ) {

            Image(
                painter = painterResource(id = R.drawable.sky_high_watchers_logo),
                modifier = Modifier.fillMaxWidth(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )

        }//end of logo image box
        Spacer(modifier = Modifier.height(10.dp))
        //box for menu buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color.White),

        ) {

            //icon button for home
            IconButton(
                onClick = {
                    navController.navigate("Homepage")
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier
                    .padding(5.dp)
                    .background(colorResource(id = R.color.french_grey))
                    .fillMaxWidth(),
                colors = IconButtonDefaults.iconButtonColors(colorResource(id = R.color.french_grey))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        tint = Color.Blue,
                        contentDescription = null,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Home", fontWeight = FontWeight.Bold)
                }
            }//end of icon button for home

            //icon button for collection
            IconButton(
                onClick = {
                    navController.navigate("collection")
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier
                    .padding(5.dp, 70.dp)
                    .background(colorResource(id = R.color.french_grey))
                    .fillMaxWidth(),
                colors = IconButtonDefaults.iconButtonColors(colorResource(id = R.color.french_grey))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        tint = Color.Blue,
                        contentDescription = null,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Collection", fontWeight = FontWeight.Bold)
                }
            }//end of collection button

            //icon button for profile
            IconButton(
                onClick = {
                    navController.navigate("profile")
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier
                    .padding(5.dp, 140.dp)
                    .background(colorResource(id = R.color.french_grey))
                    .fillMaxWidth(),
                colors = IconButtonDefaults.iconButtonColors(colorResource(id = R.color.french_grey))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        tint = Color.Blue,
                        contentDescription = null,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Profile", fontWeight = FontWeight.Bold)
                }
            }//end of profile icon button

            //icon button for settings
            IconButton(
                onClick = {
                    navController.navigate("settings")
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier
                    .padding(5.dp, 210.dp)
                    .background(colorResource(id = R.color.french_grey))
                    .fillMaxWidth(),
                colors = IconButtonDefaults.iconButtonColors(colorResource(id = R.color.french_grey))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        tint = Color.Blue,
                        contentDescription = null,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Settings", fontWeight = FontWeight.Bold)
                }
            }//end of icon button for settings


            //log out icon button
            IconButton(
                onClick = {
                    navController.navigate("logout")
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier
                    .padding(5.dp, 280.dp, 5.dp, 0.dp)
                    .background(colorResource(id = R.color.red))
                    .fillMaxWidth(),
                colors = IconButtonDefaults.iconButtonColors(colorResource(id = R.color.red))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    @Suppress("DEPRECATION")
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        tint = Color.Black,
                        contentDescription = null,
                        modifier = Modifier.padding(5.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Logout", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }//end of box two


    }//end of main column

}

@SuppressLint("ResourceAsColor")
@Composable
fun Homepage() {
    var enabled by remember { mutableStateOf(true) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(dark_blue))
                    .height(230.dp)
                    .padding(paddingValues),
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
                            items(3) {
                                @Suppress("LABEL_NAME_CLASH")
                                if (!enabled) return@items
                                @Suppress("KotlinConstantConditions")
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
