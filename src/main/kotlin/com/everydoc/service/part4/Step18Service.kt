package com.everydoc.service.part4

import com.everydoc.domain.Order
import com.everydoc.repository.OrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 18단계: R2DBC (STEP18.md)
 * - ReactiveCrudRepository 기반 CRUD
 * - 쿼리 메서드(메서드 이름으로 자동 생성)
 * - 전 구간 Non-Blocking DB 접근
 */
@Service
class Step18Service(
    private val orderRepository: OrderRepository,
) {

    // ──────────────────────────────────────────────
    // 1. 전체 조회 — findAll() : Flux<Order>
    // ──────────────────────────────────────────────

    /**
     * ReactiveCrudRepository.findAll()은 Flux<T>를 반환한다.
     * JDBC의 List<Order>와 달리 스트림으로 한 건씩 발행된다.
     */
    fun findAll(): Flux<Order> = orderRepository.findAll()

    // ──────────────────────────────────────────────
    // 2. 단건 조회 — findById() : Mono<Order>
    // ──────────────────────────────────────────────

    /**
     * 존재하지 않는 id를 조회하면 빈 Mono(empty)가 반환된다.
     * switchIfEmpty로 404 처리를 체이닝할 수 있다.
     */
    fun findById(id: Long): Mono<Order> =
        orderRepository.findById(id)
            .switchIfEmpty(Mono.error(NoSuchElementException("Order not found: $id")))

    // ──────────────────────────────────────────────
    // 3. 쿼리 메서드 — 메서드 이름으로 자동 쿼리 생성
    // ──────────────────────────────────────────────

    /**
     * findByStatus(status) → SELECT * FROM orders WHERE status = :status
     * JPA와 동일한 메서드 이름 규칙을 R2DBC도 지원한다.
     */
    fun findByStatus(status: String): Flux<Order> =
        orderRepository.findByStatus(status)

    fun findByUserId(userId: Long): Flux<Order> =
        orderRepository.findByUserId(userId)

    // ──────────────────────────────────────────────
    // 4. 저장 — save() : Mono<Order>
    // ──────────────────────────────────────────────

    /**
     * id = null 이면 INSERT, id가 있으면 UPDATE.
     * 저장 후 DB가 생성한 id가 채워진 Order를 Mono로 반환한다.
     */
    fun save(order: Order): Mono<Order> = orderRepository.save(order)

    // ──────────────────────────────────────────────
    // 5. 삭제 — deleteById() : Mono<Void>
    // ──────────────────────────────────────────────

    fun delete(id: Long): Mono<String> =
        orderRepository.deleteById(id)
            .thenReturn("삭제 완료: id=$id")

    // ──────────────────────────────────────────────
    // 6. @Query — Native SQL 직접 작성
    // ──────────────────────────────────────────────

    /**
     * @Query로 Native SQL을 직접 작성한 범위 조회.
     * JPA의 JPQL 없이 무조건 Native SQL을 사용한다.
     *
     * Repository: @Query("SELECT * FROM orders WHERE total_amount > :amount ...")
     */
    fun findByAmountGreaterThan(amount: Long): Flux<Order> =
        orderRepository.findByTotalAmountGreaterThan(amount)

    /**
     * @Query로 집계 함수 사용 — SUM 결과를 Mono<Long>으로 수신.
     *
     * Repository: @Query("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE user_id = :userId")
     */
    fun sumAmountByUserId(userId: Long): Mono<Long> =
        orderRepository.sumAmountByUserId(userId)

    /**
     * @Modifying + @Query — UPDATE 문 실행.
     * DML에는 @Modifying이 필수이며, 반환값은 영향받은 행 수(Int).
     *
     * Repository: @Modifying @Query("UPDATE orders SET status = :status WHERE id = :id")
     */
    fun updateStatus(id: Long, status: String): Mono<String> =
        orderRepository.updateStatus(id, status)
            .map { affected -> "업데이트 완료: id=$id, status=$status, 영향받은 행=$affected" }

    // ──────────────────────────────────────────────
    // 7. 핵심 정리
    // ──────────────────────────────────────────────

    fun summary(): Mono<String> = Mono.just(
        """
        [Step18 — R2DBC 핵심 요약]

        ① JDBC vs R2DBC
           JDBC   : Blocking — 스레드 점유, JPA 사용 가능
           R2DBC  : Non-Blocking — 이벤트 루프, Mono/Flux 반환

        ② 의존성
           spring-boot-starter-data-r2dbc
           org.postgresql:r2dbc-postgresql (런타임)

        ③ 엔티티
           @Table("테이블명")  — JPA의 @Entity 대신
           @Id                — PK (spring-data-relational)
           @Column("컬럼명")  — snake_case ↔ camelCase 매핑

        ④ 리포지토리 기본 메서드
           findAll()    → Flux<T>
           findById(id) → Mono<T>   (없으면 empty Mono)
           save(entity) → Mono<T>   (id=null이면 INSERT)
           deleteById() → Mono<Void>

        ⑤ JPA vs R2DBC 쿼리 비교
           구분              JPA                   R2DBC
           메서드 이름 자동  O                     O (동일)
           커스텀 쿼리       JPQL or nativeQuery    무조건 Native SQL
           연관관계 매핑     @OneToMany 자동        지원 안 함 — 직접 조합
           DML 어노테이션    @Modifying             @Modifying (동일)

        ⑥ @Query 사용법
           @Query("SELECT * FROM orders WHERE total_amount > :amount")
           fun findByTotalAmountGreaterThan(amount: Long): Flux<Order>

           @Modifying
           @Query("UPDATE orders SET status = :status WHERE id = :id")
           fun updateStatus(id: Long, status: String): Mono<Int>

        ⑦ 진짜 Reactive를 위한 조건
           WebFlux (Non-Blocking 웹) + R2DBC (Non-Blocking DB)
           → 전 구간 Non-Blocking 달성
        """.trimIndent()
    )
}
