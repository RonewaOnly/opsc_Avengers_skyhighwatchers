package com.example.skyhigh_prototype.View

//import com.example.skyhigh_prototype.CameraApp
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.skyhigh_prototype.Data.Location
import com.example.skyhigh_prototype.Model.CameraApp
import com.example.skyhigh_prototype.Model.DatabaseHandler
import com.example.skyhigh_prototype.Model.LocationScreen
import com.example.skyhigh_prototype.Model.LocationViewModel
import com.example.skyhigh_prototype.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val DatabaseClass = DatabaseHandler()//this the global calling of the object for the class handling the process through the firestore

@Composable
fun PersonalCollection() {
    val onClick = remember { mutableStateOf(false) }
    var newObject by remember { mutableStateOf(listOf<BirdTip>()) }
    var editButton by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        DatabaseClass.fetchCards(onSuccess = { card ->
            newObject = card
        }, onError = {
            Toast.makeText(context, it,Toast.LENGTH_SHORT).show();
        })

        Log.d("The cards fetched: ", "${newObject.size}");
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .height(2500.dp)
            .padding(top = 60.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .height(290.dp)
                    .border(1.dp, Color.Blue, RectangleShape),
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
    newObject.forEach {
        if (editButton) {
            it.card_id?.let { it1 ->
                EditCard(card_id = it1, card_details = newObject)
            }
        }
    }
    Log.d("The cards fetched after: ", "${newObject.size}");

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
                .padding(10.dp)
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

@Composable
fun EditCard(card_id: String, card_details: List<BirdTip>) {
    var enabled by remember { mutableStateOf(true) }
    var mediaButton by remember { mutableStateOf(false) }

    var birdName by remember { mutableStateOf("") }
    var birdDescription by remember { mutableStateOf("") }

    val bird_found = Location
    Log.d("In the edit screen: ", "${card_details.size}");

    card_details.forEach { tip ->
        if (tip.card_id == card_id) {
            Log.d("tHE ONE OPENED: ", tip.card_name);
            Log.d("tHE ONE OPENED WITH ID: ", tip.card_id!!);

            LazyColumn(
                modifier = Modifier
                    .height(500.dp)
                    .background(Color.White)
                    .padding(top = 60.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(Color.Red, RectangleShape),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween
                    ) {
                        IconButton(onClick = { /* TODO: Handle back navigation */ }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                        }
                        Text(text = tip.card_name+"Name")
                        IconButton(onClick = {  }) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                        }
                    }

                    TextField(value = birdName, onValueChange = {birdName = it}, label = { Text(text = "Enter bird's name") })
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    ) {
                        LazyRow {
                            item {
                                if (enabled) {
                                    InputChip(
                                        onClick = { enabled = !enabled },
                                        label = { Text("hi") },
                                        selected = enabled,
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Localized description",
                                                Modifier.size(InputChipDefaults.AvatarSize)
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                    TextField(value = birdDescription, onValueChange = {birdDescription = it}, label = { Text(text = "Enter bird description.") })
//                    bird_found.LATITUDE = "54°45′N"
//                    bird_found.LONGITUDE = "55°58′E"
                    //Text(text = "LATITUDE: ${bird_found.LATITUDE}, LONGITUDE: ${bird_found.LONGITUDE}")
                    LocationScreen(viewModel = LocationViewModel())
                    Button(onClick = { mediaButton = true }) {
                        Text(text = "Media button")
                    }
                    if (mediaButton) {
                        CameraApp()
                    }

                    Button(onClick = { /* TODO: Handle save */ }) {
                        Text(text = "Save")
                    }
                }
                }

        }else {
            Text(text = "No information found")
        }
    }
}
//
//// Main class that processes the information taken by the user
//data class BirdTip(
//    val card_id: String,
//    val card_name: String,
//    val card_description: String,
//    val card_category: String,
//    val card_cover_img: String? = R.drawable.bird2.toString(),
//    val content: List<Birds> = emptyList()
//)
//
//// Bird species card details class
//data class Birds(
//    val bird_name: String = "",
//    val bird_species: List<String> = emptyList(),
//    val gender: String = "",
//    val color: List<String> = emptyList(),
//    val location: String = "",
//    val timestamp: Timestamp = Timestamp.now(),
//    val images: List<String> = emptyList(),
//    val feed: List<BirdFeed> = emptyList(),
//    val bird_description: List<String> = emptyList(),
//    val relatedSpecies: List<Birds> = emptyList(),
//    val hotspots: List<Location> = emptyList()
//)
//
//// Object used to store the location where birds are found
//data object Location {
//    var LONGITUDE = ""
//    var LATITUDE = ""
//}
//
//// Data class for retrieving feed data
//data class BirdFeed(
//    val feed_id: Int = 0,
//    val feed_name: String,
//    val feed_grown: List<String> = emptyList(),
//    val description: List<String>,
//    val species_specific: List<String>,
//    val feed_images: List<String> = emptyList(),
//    val location: List<Location> = emptyList(),
//)
//
