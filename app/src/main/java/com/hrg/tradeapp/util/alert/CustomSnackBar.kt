package com.hrg.tradeapp.util.alert

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hrg.tradeapp.R
import com.hrg.tradeapp.util.MessageType
import javax.inject.Inject


class CustomSnackBar @Inject constructor() {
    fun showSnackBar(
        context: Context,
        view: View,
        messageType: MessageType,
        message: String,
    ) {
        // Create the Snackbar
        // Check duration
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
        // Get the Snackbar's layout view
        val layout = snackbar.view
        val textView = layout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        snackbar.setText(message)
        // check need action and set this
        // set background
        val typedValue = TypedValue()
        val theme = context.theme

        layout.background = when (messageType) {
            MessageType.SUCCESS -> {
                theme.resolveAttribute(R.attr.colorBackgroundSnackBarSuccess, typedValue, true)
//                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.white_checkbox, 0, 0, 0)
                ContextCompat.getDrawable(context, R.drawable.bg_snackbar_success)
            }
            MessageType.ERROR -> {
                theme.resolveAttribute(R.attr.colorBackgroundSnackBarError, typedValue, true)
//                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0)
                ContextCompat.getDrawable(context, R.drawable.bg_snackbar_error)
            }
            MessageType.INFO -> {
                theme.resolveAttribute(R.attr.colorBackgroundSnackBarInfo, typedValue, true)
//                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.white_checkbox, 0, 0, 0)
                ContextCompat.getDrawable(context, R.drawable.bg_snackbar_info)
            }
        }

        textView.compoundDrawablePadding =
            context.resources.getDimensionPixelOffset(R.dimen.size_5dp)
        textView.gravity = Gravity.CENTER_VERTICAL
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.white))

        snackbar.show()
    }
}