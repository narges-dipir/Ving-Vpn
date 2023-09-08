package com.abrnoc.application.repository.model

sealed class ResultWrapper<out R> {
    data class Success<out T> (val data: T) : ResultWrapper<T>()
    data class Error(val exception: Exception) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}
val <T> ResultWrapper<T>.data: T?
    get() {
        return (this as? ResultWrapper.Success)?.data
    }
