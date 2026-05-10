package com.charlesluxinger.estaparking.domain.result

import com.charlesluxinger.estaparking.domain.result.DomainResult.Error
import com.charlesluxinger.estaparking.domain.result.DomainResult.Success

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
        is Success -> Success(transform(value))
        is Error -> Error(error)
    }

fun <T, E, R> DomainResult<T, E>.flatMap(transform: (T) -> DomainResult<R, E>): DomainResult<R, E> =
    when (this) {
        is Success -> transform(value)
        is Error -> Error(error)
    }
