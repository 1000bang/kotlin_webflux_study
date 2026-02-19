# 19단계: 테스트

## 목표
- `StepVerifier`로 `Mono` / `Flux`를 단위 테스트하는 방법을 안다.
- `WebTestClient`로 HTTP 엔드포인트를 테스트하는 방법을 안다.
- `@WebFluxTest`로 WebFlux 레이어만 로드하는 슬라이스 테스트를 구성한다.

---

## 1. 테스트 도구 비교

| 도구 | 대상 | 특징 |
|---|---|---|
| `StepVerifier` | `Mono` / `Flux` | 리액티브 스트림 단위 테스트, 값·에러·완료 신호 순서 검증 |
| `WebTestClient` | HTTP 엔드포인트 | 실제 HTTP 없이 컨텍스트에 바인딩, MVC·WebFlux 모두 지원 |
| `@WebFluxTest` | Controller 레이어 | 전체 컨텍스트 없이 WebFlux 컴포넌트만 로드 (빠른 속도) |

---

## 2. StepVerifier — Mono / Flux 단위 테스트

```kotlin
// 기본 패턴: create → 검증 체인 → verifyComplete()/verify()
StepVerifier.create(service.greet("철수"))
    .expectNext("안녕하세요, 철수 님!")
    .verifyComplete()               // 완료 신호 검증 + 구독 시작
```

### 주요 메서드

| 메서드 | 설명 |
|---|---|
| `expectNext(value)` | 다음 발행값이 `value`인지 검증 |
| `expectNext(v1, v2, ...)` | 여러 값을 순서대로 검증 |
| `expectNextCount(n)` | 값을 꺼내지 않고 n개 발행 기대 |
| `assertNext { ... }` | 람다로 값 내용 직접 검증 |
| `expectError(Type)` | 에러 타입 검증 |
| `verifyComplete()` | 완료 신호 검증 후 구독 시작 |
| `verify()` | 에러 신호 검증 후 구독 시작 |

### 케이스별 예시

```kotlin
// ① 정상값
StepVerifier.create(service.greet("철수"))
    .expectNext("안녕하세요, 철수 님!")
    .verifyComplete()

// ② 에러
StepVerifier.create(service.greet(""))
    .expectError(IllegalArgumentException::class.java)
    .verify()

// ③ Flux 순서 검증
StepVerifier.create(service.numbers())
    .expectNext(1, 2, 3, 4, 5)
    .verifyComplete()

// ④ Flux 개수만 검증
StepVerifier.create(service.numbers())
    .expectNextCount(5)
    .verifyComplete()

// ⑤ empty Mono (값 없이 완료)
StepVerifier.create(service.findOptional(0L))
    .verifyComplete()

// ⑥ assertNext — 람다로 검증
StepVerifier.create(service.greet("영희"))
    .assertNext { result ->
        assert(result.contains("영희"))
    }
    .verifyComplete()
```

---

## 3. @WebFluxTest — 슬라이스 테스트

```kotlin
@WebFluxTest
@Import(Step19Service::class)       // 실제 Service를 직접 로드
class Step19ControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient
}
```

- 전체 Spring 컨텍스트를 띄우지 않고 **WebFlux 레이어(Controller, Filter 등)만 로드**
- `Service`, `Repository` 등 나머지는 `@MockBean`으로 가짜 객체를 주입한다
- `@SpringBootTest`보다 훨씬 빠르게 실행된다

### @MockBean 사용 예시

```kotlin
@WebFluxTest(Part4Controller::class)
class ExampleControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var step19Service: Step19Service

    @Test
    fun `mock으로 응답 지정`() {
        given(step19Service.greet("테스트")).willReturn(Mono.just("모의 응답"))

        webTestClient.get()
            .uri("/test/step19/greet?name=테스트")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).isEqualTo("모의 응답")
    }
}
```

---

## 4. WebTestClient — HTTP 엔드포인트 테스트

```kotlin
// GET — 단건 응답
webTestClient.get()
    .uri("/test/step19/greet?name=철수")
    .exchange()
    .expectStatus().isOk
    .expectBody(String::class.java)
    .isEqualTo("안녕하세요, 철수 님!")

// GET — Flux (JSON Array) 응답
webTestClient.get()
    .uri("/test/step19/numbers")
    .exchange()
    .expectStatus().isOk
    .expectBodyList(Int::class.java)
    .hasSize(5)
    .contains(1, 2, 3, 4, 5)

// POST — 요청 본문 전송
webTestClient.post()
    .uri("/test/step18/orders")
    .bodyValue(order)
    .exchange()
    .expectStatus().isOk
    .expectBody(Order::class.java)
```

### WebTestClient 주요 메서드

| 메서드 | 설명 |
|---|---|
| `.get()` / `.post()` / `.put()` / `.delete()` / `.patch()` | HTTP 메서드 선택 |
| `.uri("/path")` | 경로 지정 |
| `.bodyValue(body)` | 요청 본문 (POST/PUT) |
| `.exchange()` | 요청 전송 → `ResponseSpec` 수신 |
| `.expectStatus().isOk` | 200 검증 |
| `.expectStatus().isCreated` | 201 검증 |
| `.expectStatus().is5xxServerError` | 5xx 검증 |
| `.expectBody(Type)` | 단건 응답 타입 검증 |
| `.expectBodyList(Type)` | 배열(Flux) 응답 타입 검증 |
| `.returnResult()` | 원시 결과 반환 (StepVerifier 연계 가능) |

---

## 5. 엔드포인트 정리

| Method | URL | 설명 |
|---|---|---|
| GET | `/test/step19/summary` | 핵심 요약 |
| GET | `/test/step19/greet?name=철수` | StepVerifier / WebTestClient 테스트 대상 |
| GET | `/test/step19/numbers` | Flux 테스트 대상 |
| GET | `/test/step19/sum` | 집계 Mono 테스트 대상 |

---

## 6. 테스트 파일 위치

```
src/test/kotlin/com/everydoc/
├── dto/
│   └── DtoNullHandlingTest.kt       (기존)
└── step19/
    ├── Step19ServiceTest.kt         ← StepVerifier 단위 테스트
    └── Step19ControllerTest.kt      ← @WebFluxTest + WebTestClient
```

```bash
# 전체 테스트 실행
./gradlew test

# Step19 테스트만 실행
./gradlew test --tests "com.everydoc.step19.*"
```
