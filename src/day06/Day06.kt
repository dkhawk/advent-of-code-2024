package day06

import Heading
import Bounds
import Vector
import minMax
import printGrid
import println
import readLines
import utils.BOLD
import utils.COLORS
import utils.INVERT
import java.util.*

val testInput = """
    ....#.....
    .........#
    ..........
    ..#.......
    .......#..
    ..........
    .#..^.....
    ........#.
    #.........
    ......#...""".trimIndent()

fun main() {
    val input = readLines("inputs/06")

//    check(part1(testInput.lines()) == 41)
//    part1(input).println()

//    check(part2(testInput.lines()) == 6)
    part2(input).println()

    "${COLORS.RED}Reminder: 785 is too low!${COLORS.NONE}".println()
}

fun buildGrid(input: List<String>): Map<Vector, Char> {
    return buildMap {
        input.mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c != '.') put(Vector(x, y), c)
            }
        }.flatten()
    }
}

fun part1(input: List<String>): Int {
    val grid = buildGrid(input).apply {
        // printGrid()
    }.withDefault { '.' }

    val bounds = grid.keys.minMax()

    var guard = grid.entries.first { (k, v) -> v == '^' }.key
    var heading = Heading.NORTH

    val visited = mutableSetOf<Vector>()

    while (bounds.contains(guard)) {
        visited.add(guard)
        val next = guard.advance(heading)
        if (!bounds.contains(next)) {
            break
        }
        if (grid.getValue(next) == '#') {
            heading = heading.turnRight()
        } else {
            guard = next
        }
    }

//    val result = grid.toMutableMap()
//    visited.forEach {
//        result[it] = 'X'
//    }
//
//    result.printGrid()

    return visited.size
}

typealias Straight = HashSet<Vector>

fun part2(input: List<String>): Int {
    val grid = buildGrid(input).apply {
        // printGrid()
    }.withDefault { '.' }

//    val expectedObstacles = getExpectedObstacles(testData)

    val bounds = grid.keys.minMax()

    var guard = grid.entries.first { (k, v) -> v == '^' }.key
    val start = guard
    var heading = Heading.NORTH

    val visited = mutableMapOf<Vector, MutableSet<Heading>>()
    visited.record(guard, heading)

    val obstacles = mutableSetOf<Vector>()

    val recentGuards = LinkedList<Vector>()
    recentGuards.add(guard)

    var first = true

    while (true) {
        val next = guard.advance(heading)

        // If we are about to run off the map, break
        if (!bounds.contains(next)) {
            if (!first) {
                break
            }

            println()
            "resetting".println()
            println()

            first = false
            guard = start
            heading = Heading.NORTH

            continue
        }

        // If we are about to hit an obstacle, extend the tail to the next obstacle or edge
        if (grid.getValue(next) == '#') {
            extendTail2(bounds, guard, visited, heading, grid)
            heading = heading.turnRight()
            continue
        } else {
            // If we cross a previously visited location with a recorded location that is a right turn away, then we have
            // found a loop candidate!
            if (visited[guard]?.contains(heading.turnRight()) == true) {
                if (grid.getValue(next) != '#' && bounds.contains(next)) {
                    obstacles.add(next)
                }
            }
        }

        recentGuards.add(guard)
        while (recentGuards.size > 10) {
            recentGuards.removeFirst()
        }
        guard = next
        visited.record(guard, heading)

        if (guard == Vector(34, 115) && heading == Heading.WEST) {
            println()
            showState(grid, guard, heading, obstacles, visited) { location, value ->
                when (location) {
                    guard -> {
                        "\u001B[7;31;1m@${COLORS.NONE}"
                    }
                    in recentGuards -> {
                        "\u001B[7;35m$value${COLORS.NONE}"
                    }
                    Vector(34, 115) -> {
                        "$INVERT$value${COLORS.NONE}"
                    }
                    else -> {
                        value
                    }
                }
            }
            println()
        }
    }

    showState(grid, guard, heading, obstacles, visited) { location, value ->
        if (location == Vector(34, 115)) {
            "${COLORS.MAGENTA}$value${COLORS.NONE}"
        } else {
            value
        }
    }

//
//    "Found obstacles: $obstacles".println()
//
//    "Missing obstacles: ".println()
//    (expectedObstacles - obstacles).println()
//
//    "Extra obstacles: ".println()
//    (obstacles - expectedObstacles).println()
//
//    val missing = expectedObstacles - obstacles
//
//    println()
//    showState(grid, guard, heading, obstacles, visited) { location, s ->
//        if (missing.contains(location)) {
//            "${COLORS.YELLOW}X${COLORS.NONE}"
//        } else {
//            s
//        }
//    }

    return obstacles.size
}

