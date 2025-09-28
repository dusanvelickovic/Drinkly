package com.example.drinkly.ui.venue_review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drinkly.data.model.Review
import com.example.drinkly.ui.venue.VenueReviewViewModel
import com.example.drinkly.ui.theme.AppColorBg
import com.example.drinkly.ui.theme.AppColorBorder
import com.example.drinkly.ui.theme.AppColorOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueReviewScreen(
    venueId: String?,
    onBackClick: () -> Unit = { },
    venueReviewViewModel: VenueReviewViewModel = viewModel(),
) {
    val reviews by venueReviewViewModel.reviewsFlow.collectAsState(initial = Result.success(emptyList()))
    LaunchedEffect(venueId) {
        venueId?.let {
            venueReviewViewModel.observeReviewsForVenue(it)
        }
    }

    // State za prikaz forme za dodavanje recenzije
    var showReviewForm by remember { mutableStateOf(false) }
    var newReviewRating by remember { mutableStateOf(5) }
    var newReviewTitle by remember { mutableStateOf("") }
    var newReviewComment by remember { mutableStateOf("") }

    fun resetFormAndClose() {
        newReviewTitle = ""
        newReviewComment = ""
        newReviewRating = 5
        showReviewForm = false
    }

    val coroutineScope = rememberCoroutineScope()

    fun handleSubmitReview() {
        venueId?.let {
            coroutineScope.launch {
                venueReviewViewModel.submitReview(it, newReviewTitle, newReviewComment, newReviewRating)
                resetFormAndClose()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorBg)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Reviews",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Add Review Button
                if (!showReviewForm) {
                    Button(
                        onClick = { showReviewForm = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColorOrange
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "Write a Review",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // Forma za dodavanje recenzije
            if (showReviewForm) {
                item {
                    // Review Form
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5f.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Leave a Review",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            // Rating
                            Column {
                                Text(
                                    text = "Rating",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = if (index < newReviewRating) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = "Star ${index + 1}",
                                            tint = if (index < newReviewRating) AppColorOrange else Color.LightGray,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable { newReviewRating = index + 1 }
                                        )
                                    }
                                }
                            }

                            // Title
                            Column {
                                Text(
                                    text = "Review Title",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                OutlinedTextField(
                                    value = newReviewTitle,
                                    onValueChange = { newReviewTitle = it },
                                    placeholder = { Text("Give your review a title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AppColorBorder,
                                        cursorColor = AppColorBorder,
                                        unfocusedBorderColor = AppColorBorder,
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                )
                            }

                            // Comment
                            Column {
                                Text(
                                    text = "Your Review",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                OutlinedTextField(
                                    value = newReviewComment,
                                    onValueChange = { newReviewComment = it },
                                    placeholder = { Text("Tell others about your experience...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AppColorBorder,
                                        cursorColor = AppColorBorder,
                                        unfocusedBorderColor = AppColorBorder,
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                )
                            }

                            // Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showReviewForm = false },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.DarkGray,
                                        containerColor = Color.White,
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = AppColorBorder
                                    )
                                ) {
                                    Text("Cancel")
                                }

                                Button(
                                    onClick = { handleSubmitReview() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AppColorOrange
                                    )
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Submit Review")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Prikaz recenzije
            reviews.onSuccess { reviews ->
                // Reviews List
                items(reviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5f.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9DB2BF))
                    )

                    Column {
                        Text(
                            text = (review.user["name"]?.toString() ?: "") + " - " + review.getDateFormatted(),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = review.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Rejting
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (index < review.rating) AppColorOrange else Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Review comment
            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(start = 52.dp)
            )
        }
    }
}