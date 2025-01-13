package com.example.pomodojo

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

class SpotifyPlaylist : AppCompatActivity() {

    // Spotify API credentials and endpoints
    private val clientId = "2cec84f1e66042ccb6ad5336208f31d4"
    private val clientSecret = "8fd83354b1824cf7b2c6f55cc11188ff"
    private val redirectUri = "myapp://callback"
    private val authEndpoint = "https://accounts.spotify.com/authorize"
    private val tokenEndpoint = "https://accounts.spotify.com/api/token"

    // Tokens for Spotify API access
    private var accessToken: String? = null
    private var refreshToken: String? = null

    // User selections and detected emotion
    private var detectedEmotion: String? = ""
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
                // Determine the selected genre
                selectedGenre = when (radioGroupGenres.checkedRadioButtonId) {
                    R.id.radioButtonLoFi -> "Lo-Fi"
                    R.id.radioButtonClassical -> "Classical"
                    R.id.radioButtonPop -> "Pop"
                    else -> "Unknown"
                }

                // Determine the selected mood
                selectedMood = when (radioGroupMood.checkedRadioButtonId) {
                    R.id.radioButtonCalm -> "Calm"
                    R.id.radioButtonDance -> "Dance"
                    R.id.radioButtonSad -> "Sad"
                    else -> "Neutral"
                }

                Log.d("SpotifyPlaylist", "Selected Genre: $selectedGenre")
                Log.d("SpotifyPlaylist", "Selected Mood: $selectedMood")

                // Check token validity and proceed accordingly
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

    /**
     * Saves the access token, refresh token, and expiration time to SharedPreferences.
     */
    private fun saveTokenInfo(accessToken: String, refreshToken: String, expirationTime: Long) {
        val sharedPreferences = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("access_token", accessToken)
        editor.putString("refresh_token", refreshToken)
        editor.putLong("expiration_time", expirationTime)
        editor.apply()
    }

    /**
     * Retrieves the access token from SharedPreferences.
     */
    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

    /**
     * Retrieves the refresh token from SharedPreferences.
     */
    private fun getRefreshToken(): String? {
        val sharedPreferences = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("refresh_token", null)
    }

    /**
     * Retrieves the token expiration time from SharedPreferences.
     */
    private fun getExpirationTime(): Long {
        val sharedPreferences = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
        return sharedPreferences.getLong("expiration_time", 0)
    }

    /**
     * Checks if the current access token has expired.
     */
    private fun isTokenExpired(): Boolean {
        val expirationTime = getExpirationTime()
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime >= expirationTime
    }

    /**
     * Initiates Spotify authentication by redirecting the user to the Spotify login page.
     */
    private fun authenticateSpotify() {
        val authUrl = "$authEndpoint?" +
                "client_id=$clientId&" +
                "response_type=code&" +
                "redirect_uri=$redirectUri&" +
                "scope=user-top-read%20playlist-modify-private%20user-read-recently-played"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        startActivity(intent)
    }

    /**
     * Handles the redirect from Spotify after authentication.
     */
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

    /**
     * Exchanges the authorization code for access and refresh tokens.
     */
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

    /**
     * Refreshes the access token using the refresh token.
     */
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

    /**
     * Creates a new Spotify playlist based on the selected genre and mood.
     */
    private fun createSpotifyPlaylist(genre: String, mood: String?) {
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
                    // Unauthorized, token might have expired
                    refreshAccessToken {
                        createSpotifyPlaylist(genre, mood)
                    }
                    return
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    val playlistId = json.getString("id")

                    Log.d("SpotifyPlaylist", "Playlist ID: $playlistId")

                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Playlist created successfully!", Toast.LENGTH_SHORT).show()
                    }
                    getUserTopTracksAndAddToPlaylist(playlistId, selectedMood)
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

