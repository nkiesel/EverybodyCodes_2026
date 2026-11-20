import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.shouldBe

object Quest17 {
    fun one(input: List<String>): Int {
        val lx = input[0].lastIndex
        val ly = input.lastIndex
        val edges = buildList {
            for (x in 0..lx) for (y in 0..ly) {
                if (input[y][x] == '#') {
                    if (x < lx && input[y][x + 1] == '#') add(UndirectedEdge(Point(x, y), Point(x + 1, y), 1))
                    if (y < ly && input[y + 1][x] == '#') add(UndirectedEdge(Point(x, y), Point(x, y + 1), 1))
                }
            }
        }
        val sp = ShortestPathSolver(edges).shortestPath(Point(0, 0), Point(lx, ly))
        return sp?.first ?: 0
    }

    fun two(input: List<String>): Int {
        val area = CharArea(input).surround('.')
        return area.tiles('#').sumOf { area.neighbors4(it, '.').size }
    }

    fun three(input: List<String>): Int {
        val area1 = CharArea(input[0].length * 2 - 1, input.size * 2 - 1, ' ').surround(' ', '.')
        for (x in input[0].indices) for (y in input.indices) {
            area1[x * 2 + 2, y * 2 + 2] = input[y][x]
        }
        val n4 = [0 to -1, -1 to 0, 1 to 0, 0 to 1]
        area1.tiles('#').forEach { (x, y) ->
            n4.forEach { [dx, dy] -> if (area1[x + dx * 2, y + dy * 2] == '.') area1[x + dx, y + dy] = '*' }
        }
        area1.tiles(' ').forEach { p ->
            val n = area1.neighbors4(p, '*')
            area1[p] = when (n.size) {
                4 -> when {
                    area1[p.x - 1, p.y - 1] == '#' -> '<'
                    else -> '>'
                }

                2 -> when {
                    n.all { it.x == p.x } -> '|'
                    n.all { it.y == p.y } -> '-'
                    n.all { it.x >= p.x && it.y >= p.y } -> 'F'
                    n.all { it.x <= p.x && it.y <= p.y } -> 'J'
                    n.all { it.x <= p.x && it.y >= p.y } -> '7'
                    else -> 'L'
                }

                else -> ' '
            }
        }

        val area2 = CharArea(input[0].length + 1, input.size + 1, ' ')
        area2.tiles().forEach { p ->
            area2[p] = area1[p.x * 2 + 1, p.y * 2 + 1]
        }

        fun walk(initialDir: Direction): Int {
            var steps = 0
            var pos = Point(0, 0)
            val end = Point(area2.xRange.last, area2.yRange.last)
            var dir = initialDir
            while (pos != end) {
                steps++
                val next = pos.move(dir)
                val move = pos.direction(next)
                dir = when (area2[next]) {
                    '|' -> move
                    '-' -> move
                    'L' -> if (move == S) E else N
                    'J' -> if (move == S) W else N
                    '7' -> if (move == N) W else S
                    'F' -> if (move == N) E else S
                    '<' -> when (move) {
                        N -> E
                        S -> W
                        E -> N
                        else -> S
                    }

                    '>' -> when (move) {
                        N -> W
                        S -> E
                        E -> S
                        else -> N
                    }

                    else -> error("Invalid direction")
                }
                pos = next
            }
            return steps
        }

        return [Direction.S, Direction.E].minOf { walk(it) }
    }
}

val Quest17Test by testSuite {
    val quest = "17"

    with(Quest17) {
        test("one") {
            val sample = """
                ####.######
                #.#.##..#..
                ##.##.##..#
                #.#.###.###
                #.###.###.#
                ###.##.#.##
            """.trimIndent().lines()
            one(sample) shouldBe 21

            val input = lines(quest, 1)
            one(input) shouldBe 168
        }

        test("two") {
            val sample = """
                ####.######
                #.#.##..#..
                ##.##.##..#
                #.#.###.###
                #.###.###.#
                ###.##.#.##
            """.trimIndent().lines()
            two(sample) shouldBe 94

            val input = lines(quest, 2)
            two(input) shouldBe 6240
        }

        test("three") {
            val sample = """
                ####.######
                #.#.##..#..
                ##.##.##..#
                #.#.###.###
                #.###.###.#
                ###.##.#.##
            """.trimIndent().lines()
            three(sample) shouldBe 31

            val input = lines(quest, 3)
            three(input) shouldBe 2824
        }
    }
}
