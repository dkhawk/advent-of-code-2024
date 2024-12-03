package day03b

import println
import readText

data class State(val sum: Int = 0, val enabled: Boolean = true)

sealed interface Instruction {
    fun apply(state: State): State
}

data object Do: Instruction {
    override fun apply(state: State) = state.copy(enabled = true)
}

data object Dont: Instruction {
    override fun apply(state: State) = state.copy(enabled = false)
}

data class Mul(val a: Int, val b: Int) : Instruction {
    override fun apply(state: State): State {
        return if (state.enabled) state.copy(sum = state.sum + (a * b)) else state
    }
}

const val testInput1 = """xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"""
const val testInput2 = """xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"""

val regexImproved = """(?<mult>mul\((?<first>[0-9]{1,3}),(?<second>[0-9]{1,3})\))|(?<do>do\(\))|(?<donot>don't\(\))""".toRegex()

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(parseInput(testInput1)) == 161)
    check(part2(parseInput(testInput2)) == 48)

    // Read the input from the `src/Day01.txt` file.
    val input = parseInput(readText("inputs/03"))

    part1(input).println()
    part2(input).println()
}

fun part1(instructions: Sequence<Instruction>): Int {
    return instructions.filter { it is Mul }.fold(State()) { state, instruction -> instruction.apply(state) }.sum
}

fun part2(instructions: Sequence<Instruction>): Int {
    return instructions.fold(State()) { state, instruction -> instruction.apply(state) }.sum
}

fun parseInput(input: String): Sequence<Instruction> {
    return regexImproved.findAll(input).map { matchResult -> matchResult.toInstruction() }
}

private fun MatchResult.toInstruction() : Instruction {
    return when {
        groups.contains("mult") -> {
            ifNotNull(get<Int>("first"), get<Int>("second"), ::Mul) ?: error("Invalid mul instruction: ${this.value}")
        }
        groups.contains("do") -> Do
        groups.contains("donot") -> Dont
        else -> error("Invalid instruction: ${this.value}")
    }
}

private fun MatchGroupCollection.contains(key: String) = this[key] != null

private inline fun <reified T> MatchResult.get(key: String): T? {
    val v = groups[key]?.value ?: return null
    return when (T::class) {
        Int::class -> v.toIntOrNull() as T?
        Long::class -> v.toLongOrNull() as T?
        Double::class -> v.toDoubleOrNull() as T?
        String::class -> v as T?
        // Add other types as needed
        else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
    }
}

fun <T, R, W> ifNotNull(a: T?, b: R?, block: (T, R) -> W): W? {
    return if (a != null && b != null) {
        block(a, b)
    } else {
        null
    }
}
