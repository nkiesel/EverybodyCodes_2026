import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest18 {
    private data class P3(val x: Int, val y: Int, val w: Int)

    private fun parse(input: List<String>) = input.map { l -> l.ints().let { P3(it[0], it[1], it.getOrNull(2) ?: 1) } }

    fun one(input: List<String>): Int {
        val tops = parse(input)
        val mx = tops.maxOf { it.x }
        val my = tops.maxOf { it.y }
        val area = CharArea(mx + 1, my + 1, '.')
        tops.forEach { t ->
            (0..<t.y).forEach { y -> area[t.x, my - y] = '#' }
        }
        for (y in area.yRange) {
            val lx = area.row(y).indexOfFirst { it == '#' }
            val rx = area.row(y).indexOfLast { it == '#' }
            for (x in lx + 1 ..< rx) if (area[x,y] != '#') area[x,y] = '='
        }
        return area.tiles('#', '=').count()
    }

    fun two(input: List<String>): Int {
        val tops = parse(input)
        val maxx = tops.maxOf { it.x } + tops.last().w
        val maxy = tops.maxOf { it.y }
        val area = CharArea(maxx + 1, maxy + 1, '.')
        tops.forEach { [x, y, w] ->
            (0..<w).forEach { dx -> (0..<y).forEach { y -> area[x + dx, maxy - y] = '#' } }
        }
        for (y in area.yRange) {
            val lx = area.row(y).indexOfFirst { it == '#' }
            val rx = area.row(y).indexOfLast { it == '#' }
            for (x in lx + 1 ..< rx) if (area[x,y] != '#') area[x,y] = '='
        }
        return area.tiles('#', '=').count()
    }

    fun three(input: List<String>): Long {
        val tops = parse(input)
        val minx = tops.first().x
        val maxx = tops.last().x + tops.last().w
        val maxy = tops.maxOf { it.y }
        var total = 0L
        for (y in 0..<maxy) {
            val lx = tops.first { it.y >= maxy - y }.x
            val rx = tops.last { it.y >= maxy - y }.let { it.x + it.w }
            if (lx == minx && rx == maxx) {
                total += (rx - lx).toLong() * (maxy - y)
                break
            }
            total += rx - lx
        }
        return total
    }
}

val Quest18Test by testSuite {
    val quest = "18"

    with(Quest18) {
        test("one") {
            val sample = """
                x=0 y=5
                x=5 y=7
                x=6 y=8
                x=7 y=1
                x=8 y=3
                x=10 y=6
                x=15 y=3
                x=18 y=4
            """.trimIndent().lines()
            one(sample) shouldBe 96
            three(sample) shouldBe 96L

            val input = lines(quest, 1)
            one(input) shouldBe 854
            three(input) shouldBe 854L
        }

        test("two") {
            val sample = """
                x=0 y=5 width=1
                x=1 y=4 width=3
                x=5 y=3 width=2
                x=7 y=9 width=1
                x=8 y=8 width=1
                x=9 y=9 width=3
                x=12 y=3 width=1
                x=16 y=7 width=1
                x=19 y=3 width=5
                x=25 y=5 width=1
                x=29 y=1 width=2
                x=31 y=6 width=1
            """.trimIndent().lines()
            two(sample) shouldBe 205
            three(sample) shouldBe 205L

            val input = lines(quest, 2)
            two(input) shouldBe 2319025
            three(input) shouldBe 2319025L
        }

        test("three") {
            val input = lines(quest, 3)
            three(input) shouldBe 931343089533687L
        }
    }
}
