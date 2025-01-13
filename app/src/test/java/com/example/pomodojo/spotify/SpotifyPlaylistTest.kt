package com.example.pomodojo.functionality.spotify

import android.content.Intent
import android.widget.Button
import android.widget.RadioGroup
import androidx.test.core.app.ApplicationProvider
import com.example.pomodojo.R
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for the SpotifyPlaylist class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SpotifyPlaylistTest {

    private lateinit var activity: SpotifyPlaylist
    private lateinit var mockWebServer: MockWebServer

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        activity = Robolectric.buildActivity(SpotifyPlaylist::class.java).create().resume().get()
    }

    /**
     * Shuts down the mock web server after each test.
     */
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    /**
     * Tests that the createSpotifyPlaylist method makes a valid API call.
     */
    @Test
    fun `createSpotifyPlaylist should make valid API call`() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JSONObject().put("id", "playlist123").toString())
        mockWebServer.enqueue(mockResponse)

        activity.createSpotifyPlaylist("Pop", "Dance")

        val request = mockWebServer.takeRequest()
        assert(request.path == "/v1/me/playlists")
        assert(request.method == "POST")
    }

    /**
     * Tests that the refreshAccessToken method updates the token.
     */
    @Test
    fun `refreshAccessToken should update token`() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JSONObject().put("access_token", "new_token").put("expires_in", 3600).toString())
        mockWebServer.enqueue(mockResponse)

        activity.refreshAccessToken { }

        val request = mockWebServer.takeRequest()
        assert(request.path == "/api/token")
        assert(request.method == "POST")
    }
}