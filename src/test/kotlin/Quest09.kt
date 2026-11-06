import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest09 {
    private data class Tree(val growth: Int, val limit: Int) {
        private val g2 = growth / 2

        fun grow(part: Part): Int {
            val branches = mutableListOf(1)
            var seconds = 1
            var splits = 0
            while (splits + (if (part == ONE) branches.sum() else 1) < limit) {
                var added = 0
                for (i in branches.indices) {
                    val b = branches[i]
                    branches[i] = if (b + 1 == growth) g2.also { added++ } else b + 1
                }
                repeat(added) { branches += g2; branches += 1 }
                splits += added
                seconds++
            }
            return seconds
        }
    }

    private data class Tree2(val growth: Long, val limit: Long) {
        private val g2 = growth / 2L

        fun grow(): Long {
            val branches = mutableMapOf(1L to 1L)
            var seconds = 1L
            var leaves = 1L
            while (leaves < limit) {
                val k = branches.keys.max()
                val c = branches[k]!!
                val d = growth - k
                branches.remove(k)
                branches.entries.sortedByDescending { it.key }.forEach { (height, count)  ->
                    branches[height + d] = branches.getOrDefault(height + d, 0) + count
                    branches.remove(height)
                }
                branches[1L] = branches.getOrDefault(1L, 0) + c
                branches[g2] = branches.getOrDefault(g2, 0) + c * 2L
                leaves += c
                seconds += d
            }
            return seconds
        }
    }

    private fun parse(input: List<String>) = input.map { it.ints() }.associate { it[0] to Tree(it[1], it[2]) }
    private fun parse2(input: List<String>) = input.map { it.longs() }.associate { it[0] to Tree2(it[1], it[2]) }

    fun one(input: List<String>): Int {
        return parse(input).values.sumOf { it.grow(ONE) }
    }

    fun two(input: List<String>): Long {
        return parse2(input).values.sumOf { it.grow() }
    }

    fun three(input: List<String>): Long {
        return parse2(input).values.sumOf { it.grow() }
    }
}

val Quest09Test by testSuite {
    val quest = "09"

    val sample = """
                id=1, growth=3, limit=10
                id=2, growth=5, limit=20
                id=3, growth=7, limit=30
            """.trimIndent().lines()

    with(Quest09) {
        test("one") {
            one(sample) shouldBe 27

            val input = lines(quest, 1)
            one(input) shouldBe 241
        }

        test("two") {
            two(sample) shouldBe 42

            val input = lines(quest, 2)
            two(input) shouldBe 15448
        }

        test("three") {
            val input = lines(quest, 3)
            three(input) shouldBe 858650313949860L
        }
    }
}
