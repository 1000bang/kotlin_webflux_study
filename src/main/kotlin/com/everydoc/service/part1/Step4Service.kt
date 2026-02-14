package com.everydoc.service.part1

import org.springframework.stereotype.Service

/**
 * 4단계: 널 안정성 연습 (STEP4.md)
 * - ?, ?., ?:, !!, let, require/check/requireNotNull
 */
@Service
class Step4Service {

    fun hello(): String {
        Thread.sleep(400)
        println(practiceNullableType("토스 결제"))
        println(practiceSafeCall("hello"))
        println(practiceElvis("elvis"))
        println(practiceNonNullAssertion("nonull!!"))
        println(practiceLet("ad@ex.com"))
        println(practiceRequireCheck("data"))
        println(practiceRequireFailure(10))
        return "Step4: nullable(?), safe call(?.), elvis(?:), !!, let, require/check 연습이 있습니다."
    }

    // ---------- 1. nullable 타입 ----------
    fun practiceNullableType(data: String?): String {
        val nonNull: String = "토스"
        val nullable: String? = null
        val nullableValue: String? = data
        return "nonNull=$nonNull, nullable=$nullable, nullableValue=$nullableValue"
        //nonNull=토스, nullable=null, nullableValue토스 결제
    }

    // ---------- 2. Safe call (?.) ----------
    fun practiceSafeCall(data: String?): String {
        val s: String? = data
        val len = s?.length
        val sNull: String? = null
        val lenNull = sNull?.length
        val chain = s?.uppercase()?.take(2)
        return "len=$len, lenNull=$lenNull, chain=$chain"
        //len=5, lenNull=null, chain=HE
    }

    // ---------- 3. Elvis (?:) ----------
    fun practiceElvis(data: String?): String {
        val name: String? = null
        val display = name ?: "이름 없음"
        val name2: String? = data
        val display2 = name2 ?: "이름 없음"
        val len = name?.length ?: 0
        return "display=$display, display2=$display2, len=$len"
        //display=이름 없음, display2=evlis, len=0
    }

    // ---------- 4. !! (Non-null 단언) ----------
    fun practiceNonNullAssertion(data: String?): String {
        val s: String? = data
        val len = s!!.length
        return "s!!.length=$len"
        //s!!.length=8
    }

    // ---------- 5. let ----------
    fun practiceLet(requestEmail: String?): String {
        val email: String? = requestEmail
        val formatted = email?.let { "[$it]" }
        val emailNull: String? = null
        val formattedNull = emailNull?.let { "[$it]" }
        val withElvis = emailNull?.let { "[$it]" } ?: "(none)"
        return "formatted=$formatted, formattedNull=$formattedNull, withElvis=$withElvis"
        //formatted=[ad@ex.com], formattedNull=null, withElvis=(none)
    }

    // ---------- 6. require / check / requireNotNull ----------
    fun practiceRequireCheck(data: String?): String {
        fun setAge(age: Int): String {
            require(age >= 0) { "age must be non-negative" }
            return "age=$age"
        }
        val ok = setAge(10)

        val value: String? = data
        val notNull = requireNotNull(value) { "value was null" }

        return "setAge=$ok, requireNotNull=$notNull"
        //setAge=age=10, requireNotNull=data
    }

    // require 실패 시 예외 (호출하는 쪽에서 try 또는 별도 테스트로 확인)
    fun practiceRequireFailure(age: Int): String {
        require(age >= 0) { "age must be non-negative, but was $age" }
        return "age=$age"
        //age=10
    }
}
