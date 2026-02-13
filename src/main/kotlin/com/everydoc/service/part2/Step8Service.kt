package com.everydoc.service.part2

import org.springframework.stereotype.Service

/**
 * 8단계: 스코프 함수 연습 (STEP8.md)
 * - let, run, with, apply, also 차이와 사용처
 */
@Service
class Step8Service {

    fun hello(): String {
        println(practiceLet())
        println(practiceRun())
        println(practiceWith())
        println(practiceApply())
        println(practiceAlso())
        return "Step8: let, run, with, apply, also 스코프 함수 연습이 있습니다."
    }

    // ---------- 1. let (it, 블록 결과 반환) ----------
    fun practiceLet(): String {
        val nullable: String? = "hello"
        val len = nullable?.let { it.length } ?: 0
        val formatted = nullable?.let { "[$it]" } ?: "(null)"
        val nullStr: String? = null
        val lenNull = nullStr?.let { it.length } ?: -1
        return "let: len=$len, formatted=$formatted, lenNull=$lenNull"
    }

    // ---------- 2. run (this, 블록 결과 반환) ----------
    fun practiceRun(): String {
        data class User(val name: String, val email: String)
        val user = User("토스", "toss@example.com")
        val summary = user.run { "$name ($email)" }
        val result = "abc".run { length + 1 }
        return "run: summary=$summary, result=$result"
    }

    // ---------- 3. with (인자로 객체, this, 블록 결과 반환) ----------
    fun practiceWith(): String {
        val sb = StringBuilder()
        val result = with(sb) {
            append("a")
            append("b")
            append("c")
            toString()
        }
        return "with: result=$result"
    }

    // ---------- 4. apply (this, 수신자 자신 반환) ----------
    fun practiceApply(): String {
        data class Config(var url: String = "", var timeout: Int = 0)
        val config = Config().apply {
            url = "https://api.example.com"
            timeout = 30
        }
        val list = mutableListOf<Int>().apply {
            add(1)
            add(2)
            add(3)
        }
        return "apply: config=$config, list=$list"
    }

    // ---------- 5. also (it, 수신자 자신 반환) ----------
    fun practiceAlso(): String {
        val list = mutableListOf(1, 2, 3)
            .also { it.add(4) }
            .also { it.filter { n -> n > 2 } }
        val size = list.size
        val doubled = listOf(1, 2, 3)
            .also { println("원본: $it") }
            .map { it * 2 }
        return "also: list.size=$size, doubled=$doubled"
    }
}
