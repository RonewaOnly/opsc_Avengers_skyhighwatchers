package com.example.skyhigh_prototype.View

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.skyhigh_prototype.Data.BirdTip
import com.example.skyhigh_prototype.Data.Birds
import com.example.skyhigh_prototype.Model.CameraApp
import com.example.skyhigh_prototype.Model.CameraPreviewScreen
import com.example.skyhigh_prototype.Model.CaptureScreen
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.example.skyhigh_prototype.Model.LocationScreen
import com.example.skyhigh_prototype.Model.LocationViewModel
import com.example.skyhigh_prototype.Model.currentLocations
import com.example.skyhigh_prototype.R


val DatabaseClass = DatabaseHandler()//this the global calling of the object for the class handling the process through the firestore

@Composable
fun PersonalCollection() {
    val onClick = remember { mutableStateOf(false) }
    var newObject by remember { mutableStateOf(listOf<BirdTip>()) }
    var editButton by remember { mutableStateOf(false) }
    var selectedCardId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        DatabaseClass.fetchCards(onSuccess = { card ->
            newObject = card
        }, onError = {
            Toast.makeText(context, it,Toast.LENGTH_SHORT).show()
        })
        Log.d("The cards fetched: ", "${newObject.size}")
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .height(2500.dp)
            .padding(start = 10.dp, top = 120.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .height(290.dp)
                    .border(1.dp, Color.Blue, RectangleShape)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = { onClick.value = true }, shape = CircleShape, modifier = Modifier.size(70.dp)) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        }

        items(newObject) { item ->
            ViewCard(tips = listOf(item), editButton={
                editButton = true
                selectedCardId = item.card_id
            })
            Spacer(modifier = Modifier.padding(12.dp))
        }

    }

    if (onClick.value) {
        CreateCard(
            onClose = { onClick.value = false },
            onSave = { savedTip ->
                newObject = newObject + savedTip
                onClick.value = false
            }
        )
    }
    if (editButton && selectedCardId != null) {
        EditCard(card_id = selectedCardId!!, card_details = newObject, DatabaseClass, context)
        Log.d("The clicked card: ", selectedCardId!!)
    }
    Log.d("The cards fetched after: ", "${newObject.size}")
}

@Composable
fun CreateCard(onClose: () -> Unit, onSave: (BirdTip) -> Unit) {
    var cardName by remember { mutableStateOf("") }
    var cardDescription by remember { mutableStateOf("") }
    var birdType by remember { mutableStateOf("") }
    //firebase instances
    //    var auth : FirebaseAuth
    //    var firestore: FirebaseFirestore
    val context = LocalContext.current
    Dialog(onDismissRequest = { onClose() }) {
        Surface(
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { onClose() }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text(text = "Enter Collection Name") },
                    modifier = Modifier.padding(top = 10.dp)
                )
                TextField(
                    value = cardDescription,
                    onValueChange = { cardDescription = it },
                    label = { Text(text = "Enter Collection Description") },
                    modifier = Modifier.padding(top = 20.dp)
                )
                TextField(
                    value = birdType,
                    onValueChange = { birdType = it },
                    label = { Text(text = "Enter Bird Type") },
                    modifier = Modifier.padding(top = 20.dp)
                )
                OutlinedButton(onClick = {
// We will be using an object reference to call function dealing with the storing to the database.
                DatabaseClass.createCollection(birdTip =
                BirdTip(
                    card_name = cardName,
                    card_description = cardDescription,
                    card_category = birdType
                ), context = context, )
//                    //initialize
//                    auth = FirebaseAuth.getInstance()
//                    firestore = FirebaseFirestore.getInstance()
//
//                    //user id
//                    val userID = auth.currentUser?.uid.toString()
//
//                    //adding to hash map
//                    val birdCollection = hashMapOf(
//
//                        "Collection Name" to cardName,
//                        "Card Description" to cardDescription,
//                        "Bird Type" to birdType
//                    )
//
//                    //adding to firestore collection to a user with their id
//                    firestore.collection("Users").document(userID).collection("Bird Collection").add(birdCollection).addOnSuccessListener {
//                        //toast message
//                        Toast.makeText(context, "Save Collection", Toast.LENGTH_LONG).show()
//
//                    }.addOnFailureListener {
//                        //toast message
//                        Toast.makeText(context, "Unable to save collection", Toast.LENGTH_LONG).show()
//                    }

                    onSave(
                        BirdTip(
                            card_name = cardName,
                            card_description = cardDescription,
                            card_category = birdType
                        )
                    )
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Create")
                }
            }
        }
    }
}

