import java.util.Queue
import java.util.Stack

fun main() {


    fun part1(input: List<String>): Long {
        return 0L
//        val discStack = Stack<Pair<LongRange, Long>>()
//        val freeSpaceQueue = mutableListOf<LongRange>()
//        var currentBlockIndex = 0L
//        input
//            .first()
//            .split("")
//            .drop(1)
//            .dropLast(1)
//            .map { it.toInt() }
//            .forEachIndexed { index, block ->
//                if (index % 2 == 0) {
//                    discStack.add(Pair(LongRange(currentBlockIndex, currentBlockIndex + block-1), index / 2L))
//                } else {
//                    freeSpaceQueue.add(LongRange(currentBlockIndex, currentBlockIndex + block-1))
//                }
//                currentBlockIndex += block
//            }
//        var movedBlocks = mutableListOf<Pair<LongRange, Long>>()
//        while (discStack.isNotEmpty()) {
//            val nextBlockToMove = discStack.pop()
//            val leftmostFreeSpace = freeSpaceQueue
//                .filter { it.first < nextBlockToMove.first.first }
//                .filter { it.length() >= nextBlockToMove.first.length() }
//                .minByOrNull { it.first }
//            if (leftmostFreeSpace != null) {
//                movedBlocks.add(Pa)
//            }
//        }
//        return discStack.sumOf { it.first * it.second } + movedBlocks.sumOf { it.first * it.second }
    }

    fun part2(input: List<String>): Long {

        val discStack = Stack<Pair<LongRange, Long>>()
        val freeSpaceQueue = mutableListOf<LongRange>()
        var currentBlockIndex = 0L
        input
            .first()
            .split("")
            .drop(1)
            .dropLast(1)
            .map { it.toInt() }
            .forEachIndexed { index, block ->
                if (index % 2 == 0) {
                    discStack.add(Pair(LongRange(currentBlockIndex, currentBlockIndex + block-1), index / 2L))
                } else {
                    freeSpaceQueue.add(LongRange(currentBlockIndex, currentBlockIndex + block-1))
                }
                currentBlockIndex += block
            }
        var movedBlocks = mutableListOf<Pair<LongRange, Long>>()
        while (discStack.isNotEmpty()) {
            val nextBlockToMove = discStack.pop()
            val leftmostFreeSpace = freeSpaceQueue
                .filter { it.first < nextBlockToMove.first.first }
                .filter { it.length() >= nextBlockToMove.first.length() }
                .minByOrNull { it.first }
            if (leftmostFreeSpace != null) {
                val newBlockLocation = LongRange(leftmostFreeSpace.first, leftmostFreeSpace.first + nextBlockToMove.first.length())
//                println("moving block ${nextBlockToMove.second} from ${nextBlockToMove.first} to $newBlockLocation")
                movedBlocks.add(Pair(newBlockLocation, nextBlockToMove.second))
                freeSpaceQueue.remove(leftmostFreeSpace)
                if (leftmostFreeSpace.length() > nextBlockToMove.first.length()) {
                    freeSpaceQueue.add(LongRange(leftmostFreeSpace.first + nextBlockToMove.first.length()+1, leftmostFreeSpace.last))
                }
            } else {
                movedBlocks.add(nextBlockToMove)
            }
        }
        return movedBlocks.sumOf { it.first.sumOf { index -> index * it.second } }
    }

    val testInput = readInput("09", "test_2024")
    val testResult = part2(testInput)
//    println(testResult)
    check(testResult == 2858L)

//    part1(readInput("09", "input_2024")).println()
    part2(readInput("09", "input_2024")).println()
}
