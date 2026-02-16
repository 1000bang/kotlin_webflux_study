package com.everydoc.controller.part4

import com.everydoc.service.part4.Step14Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Part 4 — WebFlux + Kotlin 실습 (Step 14~18)
 * - WebFlux 프로젝트 구성, 리액티브 Controller/Service, 파일 업로드 등
 */
@RestController
@RequestMapping("/test")
class Part4Controller(
    private val step14Service: Step14Service,
) {

    @GetMapping("/step14")
    fun step14(): Mono<String> = step14Service.info()
}

