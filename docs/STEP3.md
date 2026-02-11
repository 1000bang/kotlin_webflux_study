# 3단계: 클래스와 객체

## 목표
- `class`로 클래스를 정의하고, 생성자·프로퍼티를 쓸 수 있다.
- `data class`로 DTO·값을 담는 클래스를 짧게 쓸 수 있다.
- `object`로 싱글톤을 선언할 수 있다.

---

## 1. 클래스와 생성자

```kotlin
class User(val name: String, var age: Int) {
    init {
        require(age >= 0) { "age must be non-negative" }
    }
}

val user = User("토스", 20)
println(user.name)  // getter 자동
user.age = 21       // setter (var일 때만)
```

- **주 생성자**: 클래스 이름 옆 `( )` 안에 `val`/`var` 파라미터를 쓰면 **프로퍼티 + 생성자**가 한 번에 선언된다.
- **init 블록**: 생성 시 실행되는 초기화 로직. `require`/`check`로 검증할 때 자주 쓴다.
- **보조 생성자**: `constructor(...) { }` 로 추가 생성자를 만들 수 있다 (주 생성자와 함께 쓸 때는 반드시 위임 호출).

---

## 2. 프로퍼티 (Property)

```kotlin
class Person(val name: String) {
    var age: Int = 0
        private set   // setter만 private

    val isAdult: Boolean
        get() = age >= 19   // 커스텀 getter

    var nickname: String = ""
        set(value) {
            field = value.take(10)  // 10자로 제한
        }
}
```

- `val` → getter만, `var` → getter + setter 자동 생성.
- `get()` / `set(value)` 로 커스텀 접근자. 백킹 필드는 `field`로 참조.
- `private set` 으로 외부에서는 읽기만 허용할 수 있다.

---

## 3. data class

```kotlin
data class UserDto(
    val id: Long,
    val name: String,
    val email: String? = null,
)

val u = UserDto(1L, "토스", "toss@example.com")
println(u)           // UserDto(id=1, name=토스, email=toss@example.com)
val copy = u.copy(name = "토스페이")  // 일부만 바꾼 복사본
```

- **자동 생성**: `equals`, `hashCode`, `toString`, `copy`, (주 생성자 프로퍼티에 대한) `componentN()`.
- DTO·불변 값 객체에 쓰기 좋다. 프로퍼티는 보통 `val`로.
- **제한**: 주 생성자에 최소 1개 파라미터, `val`/`var`만, abstract/open/inner/제네릭 등 일부만 가능.

---

## 4. object (싱글톤)

```kotlin
object Config {
    const val API_URL = "https://api.example.com"
    fun getTimeout(): Int = 30
}

Config.API_URL
Config.getTimeout()
```

- **인스턴스가 하나만** 존재하는 타입. `object` 선언 시 곧바로 싱글톤이 만들어진다.
- 클래스처럼 프로퍼티·메서드·init 블록을 가질 수 있다.
- **동반 객체(companion object)**: 클래스 안에 `companion object { }` 를 두면 자바의 `static` 멤버처럼 클래스 이름으로 접근할 수 있다.

---

## 5. companion object

```kotlin
class User(val name: String) {
    companion object {
        const val MIN_AGE = 0
        fun create(name: String): User = User(name)
    }
}

User.MIN_AGE
User.create("토스")
```

- 클래스당 하나. `클래스명.멤버` 로 접근.
- 이름을 줄 수 있다: `companion object Loader { }` → `User.Loader.create(...)`.
- `@JvmStatic` 을 붙이면 자바에서도 `User.create(...)` 로 호출 가능.

---

## 이 프로젝트에서 확인하기
- `Step3Service.kt` 에서 class, data class, object, companion object 연습 함수 실행해 보기.
- `./gradlew bootRun` 후 `http://localhost:8080/test/step3` 호출해 동작 확인.