    /**
     * Retrieves the user's top tracks and adds them to the specified playlist after filtering based on mood.
     * Also fetches 10 random tracks that suit the mood and adds them to the playlist.
     */
    private fun getUserTopTracksAndAddToPlaylist(playlistId: String, selectedMood: String?) {
        if (accessToken == null) {
            Toast.makeText(this, "No access token available.", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val topTracksUrl = "https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=medium_term"

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
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    val tracksArray = json.getJSONArray("items")

                    Log.d("SpotifyPlaylist", "Tracks Array: $tracksArray")

                    val trackData = mutableListOf<Pair<String, String>>()
                    for (i in 0 until tracksArray.length()) {
                        val track = tracksArray.getJSONObject(i)
                        val trackUri = track.getString("uri")
                        val trackName = track.getString("name")
                        trackData.add(Pair(trackUri, trackName))
                    }

                    Log.d("SpotifyPlaylist", "Track Data: $trackData")

                    val filteredTrackUris = filterTracksBasedOnMood(trackData, selectedMood)
                    Log.d("SpotifyPlaylist", "Filtered Track URIs: $filteredTrackUris")

                    getRandomTracksForMood(selectedMood) { randomTrackUris ->
                        Log.d("SpotifyPlaylist", "Random Track URIs received: $randomTrackUris")

                        val combinedTrackUris = (filteredTrackUris + randomTrackUris).distinct()
                        Log.d("SpotifyPlaylist", "Combined Track URIs: $combinedTrackUris")

                        addTracksToPlaylist(playlistId, combinedTrackUris)
                    }
                } else {
                    val errorResponse = response.body?.string()
                    Log.e("SpotifyPlaylist", "Error response: $errorResponse")
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Failed to get top tracks: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    /**
     * Filters the list of tracks based on mood-related keywords found in the track names.
     *
     * @param trackData A list of pairs containing track URIs and names.
     * @param selectedMood The user-selected mood (e.g., "Calm", "Dance", "Sad").
     * @return A list of track URIs that match any of the mood keywords.
     */
    private fun filterTracksBasedOnMood(
        trackData: List<Pair<String, String>>,
        selectedMood: String?
    ): List<String> {
        val happyKeywords = listOf(
            "happy", "party", "dance", "upbeat", "fun",
            "joy", "joyful", "cheerful", "bright", "celebrate",
            "celebration", "sunny", "glowing", "optimistic", "smiling",
            "glee", "delight", "ecstatic", "bliss", "uplifting",
            "positive", "lively", "vibrant", "energetic", "festive",
            "merry", "gleeful", "sparkling", "radiant", "bubbly",
            "bouncy", "feel-good", "rejoice", "euphoria", "sunshine",
            "good vibes", "laugh", "pleasant", "playful", "peppy",
            "lighthearted", "grand", "wonderful", "cheery", "thrill",
            "shining", "up-tempo", "buoyant", "groovy", "swing",
            "carefree", "vivacious", "unforgettable", "exhilarating",
            "boogie", "giddy"
        )

        val sadKeywords = listOf(
            "sad", "melancholy", "down", "heartbreak", "blue",
            "sorrow", "lonely", "loneliness", "tears", "cry",
            "depressed", "gloomy", "grief", "despair", "mourning",
            "misery", "mournful", "wounded", "broken", "bleeding",
            "heartache", "somber", "desolate", "empty", "void",
            "anguish", "lost", "haunting", "pensive", "regret",
            "aching", "dark", "hopeless", "goodbye", "farewell",
            "lament", "bleak", "elegy", "tragic", "cloudy",
            "downhearted", "blue Monday", "miserable", "weary", "woeful",
            "tortured", "longing", "unrequited", "hollow", "mourning rain",
            "drowning", "painful", "unloved", "forlorn", "lonesome"
        )

        val neutralKeywords = listOf(
            "chill", "relax", "calm", "lofi", "smooth",
            "laid-back", "peaceful", "mellow", "serene", "soothing",
            "gentle", "tranquil", "easygoing", "soft", "ambient",
            "restful", "hushed", "understated", "subtle", "airy",
            "dreamy", "cozy", "breeze", "cool", "low-key",
            "flowing", "minimal", "zen", "meditative", "chilled",
            "evening", "dusk", "nightfall", "haze", "foggy",
            "light", "balanced", "downtempo", "slow", "moody",
            "purity", "elegant", "harmony", "quiet", "drifting",
            "pastel", "hazy", "smooth jazz", "background", "placid",
            "lo-fi beats", "warm", "comforting", "rest", "gentle waves"
        )

        val danceKeywords = listOf(
            "dance", "dancing", "groove", "groovy", "beat", "rhythm",
            "club", "electronic", "EDM", "house", "techno", "swing",
            "salsa", "disco", "funk", "hip-hop", "remix", "vibe",
            "swing", "step", "bounce", "twerk", "jam", "tempo",
            "movement", "energetic", "bounce", "move"
        )

        val calmKeywords = listOf(
            "calm", "relax", "peaceful", "serene", "soothing",
            "gentle", "tranquil", "mellow", "soft", "ambient",
            "restful", "hushed", "subtle", "airy", "dreamy",
            "cozy", "breeze", "quiet", "placid", "warm",
            "comforting", "gentle waves", "slow", "meditative"
        )

        val keywords = when (selectedMood?.toLowerCase()) {
            "happy" -> happyKeywords
            "sad" -> sadKeywords
            "neutral" -> neutralKeywords
            "dance" -> danceKeywords
            "calm" -> calmKeywords
            else -> emptyList()
        }

        Log.d("SpotifyPlaylist", "Selected Mood: $selectedMood")
        Log.d("SpotifyPlaylist", "Keywords: $keywords")

        for ((uri, name) in trackData) {
            Log.d("SpotifyPlaylist", "Track: $name (URI: $uri)")
        }

        val filteredTracks = trackData.filter { (_, trackName) ->
            keywords.any { keyword ->
                trackName.contains(keyword, ignoreCase = true)
            }
        }

        Log.d("SpotifyPlaylist", "Filtered Tracks: ${filteredTracks.map { it.second }}")

        return when (selectedMood?.toLowerCase()) {
            "happy" -> filteredTracks.take(15).map { it.first }
            "sad" -> filteredTracks.take(15).map { it.first }
            "calm" -> filteredTracks.take(25).map { it.first }
            "dance" -> filteredTracks.take(25).map { it.first }
            "neutral" -> filteredTracks.take(25).map { it.first }
            else -> trackData.shuffled().take(25).map { it.first }
        }
    }

    /**
     * Adds the filtered tracks to the specified Spotify playlist.
     */
    private fun addTracksToPlaylist(playlistId: String, trackUris: List<String>) {
        if (accessToken == null) {
            Toast.makeText(this, "No access token available.", Toast.LENGTH_SHORT).show()
            return
        }

        if (trackUris.isEmpty()) {
            runOnUiThread {
                Toast.makeText(this@SpotifyPlaylist, "No tracks matched the selected mood.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val client = OkHttpClient()
        val trackUrisJson = JSONArray(trackUris)
        Log.d("SpotifyPlaylist", "Track URIs: $trackUris")
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

    /**
     * Fetches 10 random tracks based on the selected mood's keywords.
     *
     * @param selectedMood The user-selected mood (e.g., "Calm", "Dance", "Sad").
     * @param callback A callback function that receives the list of random track URIs.
     */
    private fun getRandomTracksForMood(selectedMood: String?, callback: (List<String>) -> Unit) {
        if (accessToken == null) {
            runOnUiThread {
                Toast.makeText(this@SpotifyPlaylist, "No access token available for fetching random tracks.", Toast.LENGTH_SHORT).show()
            }
            callback(emptyList())
            return
        }

        val searchQuery = when (selectedMood?.toLowerCase()) {
            "happy" -> "happy OR joyful OR upbeat"
            "sad" -> "sad OR melancholy OR heartbreak"
            "calm" -> "calm OR relax OR peaceful"
            "dance" -> "dance OR groove OR rhythm"
            "neutral" -> "chill OR mellow OR ambient"
            else -> "mood"
        }

        val encodedQuery = Uri.encode(searchQuery)

        val searchUrl = "https://api.spotify.com/v1/search?q=$encodedQuery&type=track&limit=10"
        
        val request = Request.Builder()
            .url(searchUrl)
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SpotifyPlaylist, "Failed to fetch random tracks: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody ?: "")
                    val tracksArray = json.getJSONObject("tracks").getJSONArray("items")
                    val randomTrackUris = mutableListOf<String>()
                    for (i in 0 until tracksArray.length()) {
                        val track = tracksArray.getJSONObject(i)
                        val trackUri = track.getString("uri")
                        randomTrackUris.add(trackUri)
                    }
                    Log.d("SpotifyPlaylist", "Random Track URIs: $randomTrackUris")
                    callback(randomTrackUris)
                } else {
                    val errorResponse = response.body?.string() ?: "No response body"
                    Log.e("SpotifyPlaylist", "Error fetching random tracks: $errorResponse")
                    runOnUiThread {
                        Toast.makeText(this@SpotifyPlaylist, "Failed to fetch random tracks: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                    callback(emptyList())
                }
            }
        })
    }
}
