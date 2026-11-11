import de.infix.testBalloon.framework.core.testSuite
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldNotBeInRange
import io.kotest.matchers.shouldBe

object Quest12 {
    private fun parse(input: List<String>): List<CharArray> {
        val width = input[0].length
        return buildList {
            add(".".repeat(width + 2).toCharArray())
            input.forEach { l -> add(".$l.".toCharArray()) }
            add(".".repeat(width + 2).toCharArray())
        }
    }

    const val magma = '#'

    fun one(input: List<String>): Int {
        val area = parse(input)
        var spells = 0
        while (true) {
            val y = area.indexOfFirst { magma in it }
            if (y == -1) return spells
            val x = area[y].indexOf(magma)
            var dx = 1
            while (area[y][x + dx] == magma) dx++
            var dy = 1
            while (area[y + dy][x] == magma) dy++
            spells += minOf(dx, dy)
            val sc = if (dx <= dy) '|' else '-'
            for (sy in y..<y + dy) for (sx in (x..<x + dx)) area[sy][sx] = sc
        }
    }

    fun two(input: List<String>): Pair<Int, List<Int>> {
        val area = parse(input)
        val sl = mutableListOf(0)
        var spells = 0
        while (true) {
            var ty = area.indexOfFirst { magma in it }
            sl += spells - sl.sum()
            if (sl.lastIndex == 230) {
                val a = sl.sum()
                println(a)
            }
            if (ty == -1) {
                return spells to sl
            }
            var row = area[ty]
            var lx = row.indexOf(magma)
            var rx = lx
            val stack = mutableListOf(lx to ty)
            while (row[rx + 1] == magma) {
                rx++; stack.add(rx to ty)
            }
            var dy = ty + 1
            while (true) {
                row = area[dy]
                if (row[lx] == magma) {
                    rx = lx - 1
                    while (row[lx - 1] == magma) {
                        lx--; stack.add(lx to dy)
                    }
                    while (row[rx + 1] == magma) {
                        rx++; stack.add(rx to dy)
                    }
                } else if (row[rx] == magma) {
                    lx = rx + 1
                    while (row[rx + 1] == magma) {
                        rx++; stack.add(rx to dy)
                    }
                    while (row[lx - 1] == magma) {
                        lx--; stack.add(lx to dy)
                    }
                } else break
                dy++
            }
            lx = stack.minOf { it.first }
            rx = stack.maxOf { it.first }
            ty = stack.minOf { it.second }
            dy = stack.maxOf { it.second }
            val w = rx - lx + 1
            val h = dy - ty + 1
            val l = [lx to ty, lx to dy].all { it in stack }
            val r = [rx to ty, rx to dy].all { it in stack }
            val t = [lx to ty, rx to ty].all { it in stack }
            val d = [lx to dy, rx to dy].all { it in stack }
            stack.forEach { [x, y] -> area[y][x] = '.' }
            if (l && r && t && d) {
                spells += minOf(w, h)
            } else if (l && t) {
                if (w < h) {
                    while (lx to dy in stack) {
                        stack.removeIf { it.first == lx }
                        lx++
                        spells++
                    }
                    dy = stack.maxOf { it.second }
                } else {
                    while (rx to ty in stack) {
                        stack.removeIf { it.second == ty }
                        ty++
                        spells++
                    }
                    rx = stack.maxOf { it.first }
                }
                spells += minOf(rx - lx + 1, dy - ty + 1)
            } else if (l && d) {
                if (w < h) {
                    while (lx to ty in stack) {
                        stack.removeIf { it.first == lx }
                        lx++
                        spells++
                    }
                    ty = stack.minOf { it.second }
                } else {
                    while (rx to dy in stack) {
                        stack.removeIf { it.second == dy }
                        dy--
                        spells++
                    }
                    rx = stack.maxOf { it.first }
                }
                spells += minOf(rx - lx + 1, dy - ty + 1)
            } else if (r && t) {
                if (w < h) {
                    while (rx to dy in stack) {
                        stack.removeIf { it.first == rx }
                        rx--
                        spells++
                    }
                    dy = stack.maxOf { it.second }
                } else {
                    while (lx to ty in stack) {
                        stack.removeIf { it.second == ty }
                        ty++
                        spells++
                    }
                    lx = stack.minOf { it.first }
                }
                spells += minOf(rx - lx + 1, dy - ty + 1)
            } else if (r && d) {
                if (w < h) {
                    while (rx to ty in stack) {
                        stack.removeIf { it.first == rx }
                        rx--
                        spells++
                    }
                    ty = stack.minOf { it.second }
                } else {
                    while (lx to dy in stack) {
                        stack.removeIf { it.second == dy }
                        dy--
                        spells++
                    }
                    lx = stack.minOf { it.first }
                }
                spells += minOf(rx - lx + 1, dy - ty + 1)
            }
        }
    }

