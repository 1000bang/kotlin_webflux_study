package com.everydoc.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * orders 테이블과 매핑되는 R2DBC 엔티티.
 *
 * - @Table  : DB 테이블명 지정
 * - @Id     : PK 컬럼 (spring-data-relational)
 * - @Column : Kotlin camelCase ↔ DB snake_case 매핑
 *
 * JPA @Entity 없이 data class 만으로 정의하는 것이 R2DBC 방식이다.
 */
@Table("orders")
data class Order(
    @Id
    val id: Long? = null,

    @Column("user_id")
    val userId: Long,

    @Column("total_amount")
    val totalAmount: Long,

    @Column("status")
    val status: String,
)
