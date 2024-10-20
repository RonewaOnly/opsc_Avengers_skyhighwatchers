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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.example.skyhigh_prototype.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun Register(navController: NavController) {

    //variables for form
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    remember { mutableStateOf(false) }

    // State to track whether the password is visible
    var passwordVisible by remember { mutableStateOf(false) }

    // Error states
    var firstnameError by remember { mutableStateOf("") }
    var lastnameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    //firebase instances
    var auth : FirebaseAuth
    var firestore: FirebaseFirestore

    //DatabaseHandler in register screen
    val dbClass = DatabaseHandler()
    //column for page
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 50.dp)
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
                    .padding(top = 30.dp)
                    .align(Alignment.Center),
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

                //val context for toast message
                val context = LocalContext.current
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
                Spacer(modifier = Modifier.height(20.dp))
                //text field for last name
                OutlinedTextField(
                    value = lastname,
                    onValueChange = { lastname = it },
                    label = { Text(text = "Last Name") },
                    isError = lastnameError.isNotEmpty(),
                    placeholder = { Text(text = "Enter Last Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp),
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
                Spacer(modifier = Modifier.height(20.dp))
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
                Spacer(modifier = Modifier.height(20.dp))
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
                Spacer(modifier = Modifier.height(30.dp))
                // register button
                Button(
                    onClick = {
                        // Validate all input fields before proceeding
                        firstnameError = ValidateForms.validateFirstName(firstname)
                        lastnameError = ValidateForms.validateLastName(lastname)
                        emailError = ValidateForms.validateEmail(email)
                        passwordError = ValidateForms.validatePassword(password)
                        confirmPasswordError = ValidateForms.validateConfirmPassword(password, confirmPassword)
                        //initialising firebase instances
                        auth = FirebaseAuth.getInstance()
                        firestore = FirebaseFirestore.getInstance()

                        // If all validations pass, navigate to the login screen
                        // If no errors, proceed with registration
                        if (firstnameError.isEmpty() && lastnameError.isEmpty() && emailError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                                dbClass.register(firstname, lastname, email, password, navController, context)
//                            //using firebase auth to create a user with authentication
//                            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
//
//                                //get current user id
//                                val userID = auth.currentUser?.uid
//
//                                //using hash map to store user details to firestore database
//                                val user = hashMapOf(
//                                    "First Name" to  firstname,
//                                    "Last Name" to lastname,
//                                    "Email" to email
//
//                                )
//
//                                //creating user with id
//                                if(userID != null){
//
//                                    //adding user to collection
//                                    firestore.collection("Users").document(userID).set(user).addOnFailureListener {
//                                        //to alert user
//                                        Toast.makeText(context,"Unable to save user details to database", Toast.LENGTH_LONG).show()
//                                    }
//                                }
//
//                                //alert user
//                                Toast.makeText(context,"Successful Account Creation\nYou will be redirected to Login Page", Toast.LENGTH_LONG).show()
//
//                                //to delay intent
//                                @Suppress("DEPRECATION")
//                                Handler().postDelayed({
//
//                                    //navigate to login
//                                    navController.navigate("login")
//
//                                }, 2000)
//
//
//                            }.addOnFailureListener {
//
//                                //to alert user
//                                Toast.makeText(context,"Email already exit", Toast.LENGTH_LONG).show()
//
//                            }
                        }
                    },
                    shape = RoundedCornerShape(size = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Create Account", fontSize = 20.sp)
                }//end of button

            }
        }

    }//main column

}//end function



