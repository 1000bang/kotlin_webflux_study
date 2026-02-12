package com.everydoc.service.part1

import org.springframework.stereotype.Service

/**
 * 3단계: 클래스와 객체 연습 (STEP3.md)
 * - class, 생성자, 프로퍼티
 * - data class, object, companion object
 */
@Service
class Step3Service {

    fun hello(): String {
        println(practiceClassAndConstructor())
        println(practiceProperty())
        println(practiceDataClass())
        println(practiceObject())
        println(practiceCompanionObject())  
        return "Step3: class, 생성자, 프로퍼티, data class, object, companion object 연습이 있습니다."
    }

    // ---------- 1. 클래스와 생성자 ----------
    fun practiceClassAndConstructor(): String {
        class User(val name: String, var age: Int) {
            // init 블록은 클래스 인스턴스가 생성될 때 실행되는 초기화 블록
            init {
                require(age >= 19) { "age must be 19 or older" }
            }
        }
        // 이때 Init 블록이 실행됨  User("토스", 18) 이렇게 생성될 때
        // require(age >= 19) { "age must be 19 or older" } 이 실행됨
        // age는 18이므로 조건이 거짓이므로 IllegalArgumentException 예외가 발생함
        // 그러므로 user.age = 21 이 실행되지 않음
        try{
            val user = User("토스", 18)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            //age must be 19 or older
        }
                
        val user = User("토스", 20)
        
        return "name=${user.name}, age=${user.age}"
        //name=토스, age=20
    }

    // ---------- 2. 프로퍼티 (커스텀 getter/setter) ----------
    fun practiceProperty(): String {
        class Person(val name: String) {
            var age: Int = 0
                private set

            val isAdult: Boolean
                // get()은 프로퍼티 값을 가져올 때 호출되는 함수
                get() = age >= 19

            fun setAge(value: Int) {
                age = value
            }
        }
        val p = Person("토스")
        p.setAge(20)
        return "name=${p.name}, isAdult=${p.isAdult}"
        //name=토스, isAdult=true
    }

    // ---------- 3. data class ----------
    fun practiceDataClass(): String {
        data class UserDto(
            val id: Long,
            val name: String,
            val email: String? = null,
        )
        val u = UserDto(1L, "토스", "toss@example.com")
        val copy = u.copy(name = "토스페이")
        val (id, name, email) = u  // destructuring
        /*
        Destructuring(구조 분해)은 객체를 풀어서 그 안의 값들을 한 번에 변수에 넣는 문법
        아래와 같은 의미
        val id   = u.component1()   // 1L
        val name = u.component2()   // "토스"
        val email = u.component3()  // "toss@example.com"
        
        */
        return "u=$u, copy=$copy, id=$id"
        //u=UserDto(id=1, name=토스, email=toss@example.com), copy=UserDto(id=1, name=토스페이, email=toss@example.com), id=1
    }

    // ---------- 4. object (싱글톤) ----------
    fun practiceObject(): String {
        val url = Step3Config.API_URL
        val timeout = Step3Config.getTimeout()
        return "url=$url, timeout=$timeout"
        //url=https://api.example.com, timeout=30
    }

    // ---------- 5. companion object ----------
    fun practiceCompanionObject(): String {
        val minAge = UserFactory.MIN_AGE
        val user = UserFactory.create("토스")
        return "MIN_AGE=$minAge, user=${user.name}"
        //MIN_AGE=0, user=토스
    }
}

// STEP3 연습용: object (싱글톤)
object Step3Config {
    const val API_URL = "https://api.example.com"
    fun getTimeout(): Int = 30
}

// STEP3 연습용: companion object
//companion object는 클래스에 딸린 싱글톤 객체. 자바의 static 멤버처럼 클래스 이름만으로 접근 가능
class UserFactory(val name: String) {
    companion object {
        const val MIN_AGE = 0
        fun create(name: String): UserFactory = UserFactory(name)
    }
}
