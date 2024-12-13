package day13

import utils.*
import kotlin.math.min
import kotlin.math.max

val testInput = """
    Button A: X+94, Y+34
    Button B: X+22, Y+67
    Prize: X=8400, Y=5400

    Button A: X+26, Y+66
    Button B: X+67, Y+21
    Prize: X=12748, Y=12176

    Button A: X+17, Y+86
    Button B: X+84, Y+37
    Prize: X=7870, Y=6450

    Button A: X+69, Y+23
    Button B: X+27, Y+71
    Prize: X=18641, Y=10279
""".trimIndent().lines()

val buttonACost = 3
val buttonBCost = 1

data class Machine(
    val btnA: Vector,
    val btnB: Vector,
    val prize: Vector,
)

fun main() {
    val machines = parseInput(testInput)

    check(part1(machines) == 480)
//    check(part2(machines) == 15)

    val machinesReal = parseInput(readLines("inputs/13"))
    "25751 is correct!!".println()
    part1(machinesReal).println()
//    part2(machinesReal).println()
}

private fun part1(machines: List<Machine>): Int {
    return machines.mapNotNull { machine ->
        playMachine(machine)
    }.sum()
}

fun playMachine(machine: Machine): Int? {
    val maxBPresses = max(100,
        min(machine.prize.x / machine.btnB.x, machine.prize.y / machine.btnB.y))

    return (0..maxBPresses).firstOrNull { bPresses ->
        val target = machine.prize - (machine.btnB * bPresses)
        val aPresses = (target.x / machine.btnA.x)

        (aPresses <= 100) && (machine.btnA * aPresses == target)
    }?.let { bPresses ->
        val target = machine.prize - (machine.btnB * bPresses)
        val aPresses = (target.x / machine.btnA.x)
        val aPresses2 = (target.y / machine.btnA.y)

        check(aPresses == aPresses2)
        check(aPresses <= 100)
        check(bPresses <= 100)

        check(target.x.rem(machine.btnA.x) == 0)
        check(target.y.rem(machine.btnA.y) == 0)

        (target.x / machine.btnA.x) * buttonACost + (bPresses * buttonBCost)
    }
}

private fun part2(input: List<Machine>): Int {
    return -1
}

private fun parseInput(input: List<String>): List<Machine> {
    return input.chunked(4).mapNotNull { chunk ->
        if (chunk.size < 3) {
            null
        } else {
            parseMachine(chunk)
        }
    }
}

private fun parseMachine(lines: List<String>): Machine {
    return Machine(
        btnA = parseMachineLine(lines[0]),
        btnB = parseMachineLine(lines[1]),
        prize = parseMachineLine(lines[2]),
    )
}

val buttonRegex = """X[+=](?<x>[0-9]*), Y[+=](?<y>[0-9]*)""".trimMargin().toRegex()

private fun parseMachineLine(line: String): Vector {
    return buttonRegex.find(line)!!.groups.let { groups ->
        Vector(groups["x"]!!.value.toInt(), groups["y"]!!.value.toInt())
    }
}
