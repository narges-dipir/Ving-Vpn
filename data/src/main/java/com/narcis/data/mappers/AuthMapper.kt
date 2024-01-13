package com.narcis.data.mappers

import com.narcis.data.api.auth.model.JwtDto
import com.narcis.data.remote.model.Jwt

     fun JwtDto.mapJwt(): Jwt {
        return Jwt(
            email,
            id,
            jwt
        )
    }

