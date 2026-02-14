package com.everydoc.service.part3

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * 10단계: 웹플럭스란 (STEP10.md)
 * - 논블로킹 I/O, 리액티브 스트림, Spring WebFlux 위치
 */
@Service
class Step10Service {

    fun hello(): String {
        println(practiceMonoJust())
        return "Step10: 웹플럭스 — 논블로킹 I/O, 리액티브 스트림, Spring WebFlux."
    }

    // ---------- Mono 맛보기 (리액티브 타입) ----------
    fun practiceMonoJust(): String {
        val mono = Mono.just("reactive")
        // 구독 전까지는 아무 일도 안 함 (lazy). block()은 학습용으로 값만 꺼낼 때
        val result = mono.block()
        return "Mono.just 맛보기: result=$result"
    }
}
