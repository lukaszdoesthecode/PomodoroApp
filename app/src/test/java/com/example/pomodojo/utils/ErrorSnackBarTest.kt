package com.example.pomodojo.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.example.pomodojo.R
import com.google.android.material.snackbar.Snackbar
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith

/**
 * Unit tests for the ErrorSnackBar class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = android.app.Application::class)
class ErrorSnackBarTest {

    private lateinit var rootView: ViewGroup

    /**
     * Sets up the test environment.
     */
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        rootView = LayoutInflater.from(context).inflate(R.layout.activity_main, null) as ViewGroup
    }

    /**
     * Tests the showErrorSnackBar method.
     */
    @Test
    fun testShowErrorSnackBar() {
        val mainMessage = "Error occurred"
        val subMessage = "Please try again"

        mockkStatic(Snackbar::class)

        val snackbarMock = mockk<Snackbar>(relaxed = true)
        val snackbarViewMock = mockk<ViewGroup>(relaxed = true)
        every { Snackbar.make(any(), any<String>(), any()) } returns snackbarMock
        every { snackbarMock.view } returns snackbarViewMock

        ErrorSnackBar.showErrorSnackBar(rootView, mainMessage, subMessage)

        verify { Snackbar.make(rootView, "", Snackbar.LENGTH_LONG) }

        val customView = LayoutInflater.from(rootView.context)
            .inflate(R.layout.custom_snackbar_error, rootView, false)

        every { snackbarViewMock.removeAllViews() } just Runs
        every { snackbarViewMock.addView(customView) } just Runs

        val mainTextView = customView.findViewById<TextView>(R.id.snackbar_main_text)
        val subTextView = customView.findViewById<TextView>(R.id.snackbar_sub_text)
        mainTextView.text = mainMessage
        subTextView.text = subMessage

        assert(mainTextView.text == mainMessage)
        assert(subTextView.text == subMessage)

        verify { snackbarMock.show() }
    }
}