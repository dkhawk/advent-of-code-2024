package utils

import kotlin.math.abs

data class Vector(val x: Int = 0, val y: Int = 0) {
    fun advance(heading8: Heading8): Vector {
        return this + heading8.vector
    }

    fun advance(heading: Heading): Vector {
        return this + heading.vector
    }

    operator fun unaryMinus(): Vector = Vector(-x, -y)
    operator fun plus(other: Vector): Vector {
        return Vector(x + other.x, y + other.y)
    }

    operator fun minus(other: Vector): Vector {
        return Vector(x - other.x, y - other.y)
    }

    operator fun plus(heading8: Heading8): Vector {
        return Vector(x + heading8.vector.x, y + heading8.vector.y)
    }

    operator fun times(scalar: Int): Vector {
        return Vector(x * scalar, y * scalar)
    }

    fun magnitude(): Int {
        return abs(x) + abs(y)
    }

    fun toHeading(): Heading {
        return Heading.entries.first {
            it.vector == this
        }
    }
}

data class VectorLong(val x: Long, val y: Long) {
    fun advance(heading8: Heading8): VectorLong {
        return this + heading8.vector.toLongVector()
    }

    fun advance(heading: Heading): VectorLong {
        return this + heading.vector.toLongVector()
    }

    operator fun unaryMinus(): VectorLong = VectorLong(-x, -y)
    operator fun plus(other: VectorLong): VectorLong {
        return VectorLong(x + other.x, y + other.y)
    }

    operator fun minus(other: VectorLong): VectorLong {
        return VectorLong(x - other.x, y - other.y)
    }

    operator fun plus(heading8: Heading8): VectorLong {
        return VectorLong(x + heading8.vector.x, y + heading8.vector.y)
    }

    operator fun times(scalar: Long): VectorLong {
        return VectorLong(x * scalar, y * scalar)
    }

    fun entrywiseProduct(costBasis: VectorLong): VectorLong {
        return VectorLong(x = x * costBasis.x, y = y * costBasis.y)
    }

    fun sum() = x + y
}

private fun Vector.toLongVector() = VectorLong(x.toLong(), y.toLong())

fun VectorLong.toVector() = Vector(
    x = x.toInt(),
    y = y.toInt()
)

fun Pair<Int, Int>.toVector() = Vector(first, second)
fun Vector.manhattanDistanceTo(other: Vector): Int {
    return abs(x - other.x) + abs(y - other.y)
}