    fun three(input: List<String>, img: Boolean = false): Pair<Int, List<Int>> {
        val area = parse(input)
        var spells = 0
        val sl = mutableListOf(0)
        var count = 0
        //if (img) CharArea(area.map { it.concatToString() }).png()
        while (true) {
            val ss = spells
            sl += spells - sl.sum()
//            val lastToggle = sl.last()
//            val lastIndex = sl.lastIndex
//            if (count == 25) {
//                val i = sl.lastIndex
//                println(i)
//            }
//            if (sl.lastIndex == 290) {
//                val a = sl.sum()
//                println(a)
//            }
//            if (sl.last() == 27) {
//                val i = sl.lastIndex
//                println(i)
//            }
            var ty = area.indexOfFirst { magma in it }
            if (ty == -1) {
                //if (img) CharArea(area.map { it.concatToString() }).png(Tiles.DIGIT)
                return spells to sl
            }
            var row = area[ty]
            var lx = row.indexOf(magma)
            var rx = lx
            val stack = mutableListOf(lx to ty)
            while (row[rx + 1] == magma) {
                rx++; stack.add(rx to ty)
            }
            var dy = isRectangle(area, lx, rx, ty)
            val isRectangle = dy != null
            if (isRectangle) {
                for (y in ty + 1..dy) {
                    var n = 0
                    while (area[y][lx + n] == magma) stack.add(lx + n++ to y)
                    if (rx to y !in stack) {
                        n = 0
                        while (area[y][rx - n] == magma) stack.add(rx - n++ to y)
                    }
                }
            } else {
                dy = ty + 1
                while (true) {
                    row = area[dy]
                    if (row[lx] == magma) {
                        rx = lx - 1
                        while (row[lx - 1] == magma) stack.add(--lx to dy)
                        while (row[rx + 1] == magma) stack.add(++rx to dy)
                    } else if (row[rx] == magma) {
                        lx = rx + 1
                        while (row[rx + 1] == magma) stack.add(++rx to dy)
                        while (row[lx - 1] == magma) stack.add(--lx to dy)
                    } else break
                    dy++
                }
            }
            lx = stack.minOf { it.first }
            rx = stack.maxOf { it.first }
            ty = stack.minOf { it.second }
            dy = stack.maxOf { it.second }
            val w = rx - lx + 1
            val h = dy - ty + 1
//            val l = [lx to ty, lx to dy].all { it in stack }
//            val r = [rx to ty, rx to dy].all { it in stack }
//            val t = [lx to ty, rx to ty].all { it in stack }
//            val d = [lx to dy, rx to dy].all { it in stack }
            count++
            val c = (count % 10).digitToChar()
            stack.forEach { [x, y] -> area[y][x] = c }
            val sc = stack.toMutableList().toList()
            if (stack.size == w * h) {
                spells += minOf(w, h)
            } else if (isRectangle) {
                val rowsWithHoles = (ty..dy).filter { y -> stack.count { it.second == y } != w }
                val colsWithHoles = (lx..rx).filter { x -> stack.count { it.first == x } != h }
                val emptyHeight = rowsWithHoles.max() - rowsWithHoles.min() + 1
                val emptyWidth = colsWithHoles.max() - colsWithHoles.min() + 1
                val above = rowsWithHoles.min() - ty
                val left = colsWithHoles.min() - lx
                val right = rx - colsWithHoles.max()
                val below = dy - rowsWithHoles.max()
                val horizontal = above + below + minOf(emptyHeight, left) + minOf(emptyHeight, right)
                val vertical = left + right + minOf(emptyWidth, above) + minOf(emptyWidth, below)
                spells += minOf(horizontal, vertical)
            } else {
                do {
                    toggleLargestRow(stack)
                    spells++
                } while (stack.isNotEmpty())
            }
//            if (l && r && t && d) {
//                if (stack.size == w * h) {
//                    spells += minOf(w, h)
//                } else {
//                    do {
//                        spells++
//                        toggleLargestRow(stack)
//                    } while (stack.isNotEmpty())
//                }
//            } else if (l && t) {
//                if (w < h) {
//                    while (lx to dy in stack) {
//                        stack.removeIf { it.first == lx }
//                        lx++
//                        spells++
//                    }
//                    dy = stack.maxOf { it.second }
//                } else {
//                    while (rx to ty in stack) {
//                        stack.removeIf { it.second == ty }
//                        ty++
//                        spells++
//                    }
//                    rx = stack.maxOf { it.first }
//                }
//                spells += minOf(rx - lx + 1, dy - ty + 1)
//            } else if (l && d) {
//                if (w < h) {
//                    while (lx to ty in stack) {
//                        stack.removeIf { it.first == lx }
//                        lx++
//                        spells++
//                    }
//                    ty = stack.minOf { it.second }
//                } else {
//                    while (rx to dy in stack) {
//                        stack.removeIf { it.second == dy }
//                        dy--
//                        spells++
//                    }
//                    rx = stack.maxOf { it.first }
//                }
//                spells += minOf(rx - lx + 1, dy - ty + 1)
//            } else if (r && t) {
//                if (w < h) {
//                    while (rx to dy in stack) {
//                        stack.removeIf { it.first == rx }
//                        rx--
//                        spells++
//                    }
//                    dy = stack.maxOf { it.second }
//                } else {
//                    while (lx to ty in stack) {
//                        stack.removeIf { it.second == ty }
//                        ty++
//                        spells++
//                    }
//                    lx = stack.minOf { it.first }
//                }
//                spells += minOf(rx - lx + 1, dy - ty + 1)
//            } else if (r && d) {
//                if (w < h) {
//                    while (rx to ty in stack) {
//                        stack.removeIf { it.first == rx }
//                        rx--
//                        spells++
//                    }
//                    ty = stack.minOf { it.second }
//                } else {
//                    while (lx to dy in stack) {
//                        stack.removeIf { it.second == dy }
//                        dy--
//                        spells++
//                    }
//                    lx = stack.minOf { it.first }
//                }
//                spells += minOf(rx - lx + 1, dy - ty + 1)
//            }
//            val a = CharArea(w, h, '.')
//            sc.forEach { [x, y] -> a[x - lx, y - ty] = 'd' }
//            val flips = spells - ss
//            println(flips)
//            a.png(Tiles.BASE)
        }
    }

