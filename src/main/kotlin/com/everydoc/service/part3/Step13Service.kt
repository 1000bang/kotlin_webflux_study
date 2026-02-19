package com.everydoc.service.part3

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 13단계: Controller/Service에서 Mono·Flux 쓰기 (STEP13.md)
 * - Service에서부터 Mono/Flux를 반환하고, Controller는 그대로 반환하는 패턴.
 */
@Service
class Step13Service {

    /** Service에서 바로 Mono 반환 (한 건) */
    fun hello(): Mono<String> = Mono.just(
        "Step13: Service에서 Mono/Flux를 반환하고, Controller는 그대로 반환하는 패턴."
    )

    /** Flux 예제: 1..3 숫자를 스트림으로 발행 후 문자열로 변환 */
    fun numbersAsString(): Mono<String> = Flux.range(1, 3)
        .collectList()
        .map { list -> "Flux.range(1,3) = ${list.joinToString(", ")}" }

    /** 여러 리액티브 메서드를 조합해서 하나의 응답으로 */
    fun composed(): Mono<String> =
        hello().flatMap { h ->
            numbersAsString().map { n ->
                "$h\n$n"
            }
        }
}

