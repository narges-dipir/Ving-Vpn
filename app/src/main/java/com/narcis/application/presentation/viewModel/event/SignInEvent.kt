package com.narcis.application.presentation.viewModel.event

sealed class SignInEvent {
    data class SignInQuery(val email: String, val password: String): SignInEvent()
    object ClearEvent: SignInEvent()
}