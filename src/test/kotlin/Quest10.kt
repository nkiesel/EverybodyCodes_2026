import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest10 {
    data class Ghost(val i: Int, val p: Point, val d: Direction, val c: Long = 0L)

    private fun parse(input: List<String>): Pair<CharArea, List<Ghost>> {
        val area = CharArea(input)
        var id = 0
        return area to area.tiles('<', '>', 'v', '^').map { Ghost(id++, it, Direction.from(area[it])) }.toList()
    }

    fun one(input: List<String>): Int {
        var (area, ghosts) = parse(input)
        var collisions = 0
        repeat(10) {
            val ng = mutableListOf<Ghost>()
            for (g in ghosts) {
                val np = g.p.move(g.d)
                if (area[np] == '#') {
                    ng.add(g.copy(d = g.d.turnRight()))
                    collisions++
                } else {
                    ng.add(g.copy(p = np))
                }
            }
            ghosts = buildList {
                for (gc in ng.groupBy { it.p }.values) {
                    if (gc.size > 1) {
                        collisions += gc.size
                        gc.forEach { g -> add(g.copy(d = g.d.reverse())) }
                    } else {
                        add(gc.first())
                    }
                }
            }
        }
        return collisions
    }

    fun two(input: List<String>, limit: Long): Long {
        var (area, ghosts) = parse(input)
        var rounds = 0L
        do {
            rounds++
            val ng = buildList {
                for (g in ghosts) {
                    val np = g.p.move(g.d)
                    if (area[np] == '#') {
                        if (g.c + 1 < limit) add(g.copy(d = g.d.turnRight(), c = g.c + 1))
                    } else {
                        add(g.copy(p = np))
                    }
                }
            }
            ghosts = buildList {
                for (gc in ng.groupBy { it.p }.values) {
                    if (gc.size > 1) {
                        gc.forEach { g -> if (g.c + 1 < limit) add(g.copy(d = g.d.reverse(), c = g.c + 1)) }
                    } else {
                        add(gc.first())
                    }
                }
            }
        } while (ghosts.isNotEmpty())
        return rounds
    }

    fun three(input: List<String>, limit: Long): Long {
        var (area, ghosts) = parse(input)
        var rounds = 0L
        var history = mutableListOf(ghosts to 0L)
        do {
            rounds++
            var collisions = false
            val ng = buildList {
                for (g in ghosts) {
                    val np = g.p.move(g.d)
                    if (area[np] == '#') {
                        if (g.c + 1 < limit) add(g.copy(d = g.d.turnRight(), c = g.c + 1))
                        collisions = true
                    } else {
                        add(g.copy(p = np))
                    }
                }
            }
            ghosts = buildList {
                for (gc in ng.groupBy { it.p }.values) {
                    if (gc.size > 1) {
                        gc.forEach { g -> if (g.c + 1 < limit) add(g.copy(d = g.d.reverse(), c = g.c + 1)) }
                        collisions = true
                    } else {
                        add(gc.first())
                    }
                }
            }
            if (collisions) {
                history = history.filter { it.first.size == ghosts.size }.toMutableList()
                for ((hg, ht) in history) {
                    if (ghosts.size == hg.size) {
                        val pp = ghosts.map { g -> g to hg.first { g.i == it.i } }
                        if (pp.all { (g, p) -> p.p == g.p && p.d == g.d }) {
                            val f = pp.minOf { (g, p) -> (limit - g.c) / (g.c - p.c) }
                            if (f == 0L) {
                                history.clear()
                                break
                            }
                            ghosts = pp.map { (g, p) -> g.copy(c = g.c + (g.c - p.c) * f) }
                            rounds += (rounds - ht) * f
                            break
                        }
                    }
                }
            }
            history += ghosts to rounds
        } while (ghosts.isNotEmpty())
        return rounds
    }
}

val Quest10Test by testSuite {
    val quest = "10"

    with(Quest10) {
        val sample = """
                ###########
                #####.#####
                ###.....###
                ##.......##
                ##.>>.<<.##
                #v...>...v#
                ##...v...##
                ##.......##
                ###.....###
                #####.#####
                ###########
            """.trimIndent().lines()

        test("one") {
            one(sample) shouldBe 29

            val input = lines(quest, 1)
            one(input) shouldBe 51
        }

        test("two") {
            two(sample, 10L) shouldBe 53L
            two(sample, 10000L) shouldBe 50000L

            val input = lines(quest, 2)
            two(input, 10000L) shouldBe 139791L
        }

        test("three") {
            val sample = """
                #######################
                ###########.###########
                #######.^>.^.<^.#######
                #####..<.>.v^>>^^<#####
                ####..v<^v.>.>v..^.####
                ###.................###
                ###.................###
                ##.......<...<.<....>##
                ##...v....>....v.v<.^##
                ##........###........##
                ##v......#####.......##
                #.<......#####......>.#
                ##.......#####.......##
                ##........###........##
                ##...................##
                ##...................##
                ###.................###
                ###.................###
                ####...............####
                #####.............#####
                #######....v....#######
                ###########.###########
                #######################
            """.trimIndent().lines()
            three(sample, 1000000000000L) shouldBe 13314695352588L

            val input = lines(quest, 3)
            three(input, 1000000000000L) shouldBe 14349949056099L
        }
    }
}
