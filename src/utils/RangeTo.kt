package utils

operator fun Vector.rangeTo(other: Vector): List<List<Vector>> {
    return (this.y..other.y).map { y ->
        (this.x..other.x).map { x ->
            Vector(x, y)
        }
    }
}