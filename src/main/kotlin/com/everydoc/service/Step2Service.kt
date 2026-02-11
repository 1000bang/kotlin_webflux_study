package com.everydoc.service

import org.springframework.stereotype.Service

/**
 * 2단계: 함수와 람다 연습 (STEP2.md)
 * - 함수 정의, 기본/이름 인자, 단일 표현식
 * - 람다, 고차 함수, 컬렉션 고차 함수
 */
@Service
class Step2Service {

    fun hello(): String {
        println(practiceFunctionDefinition())
        println(practiceDefaultArgument())
        println(practiceNamedArgument())
        println(practiceSingleExpression())
        println(practiceLambda())
        println(practiceHigherOrderFunction())
        println(practiceCollectionHigherOrder())
        println(practiceUnitAsType())
        return "Step2: 함수 정의, 기본/이름 인자, 단일 표현식, 람다, 고차 함수, 컬렉션 고차 함수 연습이 있습니다."
    }

    // ---------- 1. 함수 정의 ----------
    fun practiceFunctionDefinition(): String {
        fun greet(name: String): String {
            return "Hello, $name"
        }
        fun max(a: Int, b: Int): Int {
            return if (a > b) a else b
        }
        // Unit은 자바의 void와 비슷함 (반환값 없음). 생략 가능.
        fun noReturn(): Unit {}
        return "greet=${greet("토스")}, max(3,5)=${max(3, 5)}"
        //greet=Hello, 토스, max(3,5)=5
    }

    // ---------- 2. 기본 인자 (Default Argument) ----------
    fun practiceDefaultArgument(): String {
        fun greet(name: String, prefix: String = "안녕하세요"): String {
            return "$prefix, $name"
        }
        val a = greet("토스")
        val b = greet("토스", "반갑습니다")
        return "default=$a, override=$b"
        //default=안녕하세요, 토스, override=반갑습니다, 토스
    }

    // ---------- 3. 이름 인자 (Named Argument) ----------
    fun practiceNamedArgument(): String {
        fun createUser(id: Long, name: String, email: String? = null): String {
            return "id=$id,name=$name,email=$email"
        }
        val a = createUser(1L, "토스")
        val b = createUser(id = 1L, name = "토스", email = "toss@example.com")
        val c = createUser(name = "토스", id = 2L)
        return "a=$a|b=$b|c=$c"
        //a=id=1,name=토스,email=null|b=id=1,name=토스,email=toss@example.com|c=id=2,name=토스,email=null
    }

    // ---------- 4. 단일 표현식 함수 ----------
    fun practiceSingleExpression(): String {
        fun add(a: Int, b: Int) = a + b
        fun isEven(n: Int) = n % 2 == 0
        fun double(list: List<Int>) = list.map { it * 2 }
        val sum = add(2, 3)
        val even = isEven(4)
        val doubled = double(listOf(1, 2, 3))
        return "add=$sum, isEven(4)=$even, double=$doubled"
        //add=5, isEven(4)=true, double=[2, 4, 6]
    }

    // ---------- 5. 람다 (Lambda) ----------
    fun practiceLambda(): String {
        val double: (Int) -> Int = { x -> x * 2 }
        val sum: (Int, Int) -> Int = { a, b -> a + b }
        val doubleIt: (Int) -> Int = { it * 2 }
        val a = double(5)
        val b = sum(1, 2)
        val c = listOf(1, 2, 3).map { it * 2 }
        return "double(5)=$a, sum(1,2)=$b, map=$c"
        //double(5)=10, sum(1,2)=3, map=[2, 4, 6]
    }

    // ---------- 6. 고차 함수 (Higher-Order Function) ----------
    fun practiceHigherOrderFunction(): String {
        fun runTwice(block: () -> Unit) {
            block()
            block()
        }
        var count = 0
        runTwice { count++ }
        val runTwiceResult = count == 2

        fun <T, R> myMap(list: List<T>, transform: (T) -> R): List<R> = list.map(transform)
        val mapped = myMap(listOf(1, 2, 3)) { it * 2 }

        fun multiplier(factor: Int): (Int) -> Int = { x -> x * factor }
        val double = multiplier(2)
        val triple = multiplier(3)
        val doubleResult = double(5)
        val tripleResult = triple(5)

        return "runTwice=$runTwiceResult, myMap=$mapped, double(5)=$doubleResult, triple(5)=$tripleResult"
        //runTwice=true, myMap=[2, 4, 6], double(5)=10, triple(5)=15
    }

    // ---------- 7. 자주 쓰는 고차 함수 (컬렉션) ----------
    fun practiceCollectionHigherOrder(): String {
        val list = listOf(1, 2, 3, 4, 5)
        val filtered = list.filter { it % 2 == 0 }
        val mapped = list.map { it * 2 }
        val found = list.find { it > 3 }
        val any = list.any { it > 4 }
        val all = list.all { it > 0 }
        val sum = list.fold(0) { acc, n -> acc + n }
        val forEachLog = StringBuilder().apply {
            list.forEach { append(it) }
        }.toString()
        return "filter=$filtered, map=$mapped, find>$3=$found, any>$4=$any, all>0=$all, fold(sum)=$sum, forEach=$forEachLog"
        //filter=[2, 4], map=[2, 4, 6, 8, 10], find>$3=4, any>$4=true, all>0=true, fold(sum)=15, forEach=12345
    }

    // ---------- Unit을 타입으로 쓰는 예시 ----------
    fun practiceUnitAsType(): String {
        // 1. 변수 타입: Unit은 값이 하나뿐인 타입 (그 값이 Unit)
        val u: Unit = Unit
        val resultOfUnit: Unit = println("side effect")  // println은 Unit 반환

        // 2. 제네릭: "아무것도 안 담는" 리스트 타입으로 Unit 사용
        val listOfUnit: List<Unit> = listOf(Unit, Unit)

        // 3. 함수 타입 (() -> Unit): "인자 없이 호출하고, Unit을 반환하는 함수"
        val callback: () -> Unit = { println("callback") }
        callback()

        // 4. 고차 함수에서 "반환 없는 블록"을 받을 때
        fun runBlock(block: () -> Unit) {
            block()
        }
        runBlock { println("runBlock") }

        return "Unit as type: u=$u, listOfUnit.size=${listOfUnit.size}"
        /*
        side effect
        callback
        runBlock
        Unit as type: u=kotlin.Unit, listOfUnit.size=2
        */
    }
}
