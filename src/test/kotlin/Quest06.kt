import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest06 {
    private typealias Data = Map<String, Set<String>>

    private fun parse(input: List<String>): Data =
        input.associate { l -> l.split(":").let { it[0] to it[1].split(",").toSet() } }

    fun one(input: List<String>): Int {
        val data = parse(input)
        val ingredients = data["NULL"]?.toMutableSet() ?: return 0
        while (true) {
            val ps = ingredients.size
            ingredients += ingredients.flatMap { i -> data[i]?.filter { it != "NULL" } ?: emptyList() }.toSet()
            if (ingredients.size == ps) return ingredients.size
        }
    }

    fun two(input: List<String>): Int = uses("NULL", parse(input)).size

    fun uses(name: String, data: Data): Set<String> {
        val ingredients = data.filter { name in it.value }.keys.toMutableSet()
        while (true) {
            val ps = ingredients.size
            ingredients += ingredients.flatMap { n -> data.filter { it.key != name && n in it.value }.keys }.toSet()
            if (ingredients.size == ps) return ingredients
        }
    }

    fun three(input: List<String>): Int {
        val data = parse(input)
        val v = (1..3).map { uses("NULLv$it", data) }
        return v.flatten().toSet().count { k -> v.count { k in it } >= 2 }
    }
}

val Quest06Test by testSuite {
    val quest = "06"

    with(Quest06) {
        test("one") {
            val sample = """
                BRDS:FLWR,SLTT,YSTS
                FLWR:SLTT,YSTS
                SLTT:YSTS,MLKK,BTTR
                NULL:BRDS,FLWR,SLTT
                YSTS:BRDS,FLWR
                MLKK:SPCS,NULL
                HNYN:BRDS,FLWR,SLTT,YSTS
            """.trimIndent().lines()
            one(sample) shouldBe 7

            val input = lines(quest, 1)
            one(input) shouldBe 48
        }

        test("two") {
            val sample = """
                BRDS:FLWR,SLTT,YSTS
                FLWR:SLTT,YSTS
                SLTT:YSTS,MLKK,BTTR
                NULL:BRDS,FLWR,SLTT
                YSTS:BRDS,FLWR
                MLKK:SPCS,NULL
                HNYN:BRDS,FLWR,SLTT,YSTS
            """.trimIndent().lines()
            two(sample) shouldBe 6

            val input = lines(quest, 2)
            two(input) shouldBe 830
        }

        test("three") {
            val sample = """
                MEAL:BRDS,CAKE,DOUG
                BRDS:DOUG,FLWR
                CAKE:DOUG,FLWR,NULLv2,NULLv3
                DOUG:FLWR,YSTS,SLTT,NULLv1
                FLWR:BRDS,DOUG
                YSTS:SPCS
                SOUP:BRDS,CAKE,DOUG,FLWR
            """.trimIndent().lines()
            three(sample) shouldBe 3

            val input = lines(quest, 3)
            three(input) shouldBe 1626
        }
    }
}
