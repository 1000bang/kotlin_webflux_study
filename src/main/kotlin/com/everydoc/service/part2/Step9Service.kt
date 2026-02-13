package com.everydoc.service.part2

import org.springframework.stereotype.Service

/**
 * 9단계: 왜 코틀린을 쓰는가 (STEP9.md)
 * - null 안정성, 간결한 문법만 실제 코드로 맛보기
 */
@Service
class Step9Service {

    fun hello(): String {
        println(practiceNullSafety())
        println(practiceConciseness())
        return "Step9: 왜 코틀린을 쓰는가 — null 안정성·간결한 문법 맛보기."
    }

    // ---------- null 안정성 (실제 코드) ----------
    fun practiceNullSafety(): String {
        val name: String? = "토스"
        val nullName: String? = null
        val display = name ?: "(이름 없음)"
        val len = nullName?.length ?: 0
        val safe = name?.uppercase()
        return "null 안정성: display=$display, len=$len, safe=$safe"
    }

    // ---------- 간결한 문법 (실제 코드) ----------
    fun practiceConciseness(): String {
        data class User(val id: Long, val name: String)
        fun double(x: Int) = x * 2
        val user = User(1L, "토스")
        val list = listOf(1, 2, 3).map { it * 2 }
        return "간결함: user=$user, double(5)=${double(5)}, list=$list"
    }
}
