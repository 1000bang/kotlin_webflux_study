package com.everydoc.service.part5

import com.everydoc.exception.NotFoundException
import com.everydoc.exception.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * 20단계: 실전 패턴 (STEP20.md)
 * - Controller → Service 흐름
 * - 예외 처리: onErrorResume, onErrorMap, Mono.error
 * - 로깅: doOnNext, doOnError, doOnSubscribe (리액티브 사이드 이펙트)
 */
@Service
class Step20Service {

    private val log = LoggerFactory.getLogger(javaClass)

    // ──────────────────────────────────────────────
    // 1. Controller → Service 흐름 — 실전 패턴
    // ──────────────────────────────────────────────

    /**
     * 실전에서 자주 쓰이는 흐름:
     *   ① 입력값 검증 → ValidationException
     *   ② DB/외부 API 조회 → NotFoundException (없을 때)
     *   ③ 결과 변환 → map/flatMap
     *   ④ 에러 복구 or 전파 → GlobalExceptionHandler가 처리
     */
    fun findItem(id: Long): Mono<String> {
        // ① 입력값 검증
        if (id <= 0) {
            return Mono.error(ValidationException("id는 양수여야 합니다: $id"))
        }

        // ② 조회 (DB 대신 간단한 시뮬레이션)
        val fakeDb = mapOf(1L to "노트북", 2L to "마우스", 3L to "키보드")

        return Mono.justOrEmpty(fakeDb[id])
            // ③ 없으면 NotFoundException 발생 → GlobalExceptionHandler → 404
            .switchIfEmpty(Mono.error(NotFoundException("아이템을 찾을 수 없습니다: id=$id")))
            // ④ 결과 변환
            .map { name -> "아이템[$id]: $name" }
    }

    // ──────────────────────────────────────────────
    // 2. onErrorMap — 예외 타입 변환
    // ──────────────────────────────────────────────

    /**
     * onErrorMap: 특정 예외를 다른 예외로 변환한다.
     * 외부 API 예외 → 내부 도메인 예외로 래핑할 때 주로 사용.
     */
    fun callExternalApi(param: String): Mono<String> =
        Mono.fromCallable { simulateExternalCall(param) }
            .onErrorMap(IllegalStateException::class.java) { ex ->
                // 외부 예외 → 도메인 예외로 변환
                ValidationException("외부 API 오류: ${ex.message}")
            }

    private fun simulateExternalCall(param: String): String {
        if (param == "error") throw IllegalStateException("외부 시스템 장애")
        return "외부 API 응답: $param"
    }

    // ──────────────────────────────────────────────
    // 3. 로깅 — doOn* 사이드 이펙트 연산자
    // ──────────────────────────────────────────────

    /**
     * 리액티브 스트림에서는 체인 중간에 로그를 남기기 위해
     * 사이드 이펙트 전용 연산자(doOn*)를 사용한다.
     *
     * doOnSubscribe : 구독 시작 시 (요청 수신 로그)
     * doOnNext      : 값 발행 시 (응답값 로그)
     * doOnError     : 에러 발생 시 (에러 로그)
     * doOnSuccess   : Mono 완료 시 (성공 로그, Mono 전용)
     */
    fun findItemWithLogging(id: Long): Mono<String> =
        findItem(id)
            .doOnSubscribe { log.info("[요청] findItem 시작: id={}", id) }
            .doOnNext      { result -> log.info("[응답] findItem 완료: {}", result) }
            .doOnError     { ex -> log.error("[에러] findItem 실패: id={}, error={}", id, ex.message) }

    // ──────────────────────────────────────────────
    // 4. 핵심 정리
    // ──────────────────────────────────────────────

    fun summary(): Mono<String> = Mono.just(
        """
        [Step20 — 실전 패턴 핵심 요약]

        ① Controller → Service 흐름
           Controller : Mono/Flux 그대로 return (block() 금지)
           Service    : 입력 검증 → 조회 → 변환 → 에러 전파

        ② 예외 처리
           Mono.error(NotFoundException(...))     → 404
           Mono.error(ValidationException(...))   → 400
           GlobalExceptionHandler(@RestControllerAdvice) 에서 타입별 상태 코드 매핑

           onErrorResume { }  : 에러 → 대체 Mono 반환 (복구)
           onErrorMap { }     : 에러 → 다른 예외 타입으로 변환
           switchIfEmpty { }  : empty Mono → NotFoundException 발행

        ③ 로깅 (doOn* 사이드 이펙트)
           doOnSubscribe : 구독 시작 (요청 수신 로그)
           doOnNext      : 값 발행 (응답값 로그)
           doOnError     : 에러 발생 (에러 로그)
           doOnSuccess   : Mono 완료 (성공 로그, Mono 전용)

           ※ 리액티브에서 map/flatMap 안에 로그 찍는 것은 사이드 이펙트 혼재
             → doOn* 연산자를 별도로 체이닝하는 것이 올바른 패턴
        """.trimIndent()
    )
}
