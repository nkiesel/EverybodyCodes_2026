import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest20 {
    private fun parse(input: List<String>) = input.map { it.replace(" ", "") }

    private val regex = Regex("""([\dA]+)([-+])([\dA]+)=([\dA]+)([-+])([\dA]+)""")

    private val segments = mapOf(
        '0' to 6,
        '1' to 2,
        '2' to 5,
        '3' to 5,
        '4' to 4,
        '5' to 5,
        '6' to 6,
        '7' to 3,
        '8' to 7,
        '9' to 6,
        '+' to 2,
        '-' to 1,
        '=' to 2,
        'A' to 6,
    )

    private val moveSegments = mapOf(
        '0' to ['6', '9', 'A'],
        '2' to ['3'],
        '3' to ['2', '5'],
        '5' to ['3'],
        '6' to ['0', '9', 'A'],
        '9' to ['0', '6', 'A'],
        'A' to ['0', '6', '9'],
    )

    private val removeSegments = mapOf(
        '6' to ['5'],
        '7' to ['1'],
        '8' to ['0', '6', '9', 'A'],
        '9' to ['3', '5'],
        '+' to ['-'],
    )

    private val addSegments = mapOf(
        '0' to ['8'],
        '1' to ['7'],
        '3' to ['9'],
        '5' to ['6', '9'],
        '6' to ['8'],
        '9' to ['8'],
        'A' to ['8'],
        '-' to ['+'],
    )

    private fun segmentCount(line: String, radix: Int): Int? {
        val g = regex.matchEntire(line)!!.groupValues
        val a = g[1].toInt(radix)
        val b = g[3].toInt(radix)
        val c = g[4].toInt(radix)
        val d = g[6].toInt(radix)
        val l = if (g[2] == "+") a + b else a - b
        val r = if (g[5] == "+") c + d else c - d
        return if (l == r) line.filter { it != ' ' }.sumOf { segments[it]!! } else null
    }

    private fun oneTwo(input: List<String>, radix: Int): String {
        return parse(input).sumOf { line -> segmentCount(line, radix) ?: 0 }.toString(radix)
    }

    fun one(input: List<String>) = oneTwo(input, 10)
    fun two(input: List<String>) = oneTwo(input, 11)

    fun three(input: List<String>): String {
        var total = 0L
        line@ for (line in parse(input)) {
            val c = segmentCount(line, 11)
            if (c != null) {
                total += c
                continue@line
            }
            val lc = line.toCharArray()

            for (i in lc.indices) {
                val c = lc[i]
                val m = moveSegments[c]
                if (m != null) {
                    for (o in m) {
                        lc[i] = o
                        val sc = segmentCount(lc.concatToString(), 11)
                        if (sc != null) {
                            total += sc
                            continue@line
                        }
                    }
                    lc[i] = c
                }
            }

            for (i in lc.indices) {
                val c = lc[i]
                val rm = removeSegments[c]
                if (rm != null) {
                    for (r in rm) {
                        lc[i] = r
                        for (j in i + 1 until lc.size) {
                            val o = lc[j]
                            val ad = addSegments[o]
                            if (ad != null) {
                                for (a in ad) {
                                    lc[j] = a
                                    val sc = segmentCount(lc.concatToString(), 11)
                                    if (sc != null) {
                                        total += sc
                                        continue@line
                                    }
                                }
                            }
                            lc[j] = o
                        }
                    }
                    lc[i] = c
                }
            }

            for (i in lc.indices) {
                val c = lc[i]
                val rm = addSegments[c]
                if (rm != null) {
                    for (r in rm) {
                        lc[i] = r
                        for (j in i + 1 until lc.size) {
                            val o = lc[j]
                            val ad = removeSegments[o]
                            if (ad != null) {
                                for (a in ad) {
                                    lc[j] = a
                                    val sc = segmentCount(lc.concatToString(), 11)
                                    if (sc != null) {
                                        total += sc
                                        continue@line
                                    }
                                }
                            }
                            lc[j] = o
                        }
                    }
                    lc[i] = c
                }
            }
        }

        return total.toString(11)
    }
}

val Quest20Test by testSuite {
    val quest = "20"

    with(Quest20) {
        test("one") {
            val sample = """
                5 - 4 = 9 - 8
                2 + 1 = 3 + 0
                6 + 7 = 4 + 9
                1 + 2 = 3 + 4
            """.trimIndent().lines()
            one(sample) shouldBe "75"

            val input = lines(quest, 1)
            one(input) shouldBe "389"
        }

        test("two") {
            val sample = """
                10 + 2A = 13 + 27
                98 - 54 = 76 - 32
                10 + 12 = 30 - 9
                20 - 10 = 5 + 5
            """.trimIndent().lines()
            two(sample) shouldBe "101"

            val input = lines(quest, 2)
            two(input) shouldBe "45328"
        }

        test("three") {
            val sample1 = """
                1 + 1 = 5 - 1
                1 + 1 = 3 - 3
                0 - 1 = 1 + 7
                1 + 1 = 1 - 6
                1 + 1 = 2 - 1
                1 + 1 = 5 + 7
                1 + 4 = 9 - 9
                1 + 1 = 2 + 9
                1 + 1 = 1 + 2
            """.trimIndent().lines()
            three(sample1) shouldBe "125"

            val sample2 = """
                4469 - 5934 = 1631 - 2AA7
                8471 - 2276 = 4510 + 2736
                A767 - 6104 = 1825 + 2538
                9961 + 1140 = 5963 + 2974
                9188 - 406A = 3598 + 1533
                96A6 - 1606 = 4676 - 3524
                9114 - 15A2 = 5258 + 2371
                7023 - 2209 = 1433 + 3436
                921A - 2696 = 2A91 + 3688
                8492 - 2055 = 4186 + 3260
                1234 + 4567 = 8901 + 2345
            """.trimIndent().lines()
            three(sample2) shouldBe "693"

            val input = lines(quest, 3)
            three(input) shouldBe "48026"
        }
    }
}
