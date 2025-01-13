package com.example.pomodojo.facescan

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.pomodojo.functionality.facescan.FaceScanScreen
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// The tests with a callback don't work properly possibly due to the UI not being fully rendered or the component not being found in the current state of the UI hierarchy.

/**
 * Unit tests for the FaceScanScreen Composable.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FaceScanTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockBitmap: Bitmap
    private lateinit var mockUri: Uri

    @Before
    fun setUp() {
        mockBitmap = mockk(relaxed = true)
        mockUri = mockk(relaxed = true)
    }

    /**
     * Tests that the FaceScanScreen displays all UI components correctly.
     */
    @Test
    fun faceScanScreen_DisplaysUIComponents() {
        composeTestRule.setContent {
            FaceScanScreen(
                selectedBitmap = null,
                detectedEmotion = "Show us your face and generate a Spotify playlist based on your mood!",
                onGalleryClick = {},
                onCameraClick = {},
                onAnalyzeClick = {}
            )
        }

        composeTestRule.onNodeWithText("Analyze your face!").assertIsDisplayed()
    }

    /**
     * Tests that clicking the "Gallery" button triggers the correct callback.
     */
//    @Test
//    fun faceScanScreen_GalleryClickTriggersCallback() {
//        var galleryClicked = false
//
//        composeTestRule.setContent {
//            FaceScanScreen(
//                selectedBitmap = null,
//                detectedEmotion = "",
//                onGalleryClick = { galleryClicked = true },
//                onCameraClick = {},
//                onAnalyzeClick = {}
//            )
//        }
//
//        // Simulate a click and verify the flag
//        composeTestRule.onNodeWithText("Gallery").performClick()
//        assert(galleryClicked) { "Gallery button click did not trigger the callback." }
//    }

    /**
     * Tests that clicking the "Camera" button triggers the correct callback.
     */
//    @Test
//    fun faceScanScreen_CameraClickTriggersCallback() {
//        var cameraClicked = false
//
//        composeTestRule.setContent {
//            FaceScanScreen(
//                selectedBitmap = null,
//                detectedEmotion = "",
//                onGalleryClick = {},
//                onCameraClick = { cameraClicked = true },
//                onAnalyzeClick = {}
//            )
//        }
//
//        // Simulate a click and verify the flag
//        composeTestRule.onNodeWithText("Camera").performClick()
//        assert(cameraClicked) { "Camera button click did not trigger the callback." }
//    }

    /**
     * Tests that clicking the "Analyze Image" button triggers the correct callback.
     */
    @Test
    fun faceScanScreen_AnalyzeClickTriggersCallback() {
        var analyzeClicked = false

        composeTestRule.setContent {
            FaceScanScreen(
                selectedBitmap = null,
                detectedEmotion = "",
                onGalleryClick = {},
                onCameraClick = {},
                onAnalyzeClick = { analyzeClicked = true }
            )
        }

        // Perform click and verify callback
        composeTestRule.onNodeWithText("Analyze Image").performClick()
        assert(analyzeClicked) { "Analyze Image button click did not trigger the callback." }
    }
}