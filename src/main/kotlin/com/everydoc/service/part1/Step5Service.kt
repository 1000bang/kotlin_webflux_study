package com.everydoc.service.part1

import org.springframework.stereotype.Service

/**
 * 5단계: 컬렉션 연습 (STEP5.md)
 * - List/Set/Map, listOf/mapOf/setOf, 불변·가변
 * - filter, map, forEach, fold, groupBy 등
 */
@Service
class Step5Service {

    fun hello(): String {
        println(practiceList())
        println(practiceSet())
        println(practiceMap())
        println(practiceFilterMapForEach())
        println(practiceFoldReduce())
        println(practiceCollectInLoop())
        println(practiceSortedDistinctGroupBy())
        return "Step5: List/Set/Map, 불변·가변, filter/map/forEach/fold/groupBy 연습이 있습니다."
    }

    // ---------- 1. List (불변/가변) ----------
    fun practiceList(): String {
        val readOnly = listOf(1, 2, 3)
        val mutable = mutableListOf(1, 2, 3)
        mutable.add(4)
        val empty = emptyList<Int>()
        val fromRange = (1..3).toList()
        val plus = readOnly + 4
        return "readOnly=$readOnly, mutable=$mutable, empty.size=${empty.size}, plus=$plus"
    }

    // ---------- 2. Set (중복 없음) ----------
    fun practiceSet(): String {
        val set = setOf(1, 2, 3, 2, 1)
        val mutable = mutableSetOf("a", "b", "c")
        mutable.add("a")
        return "set=$set, mutable=$mutable"
    }

    // ---------- 3. Map ----------
    fun practiceMap(): String {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val mutable = mutableMapOf("a" to 1, "b" to 2)
        mutable["c"] = 3
        val value = map[1]
        val default = map.getOrDefault(99, "?")
        return "map=$map, value(1)=$value, getOrDefault(99)=$default"
    }

    // ---------- 4. filter / map / forEach ----------
    fun practiceFilterMapForEach(): String {
        val list = listOf(1, 2, 3, 4, 5)
        val filtered = list.filter { it % 2 == 0 }
        val mapped = list.map { it * 2 }
        val found = list.find { it > 3 }
        val any = list.any { it > 4 }
        val all = list.all { it > 0 }
        val count = list.count { it % 2 == 0 }
        return "filter=$filtered, map=$mapped, find>$3=$found, any=$any, all=$all, count=$count"
    }

    // ---------- 5. fold / reduce / sum ----------
    fun practiceFoldReduce(): String {
        val list = listOf(1, 2, 3, 4, 5)
        val foldSum = list.fold(0) { acc, n -> acc + n }
        val reduceSum = list.reduce { acc, n -> acc + n }
        val sum = list.sum()
        val sumOf = list.sumOf { it * 2 }
        return "fold=$foldSum, reduce=$reduceSum, sum=$sum, sumOf(x2)=$sumOf"
    }

    // ---------- 6. 빈 컬렉션 선언 후 for/if 로 하나씩 add·put (실전 패턴) ----------
    fun practiceCollectInLoop(): String {
        val responseData = mutableListOf<String>()
        val sourceList = listOf("  a  ", "", "  b  ", " ", "  c  ")
        for (item in sourceList) {
            if (item.isNotBlank()) {
                responseData.add(item.trim())
            }
        }

        val resultMap = mutableMapOf<Int, String>()
        for (i in 1..5) {
            if (i % 2 == 0) {
                resultMap[i] = "even-$i"
            }
        }

        val resultSet = mutableSetOf<Int>()
        for (x in listOf(1, 2, 2, 3, 3, 3)) {
            if (x >= 2) resultSet.add(x)
        }

        return "list=$responseData, map=$resultMap, set=$resultSet"
    }

    // ---------- 7. sorted / distinct / groupBy ----------
    fun practiceSortedDistinctGroupBy(): String {
        val list = listOf(3, 1, 2, 2, 1, 3)
        val sorted = list.sorted()
        val distinct = list.distinct()
        val groupBy = listOf(1, 2, 3, 4, 5).groupBy { it % 2 }
        val associateWith = listOf(1, 2, 3).associateWith { it * 10 }
        return "sorted=$sorted, distinct=$distinct, groupBy=$groupBy, associateWith=$associateWith"
    }
}
