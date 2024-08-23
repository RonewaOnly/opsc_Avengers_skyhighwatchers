package com.example.skyhigh_prototype.View

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skyhigh_prototype.R
import com.example.skyhigh_prototype.R.*

@SuppressLint("ResourceAsColor")
@Composable
fun Login(navController: NavController){
    var username by remember {
        mutableStateOf("")
    }
    var password by  remember {
        mutableStateOf("")
    }
    val rememberMe = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(width = 2.dp, color = Color(R.color.dark_blue), RoundedCornerShape(12.dp))
            .fillMaxHeight(1f)
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "logo")
        Spacer(modifier = Modifier.padding(50.dp))
        TextField(
            value = username,
            onValueChange = {username = it},
            label = {
                Text(text = "Enter username/email")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = null
                )
            },
            modifier = Modifier.width(350.dp)
        )
        Spacer(modifier = Modifier.padding(10.dp))
        TextField(
            value = password,
            onValueChange = {password = it},
            label = {
                Text(text = "Enter password")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password_key),
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_remove_red_eye_24),
                    contentDescription = null,
                    modifier = Modifier.clickable {

                    }
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.width(350.dp)
        )
        OutlinedButton(onClick = { navController.navigate("forgotpassword") }, shape = RoundedCornerShape(0)) {
            Text(text = "Forgot password")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(checked = rememberMe.value, onCheckedChange = {rememberMe.value = it})
            Text(text = "Remember Me")
        }
        
        Button(onClick = { navController.navigate("homepage") }) {
            Text(text = "Login")
        }
        HorizontalDivider()
        Button(onClick = { /*TODO*/ }, contentPadding = PaddingValues(10.dp), modifier = Modifier.width(300.dp), shape = RectangleShape, colors = ButtonColors(
            Color.White, Color.Black, Color.Blue,Color.LightGray), border = BorderStroke(1.dp, Color.DarkGray)) {
            Icon(painter = painterResource(id = R.drawable.google_brands_solid), contentDescription = null ,modifier = Modifier
                .clip(
                    RoundedCornerShape(5)
                )
                .size(30.dp))
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Continue with Google")
        }
        Button(onClick = { /*TODO*/ },modifier = Modifier
            .padding(10.dp, 5.dp)
            .width(300.dp), shape = RectangleShape, colors = ButtonColors(
            Color.White, Color.Black, Color.Blue,Color.LightGray), border = BorderStroke(1.dp, Color.DarkGray),
        ) {
            Icon(painter = painterResource(id = R.drawable.facebook_brands_solid), contentDescription = null,modifier = Modifier
                .clip(
                    RoundedCornerShape(5)
                )
                .size(30.dp)
                .padding(0.dp))
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Continue with facebook")
        }
        Button(onClick = { navController.navigate("register") },modifier = Modifier
            .padding(10.dp, 5.dp)
            .width(300.dp), shape = RectangleShape, colors = ButtonColors(
            Color.White, Color.Black, Color.Blue,Color.LightGray)
        ) {

            Text(text = "Continue with email")
        }

    }
}