package day04

import println
import readText

val input = """
    MMMSXXMASM
    MSAMXMSMSA
    AMXSXMAAMM
    MSAMASMSMX
    XMASAMXAMM
    XXAMMXXAMA
    SMSMSASXSS
    SAXAMASAAA
    MAMMMXMMMM
    MXMXAXMASX
""".trimIndent().lines()

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(input) == 18)
    check(part2(input) == 9)

    // Read the input from the `src/Day01.txt` file.
    val input = readText("inputs/04").lines()

    part1(input).println()
    part2(input).println()
}

data class Vector(val x: Int, val y: Int)

enum class Direction(val vector: Vector) {
    NORTH(Vector(0, -1)),
    SOUTH(Vector(0, 1)),
    EAST(Vector(1, 0)),
    WEST(Vector(-1, 0)),
    NORTHEAST(Vector(1, -1)),
    SOUTHEAST(Vector(1, 1)),
    SOUTHWEST(Vector(-1, 1)),
    NORTHWEST(Vector(-1, -1)),
}

fun part1(input: List<String>): Int {
    val data = mapToLocations(input)

    val grid = data.toMap().withDefault { '.' }

    val letterXs = data.filter { it.second == 'X' }.map { it.first }

    return letterXs.sumOf { location ->
        Direction.entries.toTypedArray().count { direction ->
            findXmas(grid, location, direction.vector)
        }
    }
}

fun findXmas(grid: Map<Vector, Char>, location: Vector, direction: Vector): Boolean {
    return (grid[location] == 'X') &&
        (grid[location + direction] == 'M') &&
        (grid[location + (direction * 2)] == 'A') &&
        (grid[(location + (direction * 3))] == 'S')
}

private operator fun Vector.plus(other: Vector): Vector {
    return Vector(x + other.x, y + other.y)
}

private operator fun Vector.times(scalar: Int): Vector {
    return Vector(x * scalar, y * scalar)
}

fun part2(input: List<String>): Int {
    val data = mapToLocations(input)

    val grid = data.toMap().withDefault { '.' }

    val letterAs = data.filter { it.second == 'A' }.map { it.first }

    return letterAs.sumOf { location ->
        findCrossmas(grid, location)
    }
}

private fun mapToLocations(input: List<String>): List<Pair<Vector, Char>> {
    return input.mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            Vector(x, y) to c
        }
    }.flatten()
}

fun findCrossmas(grid: Map<Vector, Char>, location: Vector): Int {
    val a = if (grid[location + Direction.NORTHWEST.vector] == 'M'
        && grid[location + Direction.SOUTHEAST.vector] == 'S') 1 else 0

    val b = if (grid[location + Direction.NORTHWEST.vector] == 'S'
        && grid[location + Direction.SOUTHEAST.vector] == 'M') 1 else 0

    val c = if (grid[location + Direction.NORTHEAST.vector] == 'M'
        && grid[location + Direction.SOUTHWEST.vector] == 'S') 1 else 0

    val d = if (grid[location + Direction.NORTHEAST.vector] == 'S'
        && grid[location + Direction.SOUTHWEST.vector] == 'M') 1 else 0

    // Logically on one of a or b (and c or d) can be true (1)
    // this is essentially (a || b) && (c || d)
    return (a + b) * (c + d)
}
