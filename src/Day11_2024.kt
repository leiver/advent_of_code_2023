import kotlin.math.max

fun main() {

    var memory: HashMap<Pair<Long, Int>, Long> = HashMap()

    fun blink(stone: Long, step: Int, maxSteps: Int): Long {
        if (step == maxSteps) {
            return if (stone.toString().length % 2 == 0) 2 else 1
        }
        var memoryLookup = memory[Pair(stone, maxSteps - step)]
        if (memoryLookup != null) {
            return memoryLookup
        }
        var result = if (stone == 0L) {
            blink(1L, step+1, maxSteps)
        } else if (stone.toString().length % 2 == 0) {
            val stoneString = stone.toString()
            var firstHalf = stoneString.substring(0, stoneString.length / 2).toLong()
            var secondHalf = stoneString.substring(stoneString.length / 2, stoneString.length).toLong()
            blink(firstHalf, step+1, maxSteps) + blink(secondHalf, step+1, maxSteps)
        } else {
            blink(stone * 2024, step+1, maxSteps)
        }
        memory[Pair(stone, maxSteps - step)] = result
        return result
    }

    fun part1(input: List<String>): Long {
        return input
            .first().split(" ")
            .map { blink(it.toLong(), 1, 25) }
            .sum()
    }

    fun part2(input: List<String>): Long {
        return input
            .first().split(" ")
            .map { blink(it.toLong(), 1, 75) }
            .sum()
    }

//    val testInput = readInput("11", "test_part1")
//    check(part1(testInput) == 374L)

    part1(readInput("11", "input_2024")).println()
    part2(readInput("11", "input_2024")).println()
}
