fun main() {

    fun parseInput(input: String): Map<LongRange, LongRange> {
        if (input.startsWith("seeds:")) {
            return input
                .split(" ")
                .filter { !it.startsWith("seeds:") }
                .map { it.toLong() }
                .map { LongRange(it, it) }
                .associate { it to it }
        }
        return input
            .split(";")
            .filter { !it.contains("-to-") }
            .map { it.split(" ").map { number -> number.toLong() } }
            .associate { LongRange(it[1], it[1] + it[2]) to LongRange(it[0], it[0] + it[2]) }

    }

    fun parseInputPart2(input: String): Map<LongRange, LongRange> {
        if (input.startsWith("seeds:")) {
            return input
                .split(" ")
                .filter { !it.startsWith("seeds:") }
                .map { it.toLong() }
                .windowed(2, 2)
                .map { LongRange(it[0], it[0] + it[1]) }
                .associate { it to it }
        }
        return input
            .split(";")
            .filter { !it.contains("-to-") }
            .map { it.split(" ").map { number -> number.toLong() } }
            .associate { LongRange(it[1], it[1] + it[2]) to LongRange(it[0], it[0] + it[2]) }

    }

    fun part1(input: List<String>): Long {
        return input
            .joinToString(";")
            .split(";;")
            .map { parseInput(it) }
            .reduce{ acc, nextMap ->
                acc
                    .values
                    .map { it.first }
                    .map {
                        nextMap
                            .keys
                            .filter { range -> range.contains(it) }
                            .map { range ->
                                val length = it - range.first
                                nextMap[range]!!.first + length
                            }
                            .firstOrNull() ?: it
                    }
                    .map { LongRange(it, it) }
                    .associate { it to it }
            }
            .values
            .map { it.first }
            .min()
    }

    fun parseNewRanges(
        acc: Map<LongRange, LongRange>,
        nextMap: Map<LongRange, LongRange>
    ): List<LongRange> {
        val unmodifiedRanges: List<LongRange> = acc
            .values
            .flatMap { it.except(nextMap.keys) }
        val modifiedRanges = acc
            .values
            .flatMap {
                nextMap.keys
                    .filter { range -> range.overlap(it) }
                    .map { range ->
                        val intersection = range
                            .intersect(it)
                        LongRange(
                            nextMap[range]!!.first + intersection.first - range.first,
                            nextMap[range]!!.last + intersection.last - range.last
                        )
                    }
            }
        return unmodifiedRanges + modifiedRanges
    }

    fun part2(input: List<String>): Long {
        return input
            .joinToString(";")
            .split(";;")
            .map { parseInputPart2(it) }
            .reduceIndexed{ index, acc, nextMap ->
                parseNewRanges(acc, nextMap)
                    .associate { it to it }
            }
            .values
            .map { it.first }
            .min()
    }

    val testInput = readInput("05", "test")
    check(part1(testInput) == 35L)

    val testInput2 = readInput("05", "test")
    check(part2(testInput2) == 46L)

    part1(readInput("05", "input")).println()
    part2(readInput("05", "input")).println()
}