    private fun isRectangle(
        area: List<CharArray>,
        lx: Int,
        rx: Int,
        ty: Int
    ): Int? {
        var dy = ty + 1
        while (true) {
            val l = area[dy][lx] == magma
            val r = area[dy][rx] == magma
            if (l && area[dy][lx - 1] != magma && r && area[dy][rx + 1] != magma) {
                dy++
            } else if (!l && !r) {
                return dy - 1
            } else {
                return null
            }
        }
    }

    private fun toggleLargestRow(stack: MutableList<Pair<Int, Int>>) {
        val lx = stack.minOf { it.first }
        val rx = stack.maxOf { it.first }
        val ty = stack.minOf { it.second }
        val dy = stack.maxOf { it.second }
        var largestRow = listOf<Pair<Int, Int>>()
        var largestColumn = listOf<Pair<Int, Int>>()
        var prev = 'r'
        val rd = (ty..dy).minOf { y -> stack.count { it.second == y } }
        val cd = (lx..rx).minOf { x -> stack.count { it.first == x } }
        if (rx - lx == dy - ty && lx to ty in stack && rx to ty in stack && lx to dy in stack && rx to dy in stack) {
            if (rd > cd) prev = 'c'
        }

        for (y in ty..dy) {
            if (lx to y in stack) {
                var n = 1
                while (lx + n to y in stack) n++
                n--
                if (n >= largestRow.size) largestRow = (lx..lx + n).map { it to y }
                if (n == rx - lx + 1) break
            } else if (rx to y in stack) {
                var n = 1
                while (rx - n to y in stack) n++
                n--
                if (n >= largestRow.size) largestRow = (rx - n..rx).map { it to y }
            }
        }
        for (x in lx..rx) {
            if (x to ty in stack) {
                var n = 1
                while (x to ty + n in stack) n++
                n--
                if (n >= largestColumn.size) largestColumn = (ty..ty + n).map { x to it }
                if (n == dy - ty + 1) break
            } else if (x to dy in stack) {
                var n = 1
                while (x to dy - n in stack) n++
                n--
                if (n >= largestColumn.size) largestColumn = (dy - n..dy).map { x to it }
            }
        }
        val rs = largestRow.size
        val cs = largestColumn.size
        val largest = if (rs == cs) {
            if (prev == 'r') largestRow else largestColumn
        } else if (rs > cs) largestRow else largestColumn
        if (largest.any { it !in stack }) error("largest $largest is not in stack $stack")
        stack.removeAll(largest)
    }
}

