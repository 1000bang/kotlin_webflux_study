# 18단계: R2DBC

## 목표
- JDBC(Blocking)와 R2DBC(Non-Blocking)의 차이를 이해한다.
- `ReactiveCrudRepository`로 Mono/Flux 기반 CRUD를 구현한다.
- WebFlux + R2DBC 조합으로 **전 구간 Non-Blocking**을 달성하는 방법을 안다.

---

## 1. JDBC vs R2DBC

| 구분 | JDBC | R2DBC |
|---|---|---|
| I/O 방식 | Blocking | Non-Blocking |
| 반환 타입 | `List<T>`, `Optional<T>` | `Flux<T>`, `Mono<T>` |
| ORM | JPA / Hibernate 사용 가능 | 사용 불가 (직접 매핑) |
| WebFlux와의 궁합 | 이벤트 루프 스레드 점유 위험 | 완벽한 Non-Blocking 달성 |

> JPA를 WebFlux 위에서 그대로 사용하면 Blocking I/O가 이벤트 루프 스레드를 점유해
> WebFlux의 장점이 크게 줄어든다. 진짜 Reactive를 원한다면 R2DBC를 써야 한다.

---

## 2. 의존성 설정 (`build.gradle.kts`)

```kotlin
// R2DBC
implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
runtimeOnly("org.postgresql:r2dbc-postgresql")
```

---

## 3. R2DBC 접속 설정 (`application.yml`)

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://host:5432/database
    username: user
    password: password
```

JDBC URL `jdbc:postgresql://...` 과 달리 `r2dbc:postgresql://...` 을 사용한다.

---

## 4. 엔티티 정의

```kotlin
@Table("orders")          // JPA의 @Entity 대신 @Table
data class Order(
    @Id                   // spring-data-relational의 @Id
    val id: Long? = null,

    @Column("user_id")    // snake_case ↔ camelCase 명시적 매핑
    val userId: Long,

    @Column("total_amount")
    val totalAmount: Long,

    @Column("status")
    val status: String,
)
```

- `@Table`, `@Id`, `@Column` 모두 `org.springframework.data.relational` 패키지
- `id = null` 로 두면 INSERT 시 DB가 생성한 값을 채워서 반환한다

---

## 5. 리포지토리

```kotlin
interface OrderRepository : ReactiveCrudRepository<Order, Long> {

    // 메서드 이름 규칙 → 자동으로 쿼리 생성
    fun findByStatus(status: String): Flux<Order>
    fun findByUserId(userId: Long): Flux<Order>
}
```

### 기본 제공 메서드

| 메서드 | SQL | 반환 타입 |
|---|---|---|
| `findAll()` | `SELECT * FROM orders` | `Flux<Order>` |
| `findById(id)` | `SELECT * WHERE id = :id` | `Mono<Order>` (없으면 empty) |
| `save(entity)` | `INSERT` or `UPDATE` | `Mono<Order>` |
| `deleteById(id)` | `DELETE WHERE id = :id` | `Mono<Void>` |
| `count()` | `SELECT COUNT(*)` | `Mono<Long>` |

---

## 6. 서비스 패턴

```kotlin
@Service
class Step18Service(private val orderRepository: OrderRepository) {

    // 전체 조회 — Flux로 스트리밍
    fun findAll(): Flux<Order> = orderRepository.findAll()

    // 단건 조회 — 없으면 에러 발행
    fun findById(id: Long): Mono<Order> =
        orderRepository.findById(id)
            .switchIfEmpty(Mono.error(NoSuchElementException("Order not found: $id")))

    // 저장 (id=null → INSERT, id 있음 → UPDATE)
    fun save(order: Order): Mono<Order> = orderRepository.save(order)

    // 삭제 후 메시지 반환
    fun delete(id: Long): Mono<String> =
        orderRepository.deleteById(id)
            .thenReturn("삭제 완료: id=$id")
}
```

---

## 7. 엔드포인트 정리

| Method | URL | 설명 |
|---|---|---|
| GET | `/test/step18/summary` | 핵심 요약 |
| GET | `/test/step18/orders` | 전체 주문 조회 (`Flux<Order>`) |
| GET | `/test/step18/orders/{id}` | id로 단건 조회 |
| GET | `/test/step18/orders/status/{status}` | status로 필터 조회 |
| GET | `/test/step18/orders/user/{userId}` | userId로 필터 조회 |
| POST | `/test/step18/orders` | 주문 저장 (RequestBody) |
| DELETE | `/test/step18/orders/{id}` | 주문 삭제 |

