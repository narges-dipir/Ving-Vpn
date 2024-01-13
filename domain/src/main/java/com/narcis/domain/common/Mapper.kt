package com.narcis.domain.common

import com.narcis.data.remote.model.DefaultConfig
import com.narcis.domain.model.DefaultConfigResult

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