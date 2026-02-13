# 7단계: 확장 함수·프로퍼티 (Extension Function / Property)

## 목표
- **확장 함수** `fun 타입.함수명()` 문법으로 기존 타입에 함수를 붙일 수 있다.
- **확장 프로퍼티**로 getter(필요 시 setter)를 붙일 수 있다.
- 유틸 함수를 멤버처럼 `객체.함수()` 형태로 쓸 수 있다.

---

## 0. 왜 쓰는가?

### 유틸 클래스 vs 확장 함수

자바에서는 흔히 **유틸 클래스**에 static 메서드로 모아 둔다.

```java
// 자바: 인자가 항상 앞에 온다. "대상"이 뒤로 밀린다.
String result = StringUtil.addPrefix("토스", "회사: ");
boolean ok = ValidationUtil.isValidEmail(email);
```

호출할 때마다 **"대상 객체"가 인자로 넘어가서**, 문장이 "동사(유틸) + 목적어(대상)" 순이 된다. 사람이 읽을 때는 **"대상이 뭘 한다"**가 더 자연스러운데, 유틸은 **"유틸이 대상을 처리한다"** 순서라서 가독성이 떨어진다.

```kotlin
// 코틀린 확장: "대상이 뭘 한다" 순서. 주어가 앞에 온다.
val result = "토스".addPrefix("회사: ")
val ok = email.isValidEmail()
```

- **가독성**: `email.isValidEmail()` 이 `ValidationUtil.isValidEmail(email)` 보다 의도가 잘 드러난다.
- **자동완성**: `"문자열".` 까지 치면 그 타입에 쓸 수 있는 확장이 같이 뜬다. 유틸 클래스 이름을 외우고 있을 필요가 줄어든다.
- **기존 타입 수정 불가할 때**: JDK·서드파티 라이브러리의 `String`, `List`, `LocalDateTime` 등은 우리가 수정할 수 없다. 확장은 **클래스 코드를 건드리지 않고** 그 타입에 메서드를 붙인 것처럼 쓸 수 있게 해 준다.
- **도메인 표현**: "이 문자열은 이메일 형식인가?", "이 리스트를 API 응답용 DTO 리스트로 바꿔라" 같은 걸 `email.isValidEmail()`, `list.toResponseDtos()` 처럼 **도메인 언어에 가깝게** 쓸 수 있다.

정리하면, **"그 타입이 하는 동작"을 문법적으로 그 타입에 붙여서 쓰고 싶을 때** 확장 함수를 쓰면 된다. 유틸은 그대로 두고, **호출만** `대상.함수()` 형태로 바꾸고 싶을 때도 확장으로 감싸서 쓸 수 있다.

---

## 1. 확장 함수 문법

```kotlin
fun String.addPrefix(prefix: String): String = "$prefix$this"

"토스".addPrefix("회사: ")  // "회사: 토스"
```

- **수신자 타입(Receiver type)**: `String.` → 이 확장은 `String` 인스턴스에 붙는다.
- **수신자 객체(Receiver object)**: 호출 시 `"토스"` 가 `this` 로 전달된다.
- **정적 디스패치**: 실제로는 정적 함수로 컴파일된다. 상속·오버라이드와 무관하다.
- 기존 클래스를 수정하지 않고, **다른 파일·패키지**에서도 확장을 정의할 수 있다.

---

## 2. 확장 함수 예시 (유틸을 멤버처럼)

```kotlin
fun String?.orEmpty(): String = this ?: ""

fun List<Int>.secondOrNull(): Int? = this.getOrNull(1)

fun Int.isPositive(): Boolean = this > 0
10.isPositive()  // true
```

- nullable 수신자: `String?.orEmpty()` → null 이면 `""` 반환.
- 제네릭 타입에도 확장 가능: `List<Int>.secondOrNull()`.

---

## 3. 확장 프로퍼티 (Extension Property)

```kotlin
val String.lastChar: Char
    get() = this[length - 1]

"토스".lastChar  // '스'
```

- **get()** 만 있으면 **val** 확장 프로퍼티. **set()** 도 주면 **var** (가변 타입일 때만).
- 필드를 붙이는 게 아니라, getter/setter 로 계산된 값을 노출한다.

```kotlin
val List<Int>.secondToLast: Int?
    get() = this.getOrNull(size - 2)
```

---

## 4. 실전에서 어떻게 쓰는지

### 4-1. String: null·빈 문자열 처리

```kotlin
// API·DB에서 올 수 있는 nullable String을 안전하게 다룰 때
fun String?.orEmpty(): String = this ?: ""
fun String?.toNullOrBlank(): String? = if (this.isNullOrBlank()) null else this

val name: String? = ...
val display = name.orEmpty()           // null이면 ""
val trimmed: String? = input.toNullOrBlank()  // "" 나 공백만 있으면 null
```

- 컨트롤러·서비스에서 **"값이 없으면 기본값"** 처리할 때 자주 쓴다.
- 코틀린 표준 라이브러리에도 `String?.orEmpty()` 가 이미 있다. 같은 패턴을 **우리 도메인 규칙**에 맞게 한 번 더 감싸서 쓸 수 있다.

