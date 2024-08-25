@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View

import android.util.Patterns
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
fun Register(navController: NavController) {

    //variables for form
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State to track whether the password is visible
    var passwordVisible by remember { mutableStateOf(false) }

    // Error states
    var firstnameError by remember { mutableStateOf("") }
    var lastnameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    //functions to validate inputs
    //function to validate names of the user
    fun validateNames(name: String): Boolean {
        return if (name.length < 3) {
            firstnameError = "At least 3 letters"
            false
        } else if (name.any { !it.isLetter() }) {
            firstnameError = "Must contain only letters"
            false
        } else {
            firstnameError = ""
            true
        }
    }

    //function to validate email
    fun validateEmail(email: String): Boolean {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email address."
            false
        } else {
            emailError = ""
            true
        }
    }

    //function for validating password
    fun validatePassword(password: String): Boolean {
        return if (password.length < 8) {
            passwordError = "Must contain at least 8 characters long"
            false
        } else if (!password.any { it.isDigit() }) {
            passwordError = "Must contain at least a number"
            false
        } else if (!password.any { it.isLowerCase() }) {
            passwordError = "Must contain at least a lowercase letter"
            false
        } else if (!password.any { it.isUpperCase() }) {
            passwordError = "Must contain at least a uppercase letter"
            false
        } else if (!password.any { !it.isLetterOrDigit() }) {
            passwordError = "Must contain at least special characters"
            false
        } else {
            passwordError = ""
            true
        }
    }

    //function to validate confirm password to check if passwords match
    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (password != confirmPassword) {
            confirmPasswordError = "Passwords do not match."
            false
        } else {
            confirmPasswordError = ""
            true
        }
    }


    //column for page
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        //box for logo image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .background(Color.LightGray)
        ) {

            //logo image
            Image(
                modifier = Modifier
                    .width(400.dp)
                    .height(350.dp)
                    .padding(top = 30.dp),
                painter = painterResource(id = R.drawable.sky_high_watchers_logo),
                contentDescription = "logo"
            )

        }//end of logo image box

        // Welcome text
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Welcome, Create an Account...",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(20.dp, 0.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        //box for register form
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color.White)
        ) {

            //column for the form input fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                //text fields inputs for the register form
                // First name input field
                OutlinedTextField(
                    value = firstname,
                    onValueChange = { firstname = it },
                    label = { Text(text = "First Name") },
                    placeholder = { Text(text = "Enter First Name") },
                    isError = firstnameError.isNotEmpty(), // Show error color if there's an error
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp),
                    singleLine = true,

                    )
                if (firstnameError.isNotEmpty()) {
                    Text(
                        text = firstnameError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                //end of first name input layout
                //-----------
                //text field for last name
                OutlinedTextField(
                    value = lastname,
                    onValueChange = { lastname = it },
                    label = { Text(text = "Last Name") },
                    isError = lastnameError.isNotEmpty(),
                    placeholder = { Text(text = "Enter Last Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 20.dp),
                    singleLine = true
                )
                if (lastnameError.isNotEmpty()) {
                    Text(
                        text = lastnameError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                //end of last name text field
                //-------
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
                //-------
                //text field input for password
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
                        .padding(10.dp, 20.dp),
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
                        .padding(10.dp, 10.dp),
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
                Spacer(modifier = Modifier.height(30.dp))
                // Register button
                Button(
                    onClick = {
                        // Validate all input fields before proceeding
                        val isValid =
                            validateNames(firstname) && validateNames(lastname) && validateEmail(
                                email
                            ) && validatePassword(password) && validateConfirmPassword(
                                password,
                                confirmPassword
                            )

                        // If all validations pass, navigate to the login screen
                        if (isValid) {
                            navController.navigate("login")
                        }
                    },
                    shape = RoundedCornerShape(size = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Create Account", fontSize = 20.sp)
                }

            }
        }

    }//main column

}//end function