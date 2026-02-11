# 1단계: 코틀린 기본 문법

## 목표
- 패키지, 함수, 변수, 기본 타입을 Kotlin으로 쓸 수 있다.
- `if` / `when`, `for` / `while` 문법을 안다.

---

## 1. 패키지와 진입점

```kotlin
package com.everydoc

fun main(args: Array<String>) {
    println("Hello")
}
```
- `package`: 파일 상단에 한 번만.
- `fun`: 함수 선언.
- `main`: JVM 진입점, 인자 `Array<String>`.

---

## 2. 변수: val / var

```kotlin
val name = "everydoc"   // 읽기 전용 (권장)
var count = 0          // 재할당 가능
count = 1

val num: Int = 42      // 타입 명시
```

- **val**: 불변. 되도록 val 사용.
- **var**: 가변. 꼭 필요할 때만.

---

## 3. 기본 타입

```kotlin
val i: Int = 1
val l: Long = 1L
val d: Double = 3.14
val f: Float = 3.14f
val b: Boolean = true
val c: Char = 'A'
val s: String = "hello"
```

- Kotlin에서는 모두 객체(래퍼). 자동 변환으로 기본형으로도 동작.

---

## 4. 함수

```kotlin
fun greet(name: String): String {
    return "Hello, $name"
}

fun add(a: Int, b: Int) = a + b   // 단일 표현식
```

- `fun 함수명(파라미터: 타입): 반환타입`
- `= 표현식` 이면 반환 타입 생략 가능.

---

## 5. if / when

```kotlin
// if는 표현식 (값으로 쓸 수 있음)
val max = if (a > b) a else b

when (x) {
    1 -> "one"
    2, 3 -> "two or three"
    in 4..10 -> "four to ten"
    is String -> "string"
    else -> "other"
}
```

---

## 6. for / while

```kotlin
for (i in 1..5) { }        // 1,2,3,4,5
for (i in 1 until 5) { }   // 1,2,3,4
for (i in 5 downTo 1) { }  // 5,4,3,2,1
for (c in "abc") { }       // 문자 순회

while (condition) { }
do { } while (condition)
```

---

## 이 프로젝트에서 확인하기
- `./gradlew bootRun` 후 브라우저 또는 curl: `http://localhost:8080/api/step1`
- `Step1Controller.kt`에서 `val`, `fun`, `Mono.just()` 등 위 문법이 쓰인 부분을 찾아보기.
