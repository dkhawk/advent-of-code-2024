package day09

import kotlinx.coroutines.runBlocking
import utils.COLORS
import utils.println
import utils.readText
import java.math.BigInteger
import java.util.*

val testInput = """
    12345
""".trimIndent()

val testInput2 = """
    2333133121414131402
""".trimIndent()

val expected = """
    00...111...2...333.44.5555.6666.777.888899    
    00...111...2...333.44.5555.6666.777.888899

    0099.111...2...333.44.5555.6666.777.8888..
    0099.111...2...333.44.5555.6666.777.8888..

    0099.1117772...333.44.5555.6666.....8888..
    0099.1117772...333.44.5555.6666.....8888..

    0099.111777244.333....5555.6666.....8888..
    0099.111777244.333....5555.6666.....8888..

    00992111777.44.333....5555.6666.....8888..
    00992111777.44.333....5555.6666.....8888..
""".trimIndent()

fun main() = runBlocking {
//    check(part1(testInput2) == 1928L)
    check(part2(testInput2) == BigInteger.valueOf( 2858))

    val input = readText("inputs/09")
//    part1(input).println()
    part2(input).println()
    "9628495393 is too low".println()
    "10783467966 is too low".println()
}

fun part1(input: String): Long {
    val memory = createMemory(
        input.map { it - '0' }
    )
    defrag(memory)
    return checksum(memory)
}

private fun memoryDump(memory: MutableList<Int>): String {
    return memory.joinToString("") {
        if (it < 0) "." else it.toString()
    }
}

private fun checksum(memory: List<Int>): Long {
    return memory.mapIndexed { index, i ->
        if (i <= 0) {
            0
        } else {
            index * i.toLong()
        }
    }.sum()
}

fun createMemory(map: List<Int>): MutableList<Int> {
    val result = mutableListOf<Int>()

    var id = 0

    map.windowed(2,2, partialWindows = true).forEach {
        repeat(it.first()) { result.add(id) }
        if (it.size == 2) repeat(it[1]) { result.add(-1) }
        id += 1
    }

    return result
}

fun defrag(memory: MutableList<Int>) {
    var tail = memory.indices.last
    var head = 0

//    var steps = 200

//    val pointersline = mutableListOf<Char>()
//    repeat(memory.size) {
//        pointersline.add('.')
//    }

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
    val memory = createMemory3(
        input.map { it - '0' }
    )

    val memorySize = memory.last.let { it.size + it.start }

//    memoryDump3(memory, memorySize).also {
//        it.println()
////        check(it == "00...111...2...333.44.5555.6666.777.888899")
//    }

    defrag4(memory, memorySize)

//    memoryDump3(memory, memorySize).also {
//        it.println()
////        check(it == "00...111...2...333.44.5555.6666.777.888899")
//    }
    return checksum2(memory)
}

fun checksum2(memory: SortedSet<FileBlock>) : BigInteger {
    return memory.fold(BigInteger.ZERO) { acc, block ->
        acc + (block.start..<block.end).fold(BigInteger.ZERO) { acc, index ->
            acc + BigInteger.valueOf(index.toLong() * block.id.toLong())
        }
    }
}

