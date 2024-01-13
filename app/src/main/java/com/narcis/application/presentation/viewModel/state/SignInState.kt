package com.narcis.application.presentation.viewModel.state

import com.narcis.application.presentation.viewModel.state.model.SignInObj

data class SignInState(
    val isLoading: Boolean = false,
    val signIn: SignInObj?= null,
    val isSuccessful: Boolean = false,
    val isRequestSend: Boolean = true,
    val error: String = ""
)