fun extendTail2(
    bounds: Bounds,
    guard: Vector,
    visited: MutableMap<Vector, MutableSet<Heading>>,
    heading: Heading,
    grid: Map<Vector, Char>
) {
    val oppositeHeading = heading.opposite()
    var tail = guard.advance(oppositeHeading)

    while (bounds.contains(tail) && grid[tail] != '#') {
        // Note: this is heading!
        visited.record(tail, heading)
        tail = tail.advance(oppositeHeading)
    }
}

private fun MutableMap<Vector, MutableSet<Heading>>.record(guard: Vector, heading: Heading) {
    getOrPut(guard) { mutableSetOf() }.add(heading)
}

fun getExpectedObstacles(testData: String): Set<Vector> {
    return testData.split("\n\n").map { testGrid ->
        buildGrid(testGrid.lines()).entries.first { (k, v) -> v == 'O' }.key
    }.also { it.println() }.toSet()
}

private fun extendTail(
    bounds: Bounds,
    tail: Vector,
    keyedRecents: MutableMap<Heading, Straight>,
    heading: Heading,
    oppositeHeading: Heading,
    grid: Map<Vector, Char>
): Vector {
    var tail1 = tail
    while (bounds.contains(tail1)) {
        keyedRecents[heading]!!.add(tail1)
//                recent.last.addLast(tail)
        tail1 = tail1.advance(oppositeHeading)
        if (grid.getValue(tail1) == '#') {
            break
        }
    }
    return tail1
}

private fun showState(
    grid: Map<Vector, Char>,
    guard: Vector,
    heading: Heading,
    obstacles: Set<Vector> = emptySet(),
    visited: Map<Vector, Set<Heading>>,
    formatter: (Vector, String) -> String = { _, c -> c }
) {
    val result = grid.toMutableMap()

    visited.entries.forEach { (k, v) ->
        result[k] = if (v.size == 1) {
            v.first().toSymbol()
        } else {
            '0' + v.size
        }
    }

    obstacles.forEach { v -> result[v] = 'O' }

    result[guard] = heading.toSymbol()
    result[guard] = '$'

    result.printGrid(default = '.') { location, symbol ->
        formatter(
            location,
            if (location == guard) {
                "${COLORS.RED}$symbol${COLORS.NONE}"
            } else if (symbol == 'O') {
                "${COLORS.LT_GREEN}$symbol${COLORS.NONE}"
            } else {
                "$symbol"
            }
        )
    }
}

private fun Heading.toSymbol(): Char {
    return when (this) {
        Heading.NORTH -> '^'
        Heading.EAST -> '>'
        Heading.SOUTH -> 'v'
        Heading.WEST -> '<'
    }
}

val testData = """
    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ....|..#|.
    ....|...|.
    .#.O^---+.
    ........#.
    #.........
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    ......O.#.
    #.........
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    .+----+O#.
    #+----+...
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    ..|...|.#.
    #O+---+...
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    ....|.|.#.
    #..O+-+...
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    .+----++#.
    #+----++..
    ......#O..
""".trimIndent()