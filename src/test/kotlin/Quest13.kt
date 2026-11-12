import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest13 {
    data class Hand(val hands: Int, val direction: Int, val current: Int, val move: Long) {
        var position: Int = 0
        var previous: Int = 0
        var factor: Int = 1
        var max: Int = 0
        var active = move != 0L

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
            return direction != o.direction || hands != o.hands || current != o.current || active != o.active
        }
    }

    private fun parse(input: List<String>): List<Hand> {
        val hands = input[0].ints()
        val direction = input[1].substringAfter(" ").split(":")
        val current = input[2].ints()
        val move = input[3].longs()
        return (0..2).map { Hand(hands[it], if (direction[it] == "CW") 1 else -1, current[it], move[it]) }
    }

    private data class HistoryKey(
        val p1: Int,
        val p2: Int,
        val p3: Int,
        val a1: Boolean,
        val a2: Boolean,
        val a3: Boolean
    )

    fun one(input: List<String>): Long {
        val hands = parse(input)
        val positions = lcm(hands.map { it.hands })
        val history = mutableMapOf<HistoryKey, Pair<Long, Long>>()
        hands.forEach {
            it.factor = positions / it.hands
            it.position = it.current * it.factor
            it.max = positions
        }
        var calibrations = 0L
        var tick = 0L
        val maxMoves = hands.maxOf { it.move }
        while (++tick <= maxMoves) {
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
                        }
                    }
                }
            }
            val current = hands.map { it.position }
            val key =
                HistoryKey(current[0], current[1], current[2], hands[0].active, hands[1].active, hands[2].active)
            val previous = history[key]
            if (previous != null) {
                val minMoves = hands.filter { it.active }.minOf { it.move }
                val d = (minMoves - tick) / (tick - previous.first)
                tick += d * (tick - previous.first)
                calibrations += d * (calibrations - previous.second)
                history.clear()
            } else {
                history[key] = tick to calibrations
            }
        }

        return calibrations
    }

    fun two(input: List<String>): Long {
        return input.chunkedBy(String::isBlank).sumOf { one(it) }
    }

    fun three(input: List<String>): Long {
        return input.chunkedBy(String::isBlank).sumOf { one(it) }
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
            two(input) shouldBe 128678
        }

        test("three") {
            val input = lines(quest, 3)
            three(input) shouldBe 1017007696393L
        }
    }
}
