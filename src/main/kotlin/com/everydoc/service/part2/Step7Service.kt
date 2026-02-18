package com.everydoc.service.part2

import org.springframework.stereotype.Service

/**
 * 7단계: 확장 함수·프로퍼티 연습 (STEP7.md)
 * - fun Type.함수(), 유틸을 멤버처럼 쓰기
 */
@Service
class Step7Service {

    fun hello(): String {
        println(practiceExtensionFunction())
        println(practiceNullableReceiver())
        println(practiceGenericExtension())
        println(practiceExtensionProperty())
        return "Step7: 확장 함수·확장 프로퍼티 연습이 있습니다."
    }

    // ---------- 1. 확장 함수 기본 ----------
    fun practiceExtensionFunction(): String {
        val withPrefix = "토스".addPrefix("회사: ")
        val doubled = 10.double()
        val positive = (-5).isPositive()
        return "withPrefix=$withPrefix, doubled=$doubled, isPositive=$positive"
    }

    // ---------- 2. nullable 수신자 ----------
    fun practiceNullableReceiver(): String {
        val a: String? = "hello"
        val b: String? = null
        val orEmptyA = a.orEmpty()
        val orEmptyB = b.orEmpty()
        return "a.orEmpty()=$orEmptyA, null.orEmpty()=$orEmptyB"
    }

    // ---------- 3. 제네릭 타입 확장 ----------
    fun practiceGenericExtension(): String {
        val list = listOf(10, 20, 30)
        val second = list.secondOrNull()
        val empty = emptyList<Int>().secondOrNull()
        return "secondOrNull=$second, empty.secondOrNull()=$empty"
    }

    // ---------- 4. 확장 프로퍼티 ----------
    fun practiceExtensionProperty(): String {
        val last = "토스".lastChar
        val list = listOf(1, 2, 3, 4)
        val secondToLast = list.secondToLast
        return "lastChar=$last, secondToLast=$secondToLast"
    }
}

// 확장 함수
private fun String.addPrefix(prefix: String): String = "$prefix$this"
private fun Int.double(): Int = this * 2
private fun Int.isPositive(): Boolean = this > 0

// nullable 수신자
private fun String?.orEmpty(): String = this ?: ""

// 제네릭 확장
private fun <T> List<T>.secondOrNull(): T? = this.getOrNull(1)

// 확장 프로퍼티
private val String.lastChar: Char
    get() = this[length - 1]

private val <T> List<T>.secondToLast: T?
    get() = this.getOrNull(size - 2)
