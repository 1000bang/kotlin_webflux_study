package com.everydoc.service.part3

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 11단계: Reactor 기초 (STEP11.md)
 * - Mono(0 또는 1개), Flux(0~N개), just, map, flatMap, fromIterable
 */
@Service
class Step11Service {

    /** 한 문장 요약 (Mono 한 건) */
    fun summary(): Mono<String> = Mono.just(
        "Step11: Reactor — Mono(0~1개), Flux(0~N개), just/map/flatMap, lazy 구독."
    )

    /** Mono.just + map 예제 */
    fun monoWithMap(): Mono<String> = Mono.just(10)
        .map { it + 1 }
        .map { "Mono.just(10).map(+1) = $it" }

    /** Flux.just로 여러 값 → 문자열 리스트로 모아서 한 줄 설명 */
    fun fluxJustDescription(): Mono<String> = Flux.just("Mono", "Flux", "Reactor")
        .collectList()
        .map { list -> "Flux.just: ${list.joinToString(", ")}" }

    /** 지연 생성: fromCallable (구독할 때만 실행) */
    fun lazyValue(): Mono<String> = Mono.fromCallable {
        "Mono.fromCallable — 구독 시점에 실행됨(lazy)."
    }
}
