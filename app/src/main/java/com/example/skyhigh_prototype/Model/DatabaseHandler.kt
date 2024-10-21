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
import com.example.skyhigh_prototype.Data.Achievement
import com.example.skyhigh_prototype.Data.BirdFeed
import com.example.skyhigh_prototype.Data.BirdTip
import com.example.skyhigh_prototype.Data.Birds
import com.example.skyhigh_prototype.Data.CustomArea
import com.example.skyhigh_prototype.Data.Reports
import com.example.skyhigh_prototype.Data.Settings
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

            //val hashed = hashPassword(password)
            val user = UserDetails(firstname,lastname,email);
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
    //updating the setting
    fun updateSettings(
        notification: Boolean = false,
        unitOfDistance: Boolean = true,
        language: String = "en",
        theme: Int = R.string.switch_to_light_mode,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        // Create a settings map to update Firestore
        val settingsMap = hashMapOf(
            "notification" to notification,
            "distance" to unitOfDistance,
            "languageRange" to language,
            "theme" to theme
        )

        // Update Firestore with the structured settings map
        db.collection("Users").document(userID)
            .update("settings", settingsMap)
            .addOnSuccessListener {
                onSuccess("Updated successfully :)")
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
    fun createCustomArea(areaName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        // Create a new custom area with a unique areaId
        val newArea = CustomArea(
            areaId = db.collection("Users").document(userID).collection("CustomAreas").document().id,
            name = areaName
        )
        // Add the new area to the customArea array using FieldValue.arrayUnion
        db.collection("Users").document(userID)
            .update("customArea", FieldValue.arrayUnion(newArea))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }


    fun updateCustomArea(areaId: String, name: String, locationDesc: String, sightings: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        // Retrieve the current custom areas for the user
        db.collection("Users").document(userID).get()
            .addOnSuccessListener { document ->
                val customAreas = document.get("customArea") as? List<Map<String, Any?>>
                if (customAreas != null) {
                    // Convert the list of maps back to CustomArea objects
                    val updatedAreas = customAreas.map { areaMap ->
                        if (areaMap["areaId"] == areaId) {
                            CustomArea(
                                areaId = areaId,
                                name = name,
                                locationDescription = locationDesc,
                                sightingsInArea = sightings
                            )
                        } else {
                            CustomArea(
                                areaId = areaMap["areaId"] as String?,
                                name = areaMap["name"] as String?,
                                locationDescription = areaMap["locationDescription"] as String?,
                                sightingsInArea = (areaMap["sightingsInArea"] as? Long)?.toInt() ?: 0
                            )
                        }
                    }

                    // Update the customArea array with the modified areas
                    db.collection("Users").document(userID)
                        .update("customArea", updatedAreas)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { error ->
                            onFailure(error)
                        }
                } else {
                    onFailure(Exception("No custom areas found"))
                }
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun fetchCustomAreas(onSuccess: (List<CustomArea>) -> Unit, onFailure: (Exception) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        // Get the user document from Firestore
        db.collection("Users").document(userID).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Fetch the customArea field
                    val customAreas = document.get("customArea") as? List<Map<String, Any?>>

                    if (customAreas != null) {
                        // Map each area in Firestore to a CustomArea object
                        val areasList = customAreas.map { areaMap ->
                            CustomArea(
                                areaId = areaMap["areaId"] as String?,
                                name = areaMap["name"] as String?,
                                locationDescription = areaMap["locationDescription"] as String?,
                                sightingsInArea = (areaMap["sightingsInArea"] as? Long)?.toInt() ?: 0
                            )
                        }
                        // Pass the list of CustomArea objects to the success callback
                        onSuccess(areasList)
                    } else {
                        onSuccess(emptyList())  // If no custom areas, return an empty list
                    }
                } else {
                    onFailure(Exception("User document does not exist"))
                }
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
    fun fetchUserDetails(onSuccess: (UserDetails) -> Unit, onFailure: (Exception) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        // Get the user document from Firestore
        db.collection("Users").document(userID).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Convert Firestore data to a UserDetails object
                    val userDetails = UserDetails(
                        firstname = document.getString("firstname") ?: "",
                        lastname = document.getString("lastname") ?: "",
                        email = document.getString("email") ?: "",
                        bio = document.getString("bio") ?: "",
                        location = document.getString("location") ?: "",
                        profilePic = document.getString("profilePic") ?: "",

                        // Convert settings map back to Settings object
                        settings = listOf(
                            document.get("settings")?.let { settingsMap ->
                                if (settingsMap is Map<*, *>) {
                                        Settings(
                                        notification = settingsMap["notification"] as? Boolean ?: false,
                                        distance = settingsMap["distance"] as? Boolean ?: true,
                                        languageRange = settingsMap["languageRange"] as? String ?: "en",
                                        theme = settingsMap["theme"] as? Int ?: R.string.switch_to_light_mode
                                    )
                                } else null
                            }
                        ),

                        // Convert customArea array back to a list of CustomArea objects
                        customArea = (document.get("customArea") as? List<Map<String, Any?>>)?.map { areaMap ->
                            CustomArea(
                                areaId = areaMap["areaId"] as String?,
                                name = areaMap["name"] as String?,
                                locationDescription = areaMap["locationDescription"] as String?,
                                sightingsInArea = (areaMap["sightingsInArea"] as? Long)?.toInt() ?: 0
                            )
                        } ?: emptyList(),
                        sightingsCount = 0.0,
                        // Convert favoriteBirds array back to a list of strings
                        favoriteBirds = document.get("favoriteBirds") as? List<String> ?: emptyList(),

                        // Convert reports array back to a list of Reports objects
                        reports = (document.get("reports") as? List<Map<String, Any?>>)?.map { reportMap ->
                            Reports(
                                reportId = reportMap["reportId"] as String?, // Assuming the Reports class has this structure
                                reportDescription  = reportMap["description"] as String?,
                                reportName = reportMap["reportName"] as String?,
                                reportDate = reportMap["reportDate"] as Timestamp
                            )
                        } ?: emptyList(),

                        // Convert achievements array back to a list of Achievement objects
                        achievements = (document.get("achievements") as? List<Map<String, Any?>>)?.map { achievementMap ->
                            Achievement(
                                name = achievementMap["name"] as String?,
                                description = achievementMap["description"] as String?
                            )
                        } ?: emptyList()
                    )

                    // Return the UserDetails object to the success callback
                    onSuccess(userDetails)

                } else {
                    onFailure(Exception("User document does not exist"))
                }
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
    fun createReport(reportName: String, reportDescription: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        // Generate a unique report ID
        val reportId = db.collection("Users").document(userID).collection("Reports").document().id

        // Create a new Reports object
        val newReport = Reports(
            reportId = reportId,
            reportName = reportName,
            reportDescription = reportDescription
        )

        // Send the new report to Firestore
        db.collection("Users").document(userID)
            .update("reports", FieldValue.arrayUnion(newReport))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
    fun fetchReports(onSuccess: (List<Reports>) -> Unit, onFailure: (Exception) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid.toString()

        // Get the user's reports array from Firestore
        db.collection("Users").document(userID).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Extract the reports array and map it to a list of Reports objects
                    val reportsList = (document.get("reports") as? List<Map<String, Any?>>)?.map { reportMap ->
                        Reports(
                            reportId = reportMap["reportId"] as? String ?: "",
                            reportName = reportMap["reportName"] as? String ?: "",
                            reportDescription = reportMap["reportDescription"] as? String ?: "",
                            reportDate = reportMap["reportDate"] as? Timestamp ?: Timestamp.now()
                        )
                    } ?: emptyList()

                    // Return the list of reports to the success callback
                    onSuccess(reportsList)

                } else {
                    onFailure(Exception("User document does not exist"))
                }
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }


    // Hash password using SHA-256
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("-") { "%02x".format(it) }
    }
}