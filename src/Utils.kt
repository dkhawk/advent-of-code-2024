import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

fun readText(name: String) = Path("src/$name.txt").readText()

/**
 * Reads lines from the given input txt file.
 */
fun readLines(name: String) = readText(name).trim().lines()

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
    fun advance(heading8: Heading8): Vector {
        return this + heading8.vector
    }

    fun advance(heading: Heading): Vector {
        return this + heading.vector
    }
}

enum class Heading(val vector: Vector) {
    NORTH(Vector(0, -1)),
    WEST(Vector(-1, 0)), EAST(Vector(1, 0)),
    SOUTH(Vector(0, 1));

    fun turnRight(): Heading {
        return when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
    }

    fun turnLeft(): Heading {
        return when (this) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
        }
    }

    fun opposite(): Heading {
        return when (this) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }
}

enum class Heading8(val vector: Vector) {
    NORTHWEST(Vector(-1, -1)), NORTH(Vector(0, -1)), NORTHEAST(Vector(1, -1)),
         WEST(Vector(-1,  0)),                                  EAST(Vector(1,  0)),
    SOUTHWEST(Vector(-1,  1)), SOUTH(Vector(0,  1)), SOUTHEAST(Vector(1,  1));

}

fun allHeadings8(): List<Vector> {
    return Heading8.entries.map { heading -> heading.vector }
}

operator fun Vector.plus(other: Vector): Vector {
    return Vector(x + other.x, y + other.y)
}

operator fun Vector.plus(heading8: Heading8): Vector {
    return Vector(x + heading8.vector.x, y + heading8.vector.y)
}

operator fun Vector.times(scalar: Int): Vector {
    return Vector(x * scalar, y * scalar)
}

fun mapToLocations(input: List<String>): List<Pair<Vector, Char>> {
    return input.mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            Vector(x, y) to c
        }
    }.flatten()
}

data class Bounds(
    val min: Vector,
    val max: Vector,
) {
    fun contains(vector: Vector): Boolean {
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

fun Collection<Vector>.minMax(): Bounds {
    val xMinMax = this.map { it.x }.sorted().let { l -> l.first() to l.last() }
    val yMinMax = this.map { it.y }.sorted().let { l -> l.first() to l.last() }

    return Bounds(Vector(xMinMax.first, yMinMax.first), Vector(xMinMax.second, yMinMax.second))
}

fun Map<Vector, Char>.printGrid(
    default: Char = '.',
    formatter: (Vector, Char) -> String = { _, c -> "$c" }
) {
    val (minLocation, maxLocation) = this.keys.minMax()

    val gw = this.withDefault { default }

    (minLocation..maxLocation).map { row ->
        row.joinToString("") {
            formatter(it, gw.getValue(it))
        }.println()
    }
}

operator fun Vector.rangeTo(other: Vector): List<List<Vector>> {
    return (this.y..other.y).map { y ->
        (this.x..other.x).map { x ->
            Vector(x, y)
        }
    }
}

fun Heading.toSymbol(): Char {
    return when (this) {
        Heading.NORTH -> '^'
        Heading.EAST -> '>'
        Heading.SOUTH -> 'v'
        Heading.WEST -> '<'
    }
}