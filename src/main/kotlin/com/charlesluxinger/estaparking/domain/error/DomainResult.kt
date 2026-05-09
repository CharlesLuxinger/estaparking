package com.charlesluxinger.estaparking.domain.error

sealed interface DomainResult<out T, out E> {
    data class Success<out T>(
        val value: T,
    ) : DomainResult<T, Nothing>

    data class Error<out E>(
        val error: E,
    ) : DomainResult<Nothing, E>
}
