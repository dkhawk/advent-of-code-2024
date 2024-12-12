package day12

import utils.*

val testInput1 = """
    AAAA
    BBCD
    BBCC
    EEEC
""".trimIndent().lines()

val testInput2 = """
    RRRRIICCFF
    RRRRIICCCF
    VVRRRCCFFF
    VVRCCCJFFF
    VVVVCJJCFE
    VVIVCCJJEE
    VVIIICJJEE
    MIIIIIJJEE
    MIIISIJEEE
    MMMISSJEEE
""".trimIndent().lines()

val testInput3 = """
    OOOOO
    OXOXO
    OOOOO
    OXOXO
    OOOOO
""".trimIndent().lines()

val testInput4 = """
    EEEEE
    EXXXX
    EEEEE
    EXXXX
    EEEEE
""".trimIndent().lines()

val testInput5 = """
    AAAAAA
    AAABBA
    AAABBA
    ABBAAA
    ABBAAA
    AAAAAA
""".trimIndent().lines()

fun main() {
    check(part1(testInput1) == 140)
    check(part1(testInput2) == 1930)
    check(part1(testInput3) == 772)
    check(part2(testInput1) == 80)
    check(part2(testInput4) == 236)
    check(part2(testInput5) == 368)

    val input = readLines("inputs/12")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    val grid = buildGrid(input).withDefault { '.' }

    val perimeters = grid.map { (location, value) ->
        location to Heading.entries.map { heading -> location.advance(heading) }.count { grid.getValue(it) != value }
    }.toMap()

    val clusters = createClusters(grid)

    val regions = clusters.map { (clusterId, plots) ->
        val area = plots.size
        val perimeter = plots.sumOf { plot -> perimeters.getValue(plot) }

        val clusterLetter = grid.getValue(plots.first())

        (clusterId to clusterLetter) to (area to perimeter)
    }

    return regions.sumOf { (_, region) ->
        val (area, perimeter) = region
        area * perimeter
    }
}

private fun createClusters(grid: Map<Vector, Char>): MutableMap<Int, MutableSet<Vector>> {
    val clusterIndexMap = grid.entries.mapIndexed { index, (location, _) ->
        location to index
    }

    val inverseClusters = clusterIndexMap.associate {
        it.first to it.second
    }.toMutableMap()

    val clusters = clusterIndexMap.associate { (location, value) ->
        value to mutableSetOf(location)
    }.toMutableMap()

    grid.map { (location, value) ->
        location to Heading.entries.map { heading -> location.advance(heading) }.forEach { neighbor ->
            val neighborValue = grid.getValue(neighbor)
            if (neighborValue == value) {
                mergeClusters(clusters, inverseClusters, neighbor, location)
            }
        }
    }
    return clusters
}

fun mergeClusters(
    clusterMap: MutableMap<Int, MutableSet<Vector>>,
    inverseClusters: MutableMap<Vector, Int>,
    neighbor: Vector,
    location: Vector
) {
    val myClusterId = inverseClusters.getValue(location)
    val neighborClusterId = inverseClusters.getValue(neighbor)

    if (myClusterId == neighborClusterId) {
        return
    }

    val others = clusterMap.getValue(neighborClusterId)

    others.forEach { other ->
        inverseClusters[other] = myClusterId
    }

    clusterMap.getValue(myClusterId).addAll(others)
    clusterMap.remove(neighborClusterId)
}

data class Region(
    val id: Int,
    val label: Char,
    val area: Int,
    val perimeter: Int,
    val cost: Int
)

private fun part2(input: List<String>): Int  {
    val grid = buildGrid(input).withDefault { '.' }
    val clusters = createClusters(grid)

    val perimeters = grid.map { (location, value) ->
        location to grid.getNeighbors(location).mapNotNull { neighbor ->
            if (value != neighbor.second) { neighbor.first } else { null }
        }
    }.toMap()

    val perimeterStraights = clusters.map { (clusterId, plots) ->
        clusterId to getStraightPerimeterCount(plots, perimeters)
    }.toMap()

    val regions = clusters.map { (clusterId, plots) ->
        val area = plots.size
        val perimeter = perimeterStraights.getValue(clusterId)
        val label = grid.getValue(plots.first())
        val cost = area * perimeter
        Region(
            id = clusterId,
            label = label,
            area = area,
            perimeter = perimeter,
            cost = cost
        )
    }

    return regions.sumOf { it.cost }
}

fun getStraightPerimeterCount(plots: MutableSet<Vector>, perimeters: Map<Vector, List<Heading>>): Int {
    var discount = 0

    val clusterPerimeter = plots.map { plot -> perimeters.getValue(plot).size }.sum()

    plots.map { plot ->
        val myPerimeter = perimeters.getValue(plot)
        val east = plot.advance(Heading.EAST)
        if (east in plots) {
            val eastPerimeter = perimeters.getValue(east)
            if (Heading.NORTH in eastPerimeter && Heading.NORTH in myPerimeter) {
                discount += 1
            }
            if (Heading.SOUTH in eastPerimeter && Heading.SOUTH in myPerimeter) {
                discount += 1
            }
        }

        val south = plot.advance(Heading.SOUTH)
        if (south in plots) {
            val southPerimeter = perimeters.getValue(south)
            if (Heading.WEST in southPerimeter && Heading.WEST in myPerimeter) {
                discount += 1
            }
            if (Heading.EAST in southPerimeter && Heading.EAST in myPerimeter) {
                discount += 1
            }
        }
    }

    return clusterPerimeter - discount
}

private fun Map<Vector, Char>.getNeighbors(location: Vector): List<Pair<Heading, Char>> {
    return Heading.entries.map { heading ->
        location.advance(heading).let { heading to getValue(it) }
    }
}
