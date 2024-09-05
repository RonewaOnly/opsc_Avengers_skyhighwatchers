package com.example.skyhigh_prototype

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.Model.MapboxViewModel
import com.example.skyhigh_prototype.View.ForgotPassword
import com.example.skyhigh_prototype.View.Login
import com.example.skyhigh_prototype.View.Main
import com.example.skyhigh_prototype.View.Register
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val locationPermissionsRequestCode = 1
    val mapboxViewModel: MapboxViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize PermissionsManager (if still needed)
        // permissionsManager = PermissionsManager(this)

        // Check and request permissions
        checkAndRequestLocationPermissions()

        setContent {
            val navController = rememberNavController()
            MyAppNavHost(navController = navController)
        }
    }

    private fun checkAndRequestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionsRequestCode)
        } else {
            // Permission already granted, initialize map
            mapboxViewModel.setupLocationProvider()
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
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun SkyHigh(mainActivity: MainActivity) {
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
            Main( mainActivity.mapboxViewModel) // Passes ViewModel to Main Composable
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
fun MyAppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") { SplashScreen(navController) }
        composable("sky_high") { SkyHigh(mainActivity = LocalContext.current as MainActivity) }
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

@Composable
@Preview
fun PreviewScreen() {
    SkyHigh(mainActivity = LocalContext.current as MainActivity)
}
