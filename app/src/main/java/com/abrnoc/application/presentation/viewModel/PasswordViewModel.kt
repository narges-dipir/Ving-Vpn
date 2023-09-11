package com.abrnoc.application.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _email = mutableStateOf("")
    val email: State<String> = _email
    init {
        savedStateHandle.get<String>("email")?.let {email ->
           _email.value = email
        }
    }

}