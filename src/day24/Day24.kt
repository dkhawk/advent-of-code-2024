package day24

import utils.*

val testInput = """
    x00: 1
    x01: 1
    x02: 1
    y00: 0
    y01: 1
    y02: 0

    x00 AND y00 -> z00
    x01 XOR y01 -> z01
    x02 OR y02 -> z02
""".trimIndent().lines()

val testInput2 = """
    x00: 1
    x01: 0
    x02: 1
    x03: 1
    x04: 0
    y00: 1
    y01: 1
    y02: 1
    y03: 1
    y04: 1

    ntg XOR fgs -> mjb
    y02 OR x01 -> tnw
    kwq OR kpj -> z05
    x00 OR x03 -> fst
    tgd XOR rvg -> z01
    vdt OR tnw -> bfw
    bfw AND frj -> z10
    ffh OR nrd -> bqk
    y00 AND y03 -> djm
    y03 OR y00 -> psh
    bqk OR frj -> z08
    tnw OR fst -> frj
    gnj AND tgd -> z11
    bfw XOR mjb -> z00
    x03 OR x00 -> vdt
    gnj AND wpb -> z02
    x04 AND y00 -> kjc
    djm OR pbm -> qhw
    nrd AND vdt -> hwm
    kjc AND fst -> rvg
    y04 OR y02 -> fgs
    y01 AND x02 -> pbm
    ntg OR kjc -> kwq
    psh XOR fgs -> tgd
    qhw XOR tgd -> z09
    pbm OR djm -> kpj
    x03 XOR y03 -> ffh
    x00 XOR y04 -> ntg
    bfw OR bqk -> z06
    nrd XOR fgs -> wpb
    frj XOR qhw -> z04
    bqk OR frj -> z07
    y03 OR x01 -> nrd
    hwm AND bqk -> z03
    tgd XOR rvg -> z12
    tnw OR pbm -> gnj
""".trimIndent().lines()

fun main() {
    check(part1(testInput) == 4L)
    check(part1(testInput2) == 2024L)
//    check(part2(testInput) == 0)

    val input = readLines("inputs/24")
    part1(input).println()
}

sealed interface Gate {
    val a: String
    val b: String

    operator fun component1(): String = a
    operator fun component2(): String = b
    fun output(b: Boolean, b1: Boolean): Boolean
}

data class AndGate(override val a: String, override val b: String) : Gate {
    override fun output(b: Boolean, b1: Boolean) = b && b1
}

data class OrGate(override val a: String, override val b: String) : Gate {
    override fun output(b: Boolean, b1: Boolean) = b || b1
}

data class XorGate(override val a: String, override val b: String) : Gate {
    override fun output(b: Boolean, b1: Boolean) = b xor b1
}

private fun part1(input: List<String>): Long {
    val pins = input.takeWhile { it.isNotBlank() }.associate { line ->
        val parts = line.split(":")
        parts[0].trim() to (parts[1].trim() == "1")
    }

    val gates = input.dropWhile { it.isNotBlank() }.drop(1).associate { line ->
        val parts = line.split("->")
        parts[1].trim() to (parts[0].trim().toGate())
    }

//    pins.printAsLines()
//    gates.printAsLines()

    val resolved = pins.toMutableMap()

    // Find all the z pins
    val zPins = gates.entries.filter { it.key.startsWith("z") }.map { it.key to it.value }
    val queue = ArrayDeque(zPins)

    "There are ${zPins.size} z pins".println()

    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()

        if (resolved.contains(next.first)) {
            println("Already resolved ${next.first}")
            continue
        }

        val gate = next.second
        val (a, b) = next.second

        if (resolved.contains(a) && resolved.contains(b)) {
            resolved[next.first] = gate.output(resolved[a]!!, resolved[b]!!)
        } else {
            queue.addFirst(next)
            if (!resolved.contains(a)) {
                queue.addFirst(a to gates.getValue(a))
            }
            if (!resolved.contains(b)) {
                queue.addFirst(b to gates.getValue(b))
            }
        }
    }

    return zPins.map { it.first }.sorted().reversed()
        .map { resolved.getValue(it) }.map { if (it) 1L else 0L }
        .fold(0L) { acc, value -> (acc shl 1) or value }
            .also { it.println() }
}

private fun String.toGate(): Gate {
    val parts = this.split(" ").map(String::trim)

    return when (parts[1]) {
        "AND" -> AndGate(parts[0], parts[2])
        "OR" -> OrGate(parts[0], parts[2])
        "XOR" -> XorGate(parts[0], parts[2])
        else -> error("Unknown GATE type: ${parts[1]}")
    }
}

private fun part2(input: List<String>): Int {
    return -1
}