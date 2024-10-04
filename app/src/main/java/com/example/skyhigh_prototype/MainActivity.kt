package com.example.skyhigh_prototype

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.skyhigh_prototype.Model.MapboxViewModel
import com.example.skyhigh_prototype.Model.currentLocations
import com.example.skyhigh_prototype.View.ForgotPassword
import com.example.skyhigh_prototype.View.Login
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
import kotlinx.coroutines.delay
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val locationPermissionsRequestCode = 1
    val mapboxViewModel: MapboxViewModel by viewModels()
    private val viewModel: BirdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize PermissionsManager (if still needed)
        // permissionsManager = PermissionsManager(this)

        // Check and request permissions
        checkAndRequestLocationPermissions()
        // Use your API key from strings.xml
        val apiKey = getString(R.string.ebird_api_key)

        // Fetch bird observations (example latitude and longitude)
        viewModel.getRecentBirdObservations(currentLocations.LATITUDE, currentLocations.LONGITUDE, apiKey)
        setContent {
            val navController = rememberNavController()

            var isDarkTheme by remember { mutableStateOf(false) }
            MaterialTheme(colorScheme = if (isDarkTheme) getDarkColors() else getLightColors()) {
                MyAppNavHost(navController = navController, viewModel,isDarkTheme, onThemeChange = { isDarkTheme = it })

            }
           // MyAppNavHost(navController = navController, viewModel)
        }
    }

    private fun checkAndRequestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionsRequestCode)
        } else {
            // Permission already granted, initialize map
            mapboxViewModel.setupLocationProvider()
            mapboxViewModel.onPermissionResult(true)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionsRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
                mapboxViewModel.setupLocationProvider()
                mapboxViewModel.onPermissionResult(true)

            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                mapboxViewModel.onPermissionResult(true)

            }
        }
    }


}

@Composable
fun SkyHigh(mainActivity: MainActivity,ebirdView: BirdViewModel,isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val rememberNav = rememberNavController()

    NavHost(navController = rememberNav, startDestination = "login") {
        composable("login") {
            Login(rememberNav)
        }
        composable("register") {
            Register(rememberNav)
        }
        composable("forgotPassword") {
            ForgotPassword(rememberNav)
        }
        composable("homepage") {
            Main( mainActivity.mapboxViewModel,ebirdView, isDarkTheme, onThemeChange) // Passes ViewModel to Main Composable
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
fun MyAppNavHost(navController: NavHostController,ebirdView: BirdViewModel,isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") { SplashScreen(navController) }
        composable("sky_high") { SkyHigh(mainActivity = LocalContext.current as MainActivity,ebirdView,isDarkTheme, onThemeChange) }
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