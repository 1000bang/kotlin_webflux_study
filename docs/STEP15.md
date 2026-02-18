# 15단계: 리액티브 Controller

## 목표
- `@RestController`에서 `Mono` / `Flux`를 반환하는 패턴을 이해한다.
- `@RequestBody`, `@PathVariable`, `@RequestParam`을 리액티브 방식으로 활용한다.
- Controller에서 절대 `block()`을 호출하면 안 되는 이유를 안다.

---

## 1. Mono 반환 — 단건 응답

```kotlin
@GetMapping("/step15")
fun step15(): Mono<String> = step15Service.monoExample()
```

- Controller 메서드가 `Mono<T>`를 return하면 **WebFlux가 자동으로 subscribe**해서 값이 나오는 시점에 HTTP 응답을 전송한다.
- `Mono.just(...)` 는 이미 값이 있을 때, `Mono.fromCallable { ... }` 는 동기 작업을 감쌀 때 사용한다.

---

## 2. Flux 반환 — 다건 스트림

```kotlin
@GetMapping("/step15/flux")
fun step15Flux(): Flux<String> = step15Service.fluxExample()
```

| Accept 헤더 | 응답 형태 |
|---|---|
| `application/json` | `["첫 번째", "두 번째", "세 번째"]` (JSON Array) |
| `text/event-stream` | SSE(Server-Sent Events) 스트림 |

---

## 3. @RequestBody를 Mono로 받기

```kotlin
@PostMapping("/step15/echo")
fun step15Echo(@RequestBody body: Mono<String>): Mono<String> =
    step15Service.echoBody(body)
```

- `@RequestBody`의 타입을 `Mono<T>`로 선언하면 본문이 도착할 때까지 **논블로킹**으로 기다린다.
- Service에서는 `body.map { ... }` 으로 체인을 이어간다.

```kotlin
// Step15Service
fun echoBody(body: Mono<String>): Mono<String> =
    body.map { received -> "받은 내용: $received" }
```

---

## 4. @PathVariable / @RequestParam

```kotlin
@GetMapping("/step15/greet/{name}")
fun step15Greet(
    @PathVariable name: String,
    @RequestParam(defaultValue = "1") repeat: Int,
): Mono<String> = step15Service.greet(name, repeat)
```

- `@PathVariable`, `@RequestParam`은 **일반 타입**으로 받은 뒤 Service에서 `Mono.just()`로 감싸 리액티브 체인에 진입시킨다.

```kotlin
// Step15Service
fun greet(name: String, repeat: Int): Mono<String> =
    Mono.just(name)
        .map { n -> "안녕하세요, $n!".repeat(repeat) }
```

---

## 5. ResponseEntity로 상태 코드 제어

직접 상태 코드나 헤더를 설정해야 할 때는 `ResponseEntity<Mono<T>>` 대신 `Mono<ResponseEntity<T>>`를 사용한다.

```kotlin
@PostMapping("/resource")
fun create(@RequestBody body: Mono<String>): Mono<ResponseEntity<String>> =
    body.map { data ->
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body("생성됨: $data")
    }
```

---

## 6. 핵심 규칙

| 규칙 | 이유 |
|---|---|
| Controller에서 `block()` 금지 | Netty 이벤트 루프 스레드를 점유해 전체 서버가 멈춤 |
| Service가 반환한 `Mono/Flux`를 그대로 `return` | 구독은 WebFlux 프레임워크가 담당 |
| 동기 코드가 필요하면 `subscribeOn(Schedulers.boundedElastic())` | I/O 블로킹 작업을 별도 스레드 풀로 오프로드 |

---

## 7. 엔드포인트 정리

| Method | URL | 설명 |
|---|---|---|
| GET | `/test/step15` | Mono 단건 반환 |
| GET | `/test/step15/summary` | 핵심 요약 |
| GET | `/test/step15/flux` | Flux 다건 스트림 |
| POST | `/test/step15/echo` | @RequestBody Mono 수신 |
| GET | `/test/step15/greet/{name}?repeat=N` | PathVariable + RequestParam |
