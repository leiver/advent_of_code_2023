import java.util.Comparator
import java.util.Comparator.comparing

fun main() {

    fun processNextAddAndMult(result: Long, remainingNumbers: List<Long>, function: (Long) -> Long): Boolean {
        val nextSum = function(remainingNumbers.first())
        if (nextSum >= result || remainingNumbers.size == 1) {
            return nextSum == result
        }
        val newList = remainingNumbers.drop(1).toList()
        return processNextAddAndMult(result, newList, { nextSum + it }) || processNextAddAndMult(result, newList, { nextSum * it })
    }

    fun processNextAddAndMultAndConcat(result: Long, remainingNumbers: List<Long>, function: (Long) -> Long, toStringFunction: (Long) -> String): Boolean {
        val nextSum = function(remainingNumbers.first())
        val nextString = toStringFunction(remainingNumbers.first())
        if (nextSum > result) {
            return false
        }
        if (remainingNumbers.size == 1) {
            return nextSum == result
        }
        val newList = remainingNumbers.drop(1).toList()
        if (processNextAddAndMultAndConcat(result, newList, { nextSum + it }, {"$nextString + $it"})) {
            return true
        } else if (processNextAddAndMultAndConcat(result, newList, { nextSum * it }, {"$nextString * $it"})) {
            return true
        } else {
            return processNextAddAndMultAndConcat(result, newList, { "$nextSum$it".toLong() }, {"$nextString || $it"})
        }
    }

    fun part1(input: List<String>): Long {
        return input
            .map { it.split(": ") }
            .map { (result, numbers) -> Pair(result.toLong(), numbers.split(" ").map { it.toLong() }.toList()) }
            .filter { (result, numbers) -> processNextAddAndMult(result, numbers, {it}) }
            .sumOf { it.first }
    }

    fun part2(input: List<String>): Long {
        return input
            .map { it.split(": ") }
            .map { (result, numbers) -> Pair(result.toLong(), numbers.split(" ").map { it.toLong() }.toList()) }
            .filter { (result, numbers) -> processNextAddAndMultAndConcat(result, numbers, {it}, {it.toString()}) }
            .sumOf { it.first }
    }

//    val testInput = readInput("07", "test")
//    check(part1(testInput) == 6440)
//
    val testInput2 = readInput("07", "test_2024")
    check(part2(testInput2) == 11387L)

    part1(readInput("07", "input_2024")).println()
    part2(readInput("07", "input_2024")).println()
}
