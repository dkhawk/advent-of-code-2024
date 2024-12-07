package day06

import utils.minMax
import utils.println
import utils.COLORS
import java.util.*
import kotlinx.coroutines.*

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

fun main() = runBlocking {
    val input = _root_ide_package_.utils.readLines("inputs/06")

    check(part1(testInput.lines()) == 41)
    part1(input).println()

    launch(Dispatchers.Default) {
        check(part2(testInput.lines(), this) == 6)
        part2(input, this).println()
    }

    "${COLORS.RED}Reminder: 785 is too low!${COLORS.NONE}".println()
    "${COLORS.RED}Reminder: 1502 is too high!${COLORS.NONE}".println()
}

fun buildGrid(input: List<String>): Map<_root_ide_package_.utils.Vector, Char> {
    return buildMap {
        input.mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c != '.') put(_root_ide_package_.utils.Vector(x, y), c)
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
    var heading = _root_ide_package_.utils.Heading.NORTH

    val visited = mutableSetOf<_root_ide_package_.utils.Vector>()

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

    return visited.size
}

data class CandidateObject(
    val location: _root_ide_package_.utils.Vector,
    val guard: _root_ide_package_.utils.Vector,
    val heading: _root_ide_package_.utils.Heading,
)

suspend fun part2(input: List<String>, coroutineScope: CoroutineScope): Int {
    val grid = buildGrid(input).apply {
        // printGrid()
    }.withDefault { '.' }

    val bounds = grid.keys.minMax()

    var guard = grid.entries.first { (k, v) -> v == '^' }.key
    val start = guard
    var heading = _root_ide_package_.utils.Heading.NORTH

    val visited = mutableSetOf<_root_ide_package_.utils.Vector>()

//    val expectedObstacles = getExpectedObstacles(testData)
//    expectedObstacles.println()

//    expectedObstacles.forEach { expected ->
//        Heading.entries.forEach { heading ->
//
//            val guard = expected.advance(heading.turnLeft())
//
//            checkCandidate(
//                grid,
//                bounds,
//                CandidateObject(
//                    guard = guard,
//                    heading = Heading.NORTH,
//                    location = expected
//                )
//            ).println()
//        }
//    }

    // These are all candidate locations where we _could_ put an obstacle
    val candidates = mutableSetOf<CandidateObject>()

    while (bounds.contains(guard)) {
        visited.add(guard)
        val next = guard.advance(heading)
        if (!bounds.contains(next)) {
            break
        }
        if (grid.getValue(next) == '#') {
            heading = heading.turnRight()
        } else {
            candidates.add(
                CandidateObject(
                    location = next,
                    guard = start,
                    heading = _root_ide_package_.utils.Heading.NORTH,
                )
            )
            guard = next
        }
    }

    val deferreds = candidates.map {
        coroutineScope.async {
            checkCandidate(grid, bounds, it)
        }
    }

    val actual = deferreds.awaitAll().filterNotNull().toSet()

//    "Actual: $actual".println()
//    "Missing: ${expectedObstacles - actual}".println()
//    "Extra: ${actual - expectedObstacles}".println()

    val result = grid.toMutableMap()
    visited.forEach {
        result[it] = 'X'
    }

//    result.printGrid()
//
//    grid.printGrid { location, c ->
//        if (location in actual) {
//            "${COLORS.GREEN}O${COLORS.NONE}"
//        } else {
//            "$c"
//        }
//    }

    return actual.size
}

fun checkCandidate(grid: Map<_root_ide_package_.utils.Vector, Char>, bounds: _root_ide_package_.utils.Bounds, candidate: CandidateObject) : _root_ide_package_.utils.Vector? {
    var heading = candidate.heading
    var guard = candidate.guard

    val visited = mutableSetOf<Pair<_root_ide_package_.utils.Vector, _root_ide_package_.utils.Heading>>()
    visited.add(guard to heading)

    var result: _root_ide_package_.utils.Vector? = null

    while (true) {
        val next = guard.advance(heading)

        if (!bounds.contains(next)) {
            result = null
            break
        }

        if (grid.getValue(next) == '#' || next == candidate.location) {
            heading = heading.turnRight()
        } else {
            guard = next
            if (visited.contains(guard to heading)) {
                return candidate.location
            }
            visited.add(guard to heading)
        }
    }

//    grid.printGrid { vector, c ->
//        if (candidate.location == vector) {
//            "${COLORS.GREEN}O${COLORS.NONE}"
//        } else if (visited.contains(vector)) {
//            "${COLORS.RED}+${COLORS.NONE}"
//        } else {
//            c.toString()
//        }
//    }

    return result
}

fun getExpectedObstacles(testData: String): Set<_root_ide_package_.utils.Vector> {
    return testData.split("\n\n").map { testGrid ->
        buildGrid(testGrid.lines()).entries.first { (k, v) -> v == 'O' }.key
    }.also { it.println() }.toSet()
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