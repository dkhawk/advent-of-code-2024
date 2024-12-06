package day04

import Vector
import allHeadings8
import mapToLocations
import plus
import println
import readText
import times

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

fun part1(input: List<String>): Int {
    return mapToLocations(input).toMap().withDefault { '.' }.let { grid ->
        grid.entries.filter { it.value == 'X' }.sumOf { (location, _) ->
            allHeadings8().count { heading ->
                findXmas(grid, location, heading)
            }
        }
    }
}

fun part2(input: List<String>): Int {
    return mapToLocations(input).toMap().withDefault { '.' }.let { grid ->
        grid.entries.filter { it.value == 'A' }.sumOf { (location, _) ->
            findCrossmas(grid, location)
        }
    }
}

fun findXmas(grid: Map<Vector, Char>, location: Vector, heading: Vector): Boolean {
    return (grid[location] == 'X') &&
        (grid[location + heading] == 'M') &&
        (grid[location + (heading * 2)] == 'A') &&
        (grid[location + (heading * 3)] == 'S')
}

fun findCrossmas(grid: Map<Vector, Char>, location: Vector): Int {
    val a = if (grid[location + Heading8.NORTHWEST.vector] == 'M'
        && grid[location + Heading8.SOUTHEAST.vector] == 'S') 1 else 0

    val b = if (grid[location + Heading8.NORTHWEST.vector] == 'S'
        && grid[location + Heading8.SOUTHEAST.vector] == 'M') 1 else 0

    val c = if (grid[location + Heading8.NORTHEAST.vector] == 'M'
        && grid[location + Heading8.SOUTHWEST.vector] == 'S') 1 else 0

    val d = if (grid[location + Heading8.NORTHEAST.vector] == 'S'
        && grid[location + Heading8.SOUTHWEST.vector] == 'M') 1 else 0

    // Logically only one of a or b (and c or d) can be true (1)
    // this is essentially (a || b) && (c || d)
    return (a + b) * (c + d)
}
