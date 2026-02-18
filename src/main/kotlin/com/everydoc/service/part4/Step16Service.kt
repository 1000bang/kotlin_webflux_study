package com.everydoc.service.part4

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

/**
 * 16단계: 리액티브 Service (STEP16.md)
 * - Mono.fromCallable : 동기 코드를 리액티브 체인으로 감싸기
 * - map / flatMap 체이닝 : 변환과 비동기 조합
 * - onErrorResume / onErrorReturn : 에러 복구 전략
 */
@Service
class Step16Service {

    // ──────────────────────────────────────────────
    // 1. Mono.fromCallable — 동기 코드를 Mono로 감싸기
    // ──────────────────────────────────────────────

    /**
     * fromCallable { } 안에서 동기 작업(DB 조회, 파일 읽기 등)을 실행한다.
     * 블로킹 작업이면 반드시 subscribeOn(boundedElastic)을 함께 사용한다.
     */
    fun fromCallableExample(): Mono<String> =
        Mono.fromCallable {
            // 실제 서비스라면 이 위치에 DB 조회, HTTP 클라이언트 호출 등이 온다
            "fromCallable: 동기 작업의 결과값"
        }.subscribeOn(Schedulers.boundedElastic())

    // ──────────────────────────────────────────────
    // 2. map — 값을 동기적으로 변환
    // ──────────────────────────────────────────────

    /**
     * map { } 은 값이 있을 때 동기적으로 변환한다.
     * 새로운 Mono를 반환하지 않고 "값 자체"를 바꾼다.
     */
    fun mapExample(): Mono<String> =
        Mono.just(42)
            .map { number -> number * 2 }           // 84
            .map { doubled -> "결과: $doubled" }    // "결과: 84"

    // ──────────────────────────────────────────────
    // 3. flatMap — 비동기 작업을 체이닝
    // ──────────────────────────────────────────────

    /**
     * flatMap { } 은 안에서 또 다른 Mono를 반환할 때 사용한다.
     * map은 T → R, flatMap은 T → Mono<R>.
     *
     * 예) 사용자 ID로 사용자를 조회한 뒤, 그 결과로 다시 권한을 조회하는 흐름.
     */
    fun flatMapExample(): Mono<String> =
        fetchUserId()
            .flatMap { id -> fetchUserName(id) }
            .flatMap { name -> buildGreeting(name) }

    private fun fetchUserId(): Mono<Int> =
        Mono.just(1001)

    private fun fetchUserName(id: Int): Mono<String> =
        Mono.just("유저-$id")

    private fun buildGreeting(name: String): Mono<String> =
        Mono.just("안녕하세요, $name 님!")

    // ──────────────────────────────────────────────
    // 4. onErrorResume — 에러 발생 시 대체 Mono로 복구
    // ──────────────────────────────────────────────

    /**
     * onErrorResume { } 은 에러가 발생했을 때 대체 Mono를 반환한다.
     * 예외 타입으로 필터링하거나, 로깅 후 fallback 값을 내릴 수 있다.
     */
    fun onErrorResumeExample(): Mono<String> =
        Mono.fromCallable<String> { error("의도적으로 발생시킨 에러") }
            .onErrorResume { ex ->
                Mono.just("onErrorResume: 에러 복구 → ${ex.message}")
            }

    // ──────────────────────────────────────────────
    // 5. onErrorReturn — 에러 발생 시 기본값으로 대체
    // ──────────────────────────────────────────────

    /**
     * onErrorReturn(value) 은 에러가 나면 고정 값 하나를 돌려준다.
     * 대체 로직이 필요 없을 때 onErrorResume 보다 간결하다.
     */
    fun onErrorReturnExample(): Mono<String> =
        Mono.fromCallable<String> { error("에러 발생") }
            .onErrorReturn("onErrorReturn: 기본값으로 대체")

    // ──────────────────────────────────────────────
    // 6. 실전 패턴 — fromCallable + flatMap + onErrorResume 조합
    // ──────────────────────────────────────────────

    /**
     * 실제 서비스에서 자주 쓰이는 조합:
     * ① fromCallable 로 동기 작업 래핑
     * ② flatMap 으로 추가 비동기 처리
     * ③ onErrorResume 으로 에러 복구
     */
    fun combined(): Mono<String> =
        Mono.fromCallable { "원본 데이터" }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { data -> Mono.just("[$data] → flatMap 처리 완료") }
            .onErrorResume { ex -> Mono.just("에러 복구: ${ex.message}") }

    // ──────────────────────────────────────────────
    // 7. 핵심 정리
    // ──────────────────────────────────────────────

    fun summary(): Mono<String> = Mono.just(
        """
        [Step16 — 리액티브 Service 핵심 요약]

        ① fromCallable { }   : 동기 코드 → Mono로 래핑 (블로킹이면 +subscribeOn(boundedElastic))
        ② map { }            : T → R 동기 변환 (새 Mono 반환 X)
        ③ flatMap { }        : T → Mono<R> 비동기 체이닝 (Mono를 또 반환할 때)
        ④ onErrorResume { }  : 에러 → 대체 Mono 반환 (복구 로직 포함 가능)
        ⑤ onErrorReturn(v)   : 에러 → 고정 기본값 반환 (간결한 fallback)

        map vs flatMap 선택 기준:
          - 안에서 Mono를 반환하지 않는다 → map
          - 안에서 Mono를 반환한다       → flatMap
        """.trimIndent()
    )
}
