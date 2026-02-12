# 7단계: 확장 함수·프로퍼티 (Extension Function / Property)

## 목표
- **확장 함수** `fun 타입.함수명()` 문법으로 기존 타입에 함수를 붙일 수 있다.
- **확장 프로퍼티**로 getter(필요 시 setter)를 붙일 수 있다.
- 유틸 함수를 멤버처럼 `객체.함수()` 형태로 쓸 수 있다.

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

## 4. 사용처

- **유틸 함수**: `StringUtil.capitalize(s)` 대신 `s.capitalize()` 처럼 호출.
- **가독성**: `list.filter { it.isValid() }` 에서 `isValid()` 를 확장으로 정의하면 도메인 표현이 명확해진다.
- **기존 라이브러리 확장**: JDK·서드파티 타입에 메서드를 추가한 것처럼 쓸 수 있다 (클래스 수정 없이).

---

## 5. 주의점

- **정적 디스패치**: 수신자 타입이 **컴파일 시점**에 정해진 타입으로 결정된다. 다형성(오버라이드)은 적용되지 않는다.
- **캡슐화 깨지 않음**: private 멤버에는 접근할 수 없다. public API 기준으로만 확장한다.
- **같은 시그니처**: 클래스에 이미 같은 이름·시그니처 멤버가 있으면 **멤버가 우선**이다.

---

## 이 프로젝트에서 확인하기
- `Step7Service.kt` 에서 확장 함수·확장 프로퍼티 연습 함수 실행해 보기.
- `./gradlew bootRun` 후 `http://localhost:8080/test/step7` 호출해 동작 확인.
