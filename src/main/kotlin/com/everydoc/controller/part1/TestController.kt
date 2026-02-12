package com.everydoc.step1

import com.everydoc.service.Step5Service
import com.everydoc.service.part1.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * 1단계: 코틀린 기본 문법
 * - 패키지, fun, val/var, 기본 타입
 * - 이 엔드포인트는 앱이 동작하는지 확인용
 */
@RestController
@RequestMapping("/test")
class Step1Controller {

    private val step1Service: Step1Service = Step1Service()
    private val step2Service: Step2Service = Step2Service()
    private val step3Service: Step3Service = Step3Service()
    private val step4Service: Step4Service = Step4Service()
    private val step5Service: Step5Service = Step5Service()
    private val step6Service: Step6Service = Step6Service()

    @GetMapping("/step1")
    fun hello(): Mono<String> {
        val message = step1Service.hello()
        return Mono.just(message)
    }

    @GetMapping("/step2")
    fun step2(): Mono<String> {
        val message = step2Service.hello()
        return Mono.just(message)
    }

    @GetMapping("/step3")
    fun step3(): Mono<String> {
        val message = step3Service.hello()
        return Mono.just(message)
    }

    @GetMapping("/step4")
    fun step4(): Mono<String> {
        val message = step4Service.hello()
        return Mono.just(message)
    }

    @GetMapping("/step5")
    fun step5(): Mono<String> {
        val message = step5Service.hello()
        return Mono.just(message)
    }

    @GetMapping("/step6")
    fun step6(): Mono<String> {
        val message = step6Service.hello()
        return Mono.just(message)
    }
}
