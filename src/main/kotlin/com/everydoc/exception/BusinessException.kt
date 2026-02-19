package com.everydoc.exception

/**
 * 애플리케이션 비즈니스 예외 계층.
 *
 * sealed class를 사용하면 when 분기에서 else 없이 모든 케이스를 처리할 수 있다.
 * GlobalExceptionHandler에서 타입별로 HTTP 상태 코드를 매핑한다.
 */
sealed class BusinessException(message: String) : RuntimeException(message)

/** 404 — 리소스를 찾을 수 없을 때 */
class NotFoundException(message: String) : BusinessException(message)

/** 400 — 요청 값이 잘못되었을 때 */
class ValidationException(message: String) : BusinessException(message)

/** HTTP 에러 응답 바디 */
data class ErrorResponse(
    val code: String,
    val message: String,
)
