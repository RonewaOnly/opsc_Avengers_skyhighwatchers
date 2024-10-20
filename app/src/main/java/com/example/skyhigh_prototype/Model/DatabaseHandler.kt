package com.example.skyhigh_prototype.Model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.google.firebase.Timestamp
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.NavController
import com.example.skyhigh_prototype.Data.BirdFeed
import com.example.skyhigh_prototype.Data.BirdTip
import com.example.skyhigh_prototype.Data.Birds
import com.example.skyhigh_prototype.Data.UserDetails
import com.example.skyhigh_prototype.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.security.MessageDigest
import java.util.UUID

class DatabaseHandler{
    val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient

    // Initialize Google Sign-In
    fun initGoogleSignIn(context: Context) {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // From google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        auth = FirebaseAuth.getInstance()
    }

    // Start Google Sign-In Intent
    fun signInWithGoogle(activity: Activity, signInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    // Handle Google Sign-In Result
    fun handleSignInResult(data: Intent?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account.idToken!!, onSuccess, onError)
            }
        } catch (e: ApiException) {
            onError("Google sign-in failed")
            e.message?.let { Log.e("Google sign-in failed:", it) }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Firebase authentication failed")
                }
            }
    }
    fun Login(email: String,password: String,context: Context, navController: NavController ,onSuccess: () -> Unit,onError: (String) -> Unit){

        //using firebase auth method to sign in a user
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

            //to alert user
            Toast.makeText(context, "Successful Login you will be shortly redirected to your dashboard", Toast.LENGTH_LONG).show()
            //handler to delay intent
            @Suppress("DEPRECATION")
            Handler().postDelayed({
                //navigating to home page
                navController.navigate("homepage")
            }, 2000)
            onSuccess()
        }.addOnFailureListener {
            onError("Email or Password Incorrect")
        }
    }

    fun register(firstname:String, lastname:String, email: String, password: String, navController: NavController, context:Context){
//using firebase auth to create a user with authentication
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

            //get current user id
            val userID = auth.currentUser?.uid

            //using hash map to store user details to firestore database
//            val user = hashMapOf(
//                "First Name" to  firstname,
//                "Last Name" to lastname,
//                "Email" to email
//
//            )

            val hashed = hashPassword(password)
            val user = UserDetails(firstname,lastname,email,hashed);
            //creating user with id
            if(userID != null){
                //adding user to collection
                db.collection("Users").document(userID).set(user).addOnFailureListener {
                    //to alert user
                    Toast.makeText(context,"Unable to save user details to database", Toast.LENGTH_LONG).show()
                }
            }
            //alert user
            Toast.makeText(context,"Successful Account Creation\nYou will be redirected to Login Page", Toast.LENGTH_LONG).show()

            //to delay intent
            @Suppress("DEPRECATION")
            Handler().postDelayed({

                //navigate to login
                navController.navigate("login")

            }, 2000)


        }.addOnFailureListener {

            //to alert user
            Toast.makeText(context,"Email already exit", Toast.LENGTH_LONG).show()

        }
    }
    fun createCollection(birdTip: BirdTip,context: Context){
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()
        birdTip.card_id = db.collection("Bird Collection").document().id//will be generating for the Cards collection for the specific breeds or group of birds the user will be looking for.
        db.collection("Users").document(userID).collection("Bird Collection")
            .add(birdTip)
            .addOnSuccessListener {
                Toast.makeText(context, "Collection saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error saving collection: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun addObservation(card_id: String, newObservation: Birds, context: Context, onSave: () -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid

        // Check if user is authenticated
        if (userID != null) {
            // Use arrayUnion to add new observation to the 'content' array
            db.collection("Users").document(userID).collection("Bird Collection")
                .document(card_id)
                .update("content", FieldValue.arrayUnion(newObservation))  // Appends the new observation
                .addOnSuccessListener {
                    Toast.makeText(context, "Observation added successfully!", Toast.LENGTH_SHORT).show()
                    onSave()  // Callback to refresh or close after save
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to add observation: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
    // Function to update bird observation with image and video
    fun updateCardWithObservation(
        cardId: String,
        bird: Birds,
        imageUri: Uri?,
        videoUri: Uri?,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Handle Image Upload (if provided)
        if (imageUri != null) {
            val imageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        bird.images = listOf(imageUrl.toString()) // Update bird with image URL
                        uploadObservation(cardId, bird,context ,onSuccess, onFailure)
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }

        // Handle Video Upload (if provided)
        if (videoUri != null) {
            val videoRef = storage.reference.child("videos/${UUID.randomUUID()}.mp4")
            videoRef.putFile(videoUri)
                .addOnSuccessListener {
                    videoRef.downloadUrl.addOnSuccessListener { videoUrl ->
                        bird.videos = listOf(videoUrl.toString()) // Update bird with video URL
                        uploadObservation(cardId, bird,context ,onSuccess, onFailure)
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }

        // If no image or video, directly upload the bird observation
        if (imageUri == null && videoUri == null) {
            uploadObservation(cardId, bird,context ,onSuccess, onFailure)
        }
    }

    // Function to upload observation to Firestore
    private fun uploadObservation(
        cardId: String,
        bird: Birds,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("Bird Collection").document(cardId)
            .update("content", FieldValue.arrayUnion(bird))
            .addOnSuccessListener {
                Toast.makeText(context, "Observation added successfully!", Toast.LENGTH_SHORT).show()
                onSuccess() // Trigger callback
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to add observation: ${exception.message}", Toast.LENGTH_SHORT).show()
                onFailure(exception) // Trigger error callback
            }
    }
    //this function will be used for the personalCollection page only has on that will be fetching and displaying the current cards created.
    fun fetchCards(onSuccess: (List<BirdTip>) -> Unit, onError: (String) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        // Fetch user's bird collection
        db.collection("Users").document(userID).collection("Bird Collection")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val cards = querySnapshot.map { document ->
                    val birdTip = document.toObject(BirdTip::class.java)
                    birdTip.card_id = document.id // Assign the document ID (card_id)
                    birdTip
                }
                onSuccess(cards) // Return the list of BirdTips with card_id
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Unknown error occurred")
            }
    }

    // Function to fetch collection details
    fun fetchCollection(onSuccess: (List<Birds>) -> Unit, onFailure: (Exception) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        db.collection("Users").document(userID).collection("Bird Collection")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Create a mutable list to store the birds
                val allBirds = mutableListOf<Birds>()

                // Loop through each document in the "Bird Collection"
                for (document in querySnapshot.documents) {
                    // Extract the "content" field (assuming it's a list of Birds objects)
                    val content = document.get("content") as? List<Map<String, Any>>

                    // Map the content to Birds objects
                    content?.let {
                        val birdsList = content.map { birdData ->
                            // Create a Birds object from each map entry in the content list
                            val bird = Birds(
                                bird_name = birdData["bird_name"] as? String ?: "",
                                bird_species = birdData["bird_species"] as? List<String> ?: emptyList(),
                                gender = birdData["gender"] as? String ?: "",
                                color = birdData["color"] as? List<String> ?: emptyList(),
                                location = birdData["location"] as? String ?: "",
                                timestamp = birdData["timestamp"] as? Timestamp ?: Timestamp.now(),
                                images = birdData["images"] as? List<String> ?: emptyList(),
                                videos = birdData["videos"] as? List<String> ?: emptyList(),
                                feed = (birdData["feed"] as? List<Map<String, Any>>)?.map { feedData ->
                                    BirdFeed(
                                        feed_name = feedData["feed_name"] as? String ?: "",
                                        feed_grown = feedData["feed_grown"] as? String ?: ""
                                    )
                                } ?: emptyList(),
                                bird_description = birdData["bird_description"] as? List<String> ?: emptyList(),
                                relatedSpecies = emptyList(), // You can add handling for relatedSpecies if needed
                                hotspots = emptyList() // Add handling for hotspots if needed
                            )
                            bird // Add this bird to the list
                        }
                        // Add this list of birds to the overall list
                        allBirds.addAll(birdsList)
                    }
                }

                // Return the list of Birds through the success callback
                onSuccess(allBirds)
            }
            .addOnFailureListener { exception ->
                // Handle errors in case of failure
                onFailure(exception)
            }
    }


    // Function to update Firestore document
    fun updateFirestoreCard(
        cardId: String,
        field: String,
        value: Any,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("Bird Collection").document(cardId)
            .update(field, value)
            .addOnSuccessListener {
                onSuccess() // Trigger callback on success
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Trigger error callback
            }
    }

    // Function to delete an observation from a card
    fun deleteObservation(cardId: String, bird: Birds,context: Context, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Bird Collection").document(cardId)
            .update("content", FieldValue.arrayRemove(bird))
            .addOnSuccessListener {
                Toast.makeText(context, "Observation deleted successfully!", Toast.LENGTH_SHORT).show()
                onSuccess() // Trigger callback on success
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to delete observation: ${exception.message}", Toast.LENGTH_SHORT).show()
                onFailure(exception) // Trigger error callback
            }
    }
    // Hash password using SHA-256
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("-") { "%02x".format(it) }
    }
}