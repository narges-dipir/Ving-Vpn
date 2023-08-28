package com.abrnoc.application.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomOtpContainer(
    modifier: Modifier = Modifier,
    value: String,
    backgroundColor: Color = ApplicationTheme.colors.uiBackground,
    onValueChange: (String) -> (Unit),
    length: Int = 6
) {
    val focusRequest = remember {
        FocusRequester()
    }
    val keyboard = LocalSoftwareKeyboardController.current

    TextField(
        value = value, onValueChange = {
            if (it.length <= length) {
                if (it.all { c -> c in '0'..'9' }) {
                    onValueChange(it)
                }
                if (it.length >= length) {
                    keyboard?.hide()
                }
            }
        },
        modifier = Modifier
            .size(0.dp)
            .focusRequester(focusRequest),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(length) {
            Otp(
                modifier = modifier
                    .size(width = 45.dp, height = 45.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(backgroundColor)
                    .clickable {
                        focusRequest.requestFocus()
                        keyboard?.show()
                    }, value = value.getOrNull(it).toString() ?: "",
                isCursorVisible = value.length == it
            )
            Spacer(modifier = Modifier.size(8.dp))
        }

    }

}

@Composable
fun Otp(
    modifier: Modifier,
    value: String,
    isCursorVisible: Boolean = false
) {
    val scope = rememberCoroutineScope()
    val (cursorSymbol, setCursorSymbol) = remember { mutableStateOf("") }
    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
        if (isCursorVisible) {
            scope.launch {
                delay(350)
                setCursorSymbol(if (cursorSymbol.isEmpty()) "|" else "")
            }
        }
    }

    Box(modifier = modifier) {
        Text(
            text = if (isCursorVisible) cursorSymbol else value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Center),
            color = ApplicationTheme.colors.textPrimary
        )
    }

}