package com.abrnoc.application.presentation.utiles

import android.content.Context
import android.util.Patterns
import android.widget.Toast

fun isValidEmail(target: CharSequence?): Boolean {
    return if (target == null) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

 fun longToast(context: Context, message: String) {
    Toast.makeText(context, message , Toast.LENGTH_LONG)
        .show()
}

fun shortToast(context: Context, message: String) {
    Toast.makeText(context, message , Toast.LENGTH_SHORT)
        .show()
}
