import Direction.*

fun main() {

    fun BoundedCoordinateMap<Char>.findNorthBeamLoad(): Long =
        columns()
        .sumOf { (_, rocks) ->
            rocks
                .filter { it.second == 'O' }
                .sumOf { yBounds.last - it.first.y + 1 }
        }

    fun BoundedCoordinateMap<Char>.shiftNorth(): BoundedCoordinateMap<Char> =
        BoundedCoordinateMap(
            columns()
                .flatMap { (x, rocks) ->
                    rocks.scan(-1L to '#') { (prevRockIndex, _), (coordinate, type) ->
                        if (type == 'O')
                            prevRockIndex + 1L to type
                        else
                            coordinate.y to type
                    }
                        .drop(1)
                        .map { (y, type) ->
                            Coordinate(
                                x,
                                y
                            ) to type
                        }
                }
                .associate { it },
            xBounds,
            yBounds
        )

    fun BoundedCoordinateMap<Char>.runOneCycle(): BoundedCoordinateMap<Char> =
        shiftNorth()
            .rotateClockwise90()
            .shiftNorth()
            .rotateClockwise90()
            .shiftNorth()
            .rotateClockwise90()
            .shiftNorth()
            .rotateClockwise90()


    fun BoundedCoordinateMap<Char>.runCycles(cycles: Long): BoundedCoordinateMap<Char> {
        val cache: MutableMap<String, Long> = mutableMapOf()
        return LongRange(1L, cycles)
            .fold(this) { prevPlatform, cycle ->
                val hashedPlatform = prevPlatform.map.toString().md5()

                if (hashedPlatform in cache) {
                    val loopLength = cycle - 1 - cache[hashedPlatform]!!
                    val remainingtoParse = (cycles - cache[hashedPlatform]!!) % loopLength
                    val range = LongRange(cache[hashedPlatform]!! + 1L, cache[hashedPlatform]!! + remainingtoParse)
                    return@runCycles range
                        .fold(prevPlatform) { prevPlatform2, _ ->
                            prevPlatform2.runOneCycle()
                        }
                } else cache[hashedPlatform] = cycle - 1

                prevPlatform.runOneCycle()
            }
    }

    fun parseInput(input: List<String>): BoundedCoordinateMap<Char> =
        BoundedCoordinateMap(
            input
                .flatMapIndexed { y, row ->
                    row.mapIndexed { x, char -> Coordinate(x, y) to char }
                }
                .filter { it.second != '.' }
                .associate { it },
            LongRange(0L, input[0].length - 1L),
            LongRange(0L, input.size - 1L)
        )

    fun part1(input: List<String>): Long {
        return parseInput(input)
            .shiftNorth()
            .findNorthBeamLoad()
    }

    fun part2(input: List<String>): Long {
        return parseInput(input)
            .runCycles(1000000000)
            .findNorthBeamLoad()
    }

    val testInput = readInput("14", "test_part1")
    check(part1(testInput) == 136L)

    val testInput2 = readInput("14", "test_part1")
    check(part2(testInput2) == 64L)

    part1(readInput("14", "input")).println()
    part2(readInput("14", "input")).println()
}
