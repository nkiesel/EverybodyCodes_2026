import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest11 {
    private fun parse(input: List<String>): Pair<Int, List<UndirectedEdge>> {
        val portals = input[0].substringAfter(": ").toInt()
        val extra =
            input.drop(2).map { lines -> lines.split(' ').map { it.toInt() }.let { UndirectedEdge(it[0], it[1], 1) } }
        return Pair(portals, extra)
    }

    fun one(input: List<String>): Int {
        val [portals, extra] = parse(input)
        val initial = (1 until portals).map { UndirectedEdge(it, it + 1, 1) }.toMutableList()
        initial += UndirectedEdge(portals, 1, 1)
        val s1 = ShortestPathSolver(initial)
        val n1 = (2..portals).sumOf { s1.shortedPath(1, it)!!.first }
        val s2 = ShortestPathSolver(initial + extra)
        val n2 = (1..portals).sumOf { a ->
            (1..portals).filter { it != a }.sumOf { b -> s2.shortedPath(a, b)!!.first }
        }
        return n1 * portals - n2
    }

    fun two(input: List<String>): Int {
        val [portals, extra] = parse(input)
        val r3 = (1..portals).toList() + (1..4).toList()
        val initial = (1..portals).flatMap { p -> r3.subList(p, p + 3).map { UndirectedEdge(p, it, 1) } }
        val s1 = ShortestPathSolver(initial)
        val n1 = (2..portals).sumOf { s1.shortedPath(1, it)!!.first }
        val combined = initial + extra
        val connected = combined.map { (from, to) -> setOf(from, to) }.toSet()
        val crystals = buildList {
            for (c1 in 1..portals) {
                for (c2 in 1..portals) {
                    if (c1 != c2 && [c1, c2] !in connected)
                        add(UndirectedEdge(c1, c2, 1))
                }
            }
        }
        var best = Int.MAX_VALUE
        val n2 = crystals.minOf { crystal ->
            val s2 = ShortestPathSolver(combined + crystal)
            (1..portals).sumOf { a ->
                (1..portals).filter { it != a }.sumOf { b -> s2.shortedPath(a, b, maxDistance = best)!!.first }
            }.also { best = minOf(it, best) }
        }
        return n1 * portals - n2
    }

    private fun generatePairs(
        remaining: List<Int>,
        currentPairs: List<Pair<Int, Int>>,
        results: MutableList<List<Pair<Int, Int>>>
    ) {
        if (remaining.isEmpty()) {
            results.add(currentPairs)
            return
        }

        val first = remaining[0]
        for (i in 1..<remaining.size) {
            val second = remaining[i]
            val newPair = Pair(first, second)
            val nextRemaining = remaining.filterIndexed { index, _ -> index != 0 && index != i }
            generatePairs(nextRemaining, currentPairs + newPair, results)
        }
    }

    fun three(input: List<String>): Int {
        val [portals, extra] = parse(input)
        val r3 = (1..portals).toList() + (1..4).toList()
        val initial = (1..portals).flatMap { p -> r3.subList(p, p + 3).map { UndirectedEdge(p, it, 1) } }
        val s1 = ShortestPathSolver(initial)
        val n1 = (2..portals).sumOf { s1.shortedPath(1, it)!!.first }
        val combined = initial + extra
        val cn = portals / 8
        val crystals = buildList {
            for (c1 in 1..portals) {
                val cc = List(8) { a -> (c1 + a * cn).let { if (it > portals) it - portals else it } }
                val results = mutableListOf<List<Pair<Int, Int>>>()
                generatePairs(cc, emptyList(), results)
                add(results.map { pairs -> pairs.map { [a, b] -> UndirectedEdge(a, b, 1) } })
            }
        }.flatten()
        var best = Int.MAX_VALUE
        val n2 = crystals.minOf { crystal ->
            val s2 = ShortestPathSolver(combined + crystal)
            (1..portals).sumOf { a ->
                (1..portals).filter { it != a }.sumOf { b -> s2.shortedPath(a, b, maxDistance = best)!!.first }
            }.also { best = minOf(it, best) }
        }
        return n1 * portals - n2
    }
}

val Quest11Test by testSuite {
    val quest = "11"

    with(Quest11) {
        test("one") {
            val sample = """
                portals: 10

                5 10
                2 7
            """.trimIndent().lines()
            one(sample) shouldBe 60

            val input = lines(quest, 1)
            one(input) shouldBe 1744
        }

        test("two") {
            val sample = """
                portals: 10

                5 10
                2 7
            """.trimIndent().lines()
            two(sample) shouldBe 6

            val input = lines(quest, 2)
            two(input) shouldBe 1790
        }

        test("three") {
//            val sample1 = """
//                portals: 32
//
//                1 10
//                20 30
//                15 29
//                13 10
//            """.trimIndent().lines()
//            three(sample1) shouldBe 854
//
//            val sample2 = """
//                portals: 64
//
//                1 40
//                3 35
//                5 42
//                7 50
//            """.trimIndent().lines()
//            three(sample2) shouldBe 9634

            val input = lines(quest, 3)
            three(input) shouldBe 20732
        }
    }
}