fun memoryDump3(
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

fun defrag4(memory: SortedSet<FileBlock>, memorySize: Int): SortedSet<FileBlock> {
//    println()
//
//    memory.forEach { fileBlock ->
//        fileBlock.println()
//    }
//
//    println()

    // Create buckets
//    val maxBlockSize = 9
//    val buckets = List(maxBlockSize + 1) { sortedSetOf<FileBlock>(kotlin.Comparator { o1, o2 -> o2.compareTo(o1) }) }
//
//    memory.forEach { block ->
//        ((block.size)..(maxBlockSize)).forEach { bs ->
//            buckets[bs].add(block)
//        }
//    }

    val blocksQueue = memory.toSortedSet { o1, o2 -> o2.id.compareTo(o1.id) }

//    blocksQueue.take(5).println()
//    blocksQueue.reversed().take(5).println()
//
//    return memory

    while (blocksQueue.isNotEmpty()) {
        val next = blocksQueue.removeFirst()

        if (blocksQueue.isEmpty()) {
            // Don't move the last block...
            break
        }

        memory.takeWhile { it.start <= next.start }.windowed(2, 1).firstOrNull { (a, b) ->
            b.start - a.end >= next.size
        }?.first()?.end?.let { newStart ->
            if (newStart > next.start) {
                error("Attempted to move $next to $newStart")
            }

            val newBlock = next.copy(start = newStart)
            memory.remove(next)
            memory.add(newBlock)
//            memoryDump3(memory, memorySize).println()
        }

//        memory.windowed(2, 1).firstOrNull { (a, b) ->
//            a.end < next.start && b.start - a.end >= next.size
//        }?.first()?.end?.let { newStart ->
//            if (newStart > next.start) {
//                error("Attempted to move $next to $newStart")
//            }
//
//            val newBlock = next.copy(start = newStart)
//            memory.remove(next)
//            memory.add(newBlock)
//        }
    }

    return memory


//    buckets.forEachIndexed { index, bucket ->
//        "${index} -> ${bucket}".println()
//    }
//
//    return memory
//
//    val spaces = memory.windowed(2, 1).mapNotNull { (a, b) ->
//        if (a.end < b.start) {
//            a.end .. b.start
//        } else null
//    }.toSet()
//
//    spaces.forEach { space ->
////        "Filling $space".println()
//        val size = space.last - space.first
//
//        var spaceToFill = size
//
//        while (spaceToFill > 0) {
////            "Looking for block to fill $spaceToFill".println()
//            val block = getNextCandidate(buckets, spaceToFill)
//            if (block != null) {
////                "Block found $block".println()
//                val newBlock = block.copy(start = space.last- spaceToFill)
//                memory.add(newBlock)
//                memory.remove(block)
//                spaceToFill -= block.size
//            } else {
////                "No block found for $spaceToFill".println()
//                break
//            }
//        }
//    }
//
//    return memory
}

fun getNextCandidate(buckets: List<SortedSet<FileBlock>>, spaceToFill: Int): FileBlock? {
    var bucketNumber = spaceToFill

    while (true) {
        if (buckets[bucketNumber].isNotEmpty()) {
            val block = buckets[bucketNumber].first()
            remove(block, buckets)
            return block
        }

        bucketNumber -= 1
        if (bucketNumber < 1) {
            return null
        }
    }
}

fun remove(block: FileBlock?, buckets: List<SortedSet<FileBlock>>) {
    buckets.forEach { bucket -> bucket.remove(block) }
}

sealed interface Block : Comparable<Block> {
    val size: Int
    val start: Int

    val end: Int
        get() { return start + size }

    override operator fun compareTo(other: Block) = compareValuesBy(this, other) { it.start }
}

data class FileBlock(val id: Int, override val start: Int, override val size: Int) : Block
data class EmptyBlock(override val start: Int, override val size: Int) : Block

fun createMemory2(map: List<Int>): SortedSet<Block> {
    var id = 0
    val blocks = mutableListOf<Block>()
    var address = 0

    map.windowed(2,2, partialWindows = true).forEach {
        if (it.first() > 0) {
            blocks.add(
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
                blocks.add(
                    EmptyBlock(
                        start = address,
                        size = it.last()
                    )
                )
                address += it.last()
            }
        }
    }

    return sortedSetOf<Block>().apply { addAll(blocks) }
}

fun createMemory3(map: List<Int>): SortedSet<FileBlock> {
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

fun memoryDump2(memory: Collection<Block>) : String {
    val size = memory.sumOf { it.size }

    val mem = Array(size) { '.' }

    memory.forEach { block ->
        when (block) {
            is FileBlock -> { repeat(block.size) { mem[block.start + it] = '0' + block.id } }
            is EmptyBlock -> {}
        }
    }

    return mem.joinToString("")
}

//class Node(
//    val block: Block,
//    var previous: Node? = null,
//    var next: Node? = null,
//)
//
//class MyLinkedList{
//    lateinit var head: Node
//    lateinit var tail: Node
//
//    fun add(block: Block) {
//        if (!this::head.isInitialized) {
//            head = Node(block = block, previous = null, next = null)
//            tail = head
//        } else {
//            val oldTail = tail
//            tail = Node(block = block, previous = oldTail, next = head)
//            oldTail.next = tail
//        }
//    }
//
//    fun remove(block: Block) {
//        check(this::head.isInitialized)
//        findOrNull(block)
//    }
//}

fun defrag3(memory: SortedSet<Block>, maxFilesToMove: Int) {
    val filesMoved = mutableSetOf<Int>()

    val memoryIterator = memory.iterator()

    while (filesMoved.size < maxFilesToMove) {

//        for (block in memory) {
//            when (block) {
//                is FileBlock -> { "file: $block".println() }
//                is EmptyBlock -> { "empty: $block".println() }
//            }
//        }

        if (!memoryIterator.hasNext()) {
            break
        }

        var emptyBlockToReplace = memoryIterator.next()
        // Find the next empty block
        while (emptyBlockToReplace is FileBlock && memoryIterator.hasNext()) {
            emptyBlockToReplace = memoryIterator.next()
        }

        // Starting from the back, find the next file block that will fit
        val blockToMove = memory.reversed().firstOrNull { block ->
            block is FileBlock && block.size <= emptyBlockToReplace.size
        } as FileBlock?

        if (blockToMove == null) {
            // No block is small enough
            continue
        }

        val target = emptyBlockToReplace

        // Create a new block at the new location
        val movedBlock = FileBlock(
            id = blockToMove.id,
            size = blockToMove.size,
            start = target.start,
        )

        // Create a new empty block at the old location
        val newEmptySpace = EmptyBlock(
            start = blockToMove.start,
            size = blockToMove.size,
        )

        if (movedBlock.size < emptyBlockToReplace.size) {


        }

        // Create a new empty block after the new location
        val splitSpace = EmptyBlock(
            start = movedBlock.end,
            size = target.size - movedBlock.size,
        )

        memory.remove(target)
        memory.remove(movedBlock)

        memory.add(movedBlock)
        if (splitSpace.size > 0) memory.add(splitSpace)
        if (newEmptySpace.size > 0) memory.add(newEmptySpace)

//        healMap(memory)

        break
    }
}

fun healMap(memory: MutableList<Block>) {
    val iterator = memory.iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next is EmptyBlock && next.size <= 0) {}
    }
}


