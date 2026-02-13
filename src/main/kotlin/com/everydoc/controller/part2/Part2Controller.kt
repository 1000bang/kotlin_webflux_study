package com.everydoc.controller.part2

import com.everydoc.service.part2.Step6Service
import com.everydoc.service.part2.Step7Service
import com.everydoc.service.part2.Step8Service
import com.everydoc.service.part2.Step9Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Part 2 — Kotlin 특징 (Step 6~9)
 * - 코틀린 특징 요약, 확장 함수·프로퍼티, 스코프 함수, 왜 코틀린을 쓰는가
 */
@RestController
@RequestMapping("/test")
class Part2Controller(
    private val step6Service: Step6Service,
    private val step7Service: Step7Service,
    private val step8Service: Step8Service,
    private val step9Service: Step9Service,
) {

    @GetMapping("/step6")
    fun step6(): Mono<String> = Mono.just(step6Service.hello())

    @GetMapping("/step7")
    fun step7(): Mono<String> = Mono.just(step7Service.hello())

    @GetMapping("/step8")
    fun step8(): Mono<String> = Mono.just(step8Service.hello())

    @GetMapping("/step9")
    fun step9(): Mono<String> = Mono.just(step9Service.hello())
}
