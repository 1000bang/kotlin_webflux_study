# 13단계: Controller/Service에서 Mono·Flux 쓰기

## 목표
- Controller와 Service에서 **Mono/Flux를 자연스럽게 반환**하는 패턴을 이해한다.
- \"Service는 동기, Controller에서 `Mono.just(service())`\" 패턴과의 차이를 안다.
- 여러 리액티브 메서드를 **조합(map/flatMap)** 해서 하나의 응답으로 만드는 예를 본다.

---

## 1. 기존 패턴 vs 리액티브 패턴

### 기존(동기) 패턴

- Service: `fun findUser(id: Long): User`
- Controller: `fun getUser(id: Long): User = userService.findUser(id)`

### WebFlux에서 자주 보이는 패턴

- Service는 그대로 동기: `fun findUser(id: Long): User`
- Controller에서만 감싸기: `fun getUser(id: Long): Mono<User> = Mono.just(userService.findUser(id))`

이 패턴은 **동작은 하지만**, Service가 여전히 **블로킹**이라서, 전체적으로는 완전한 리액티브 구조가 아니다.

### 리액티브 패턴

- Service: `fun findUser(id: Long): Mono<User>`
- Controller: `fun getUser(id: Long): Mono<User> = userService.findUser(id)`

Service에서부터 Mono/Flux를 반환하면, **상위 계층까지 리액티브 체인**을 자연스럽게 이어 갈 수 있다.

---

## 2. Service에서 Mono/Flux 반환하기

- 한 건: `fun hello(): Mono<String> = Mono.just("hello")`
- 여러 건: `fun numbers(): Flux<Int> = Flux.range(1, 3)`
- 리스트 응답으로 바꿀 때: `numbers().collectList()` → `Mono<List<Int>>`

Service 내부에서도 `map`, `flatMap` 으로 **다른 리액티브 메서드와 조합**할 수 있다.

---

## 3. Controller에서 그대로 반환하기

- Controller에서는 `Mono`/`Flux`를 **그대로 반환**한다.
- WebFlux가 알아서 구독하고, 결과를 HTTP 응답으로 쓴다.

예:

- `@GetMapping("/step13") fun step13(): Mono<String> = step13Service.composed()`

---

## 4. 실전에서 어떻게 쓰는지

| 상황 | 어떻게 |
|------|--------|
| Service가 동기인 레거시 | 일단 Controller에서 `Mono.fromCallable { service() }` + `subscribeOn(boundedElastic())` 로 감싸고, 점진적으로 Service 시그니처를 Mono/Flux로 바꿔 간다. |
| 새 코드 | Service부터 `Mono/Flux` 반환, Controller는 그대로 반환. map/flatMap으로 다른 리액티브 메서드와 조합. |
| 여러 서비스 조합 | `serviceA().flatMap { a -> serviceB(a).map { b -> ... } }` 처럼 체인으로 연결. |

---

## 5. 주의점

- `Mono.just(service())` 는 **service()가 먼저 실행**된 뒤 Mono로 감싸는 것이므로, service()가 블로킹이면 그 지점에서 이미 스레드가 막힌다.
- Service부터 Mono/Flux를 반환하면, 나중에 R2DBC·WebClient 같은 리액티브 구현으로 바꾸기 수월하다.

