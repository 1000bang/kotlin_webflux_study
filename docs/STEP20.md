# 20단계: 실전 패턴 (Part 5 — 정리)

## 목표
- Controller → Service 전체 흐름을 실전 패턴으로 정리한다.
- `Mono.error` + `GlobalExceptionHandler`로 에러를 일관성 있게 처리한다.
- `doOn*` 연산자로 리액티브 체인에 로깅을 올바르게 삽입한다.

---

## 1. 전체 흐름 (실전 패턴)

```
Client
  ↓ GET /test/step20/items/{id}
Part5Controller
  ↓ step20Service.findItem(id)
Step20Service
  ├─ 입력 검증 실패 → Mono.error(ValidationException)  ─→ 400
  ├─ 조회 결과 없음 → Mono.error(NotFoundException)    ─→ 404
  └─ 정상           → Mono.just("아이템 정보")          ─→ 200
        ↓
  GlobalExceptionHandler (@RestControllerAdvice)
        ↓
  ErrorResponse { code, message } JSON 응답
```

---

## 2. 예외 계층 설계

```kotlin
// sealed class → when 분기에서 else 없이 모든 케이스 처리 가능
sealed class BusinessException(message: String) : RuntimeException(message)

class NotFoundException(message: String)  : BusinessException(message)  // 404
class ValidationException(message: String): BusinessException(message)  // 400

data class ErrorResponse(val code: String, val message: String)
```

---

## 3. GlobalExceptionHandler

```kotlin
@RestControllerAdvice   // WebFlux에서도 동일하게 동작
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NotFoundException): ErrorResponse =
        ErrorResponse(code = "NOT_FOUND", message = ex.message ?: "")

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: ValidationException): ErrorResponse =
        ErrorResponse(code = "VALIDATION_ERROR", message = ex.message ?: "")

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneral(ex: Exception): ErrorResponse =
        ErrorResponse(code = "INTERNAL_ERROR", message = "서버 오류가 발생했습니다")
}
```

> MVC와 완전히 동일한 방식으로 WebFlux에서도 `@RestControllerAdvice`가 동작한다.

---

## 4. Service 에러 처리 패턴

```kotlin
fun findItem(id: Long): Mono<String> {
    // ① 입력 검증 → ValidationException (400)
    if (id <= 0) return Mono.error(ValidationException("id는 양수여야 합니다"))

    return Mono.justOrEmpty(findFromDb(id))
        // ② 없으면 NotFoundException (404)
        .switchIfEmpty(Mono.error(NotFoundException("아이템 없음: id=$id")))
        // ③ 결과 변환
        .map { name -> "아이템[$id]: $name" }
}
```

### 에러 처리 연산자 비교

| 연산자 | 용도 |
|---|---|
| `Mono.error(ex)` | 에러 신호 발행 → GlobalExceptionHandler로 전파 |
| `switchIfEmpty { }` | empty Mono → 에러 또는 기본값으로 대체 |
| `onErrorResume { }` | 에러 → 대체 Mono 반환 (복구, 전파 안 함) |
| `onErrorReturn(value)` | 에러 → 고정 기본값 반환 |
| `onErrorMap { }` | 에러 → 다른 예외 타입으로 변환 후 전파 |

### onErrorMap 예시 — 외부 예외를 도메인 예외로 변환

```kotlin
fun callExternalApi(param: String): Mono<String> =
    Mono.fromCallable { externalCall(param) }
        .onErrorMap(IllegalStateException::class.java) { ex ->
            // 외부 라이브러리 예외 → 내부 도메인 예외로 래핑
            ValidationException("외부 API 오류: ${ex.message}")
        }
```

---

## 5. 로깅 — doOn* 사이드 이펙트 연산자

리액티브 체인에서 `map { }` 안에 로그를 찍는 것은 사이드 이펙트와 변환 로직이 섞이는 나쁜 패턴이다.
`doOn*` 연산자를 별도로 체이닝하는 것이 올바른 방법이다.

```kotlin
private val log = LoggerFactory.getLogger(javaClass)

fun findItemWithLogging(id: Long): Mono<String> =
    findItem(id)
        .doOnSubscribe { log.info("[요청] findItem 시작: id={}", id) }
        .doOnNext      { result -> log.info("[응답] findItem 완료: {}", result) }
        .doOnError     { ex -> log.error("[에러] findItem 실패: id={}, error={}", id, ex.message) }
```

### doOn* 연산자 정리

| 연산자 | 실행 시점 |
|---|---|
| `doOnSubscribe` | 구독이 시작될 때 (요청 수신) |
| `doOnNext` | 값이 발행될 때 |
| `doOnError` | 에러가 발생할 때 |
| `doOnSuccess` | Mono가 값 또는 empty로 완료될 때 (Mono 전용) |
| `doOnComplete` | Flux가 모든 값을 발행하고 완료될 때 (Flux 전용) |
| `doFinally` | 완료·에러·취소 모두 실행 (finally와 동일) |

---

## 6. 엔드포인트 정리

| Method | URL | 설명 |
|---|---|---|
| GET | `/test/step20/summary` | 핵심 요약 |
| GET | `/test/step20/items/{id}` | 실전 패턴 (검증 → 조회 → 변환 → 에러 처리) |
| GET | `/test/step20/items/{id}/log` | doOn* 로깅 적용 버전 (서버 콘솔 확인) |
| GET | `/test/step20/external?param=hello` | onErrorMap 예시 |

```bash
# 정상 조회 (200)
curl http://localhost:8080/test/step20/items/1

# 없는 아이템 (404)
curl http://localhost:8080/test/step20/items/99

# 잘못된 id (400)
curl http://localhost:8080/test/step20/items/-1

# 외부 API 에러 → ValidationException (400)
curl "http://localhost:8080/test/step20/external?param=error"

# 외부 API 정상 (200)
curl "http://localhost:8080/test/step20/external?param=hello"
```

---

## 7. 전체 프로젝트 패키지 구조 (최종)

```
com.everydoc/
├── controller/
│   ├── part1/ ~ part4/   — Step 1~19 Controller
│   └── part5/            — Step 20 Controller
├── service/
│   ├── part1/ ~ part4/   — Step 1~19 Service
│   └── part5/            — Step 20 Service
├── domain/
│   └── Order.kt          — R2DBC 엔티티
├── repository/
│   └── OrderRepository.kt — ReactiveCrudRepository
└── exception/
    ├── BusinessException.kt      — 예외 계층 + ErrorResponse
    └── GlobalExceptionHandler.kt — @RestControllerAdvice
```

---

## 8. 전체 학습 흐름 정리

| Part | Steps | 핵심 |
|---|---|---|
| Part 1 | 1~5 | Kotlin 기초 문법 |
| Part 2 | 6~9 | 확장 함수, 스코프 함수 |
| Part 3 | 10~13 | WebFlux 개념, Reactor |
| Part 4 | 14~19 | WebFlux 실습, R2DBC, 테스트 |
| Part 5 | 20 | 실전 패턴 — 예외 처리, 로깅 |
