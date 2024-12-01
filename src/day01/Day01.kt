package day01

import println
import readLines
import kotlin.math.abs

val testInput = """
    3   4
    4   3
    2   5
    1   3
    3   9
    3   3""".trimIndent().trim().lines()

fun main() {
    val testInput = parseInput(testInput)

    // Test if implementation meets criteria from the description, like:
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Or read a large test input from the `src/Day01_test.txt` file:
//    val testInput = readInput("inputs/01_test")
//    check(part1(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = parseInput(readLines("inputs/01"))
    part1(input).println()
    part2(input).println()
}

fun parseInput(input: List<String>): List<Pair<Int, Int>> {
    val regex = Regex("\\s+") // Split on whitespace characters
    return input.map { line ->
        line.split(regex).map { it.trim().toInt() }
    }.map { l ->
        l.first() to l.last()
    }
}

fun part1(input: List<Pair<Int, Int>>): Int {
    val (left, right) = input.unzip()
    return left.sorted().zip(right.sorted()).sumOf { abs(it.first - it.second) }
}

fun part2(input: List<Pair<Int, Int>>): Int {
    val (left, right) = input.unzip()
    val similarityMap = right.groupBy { it }.map { (key, value) -> key to value.size * key }.toMap().withDefault { 0 }
    return left.sumOf { similarityMap.getValue(it) }
}