**curl 테스트 예시**

```bash
# 전체 조회
curl http://localhost:8080/test/step18/orders

# 단건 조회
curl http://localhost:8080/test/step18/orders/1

# status 필터
curl http://localhost:8080/test/step18/orders/status/PAID

# 저장
curl -X POST http://localhost:8080/test/step18/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "totalAmount": 15000, "status": "PENDING"}'

# 삭제
curl -X DELETE http://localhost:8080/test/step18/orders/1
```

---

## 8. JPA vs R2DBC 쿼리 비교

| 구분 | JPA | R2DBC |
|---|---|---|
| 메서드 이름 자동 쿼리 | O | O (동일) |
| `@Query` 커스텀 쿼리 | JPQL 또는 `nativeQuery=true` | **무조건 Native SQL** |
| JOIN 매핑 (연관관계) | `@OneToMany` 등 자동 매핑 | **지원 안 함** — 직접 조합 |
| 파라미터 바인딩 | `:name` | `:name` (동일) |
| DML 어노테이션 | `@Modifying` | `@Modifying` (동일) |

> R2DBC는 연관관계(`@OneToMany` 등)를 지원하지 않는다.
> JOIN 결과는 Native SQL로 가져온 뒤 Kotlin 코드에서 DTO로 직접 조립해야 한다.

---

## 9. @Query — Native SQL 직접 작성

```kotlin
interface OrderRepository : ReactiveCrudRepository<Order, Long> {

    // ① SELECT — 조건 범위 조회
    @Query("SELECT * FROM orders WHERE total_amount > :amount ORDER BY total_amount DESC")
    fun findByTotalAmountGreaterThan(amount: Long): Flux<Order>

    // ② SELECT — 집계 함수 (SUM)
    // COALESCE: 결과가 null이면 0 반환
    @Query("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE user_id = :userId")
    fun sumAmountByUserId(userId: Long): Mono<Long>

    // ③ UPDATE — DML에는 @Modifying 필수
    // 반환 타입 Mono<Int> = 영향받은 행 수
    @Modifying
    @Query("UPDATE orders SET status = :status WHERE id = :id")
    fun updateStatus(id: Long, status: String): Mono<Int>
}
```

### JPA와 다른 점

```kotlin
// JPA — JPQL 사용 가능
@Query("SELECT o FROM Order o WHERE o.totalAmount > :amount")
fun findExpensive(amount: Long): List<Order>

// R2DBC — 무조건 Native SQL (테이블명, 컬럼명 그대로 사용)
@Query("SELECT * FROM orders WHERE total_amount > :amount")
fun findByTotalAmountGreaterThan(amount: Long): Flux<Order>
```

---

## 10. @Query 엔드포인트

| Method | URL | 설명 |
|---|---|---|
| GET | `/test/step18/orders/amount/{min}` | 금액 이상 조회 (`@Query`) |
| GET | `/test/step18/orders/user/{userId}/sum` | 총 주문 금액 합산 (`@Query` 집계) |
| PATCH | `/test/step18/orders/{id}/status?status=PAID` | status 업데이트 (`@Modifying @Query`) |

```bash
# 10000원 이상 주문 조회
curl http://localhost:8080/test/step18/orders/amount/10000

# userId=1의 총 주문 금액
curl http://localhost:8080/test/step18/orders/user/1/sum

# id=1 주문 status를 PAID로 변경
curl -X PATCH "http://localhost:8080/test/step18/orders/1/status?status=PAID"
```

---

## 11. 전 구간 Non-Blocking 아키텍처

```
Client
  ↓ HTTP 요청
Netty (Event Loop)          ← Non-Blocking 웹 서버
  ↓
DispatcherHandler / Controller
  ↓ Flux<Order> / Mono<Order>
Service (R2DBC Repository)  ← Non-Blocking DB 드라이버
  ↓
PostgreSQL
```

WebFlux(Netty) + R2DBC = **전 구간 Non-Blocking** 달성
