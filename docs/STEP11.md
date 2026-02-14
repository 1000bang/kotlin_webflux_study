# 11단계: Reactor 기초 (Mono, Flux)

## 목표
- **Mono**(0 또는 1개 값)와 **Flux**(0~N개 값)의 차이를 안다.
- **just, map, flatMap, subscribe** 같은 기본 연산을 쓸 수 있다.
- **lazy(구독 전까지 실행 안 함)** 와 **block() 주의점**을 이해한다.

---

## 1. Mono vs Flux

| 타입 | 의미 | 예 |
|------|------|-----|
| **Mono<T>** | 0개 또는 **1개**의 값을 발행 | API 응답 한 건, 단일 조회 결과 |
| **Flux<T>** | 0개, 1개, 또는 **여러 개**의 값을 스트림으로 발행 | 목록 조회, SSE, 스트리밍 |

- 둘 다 **Publisher**(리액티브 스트림). 구독(subscribe)하기 전에는 **아무 일도 하지 않는다(lazy)**.
- WebFlux Controller에서 `Mono<Foo>`를 반환하면, 구독은 프레임워크가 하고 결과를 HTTP 응답으로 쓴다.

---

## 2. 값 만들기

- **Mono.just(value)**: 값 하나로 Mono 생성. **null이면 안 됨**(NPE). null 가능하면 `Mono.justOrEmpty(value)` 또는 `Mono.empty()`.
- **Mono.empty()**: 값을 안 넘기고 완료만.
- **Flux.just(a, b, c)**: 여러 개를 순서대로 발행.
- **Flux.fromIterable(list)**: 리스트·컬렉션을 Flux로.

---

## 3. 변환·조합

- **map**: 발행된 값을 **동기적으로** 변환. `Mono.just(1).map { it + 1 }` → 2.
- **flatMap**: 값을 **다른 Mono/Flux**로 바꾼 뒤 그걸 펼쳐서 이어짐. 비동기 연쇄에 사용. `Mono.just(id).flatMap { findUser(it) }` → `Mono<User>`.
- **filter**: 조건을 만족하는 값만 넘김.

---

## 4. 구독 (subscribe)

- **subscribe()**: 구독을 시작한다. 보통 Controller에서는 반환만 하면 WebFlux가 구독하므로 직접 안 쓴다.
- **block()**: 결과가 나올 때까지 **현재 스레드를 블로킹**한다. 테스트·레거시 연동용. **프로덕션 리액티브 체인 안에서는 쓰지 말 것.**

---

## 5. 실전에서 어떻게 쓰는지

- **한 건 응답**: `Mono.just(step11Service.one())` 또는 Service가 `Mono<String>` 반환.
- **여러 건 응답**: `Flux.fromIterable(list)` 또는 Service가 `Flux<Item>` 반환 후 `collectList()`로 리스트 응답으로 바꿀 수 있음.
- **연쇄 처리**: `Mono.just(id).flatMap(repository::findById).map(Dto::from)` 처럼 비동기 파이프라인을 이어 쓴다.

---

## 6. 주의점

- **block()**은 리액티브 스레드 풀을 막으므로, WebFlux 요청 처리 경로에서는 피하고, 테스트나 한 번만 호출하는 초기화 코드에서만 제한적으로 사용.
- **Mono.just(null)** 은 NPE. null 가능성 있으면 `justOrEmpty` / `empty()`.
- 구독이 일어나야 실제로 실행된다. `Mono.just(expensiveCall())`는 **만들 때** expensiveCall()이 실행되므로, 지연하려면 `Mono.fromCallable { expensiveCall() }` 사용.
