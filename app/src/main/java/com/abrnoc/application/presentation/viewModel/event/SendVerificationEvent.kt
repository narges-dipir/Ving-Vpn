package com.abrnoc.application.presentation.viewModel.event

sealed class SendVerificationEvent {
    data class SignInQuery(val email: String, val password: String, val code: String) :
        SendVerificationEvent()
}