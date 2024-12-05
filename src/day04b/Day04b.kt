package day04b

import Vector
import allHeadings
import mapToLocations
import plus
import println
import readText
import kotlin.math.sign

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

fun Collection<Vector>.minMax(): Pair<Vector, Vector> {
    val xMinMax = this.map { it.x }.sorted().let { l -> l.first() to l.last() }
    val yMinMax = this.map { it.y }.sorted().let { l -> l.first() to l.last() }

    return Vector(xMinMax.first, yMinMax.first) to Vector(xMinMax.second, yMinMax.second)
}

private fun Map<Vector, Char>.printGrid() {
    val (minLocation, maxLocation) = this.keys.minMax()

    val gw = this.withDefault { '.' }

    (minLocation..maxLocation).map { row ->
        row.map { gw.getValue(it) }.joinToString("").println()
    }
}

private operator fun Vector.rangeTo(other: Vector): List<List<Vector>> {
    return (this.y..other.y).map { y ->
        (this.x..other.x).map { x ->
            Vector(x, y)
        }
    }
}

fun createKernels(): List<List<Pair<Vector, Char>>> {
    val word = "XMAS"

    val kernels = allHeadings().map { heading ->
        val s = generateSequence(Vector()) { it + heading }.iterator()
        word.map { s.next() to it }
    }

//    kernels.forEach {
//        it.toMap().printGrid()
//        println()
//    }

    return kernels
}

val part1Kernels = createKernels()
fun part1(input: List<String>): Int {

    return mapToLocations(input).toMap().let { grid ->
        grid.entries.filter { it.value == 'X' }.sumOf { (location, _) ->
            part1Kernels.count { kernel ->
                checkKernelAtLocation(grid, location, kernel)
            }
        }
    }
}


fun createKernels2(): List<List<Pair<Vector, Char>>> {
    val k = (-1..1).map{ Vector(it, it) }.zip("MAS".toList()).let {
        it + it.mirrorHorizontally()
    }

    val kernels = generateSequence(k) {
        it.rotate()
    }.take(4).toList()

//    kernels.forEach {
//        it.toMap().printGrid()
//        println()
//    }

    return kernels
}

private fun <T> List<Pair<Vector, T>>.pivot(): List<Pair<Vector, T>> {
    return this.map { (location, t) ->
        Vector(location.y, location.x) to t
    }
}

private fun <T> List<Pair<Vector, T>>.mirrorHorizontally(): List<Pair<Vector, T>> {
    return this.map { (location, t) ->
        Vector(-location.x, location.y) to t
    }
}

private fun <T> List<Pair<Vector, T>>.rotate(): List<Pair<Vector, T>> {
    return this.map { (location, t) ->
        val (x, y) = location
        val v = when {
            x.sign == -1 && y.sign == -1 -> Vector(-x, y)
            x.sign == 1 && y.sign == -1 -> Vector(x, -y)
            x.sign == 1 && y.sign == 1 -> Vector(-x, y)
            x.sign == -1 && y.sign == 1 -> Vector(x, -y)
            else -> location
        }

        v to t
    }
}

val part2Kernels = createKernels2()

fun part2(input: List<String>): Int {
    return mapToLocations(input).toMap().let { grid ->
        grid.entries.filter { it.value == 'A' }.sumOf { (location, _) ->
            part2Kernels.count { kernel ->
                checkKernelAtLocation(grid, location, kernel)
            }
        }
    }
}

fun checkKernelAtLocation(grid: Map<Vector, Char>, location: Vector, kernel: List<Pair<Vector, Char>>): Boolean {
    return kernel.all { (kernelLocation, expected) ->
        grid[kernelLocation + location] == expected
    }
}
