package com.example.skyhigh_prototype.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skyhigh_prototype.R

@Composable
fun Register(navController: NavController){
    var firstname by remember {
        mutableStateOf("")
    }
    var lastname by remember {
        mutableStateOf("")
    }
    var username by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var phone by remember {
        mutableStateOf("")
    }
    var address by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var confirm_password by remember {
        mutableStateOf("")
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray, RectangleShape).padding(15.dp)
    ) {
        Spacer(modifier = Modifier.padding(40.dp))
        OutlinedTextField(
            value = firstname,
            onValueChange = {firstname = it},
            label = {
                Text(text = "Enter firstname")
            },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.baseline_person_24), contentDescription = null )
            }
        )

        OutlinedTextField(
            value = lastname,
            onValueChange = {lastname = it},
            label = {
                Text(text = "Enter lastname")
            },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.baseline_person_24), contentDescription = null )
            }
        )

        OutlinedTextField(
            value = username,
            onValueChange = {username = it},
            label = {
                Text(text = "Enter username")
            },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.baseline_person_24), contentDescription = null )
            }
        )
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = {
                Text(text = "Enter email")
            },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.envelope_solid),modifier = Modifier.size(20.dp), contentDescription = null )
            }
        )
        OutlinedTextField(
            value = phone,
            onValueChange = {phone = it},
            label = {
                Text(text = "Enter cell phone")
            },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.phone_solid),modifier = Modifier.size(20.dp) ,contentDescription = null )
            }
        )
        OutlinedTextField(
            value = address,
            onValueChange = {address = it},
            label = {
                Text(text = "Enter home address")
            },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.house_solid),modifier = Modifier.size(20.dp), contentDescription = null )
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = {
                Text(text = "Enter password")
            },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.password_key), contentDescription = null )
            },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.baseline_remove_red_eye_24), contentDescription = null )
            }
        )
        OutlinedTextField(
            value = confirm_password,
            onValueChange = {confirm_password = it},
            label = {
                Text(text = "Confirm password")
            },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.lock_solid),modifier = Modifier.size(20.dp), contentDescription = null )
            },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.baseline_remove_red_eye_24), contentDescription = null )
            }
        )
        Button(onClick = { navController.navigate("login") }) {
            Text(text = "Register")
        }

    }
}