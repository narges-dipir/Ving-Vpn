package com.abrnoc.application.presentation.viewModel.state

import com.abrnoc.application.presentation.viewModel.state.model.SignInObj

data class SignInState(
    val isLoading: Boolean = false,
    val signIn: SignInObj?= null,
    val isSuccessful: Boolean = false,
    val error: String = ""
)

