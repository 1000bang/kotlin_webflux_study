package com.everydoc.service.part4

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 19단계: 테스트 (STEP19.md)
 * - StepVerifier : Mono/Flux 단위 테스트
 * - WebTestClient : HTTP 엔드포인트 통합 테스트
 * - @WebFluxTest  : WebFlux 레이어만 로드하는 슬라이스 테스트
 *
 * 이 Service는 테스트 대상이 되는 간단한 리액티브 메서드를 제공한다.
 */
@Service
class Step19Service {

    // ──────────────────────────────────────────────
    // 테스트 대상 메서드
    // ──────────────────────────────────────────────

    /** 정상 케이스: 이름을 받아 인사 문자열 반환 */
    fun greet(name: String): Mono<String> =
        if (name.isBlank())
            Mono.error(IllegalArgumentException("이름이 비어있습니다"))
        else
            Mono.just("안녕하세요, $name 님!")

    /** Flux: 1~5 숫자 스트림 */
    fun numbers(): Flux<Int> = Flux.range(1, 5)

    /** 집계: Flux 합산 → Mono */
    fun sum(): Mono<Long> =
        numbers()
            .map { it.toLong() }
            .reduce(0L, Long::plus)

    /** 에러 케이스: 빈 Mono (switchIfEmpty 테스트용) */
    fun findOptional(id: Long): Mono<String> =
        if (id > 0) Mono.just("아이템-$id")
        else Mono.empty()

    // ──────────────────────────────────────────────
    // 핵심 정리
    // ──────────────────────────────────────────────

    fun summary(): Mono<String> = Mono.just(
        """
        [Step19 — 테스트 핵심 요약]

        ① StepVerifier — Mono/Flux 단위 테스트
           StepVerifier.create(mono)
               .expectNext("기댓값")
               .verifyComplete()

           StepVerifier.create(mono)
               .expectError(IllegalArgumentException::class.java)
               .verify()

        ② WebTestClient — HTTP 엔드포인트 통합 테스트
           webTestClient.get().uri("/test/step19/greet?name=철수")
               .exchange()
               .expectStatus().isOk
               .expectBody<String>().isEqualTo("안녕하세요, 철수 님!")

        ③ @WebFluxTest — WebFlux 레이어만 로드 (빠른 슬라이스 테스트)
           전체 컨텍스트 대신 Controller + WebFlux 설정만 로드.
           Service는 @MockkBean 또는 @MockBean으로 Mock 처리.
        """.trimIndent()
    )
}