val Quest12Test by testSuite {
    val quest = "12"

    with(Quest12) {
        test("one") {
            val sample = """
                ........#.
                .##.......
                .##..####.
                .##..####.
                .....####.
                .###......
            """.trimIndent().lines()
            one(sample) shouldBe 7
            three(sample).first shouldBe 7

            val input = lines(quest, 1)
            one(input) shouldBe 233
            three(input).first shouldBe 233
        }

        test("two") {
            val sample1 = """
                ........#.
                .##.......
                .##..####.
                .#...#....
                .....#....
                .####.....
            """.trimIndent().lines()
            two(sample1).first shouldBe 6
            three(sample1).first shouldBe 6

            val sample2 = """
                ...............................
                .####....#####..#######........
                .####....#####..#######........
                .####....#####..#######........
                .........#####.......##........
                .........########....##........
                .........########....##........
                ...............................
                .######....#.....#.....###.....
                ...........#...........###.....
                ...........#......########.....
                ...........#......########.....
                ...........#......########.....
                ..................########.....
                ...............................
            """.trimIndent().lines()
            two(sample2).first shouldBe 23
            three(sample2).first shouldBe 23

            val input = lines(quest, 2)
            val r2 = two(input) //shouldBe 1973
            val r3 = three(input) //shouldBe 1973
            r2.first shouldBe 1973
            r2.first shouldBe r3.first
            r2.second shouldBe r3.second
        }

        test("three") {
            val sample6 = """
                .#######################.
                .#######################.
                .#######################.
                .#######################.
                .#######################.
                .#######################.
                .#######################.
                .#######################.
                .#######################.
                .######..###############.
                .######..###############.
                .######..###############.
                .######..###############.
                .######..###############.
                .######..###############.
                .######..###############.
                .######..###############.
                .######..###############.
                .######..###############.
                .#######################.
                .#######################.
            """.trimIndent().lines()
//            three(sample6).first shouldBe 25

            val sample1 = """
                ........#.
                .##.......
                .##..####.
                .#...#..#.
                .....####.
                .###......
            """.trimIndent().lines()
//            three(sample1).first shouldBe 8

            val sample2 = """
                ..............................
                .##########....##.....#######.
                .##########.##.##..#..#######.
                .####..####.##.##.....#.#####.
                .##########.##.#####..#.#####.
                .##########.##........#######.
                ..............................
                .##########..################.
                .##########..#..............#.
                .#######..#..#..............#.
                .#######..#..################.
                .##########..################.
                ..............................
            """.trimIndent().lines()
//            val r = three(sample2)
//            three(sample2).first shouldBe 29

            fun rotateRight(lines: List<String>): List<String> {
                return buildList {
                    for (x in lines[0].indices) {
                        add(lines.map { it[x] }.reversed().joinToString(""))
                    }
                }
            }

            val sample3 = """
                ..............................
                .############################.
                .##...................###.###.
                .##..################.###.###.
                .##..######.........#.###.###.
                .##..################.###.###.
                .##...................###.###.
                .##..####################.###.
                .##..####################.###.
                .##.......................###.
                .############################.
                ..............................
            """.trimIndent().lines()
//            CharArea(sample3).png()
//            val s30 = three(sample3)
//            s30.first shouldBe 16
//            val s31 = rotateRight(sample3)
//            CharArea(s31).png()
//            val r31 = three(s31)
//            r31.first shouldBe 16
//            val s32 = rotateRight(s31)
//            three(s32).first shouldBe 16
//            val s33 = rotateRight(s32)
//            three(s33).first shouldBe 16

            val sample4 = """
                .######.
                .######.
                .######.
                .##..##.
                .######.
                .######.
            """.trimIndent().lines()
            three(sample4).first shouldBe 7

            val sample5 = """
                .#######.
                .##.####.
                .##.####.
                .##.####.
                .##.####.
                .#######.
            """.trimIndent().lines()
            three(sample5).first shouldBe 8


            val input = lines(quest, 3)
            val r3 = three(input, false)
            println("max=" + r3.second.max())
            r3.second.drop(2) shouldNotContain 0
            r3.first shouldBe 2793
        }
    }
}
