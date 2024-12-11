package day11

import kotlinx.coroutines.runBlocking
import utils.*
import java.math.BigInteger
import kotlin.time.measureTime

val testInput = """
    125 17
""".trimIndent()

fun main() = runBlocking {
    check(part1(testInput) == 55312)
    check(part2(testInput, 6) == BigInteger.valueOf(22L))
    check(part2(testInput, 25) == BigInteger.valueOf(55312L))

    val input = readText("inputs/11")
    part1(input).println()
    measureTime {
        part2(input, 75).println()
    }.println()
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
    val last = sequences.take(25).last()

    return last.count()
}

private fun part2(input: String, generations: Int): BigInteger {
    val rocks = input.split(" ").map { it.toLong() }
    var rockPile =
        rocks.groupingBy { it }.eachCount().map { BigInteger.valueOf(it.key) to BigInteger.valueOf(it.value.toLong()) }
            .toMap()

    repeat(generations) {
        val entries = rockPile.entries

        val newRocks = buildList {
            entries.forEach { (rock, rockCount) ->
                if (rock == BigInteger.ZERO) {
                    add(BigInteger.ONE to rockCount)
                } else {
                    val rs = rock.toString()
                    if (rs.length.isEven()) {
                        val mid = rs.length shr 1
                        val k1 = BigInteger.valueOf(rs.substring(0, mid).toLong())
                        val k2 = BigInteger.valueOf(rs.substring(mid).toLong())

                        add(k1 to rockCount)
                        add(k2 to rockCount)
                    } else {
                        val k = rock * BigInteger.valueOf(2024L)
                        add(k to rockCount)
                    }
                }
            }
        }

        val newRockPile = mutableMapOf<BigInteger, BigInteger>()
        newRocks.forEach { (rock, rockCount) ->
            newRockPile[rock] = newRockPile.getOrDefault(rock, BigInteger.ZERO) + rockCount
        }

        rockPile = newRockPile.toMap()
    }

    return rockPile.values.fold(BigInteger.ZERO) { a, b -> a + b }
}

private fun Int.isEven(): Boolean {
    return this.mod(2) == 0
}
