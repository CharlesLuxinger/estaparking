package com.charlesluxinger.estaparking.infra.client.advice

import jakarta.servlet.http.HttpServletRequest
import java.net.URI
import java.util.UUID
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        BindException::class,
        HttpMessageNotReadableException::class,
        IllegalArgumentException::class,
        MissingServletRequestParameterException::class,
        MethodArgumentTypeMismatchException::class,
    )
    fun handleBadRequest(
        exception: Exception,
        request: HttpServletRequest,
    ): ProblemDetail =
        buildProblem(
            status = HttpStatus.BAD_REQUEST,
            title = "Bad Request",
            detail = resolveBadRequestDetail(exception),
            code = "BAD_REQUEST",
            request = request,
        )

    @ExceptionHandler(NoSuchElementException::class, NoHandlerFoundException::class)
    fun handleNotFound(request: HttpServletRequest): ProblemDetail =
        buildProblem(
            status = HttpStatus.NOT_FOUND,
            title = "Not Found",
            detail = "Resource not found.",
            code = "NOT_FOUND",
            request = request,
        )

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(
        exception: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest,
    ): ProblemDetail =
        buildProblem(
            status = HttpStatus.METHOD_NOT_ALLOWED,
            title = "Method Not Allowed",
            detail = "The ${exception.method} method is not supported for this request.",
            code = "METHOD_NOT_ALLOWED",
            request = request,
        )

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupported(
        exception: HttpMediaTypeNotSupportedException,
        request: HttpServletRequest,
    ): ProblemDetail =
        buildProblem(
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            title = "Unsupported Media Type",
            detail = "The media type ${exception.contentType} is not supported.",
            code = "UNSUPPORTED_MEDIA_TYPE",
            request = request,
        )

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(
        exception: Exception,
        request: HttpServletRequest,
    ): ProblemDetail {
        logger.error("Unexpected error while processing request", exception)

        return buildProblem(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            title = "Internal Server Error",
            detail = "An unexpected error occurred.",
            code = "INTERNAL_ERROR",
            request = request,
        )
    }

    private fun buildProblem(
        status: HttpStatus,
        title: String,
        detail: String,
        code: String,
        request: HttpServletRequest,
    ): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(status, detail)
        problem.title = title
        problem.type = URI.create("about:blank")
        problem.instance = URI.create(request.requestURI)
        problem.setProperty("code", code)
        problem.setProperty("traceId", resolveTraceId(request))
        return problem
    }

    private fun resolveBadRequestDetail(exception: Exception): String =
        when (exception) {
            is MethodArgumentNotValidException ->
                exception.bindingResult.fieldErrors
                    .firstOrNull()
                    ?.defaultMessage ?: "Request validation failed."
            is BindException ->
                exception.bindingResult.fieldErrors
                    .firstOrNull()
                    ?.defaultMessage ?: "Request validation failed."
            is HttpMessageNotReadableException -> "Malformed request body."
            is IllegalArgumentException -> exception.message ?: "Request validation failed."
            is MissingServletRequestParameterException -> "Required parameter '${exception.parameterName}' is missing."
            is MethodArgumentTypeMismatchException -> "Parameter '${exception.name}' has invalid value."
            else -> "Request validation failed."
        }

    private fun resolveTraceId(request: HttpServletRequest): String =
        request
            .getHeader(TRACE_ID_HEADER)
            ?.takeIf(String::isNotBlank)
            ?: MDC
                .get(MDC_TRACE_ID_KEY)
                ?.takeIf(String::isNotBlank)
            ?: UUID.randomUUID().toString()

    companion object {
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
        private const val TRACE_ID_HEADER = "X-Trace-Id"
        private const val MDC_TRACE_ID_KEY = "traceId"
    }
}
