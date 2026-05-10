package com.charlesluxinger.estaparking.domain.error

sealed interface DomainResult<out T, out E> {
    data class Success<out T>(
        val value: T,
    ) : DomainResult<T, Nothing>

    data class Error<out E>(
        val error: E,
    ) : DomainResult<Nothing, E>
}

fun <T, E, R> DomainResult<T, E>.map(transform: (T) -> R): DomainResult<R, E> =
    when (this) {
        is DomainResult.Success -> DomainResult.Success(transform(value))
        is DomainResult.Error -> DomainResult.Error(error)
    }

fun <T, E, R> DomainResult<T, E>.flatMap(transform: (T) -> DomainResult<R, E>): DomainResult<R, E> =
    when (this) {
        is DomainResult.Success -> transform(value)
        is DomainResult.Error -> DomainResult.Error(error)
    }
