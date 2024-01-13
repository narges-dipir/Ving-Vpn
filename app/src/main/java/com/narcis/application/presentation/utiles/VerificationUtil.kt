package com.narcis.application.presentation.utiles

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import com.narcis.application.presentation.viewModel.state.SendCodeState
import java.util.regex.Pattern

fun isValidEmail(target: CharSequence?): Boolean {
    return if (target == null) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

fun longToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG)
        .show()
}

fun shortToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT)
        .show()
}

fun validateSignInRequest(email: String): SendCodeState {
    if (email.isBlank()) {
        return SendCodeState(isLoading = false, isValid = false, message = " Email field is empty")
    }
    if (email.isNotBlank()) {
        val emailRegex = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        val matches = emailRegex.matcher(email).matches()
        if (!matches) {
            return SendCodeState(
                isLoading = false,
                isValid = false,
                message = " Email is not valuable"
            )
        }

    }
    return SendCodeState(
        isLoading = false,
        isValid = true,
        message = "email accepted"
    )
}