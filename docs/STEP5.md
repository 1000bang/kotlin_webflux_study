# 5단계: 컬렉션 (Collection)

## 목표
- List / Set / Map 을 만들고, 불변·가변을 구분해서 쓸 수 있다.
- `listOf` / `mapOf` / `setOf` 와 가변 버전을 안다.
- `filter` / `map` / `forEach` 등 자주 쓰는 컬렉션 함수를 쓸 수 있다.

---

## 1. List — listOf / mutableListOf

```kotlin
val readOnly = listOf(1, 2, 3)       // 불변 (추가/삭제 불가)
val mutable = mutableListOf(1, 2, 3) // 가변
mutable.add(4)
mutable.remove(2)

val empty = emptyList<Int>()
val fromRange = (1..5).toList()
```

- **listOf** → `List` (읽기 전용). **mutableListOf** → `MutableList` (add/remove 가능).
- 인덱스 접근: `list[0]`, `list.first()`, `list.last()`, `list.getOrNull(10)`.

---

## 2. Set — setOf / mutableSetOf (중복 없음)

```kotlin
val set = setOf(1, 2, 3, 2, 1)  // [1, 2, 3] — 중복 제거
val mutable = mutableSetOf("a", "b", "c")
mutable.add("a")  // 이미 있으면 무시
```

- 순서는 보장되지 않음 (구현에 따라 유지될 수 있음). **중복 불가**.

---

## 3. Map — mapOf / mutableMapOf

```kotlin
val map = mapOf(1 to "one", 2 to "two", 3 to "three")
val mutable = mutableMapOf("a" to 1, "b" to 2)
mutable["c"] = 3
mutable.put("d", 4)

val value = map[1]           // "one"
val default = map.getOrDefault(99, "?")
```

- **키 to 값** 으로 엔트리. `map[key]`, `map.getValue(key)` (없으면 예외), `getOrElse` / `getOrDefault`.

---

## 4. 불변 vs 가변

| 생성 함수 | 타입 | 추가/삭제/수정 |
|-----------|------|----------------|
| listOf, setOf, mapOf, **emptyList()** | 읽기 전용 | **불가** |
| mutableListOf, mutableSetOf, mutableMapOf | 가변 | 가능 |

- **emptyList()** 는 읽기 전용이라 `.add()` 가 없다. 조건/반복으로 하나씩 넣을 때는 **mutableListOf()** 를 쓴다.
- 읽기 전용 컬렉션에 `+`, `-` 로 **새 컬렉션**을 만들 수 있음: `list + 4`, `map + (4 to "four")`.

---

## 4-1. 실전: 빈 컬렉션 선언 후 for/if 로 하나씩 add·put

한 번에 다 넣지 않고, **for 문이나 if 조건에 따라 하나씩 add/put** 하는 패턴이다.

```kotlin
// ❌ emptyList() 는 불변 → add() 없음 (컴파일 에러)
// val responseData = emptyList<String>()
// responseData.add(data)

// ✅ 빈 가변 리스트 선언 후 조건/반복으로 add
val responseData = mutableListOf<String>()
for (item in sourceList) {
    if (item.isNotBlank()) {
        responseData.add(item.trim())
    }
}

// Map 도 마찬가지: 빈 가변 맵 선언 후 put
val resultMap = mutableMapOf<Int, String>()
for (i in 1..5) {
    if (i % 2 == 0) {
        resultMap[i] = "even-$i"
    }
}
```

- **List**: `mutableListOf<타입>()` → `add(value)`, `addAll(list)`
- **Map**: `mutableMapOf<키, 값>()` → `put(key, value)` 또는 `map[key] = value`
- **Set**: `mutableSetOf<타입>()` → `add(value)`

---

## 5. 자주 쓰는 함수 — filter / map / forEach

```kotlin
val list = listOf(1, 2, 3, 4, 5)
list.filter { it % 2 == 0 }     // [2, 4]
list.map { it * 2 }            // [2, 4, 6, 8, 10]
list.forEach { println(it) }
list.find { it > 3 }           // 4 (첫 번째)
list.any { it > 4 }            // true
list.all { it > 0 }            // true
list.none { it < 0 }           // true
list.count { it % 2 == 0 }     // 2
```

---

## 6. fold / reduce / sum

```kotlin
list.fold(0) { acc, n -> acc + n }   // 합 (초기값 0)
list.reduce { acc, n -> acc + n }    // 합 (첫 원소가 초기값)
list.sum()
list.sumOf { it * 2 }
```

---

## 7. 정렬 / distinct / groupBy

```kotlin
list.sorted()                  // 오름차순 (새 리스트)
list.sortedDescending()
list.distinct()                // 중복 제거 (Set 아님, 순서 유지)

list.groupBy { it % 2 }        // { 0: [2,4], 1: [1,3,5] }
list.associateWith { it * 2 }  // Map: 원소 -> 변환값
```

---

## 이 프로젝트에서 확인하기
- `Step5Service.kt` 에서 List/Set/Map, 불변·가변, filter/map/forEach 등 연습 함수 실행해 보기.
- `Step1Service.kt` 의 practiceList / practiceMap / practiceSet 와 비교해 보기.
- `./gradlew bootRun` 후 `http://localhost:8080/test/step5` 호출해 동작 확인.
