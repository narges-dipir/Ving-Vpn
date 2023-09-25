package com.abrnoc.domain.common

import com.abrnoc.data.remote.model.DefaultConfig
import com.abrnoc.domain.model.DefaultConfigResult

fun DefaultConfig.mapToDomainConfig(): DefaultConfigResult {
    return DefaultConfigResult(
        address,
        alpn,
        country,
        fingerprint,
        flag,
        username,
        password,
        port,
        protocol,
        security,
        sni,
        type,
        url
    )
}