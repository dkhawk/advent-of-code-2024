package utils

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

fun readText(name: String) = Path("src/$name.txt").readText()

/**
 * Reads lines from the given input txt file.
 */
fun readLines(name: String) = _root_ide_package_.utils.readText(name).trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

data class Vector(val x: Int = 0, val y: Int = 0) {
    fun advance(heading8: _root_ide_package_.utils.Heading8): _root_ide_package_.utils.Vector {
        return this + heading8.vector
    }

    fun advance(heading: _root_ide_package_.utils.Heading): _root_ide_package_.utils.Vector {
        return this + heading.vector
    }
}

enum class Heading(val vector: _root_ide_package_.utils.Vector) {
    NORTH(_root_ide_package_.utils.Vector(0, -1)),
    WEST(_root_ide_package_.utils.Vector(-1, 0)), EAST(_root_ide_package_.utils.Vector(1, 0)),
    SOUTH(_root_ide_package_.utils.Vector(0, 1));

    fun turnRight(): _root_ide_package_.utils.Heading {
        return when (this) {
            _root_ide_package_.utils.Heading.NORTH -> _root_ide_package_.utils.Heading.EAST
            _root_ide_package_.utils.Heading.EAST -> _root_ide_package_.utils.Heading.SOUTH
            _root_ide_package_.utils.Heading.SOUTH -> _root_ide_package_.utils.Heading.WEST
            _root_ide_package_.utils.Heading.WEST -> _root_ide_package_.utils.Heading.NORTH
        }
    }

    fun turnLeft(): _root_ide_package_.utils.Heading {
        return when (this) {
            _root_ide_package_.utils.Heading.NORTH -> _root_ide_package_.utils.Heading.WEST
            _root_ide_package_.utils.Heading.EAST -> _root_ide_package_.utils.Heading.NORTH
            _root_ide_package_.utils.Heading.SOUTH -> _root_ide_package_.utils.Heading.EAST
            _root_ide_package_.utils.Heading.WEST -> _root_ide_package_.utils.Heading.SOUTH
        }
    }

    fun opposite(): _root_ide_package_.utils.Heading {
        return when (this) {
            _root_ide_package_.utils.Heading.NORTH -> _root_ide_package_.utils.Heading.SOUTH
            _root_ide_package_.utils.Heading.EAST -> _root_ide_package_.utils.Heading.WEST
            _root_ide_package_.utils.Heading.SOUTH -> _root_ide_package_.utils.Heading.NORTH
            _root_ide_package_.utils.Heading.WEST -> _root_ide_package_.utils.Heading.EAST
        }
    }
}

enum class Heading8(val vector: _root_ide_package_.utils.Vector) {
    NORTHWEST(_root_ide_package_.utils.Vector(-1, -1)), NORTH(_root_ide_package_.utils.Vector(0, -1)), NORTHEAST(
        _root_ide_package_.utils.Vector(1, -1)
    ),
         WEST(
             _root_ide_package_.utils.Vector(
                 -1,
                 0
             )
         ),                                  EAST(_root_ide_package_.utils.Vector(1, 0)),
    SOUTHWEST(_root_ide_package_.utils.Vector(-1, 1)), SOUTH(_root_ide_package_.utils.Vector(0, 1)), SOUTHEAST(
        _root_ide_package_.utils.Vector(1, 1)
    );

}

fun allHeadings8(): List<_root_ide_package_.utils.Vector> {
    return _root_ide_package_.utils.Heading8.entries.map { heading -> heading.vector }
}

operator fun _root_ide_package_.utils.Vector.plus(other: _root_ide_package_.utils.Vector): _root_ide_package_.utils.Vector {
    return _root_ide_package_.utils.Vector(x + other.x, y + other.y)
}

operator fun _root_ide_package_.utils.Vector.plus(heading8: _root_ide_package_.utils.Heading8): _root_ide_package_.utils.Vector {
    return _root_ide_package_.utils.Vector(x + heading8.vector.x, y + heading8.vector.y)
}

operator fun _root_ide_package_.utils.Vector.times(scalar: Int): _root_ide_package_.utils.Vector {
    return _root_ide_package_.utils.Vector(x * scalar, y * scalar)
}

fun mapToLocations(input: List<String>): List<Pair<_root_ide_package_.utils.Vector, Char>> {
    return input.mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            _root_ide_package_.utils.Vector(x, y) to c
        }
    }.flatten()
}

data class Bounds(
    val min: _root_ide_package_.utils.Vector,
    val max: _root_ide_package_.utils.Vector,
) {
    fun contains(vector: _root_ide_package_.utils.Vector): Boolean {
        return vector.x in min.x..(max.x) && vector.y in min.y..(max.y)
    }

    /*
        bounds.println()
    bounds.contains(bounds.min).println()
    bounds.contains(bounds.max).println()
    bounds.contains(bounds.min + Heading.NORTH).println()
    bounds.contains(bounds.min + Heading.WEST).println()
    bounds.contains(bounds.max + Heading.EAST).println()
    bounds.contains(bounds.max + Heading.SOUTH).println()
     */
}

fun Collection<_root_ide_package_.utils.Vector>.minMax(): _root_ide_package_.utils.Bounds {
    val xMinMax = this.map { it.x }.sorted().let { l -> l.first() to l.last() }
    val yMinMax = this.map { it.y }.sorted().let { l -> l.first() to l.last() }

    return _root_ide_package_.utils.Bounds(
        _root_ide_package_.utils.Vector(xMinMax.first, yMinMax.first),
        _root_ide_package_.utils.Vector(xMinMax.second, yMinMax.second)
    )
}

fun Map<_root_ide_package_.utils.Vector, Char>.printGrid(
    default: Char = '.',
    formatter: (_root_ide_package_.utils.Vector, Char) -> String = { _, c -> "$c" }
) {
    val (minLocation, maxLocation) = this.keys.minMax()

    val gw = this.withDefault { default }

    (minLocation..maxLocation).map { row ->
        row.joinToString("") {
            formatter(it, gw.getValue(it))
        }.println()
    }
}

operator fun _root_ide_package_.utils.Vector.rangeTo(other: _root_ide_package_.utils.Vector): List<List<_root_ide_package_.utils.Vector>> {
    return (this.y..other.y).map { y ->
        (this.x..other.x).map { x ->
            _root_ide_package_.utils.Vector(x, y)
        }
    }
}

fun _root_ide_package_.utils.Heading.toSymbol(): Char {
    return when (this) {
        _root_ide_package_.utils.Heading.NORTH -> '^'
        _root_ide_package_.utils.Heading.EAST -> '>'
        _root_ide_package_.utils.Heading.SOUTH -> 'v'
        _root_ide_package_.utils.Heading.WEST -> '<'
    }
}