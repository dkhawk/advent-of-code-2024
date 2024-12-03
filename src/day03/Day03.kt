package day03

import println
import readText

val testInput = """xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))""".trimIndent().trim()
val test2     = """xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"""

val regex = """mul\(([0-9]{1,3}),([0-9]{1,3})\)|do\(\)|don't\(\)""".toRegex()

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(testInput) == 161)
    check(part2(test2) == 48)

    // Read the input from the `src/Day01.txt` file.
    val input = parseInput(readText("inputs/03"))

    part1(input).println()
    part2(input).println()
}

fun parseInput(input: String): String {
    return input
}

fun part1(input: String): Int {
    return regex.findAll(input).map { g ->
        if (g.value.contains("mul")) {
            multiply2(g)
        } else 0
    }.sum()
}

data class State(val sum: Int = 0, val enabled: Boolean = true)

fun part2(input: String): Int {
    return regex.findAll(input).fold(State()) { state, g ->
        when (g.value) {
            "don't()" -> {
                state.copy(enabled = false)
            }
            "do()" -> {
                state.copy(enabled = true)
            }
            else -> {
                if (state.enabled) {
                    state.copy(sum = state.sum + multiply2(g))
                } else {
                    state
                }
            }
        }
    }.sum
}

private fun multiply(g: MatchResult): Int {
    val (a, b) = g.value.split(",")
    val c = a.substringAfter("(").toInt()
    val d = b.substringBefore(")").toInt()
    return c * d
}

// Improved multiply function that uses the better regex to get the factors
private fun multiply2(g: MatchResult): Int {
    return g.groupValues.drop(1).fold(1) { product, factor ->
        product * factor.toInt()
    }
}
