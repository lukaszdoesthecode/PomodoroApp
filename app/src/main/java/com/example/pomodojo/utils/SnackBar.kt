package com.example.pomodojo.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.example.pomodojo.R

/**
 * Class that creates a custom Snackbar with a unique style.
 */
class SnackBar {
    companion object {

        /**
         * Displays a custom Snackbar with a custom design.
         *
         * @param view The view to anchor the Snackbar.
         * @param mainMessage The main message to display.
         * @param subMessage The secondary message providing additional context.
         */
        fun showSnackBar(view: View, mainMessage: String, subMessage: String) {
            val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)

            snackbar.view.setBackgroundColor(android.graphics.Color.TRANSPARENT)

            val customView = LayoutInflater.from(view.context)
                .inflate(R.layout.custom_snackbar_accept, view.rootView as? ViewGroup, false).apply {
                    findViewById<TextView>(R.id.snackbar_main_text).apply {
                        text = mainMessage
                        setTextColor(view.context.getColor(R.color.accentD))
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
