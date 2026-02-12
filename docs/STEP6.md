# 6단계: 코틀린 특징 요약

## 목표
- 지금까지 배운 내용을 **정적 타입**, **간결한 문법**, **null-safe**, **데이터 클래스**, **확장 함수**, **코루틴** 관점에서 정리한다.
- Part 2(확장 함수, 스코프 함수, 왜 코틀린인가)로 넘어가기 전에 전체 그림을 잡는다.

---

## 1. 정적 타입 (Static Typing)

- 컴파일 시점에 타입이 정해진다. 타입 추론으로 `val x = 42` 처럼 생략 가능.
- `String?` 처럼 nullable 여부까지 타입에 포함되어 NPE를 컴파일 단계에서 줄일 수 있다.

```kotlin
val a = 42           // Int
val b: String? = null // nullable
```

---

## 2. 간결한 문법

- **단일 표현식 함수**: `fun add(a: Int, b: Int) = a + b`
- **data class**: getter/setter, equals, hashCode, toString, copy 자동 생성
- **람다**: `list.filter { it % 2 == 0 }`, **이름 인자** `User(name = "토스", id = 1L)`
- **when**, **문자열 템플릿** `"$name"`, **컬렉션 함수** map/filter/fold

---

## 3. Null-safe (널 안정성)

- **타입?** 로 nullable 표현. **?.**, **?:**, **let**, **requireNotNull** 등으로 null 처리.
- non-null 타입에는 컴파일러가 null 대입을 막아 준다.

---

## 4. 데이터 클래스 (data class)

- DTO·값 객체를 한 줄로 정의. **구조 분해**, **copy()** 지원.
- Lombok 없이 보일러플레이트를 줄일 수 있다.

```kotlin
data class UserDto(val id: Long, val name: String, val email: String? = null)
val (id, name, _) = user
```

---

## 5. 확장 함수 (Extension Function) — 맛보기

- 기존 타입에 **멤버처럼** 새 함수를 붙일 수 있다. Step 7에서 자세히.

```kotlin
fun String.addPrefix(prefix: String) = "$prefix$this"
"토스".addPrefix("회사: ")  // "회사: 토스"
```

---

## 6. 코루틴 (Coroutine) — 맛보기

- 비동기·논블로킹 코드를 **순차적으로 쓰는 것처럼** 작성할 수 있다. WebFlux(Reactor)와 다른 스타일.
- `suspend fun`, `launch`, `async` 등은 Part 4 이후 단계에서 다룬다.

```kotlin
// 코루틴 예시 (의존성·스코프 필요)
// launch { delay(1000); println("done") }
```

---

## 이 프로젝트에서 확인하기
- `Step6Service.kt` 에서 각 특징을 짧게 연습하는 함수 실행해 보기.
- `./gradlew bootRun` 후 `http://localhost:8080/test/step6` 호출해 동작 확인.
