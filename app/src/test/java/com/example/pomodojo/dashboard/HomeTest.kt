package com.example.pomodojo.functionality.dashboard

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import com.example.pomodojo.functionality.dashboard.viewmodel.HomeViewModel
import com.example.pomodojo.functionality.dashboard.screens.MainScreen
import com.example.pomodojo.functionality.facescan.FaceScan
import com.example.pomodojo.functionality.historyanalysis.ui.UserHistoryActivity
import com.example.pomodojo.functionality.pomodoro.ui.WorkTimeActivity
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// The tests for navigating to Data Analysis and Face Scan are only mock tests.
// They do not actually navigate to these activities but verify that the navigation logic is triggered correctly.

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HomeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var activity: ComponentActivity

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        every { viewModel.navigateToFaceScan } returns MutableLiveData(null)
        every { viewModel.navigateToDataAnalysis } returns MutableLiveData(null)
        every { viewModel.navigateToPomodoro } returns MutableLiveData(null)
        every { viewModel.errorMessage } returns MutableLiveData(null)

        activity = spyk(ComponentActivity())
    }


    /**
     * Tests navigation to the Face Scan screen.
     */
//    @Test
//    fun mainScreen_NavigateToFaceScan() {
//        val navigateToFaceScanLiveData = MutableLiveData<Boolean?>(true)
//        every { viewModel.navigateToFaceScan } returns navigateToFaceScanLiveData
//
//        composeTestRule.setContent {
//            MainScreen(viewModel = viewModel)
//        }
//
//        composeTestRule.onNodeWithText("Face Scan").assertIsDisplayed().performClick()
//
//        verify { viewModel.navigateToFaceScan() }
//        assert(navigateToFaceScanLiveData.value == true)
//    }

    /**
     * Tests navigation to the Data Analysis screen.
     */
//    @Test
//    fun mainScreen_NavigateToDataAnalysis() {
//        val navigateToDataAnalysisLiveData = MutableLiveData<Boolean?>(true)
//        every { viewModel.navigateToDataAnalysis } returns navigateToDataAnalysisLiveData
//
//        composeTestRule.setContent {
//            MainScreen(viewModel = viewModel)
//        }
//
//        composeTestRule.onNodeWithText("Data Analysis").assertIsDisplayed().performClick()
//
//        verify { viewModel.navigateToDataAnalysis() }
//        assert(navigateToDataAnalysisLiveData.value == true)
//    }

    /**
     * Tests navigation to the Pomodoro screen.
     */
    @Test
    fun mainScreen_NavigateToPomodoro() {
        val navigateToPomodoroLiveData = MutableLiveData<Boolean?>(true)
        every { viewModel.navigateToPomodoro } returns navigateToPomodoroLiveData

        composeTestRule.setContent {
            MainScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Start Pomodoro Session").assertIsDisplayed().performClick()

        verify { viewModel.navigateToPomodoro() }
        assert(navigateToPomodoroLiveData.value == true)
    }

    /**
     * Tests error message display.
     */
    @Test
    fun mainScreen_DisplaysErrorMessages() {
        val errorMessageLiveData = MutableLiveData(Pair("Error", "Something went wrong"))
        every { viewModel.errorMessage } returns errorMessageLiveData

        composeTestRule.setContent {
            MainScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }
}
