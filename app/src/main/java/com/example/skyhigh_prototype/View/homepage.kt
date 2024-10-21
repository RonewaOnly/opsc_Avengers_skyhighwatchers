@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.Data.Birds
import com.example.skyhigh_prototype.Data.Location
import com.example.skyhigh_prototype.Model.BirdViewModel
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.example.skyhigh_prototype.Model.MapboxViewModel
import com.example.skyhigh_prototype.Model.currentLocations
import com.example.skyhigh_prototype.R
import com.example.skyhigh_prototype.R.color.dark_blue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun Main(
    @Suppress("UNUSED_PARAMETER") mapViewModel: MapboxViewModel,
    ebirdViewModel: BirdViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    databaseHandler: DatabaseHandler
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val rememberNavController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavDrawer(
                    navController = rememberNavController,
                    drawerState = drawerState,
                    scope = rememberCoroutineScope()
                )
            }
        },
    ) {

        Spacer(
            modifier = Modifier
                .height(106.dp)
                .padding(120.dp)

        )

        Scaffold(
            topBar = {
                ExtendedFloatingActionButton(
                    text = { Text("") },
                    icon = {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = Color.Black,
                            modifier = Modifier.size(100.dp).padding(0.dp, 50.dp, 50.dp, 0.dp)
                        )
                    },
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
                        pressedElevation = 0.dp
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
                            modifier = Modifier.size(35.dp) // Keep this size as per your design
                        )
                    },
                    onClick = { rememberNavController.navigate("ViewMap") }
                )
            }
        ) { contentPadding ->
            // Screen content
            NavHost(navController = rememberNavController, startDestination = "homepage") {
                composable("homepage") {
                    Homepage(ebirdViewModel)
                }
                composable("settings") {
                    NavOption(isDarkTheme, onThemeChange,databaseHandler)
                }
                composable("profile") {
                    Profile(rememberNavController,databaseHandler)
                }
                composable("collection") {
                    PersonalCollection()
                }
                composable("ViewMap") {
                    MapOption(rememberNavController, ebirdViewModel)
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
            .background(Color.White)
            .padding(top = 50.dp),
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
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 20.dp),
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
fun Homepage(ebirdViewModel: BirdViewModel) {
    val context = LocalContext.current
    //var enabled by remember { mutableStateOf(true) }
    // State to track the selected tab index
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    // Tab titles
    val tabs = listOf("Personal Cards", "Recent Observation", "Tab 3")
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = 50.dp)
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
            ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            // Show different content based on selected tab
            when (selectedTabIndex) {
                0 -> TabContent1()
                1 -> TabContent2(
                    ebirdViewModel,
                    currentLocations.LATITUDE,
                    currentLocations.LONGITUDE,
                    getString(context, R.string.ebird_api_key)
                )

                2 -> TabContent3()
            }
        }
    }
}

@Composable
fun TabContent1() {
    val databaseClass =
        DatabaseHandler() // This is the global calling of the object for the class handling Firestore operations
    val context = LocalContext.current
    var birdsObs by remember { mutableStateOf(listOf<Birds>()) }

    LaunchedEffect(Unit) {
        databaseClass.fetchCollection(onSuccess = { collect ->
            birdsObs = collect
        }, onFailure = {
            Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Light gray background for the whole screen
            .padding(16.dp) // Padding around the entire content
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp) // Padding inside LazyColumn
        ) {
            itemsIndexed(birdsObs) { index, item ->
                Log.d("content", "$birdsObs")
                // State management for each card
                val chipStates = remember { mutableStateOf(List(3) { false }) }

                Card(
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp) // Padding between cards
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp) // Padding inside the Column
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bird1),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp) // Set height for images
                                .clip(RoundedCornerShape(8.dp)) // Rounded corners for images
                                .background(Color.White) // Background color for image area
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Space between image and text
                        Text(
                            text = "Bird Name ${item.bird_name}, $index", // Access bird name from the object
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333) // Dark gray for text
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Space between text and LazyRow
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 4.dp) // Padding inside LazyRow
                        ) {
                            items(chipStates.value.size) { chipIndex ->
                                val isSelected = chipStates.value[chipIndex]
                                InputChip(
                                    onClick = {
                                        // Toggle the selected state of the clicked chip
                                        chipStates.value =
                                            chipStates.value.mapIndexed { i, selected ->
                                                if (i == chipIndex) !selected else selected
                                            }
                                    },
                                    label = { Text("Chip ${chipIndex + 1}") },
                                    selected = isSelected,
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Localized description",
                                            Modifier.size(InputChipDefaults.AvatarSize),
                                            tint = Color.Red // Color for the trailing icon
                                        )
                                    },
                                    colors = InputChipDefaults.inputChipColors(
                                        containerColor = if (isSelected) Color(0xFFB2DFDB) else Color(
                                            0xFFE0E0E0
                                        ), // Change background color based on selection
                                        labelColor = Color.Black // Black text color for better readability
                                    ),
                                    modifier = Modifier.padding(end = 8.dp) // Padding between chips
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TabContent2(viewModel: BirdViewModel, lat: Double, lng: Double, apiKey: String) {
    val observations by viewModel.observations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF9F9F9)) // Light background for the entire screen
    ) {
        when {
            isLoading && observations.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp) // Larger size for better visibility
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(observations) { observation ->
                        BirdObservationItem(observation = observation)
                    }
                    item {
                        Button(
                            onClick = { viewModel.loadMore(lat, lng, apiKey) },
                            colors = ButtonColors(
                                disabledContentColor = Color.Gray,
                                containerColor = Color(0xFF9C27B0),
                                disabledContainerColor = Color.Magenta,
                                //backgroundColor = Color(0xFF9C27B0), // Light purple
                                contentColor = Color.White, // White text
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .height(48.dp) // Consistent height for the button
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp),
                                    color = Color.White // White progress indicator
                                )
                            }
                            Text(text = "Load More")
                        }
                    }
                }
            }
        }
    }

    Log.d("location for recent observation: ","LAT: $lat, LONG: $lng")
}

@Composable
fun TabContent3() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Content for Tab 3", modifier = Modifier.padding(16.dp))
    }
}
