package com.everydoc.controller.part5

import com.everydoc.service.part5.Step20Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Part 5 — 정리 (Step 20)
 * - 실전 패턴: Controller → Service 흐름, 예외 처리, 로깅
 */
@RestController
@RequestMapping("/test")
class Part5Controller(
    private val step20Service: Step20Service,
) {

    // ── Step 20: 실전 패턴 ──────────────────────────

    /** GET /test/step20/summary — 핵심 요약 */
    @GetMapping("/step20/summary")
    fun step20Summary(): Mono<String> = step20Service.summary()

    /**
     * GET /test/step20/items/{id}
     * - id <= 0 → 400 (ValidationException → GlobalExceptionHandler)
     * - id 없음 → 404 (NotFoundException → GlobalExceptionHandler)
     * - 정상    → 200
     */
    @GetMapping("/step20/items/{id}")
    fun step20FindItem(@PathVariable id: Long): Mono<String> =
        step20Service.findItem(id)

    /**
     * GET /test/step20/items/{id}/log
     * doOnSubscribe / doOnNext / doOnError 로깅이 적용된 버전.
     * 서버 콘솔에서 로그를 확인할 수 있다.
     */
    @GetMapping("/step20/items/{id}/log")
    fun step20FindItemWithLog(@PathVariable id: Long): Mono<String> =
        step20Service.findItemWithLogging(id)

    /**
     * GET /test/step20/external?param=hello
     * - param=error → ValidationException (onErrorMap 변환)
     * - 정상 param  → 외부 API 응답 문자열 반환
     */
    @GetMapping("/step20/external")
    fun step20External(@RequestParam param: String): Mono<String> =
        step20Service.callExternalApi(param)
}