@Composable
fun ViewCard(tips: List<BirdTip>, editButton: (Boolean)->Unit) {
    val context = LocalContext.current
    tips.forEach {
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .border(1.dp, Color.Blue)
                .width(200.dp)
                .height(290.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bird4),
                    contentDescription = "cover image",
                    modifier = Modifier
                        .clip(RectangleShape)
                        .fillMaxWidth(0.8f)
                        .height(100.dp),
                    contentScale = ContentScale.FillWidth
                )
                IconButton(onClick = {
                   editButton(true)
                }) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                }
            }

            Text(text = it.card_name, modifier = Modifier
                .fillMaxWidth()
                .height(30.dp))
            Text(text = it.card_description, modifier = Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCard(
    card_id: String,
    card_details: List<BirdTip>,
    databaseHandler: DatabaseHandler, // Injected DatabaseHandler
    context: Context
) {
    var birdName by remember { mutableStateOf("") }
    var birdDescription by remember { mutableStateOf("") }
    var mediaButton by remember { mutableStateOf(false) }
    var imageUri: Uri? by remember { mutableStateOf(null) } // To store selected image URI
    var videoUri: Uri? by remember { mutableStateOf(null) } // To store selected video URI

    // Find the card with the given ID
    val selectedCard = card_details.find { it.card_id == card_id }

    LazyColumn {
        // Your LazyColumn content goes here if needed
    }

    if (selectedCard != null) {
        // Initialize birds data for editing
        var birdObservation by remember { mutableStateOf(Birds()) } // Stores bird observation details

        // Main layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Enable scrolling
                .background(Color(0xFFF6F7FB)) // Light background
                .padding(16.dp)
        ) {
            // Top bar with navigation and actions
            TopAppBar(
                title = { Text(selectedCard.card_name, style = MaterialTheme.typography.titleSmall) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back navigation */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle more actions */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bird name input
            TextField(
                value = birdName,
                onValueChange = { birdName = it },
                label = { Text("Bird Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )

            // Bird description input
            TextField(
                value = birdDescription,
                onValueChange = { birdDescription = it },
                label = { Text("Bird Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Location section
            LocationScreen(viewModel = LocationViewModel())  // Assuming this is a custom composable that works

            Spacer(modifier = Modifier.height(16.dp))

            // Media button to select or capture image/video
            Button(
                onClick = { mediaButton = true }, // Toggle media options
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Camera", modifier = Modifier.padding(end = 8.dp))
                Text("Capture Media")
            }

            // Show Camera composable when media button is clicked (media options available)
            if (mediaButton) {
                // Conditionally show the camera capture UI
                CaptureScreen(
                    onImageCaptured = { uri ->
                        imageUri = uri
                    },
                    onVideoCaptured = { uri ->
                        videoUri = uri
                    },
                    onError = { error ->
                        Log.e("EditCard", "Error capturing media: $error")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button to update observations
            Button(
                onClick = {
                    val bird = Birds(
                        bird_name = birdName,
                        bird_description = listOf(birdDescription),
                        images = if (imageUri != null) listOf(imageUri.toString()) else emptyList(),
                        videos = if (videoUri != null) listOf(videoUri.toString()) else emptyList(),
                        location = currentLocations.toString()   // You can update it with actual data
                    )
                    // Call DatabaseHandler to save the observation
                    databaseHandler.addObservation(
                        card_id, bird,
                        context = context,
                        onSave = {
                            Toast.makeText(context, "Observation saved!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    if (imageUri != null || videoUri != null) {
                        databaseHandler.updateCardWithObservation(
                            cardId = card_id,
                            bird = bird,
                            imageUri = imageUri,
                            videoUri = videoUri,
                            context = context,
                            onSuccess = {
                                Toast.makeText(context, "Observation updated!", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { exception ->
                                Toast.makeText(context, "Failed to update observation: ${exception.message}", Toast.LENGTH_SHORT).show()
                                Log.e("Observation pages", "Failed to update observation: ${exception.message}")
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Save", modifier = Modifier.padding(end = 8.dp))
                Text("Save")
            }
        }
    } else {
        // Fallback if no card is found
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No information found", style = MaterialTheme.typography.bodySmall)
        }
    }
}

