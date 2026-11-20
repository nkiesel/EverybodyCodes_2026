import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest19 {
    private fun parse(input: List<String>) = input.chunkedBy(String::isEmpty)

    private fun solution(a: List<String>, b: List<String>, bm: Map<Char, Char>): Long {
        val cm = CountingMap<Char>()
        for (y in a.indices) for (x in a[0].indices) {
            val p = listOf(a[y][x], bm[b[y][x]]!!).sorted()
            val r = when {
                p[0] == 'K' || p[1] == 'K' -> 'K'
                p[0] == 'C' && p[1] == 'M' -> 'B'
                p[0] == 'C' && p[1] == 'Y' -> 'G'
                p[0] == 'M' && p[1] == 'Y' -> 'R'
                else -> p[0]
            }
            cm.inc(r)
        }
        return cm.values().sorted().takeLast(3).product()
    }

    private val chars = ['M', 'C', 'Y', 'K']

    fun one(input: List<String>): Long {
        val [a, b] = parse(input)
        val bm = chars.associateWith { it }
        return solution(a, b, bm)
    }

    fun two(input: List<String>): Long {
        val [a, b] = parse(input)
        return chars.permutations().maxOf { perm ->
            val bm = perm.withIndex().associate { [i, c] -> (i + 1).digitToChar() to c }
            solution(a, b, bm)
        }
    }

    fun three(input: List<String>): Long {
        val count = (input[0].length * input.size).toLong()
        val numK = input.sumOf { l -> l.count { it == 'K' }.toLong() }
        val other = count - numK
        val q2 = other / 2
        val withK = numK * q2 * when (other % 2) {
            0L -> q2
            else -> q2 + 1
        }
        val q3 = other / 3
        val withoutK = q3 * when (other % 3) {
            0L -> q3 * q3
            1L -> q3 * (q3 + 1)
            else -> (q3 + 1) * (q3 + 1)
        }
        return maxOf(withK, withoutK)
    }
}

val Quest19Test by testSuite {
    val quest = "19"

    with(Quest19) {
        test("one") {
            val sample = """
                CCCCC
                MMMMM
                YYYYY
                KKKKK
                CCMYY

                CMYKC
                CMYKC
                CMYKC
                CMYKC
                CMYKC
            """.trimIndent().lines()
            one(sample) shouldBe 144L

            val input = lines(quest, 1)
            one(input) shouldBe 3084912L
        }

        test("two") {
            val sample = """
                CCCCC
                MMMMM
                YYYYY
                KKKKK
                CCMYY

                14231
                14231
                14231
                14231
                14231
            """.trimIndent().lines()
            two(sample) shouldBe 180L

            val input = lines(quest, 2)
            two(input) shouldBe 274206252000L
        }

        test("three") {
            val sample1 = """
                CCMM
                YYKK
            """.trimIndent().lines()
            three(sample1) shouldBe 18L

            val sample2 = """
                CCCMMM
                CCMMYY
                CCCYKK
                CMMYYK
            """.trimIndent().lines()
            three(sample2) shouldBe 343L

            val sample3 = """
                CCCCYMMM
                CCMMMYYY
                CCCYYYMM
                MMCCCYYY
                YYYMMMCC
                CCYYMMKK
                MMYYCCKK
                YYYCCMMM
            """.trimIndent().lines()
            three(sample3) shouldBe 8000L

            val input = lines(quest, 3)
            three(input) shouldBe 2045803439350L
        }
    }
}
