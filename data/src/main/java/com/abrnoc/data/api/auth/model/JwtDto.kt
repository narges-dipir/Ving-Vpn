package com.abrnoc.data.api.auth.model

import com.google.gson.annotations.SerializedName

data class JwtDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("jwt")
    val jwt: String
)