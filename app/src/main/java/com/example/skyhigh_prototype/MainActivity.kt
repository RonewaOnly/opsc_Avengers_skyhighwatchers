package com.example.skyhigh_prototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.View.ForgotPassword
import com.example.skyhigh_prototype.View.Login
import com.example.skyhigh_prototype.View.Main
import com.example.skyhigh_prototype.View.Register
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            MyAppNavHost(navController = navController)

        }
    }
}
@Composable
fun SkyHigh(){
    //var drawerState = rememberDrawerState(DrawerValue.Closed)
    //val scope = rememberCoroutineScope()

    val rememberNav = rememberNavController()

    NavHost(navController = rememberNav, startDestination = "login") {
        composable("login"){
            Login(rememberNav)
        }
        composable("register"){
            Register(rememberNav)
        }
        composable("com.example.skyhigh_prototype.View.ForgotPassword"){
            ForgotPassword(rememberNav)
        }
        composable("homepage"){
            Main()
        }

    }
}

@Composable
fun SplashScreen(navController: NavController){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ){

        //animation effect for logo image
        AnimatedLogo()

        // Navigate to Onboarding after a delay
        LaunchedEffect(key1 = true) {
            delay(5000L)
            navController.navigate("main_screen") {
                popUpTo("splash") { inclusive = true }
            }
        }

    }
}

@Composable
fun MyAppNavHost(navController: NavHostController) {

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") { SplashScreen(navController) }
        composable("main_screen") { SkyHigh() }
    }
}

@Composable
fun AnimatedLogo() {

    // This variable keeps track of the current animation phase.
    // Phase 1: Starts the initial animation (shrink, fade, translate)
    // Phase 2: Reverses the animation (grow, restore opacity, translate back
    var animationPhase by remember { mutableIntStateOf(1) } // Keeps track of the current animation phase

    // Alpha (transparency) animation:
    // In phase 1, the logo fades to 50% opacity.
    // In phase 2, the logo returns to full opacity.
    val alpha = animateFloatAsState(
        targetValue = if (animationPhase == 1) 0.5f else 1f,
        animationSpec = tween(durationMillis = 2500), label = ""
    )

    // Scale animation:
    // In phase 1, the logo scales to 1.5x its original size.
    // In phase 2, it returns to its original size.
    val scale = animateFloatAsState(
        targetValue = if (animationPhase == 1) 1.5f else 1f,
        animationSpec = tween(durationMillis = 2500), label = ""
    )

    // Translation animation:
    // In phase 1, the logo moves 100 pixels downwards.
    // In phase 2, it moves back to its original position.
    val translationY = animateFloatAsState(
        targetValue = if (animationPhase == 1) 250f else 0f,
        animationSpec = tween(durationMillis = 2500), label = ""
    )

    // Apply these values to the Image
    Image(
        painter = painterResource(id = R.drawable.logo),
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

    // LaunchedEffect to trigger the next animation phase after the current one ends
    LaunchedEffect(animationPhase) {
        delay(2500) // Wait for the animation to complete
        animationPhase = if (animationPhase == 1) 2 else 1
    }
}


@Composable
@Preview
fun PreviewScreen(){
    SkyHigh()
}