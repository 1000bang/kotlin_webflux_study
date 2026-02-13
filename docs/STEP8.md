# 8단계: 스코프 함수 (let, run, with, apply, also)

## 목표
- **let**, **run**, **with**, **apply**, **also** 차이(수신자·반환값·용도)를 안다.
- null 처리·객체 초기화·부가 동작 같은 실전 상황에서 어떤 걸 쓸지 고를 수 있다.

---

## 0. 왜 쓰는가?

### 스코프 함수가 없을 때

한 객체를 **한 블록 안에서만** 쓰고, 그 블록 안에서는 **그 객체를 짧은 이름(it, this)으로 반복해서** 참조하고 싶을 때가 많다.

```kotlin
// 객체를 변수에 담고, 여러 줄에서 그 변수 이름을 반복
val user = loadUser()
val validated = validate(user)
val dto = toDto(validated)
return dto
```

또 **null이 아닐 때만** 다음 처리를 하고 싶다면, 매번 `if (x != null) { ... }` 로 감싸거나 `x?.let { ... }` 를 쓴다. 이때 **이 블록의 주인공은 이 객체다**를 문법으로 묶어 주는 게 스코프 함수다.

- **스코프**: `객체.함수 { }` 안에서는 그 **객체를 it 또는 this 로** 짧게 부를 수 있다. 블록이 끝나면 그 스코프는 사라진다.
- **반환값**: 스코프 함수마다 **블록의 마지막 식**을 반환할지, **객체 자신**을 반환할지가 다르다. 그래서 "값을 바꿔서 반환하고 싶을 때"와 "객체를 설정만 하고 그대로 넘기고 싶을 때"를 나눠 쓸 수 있다.
- **이름만으로 의도 전달**: `let`은 보통 "null이 아닐 때 변환", `apply`는 "설정 후 자기 자신 반환"처럼, 팀에서 통용되는 패턴이 있어서 **왜 이걸 썼는지** 코드만 봐도 추론하기 쉽다.

정리하면, **한 객체를 기준으로 스코프를 잡고, 그 안에서 처리한 뒤 반환값을 어떻게 할지(블록 결과 vs 객체 자신)** 를 함수별로 정해 둔 것이 스코프 함수다. 쓰지 않아도 되지만, 쓰면 null 처리·초기화·빌더 패턴이 짧고 읽기 좋아진다.

---

## 1. 다섯 가지 스코프 함수 차이

| 함수 | 수신자 참조 | 반환값 | 자주 쓰는 상황 |
|------|-------------|--------|----------------|
| **let** | `it` | 블록 결과 | null이 아닐 때만 처리·변환 |
| **run** | `this` | 블록 결과 | 객체로 뭔가 계산해서 반환 |
| **with** | `this` | 블록 결과 | 인자를 받아서 그 객체로 블록 실행 (run과 비슷, 문법만 다름) |
| **apply** | `this` | **수신자 자신** | 객체 설정(프로퍼티 채우기) 후 그대로 반환 |
| **also** | `it` | **수신자 자신** | 로깅·부가 동작 후 그대로 반환 |

- **it** vs **this**: `it`은 인자처럼 쓸 때(이름을 줄 수 있음), `this`는 "이 객체의 멤버"처럼 쓸 때. `this`는 생략 가능해서 `run { length }` 처럼 쓸 수 있다.
- **블록 결과** vs **수신자 자신**: "블록 안에서 계산한 값"을 반환하면 **let/run/with**, "설정만 하고 같은 객체를 넘기겠다"면 **apply/also**.

---

## 2. let

```kotlin
val len = nullableString?.let { it.length } ?: 0
val dto = entity?.let { it.toResponse() }
```

- **수신자**: `it`. **반환**: 블록의 마지막 식.
- **용도**: null이 아닐 때만 블록 실행하고 **그 결과**를 쓰고 싶을 때. `?.let { } ?: 기본값` 패턴이 많다.

---

## 3. run

```kotlin
val result = user.run {
    "$name ($email)"
}
val config = Config().run {
    timeout = 30
    url = "https://..."
    this  // 명시적으로 수신자 반환 (보통 apply 씀)
}
```

- **수신자**: `this`(생략 가능). **반환**: 블록 결과.
- **용도**: 그 객체로 **무언가 계산해서** 값을 반환할 때.

