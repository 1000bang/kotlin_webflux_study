package com.everydoc.service.part4

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 15단계: 리액티브 Controller (STEP15.md)
 * - @RestController에서 Mono/Flux를 반환하는 패턴
 * - @RequestBody, @PathVariable, @RequestParam 활용
 * - ResponseEntity로 상태 코드 직접 제어
 */
@Service
class Step15Service {

    // ──────────────────────────────────────────────
    // 1. Mono 반환 — 단건 응답
    // ──────────────────────────────────────────────

    /**
     * GET /step15 → Mono<String>
     * 가장 기본 형태: Controller 메서드가 Mono를 반환하면
     * WebFlux가 구독(subscribe)해서 값이 나오면 HTTP 응답으로 내보낸다.
     */
    fun monoExample(): Mono<String> =
        Mono.just("Step15: @GetMapping + Mono<String> 반환 — WebFlux가 자동으로 subscribe 후 응답")

    // ──────────────────────────────────────────────
    // 2. Flux 반환 — 다건 스트림
    // ──────────────────────────────────────────────

    /**
     * GET /step15/flux → Flux<String>
     * Flux를 반환하면 JSON Array(또는 text/event-stream)로 직렬화된다.
     * Accept: application/json  → ["a","b","c"]
     * Accept: text/event-stream → SSE 스트림
     */
    fun fluxExample(): Flux<String> =
        Flux.just(
            "첫 번째 아이템",
            "두 번째 아이템",
            "세 번째 아이템"
        )

    // ──────────────────────────────────────────────
    // 3. @RequestBody — 요청 본문을 Mono로 받기
    // ──────────────────────────────────────────────

    /**
     * POST /step15/echo
     * Controller 시그니처 예시:
     *   fun echo(@RequestBody body: Mono<String>): Mono<String>
     *
     * @RequestBody를 Mono<T>로 선언하면 본문이 준비되는 시점까지
     * 논블로킹으로 기다렸다가 처리한다.
     */
    fun echoBody(body: Mono<String>): Mono<String> =
        body.map { received -> "받은 내용: $received" }

    // ──────────────────────────────────────────────
    // 4. @PathVariable / @RequestParam
    // ──────────────────────────────────────────────

    /**
     * GET /step15/greet/{name}?repeat=3
     * Controller에서 일반 타입(@PathVariable name: String)으로 받아도
     * Service에서는 Mono.just()로 감싸서 리액티브 체인으로 돌린다.
     */
    fun greet(name: String, repeat: Int): Mono<String> =
        Mono.just(name)
            .map { n -> "안녕하세요, $n!".repeat(repeat) }

    // ──────────────────────────────────────────────
    // 5. 핵심 정리
    // ──────────────────────────────────────────────

    fun summary(): Mono<String> = Mono.just(
        """
        [Step15 — 리액티브 Controller 핵심 요약]

        ① Mono 반환  : 단건 응답, WebFlux가 자동 subscribe
        ② Flux 반환  : 다건 스트림 (JSON Array or SSE)
        ③ @RequestBody Mono<T> : 요청 본문도 논블로킹으로 수신
        ④ @PathVariable/@RequestParam : 일반 타입으로 받은 뒤 Mono.just()로 체인 진입
        ⑤ ResponseEntity<Mono<T>> : 상태 코드·헤더를 직접 제어할 때 사용

        → Controller는 절대 block() 호출 금지!
           Service가 반환한 Mono/Flux를 그대로 return 하는 것이 원칙.
        """.trimIndent()
    )
}
