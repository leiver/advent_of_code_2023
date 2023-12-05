fun main() {

    data class Mapping(val source: LongRange, val destination: LongRange) {

        constructor(destination: String, source: String, length: String):
            this(
                LongRange(
                    source.toLong(),
                    source.toLong() + length.toLong()
                ),
                LongRange(
                    destination.toLong(),
                    destination.toLong() + length.toLong()
                )
            )

        fun intersectBasedOnSource(range: LongRange): Mapping {
            val newSource = source.intersect(range)

            val newDestination = LongRange(
                destination.first + newSource.first - source.first,
                destination.last + newSource.last - source.last
            )

            return Mapping(
                newSource,
                newDestination
            )
        }

        fun mapPointFromSource(point: Long): Long {
            return destination.first + point - source.first
        }

        fun overlapsWithSource(point: Long): Boolean {
            return source.contains(point)
        }

        fun overlapsWithSource(range: LongRange): Boolean {
            return source.overlap(range)
        }
    }

    data class SeedsAndMappings(val seeds: List<Long>, val orderedMappings: List<List<Mapping>>) {
        fun seedsAsRanges(): List<LongRange> = this.seeds.windowed(2, 2).map { LongRange(it[0], it[0] + it[1]) }
    }

    fun parseInput(input: List<String>): SeedsAndMappings {
        val sections = input
            .joinToString(";")
            .split(";;")

        val seeds = sections[0]
            .split(" ")
            .drop(1)
            .map { it.toLong() }

        val orderedMappings = sections
            .drop(1)
            .map {
                it
                    .split(";")
                    .drop(1)
                    .map { ranges -> "(\\d+) (\\d+) (\\d+)".toRegex().matchEntire(ranges)!!.destructured }
                    .map { (dest, src, length) -> Mapping(dest, src, length) }
            }

        return SeedsAndMappings(seeds, orderedMappings)
    }

    fun foldMappings(
        current: List<LongRange>,
        nextMap: List<Mapping>
    ): List<LongRange> {
        val unmodifiedRanges: List<LongRange> = current
            .flatMap { it.except(nextMap.map(Mapping::source)) }
        val modifiedRanges: List<LongRange> = current
            .flatMap {
                nextMap
                    .filter { mapping -> mapping.overlapsWithSource(it) }
                    .map { mapping -> mapping.intersectBasedOnSource(it) }
                    .map(Mapping::destination)
            }
        return unmodifiedRanges + modifiedRanges
    }

    fun foldMappings(
        current: List<Long>,
        nextMap: List<Mapping>
    ): List<Long> {
        return current
            .map { point ->
                nextMap
                    .filter { mapping -> mapping.overlapsWithSource(point) }
                    .firstOrNull()
                    ?.mapPointFromSource(point) ?: point
            }
    }

    fun part1(input: List<String>): Long {
        val seedsAndMappings = parseInput(input)
        return seedsAndMappings
            .orderedMappings
            .fold(seedsAndMappings.seeds, ::foldMappings)
            .min()
    }

    fun part2(input: List<String>): Long {
        val seedsAndMappings = parseInput(input)
        return seedsAndMappings
            .orderedMappings
            .fold(seedsAndMappings.seedsAsRanges(), ::foldMappings)
            .minOf(LongRange::first)
    }

    val testInput = readInput("05", "test")
    check(part1(testInput) == 35L)

    val testInput2 = readInput("05", "test")
    check(part2(testInput2) == 46L)

    part1(readInput("05", "input")).println()
    part2(readInput("05", "input")).println()
}
