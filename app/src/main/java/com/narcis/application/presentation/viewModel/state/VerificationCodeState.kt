package com.narcis.application.presentation.viewModel.state

import com.narcis.domain.model.VerificationObject

data class VerificationCodeState(
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val error: String = "",
    val verificationObject: VerificationObject?= null,
    val password: String = "",
    val email: String = "",
    val code: String = ""
)

