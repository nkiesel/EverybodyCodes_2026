import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest07 {
    private fun parse(input: List<String>) = input.associate { Stage(it) }

    fun one(input: List<String>): Int {
        val stages = parse(input)
        stages.values.maxBy { it.connections.size }.sendImpulse(5, stages)
        return stages.values.sumOf { it.energy }
    }

    private data class Stage(var energy: Int, val connections: List<Int>) {
        companion object {
            operator fun invoke(input: String): Pair<Int, Stage> =
                input.ints().let { it[0] to Stage(it[1], it.drop(2)) }
        }

        fun sendImpulse(strength: Int, stages: Map<Int, Stage>) {
            energy += strength
            if (strength > 1) connections.forEach { stages[it]?.sendImpulse(strength - 1, stages) }
        }
    }

    fun two(input: List<String>): Int {
        val stages = parse(input)
        stages.values.maxBy { it.connections.size }.sendImpulse(10, stages)
        return stages.values.sumOf { it.energy }
    }

    fun three(input: List<String>, limit: Int = 1000000): Int {
        val stages = parse(input)
        var maxEnergy = 0
        for (id in stages.keys) {
            val sm = stages.mapValues { it.value.copy() }.toMutableMap()
            var receiving = sm.keys.associateWith { IntArray(11) }
            receiving[id]!![10] = 1
            while (receiving.isNotEmpty()) {
                val nr = sm.keys.associateWith { IntArray(11) }
                for ((k, v) in receiving) {
                    val s = sm[k] ?: continue
                    s.energy += v.foldIndexed(0) { i, a, v -> a + i * v }
                    s.connections.mapNotNull { nr[it] }.forEach { r ->
                        v.forEachIndexed { i, c -> if (i > 1) r[i - 1] += c }
                    }
                }
                sm.filter { it.value.energy >= limit }.forEach { exploded ->
                    sm.remove(exploded.key)
                    exploded.value.connections.mapNotNull { nr[it] }.forEach { r -> r[10] += 1 }
                }
                receiving = nr.filter { r -> r.value.any { it > 0 } }
            }
            val b = sm.values.sumOf { it.energy }
            maxEnergy = maxOf(maxEnergy, b)
        }
        return maxEnergy
    }
}

val Quest07Test by testSuite {
    val quest = "07"

    with(Quest07) {
        test("one") {
            val sample = """
                id=1, energy=5, connections=2,5,7
                id=2, energy=9, connections=6,7
                id=3, energy=3, connections=4
                id=4, energy=8, connections=3
                id=5, energy=1, connections=6,7
                id=6, energy=7, connections=1,7
                id=7, energy=6, connections=5
            """.trimIndent().lines()
            one(sample) shouldBe 102

            val input = lines(quest, 1)
            one(input) shouldBe 271
        }

        test("two") {
            val sample1 = """
                id=1, energy=5, connections=2,5,7
                id=2, energy=9, connections=6,7
                id=3, energy=3, connections=4
                id=4, energy=8, connections=3
                id=5, energy=1, connections=6,7
                id=6, energy=7, connections=1,7
                id=7, energy=6, connections=5
            """.trimIndent().lines()
            two(sample1) shouldBe 1316

            val sample2 = """
                id=1, energy=5, connections=2,3,5,7
                id=2, energy=9, connections=1,3,4,5,6,7
                id=3, energy=3, connections=1,2,4,6,7
                id=4, energy=8, connections=1,2,7
                id=5, energy=1, connections=2,3,4,6
                id=6, energy=7, connections=1,2,3,4
                id=7, energy=6, connections=1,2,4,6
            """.trimIndent().lines()
            two(sample2) shouldBe 1214633

            val input = lines(quest, 2)
            two(input) shouldBe 104117501
        }

        test("three") {
            val sample1 = """
                id=1, energy=5, connections=2,5,7
                id=2, energy=9, connections=6,7
                id=3, energy=3, connections=4
                id=4, energy=8, connections=3
                id=5, energy=1, connections=6,7
                id=6, energy=7, connections=1,7
                id=7, energy=6, connections=5
            """.trimIndent().lines()
            three(sample1, 100) shouldBe 187

            val input = lines(quest, 3)
            three(input) shouldBe 27533277
        }
    }
}
