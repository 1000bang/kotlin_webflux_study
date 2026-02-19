package com.everydoc.step19

import com.everydoc.service.part4.Step19Service
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 19단계: @WebFluxTest + WebTestClient — HTTP 엔드포인트 슬라이스 테스트
 *
 * @WebFluxTest
 *   - 전체 Spring 컨텍스트를 띄우지 않고 WebFlux 레이어(Controller, Filter 등)만 로드.
 *   - Service, Repository 등은 @MockBean으로 대체한다.
 *   - 빠른 실행 속도가 장점.
 *
 * WebTestClient
 *   - 실제 HTTP 요청을 날리지 않고 애플리케이션 컨텍스트에 직접 바인딩해서 테스트.
 *   - exchange() → 응답 수신.
 *   - expectStatus() / expectBody() 등 체이닝으로 검증.
 */
@WebFluxTest
@Import(Step19Service::class)           // 실제 Service를 직접 로드 (Mock 없이 통합 검증)
class Step19ControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    // ──────────────────────────────────────────────
    // 1. 정상 응답 검증
    // ──────────────────────────────────────────────

    @Test
    fun `GET step19 greet - 200 OK 및 응답 본문 검증`() {
        webTestClient.get()
            .uri("/test/step19/greet?name=철수")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .isEqualTo("안녕하세요, 철수 님!")
    }

    @Test
    fun `GET step19 sum - 1부터 5까지 합 15 반환`() {
        webTestClient.get()
            .uri("/test/step19/sum")
            .exchange()
            .expectStatus().isOk
            .expectBody(Long::class.java)
            .isEqualTo(15L)
    }

    // ──────────────────────────────────────────────
    // 2. Flux 응답 (JSON Array) 검증
    // ──────────────────────────────────────────────

    @Test
    fun `GET step19 numbers - Flux를 JSON Array로 수신`() {
        webTestClient.get()
            .uri("/test/step19/numbers")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Int::class.java)
            .hasSize(5)
            .contains(1, 2, 3, 4, 5)
    }

    // ──────────────────────────────────────────────
    // 3. 에러 응답 검증
    // ──────────────────────────────────────────────

    @Test
    fun `GET step19 greet - 이름 공백이면 500 에러`() {
        webTestClient.get()
            .uri("/test/step19/greet?name=")
            .exchange()
            .expectStatus().is5xxServerError   // 에러 핸들러 없으면 500
    }

    // ──────────────────────────────────────────────
    // 4. @MockBean 사용 예시 (Service를 Mock으로 대체)
    // ──────────────────────────────────────────────

    /**
     * @MockBean을 사용하면 실제 Service 없이 가짜 응답을 주입할 수 있다.
     * 아래는 주석으로 패턴만 보여주는 예시다.
     *
     * @MockBean
     * lateinit var step19Service: Step19Service
     *
     * @Test
     * fun `mock 예시 - given으로 반환값 지정`() {
     *     given(step19Service.greet("테스트")).willReturn(Mono.just("모의 응답"))
     *
     *     webTestClient.get()
     *         .uri("/test/step19/greet?name=테스트")
     *         .exchange()
     *         .expectStatus().isOk
     *         .expectBody(String::class.java).isEqualTo("모의 응답")
     * }
     */
    @Test
    fun `WebTestClient 패턴 설명`() {
        // WebTestClient 주요 메서드 정리:
        //   .get() / .post() / .put() / .delete()  — HTTP 메서드 선택
        //   .uri("/path")                           — 경로 지정
        //   .bodyValue(body)                        — 요청 본문 (POST/PUT)
        //   .exchange()                             — 요청 전송 → ResponseSpec 수신
        //   .expectStatus().isOk / .isCreated()     — 상태 코드 검증
        //   .expectBody(Type::class.java)           — 단건 응답 타입 검증
        //   .expectBodyList(Type::class.java)       — 배열 응답 타입 검증
        //   .returnResult()                         — 원시 결과 반환 (StepVerifier 연계 가능)
        assert(true)
    }
}
