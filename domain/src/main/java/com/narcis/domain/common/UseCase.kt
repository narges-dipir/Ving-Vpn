package com.narcis.domain.common

import kotlin.jvm.Throws

abstract class UseCase<in P, R> {
    operator fun invoke(parameters: P): R = execute(parameters)

    @Throws(RuntimeException::class)
    protected abstract fun execute(parameters: P): R
}