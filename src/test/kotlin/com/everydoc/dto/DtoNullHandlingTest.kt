package com.everydoc.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * Kotlin DTO + null 처리 예제
 *
 * - data class로 간결한 DTO
 * - nullable(?) vs non-null 타입
 * - safe call(?. ), elvis(?: ), let, require
 */
class DtoNullHandlingTest {

    // ---------- 1. DTO 정의 (data class) ----------
    // nullable 필드는 타입에 ? 붙이면 됨
    data class UserDto(
        val id: Long,
        val name: String,
        val email: String?,           // nullable: 이메일 없을 수 있음
        val nickname: String? = null, // 기본값 null
        val profileImageUrl: String? = null,
    )

    // ---------- 2. null 안전 처리 유틸 ----------
    private fun displayName(dto: UserDto): String {
        // ?: (elvis) - null이면 오른쪽 값 사용
        return dto.nickname ?: dto.name
    }

    private fun profileOrPlaceholder(dto: UserDto): String {
        // ?. (safe call) - null이면 전체가 null, 그다음 ?: 로 기본값
        return dto.profileImageUrl?.takeIf { it.isNotBlank() } ?: "https://example.com/default.png"
    }

    private fun requireEmail(dto: UserDto): String {
        // require - null/blank면 예외 (비즈니스 검증)
        return requireNotNull(dto.email) { "email is required" }
            .takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("email must not be blank")
    }

    private fun parseOptionalId(value: String?): Long? {
        // ?.let { } - null이 아닐 때만 변환
        return value?.trim()?.takeIf { it.isNotBlank() }?.toLongOrNull()
    }

    // ?.let { } - null이 아닐 때만 블록 실행, 결과 반환
    private fun formatEmail(email: String?): String? =
        email?.let { "[$it]" }

    // require(조건) - 조건이 false면 IllegalArgumentException
    private fun validateId(id: Long): Long {
        require(id > 0) { "id must be positive, but was $id" }
        return id
    }

    @Test
    fun `DTO 생성 - nullable 필드는 생략 가능`() {
        val withNickname = UserDto(
            id = 1L,
            name = "김코틀린",
            email = "kotlin@example.com",
            nickname = "코코",
        )
        val withoutNickname = UserDto(
            id = 2L,
            name = "이자바",
            email = null,
        )

        assertEquals("코코", withNickname.nickname)
        assertNull(withoutNickname.nickname)
        assertNull(withoutNickname.email)
    }

    @Test
    fun `displayName - nickname이 있으면 nickname, 없으면 name`() {
        val withNick = UserDto(1L, "홍길동", "hong@a.com", nickname = "길동이")
        val withoutNick = UserDto(2L, "김철수", null)

        assertEquals("길동이", displayName(withNick))
        assertEquals("김철수", displayName(withoutNick))
    }

    @Test
    fun `profileOrPlaceholder - url 없거나 blank면 기본 이미지`() {
        val withUrl = UserDto(1L, "A", null, null, "https://cdn.example.com/me.png")
        val withoutUrl = UserDto(2L, "B", null)
        val blankUrl = UserDto(3L, "C", null, null, "  ")

        assertEquals("https://cdn.example.com/me.png", profileOrPlaceholder(withUrl))
        assertEquals("https://example.com/default.png", profileOrPlaceholder(withoutUrl))
        assertEquals("https://example.com/default.png", profileOrPlaceholder(blankUrl))
    }

    @Test
    fun `parseOptionalId - null 또는 빈 문자열이면 null 반환`() {
        assertEquals(42L, parseOptionalId("42"))
        assertEquals(42L, parseOptionalId("  42  "))
        assertNull(parseOptionalId(null))
        assertNull(parseOptionalId(""))
        assertNull(parseOptionalId("  "))
        assertNull(parseOptionalId("abc"))
    }

    @Test
    fun `requireEmail - email 있으면 값 반환`() {
        val dto = UserDto(1L, "Test", "a@b.com")
        assertEquals("a@b.com", requireEmail(dto))
    }

    @Test
    fun `requireEmail - email이 null이면 예외`() {
        assertThrows(IllegalArgumentException::class.java) {
            requireEmail(UserDto(1L, "Test", null))
        }
    }

    @Test
    fun `formatEmail - let으로 null이 아닐 때만 포맷 적용`() {
        assertEquals("[a@b.com]", formatEmail("a@b.com"))
        assertNull(formatEmail(null))
    }

    @Test
    fun `validateId - require로 양수일 때만 통과`() {
        assertEquals(1L, validateId(1L))
        assertEquals(100L, validateId(100L))
    }

    @Test
    fun `validateId - require 조건 실패 시 예외`() {
        assertThrows(IllegalArgumentException::class.java) {
            validateId(0L)
        }
        assertThrows(IllegalArgumentException::class.java) {
            validateId(-1L)
        }
    }
}
