# 4단계: 널 안정성 (Null Safety)

## 목표
- nullable 타입(`?`)과 non-null 타입을 구분해서 쓸 수 있다.
- `?.`(safe call), `?:`(elvis), `!!`, `let`, `require`/`check`로 null을 안전하게 다룰 수 있다.

---

## 1. nullable 타입 (`?`)

```kotlin
var name: String = "토스"
name = null  // 컴파일 에러

var nickname: String? = "토스페이"
nickname = null  // OK
```

- **타입?** = null을 허용하는 타입. **타입** (without ?) = null 불가.
- 컴파일 시점에 null 사용을 제한해서 NPE를 줄인다.

---

## 2. Safe call (`?.`)

```kotlin
val s: String? = null
val len = s?.length  // null이면 호출 안 하고 len도 null
val len2 = "abc".length  // non-null이면 그냥 .length
```

- **변수?.멤버** = 변수가 null이 아니면 멤버 접근, null이면 전체 식의 결과가 null.
- 연쇄: `a?.b?.c` → 중간에 null 나오면 뒤는 실행 안 함.

---

## 3. Elvis 연산자 (`?:`)

```kotlin
val name: String? = null
val display = name ?: "이름 없음"  // null이면 "이름 없음"

val len = name?.length ?: 0  // null이면 0
```

- **좌항 ?: 우항** = 좌항이 null이면 우항을 사용.
- 기본값·폴백을 한 줄로 쓸 때 유용.

---

## 4. Non-null 단언 (`!!`)

```kotlin
val s: String? = "hello"
val len = s!!.length  // "절대 null 아님"이라고 단언. null이면 NPE
```

- **변수!!** = "지금 null이 아니다"라고 컴파일러에게 알려줌. null이면 런타임에 NPE.
- 확실할 때만 쓰고, 가능하면 `?.` + `?:` 로 대체하는 게 좋다.

---

## 5. `let` (null이 아닐 때만 실행)

```kotlin
val email: String? = "toss@example.com"
val formatted = email?.let { "[$it]" }  // null이 아니면 블록 실행
val formattedNull = null?.let { "[$it]" }  // null → 블록 미실행 → null
```

- **nullable?.let { it -> ... }** = null이 아니면 블록 실행하고 결과 반환, null이면 null 반환.
- null일 때만 다른 처리를 하고 싶을 때 `?: 기본값` 과 함께 자주 쓴다.

---

## 6. `require` / `check` / `requireNotNull`

```kotlin
fun setAge(age: Int) {
    require(age >= 0) { "age must be non-negative" }
}

fun getValue(): String {
    check(value != null) { "value was null" }
    return value!!
}

val email: String? = ...
val notNull = requireNotNull(email) { "email is required" }
```

- **require(조건) { 메시지 }**: 조건이 false면 `IllegalArgumentException`.
- **check(조건) { 메시지 }**: 조건이 false면 `IllegalStateException`.
- **requireNotNull(값) { 메시지 }**: null이면 `IllegalArgumentException`, 아니면 non-null 반환.
- 생성자·함수 입구에서 인자·상태 검증할 때 사용.

---

## 이 프로젝트에서 확인하기
- `Step4Service.kt` 에서 nullable, safe call, elvis, let, require 연습 함수 실행해 보기.
- `src/test/kotlin/com/everydoc/dto/DtoNullHandlingTest.kt` 에서 DTO + null 처리 테스트 참고.
- `./gradlew bootRun` 후 `http://localhost:8080/test/step4` 호출해 동작 확인.
