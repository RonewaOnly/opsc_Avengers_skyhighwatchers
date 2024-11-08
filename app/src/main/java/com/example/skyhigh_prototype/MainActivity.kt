package com.example.skyhigh_prototype

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.Model.BirdViewModel
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.example.skyhigh_prototype.Model.LocationViewModel
import com.example.skyhigh_prototype.Model.MapboxViewModel
import com.example.skyhigh_prototype.Model.currentLocations
import com.example.skyhigh_prototype.View.ForgotPassword
import com.example.skyhigh_prototype.View.Login
import com.example.skyhigh_prototype.View.Logout
import com.example.skyhigh_prototype.View.Main
import com.example.skyhigh_prototype.View.Register
import com.example.skyhigh_prototype.ui.theme.DarkBackground
import com.example.skyhigh_prototype.ui.theme.DarkOnPrimary
import com.example.skyhigh_prototype.ui.theme.DarkPrimary
import com.example.skyhigh_prototype.ui.theme.DarkSurface
import com.example.skyhigh_prototype.ui.theme.LightBackground
import com.example.skyhigh_prototype.ui.theme.LightOnPrimary
import com.example.skyhigh_prototype.ui.theme.LightPrimary
import com.example.skyhigh_prototype.ui.theme.LightSurface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val locationPermissionsRequestCode = 1
    val mapboxViewModel: MapboxViewModel by viewModels()
    private val viewModel: BirdViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var databaseHandle: DatabaseHandler


    // Initialize FusedLocationProviderClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databaseHandle = DatabaseHandler()
        databaseHandle.initGoogleSignIn(this)

        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            databaseHandle.handleSignInResult(data,
                onSuccess = {
                    Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                    // Navigate to the next screen
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
        // Initialize FusedLocationProviderClient in the ViewModel
        locationViewModel.initLocationClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Check for location permissions
//        if (hasLocationPermission()) {
//            locationViewModel.requestLocation(this)
//        } else {
//            requestLocationPermission()
//        }
        setContent {
            val navController = rememberNavController()

            var isDarkTheme by remember { mutableStateOf(false) }
            MaterialTheme(colorScheme = if (isDarkTheme) getDarkColors() else getLightColors()) {
                MyAppNavHost(fusedLocationProviderClient,navController = navController, viewModel,isDarkTheme, onThemeChange = { isDarkTheme = it },databaseHandle,googleSignInLauncher)

            }





            // MyAppNavHost(navController = navController, viewModel)
        }
    }

//    private fun hasLocationPermission(): Boolean {
//        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestLocationPermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionsRequestCode)
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == locationPermissionsRequestCode) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
//                locationViewModel.requestLocation(this) // Request location after permission is granted
//            } else {
//                // Permission denied
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


}

@Composable
fun SkyHigh(fusedLocationProviderClient: FusedLocationProviderClient, mainActivity: MainActivity, ebirdView: BirdViewModel, isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit, databaseHandle: DatabaseHandler, googleSignInLauncher: ActivityResultLauncher<Intent>) {
    val rememberNav = rememberNavController()

    NavHost(navController = rememberNav, startDestination = "login") {
        composable("login") {
            Login(rememberNav,databaseHandle, googleSignInLauncher)
        }
        composable("register") {
            Register(rememberNav)
        }
        composable("forgotPassword") {
            ForgotPassword(rememberNav)
        }
        composable("homepage") {
            Main( mainActivity.mapboxViewModel,ebirdView, isDarkTheme, onThemeChange,databaseHandle,rememberNav) // Passes ViewModel to Main Composable
        }
        composable("logout"){
            Logout(rememberNav)
        }
    }
}

// Composable function for splash screen
@Composable
fun SplashScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Animation effect for logo image
        AnimatedLogo()

        // Navigate to next screen after a delay
        LaunchedEffect(key1 = true) {
            delay(5000L)
            navController.navigate("sky_high") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}

// Composable function for navigation between splash and main login screen
@Composable
fun MyAppNavHost(fusedLocationProviderClient:FusedLocationProviderClient,navController: NavHostController,ebirdView: BirdViewModel,isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit, databaseHandle: DatabaseHandler, googleSignInLauncher: ActivityResultLauncher<Intent>) {
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") { SplashScreen(navController) }
        composable("sky_high") { SkyHigh( fusedLocationProviderClient =  fusedLocationProviderClient,mainActivity = LocalContext.current as MainActivity,ebirdView,isDarkTheme, onThemeChange,databaseHandle, googleSignInLauncher) }
    }
}

// Composable function for animating logo image
@Composable
fun AnimatedLogo() {
    var animationPhase by remember { mutableIntStateOf(1) }

    val alpha = animateFloatAsState(
        targetValue = if (animationPhase == 1) 0.5f else 1f,
        animationSpec = tween(durationMillis = 2500), label = ""
    )

    val scale = animateFloatAsState(
        targetValue = if (animationPhase == 1) 1.5f else 1f,
        animationSpec = tween(durationMillis = 2500), label = ""
    )

    val translationY = animateFloatAsState(
        targetValue = if (animationPhase == 1) 100f else 0f,
        animationSpec = tween(durationMillis = 2500), label = ""
    )

    Image(
        painter = painterResource(id = R.drawable.sky_high_watchers_logo),
        contentDescription = "Animated Logo",
        modifier = Modifier
            .size(250.dp)
            .graphicsLayer(
                alpha = alpha.value,
                scaleX = scale.value,
                scaleY = scale.value,
                translationY = translationY.value
            )
    )

    LaunchedEffect(animationPhase) {
        delay(2500)
        animationPhase = if (animationPhase == 1) 2 else 1
    }
}

fun getDarkColors() = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnPrimary,  // Text color on primary background
    onBackground = Color.White,  // Text color on dark background
    onSurface = Color.White      // Text color on dark surfaces
)

// Light color palette for the light theme
fun getLightColors() = lightColorScheme(
    primary = LightPrimary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,  // Text color on primary background
    onBackground = Color.Black,  // Text color on light background
    onSurface = Color.Black      // Text color on light surfaces
)


fun updateAppLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // Forcefully recreate the current activity to apply the new locale
    if (context is Activity) {
        context.recreate()
    }
}