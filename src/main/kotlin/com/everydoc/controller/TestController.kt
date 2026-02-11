package com.everydoc.step1

import com.everydoc.service.Step2Service
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
}