---

## 4. with

```kotlin
val result = with(sb) {
    append("a")
    append("b")
    toString()
}
```

- **수신자**: `this`. **반환**: 블록 결과.
- **문법**: `with(객체) { }` — 인자로 객체를 받는다. run은 `객체.run { }`.
- **용도**: 한 객체에 대해 **여러 작업을 한 뒤** 그중 마지막 결과를 반환할 때.

---

## 5. apply

```kotlin
val request = HttpRequest().apply {
    method = "GET"
    url = "https://api.example.com"
}
val list = mutableListOf<String>().apply {
    add("a")
    add("b")
}
```

- **수신자**: `this`. **반환**: **수신자 자신**.
- **용도**: 객체를 **만든 뒤 프로퍼티/설정만 채우고** 그 객체를 그대로 넘길 때. 빌더·초기화 블록처럼 쓴다.

---

## 6. also

```kotlin
val list = mutableListOf(1, 2, 3).also {
    println("생성: $it")
}.filter { it > 1 }
```

- **수신자**: `it`. **반환**: **수신자 자신**.
- **용도**: **로깅·검증·부가 동작**만 하고 값은 그대로 넘기고 싶을 때. 체이닝 중간에 끼워 넣기 좋다.

---

## 7. 실전에서 어떻게 쓰는지

### 7-1. null일 때만 변환 (let)

```kotlin
val email: String? = user.email
val display = email?.let { "[$it]" } ?: "(미입력)"
val id = queryParam?.let { it.toLongOrNull() }
```

- **왜 let**: null이면 블록을 건너뛰고, null이 아닐 때만 블록 결과를 쓰고 싶을 때. `?.let { } ?: 기본값` 으로 한 줄에 처리.

### 7-2. DTO·객체 초기화 (apply)

```kotlin
val dto = UserDto().apply {
    id = entity.id
    name = entity.name
    email = entity.email.orEmpty()
}
val request = WebClient.builder()
    .baseUrl(config.url)
    .build()
```

- **왜 apply**: 생성자만으로는 다 채우기 어렵거나, 빌더처럼 "설정만 하고 자기 자신 반환"이 필요할 때.

### 7-3. 한 객체로 여러 작업 후 결과 (with / run)

```kotlin
val summary = with(stringBuilder) {
    append("total: ")
    append(items.size)
    toString()
}
```

- **왜 with**: 그 객체를 **한 블록의 주인공**으로 두고, 여러 메서드 호출 끝에 **마지막 식**을 반환하고 싶을 때.

### 7-4. 체이닝 중 로깅·검증 (also)

```kotlin
val result = repo.findById(id)
    ?.also { println("found: $it") }
    ?.let { it.toResponse() }
```

- **왜 also**: "이 객체로 부가 동작만 하고, 값은 그대로 넘기기". 디버깅·로깅·검증을 끼워 넣을 때.

### 실전에서 정리

| 상황 | 쓰기 좋은 함수 |
|------|----------------|
| null이 아닐 때만 변환·처리 | `?.let { } ?: 기본값` |
| 객체 만들고 프로퍼티만 채워서 반환 | `apply { }` |
| 한 객체로 여러 작업 후 블록 결과 반환 | `with(객체) { }` 또는 `객체.run { }` |
| 체이닝 중 로깅·부가 동작만 | `also { }` |

---

## 8. 주의점

- **this** 를 쓸 때는 **람다 안에서 외부 클래스의 this** 와 구분이 필요하면 `this@Outer` 로 한정할 수 있다.
- **apply/also** 는 "수신자 자신"을 반환하므로, 블록 마지막에 쓴 식은 **반환값이 아니다**. 반환하고 싶으면 **let/run/with** 를 써야 한다.
- 남용하면 **중첩**이 깊어져서 읽기 힘들어질 수 있다. 한 단계만 쓰고, 두세 단계 이상 겹치면 변수로 나누는 게 나을 수 있다.

---

## 이 프로젝트에서 확인하기
- `Step8Service.kt` 에서 let, run, with, apply, also 연습 함수 실행해 보기.
- `./gradlew bootRun` 후 `http://localhost:8080/test/step8` 호출해 동작 확인.
