package com.abrnoc.data.mappers

import com.abrnoc.data.api.auth.model.JwtDto
import com.abrnoc.data.remote.model.Jwt
import javax.inject.Singleton

     fun JwtDto.mapJwt(): Jwt {
        return Jwt(
            email,
            id,
            jwt
        )
    }