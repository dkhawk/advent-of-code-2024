package day02

import println
import readLines
import kotlin.math.absoluteValue
import kotlin.math.sign

val testInput = """
    7 6 4 2 1
    1 2 7 8 9
    9 7 6 2 1
    1 3 2 4 5
    8 6 4 4 1
    1 3 6 7 9""".trimIndent().trim().lines()

fun main() {
    val testInput = parseInput(testInput)

    // Test if implementation meets criteria from the description, like:
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

//    val testInput = readInput("inputs/02_test")
//    check(part1(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = parseInput(readLines("inputs/02"))
    part1(input).println()
    part2(input).println()
}

fun parseInput(input: List<String>): List<List<Int>> {
    return input.map { line ->
        line.split("""\s+""".toRegex()).map { it.trim().toInt() }
    }
}

fun part1(input: List<List<Int>>): Int {
    return input.count(::isReportSafe)
}

fun part2(input: List<List<Int>>): Int {
    return input.count(::isAnyReportSafe)
}

fun isReportSafe(report: Collection<Int>) = isReportSafe(report.asSequence())

fun isReportSafe(report: Sequence<Int>): Boolean {
    val deltas = report.zipWithNext { a, b -> b - a }

    var direction: Int? = null

    return deltas.all { delta ->
        val sign = delta.sign
        val value = delta.absoluteValue

        val directionOkay = if (direction == null) {
            direction = sign
            // Short circuit a bad initial direction
            if (direction == 0) return@all false
            true
        } else {
            direction == sign
        }

        directionOkay && (value in 1..3)
    }
}

fun isAnyReportSafe(report: List<Int>): Boolean {
    if (isReportSafe(report)) return true

    // Creates a sequence of sequences of Ints where each nested sequence drops a single value from the report
    // If _any_ succeed then we can stop checking
    return sequence {
        // Iterate over the indices of the report
        report.indices.map { indexUnderTest ->
            // Emit another sequence with the indexUnderTest item skipped
            yield(
                sequence {
                    // Skip the index under test value
                    report.indices.forEach { index ->
                        if (index != indexUnderTest) this.yield(report[index])
                    }
                }
            )
        }
    }.any(::isReportSafe)
}
