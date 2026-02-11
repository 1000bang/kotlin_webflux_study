# 2단계: 함수와 람다

## 목표
- 함수를 정의하고, 기본 인자·이름 인자를 쓸 수 있다.
- 단일 표현식 함수로 짧게 쓸 수 있다.
- 람다 `{ }` 문법과 고차 함수(함수를 인자/반환으로 쓰기)를 안다.

---

## 1. 함수 정의

```kotlin
fun greet(name: String): String {
    return "Hello, $name"
}

fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}
```

- `fun 함수명(파라미터: 타입, ...): 반환타입 { 본문 }`
- 반환값이 없으면 `Unit` (생략 가능). `Unit`은 자바의 `void`에 대응.

---

## 2. 기본 인자 (Default Argument)

```kotlin
fun greet(name: String, prefix: String = "안녕하세요"): String {
    return "$prefix, $name"
}

greet("토스")              // "안녕하세요, 토스"
greet("토스", "반갑습니다")  // "반갑습니다, 토스"
```

- 파라미터에 `= 값`을 주면 호출 시 생략 가능.
- 생략한 인자는 오른쪽부터 채워지므로, 기본값 없는 인자는 왼쪽에 두는 것이 좋다.

---

## 3. 이름 인자 (Named Argument)

```kotlin
fun createUser(id: Long, name: String, email: String? = null): String {
    return "id=$id, name=$name, email=$email"
}

createUser(1L, "토스")
createUser(id = 1L, name = "토스", email = "toss@example.com")
createUser(name = "토스", id = 1L)  // 순서 바꿔도 OK
```

- `이름 = 값` 형태로 넘기면 가독성이 좋고, 순서를 바꿔 쓸 수 있다.
- 기본 인자와 함께 쓰면 필요한 것만 넘기기 편하다.

---

## 4. 단일 표현식 함수 (Single-Expression Function)

```kotlin
fun add(a: Int, b: Int) = a + b
fun isEven(n: Int) = n % 2 == 0
fun double(list: List<Int>) = list.map { it * 2 }
```

- 본문이 하나의 식이면 `= 식`으로 쓰고, `return`·중괄호·반환 타입 생략 가능.
- 타입 추론으로 반환 타입이 정해진다.

---

## 5. 람다 (Lambda)

```kotlin
val double: (Int) -> Int = { x -> x * 2 }
val sum: (Int, Int) -> Int = { a, b -> a + b }

// 인자가 하나면 it으로 생략
val doubleIt = { x: Int -> x * 2 }
listOf(1, 2, 3).map { it * 2 }   // it = 각 원소
```

- `{ 인자 -> 본문 }` 형태. 인자가 하나면 `it`으로 쓸 수 있다.
- 타입은 `(인자타입, ...) -> 반환타입`. 람다 본문 마지막 식이 반환값이다.

---

## 6. 고차 함수 (Higher-Order Function)

함수를 인자로 받거나, 함수를 반환하는 함수.

```kotlin
// 함수를 인자로 받기
fun runTwice(block: () -> Unit) {
    block()
    block()
}
runTwice { println("hello") }

// 함수를 인자로 받아서 변환에 사용
fun <T, R> map(list: List<T>, transform: (T) -> R): List<R> {
    return list.map(transform)
}
map(listOf(1, 2, 3)) { it * 2 }  // [2, 4, 6]

// 함수를 반환하기
fun multiplier(factor: Int): (Int) -> Int {
    return { x -> x * factor }
}
val double = multiplier(2)
double(5)  // 10
```

- `(T) -> R`: T를 받아 R을 반환하는 함수 타입.
- 람다가 마지막 인자면 `( )` 밖에 `{ }`로 쓸 수 있다 (trailing lambda).

---

## 7. 자주 쓰는 고차 함수 (컬렉션)

```kotlin
val list = listOf(1, 2, 3, 4, 5)

list.forEach { println(it) }
list.filter { it % 2 == 0 }    // [2, 4]
list.map { it * 2 }           // [2, 4, 6, 8, 10]
list.find { it > 3 }          // 4 (첫 번째)
list.any { it > 4 }           // true
list.all { it > 0 }           // true
list.fold(0) { acc, n -> acc + n }  // 15 (합)
```

- `forEach`: 각 원소에 대해 실행.
- `filter`: 조건 만족하는 것만.
- `map`: 변환.
- `find` / `any` / `all`: 검색·조건 판별.
- `fold`: 초기값부터 누적 계산.

---

## 이 프로젝트에서 확인하기
- `Step1Service.kt`의 `practiceList()` 등에서 `filter`, `map` 사용 부분 찾아보기.
- `Step2Service.kt`(또는 Step2 연습 코드)에서 기본 인자·이름 인자·람다·고차 함수 예제 실행해 보기.
- `./gradlew bootRun` 후 `http://localhost:8080/test/step2` (Step2 엔드포인트가 있다면) 호출해 동작 확인.
