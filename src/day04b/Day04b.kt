package day04b

import utils.Vector
import utils.allHeadings8
import utils.mapToLocations
import utils.plus
import utils.println
import utils.readText
import kotlin.math.sign
import kotlin.time.measureTime

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
    val input = _root_ide_package_.utils.readText("inputs/04").lines()

    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}


fun createKernels(): List<List<Pair<_root_ide_package_.utils.Vector, Char>>> {
    val word = "XMAS"

    val kernels = _root_ide_package_.utils.allHeadings8().map { heading ->
        val s = generateSequence(_root_ide_package_.utils.Vector()) { it + heading }.iterator()
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

    return _root_ide_package_.utils.mapToLocations(input).toMap().let { grid ->
        grid.entries.filter { it.value == 'X' }.sumOf { (location, _) ->
            part1Kernels.count { kernel ->
                checkKernelAtLocation(grid, location, kernel)
            }
        }
    }
}


fun createKernels2(): List<List<Pair<_root_ide_package_.utils.Vector, Char>>> {
    val k = (-1..1).map{ _root_ide_package_.utils.Vector(it, it) }.zip("MAS".toList()).let {
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

private fun <T> List<Pair<_root_ide_package_.utils.Vector, T>>.pivot(): List<Pair<_root_ide_package_.utils.Vector, T>> {
    return this.map { (location, t) ->
        _root_ide_package_.utils.Vector(location.y, location.x) to t
    }
}

private fun <T> List<Pair<_root_ide_package_.utils.Vector, T>>.mirrorHorizontally(): List<Pair<_root_ide_package_.utils.Vector, T>> {
    return this.map { (location, t) ->
        _root_ide_package_.utils.Vector(-location.x, location.y) to t
    }
}

private fun <T> List<Pair<_root_ide_package_.utils.Vector, T>>.rotate(): List<Pair<_root_ide_package_.utils.Vector, T>> {
    return this.map { (location, t) ->
        val (x, y) = location
        val v = when {
            x.sign == -1 && y.sign == -1 -> _root_ide_package_.utils.Vector(-x, y)
            x.sign == 1 && y.sign == -1 -> _root_ide_package_.utils.Vector(x, -y)
            x.sign == 1 && y.sign == 1 -> _root_ide_package_.utils.Vector(-x, y)
            x.sign == -1 && y.sign == 1 -> _root_ide_package_.utils.Vector(x, -y)
            else -> location
        }

        v to t
    }
}

val part2Kernels = createKernels2()

fun part2(input: List<String>): Int {
    return _root_ide_package_.utils.mapToLocations(input).toMap().let { grid ->
        grid.entries.filter { it.value == 'A' }.sumOf { (location, _) ->
            part2Kernels.count { kernel ->
                checkKernelAtLocation(grid, location, kernel)
            }
        }
    }
}

fun checkKernelAtLocation(grid: Map<_root_ide_package_.utils.Vector, Char>, location: _root_ide_package_.utils.Vector, kernel: List<Pair<_root_ide_package_.utils.Vector, Char>>): Boolean {
    return kernel.all { (kernelLocation, expected) ->
        grid[kernelLocation + location] == expected
    }
}