### 4-2. DTO·엔티티 변환 (toResponse, toEntity)

```kotlin
// 엔티티 → API 응답 DTO 변환을 "그 타입이 할 수 있는 일"로 표현
fun UserEntity.toResponse(): UserResponseDto = UserResponseDto(
    id = id,
    name = name,
    email = email.orEmpty(),
)

// 서비스에서
val dto = userEntity.toResponse()
// 리스트일 때
val list = entities.map { it.toResponse() }
```

- `UserConverter.toResponse(entity)` 보다 `entity.toResponse()` 가 **"이 엔티티를 응답용으로 바꾼다"**는 뜻이 분명하다.
- `map { it.toResponse() }` 로 리스트 변환도 짧고 읽기 쉽다.

### 4-3. 검증·비즈니스 규칙 (isValid, requireXxx)

```kotlin
fun String.isValidEmail(): Boolean = this.matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))

// 사용
if (!email.isValidEmail()) throw IllegalArgumentException("Invalid email")
// 또는
require(email.isValidEmail()) { "Invalid email" }
```

- "이 문자열이 이메일 형식인가?"를 **그 타입의 능력**처럼 쓸 수 있다.
- `ValidationUtil.isValidEmail(email)` 대신 `email.isValidEmail()` 로 호출부가 단순해진다.

### 4-4. 컬렉션·컬렉션 요소 가공

```kotlin
// 두 번째 요소만 꺼내는 건 여러 곳에서 쓸 수 있음
fun <T> List<T>.secondOrNull(): T? = this.getOrNull(1)

// "비어 있지 않을 때만 첫 요소" 등
fun <T> List<T>.firstOrNull(): T? = if (isEmpty()) null else this[0]
```

- `list.getOrNull(1)` 을 매번 쓰기보다 `list.secondOrNull()` 이 의도가 더 잘 드러난다.
- **nullable 수신자**로 `List<T>?.secondOrNull()` 도 정의하면, null 리스트에서도 안전하게 쓸 수 있다.

### 4-5. 날짜·숫자 포맷 (API 응답용)

```kotlin
fun LocalDateTime.toApiFormat(): String = this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
fun Long.toWon(): String = "%,d원".format(this)
```

- "이 날짜를 API 스펙 포맷으로", "이 숫자를 원화 문자열로" 같은 변환을 **그 타입의 메서드**처럼 둘 수 있다.
- 포맷 규칙이 바뀌어도 확장 구현 한 곳만 수정하면 된다.

### 4-6. 로깅·디버깅 (선택)

```kotlin
fun Any?.toLogString(): String = when (this) {
    null -> "null"
    is String -> this
    else -> toString()
}
// 또는 민감 정보 마스킹
fun String.maskMiddle(): String = ...
```

- "이 값을 로그에 안전하게 찍을 때" 같은 공통 규칙을 확장으로 두면, 호출부가 짧아진다.

### 실전에서 정리

| 상황 | 확장 없이 | 확장으로 |
|------|-----------|----------|
| nullable String 기본값 | `name ?: ""` | `name.orEmpty()` (의도 이름 붙이기) |
| 엔티티 → DTO | `UserConverter.toDto(entity)` | `entity.toResponse()` |
| 이메일 검증 | `ValidationUtil.isValidEmail(email)` | `email.isValidEmail()` |
| 리스트 두 번째 | `list.getOrNull(1)` | `list.secondOrNull()` |
| 날짜 포맷 | `format(date, formatter)` | `date.toApiFormat()` |

- **공통으로 두 번 이상 쓰는 변환·검증**을 확장으로 빼 두면, 호출하는 쪽은 **"대상.동작()"** 형태로 읽히고, 수정도 한 곳에서만 하면 된다.
- 남의 코드(JDK, 라이브러리)를 수정할 수 없을 때, 우리 프로젝트만의 **추가 동작**을 붙일 때도 확장이 유리하다.

---

## 5. 사용처 요약

- **유틸 함수**: `StringUtil.capitalize(s)` 대신 `s.capitalize()` 처럼 호출.
- **가독성**: `list.filter { it.isValid() }` 에서 `isValid()` 를 확장으로 정의하면 도메인 표현이 명확해진다.
- **기존 라이브러리 확장**: JDK·서드파티 타입에 메서드를 추가한 것처럼 쓸 수 있다 (클래스 수정 없이).

---

## 6. 주의점

- **정적 디스패치**: 수신자 타입이 **컴파일 시점**에 정해진 타입으로 결정된다. 다형성(오버라이드)은 적용되지 않는다.
- **캡슐화 깨지 않음**: private 멤버에는 접근할 수 없다. public API 기준으로만 확장한다.
- **같은 시그니처**: 클래스에 이미 같은 이름·시그니처 멤버가 있으면 **멤버가 우선**이다.

---

## 이 프로젝트에서 확인하기
- `Step7Service.kt` 에서 확장 함수·확장 프로퍼티 연습 함수 실행해 보기.
- `./gradlew bootRun` 후 `http://localhost:8080/test/step7` 호출해 동작 확인.
