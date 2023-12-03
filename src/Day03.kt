import kotlin.math.max
import kotlin.math.min

fun main() {

    data class IndexAndValue(val index: IntRange, val value: String)

    data class DigitsAndSymbols(val digitsPerRow: List<List<IndexAndValue>>, val symbolsPerRow: List<List<IndexAndValue>>)

    fun parseInput(input: List<String>): DigitsAndSymbols {
        val digitsPerRow = mutableListOf<List<IndexAndValue>>()
        val symbolsPerRow = mutableListOf<List<IndexAndValue>>()
        input
            .map {
                digitsPerRow.add(
                    "(\\d+)|([^\\.|\\d])".toRegex()
                        .findAll(it)
                        .filter { it.groupValues[1].isNotEmpty() }
                        .map { IndexAndValue(it.range, it.value) }
                        .toList()
                )
                symbolsPerRow.add(
                    "(\\d+)|([^\\.|\\d])".toRegex()
                        .findAll(it)
                        .filter { it.groupValues[2].isNotEmpty() }
                        .map { IndexAndValue(it.range, it.value) }
                        .toList()
                )
            }
        return DigitsAndSymbols(digitsPerRow, symbolsPerRow)
    }

    fun part1(input: List<String>): Int {
        val (digitsPerRow, symbolsPerRow) = parseInput(input)

        return digitsPerRow
            .zip(digitsPerRow.indices)
            .flatMap {
                it.first
                    .map { number -> Pair(number, it.second) }
            }.filter {
                val rangeX = IntRange(it.first.index.start - 1, it.first.index.endInclusive + 1)
                IntRange(max(0, it.second - 1), min(symbolsPerRow.size - 1, it.second + 1))
                    .filter { yIndex ->
                        symbolsPerRow[yIndex]
                            .filter { symbol -> symbol.index.first <= rangeX.last && symbol.index.last >= rangeX.first }
                            .isNotEmpty()
                    }.isNotEmpty()
            }.map { it.first.value.toInt() }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val (digitsPerRow, symbolsPerRow) = parseInput(input)

        return symbolsPerRow
            .zip(symbolsPerRow.indices)
            .flatMap {
                it.first
                    .map { symbol -> Pair(symbol, it.second) }
            }
            .filter { it.first.value.equals("*") }
            .map {
                val rangeX = IntRange(it.first.index.start - 1, it.first.index.endInclusive + 1)
                val adjacentNumbers = IntRange(max(0, it.second - 1), min(digitsPerRow.size - 1, it.second + 1))
                    .flatMap { yIndex ->
                        digitsPerRow[yIndex]
                            .filter { number -> number.index.first <= rangeX.last && number.index.last >= rangeX.first }
                    }
                    .map { number -> number.value.toInt() }
                if (adjacentNumbers.size == 2) {
                    adjacentNumbers[0] * adjacentNumbers[1]
                } else {
                    0
                }
            }.sum()
    }

    val testInput = readInput("03", "test")
    check(part1(testInput) == 4361)

    val testInput2 = readInput("03", "test")
    check(part2(testInput2) == 467835)

    part1(readInput("03", "input")).println()
    part2(readInput("03", "input")).println()
}
