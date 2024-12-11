package day11

import kotlinx.coroutines.runBlocking
import utils.*

val testInput = """
    125 17
""".trimIndent()

fun main() = runBlocking {
//    check(part1(testInput) == 55312)

    val input = readText("inputs/11")
//    part1(input).println()
    part2(input).println()
}

private fun part1(input: String): Int {
    val rocks = input.split(" ").map { it.toLong() }

    val sequences = sequence<List<Long>> {
        var current = rocks

        while (true) {
            current = buildList {
                current.forEach { rock ->
                    if (rock == 0L) {
                        add(1L)
                    } else {
                        val rs = rock.toString()
                        if (rs.length.isEven()) {
                            val mid = rs.length shr 1
                            add(rs.substring(0, mid).toLong())
                            add(rs.substring(mid).toLong())
                        } else {
                            add(rock * 2024)
                        }
                    }
                }
            }
            yield(current)
        }
    }
    return sequences.take(25).last().count()
}

private fun part2(input: String): Int {
    val rocks = input.split(" ").map { it.toLong() }

    val sequences = sequence<List<Long>> {
        var current = rocks

        while (true) {
            current = buildList {
                current.forEach { rock ->
                    if (rock == 0L) {
                        add(1L)
                    } else {
                        val rs = rock.toString()
                        if (rs.length.isEven()) {
                            val mid = rs.length shr 1
                            add(rs.substring(0, mid).toLong())
                            add(rs.substring(mid).toLong())
                        } else {
                            add(rock * 2024)
                        }
                    }
                }
            }
            yield(current)
        }
    }
    return sequences.take(75).last().count()
}

private fun Int.isEven(): Boolean {
    return this.mod(2) == 0
}
