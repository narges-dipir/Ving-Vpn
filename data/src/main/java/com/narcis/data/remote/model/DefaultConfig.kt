package com.narcis.data.remote.model

data class DefaultConfig(
    val address: String,
    val alpn: String,
    val country: String,
    val fingerprint: String,
    val flag: String,
    val username: String?,
    val password: String,
    val port: Int,
    val protocol: String,
    val security: String,
    val sni: String,
    val type: String,
    val url: String,
)
