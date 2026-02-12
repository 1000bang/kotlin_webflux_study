package com.everydoc.controller.part2

import com.everydoc.service.part2.Step6Service
import com.everydoc.service.part2.Step7Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Part 2 — Kotlin 특징 (Step 6~7)
 * - 코틀린 특징 요약, 확장 함수·프로퍼티
 */
@RestController
@RequestMapping("/test")
class Part2Controller(
    private val step6Service: Step6Service,
    private val step7Service: Step7Service,
) {

    @GetMapping("/step6")
    fun step6(): Mono<String> = Mono.just(step6Service.hello())

    @GetMapping("/step7")
    fun step7(): Mono<String> = Mono.just(step7Service.hello())
}
