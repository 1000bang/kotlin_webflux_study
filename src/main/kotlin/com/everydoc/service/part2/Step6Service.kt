package com.everydoc.service.part2

import org.springframework.stereotype.Service

/**
 * 6단계: 코틀린 특징 요약 (STEP6.md)
 * - 정적 타입, 간결한 문법, null-safe, data class, 확장 함수 맛보기
 */
@Service
class Step6Service {

    fun hello(): String {
        println(practiceStaticTyping())
        println(practiceConciseSyntax())
        println(practiceNullSafe())
        println(practiceDataClass())
        println(practiceExtensionFunction())
        return "Step6: 정적 타입, 간결한 문법, null-safe, data class, 확장 함수 맛보기 요약이 있습니다."
    }

    // ---------- 1. 정적 타입 ----------
    fun practiceStaticTyping(): String {
        val a = 42
        val b: String? = null
        val c: List<Int> = listOf(1, 2, 3)
        return "a=$a, b=$b, c=$c"
    }

    // ---------- 2. 간결한 문법 ----------
    fun practiceConciseSyntax(): String {
        fun add(a: Int, b: Int) = a + b
        data class Brief(val id: Long, val name: String)
        val doubled = listOf(1, 2, 3).map { it * 2 }
        val user = Brief(id = 1L, name = "토스")
        return "add(1,2)=${add(1, 2)}, user=$user, doubled=$doubled"
    }

    // ---------- 3. Null-safe ----------
    fun practiceNullSafe(): String {
        val name: String? = "토스"
        val len = name?.length ?: 0
        val safe = name?.uppercase()
        return "len=$len, safe=$safe"
    }

    // ---------- 4. data class ----------
    fun practiceDataClass(): String {
        data class UserDto(val id: Long, val name: String, val email: String? = null)
        val u = UserDto(1L, "토스", "toss@example.com")
        val (id, n, _) = u
        val copy = u.copy(name = "토스페이")
        return "id=$id, name=$n, copy=$copy"
    }

    // ---------- 5. 확장 함수 맛보기 ----------
    fun practiceExtensionFunction(): String {
        val withPrefix = "토스".addPrefix("회사: ")
        val doubled = 10.double()
        return "withPrefix=$withPrefix, doubled=$doubled"
    }
}

// 확장 함수: String에 addPrefix 추가
private fun String.addPrefix(prefix: String): String = "$prefix$this"

// 확장 함수: Int에 double 추가
private fun Int.double(): Int = this * 2
