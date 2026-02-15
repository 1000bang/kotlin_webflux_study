package com.everydoc.service.part3

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration

/**
 * 12단계: 블로킹 vs 논블로킹 (STEP12.md)
 * - 블로킹: 스레드가 I/O 완료까지 대기. 논블로킹: 제어 반환 후 콜백/체인.
 * - subscribeOn(boundedElastic())으로 블로킹 작업 격리.
 */
@Service
class Step12Service {

    /** 블로킹 예제: Thread.sleep → 스레드가 100ms 대기. subscribeOn(boundedElastic())으로 격리 */
    fun blockingOnElastic(): Mono<String> = Mono.fromCallable {
        Thread.sleep(100)
        "블로킹: boundedElastic에서 실행했음(스레드 대기)."
    }.subscribeOn(Schedulers.boundedElastic())

    /** 논블로킹 예제: Mono.delay → 스레드는 놓아 두고, 100ms 후에 완료 신호만 보냄 */
    fun nonBlockingExample(): Mono<String> = Mono.delay(Duration.ofMillis(100))
        .thenReturn("논블로킹: Mono.delay로 대기했고, 스레드는 막지 않음.")

    /** 블로킹 + 논블로킹 예제 둘 다 실행 후 한 번에 응답 */
    fun blockingAndNonBlocking(): Mono<String> = Mono.zip(blockingOnElastic(), nonBlockingExample())
        .map { (blocking, nonBlocking) ->
            "Step12 — 블로킹 vs 논블로킹\n" +
                "1) $blocking\n" +
                "2) $nonBlocking"
        }
}
