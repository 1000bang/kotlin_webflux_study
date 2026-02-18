package com.everydoc.controller.part4

import com.everydoc.service.part4.Step14Service
import com.everydoc.service.part4.Step15Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Part 4 — WebFlux + Kotlin 실습 (Step 14~18)
 * - WebFlux 프로젝트 구성, 리액티브 Controller/Service, 파일 업로드 등
 */
@RestController
@RequestMapping("/test")
class Part4Controller(
    private val step14Service: Step14Service,
    private val step15Service: Step15Service,
) {

    @GetMapping("/step14")
    fun step14(): Mono<String> = step14Service.info()

    // ── Step 15: 리액티브 Controller ──────────────────────────

    /** GET /test/step15 — Mono 단건 반환 예시 */
    @GetMapping("/step15")
    fun step15(): Mono<String> = step15Service.monoExample()

    /** GET /test/step15/summary — 핵심 요약 */
    @GetMapping("/step15/summary")
    fun step15Summary(): Mono<String> = step15Service.summary()

    /** GET /test/step15/flux — Flux 다건 스트림 반환 예시 */
    @GetMapping("/step15/flux")
    fun step15Flux(): Flux<String> = step15Service.fluxExample()

    /** POST /test/step15/echo — @RequestBody를 Mono<T>로 받는 예시 */
    @PostMapping("/step15/echo")
    fun step15Echo(@RequestBody body: Mono<String>): Mono<String> =
        step15Service.echoBody(body)

    /** GET /test/step15/greet/{name}?repeat=N — @PathVariable, @RequestParam 예시 */
    @GetMapping("/step15/greet/{name}")
    fun step15Greet(
        @PathVariable name: String,
        @RequestParam(defaultValue = "1") repeat: Int,
    ): Mono<String> = step15Service.greet(name, repeat)
}

