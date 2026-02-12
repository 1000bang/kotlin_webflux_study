package com.everydoc.controller.part1

import com.everydoc.service.part1.Step1Service
import com.everydoc.service.part1.Step2Service
import com.everydoc.service.part1.Step3Service
import com.everydoc.service.part1.Step4Service
import com.everydoc.service.part1.Step5Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Part 1 — Kotlin 기초 (Step 1~5)
 * - 코틀린 기본 문법, 함수와 람다, 클래스와 객체, 널 안정성, 컬렉션
 */
@RestController
@RequestMapping("/test")
class Part1Controller(
    private val step1Service: Step1Service,
    private val step2Service: Step2Service,
    private val step3Service: Step3Service,
    private val step4Service: Step4Service,
    private val step5Service: Step5Service,
) {

    @GetMapping("/step1")
    fun step1(): Mono<String> = Mono.just(step1Service.hello())

    @GetMapping("/step2")
    fun step2(): Mono<String> = Mono.just(step2Service.hello())

    @GetMapping("/step3")
    fun step3(): Mono<String> = Mono.just(step3Service.hello())

    @GetMapping("/step4")
    fun step4(): Mono<String> = Mono.just(step4Service.hello())

    @GetMapping("/step5")
    fun step5(): Mono<String> = Mono.just(step5Service.hello())
}
