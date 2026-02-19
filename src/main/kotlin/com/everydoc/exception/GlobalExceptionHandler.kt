package com.everydoc.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 핸들러.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * WebFlux에서도 동일하게 동작한다.
 *
 * 처리 흐름:
 *   리액티브 체인에서 에러 발생
 *   → Mono.error(NotFoundException(...))
 *   → GlobalExceptionHandler.handleNotFound()
 *   → { code, message } JSON 응답 반환
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    /** 404 — NotFoundException */
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NotFoundException): ErrorResponse {
        log.warn("Not Found: {}", ex.message)
        return ErrorResponse(code = "NOT_FOUND", message = ex.message ?: "리소스를 찾을 수 없습니다")
    }

    /** 400 — ValidationException */
    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: ValidationException): ErrorResponse {
        log.warn("Validation Error: {}", ex.message)
        return ErrorResponse(code = "VALIDATION_ERROR", message = ex.message ?: "요청이 올바르지 않습니다")
    }

    /** 500 — 처리되지 않은 예외 */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneral(ex: Exception): ErrorResponse {
        log.error("Unhandled Exception", ex)
        return ErrorResponse(code = "INTERNAL_ERROR", message = "서버 오류가 발생했습니다")
    }
}
