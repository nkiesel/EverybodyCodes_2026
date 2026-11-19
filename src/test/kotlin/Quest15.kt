import de.infix.testBalloon.framework.core.testSuite
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe

object Quest15 {
    private fun parse(input: List<String>) = CharArea(input)

    fun one(input: List<String>): Int {
        val area = parse(input)
        val s = area.first('S')
        val e = area.first('E')
        val deposits = area.tiles('@').toSet()
        var p = s
        val mustSeen = mutableMapOf<Point, Int>()
        var prev = 0
        while (p != e) {
            if (area.yRange.count { area[p.x, it] != '#' } == 1) {
                mustSeen[p] = deposits.count { it.x < p.x } - prev
                prev += mustSeen[p]!!
            }
            p = p.move(1, 0)
        }
        var total = 0
        for ([start, end] in mustSeen.keys.zipWithNext()) {
            val queue = ArrayDeque([[start to emptySet<Point>()]])
            var min = Int.MAX_VALUE
            var steps = 0
            while (queue.isNotEmpty()) {
                steps++
                val p = queue.removeFirst()
                val l = p.last()
                val ll = (if (p.size > 1) p[p.size - 2] else l).first
                if (l.first == end) {
                    if (l.second.size == mustSeen[end]) {
                        min = minOf(min, p.size)
                    }
                } else if (p.size < min) {
                    val neighbors = area.neighbors4(l.first) { it != '#' }.filter { n ->
                        if (n == ll || n in l.second || (n in mustSeen.keys && p.any { n == it.first })) false else {
                            val ls = p.lastOrNull { it.first == n }
                            ls == null || ls.second.size < l.second.size
                        }
                    }
                    neighbors.forEach { n ->
                        var d = l.second
                        if (area[n] == '@') {
                            d = d + n
                        }
                        queue.addFirst(p + (n to d))
                    }
                }
            }
            total += min
        }
        return total - mustSeen.size + 2
    }

    private fun walkLoop(area: CharArea, start: Point, dir: Direction): List<Point>? {
        if (area[start.move(dir)] == '#') return null
        return buildList {
            var p = start
            var d = dir
            var lastTurn = false
            do {
                add(p)
                var n = p.move(d)
                if (area[n] == '#') {
                    d = if (dir == N) d.turnRight() else d.turnLeft()
                    n = p.move(d)
                }
                p = n
                if (!lastTurn && p.y == start.y) {
                    lastTurn = true
                    d = if (dir == N) d.turnRight() else d.turnLeft()
                }
            } while (p != start)
        }
    }

    private fun walkLoopB(area: CharArea, start: Point): StatB {
        var p = start
        var d = Direction.N
        var rx = 0
        var stepsU = 1
        var stepsD = 1
        var depU = 0
        var depD = 0
        do {
            if (p.y < start.y) stepsU++ else if (p.y > start.y) stepsD++
            var n = p.move(d)
            if (area[n] == '#') {
                d = d.turnRight()
                n = p.move(d)
            }
            p = n
            if (area[p] == '@') {
                if (p.y < start.y) depU++ else depD++
            }
            if (rx == 0 && p.y == start.y) rx = p.x
        } while (p != start)
        return StatB(depU, depD, start.x, rx, stepsU, stepsD)
    }

    private data class Stat(val dep: Int, val lx: Int, val rx: Int, val steps: Int) {
        val width = rx - lx
        val rate = dep.toDouble() / steps
        val points = mutableSetOf<Point>()

        constructor(area: CharArea, loop: List<Point>, sy: Int) : this(
            loop.count { area[it] == '@' },
            loop.filter { it.y == sy }.minOf { it.x },
            loop.filter { it.y == sy }.maxOf { it.x },
            loop.size
        ) {
            points.addAll(loop)
        }

