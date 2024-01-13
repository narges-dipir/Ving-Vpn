package com.narcis.application.presentation.viewModel.state

data class SendCodeState(
    val isLoading: Boolean = false,
    val email: String = "",
    val isValid: Boolean = false,
    val isAlreadyRegistered: Boolean = false,
    val message: String = ""
)