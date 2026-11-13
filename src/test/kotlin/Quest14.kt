import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest14 {
    private fun parse(input: List<String>) = CharArea(input)

    fun one(input: List<String>): Int {
        val area = parse(input)
        val inside = area.tiles().sumOf { p -> area.neighbors4(p) { it != area[p] }.count() }
        return (area.xRange.count() + area.yRange.count()) * 2 + inside / 2
    }

    fun two(input: List<String>, part: Part = TWO): Int {
        val area = parse(input).surround(" ")
        val connections = mutableListOf<UndirectedEdge<Char>>()
        val seen = mutableSetOf<Char>()
        area.tiles().forEach { p ->
            val c = area[p]
            if (seen.add(c)) {
                val con = CountingMap<Char>()
                area.tiles(c).forEach { p2 -> area.neighbors4(p2) { it !in seen }.forEach { p3 -> con.inc(area[p3]) } }
                con.keys.forEach { k -> connections.add(UndirectedEdge(c, k, con.count(k).toInt())) }
            }
        }

        if (part == TWO) return minimumSpanningTree(connections, seen.size).second

        val pieces = seen.toList()
        var min = Int.MAX_VALUE
        for (a in 1..<pieces.lastIndex - 1) {
            for (b in a + 1..<pieces.lastIndex) {
                val ab = setOf(pieces[a], pieces[b])
                val co = connections.map { (from, to, weight) ->
                    UndirectedEdge(from, to, if (from in ab || to in ab) 0 else weight)
                }
                min = minOf(min, minimumSpanningTree(co, seen.size).second)
            }
        }

        return min
    }

    fun three(input: List<String>) = two(input, THREE)
}

val Quest14Test by testSuite {
    val quest = "14"

    with(Quest14) {
        test("one") {
            val sample = """
                AAAEEEFFFF
                AADDDDDGFF
                BACDDIGGFF
                BABBIIIGGH
                BBBBIIIIHH
            """.trimIndent().lines()
            one(sample) shouldBe 67

            val input = lines(quest, 1)
            one(input) shouldBe 281
        }

        test("two") {
            val sample = """
                AAAEEEFFFF
                AADDDDDGFF
                BACDDIGGFF
                BABBIIIGGH
                BBBBIIIIHH
            """.trimIndent().lines()
            two(sample) shouldBe 12

            val sample2 = """
                IIIIIILOHHHHHHHHGGGGGQSRRRRKKK
                IIIIIILOOHHHHHHGGXGGGQSRRRRKKK
                PPPPILLLOOOODDHBGGGGGQSSRRRKKK
                PPZPLLLLFFOODDBBBBGGQQQSSRKKKK
                PPPPLLLLFFFONDDDBBMGGQQSSKKJJJ
                PPPCCCCCCFFONNDDMMMGQQQSSSKJJJ
                PCCCCCCCCFFNNDDDDMAAQQQSSSKKJJ
                PPCCECCCFFNNNNDADMAATQTTSSSJJJ
                PPEEEEECCFFNNAAAMMATTTTTSTSSSJ
                PPEEEEEEEFFNNNAAAAAATTTTTTSSJJ
            """.trimIndent().lines()
            two(sample2) shouldBe 63

            val input = lines(quest, 2)
            two(input) shouldBe 495
        }

        test("three") {
            val sample = """
                IIIIIILOHHHHHHHHGGGGGQSRRRRKKK
                IIIIIILOOHHHHHHGGXGGGQSRRRRKKK
                PPPPILLLOOOODDHBGGGGGQSSRRRKKK
                PPZPLLLLFFOODDBBBBGGQQQSSRKKKK
                PPPPLLLLFFFONDDDBBMGGQQSSKKJJJ
                PPPCCCCCCFFONNDDMMMGQQQSSSKJJJ
                PCCCCCCCCFFNNDDDDMAAQQQSSSKKJJ
                PPCCECCCFFNNNNDADMAATQTTSSSJJJ
                PPEEEEECCFFNNAAAMMATTTTTSTSSSJ
                PPEEEEEEEFFNNNAAAAAATTTTTTSSJJ
            """.trimIndent().lines()
            three(sample) shouldBe 21

            val input = lines(quest, 3)
            three(input) shouldBe 1776
        }
    }
}
