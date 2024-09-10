@file:Suppress("PackageName")

package com.example.skyhigh_prototype.View
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skyhigh_prototype.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPassword(navController: NavController){

    //form variables
    var email by remember {
        mutableStateOf("")
    }

    //firebase instance
    var auth: FirebaseAuth

    //Error states
    var emailError by remember { mutableStateOf("") }


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

                //val context for toast message
                val context = LocalContext.current

                //form input fields
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Please note an email will be sent to your\nEmail inbox or Spam folder from no-reply\nContaining a link to reset password", fontSize = 18.sp, modifier = Modifier.padding(10.dp, 0.dp))
                Spacer(modifier = Modifier.height(40.dp))
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

                // Reset Password Button
                Button(
                    onClick = {

                        // Validate all input fields before proceeding
                        emailError = ValidateForms.validateEmail(email)

                        //initialising auth
                        auth = FirebaseAuth.getInstance()

                        // If all validations pass, navigate to the login screen
                        if (emailError.isEmpty()) {

                            //using firebase method to send user email to reset
                            auth.sendPasswordResetEmail(email).addOnSuccessListener {

                                //to alert user
                                Toast.makeText(context, "An email was sent to $email\nYou will be shortly redirected to Login Page", Toast.LENGTH_LONG).show()

                                //handler to delay intent
                                @Suppress("DEPRECATION")
                                android.os.Handler().postDelayed({
                                    //navigating to login
                                    navController.navigate("login")

                                }, 2000)

                            }.addOnFailureListener {

                                //to alert user
                                Toast.makeText(context, "Email $email does not exit in our database", Toast.LENGTH_LONG).show()

                            }


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
}
