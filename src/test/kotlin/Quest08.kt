import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest08 {
    private fun parse(input: List<String>) = input.map { it.toCharArray() }

    fun one(input: List<String>): Int {
        val area = parse(input)
        val lx = area[0].lastIndex
        val ly = area.lastIndex
        var rem = 0
        fun shadow(sx: Int, sy: Int) {
            var x = sx
            var y = sy
            var shadow = '0'
            while (x <= lx && y <= ly) {
                val e = area[y][x]
                if (e != '.') if (e <= shadow) area[y][x] = '.' else shadow = e.also { rem++ }
                if (shadow > '0') shadow--
                x += 1
                y += 1
            }
        }
        for (sx in 0..lx) shadow(sx, 0)
        for (sy in 1..ly) shadow(0, sy)
        return rem
    }

    private val deltas = [Point(1, 1), Point(-1, 1), Point(-2, 0), Point(-1, -1), Point(1, -1), Point(2, 0)]

    fun two(input: List<String>): Int {
        val parsed = CharArea(input)
        var area = parsed.clone()
        val lx = area.xRange.last
        val ly = area.yRange.last

        fun shadow(s: Point, d: Point) {
            var p = s
            var shadow = '0'
            while (p in area) {
                val e = area[p]
                if (e != '.') if (e <= shadow) area[p] = '.' else shadow = e
                if (shadow > '0') shadow--
                p = p.move(d)
            }
        }

        var req = 0
        for (sx in area.xRange) shadow(Point(sx, 0), deltas[0])
        for (sy in 1..ly) shadow(Point(0, sy), deltas[0])
        req += area.count { it != '.' }
        area = parsed.clone()
        for (sx in area.xRange) shadow(Point(sx, 0), deltas[1])
        for (sy in 1..ly) shadow(Point(lx, sy), deltas[1])
        req += area.count { it != '.' }
        area = parsed.clone()
        for (sy in area.yRange) {
            val sx = area.xRange.reversed().first { area[it, sy] != '.' }
            shadow(Point(sx, sy), deltas[2])
        }
        req += area.count { it != '.' }
        area = parsed.clone()
        for (sx in area.xRange) shadow(Point(sx, ly), deltas[3])
        for (sy in 0..<ly) shadow(Point(lx, sy), deltas[3])
        req += area.count { it != '.' }
        area = parsed.clone()
        for (sx in area.xRange) shadow(Point(sx, ly), deltas[4])
        for (sy in 0..<ly) shadow(Point(0, sy), deltas[4])
        req += area.count { it != '.' }
        area = parsed.clone()
        for (sy in area.yRange) {
            val sx = area.xRange.first { area[it, sy] != '.' }
            shadow(Point(sx, sy), deltas[5])
        }
        req += area.count { it != '.' }
        return req
    }

    fun three(input: List<String>): Int {
        val area = CharArea(input)
        val lx = area.xRange.last
        val ly = area.yRange.last
        var army = 0
        var total = 0

        fun shadow(s: Point, d: Point) {
            var p = s
            var shadow = '0'
            while (p in area) {
                val e = area[p]
                if (e != '.') {
                    area[p] = when {
                        e < '1' -> e + 2
                        e > shadow -> {
                            shadow = e
                            total++
                            if (e == '9') '+'.also { army++ } else e + 1
                        }
                        else -> e
                    }
                }
                if (shadow > '0') shadow--
                p = p.move(d)
            }
        }

        while (true) {
            for (sx in area.xRange) shadow(Point(sx, 0), deltas[0])
            for (sy in 1..ly) shadow(Point(0, sy), deltas[0])
            if (army >= 100000) return total
            for (sx in area.xRange) shadow(Point(sx, 0), deltas[1])
            for (sy in 1..ly) shadow(Point(lx, sy), deltas[1])
            if (army >= 100000) return total
            for (sy in area.yRange) {
                val sx = area.xRange.reversed().first { area[it, sy] != '.' }
                shadow(Point(sx, sy), deltas[2])
            }
            if (army >= 100000) return total
            for (sx in area.xRange) shadow(Point(sx, ly), deltas[3])
            for (sy in 0..<ly) shadow(Point(lx, sy), deltas[3])
            if (army >= 100000) return total
            for (sx in area.xRange) shadow(Point(sx, ly), deltas[4])
            for (sy in 0..<ly) shadow(Point(0, sy), deltas[4])
            if (army >= 100000) return total
            for (sy in area.yRange) {
                val sx = area.xRange.first { area[it, sy] != '.' }
                shadow(Point(sx, sy), deltas[5])
            }
            if (army >= 100000) return total
        }
    }
}

val Quest08Test by testSuite {
    val quest = "08"

    with(Quest08) {
        test("one") {
            val sample = """
                ....1.9.6.1.7....
                ...9.2.3.3.8.1...
                ..7.8.3.5.1.4.3..
                .4.6.7.4.1.1.8.9.
                4.5.3.6.5.8.9.4.9
                .9.5.1.5.6.2.8.9.
                ..7.7.6.4.6.2.4..
                ...9.3.8.3.8.4...
                ....1.6.6.2.8....
            """.trimIndent().lines()
            one(sample) shouldBe 32

            val input = lines(quest, 1)
            one(input) shouldBe 184
        }

        test("two") {
            val sample = """
                ....1.9.6.1.7....
                ...9.2.3.3.8.1...
                ..7.8.3.5.1.4.3..
                .4.6.7.4.1.1.8.9.
                4.5.3.6.5.8.9.4.9
                .9.5.1.5.6.2.8.9.
                ..7.7.6.4.6.2.4..
                ...9.3.8.3.8.4...
                ....1.6.6.2.8....
            """.trimIndent().lines()
            two(sample) shouldBe 187

            val input = lines(quest, 2)
            two(input) shouldBe 1089
        }

        test("three") {
            val sample = """
                ....1.9.6.1.7....
                ...9.2.3.3.8.1...
                ..7.8.3.5.1.4.3..
                .4.6.7.4.1.1.8.9.
                4.5.3.6.5.8.9.4.9
                .9.5.1.5.6.2.8.9.
                ..7.7.6.4.6.2.4..
                ...9.3.8.3.8.4...
                ....1.6.6.2.8....
            """.trimIndent().lines()
            three(sample) shouldBe 899889

            val input = lines(quest, 3)
            three(input) shouldBe 899247
        }
    }
}
