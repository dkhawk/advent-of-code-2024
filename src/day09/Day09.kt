package day09

import kotlinx.coroutines.runBlocking
import utils.println
import utils.readText
import java.math.BigInteger
import java.util.*
import kotlin.time.measureTime

val testInput2 = """
    2333133121414131402
""".trimIndent()

fun main() = runBlocking {
    check(part1(testInput2) == 1928L)
    check(part2(testInput2) == BigInteger.valueOf(2858))

    val input = readText("inputs/09")
    part1(input).println()

    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: String): Long {
    val memory = createMemory1(
        input.map { it - '0' }
    )
    defrag1(memory)
    return checksum1(memory)
}

private fun memoryDump(memory: MutableList<Int>): String {
    return memory.joinToString("") {
        if (it < 0) "." else it.toString()
    }
}

private fun checksum1(memory: List<Int>): Long {
    return memory.mapIndexed { index, i ->
        if (i <= 0) {
            0
        } else {
            index * i.toLong()
        }
    }.sum()
}

private fun createMemory1(map: List<Int>): MutableList<Int> {
    val result = mutableListOf<Int>()

    var id = 0

    map.windowed(2, 2, partialWindows = true).forEach {
        repeat(it.first()) { result.add(id) }
        if (it.size == 2) repeat(it[1]) { result.add(-1) }
        id += 1
    }

    return result
}

fun defrag1(memory: MutableList<Int>) {
    var tail = memory.indices.last
    var head = 0

    while (head <= tail) {
//        memoryDump(memory).println()
//        for (index in pointersline.indices) {
//            pointersline[index] = '.'
//        }
//        pointersline[head] = 'H'
//        pointersline[tail] = 'T'
//
//        if (head == tail) {
//            pointersline[head] = '*'
//        }
//        pointersline.joinToString("").println()

//        head.println()
//        tail.println()
        // Advance head to the next empty spot
        while (memory[head] >= 0 && head <= tail) {
            head += 1
        }

        // Advance tail to the next block to move
        while (tail > head && memory[tail] < 0) {
            tail -= 1
        }

        if (tail <= head) {
            break
        }

        memory[head] = memory[tail]
        memory[tail] = -1

        head += 1
        tail -= 1

//        println()
//        steps--
//        if (steps <= 0) error("Too many steps")
    }
}

fun part2(input: String): BigInteger {
    val memory = createMemory2(
        input.map { it - '0' }
    )

    val memorySize = memory.last.let { it.size + it.start }

//    memoryDump2(memory, memorySize).also {
//        it.println()
////        check(it == "00...111...2...333.44.5555.6666.777.888899")
//    }

    defrag2(memory, memorySize)

//    memoryDump2(memory, memorySize).also {
//        it.println()
////        check(it == "00...111...2...333.44.5555.6666.777.888899")
//    }
    return checksum2(memory)
}

fun checksum2(memory: SortedSet<FileBlock>): BigInteger {
    return memory.fold(BigInteger.ZERO) { acc, block ->
        acc + (block.start..<block.end).fold(BigInteger.ZERO) { acc, index ->
            acc + BigInteger.valueOf(index.toLong() * block.id.toLong())
        }
    }
}

fun memoryDump2(
    memory: SortedSet<FileBlock>,
    memorySize: Int,
): String {
    return buildString {
        memory.forEach { block ->
            while (this.length < block.start) {
                append('.')
            }
            while (this.length < block.end) {
                append(block.id % 10)
            }
        }
        while (this.length < memorySize) {
            append('.')
        }
    }
}

fun defrag2(memory: SortedSet<FileBlock>, memorySize: Int): SortedSet<FileBlock> {
    // Constructs a list of the block in descending order of the block id
    val blocksQueue = memory.toSortedSet { o1, o2 -> o2.id.compareTo(o1.id) }

    while (blocksQueue.isNotEmpty()) {
        val next = blocksQueue.removeFirst()

        memory
            // Find all gaps to the left of the block to move
            .takeWhile { it.start <= next.start }
            // A gap lands between two blocks...
            .windowed(2, 1)
            // Grab the first one that fits
            .firstOrNull { (a, b) ->
                b.start - a.end >= next.size
            }?.first()?.end?.let { newStart ->
                val newBlock = next.copy(start = newStart)
                memory.remove(next)
                memory.add(newBlock)
                // memoryDump3(memory, memorySize).println()
            }
    }

    return memory
}

data class FileBlock(val id: Int, val start: Int, val size: Int) : Comparable<FileBlock> {
    val end: Int
        get() {
            return start + size
        }

    override operator fun compareTo(other: FileBlock) = compareValuesBy(this, other) { it.start }
}

fun createMemory2(map: List<Int>): SortedSet<FileBlock> {
    var id = 0
    var address = 0

    return sortedSetOf<FileBlock>().apply {
        map.windowed(2, 2, partialWindows = true).forEach {
            val fileBlockSize = it.first()

            if (fileBlockSize == 0) {
                error("Zero sized file block. WTF?")
            }

            if (it.first() > 0) {
                add(
                    FileBlock(
                        id = id,
                        start = address,
                        size = it.first()
                    )
                )

                address += it.first()
            }
            id += 1

            if (it.size == 2) {
                if (it.last() > 0) {
                    address += it.last()
                }
            }
        }
    }
}
