import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.ints.shouldNotBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.text.isBlank

object Quest13 {
    data class Hand(val hands: Int, val direction: Int, val current: Int, val move: Int) {
        var position: Int = 0
        var previous: Int = 0
        var factor: Int = 1
        var max: Int = 0
        var active = move != 0

        fun range(): Set<Int> {
            return if (direction == 1) {
                if (position > previous) ((previous + 1)..position).toSet() else ((previous + 1)..<max).toSet() + (0..position).toSet()
            } else {
                if (position < previous) (position..<previous).toSet() else (position..<max).toSet() + (0..<previous).toSet()
            }
        }

        fun overlap(o: Hand): Boolean {
            if (position == o.position) return true
            val range = range()
            if (active && !o.active && o.position in range) return true
            val orange = o.range()
            if (!active && o.active && position in orange) return true
            if (active != o.active) return false
            return if (direction == o.direction) {
                position in orange && previous in orange || o.position in range && o.previous in range
            } else {
                previous in orange || o.previous in range || position in orange || o.position in range
            }
        }

        override fun toString(): String {
            return "Hand(hands=$hands, direction=$direction, current=$current, move=$move, position=$position, previous=$previous, factor=$factor, max=$max, active=$active)"
        }

        fun different(o: Hand): Boolean {
            return direction != o.direction || hands != o.hands || current != o.current
        }
    }

    private fun parse(input: List<String>): List<Hand> {
        val hands = input[0].ints()
        val direction = input[1].substringAfter(" ").split(":")
        val current = input[2].ints()
        val move = input[3].ints()
        return (0..2).map { Hand(hands[it], if (direction[it] == "CW") 1 else -1, current[it], move[it]) }
    }

    fun one(input: List<String>): Int {
        val hands = parse(input)
        val positions = lcm(hands.map { it.hands })
        hands.forEach {
            it.factor = positions / it.hands
            it.position = it.current * it.factor
            it.max = positions
        }
        var calibrations = 0
//        println("start positions: ${hands.map { it.position }}")
        for (tick in 1..hands.maxOf { it.move }) {
            for (h in hands) {
                h.active = tick <= h.move
                if (h.active) {
                    h.previous = h.position
                    h.position = (h.position + h.factor * h.direction + positions * h.factor) % positions
                }
            }
            for (i1 in 0 until hands.lastIndex) {
                val h1 = hands[i1]
                for (i2 in i1 + 1 until hands.size) {
                    val h2 = hands[i2]
                    if ((h1.active || h2.active) && h1.different(h2)) {
                            if (h1.overlap(h2) || h2.overlap(h1)) {
                                calibrations++
//                                println("calibration #$calibrations at tick $tick\n    h1 $h1\n    h2 $h2")
                            }
                    }
                }
            }
            val current = hands.map { it.position }
//            println("$calibrations calibrations after step $tick, current positions: $current")
        }

        return calibrations
    }

    fun twoTest(input: List<String>): Int {
        var total = 0
        for (lines in input.chunkedBy(String::isBlank)) {
            if (lines.size != 6) continue
            val name = lines.first()
            val expected = lines.last().ints()[0]
            val result = one(lines.drop(1).dropLast(1))
            if (result != expected) {
                println("$name expected $expected, got $result")
            }
            total += result
        }
        return total
    }

    fun two(input: List<String>): Int {
        return input.chunkedBy(String::isBlank).sumOf { one(it) }
    }

    fun three(input: List<String>): Int {
        return 0
    }
}

val Quest13Test by testSuite {
    val quest = "13"

    with(Quest13) {
        test("one") {
            val sample1 = """
                hands 6:8:12
                direction CW:CCW:CW
                current 2:4:8
                move 9:7:4
            """.trimIndent().lines()
            one(sample1) shouldBe 5

            val sample2 = """
                hands 6:8:12
                direction CW:CCW:CW
                current 1:2:2
                move 10:12:15
            """.trimIndent().lines()
            one(sample2) shouldBe 7

            val sample3 = """
                hands 6:8:12
                direction CW:CCW:CW
                current 1:2:2
                move 10:12:20
            """.trimIndent().lines()
            one(sample3) shouldBe 9

            val input = lines(quest, 1)
            one(input) shouldNotBeInRange 38..38
            one(input) shouldBe 26
        }

        test("two") {
            val sample = """
                hands 6:8:12
                direction CW:CCW:CW
                current 2:4:8
                move 9:7:4

                hands 6:8:12
                direction CW:CCW:CW
                current 1:2:2
                move 10:12:15

                hands 6:8:12
                direction CW:CCW:CW
                current 1:2:2
                move 10:12:20
            """.trimIndent().lines()
            two(sample) shouldBe 21

            val input = lines(quest, 2)
            val two = two(input)
            two shouldNotBe 128798
            two shouldBe 128678
        }

        test("three") {
            val sample = """
            """.trimIndent().lines()
            three(sample) shouldBe 0

//            val input = lines(quest, 3)
//            three(input) shouldBe 0
        }
    }
}