        fun combine(inner: Stat): Stat {
            val [a, b] = [this, inner]
            val steps = a.steps + b.steps - a.width - b.width + b.lx - a.lx + a.rx - b.rx
            return Stat(
                a.dep + b.dep,
                a.lx,
                a.rx,
                steps
            ).also { it.points.addAll(inner.points); it.points.addAll(this.points) }
        }

        fun wraps(other: Stat) = (lx <= other.lx && rx >= other.rx)
    }

    private data class StatB(val depU: Int, val depD: Int, val lx: Int, val rx: Int, val stepsU: Int, val stepsD: Int) {
        val dep = depU + depD
        val steps = stepsU + stepsD
        val width = rx - lx
        val rate = (dep).toDouble() / steps
    }

    fun two(input: List<String>, part: Part = TWO): Long {
        val area = parse(input)
        val s = area.first('S')
        val e = area.first('E')
        var p = s
        val usedX = [Direction.N, Direction.S].associateWith { mutableSetOf<Int>() }
        val single = buildList {
            while (p != e) {
                p = p.move(Direction.E)
                for ((key, value) in usedX) {
                    if (p.x !in value) {
                        val l = walkLoop(area, p, key) ?: continue
                        value += l.filter { it.y == s.y }.maxOf { it.x }
                        add(l)
                    }
                }
            }
        }.map { Stat(area, it, s.y) }

        val combined = buildList {
            for (a in 0..<single.lastIndex) {
                val sa = single[a]
                for (b in a + 1..single.lastIndex) {
                    val sb = single[b]
                    if (sa.wraps(sb)) add(sa.combine(sb))
                }
            }
        }

        val loops = (single + combined).sortedByDescending { it.rate }
        val required = if (part == TWO) 2026L else 202620262026L
        for (ls in loops) {
            val steps = area.xRange.last - (ls.rx - ls.lx) * 2 + required / ls.dep * ls.steps
            val missing = (required % ls.dep).toInt()
            if (part == TWO || missing == 0) return steps
            val added = loops.find { l -> l.dep % missing == 0 }
            if (added != null) {
                return steps + (added.rx - added.lx) * 2 + missing / added.dep * added.steps
            }
        }
        error("boo")
    }

    fun threeA(input: List<String>) = two(input, Part.THREE)

    fun threeB(input: List<String>): Long {
        val area = parse(input)
        val s = area.first('S')
        val e = area.first('E')
        var p = s
        val usedX = mutableSetOf<Int>()
        val loops = buildList {
            while (p != e) {
                p = p.move(Direction.E)
                if (p.x !in usedX && area[p.move(Direction.N)] != '#') {
                    val l = walkLoopB(area, p)
                    usedX += l.rx
                    add(l)
                }
            }
        }

        val loop = loops.maxBy { it.rate }
        val required = 202620262026L
        val loopCount = required / loop.dep
        var loopSteps = loop.steps * loopCount
        val missing = required - loop.dep * loopCount
        if (missing > 0) {
            val queue = ArrayDeque<List<Pair<StatB, Direction>>>()
            for (l in loops) {
                if (l.depU <= missing) queue.add(listOf(Pair(l, Direction.N)))
                if (l.depD <= missing) queue.add(listOf(Pair(l, Direction.S)))
                if (l.dep <= missing) queue.add(listOf(Pair(l, Direction.N), Pair(l, Direction.S)))
            }
            var minSteps = Int.MAX_VALUE
            var best = listOf<Pair<StatB, Direction>>()
            while (queue.isNotEmpty()) {
                val chain = queue.removeFirst()
                val steps = chain.sumOf { if (it.second == Direction.N) it.first.stepsU else it.first.stepsD }
                if (steps > minSteps) continue
                val deps = chain.sumOf { if (it.second == Direction.N) it.first.depU else it.first.depD }
                val req = missing - deps
                if (req == 0L && steps < minSteps) {
                    minSteps = steps
                    best = chain
                } else {
                    val lastX = chain.last().first.rx
                    loops.filter { it.lx >= lastX }.forEach { l ->
                        if (l.depU <= missing - deps) queue.add(listOf(Pair(l, Direction.N)))
                        if (l.depD <= missing - deps) queue.add(listOf(Pair(l, Direction.S)))
                        if (l.dep <= missing - deps) queue.add(listOf(Pair(l, Direction.N), Pair(l, Direction.S)))
                    }
                }
            }
            val bs = best.sumOf { if (it.second == Direction.N) it.first.stepsU else it.first.stepsD }
            loopSteps += bs
        }
        return loopSteps
    }
}

