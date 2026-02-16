package com.everydoc.service.part4

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * 14단계: WebFlux 프로젝트 구성 (STEP14.md)
 * - 이 프로젝트가 어떻게 WebFlux + Kotlin으로 구성되어 있는지 한 줄 요약.
 */
@Service
class Step14Service {

    fun info(): Mono<String> = Mono.just(
        "Step14: Spring Boot WebFlux + Kotlin 프로젝트 구성 — starter-webflux, Kotlin 플러그인, Reactor 의존성으로 WebFlux 앱을 기동."
    )
}

