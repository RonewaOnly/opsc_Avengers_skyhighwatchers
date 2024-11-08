@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View


import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.example.skyhigh_prototype.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Login(navController: NavController, databaseHandle: DatabaseHandler, googleSignInLauncher: ActivityResultLauncher<Intent>) {
    val databasehandle = DatabaseHandler()
    //variables
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val rememberMe = remember {
        mutableStateOf(false)
    }

    // State to track whether the password is visible
    var passwordVisible by remember { mutableStateOf(false) }

    //Error states
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    //firebase instances
    //var auth : FirebaseAuth

    //column for page
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding( top = 50.dp)
    ) {

        //first box contains logo image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .background(Color.LightGray)
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                modifier = Modifier
                    .width(400.dp)
                    .height(350.dp)
                    .padding(top = 30.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.sky_high_watchers_logo),
                contentDescription = "logo"

            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Hi, Welcome Back...",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(30.dp, 0.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        // box 2
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color.White)
                .padding(20.dp)
        ) {

            //column for input fields
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
            ) {

                //email input remaining
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = {
                        Text(text = "Email")
                    },
                    placeholder = { Text(text = "Enter Email") },
                    isError = emailError.isNotEmpty(),
                    modifier = Modifier
                        .width(400.dp)
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
                }//end

                Spacer(modifier = Modifier.height(10.dp))
                //password input field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(text = "Password")
                    },
                    isError = passwordError.isNotEmpty(),
                    placeholder = { Text(text = "Enter Password") },
                    trailingIcon = {
                        val image = if (passwordVisible) {

                            painterResource(id = R.drawable.baseline_remove_red_eye_24)

                        } else {

                            painterResource(id = R.drawable.baseline_remove_red_eye_24)
                        }

                        Icon(painter = image,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.clickable {
                                passwordVisible = !passwordVisible
                            })
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .width(400.dp)
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
                }//end

            }//end of column

            //val context for toast message
            val context = LocalContext.current

            //box for remember me and forgot password
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Make the Box fill the entire width of the screen
                    .padding(top = 170.dp) // Add top padding to position the elements as needed
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(), // Make the Row fill the entire width of the Box
                    verticalAlignment = Alignment.CenterVertically, // Align elements vertically center within the Row
                    horizontalArrangement = Arrangement.SpaceBetween // Space the elements between start and end
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically // Align checkbox and text vertically center
                    ) {
                        Checkbox(modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp),
                            checked = rememberMe.value,
                            onCheckedChange = { rememberMe.value = it })
                        Spacer(modifier = Modifier.width(2.dp)) // Add some space between the checkbox and text
                        Text(text = "Remember Me")
                    }

                    TextButton(
                        onClick = { navController.navigate("forgotPassword") },
                        modifier = Modifier.padding(10.dp) // Add padding to the button if needed
                    ) {
                        Text(text = "Forgot Password?")
                    }
                }
            }//End

            Spacer(modifier = Modifier.height(200.dp))
            //button to login
            Button(
                onClick = {

                    //initialising firebase instances
                   // auth = FirebaseAuth.getInstance()


                    // Validate all input fields before proceeding
                    emailError = ValidateForms.loginEmail(username)
                    passwordError = ValidateForms.loginPassword(password)

                    //if to check validations passed
                    if (emailError.isEmpty() && passwordError.isEmpty()) {
                        databasehandle.Login(username,password,context,navController,onSuccess ={}, onError = {
                            passwordError = it
                        })
                    }
                },

                shape = RoundedCornerShape(size = 10.dp),
                modifier = Modifier
                    .padding(70.dp, 250.dp, 0.dp, 0.dp)
                    .width(250.dp)
                    .height(50.dp)

            ) {
                Text(text = "Login")
            }


            HorizontalDivider(modifier = Modifier.padding(0.dp, 350.dp, 0.dp, 0.dp))

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "or continue with",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 300.dp) // Adjusted padding
                )

                Spacer(modifier = Modifier.height(20.dp))
                //row for social media buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp), // Optional padding to center the buttons within the column
                    horizontalArrangement = Arrangement.Center, // Center buttons horizontally
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Google account button
                    Button(
                        onClick = { databaseHandle.signInWithGoogle(context as Activity, googleSignInLauncher,
                            onSuccess = {
                                navController.navigate("homepage") // Navigate to homepage on success
                            },
                            onError = { errorMessage ->
                                // Handle error if needed, e.g., show a Toast
                                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                            })
                        },
                        modifier = Modifier
                            .padding(10.dp, 5.dp)
                            .width(150.dp),
                        shape = RectangleShape,
                        colors = buttonColors(
                            Color.White, Color.Black, Color.Blue, Color.LightGray
                        ),
                        border = BorderStroke(1.dp, Color.DarkGray),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.google_brands_solid),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(5))
                                .size(30.dp)
                                .padding(0.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Google")
                    }

                    // Facebook account button
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .padding(10.dp, 5.dp)
                            .width(150.dp),
                        shape = RectangleShape,
                        colors = buttonColors(
                            Color.White, Color.Black, Color.Blue, Color.LightGray
                        ),
                        border = BorderStroke(1.dp, Color.DarkGray),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.facebook_brands_solid),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(5))
                                .size(30.dp)
                                .padding(0.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Facebook")
                    }

                }
                // Column for "Don't have an account?" and "register" button
                Row(
                    modifier = Modifier.padding(top = 30.dp),
                    horizontalArrangement = Arrangement.Center, // Center buttons horizontally
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account?",
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    TextButton(
                        onClick = { navController.navigate("register") },
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 18.sp,
                            color = Color.Blue,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }


        }

    }
}