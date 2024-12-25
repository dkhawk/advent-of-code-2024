package day21c

import utils.*

val testInput = """
    029A
    980A
    179A
    456A
    379A
""".trimIndent().lines()


fun main() {
    part1(testInput).println()
}

val dpadGrid = buildGrid(
    """
    .^A
    <v>
""".trimIndent().lines()
)

val dpad = dpadGrid.entries.associate { it.value to it.key }


val keypadGrid = buildGrid(
    """
    789
    456
    123
    .0A
""".trimIndent().lines()
)

val keypad = keypadGrid.entries.associate { it.value to it.key }

sealed interface Robot {
    val location: Vector
    val grid: Map<Vector, Char>
    val pad: Map<Char, Vector>

    fun toLocation(goal: Char) = pad[goal]!!
    fun isLegalLocation(location: Vector): Boolean = grid.contains(location)
    fun toSymbolForHeading(heading: Heading): Char = grid[location.advance(heading)]!!
    fun isLegalMove(heading: Heading): Boolean = isLegalLocation(location.advance(heading))
    fun pathToGoalString(path: List<Vector>) = buildString {
        var l = location
        path.forEach {
            val heading = (it - l).toHeading()
            append(heading.toSymbol())
            l = l.advance(heading)
        }
    }
}

data class DpadRobot(override val location: Vector = dpad['A']!!) : Robot {
    override val grid: Map<Vector, Char>
        get() = dpadGrid

    override val pad: Map<Char, Vector>
        get() = dpad
}

data class KeypadRobot(override val location: Vector = keypad['A']!!) : Robot {
    override val grid: Map<Vector, Char>
        get() = keypadGrid

    override val pad: Map<Char, Vector>
        get() = keypad
}

private fun part1(input: List<String>): Int {
    val keypadRobot = KeypadRobot()
    val keypadPaths = getAllPaths(keypadRobot, '0')
    keypadPaths.toList().printAsLines()
    val keypadMoves = keypadPaths.map { path ->
        keypadRobot.pathToGoalString(path)
    }.toList()

    keypadMoves.map { moveSequence ->
        val radiationRobot = DpadRobot()
        val radiationPaths = getAllPaths(radiationRobot, moveSequence)

        val radiationMoves = radiationPaths.map { paths ->
            paths.toList().map {
                radiationRobot.pathToGoalString(it)
            }
        }

        radiationMoves.printAsLines()

        radiationMoves.forEach { candidate ->
            radiationMoves.map { moveSequence ->
                val coldRobot = DpadRobot()
//                val coldPaths = getAllPaths(coldRobot, moveSequence)

            }
        }

    }

    TODO()
}

private fun getAllPaths(
    bot: Robot,
    path: String
): List<Sequence<List<Vector>>> {
    return path.map { goal ->
        getAllPaths(bot, goal)
    }
}

private fun getAllPaths(
    bot: Robot,
    goal: Char
): Sequence<List<Vector>> {
    val goalLocation = bot.toLocation(goal)

    val allHeadingSequences = getHeadingSequences(bot, goalLocation).toList() //.also { .printAsLines() }
//    allHeadingSequences.permutations().toList().printAsLines()

    return allHeadingSequences.permutations().map { headings ->
        var location = bot.location
        headings.map { heading ->
            location.advance(heading).also { location = it }
        }
    }.filter { locations ->
        locations.all {
            val isLegal = bot.isLegalLocation(it)
            isLegal
        }
    }
}

private fun <E> List<E>.permutations(): Sequence<List<E>> = sequence {
    if (size == 1) {
        yield(listOf(first()))
    }

    val indices = this@permutations.indices

    val emitted = mutableSetOf<List<E>>()

    for (index in indices) {
        val rest = subList(0, index) + subList(index + 1, size)

        rest.permutations().forEach {
            val next = buildList {
                add(this@permutations.get(index))
                addAll(it)
            }

            if (!emitted.contains(next)) {
                emitted.add(next)
                yield(next)
            }
        }
    }
}

private fun getHeadingSequences(robot: Robot, goal: Vector) = sequence {
    val delta = goal - robot.location

    if (delta.x > 0) {
        repeat(delta.x) {
            yield(Heading.EAST)
        }
    }

    if (delta.x < 0) {
        repeat(-delta.x) {
            yield(Heading.WEST)
        }
    }

    if (delta.y > 0) {
        repeat(delta.y) {
            yield(Heading.SOUTH)
        }
    }

    if (delta.y < 0) {
        repeat(-delta.y) {
            yield(Heading.NORTH)
        }
    }
}
