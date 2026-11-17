import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest16 {
    private fun parse(input: List<String>, rep: Int) = input[0].repeat(rep).toCharArray()

    fun one(input: List<String>): Long = solution(input, ONE, 1)
    fun two(input: List<String>, rep: Int = 10): Long = solution(input, TWO, rep)
    fun three(input: List<String>, rep: Int = 100): Long = solution(input, THREE, rep)

    fun solution(input: List<String>, part: Part, rep: Int = 10): Long {
        val chars = parse(input, rep)
        val n = chars.size
        var i1 = 0
        var result = 0L

        while (i1 < n) {
            val c = chars[i1]
            var i2 = i1 + 1
            while (i2 < n && chars[i2] == c) i2++
            var i4 = n - 1
            while (i4 >= i2 && chars[i4] != c) i4--
            if (i2 < i4) {
                chars.reverse(i2, i4 + 1)
                result += if (part == ONE) 1 else i4 - i2 + 1
            } else {
                i1 = i2
            }
        }
        return result
    }

    fun threeFastest(input: List<String>, targetRep: Int = 100): Long {
        val step = 8
        val rem = targetRep % step
        val startRep = if (rem >= 2) rem else rem + step

        val y = [0, 1, 2].associateWith { threeFast(input, startRep + it * step) }
        val y0 = y[0]!!
        val y1 = y[1]!!
        val y2 = y[2]!!

        val diff = y1 - y0
        val secondDiff = (y2 - y1) - diff
        val k = ((targetRep - startRep) / step).toLong()

        return y0 + k * diff + (k * (k - 1) / 2) * secondDiff
    }

    fun threeFast(input: List<String>, rep: Int = 100): Long {
        var cur = parse(input, rep)
        var flips = 0L

        while (cur.isNotEmpty()) {
            val c = cur[0]
            val next = CharArray(cur.size)
            var outR = cur.size - 1
            var l = 0
            var r = cur.size - 1
            var rev = false

            while (l <= r && cur[l] == c) l++

            while (l <= r) {
                if (!rev) {
                    while (l <= r && cur[r] != c) next[outR--] = cur[r--]
                    if (l <= r) {
                        flips += (r - l + 1)
                        r--
                        rev = true
                        while (l <= r && cur[r] == c) r--
                    }
                } else {
                    while (l <= r && cur[l] != c) next[outR--] = cur[l++]
                    if (l <= r) {
                        flips += (r - l + 1)
                        l++
                        rev = false
                        while (l <= r && cur[l] == c) l++
                    }
                }
            }
            cur = next.copyOfRange(outR + 1, next.size)
        }
        return flips
    }

}

val Quest16Test by testSuite {
    val quest = "16"

    with(Quest16) {
        test("one") {
            val sample = """
                RRGGBBGRRGGBBG
            """.trimIndent().lines()
            one(sample) shouldBe 3

            val sample2 = """
                BBRGGRRGBBRGGBRGBBRRBRRRBGGRRRBGBGGBBRGGRRGBBRGGBRGBBRRBRRRBGGRRRBGBGG
            """.trimIndent().lines()
            one(sample2) shouldBe 26

            val input = lines(quest, 1)
            one(input) shouldBe 62
        }

        test("two") {
            val sample = """
                RRYGYBYG
            """.trimIndent().lines()
            two(sample) shouldBe 1053L

            val input = lines(quest, 2)
            two(input) shouldBe 24867797L
        }

        test("three") {
            val sample = """
                RRYGGYBBYG
            """.trimIndent().lines()
            three(sample) shouldBe 135154L
            threeFast(sample) shouldBe 135154L
            threeFastest(sample) shouldBe 135154L

            val input = lines(quest, 3)
            threeFastest(input) shouldBe 24776542662851L
        }
    }
}
