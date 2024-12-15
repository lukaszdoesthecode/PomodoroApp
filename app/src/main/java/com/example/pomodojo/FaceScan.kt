package com.example.pomodojo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class FaceScan : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var textView: TextView

    private val clientId = "2cec84f1e66042ccb6ad5336208f31d4"
    private val clientSecret = "8fd83354b1824cf7b2c6f55cc11188ff"
    private val redirectUri = "myapp://callback"
    private val tokenEndpoint = "https://accounts.spotify.com/api/token"
    private var accessToken: String? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    imageView.setImageURI(it)
                    bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(this.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                    }
                }
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val capturedImage: Bitmap? = result.data?.extras?.getParcelable("data")
                capturedImage?.let {
                    imageView.setImageBitmap(it)
                    bitmap = it
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_scan)

        imageView = findViewById(R.id.imageView)
        val galleryBtn: Button = findViewById(R.id.galleryBtn)
        val cameraBtn: Button = findViewById(R.id.cameraBtn)
        val analyzeBtn: Button = findViewById(R.id.analyzeBtn)
        textView = findViewById(R.id.textView)

        galleryBtn.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

        cameraBtn.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }

        analyzeBtn.setOnClickListener {
            if (::bitmap.isInitialized) {
                detectFaces(bitmap)
            } else {
                Toast.makeText(this, "Please select or capture an image first!", Toast.LENGTH_SHORT).show()
            }
        }

        handleRedirect()
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
                        val emotion = when {
                            smileProb > 0.7f -> "Happy"
                            smileProb in 0.3f..0.7f -> "Neutral"
                            else -> "Sad"
                        }

                        textView.text = "Emotion Detected: $emotion"

                        val intent = Intent(this, SpotifyPlaylist::class.java)
                        intent.putExtra("DETECTED_EMOTION", emotion)
                        startActivity(intent)

                        finish()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Face detection failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun handleRedirect() {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "myapp" && data.host == "callback") {
            val code = data.getQueryParameter("code")
            val error = data.getQueryParameter("error")

            if (code != null) {
                exchangeCodeForToken(code)
            } else if (error != null) {
                Toast.makeText(this, "Error during Spotify authentication: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exchangeCodeForToken(code: String) {
        val clientCredentials = "$clientId:$clientSecret"
        val encodedCredentials = Base64.encodeToString(clientCredentials.toByteArray(), Base64.NO_WRAP)

        val requestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", redirectUri)
            .build()

        val request = Request.Builder()
            .url(tokenEndpoint)
            .addHeader("Authorization", "Basic $encodedCredentials")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@FaceScan, "Failed to get token: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    accessToken = json.getString("access_token")
                    runOnUiThread {
                        Toast.makeText(this@FaceScan, "Spotify connected!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
