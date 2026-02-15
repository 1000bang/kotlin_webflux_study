package com.everydoc.controller.part3

import com.everydoc.service.part3.Step10Service
import com.everydoc.service.part3.Step11Service
import com.everydoc.service.part3.Step12Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Part 3 — WebFlux 개념 (Step 10~13)
 * - 웹플럭스란, 웹플럭스 특징, Reactor 기초, 블로킹 vs 논블로킹
 */
@RestController
@RequestMapping("/test")
class Part3Controller(
    private val step10Service: Step10Service,
    private val step11Service: Step11Service,
    private val step12Service: Step12Service,
) {

    @GetMapping("/step10")
    fun step10(): Mono<String> = Mono.just(step10Service.hello())

    @GetMapping("/step11")
    fun step11(): Mono<String> = step11Service.summary()

    @GetMapping("/step12")
    fun step12(): Mono<String> = step12Service.blockingAndNonBlocking()
}
