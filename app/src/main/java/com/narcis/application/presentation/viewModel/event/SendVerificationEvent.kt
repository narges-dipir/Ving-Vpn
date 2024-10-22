package com.narcis.application.presentation.viewModel.event

sealed class SendVerificationEvent {
    data class SignInQuery(val email: String, val password: String, val code: String) :
        SendVerificationEvent()

    object EmailQuery: SendVerificationEvent()
}