package com.everydoc.service

import org.springframework.stereotype.Service
import java.util.ArrayDeque
import java.util.LinkedList
import java.util.Queue
import java.util.TreeMap

@Service
class Step1Service {

    fun hello(): String{
        println("1. 변수선언: ${practiceVariableDeclaration()}" )
        println("2. 배열: ${practiceArray()}")
        println("3. 리스트: ${practiceList()}")
        println("4. 맵: ${practiceMap()}")
        println("5. 세트: ${practiceSet()}")
        println("6. 큐: ${practiceQueue()}")
        println("7. 그래프: ${practiceGraph()}")
        println("8. 트리 맵: ${practiceTreeMap()}")
        println("9. 반복: ${practiceLoop()}")
        println("10. 조건: ${practiceCondition()}")
        
        return "Step1: 변수선언, Array, List, Map, Set, Queue, Graph, TreeMap, 반복, 조건 연습용 함수가 있습니다."
    }

    // ---------- 1. 변수 선언 ----------
    fun practiceVariableDeclaration(): String {
        val immutable = "불변 (재할당 불가)"
        var mutable = 0
        mutable++

        val num: Int = 42
        val long: Long = 1L
        val double: Double = 3.14
        val float: Float = 3.14f
        val flag: Boolean = true
        val ch: Char = 'A'
        val text: String = "hello"

        return "immutable=$immutable, mutable=$mutable, num=$num"
    }

    // ---------- 2. Array ----------
    fun practiceArray(): String {
        val arr = arrayOf(1, 2, 3, 4, 5)
        val arrTyped = arrayOf<Int>(1, 2, 3)
        val arrSize = Array(5) { it * 10 }  // [0, 10, 20, 30, 40] 함수형 배열 초기화

        val first = arr[0]
        arr[0] = 99  // 가변 배열은 수정 가능
        val size = arr.size
        val join = arr.joinToString(",")

        return "arr=${arr.contentToString()}, join=$join"
    }

    // ---------- 3. List ----------
    fun practiceList(): String {
        val readOnly = listOf(1, 2, 3, 4, 5)
        val mutable = mutableListOf(1, 2, 3)
        mutable.add(4)
        mutable.remove(1)

        // readOnly는 listOf() → add() 없음 (읽기 전용)
        val first = readOnly.first()
        val last = readOnly.last()
        val sub = readOnly.subList(0, 2)
        val filtered = readOnly.filter { it % 2 == 0 }

        return "readOnly=$readOnly, mutable=$mutable, filtered=$filtered"
    }

    // ---------- 4. Map ----------
    fun practiceMap(): String {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val mutable = mutableMapOf("a" to 1, "b" to 2)
        mutable["c"] = 3
        mutable.put("d", 4)

        val value = map[1]
        val keys = map.keys
        val values = map.values
        val entries = map.entries

        return "map=$map, value of 1=$value"
    }

    // ---------- 5. Set ----------
    fun practiceSet(): String {
        val set = setOf(1, 2, 3, 2, 1)  // 중복 제거 → [1, 2, 3]
        val mutable = mutableSetOf("a", "b", "c")
        mutable.add("d")
        mutable.remove("b")

        val has = set.contains(2)
        val size = set.size
        val listFromSet = set.toList()

        return "set=$set, contains 2=$has"
    }

    // ---------- 6. Queue (자바 Queue 사용) ----------
    fun practiceQueue(): String {
        val queue: Queue<Int> = LinkedList(listOf(1, 2, 3))
        queue.offer(4)
        val head = queue.poll()
        val peek = queue.peek()

        val deque: Queue<String> = ArrayDeque(listOf("a", "b", "c"))
        deque.offer("d")
        val next = deque.poll()

        return "poll=$head, peek=$peek, remaining=${queue.toList()}"
    }

    // ---------- 7. Graph (인접 리스트로 표현) ----------
    fun practiceGraph(): String {
        // 0 -> [1, 2], 1 -> [2], 2 -> [0, 3], 3 -> []
        val graph = mapOf(
            0 to listOf(1, 2),
            1 to listOf(2),
            2 to listOf(0, 3),
            3 to emptyList(),
        )
        val neighborsOf0 = graph[0] ?: emptyList()
        val mutableGraph = mutableMapOf<Int, MutableList<Int>>()
        mutableGraph[0] = mutableListOf(1, 2)
        mutableGraph.getOrPut(1) { mutableListOf() }.add(2)

        return "graph keys=${graph.keys}, neighbors of 0=$neighborsOf0"
    }

    // ---------- 8. TreeMap (정렬된 Map, 자바 TreeMap) ----------
    fun practiceTreeMap(): String {
        val treeMap = TreeMap<Int, String>()
        treeMap[3] = "three"
        treeMap[1] = "one"
        treeMap[2] = "two"
        // 키 순서대로 정렬됨: 1, 2, 3

        val firstKey = treeMap.firstKey()
        val lastKey = treeMap.lastKey()
        val subMap = treeMap.subMap(1, 3)  // 1 이상 3 미만

        val fromKotlin = sortedMapOf(3 to "c", 1 to "a", 2 to "b")

        return "treeMap=$treeMap, firstKey=$firstKey, lastKey=$lastKey"
    }

    // ---------- 9. 반복 ----------
    fun practiceLoop(): String {
        val sb = StringBuilder()

        for (i in 1..5) sb.append("$i ")
        sb.append("| ")
        for (i in 1 until 5) sb.append("$i ")
        sb.append("| ")
        for (i in 5 downTo 1) sb.append("$i ")
        sb.append("| ")

        val list = listOf(10, 20, 30)
        for (v in list) sb.append("$v ")
        sb.append("| ")
        for ((idx, v) in list.withIndex()) sb.append("${idx}:$v ")

        var count = 0
        while (count < 2) {
            sb.append("w$count ")
            count++
        }
        do {
            sb.append("d$count ")
            count++
        } while (count < 4)

        return sb.toString().trim()
    }

    // ---------- 10. 조건 ----------
    fun practiceCondition(): String {
        val a = 10
        val b = 20

        val max = if (a > b) a else b
        val desc = if (a > b) "a가 큼" else if (a < b) "b가 큼" else "같음"

        val x = 2
        val whenResult = when (x) {
            1 -> "one"
            2, 3 -> "two or three"
            in 4..10 -> "four to ten"
            else -> "other"
        }

        val obj: Any = "hello"
        val typeResult = when (obj) {
            is String -> "길이=${obj.length}"
            is Int -> "정수"
            else -> "기타"
        }

        val grade = 85
        val pass = when {
            grade >= 90 -> "A"
            grade >= 80 -> "B"
            grade >= 70 -> "C"
            else -> "F"
        }

        return "max=$max, when(x)=$whenResult, type=$typeResult, pass=$pass"
    }
}
