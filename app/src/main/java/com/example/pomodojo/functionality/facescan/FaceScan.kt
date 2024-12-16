package com.example.pomodojo.functionality.facescan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pomodojo.R
import com.example.pomodojo.functionality.spotify.SpotifyPlaylist
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceScan : ComponentActivity() {

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    onGalleryImageSelected(it)
                }
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val capturedImage: Bitmap? = result.data?.extras?.get("data") as? Bitmap
                capturedImage?.let {
                    onCameraImageCaptured(it)
                }
            }
        }

    private var selectedBitmap: Bitmap? by mutableStateOf(null)
    private var detectedEmotion: String by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FaceScanScreen(
                selectedBitmap = selectedBitmap,
                onGalleryClick = {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryLauncher.launch(galleryIntent)
                },
                onCameraClick = {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraLauncher.launch(cameraIntent)
                },
                onAnalyzeClick = {
                    selectedBitmap?.let { detectFaces(it) } ?: run {
                        Toast.makeText(this, "Please select or capture an image first!", Toast.LENGTH_SHORT).show()
                    }
                },
                detectedEmotion = detectedEmotion
            )
        }
    }

    private fun onGalleryImageSelected(uri: Uri) {
        val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(this.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }
        selectedBitmap = bitmap
    }

    private fun onCameraImageCaptured(bitmap: Bitmap) {
        selectedBitmap = bitmap
    }

    private fun detectFaces(bitmap: Bitmap) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val image = InputImage.fromBitmap(bitmap, 0)
        val detector = FaceDetection.getClient(options)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    Toast.makeText(this, "No faces detected!", Toast.LENGTH_SHORT).show()
                } else {
                    for (face in faces) {
                        val smileProb = face.smilingProbability ?: -1f
                        detectedEmotion = when {
                            smileProb > 0.7f -> "Happy"
                            smileProb in 0.3f..0.7f -> "Neutral"
                            else -> "Sad"
                        }

                        Toast.makeText(this, "Detected Emotion: $detectedEmotion", Toast.LENGTH_SHORT).show()

                        // Create intent and send the detected emotion to SpotifyPlaylist activity
                        val intent = Intent(this, SpotifyPlaylist::class.java).apply {
                            putExtra("DETECTED_EMOTION", detectedEmotion)
                        }
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Face detection failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}

@Composable
fun FaceScanScreen(
    selectedBitmap: Bitmap?,
    detectedEmotion: String?,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onAnalyzeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F3E46))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Analyze your face!",
            color = Color.White,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color(0xFF52796E), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            selectedBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(250.dp)
                )
            } ?: AsyncImage(
                model = R.drawable.selfie,
                contentDescription = "Placeholder Image",
                modifier = Modifier.size(200.dp)
            )
        }

        Text(
            text = detectedEmotion ?: "Show us your face and generate a Spotify playlist based on your mood!",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        Button(
            onClick = onAnalyzeClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCAD2C5),
                contentColor = Color(0xFF2F3E46)
            )
        ) {
            Text(text = "Analyze Image")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onGalleryClick,
                modifier = Modifier.size(width = 150.dp, height = 50.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCAD2C5),
                    contentColor = Color(0xFF2F3E46)
                )
            ) {
                Text(text = "Gallery")
            }

            Button(
                onClick = onCameraClick,
                modifier = Modifier.size(width = 150.dp, height = 50.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCAD2C5),
                    contentColor = Color(0xFF2F3E46)
                )
            ) {
                Text(text = "Camera")
            }
        }
    }
}