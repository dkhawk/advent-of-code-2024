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

data class Vector(val x: Int = 0, val y: Int = 0)

enum class Heading(val vector: Vector) {
    NORTHWEST(Vector(-1, -1)), NORTH(Vector(0, -1)), NORTHEAST(Vector(1, -1)),
         WEST(Vector(-1,  0)),                                  EAST(Vector(1,  0)),
    SOUTHWEST(Vector(-1,  1)), SOUTH(Vector(0,  1)), SOUTHEAST(Vector(1,  1)),
}

fun allHeadings(): List<Vector> {
    return Heading.entries.map { heading -> heading.vector }
}

operator fun Vector.plus(other: Vector): Vector {
    return Vector(x + other.x, y + other.y)
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