val Quest15Test by testSuite {
    val quest = "15"

    with(Quest15) {
        test("one") {
            val sample = """
                #####################################################
                #.@.........#................#.....................##
                #.#########@#.##############.#.###################.##
                #.#.......#.#.##..........##.#.###..@..@......@###.##
                #.#.#####.#.#.##.########.##.#.###@###########.###.##
                #.#.#@@@#.#.#.##.##....##.##@#.###.###.....###.###.##
                #.#.#.#.#.#.#.##.##.##.##.##.#.###.###.###.###.###.##
                S...................................................E
                #.#.#.#.#.#.#.##.##.##.##.##.#.###.###.###.###.###@##
                #.#.#...#.#.#.##@##.@..##.##.#.###.###@....###.###.##
                #.#.#####.#.#.##.########.##.#.###.###########.###.##
                #.#.......#.#.##..@.......##.#.###.............###.##
                #.#########.#.##############.#.###################.##
                #...........#................#................@....##
                #####################################################
            """.trimIndent().lines()
            one(sample) shouldBe 184

            val input = lines(quest, 1)
            one(input) shouldBe 305
        }

        test("two") {
            val sample = """
                #####################################################
                #.@.........#................#.....................##
                #.#########@#.##############.#.###################.##
                #.#.......#.#.##..........##.#.###..@..@......@###.##
                #.#.#####.#.#.##.########.##.#.###@###########.###.##
                #.#.#@@@#.#.#.##.##....##.##@#.###.###.....###.###.##
                #.#.#.#.#.#.#.##.##.##.##.##.#.###.###.###.###.###.##
                S...................................................E
                #.#.#.#.#.#.#.##.##.##.##.##.#.###.###.###.###.###@##
                #.#.#...#.#.#.##@##.@..##.##.#.###.###@....###.###.##
                #.#.#####.#.#.##.########.##.#.###.###########.###.##
                #.#.......#.#.##..@.......##.#.###.............###.##
                #.#########.#.##############.#.###################.##
                #...........#................#................@....##
                #####################################################
            """.trimIndent().lines()
            two(sample) shouldBe 5448L

            val input = lines(quest, 2)
            two(input) shouldBe 12241L
        }

        test("three") {
            val sample = """
                #####################################################
                #.@.........#................#..........@..........##
                #.#########@#.##############.#.###################.##
                #.#...@...#.#.##.....@....##.#.###..@..@......@###.##
                #.#.#####.#.#.##.########.##.#.###@###########.###.##
                #.#.#@@@#.#.#.##.##..@.##.##@#.###.###..@..###.###.##
                #.#.#.#.#.#.#.##.##.##.##.##.#.###.###.###.###.###.##
                S...................................................E
                #.#.#.#.#.#.#.##.##.##.##.##.#.###.###.###.###.###@##
                #.#.#.@.#.#.#.##@##.@..##.##.#.###.###@....###.###.##
                #.#.#####.#.#.##.########.##.#.###.###########.###.##
                #.#...@...#.#.##..@.......##.#.###......@......###.##
                #.#########.#.##############.#.###################.##
                #.....@.....#........@.......#................@....##
                #####################################################
            """.trimIndent().lines()
            threeA(sample) shouldBe 540320698784L

            val input = lines(quest, 3)
            assertSoftly {
                // Both are wrong.  Entered some nearby values and got the correct result
                threeA(input) shouldBe 328384562637L
                threeB(input) shouldBe 328384562637L
            }
        }
    }
}
