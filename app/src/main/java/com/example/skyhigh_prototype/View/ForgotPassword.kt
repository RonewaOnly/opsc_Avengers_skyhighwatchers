@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skyhigh_prototype.R

@Composable
fun ForgotPassword(navController: NavController){

    //form variables
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    // State to track whether the password is visible
    var passwordVisible by remember { mutableStateOf(false) }

    //Error states
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    //column for the page
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        //box for logo image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.25f)
                .background(Color.LightGray)

        ){
            //logo image
            Image(
                modifier = Modifier
                    .width(400.dp)
                    .height(350.dp)
                    .padding(top = 30.dp),
                painter = painterResource(id = R.drawable.sky_high_watchers_logo),
                contentDescription = "logo"
            )

        }//end of first box

        //text message
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Forgot Password?, Reset Password...",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(20.dp, 0.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        //second box for form
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.7f)
                .background(Color.White)
        ){
            //column for form text fields
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {

                //form input fields
                // Email input field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    placeholder = { Text(text = "Enter Email") },
                    isError = emailError.isNotEmpty(), // Show error color if there's an error
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp),
                    singleLine = true
                )
                if (emailError.isNotEmpty()) {
                    Text(
                        text = emailError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                //end of text field input for email
                //---------
                Spacer(modifier = Modifier.height(20.dp))
                // Password input field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") },
                    placeholder = { Text(text = "Enter Password") },
                    isError = passwordError.isNotEmpty(), // Show error color if there's an error
                    trailingIcon = {
                        // Toggle password visibility icon
                        val image = if (passwordVisible) {
                            painterResource(id = R.drawable.baseline_remove_red_eye_24)
                        } else {
                            painterResource(id = R.drawable.baseline_remove_red_eye_24)
                        }

                        Icon(painter = image,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible })
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp),
                    singleLine = true
                )
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                //end of text filed for password
                //--------
                Spacer(modifier = Modifier.height(20.dp))
                // Confirm password input field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(text = "Confirm Password") },
                    isError = confirmPasswordError.isNotEmpty(), // Show error color if there's an error
                    trailingIcon = {
                        // Toggle password visibility icon
                        val image = if (passwordVisible) {
                            painterResource(id = R.drawable.baseline_remove_red_eye_24)
                        } else {
                            painterResource(id = R.drawable.baseline_remove_red_eye_24)
                        }

                        Icon(painter = image,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible })
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp),
                    singleLine = true
                )
                if (confirmPasswordError.isNotEmpty()) {
                    Text(
                        text = confirmPasswordError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                //end of confirm password
                //---
                Spacer(modifier = Modifier.height(50.dp))
                // Reset Password Button
                Button(
                    onClick = {
                        // Validate all input fields before proceeding
                        emailError = ValidateForms.validateEmail(email)
                        passwordError = ValidateForms.validatePassword(password)
                        confirmPasswordError =
                            ValidateForms.validateConfirmPassword(password, confirmPassword)

                        // If all validations pass, navigate to the login screen
                        if (emailError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                            navController.navigate("login")
                        }
                    },
                    shape = RoundedCornerShape(size = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Reset Password", fontSize = 20.sp)
                }

            }
        }

    }//end of column




//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .border(1.dp, Color.Magenta, RectangleShape),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "Text must be placed here.")
//            Spacer(modifier = Modifier.padding(10.dp))
//            OutlinedTextField(//kotlin
//                value = email,
//                onValueChange = {email = it},
//                label = {
//                    Text(text = "Enter your email")
//                }
//            )
//            Button(onClick = { navController.navigate("login") }) {
//                Text(text = "confirm")
//            }
//        }
}
