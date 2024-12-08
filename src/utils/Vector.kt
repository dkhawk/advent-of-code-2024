package utils

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
}