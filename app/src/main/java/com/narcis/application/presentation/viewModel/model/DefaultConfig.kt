package com.narcis.application.presentation.viewModel.model

data class DefaultConfig(
    val id: Long? = 0L,
    val address: String? = "",
    val alpn: String? = "",
    val country: String? = "",
    val fingerprint: String? = "",
    val flag: String? = "",
    val password: String? = "",
    val port: Int? = 0,
    val protocol: String? = "",
    val security: String? = "",
    val sni: String? = "",
    val type: String? = "",
    val url: String? = "",
)