fun defrag2(memory: MutableList<Int>) {
    var head = 0
    val movedFiles = mutableSetOf<Int>()
    var steps = 5

    val pointersline = mutableListOf<Char>()
    repeat(memory.size) {
        pointersline.add('.')
    }

    while (true) {
        memoryDump(memory).println()
        for (index in pointersline.indices) {
            pointersline[index] = '.'
        }
        pointersline[head] = 'H'
//        pointersline[tail] = 'T'

//        if (head == tail) {
//            pointersline[head] = '*'
//        }
        pointersline.joinToString("").println()

        // Find the next open block
        while (memory[head] >= 0) {
            head += 1
        }

        var emptySpaceSize = 0
        // How big is this spot?
        while (memory[head + emptySpaceSize] < 0) {
            emptySpaceSize++
        }

        // Find the next file
        var tail = memory.indices.last

        // This could be easier!!!
        val reversedMemory = memory.asReversed()
        var fileStart = 0

        while (true) {
            // skip all empty spaces
            while (memory[fileStart] < 0) {

            }

            // Skip any empty space
            while (tail > head + emptySpaceSize && memory[tail] < 0) {
                tail -= 1
            }

            val fileId = memory[tail]
            val fileEnd = tail + 1

            while (tail > head + emptySpaceSize && memory[tail] == fileId) {
                tail -= 1
            }
            tail += 1

            if (tail <= head + emptySpaceSize) {
                break
            }

            var fileSize = 0
            while (memory[tail - fileSize] > 0) {
                fileSize++
            }

            memory.withIndex().joinToString("") { (index, value) ->
                val highlight = when {
                    index in head..(head + emptySpaceSize) -> { COLORS.GREEN }
                    index in (tail - fileSize)..(tail) -> { COLORS.BLUE }
                    else -> { COLORS.NONE }
                }
                val s = if (value < 0) "." else value.toString()
                "$highlight$s"
            }.println()

            if (fileSize <= emptySpaceSize) {
                val fileId = memory[tail]
                // Move the file
                repeat(fileSize) {
                    memory[head + it] = fileId
                }

                // Clear the old space
                repeat(fileSize) {
                    memory[tail - it] = -1
                }

                movedFiles.add(fileId)
            } else {
                // Skip this file
                tail -= fileSize
                while (tail > head + emptySpaceSize && memory[tail - emptySpaceSize] < 0) {
                    tail -= 1
                }
            }
        }

        steps--
        if (steps <= 0) {
            error("Too many steps")
        }
    }
}