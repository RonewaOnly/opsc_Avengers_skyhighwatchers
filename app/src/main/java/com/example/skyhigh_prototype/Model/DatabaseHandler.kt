package com.example.skyhigh_prototype.Model

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.skyhigh_prototype.Data.BirdTip
import com.example.skyhigh_prototype.Data.Birds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class DatabaseHandler{
    val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    lateinit var auth : FirebaseAuth


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
    fun addObservation(card_id:String,updatedContent:List<BirdTip>,context:Context,onSave: ()->Unit){
        db.collection("Bird Collection").document(card_id)
            .update("content", updatedContent)
            .addOnSuccessListener {
                Toast.makeText(context, "Observation added successfully!", Toast.LENGTH_SHORT).show()
                onSave()  // Callback to refresh or close after save
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add observation: ${it.message}", Toast.LENGTH_SHORT).show()
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
    fun fetchCollection(cardId: String, onSuccess: (List<Birds>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Bird Collection").document(cardId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val birdList = documentSnapshot.toObject(BirdTip::class.java)?.content ?: emptyList()
                onSuccess(birdList) // Trigger callback with fetched bird list
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Trigger error callback
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

}