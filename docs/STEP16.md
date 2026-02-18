# 16단계: 리액티브 Service

## 목표
- `Mono.fromCallable`로 동기 코드를 리액티브 체인 안으로 안전하게 감싸는 방법을 안다.
- `map` / `flatMap`의 차이를 이해하고 올바르게 체이닝한다.
- `onErrorResume` / `onErrorReturn`으로 에러를 우아하게 복구한다.

---

## 1. Mono.fromCallable — 동기 코드를 Mono로 래핑

```kotlin
fun fromCallableExample(): Mono<String> =
    Mono.fromCallable {
        // DB 조회, 파일 읽기 등 동기 작업
        "fromCallable: 동기 작업의 결과값"
    }.subscribeOn(Schedulers.boundedElastic())
```

| 상황 | 선택 |
|---|---|
| 이미 값이 있다 | `Mono.just(value)` |
| 동기 작업이 필요하다 (논블로킹) | `Mono.fromCallable { }` |
| 동기 작업이 필요하다 (블로킹 I/O) | `Mono.fromCallable { }.subscribeOn(Schedulers.boundedElastic())` |

> **주의:** 블로킹 코드(`Thread.sleep`, JDBC, 파일 읽기 등)를 `fromCallable` 없이 Mono 체인 안에 직접 쓰면 Netty 이벤트 루프 스레드를 점유해 성능이 크게 저하된다.

---

## 2. map — 동기 변환

```kotlin
fun mapExample(): Mono<String> =
    Mono.just(42)
        .map { number -> number * 2 }        // 84
        .map { doubled -> "결과: $doubled" } // "결과: 84"
```

- `map { }` 안의 람다는 **값을 바로 반환**한다 (`T → R`).
- 새로운 `Mono`를 반환하지 않는다.

---

## 3. flatMap — 비동기 체이닝

```kotlin
fun flatMapExample(): Mono<String> =
    fetchUserId()
        .flatMap { id -> fetchUserName(id) }   // Mono<Int> → Mono<String>
        .flatMap { name -> buildGreeting(name) } // Mono<String> → Mono<String>
```

- `flatMap { }` 안의 람다는 **Mono를 반환**한다 (`T → Mono<R>`).
- 순차적 비동기 작업을 체이닝할 때 사용한다.

### map vs flatMap 선택 기준

```
람다 안에서 Mono를 반환하지 않는다 → map
람다 안에서 Mono를 반환한다       → flatMap
```

---

## 4. onErrorResume — 에러 시 대체 Mono 반환

```kotlin
fun onErrorResumeExample(): Mono<String> =
    Mono.fromCallable<String> { error("에러 발생") }
        .onErrorResume { ex ->
            Mono.just("복구: ${ex.message}")
        }
```

- 에러가 발생하면 람다가 실행되고, 반환한 `Mono`로 스트림을 이어간다.
- 예외 타입을 지정해 특정 에러만 처리할 수도 있다.

```kotlin
.onErrorResume(IllegalStateException::class.java) { ex ->
    Mono.just("IllegalState 에러만 복구")
}
```

---

## 5. onErrorReturn — 에러 시 기본값 반환

```kotlin
fun onErrorReturnExample(): Mono<String> =
    Mono.fromCallable<String> { error("에러 발생") }
        .onErrorReturn("기본값으로 대체")
```

- 복구 로직 없이 고정 값 하나를 반환할 때 `onErrorResume`보다 간결하다.

---

## 6. 실전 조합 패턴

```kotlin
fun combined(): Mono<String> =
    Mono.fromCallable { "원본 데이터" }               // ① 동기 작업 래핑
        .subscribeOn(Schedulers.boundedElastic())    // ② 블로킹이면 스케줄러 지정
        .flatMap { data ->                           // ③ 비동기 후속 처리
            Mono.just("[$data] → flatMap 처리 완료")
        }
        .onErrorResume { ex ->                       // ④ 에러 복구
            Mono.just("에러 복구: ${ex.message}")
        }
```

---

## 7. 엔드포인트 정리

| Method | URL | 설명 |
|---|---|---|
| GET | `/test/step16` | `Mono.fromCallable` 예시 |
| GET | `/test/step16/summary` | 핵심 요약 |
| GET | `/test/step16/map` | `map` 체이닝 예시 |
| GET | `/test/step16/flatmap` | `flatMap` 체이닝 예시 |
| GET | `/test/step16/error-resume` | `onErrorResume` 예시 |
| GET | `/test/step16/error-return` | `onErrorReturn` 예시 |
| GET | `/test/step16/combined` | `fromCallable + flatMap + onErrorResume` 조합 |
