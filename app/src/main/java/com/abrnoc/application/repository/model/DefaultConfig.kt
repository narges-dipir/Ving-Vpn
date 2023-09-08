package com.abrnoc.application.repository.model

data class DefaultConfig(
    val address: String,
    val alpn: String,
    val country: String,
    val fingerprint: String,
    val flag: String,
    val password: String,
    val port: Int,
    val protocol: String,
    val security: String,
    val sni: String,
    val type: String,
    val url: String
)