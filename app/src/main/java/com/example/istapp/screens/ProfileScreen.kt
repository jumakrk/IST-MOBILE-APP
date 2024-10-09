package com.example.istapp.screens

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
//noinspection ExifInterface
import android.media.ExifInterface
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.istapp.R
import com.google.firebase.auth.FirebaseAuth
import java.io.InputStream

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val username = getUsername() // Get the user's username from Firebase
    val email = "${FirebaseAuth.getInstance().currentUser?.email}"
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image Picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Ensure spacing between items
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture Section
                Box(
                    contentAlignment = Alignment.BottomEnd, // Align "+" icon to bottom-right corner of profile picture
                    modifier = Modifier
                        .size(120.dp)
                ) {
                    if (selectedImageUri != null) {
                        // Handle the input stream and adjust orientation if necessary
                        val inputStream: InputStream? = context.contentResolver.openInputStream(selectedImageUri!!)
                        inputStream?.let {
                            val bitmap = BitmapFactory.decodeStream(it)

                            // Reset stream for ExifInterface
                            it.reset()

                            // Fix image orientation
                            val exifInterface = ExifInterface(it)
                            val orientation = exifInterface.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
                            )

                            val rotatedBitmap = when (orientation) {
                                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap?.rotate(90f)
                                ExifInterface.ORIENTATION_ROTATE_180 -> bitmap?.rotate(180f)
                                ExifInterface.ORIENTATION_ROTATE_270 -> bitmap?.rotate(270f)
                                else -> bitmap
                            }

                            rotatedBitmap?.let { bmp ->
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        // Default profile picture
                        Image(
                            painter = painterResource(id = R.drawable.ist_logo),
                            contentDescription = "Default Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // "+" Button to select new image
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable {
                                launcher.launch("image/*")
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.AddCircle,
                            contentDescription = "Add Profile Picture",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Username and Email Section
                Text(text = "Username: $username", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Email: $email", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // Delete Account Button at the bottom
            Button(
                onClick = {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        user.delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Show a success message and sign out
                                    Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                    // Redirect to login screen or another appropriate screen
                                } else {
                                    // Handle failure (e.g., user may need to re-authenticate)
                                    Toast.makeText(context, "Failed to delete account: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "No user is logged in", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Delete Account", color = Color.White)
            }
        }
    }
}

// Helper function to get current username from Firebase
fun getUsername(): String {
    val user = FirebaseAuth.getInstance().currentUser
    return user?.displayName ?: "Unknown User"
}

// Helper function to rotate the bitmap
fun android.graphics.Bitmap.rotate(degrees: Float): android.graphics.Bitmap {
    val matrix = android.graphics.Matrix().apply { postRotate(degrees) }
    return android.graphics.Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
