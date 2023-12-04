import kotlin.math.max
import kotlin.math.min

fun main() {

    data class ScratchCard(val winningNumbers: List<Int>, val cardNumbers: List<Int>)

    fun parseInput(input: List<String>) = input
        .map { it.split(": ")[1] }
        .map { it.split(" | ") }
        .map {
            it.map { numbers ->
                numbers
                    .split(" ")
                    .filter { number -> number.isNotEmpty() }
                    .map { number ->
                        number.strip().toInt()
                    }
            }
        }
        .map { ScratchCard(it[0], it[1]) }

    fun part1(input: List<String>): Int {
        return parseInput(input)
            .map {
                val winningNumbers = it.cardNumbers
                    .filter { number -> number in it.winningNumbers }
                if (winningNumbers.size > 0) {
                    Math.pow(2.0, winningNumbers.size.toDouble() - 1.0).toInt()
                } else {
                    0
                }
            }.sum()
    }

    fun getAmountOfCopies(copiesPerCard: List<IntRange>, card: IntRange): Int {
        return card
            .map { getAmountOfCopies(copiesPerCard, copiesPerCard[it]) }
            .sum() + 1
    }

    fun part2(input: List<String>): Int {
        val copiesPerCard = parseInput(input)
            .zip(input.indices)
            .map {
                val winningNumbers = it.first.cardNumbers
                    .filter { number -> number in it.first.winningNumbers }
                IntRange(it.second + 1, min(input.size, it.second + winningNumbers.size))
            }
        return copiesPerCard
            .map { getAmountOfCopies(copiesPerCard, it) }
            .sum()
    }

    val testInput = readInput("04", "test")
    check(part1(testInput) == 13)

    val testInput2 = readInput("04", "test")
    check(part2(testInput2) == 30)

    part1(readInput("04", "input")).println()
    part2(readInput("04", "input")).println()
}
