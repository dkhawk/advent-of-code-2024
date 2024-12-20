package day20

import utils.*
import utils.Vector
import kotlin.collections.ArrayDeque
import kotlin.math.abs

val testInput = """
    ###############
    #...#...#.....#
    #.#.#.#.#.###.#
    #S#...#.#.#...#
    #######.#.#.###
    #######.#.#...#
    #######.#.###.#
    ###..E#...#...#
    ###.#######.###
    #...###...#...#
    #.#####.#.###.#
    #.#...#.#.#...#
    #.#.#.#.#.#.###
    #...#...#...###
    ###############
""".trimIndent().lines()

fun main() {
    check(part1(testInput, 20) == 5)
    check(part2(testInput, 70) == (12 + 22 + 4 + 3))

    val input = readLines("inputs/20")
    part1(input, 100).println()
    part2(input, 100).println()  // 823
}

private fun part1(input: List<String>, target: Int): Int {
    val grid = buildGrid(input)

    val start = grid.entries.first { it.value == 'S' }.key
    val end = grid.entries.first { it.value == 'E' }.key

    val costMap = solve(grid, start, end)

    val cheats = findCheats(costMap, grid)

    val usefulCheats = cheats.filter { it.second > 0 }

    val hist = usefulCheats.groupBy { it.second }.map { (cheat, cheats) -> cheat to cheats.size }

//    hist.joinToString("\n").println()

    val answer = hist.fold(0) { acc, cheat ->
        if (cheat.first >= target) acc + cheat.second else acc
    }

    answer.println()

    return answer
}

fun findCheats(costMap: Map<Vector, Int>, grid: Map<Vector, Char>): List<Pair<Vector, Int>> {
    return costMap.entries.flatMap { (location, cost) ->
        Heading.entries.map { heading ->
            val other = location + (heading.vector * 2)
            val costOfOther = costMap[other]
            location.advance(heading) to if (costOfOther != null) {
                (cost - costOfOther) - 2  // ??
            } else {
                Int.MIN_VALUE
            }
        }
    }
}

private fun solve(grid: Map<Vector, Char>, start: Vector, end: Vector): MutableMap<Vector, Int> {
    // Solve the maze once to get the cost map

    val visited = mutableMapOf<Vector, Int>()
    visited[start] = 0

    val queue = ArrayDeque<Vector>()
    queue.add(start)

    // I suspect the queue will never grow beyond size 1 for this maze!!
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        val nextCost = visited[current]!! + 1

        for (heading in Heading.entries) {
            val next = current.advance(heading)

            if (next == end) {
                visited[next] = nextCost
                // the queue should already be empty for this kind of maze
                require(queue.isEmpty())
            }

            if (grid[next] != '#') {
                if (nextCost < visited.getOrElse(next, { Int.MAX_VALUE })) {
                    visited[next] = nextCost
                    queue.add(next)
                }
            }
        }
    }

    return visited
}

private fun part2(input: List<String>, target: Int): Int {
    val grid = buildGrid(input)

    val start = grid.entries.first { it.value == 'S' }.key
    val end = grid.entries.first { it.value == 'E' }.key

    val cm = solve(grid, start, end)

    val costToFinish = cm[end]!!

    val costMap = cm.map { it.key to (costToFinish - it.value) }.toMap().withDefault { Int.MAX_VALUE }

    val cheats = findCheats2(costMap)

    return cheats.count { it >= target }.also { it.println() }
}

fun findCheats2(costMap: Map<Vector, Int>): List<Int> {
    return costMap.entries.flatMap { (location, cost) ->
        allCheats(location, costMap).map { it.second }
    }
}

fun allCheats(location: Vector, costMap: Map<Vector, Int>): List<Pair<Vector, Int>> {
    return ((location - Vector(20, 20))..(location + Vector(20, 20))).asSequence().flatten().filter {
        // The candidate must be on the calculated path
        costMap.containsKey(it)
    }.filter {
        // The candidate must be within cheating range
        location.manhattanDistanceTo(it) <= 20 // Ignore locations that are too far to consider
    }.filter {
        // The candidate much be cheaper than the origin
        costMap.getValue(it) < costMap.getValue(location)  // Ignore locations that are not on the original path or have a higher cost
    }.map { candidate ->
        // Calculate the savings
        val originCost = costMap.getValue(location) - costMap.getValue(candidate)
        val newCost = location.manhattanDistanceTo(candidate)
        candidate to originCost - newCost
    }.toList()
}

private fun Vector.manhattanDistanceTo(other: Vector): Int {
    return abs(x - other.x) + abs(y - other.y)
}

