package com.abrnoc.domain.model

data class VerificationObject(
    val password: String, val email: String, val code: String
)
