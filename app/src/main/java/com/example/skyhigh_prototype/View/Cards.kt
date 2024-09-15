package com.example.skyhigh_prototype.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.skyhigh_prototype.Intent.BirdObservation

@Composable
fun BirdObservationItem(observation: BirdObservation) {
    Card(
        //elevation = 4.dp,
        shape = RoundedCornerShape(12.dp), // Rounded corners for the card
        colors = CardColors(
            contentColor = Color.Black,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.DarkGray ,
            containerColor = Color(0xFFEDE7F6)
        ),
        //backgroundColor = Color(0xFFEDE7F6), // Light purple background
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Vertical padding between cards
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Padding inside the card
        ) {
            Text(
                text = "Common Name: ${observation.comName}",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color(0xFF4A148C) // Darker purple for the title
                )
            )
            Spacer(modifier = Modifier.height(4.dp)) // Space between text lines
            Text(
                text = "Scientific Name: ${observation.sciName}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF6A1B9A) // Slightly lighter purple
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Location: ${observation.locName}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF8E24AA) // Even lighter purple
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: ${observation.obsDt}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFF9C27B0) // Light purple for label
                )
            )
        }
    }
}

