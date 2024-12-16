package com.example.pomodojo.functionality.spotify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Log
import com.example.pomodojo.R

class SpotifyPlaylist : AppCompatActivity() {

    private val clientId = "2cec84f1e66042ccb6ad5336208f31d4"
    private val clientSecret = "8fd83354b1824cf7b2c6f55cc11188ff"
    private val redirectUri = "myapp://callback"
    private val authEndpoint = "https://accounts.spotify.com/authorize"
    private val tokenEndpoint = "https://accounts.spotify.com/api/token"
    private var accessToken: String? = null
    private var refreshToken: String? = null

    private var detectedEmotion: String? = null
    private var selectedGenre: String? = null
    private var selectedMood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_playlist)

        detectedEmotion = intent.getStringExtra("DETECTED_EMOTION")
        Toast.makeText(this, "Emotion Detected: $detectedEmotion", Toast.LENGTH_SHORT).show()

        accessToken = getAccessToken()
        refreshToken = getRefreshToken()

        val radioGroupGenres = findViewById<RadioGroup>(R.id.radioGroupGenres)
        val radioGroupMood = findViewById<RadioGroup>(R.id.radioGroupMood)
        val createPlaylistButton = findViewById<Button>(R.id.button)

        createPlaylistButton.setOnClickListener {
            val genreSelected = radioGroupGenres.checkedRadioButtonId != -1
            val moodSelected = radioGroupMood.checkedRadioButtonId != -1

            if (genreSelected && moodSelected) {
                selectedGenre = when (radioGroupGenres.checkedRadioButtonId) {
                    R.id.radioButtonLoFi -> "Lo-Fi"
                    R.id.radioButtonClassical -> "Classical"
                    R.id.radioButtonPop -> "Pop"
                    else -> "Unknown"
                }

                selectedMood = when (radioGroupMood.checkedRadioButtonId) {
                    R.id.radioButtonCalm -> "Calm"
                    R.id.radioButtonDance -> "Dance"
                    R.id.radioButtonSad -> "Sad"
                    else -> "Neutral"
                }

                if (isTokenExpired()) {
                    refreshAccessToken {
                        if (accessToken != null) {
                            createSpotifyPlaylist(selectedGenre!!, selectedMood!!)
                        } else {
                            authenticateSpotify()
                        }
                    }
                } else {
                    if (accessToken != null) {
                        createSpotifyPlaylist(selectedGenre!!, selectedMood!!)
                    } else {
                        authenticateSpotify()
                    }
                }
            } else {
                Toast.makeText(this, "Please select one option from both Genre and Mood!", Toast.LENGTH_LONG).show()
            }
        }

        handleRedirect()
    }

    private fun saveTokenInfo(accessToken: String, refreshToken: String, expirationTime: Long) {
        val sharedPreferences = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("access_token", accessToken)
        editor.putString("refresh_token", refreshToken)
        editor.putLong("expiration_time", expirationTime)
        editor.apply()
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

    private fun getRefreshToken(): String? {
        val sharedPreferences = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("refresh_token", null)
    }

    private fun getExpirationTime(): Long {
        val sharedPreferences = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
        return sharedPreferences.getLong("expiration_time", 0)
    }

    private fun isTokenExpired(): Boolean {
        val expirationTime = getExpirationTime()
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime >= expirationTime
    }

    private fun authenticateSpotify() {
        val authUrl = "$authEndpoint?" +
                "client_id=$clientId&" +
                "response_type=code&" +
                "redirect_uri=$redirectUri&" +
                "scope=user-top-read%20playlist-modify-private%20user-read-recently-played"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        startActivity(intent)
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
                    Toast.makeText(this@SpotifyPlaylist, "Failed to get token: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    accessToken = json.getString("access_token")
                    refreshToken = json.getString("refresh_token")
                    val expiresIn = json.getInt("expires_in")
                    val currentTime = System.currentTimeMillis() / 1000
                    val expirationTime = currentTime + expiresIn

                    saveTokenInfo(accessToken ?: "", refreshToken ?: "", expirationTime)

                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Spotify connected successfully!", Toast.LENGTH_SHORT).show()
                    }

                    if (selectedGenre != null && selectedMood != null) {
                        createSpotifyPlaylist(selectedGenre!!, selectedMood!!)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Token exchange failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun refreshAccessToken(callback: (() -> Unit)? = null) {
        val refreshToken = getRefreshToken() ?: return
        val clientCredentials = "$clientId:$clientSecret"
        val encodedCredentials = Base64.encodeToString(clientCredentials.toByteArray(), Base64.NO_WRAP)

        val requestBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
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
                    Toast.makeText(this@SpotifyPlaylist, "Failed to refresh token: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback?.invoke()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    accessToken = json.getString("access_token")
                    val expiresIn = json.getInt("expires_in")
                    val currentTime = System.currentTimeMillis() / 1000
                    val newExpirationTime = currentTime + expiresIn

                    saveTokenInfo(accessToken ?: "", refreshToken, newExpirationTime)

                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Access token refreshed!", Toast.LENGTH_SHORT).show()
                        callback?.invoke()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Failed to refresh token: ${response.message}", Toast.LENGTH_SHORT).show()
                        callback?.invoke()
                    }
                }
            }
        })
    }

    private fun createSpotifyPlaylist(genre: String, mood: String) {
        if (accessToken == null) {
            Toast.makeText(this, "No access token available.", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()

        val playlistJsonBody = JSONObject().apply {
            put("name", "Pomodojo: $detectedEmotion | $genre | $mood")
            put("description", "Generated playlist based on detected emotion, your listening history, and your preferences.")
            put("public", false)
        }

        val mediaType = "application/json".toMediaType()
        val requestBody = playlistJsonBody.toString().toRequestBody(mediaType)

        val createPlaylistRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me/playlists")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        client.newCall(createPlaylistRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SpotifyPlaylist, "Failed to create playlist: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 401) {
                    refreshAccessToken {
                        createSpotifyPlaylist(genre, mood)
                    }
                    return
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    val playlistId = json.getString("id")
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Playlist created successfully!", Toast.LENGTH_SHORT).show()
                    }
                    getUserTopTracksAndAddToPlaylist(playlistId, mood)
                } else {
                    val errorResponse = response.body?.string() ?: "No response body"
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Error creating playlist: ${response.message}", Toast.LENGTH_LONG).show()
                        Log.e("SpotifyPlaylist", "Error response: $errorResponse")
                    }
                }
            }
        })
    }

    private fun getUserTopTracksAndAddToPlaylist(playlistId: String, mood: String) {
        if (accessToken == null) {
            Toast.makeText(this, "No access token available.", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()

        val topTracksUrl = "https://api.spotify.com/v1/me/top/tracks?limit=20&time_range=medium_term"

        val request = Request.Builder()
            .url(topTracksUrl)
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SpotifyPlaylist, "Failed to get top tracks: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 401) {
                    refreshAccessToken {
                        getUserTopTracksAndAddToPlaylist(playlistId, mood)
                    }
                    return
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    val tracksArray = json.getJSONArray("items")

                    val trackUris = mutableListOf<String>()
                    for (i in 0 until tracksArray.length()) {
                        val track = tracksArray.getJSONObject(i)
                        trackUris.add(track.getString("uri"))
                    }

                    val filteredTrackUris = filterTracksBasedOnMood(trackUris, mood)
                    addTracksToPlaylist(playlistId, filteredTrackUris)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Failed to get top tracks: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun filterTracksBasedOnMood(trackUris: List<String>, mood: String): List<String> {
        val filteredTracks = mutableListOf<String>()

        val shuffledTracks = trackUris.shuffled()

        when (mood) {
            "Calm" -> filteredTracks.addAll(shuffledTracks.take(15))
            "Dance" -> filteredTracks.addAll(shuffledTracks.take(15))
            "Sad" -> filteredTracks.addAll(shuffledTracks.take(15))
            "Neutral" -> filteredTracks.addAll(shuffledTracks.take(25))
        }

        return filteredTracks
    }

    private fun addTracksToPlaylist(playlistId: String, trackUris: List<String>) {
        if (accessToken == null) {
            Toast.makeText(this, "No access token available.", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()

        val trackUrisJson = JSONArray(trackUris)
        val requestBody = JSONObject().apply {
            put("uris", trackUrisJson)
        }.toString().toRequestBody("application/json".toMediaType())

        val addTracksRequest = Request.Builder()
            .url("https://api.spotify.com/v1/playlists/$playlistId/tracks")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        client.newCall(addTracksRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SpotifyPlaylist, "Failed to add tracks: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 401) {
                    refreshAccessToken {
                        addTracksToPlaylist(playlistId, trackUris)
                    }
                    return
                }

                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Tracks added to playlist!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Failed to add tracks: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}