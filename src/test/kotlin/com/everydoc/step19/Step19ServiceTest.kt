package com.everydoc.step19

import com.everydoc.service.part4.Step19Service
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

/**
 * 19단계: StepVerifier — Mono/Flux 단위 테스트
 *
 * StepVerifier는 Project Reactor가 제공하는 테스트 도구다.
 * 구독(subscribe) 이후 발행되는 값·에러·완료 신호를 순서대로 검증한다.
 *
 * 핵심 메서드:
 *   expectNext(value)    — 다음 발행값 검증
 *   expectNextCount(n)   — n개 발행 기대
 *   expectError(Type)    — 에러 발생 검증
 *   verifyComplete()     — 완료 신호 검증 후 구독 시작
 *   verify()             — 에러 신호 검증 후 구독 시작
 */
class Step19ServiceTest {

    private val service = Step19Service()

    // ──────────────────────────────────────────────
    // 1. Mono 정상 케이스
    // ──────────────────────────────────────────────

    @Test
    fun `greet - 이름이 있으면 인사 문자열 반환`() {
        StepVerifier.create(service.greet("철수"))
            .expectNext("안녕하세요, 철수 님!")
            .verifyComplete()           // 완료 신호까지 검증
    }

    // ──────────────────────────────────────────────
    // 2. Mono 에러 케이스
    // ──────────────────────────────────────────────

    @Test
    fun `greet - 이름이 공백이면 IllegalArgumentException 발생`() {
        StepVerifier.create(service.greet(""))
            .expectError(IllegalArgumentException::class.java)
            .verify()                   // 에러 신호 검증 후 구독 시작
    }

    // ──────────────────────────────────────────────
    // 3. Flux 발행값 순서 검증
    // ──────────────────────────────────────────────

    @Test
    fun `numbers - 1부터 5까지 순서대로 발행`() {
        StepVerifier.create(service.numbers())
            .expectNext(1, 2, 3, 4, 5) // 여러 값을 한 번에 검증
            .verifyComplete()
    }

    @Test
    fun `numbers - 발행 개수 검증`() {
        StepVerifier.create(service.numbers())
            .expectNextCount(5)         // 값을 꺼내지 않고 개수만 검증
            .verifyComplete()
    }

    // ──────────────────────────────────────────────
    // 4. Flux → Mono 집계 검증
    // ──────────────────────────────────────────────

    @Test
    fun `sum - 1+2+3+4+5 = 15`() {
        StepVerifier.create(service.sum())
            .expectNext(15L)
            .verifyComplete()
    }

    // ──────────────────────────────────────────────
    // 5. 빈 Mono (empty) 검증
    // ──────────────────────────────────────────────

    @Test
    fun `findOptional - id가 양수면 값 반환`() {
        StepVerifier.create(service.findOptional(1L))
            .expectNext("아이템-1")
            .verifyComplete()
    }

    @Test
    fun `findOptional - id가 0이면 empty Mono 반환`() {
        StepVerifier.create(service.findOptional(0L))
            .verifyComplete()           // 값 없이 완료 = empty Mono
    }

    // ──────────────────────────────────────────────
    // 6. assertNext — 람다로 값 직접 검증
    // ──────────────────────────────────────────────

    @Test
    fun `greet - assertNext로 값 내용 검증`() {
        StepVerifier.create(service.greet("영희"))
            .assertNext { result ->
                assert(result.contains("영희"))
                assert(result.startsWith("안녕하세요"))
            }
            .verifyComplete()
    }
}
