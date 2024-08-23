package com.example.skyhigh_prototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyhigh_prototype.View.ForgotPassword
import com.example.skyhigh_prototype.View.Login
import com.example.skyhigh_prototype.View.Main
import com.example.skyhigh_prototype.View.Register
import com.example.skyhigh_prototype.ui.theme.SkyHigh_prototypeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkyHigh()
        }
    }
}
@Composable
fun SkyHigh(){
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val rememberNav = rememberNavController()

    NavHost(navController = rememberNav, startDestination = "login") {
        composable("login"){
            Login(rememberNav)
        }
        composable("register"){
            Register(rememberNav)
        }
        composable("forgotpassword"){
            ForgotPassword(rememberNav)
        }
        composable("homepage"){
            Main()
        }

    }
}

@Composable
@Preview
fun PreviewScreen(){
    SkyHigh()
}