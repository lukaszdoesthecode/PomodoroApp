package com.example.pomodojo.core.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.snackbar.Snackbar
import com.example.pomodojo.R

/**
 * Class that creates a custom ErrorSnackbar with a unique style.
 */
class ErrorSnackBar {
    companion object {

        /**
         * Displays an error Snackbar with a custom design.
         *
         * @param view The view to anchor the Snackbar.
         * @param mainMessage The main error message to display.
         * @param subMessage The secondary message providing additional context.
         */
        fun showErrorSnackBar(view: View, mainMessage: String, subMessage: String) {
            val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)

            snackbar.view.setBackgroundColor(android.graphics.Color.TRANSPARENT)

            val customView = LayoutInflater.from(view.context)
                .inflate(R.layout.custom_snackbar_error, view.rootView as? ViewGroup, false).apply {
                    findViewById<TextView>(R.id.snackbar_main_text).apply {
                        text = mainMessage
                        setTextColor(view.context.getColor(R.color.error))
                    }
                    findViewById<TextView>(R.id.snackbar_sub_text).apply {
                        text = subMessage
                        setTextColor(view.context.getColor(R.color.primary))
                    }

                    findViewById<ImageView>(R.id.snackbar_icon)
                }

            (snackbar.view as ViewGroup).removeAllViews()
            (snackbar.view as ViewGroup).addView(customView)

            snackbar.show()
        }
    }
}

@Composable
fun ErrorSnackBarPreview() {
    AndroidView(factory = { context ->
        val view = LayoutInflater.from(context).inflate(R.layout.activity_home, null) as ViewGroup
        ErrorSnackBar.showErrorSnackBar(view, "Error occurred", "Please try again")
        view
    })
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorSnackBar() {
    ErrorSnackBarPreview()
}