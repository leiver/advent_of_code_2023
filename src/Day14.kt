import Direction.*

fun main() {

    data class Platform(val xBounds: LongRange, val yBounds: LongRange, val platform: Map<Coordinate, Char>) {
        fun printMap() {
            platform.printMapWithDefaults(xBounds, yBounds, '.')
        }
    }

    fun Platform.findNorthBeamLoad(): Long =
        platform
        .columns()
        .sumOf { (_, rocks) ->
            rocks
                .filter { it.second == 'O' }
                .sumOf { yBounds.last - it.first.y + 1 }
        }

    fun Platform.shiftInDirection(direction: Direction): Platform =
        Platform(
            xBounds, yBounds,
            when (direction) {
                NORTH -> platform.columns()
                SOUTH -> platform.columns().reversed()
                WEST -> platform.rows()
                else -> platform.rows().reversed()
            }.flatMap { (index, rocks) ->
                val bounds = when (direction) {
                    NORTH -> yBounds
                    SOUTH -> yBounds.flipped()
                    WEST -> xBounds
                    else -> xBounds.flipped()
                }
                val delta = direction.delta
                val directionAbs = delta.abs()
                when (direction) {
                    NORTH, WEST -> rocks
                    else -> rocks.reversed()
                }.scan(bounds.first + delta.sum() to '#') { (prevRockIndex, _), (coordinate, type) ->
                        if (type == 'O')
                            prevRockIndex + delta.invert().sum() to type
                        else
                            (directionAbs multiply coordinate).sum() to type
                    }
                    .drop(1)
                    .map { (newIndex, type) ->
                        Coordinate(
                            directionAbs.y * index + directionAbs.x * newIndex,
                            directionAbs.y * newIndex + directionAbs.x * index
                        ) to type
                    }
            }
                .associate { it }
        )

    fun Platform.runOneCycle(): Platform =
        shiftInDirection(NORTH)
            .shiftInDirection(WEST)
            .shiftInDirection(SOUTH)
            .shiftInDirection(EAST)


    fun Platform.runCycles(cycles: Long): Platform {
        val cache: MutableMap<String, Long> = mutableMapOf()
        return LongRange(1L, cycles)
            .fold(this) { prevPlatform, cycle ->
                val hashedPlatform = prevPlatform.platform.toString().md5()

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

    fun parseInput(input: List<String>): Platform =
        Platform(
            LongRange(0L, input.size - 1L),
            LongRange(0L, input[0].length - 1L),
            input
                .flatMapIndexed { y, row ->
                    row.mapIndexed { x, char -> Coordinate(x, y) to char }
                }
                .filter { it.second != '.' }
                .associate { it }
        )

    fun part1(input: List<String>): Long {
        return parseInput(input)
            .shiftInDirection(NORTH)
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
