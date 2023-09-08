package com.abrnoc.data.api.auth.model

data class SignUp (
    val password: String,
    val email: String,
    val code: String
)