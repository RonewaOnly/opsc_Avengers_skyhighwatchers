@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View


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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skyhigh_prototype.R

@Composable
fun Login(navController: NavController) {

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

    //column for page
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                    .padding(top = 30.dp),
                painter = painterResource(id = R.drawable.sky_high_watchers_logo),
                contentDescription = "logo"

            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Hi, Welcome Back...",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(20.dp, 0.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // box 2
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color.White)
        ) {

            //email input remaining
            OutlinedTextField(value = username, onValueChange = { username = it }, label = {
                Text(text = "Email")
            },
                modifier = Modifier
                    .width(400.dp)
                    .padding(10.dp, 0.dp)
            )

            //password input field
            OutlinedTextField(value = password,
                onValueChange = { password = it },
                label = {
                    Text(text = "Password")
                },
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
                    .padding(10.dp, 80.dp)
            )


            //box for remember me and forgot password
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Make the Box fill the entire width of the screen
                    .padding(top = 140.dp) // Add top padding to position the elements as needed
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

            //button to login
            Button(
                onClick = { navController.navigate("homepage") },
                shape = RoundedCornerShape(size = 10.dp),
                modifier = Modifier
                    .padding(70.dp, 220.dp, 0.dp, 0.dp)
                    .width(250.dp)
                    .height(50.dp)

            ) {
                Text(text = "Login")
            }

            HorizontalDivider(modifier = Modifier.padding(0.dp, 300.dp, 0.dp, 0.dp))

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(200.dp))

                Text(
                    text = "or continue with",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp) // Adjusted padding
                )

                //row for social media buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // Optional padding to center the buttons within the column
                    horizontalArrangement = Arrangement.Center, // Center buttons horizontally
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Google account button
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
                // Column for "Don't have an account?" and "Register" button
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