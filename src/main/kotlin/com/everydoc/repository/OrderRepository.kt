package com.everydoc.repository

import com.everydoc.domain.Order
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * R2DBC 리포지토리.
 *
 * ① 메서드 이름 규칙 → Spring Data가 자동으로 Native SQL 생성
 * ② @Query           → 직접 Native SQL 작성 (JPA와 달리 JPQL 없음, 무조건 Native)
 * ③ @Modifying       → INSERT/UPDATE/DELETE 시 반드시 붙여야 한다
 */
interface OrderRepository : ReactiveCrudRepository<Order, Long> {

    // ── 메서드 이름 자동 쿼리 ──────────────────────────────────

    /** SELECT * FROM orders WHERE status = :status */
    fun findByStatus(status: String): Flux<Order>

    /** SELECT * FROM orders WHERE user_id = :userId */
    fun findByUserId(userId: Long): Flux<Order>

    // ── @Query — Native SQL 직접 작성 ─────────────────────────

    /**
     * 특정 금액 이상의 주문을 금액 내림차순으로 조회.
     * JPA와 달리 JPQL이 아닌 Native SQL만 사용 가능하다.
     */
    @Query("SELECT * FROM orders WHERE total_amount > :amount ORDER BY total_amount DESC")
    fun findByTotalAmountGreaterThan(amount: Long): Flux<Order>

    /**
     * 특정 사용자의 총 주문 금액 합산.
     * 집계 함수 결과를 Mono<Long>으로 받는다.
     * COALESCE: 주문이 없을 때 null 대신 0 반환.
     */
    @Query("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE user_id = :userId")
    fun sumAmountByUserId(userId: Long): Mono<Long>

    /**
     * status 업데이트.
     * DML(INSERT/UPDATE/DELETE)에는 @Modifying을 반드시 붙여야 한다.
     * 반환 타입 Mono<Int> = 영향받은 행 수.
     */
    @Modifying
    @Query("UPDATE orders SET status = :status WHERE id = :id")
    fun updateStatus(id: Long, status: String): Mono<Int>
}
