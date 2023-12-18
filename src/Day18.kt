import Direction.*
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalStdlibApi::class)
fun main() {

    data class Hole(val coordinate: Coordinate, val color: String) {

    }

    fun parseInput(input: List<String>): BoundedCoordinateMap<String> {
        return BoundedCoordinateMap(
            input
                .map { "(.) (\\d+) \\((#.{6})\\)".toRegex().matchEntire(it)!!.destructured }
                .scan(listOf(Hole(Coordinate(0, 0), "#ffffff"))) { acc, (directionChar, times, color) ->
                    val prevHole = acc.last()
                    val direction: Direction = when (directionChar) {
                        "U" -> NORTH
                        "L" -> WEST
                        "R" -> EAST
                        "D" -> SOUTH
                        else -> NORTH
                    }
                    IntRange(1, times.toInt())
                        .scan(prevHole.coordinate) { prev, _ ->
                            prev plus direction.delta
                        }
                        .drop(1)
                        .map { Hole(it, color) }
//                    prevHole
//                        .coordinate
//                        .allBetween(
//                            prevHole
//                                .coordinate
//                                .plus(direction.delta multiply times.toLong())
//                        )
//                        .drop(1)
//                        .map { Hole(it, color) }
                }
                .flatten()
                .associate { it.coordinate to "#" }
        )
    }

    fun parseInputPart2(input: List<String>): List<Pair<Coordinate, Coordinate>> {
        val instructions = input
            .map { ". \\d+ \\(#(.{6})\\)".toRegex().matchEntire(it)!!.destructured }
            .map { (color) ->
                when (color.last()) {
                    '3' -> NORTH
                    '2' -> WEST
                    '0' -> EAST
                    '1' -> SOUTH
                    else -> NORTH
                } to color.substring(0, 5).hexToLong()
            }
//        val coordinationRanges =
        return instructions
            .scan(
                Pair(Coordinate(0, 0), Coordinate(0, 0))
            )
            { prevCoordinates, (direction, times) ->
                var prevHole = prevCoordinates.second
//                val prevDirection = prevDirections.second

//                (prevDirection to direction) to
                prevHole to prevHole.plus(direction.delta.multiply(times))
            }
            .drop(1)
//        return coordinationRanges
//            .reversed()
//            .scan(
//                Triple(
//                    coordinationRanges.first().first.first,
//                    coordinationRanges.first().first.second,
//                    coordinationRanges.first().first.second
//                ) to coordinationRanges.first().second
//            ) { (prevDirections, _), (directions, coordinates) ->
//                Triple(
//                    directions.first,
//                    directions.second,
//                    prevDirections.second
//                ) to coordinates
//            }
//            .drop(1)
//            .reversed()

    }

    fun parseInputPart2V2(input: List<String>): List<Pair<Triple<Direction, Direction, Direction>, Pair<Coordinate, Coordinate>>> {
        val instructions = input
            .map { ". \\d+ \\(#(.{6})\\)".toRegex().matchEntire(it)!!.destructured }
            .map { (color) ->
                when (color.last()) {
                    '3' -> NORTH
                    '2' -> WEST
                    '0' -> EAST
                    '1' -> SOUTH
                    else -> NORTH
                } to color.substring(0, 5).hexToLong()
            }
        val coordinationRanges = instructions
            .scan(
                Pair(instructions.last().first, instructions.last().first) to Pair(Coordinate(0, 0), Coordinate(0, 0))
            )
            { (prevDirections, prevCoordinates), (direction, times) ->
                var prevHole = prevCoordinates.second
//                if (prevHole != Coordinate(0, 0)) {
//                    prevHole = prevHole.plus(direction.delta)
//                }
                val prevDirection = prevDirections.second

                (prevDirection to direction) to (prevHole to prevCoordinates.second.plus(direction.delta.multiply(times)))
            }
            .drop(1)
        return coordinationRanges
            .reversed()
            .scan(
                Triple(
                    coordinationRanges.first().first.first,
                    coordinationRanges.first().first.second,
                    coordinationRanges.first().first.second
                ) to coordinationRanges.first().second
            ) { (prevDirections, _), (directions, coordinates) ->
                Triple(
                    directions.first,
                    directions.second,
                    prevDirections.second
                ) to coordinates
            }
            .drop(1)
            .reversed()

    }

    fun part1(input: List<String>): Long {
        val map = parseInput(input)
//        map.printMapWithDefaults(" ")
        return map
            .rows()
            .map { (y, row) ->
                row.fold(mutableListOf<LongRange>()) { list, x ->
                    if (list.any { range -> range.last == x.first.x - 1 }) {
                        val last = list.removeLast()
                        list.add(LongRange(last.first, x.first.x))
                    } else {
                        list.add(LongRange(x.first.x, x.first.x))
                    }
                    list
                }
                    .flatMap { range ->
                        val start = if (map.map[Coordinate(range.first, y - 1)] != null) NORTH else SOUTH
                        val end = if (map.map[Coordinate(range.last, y - 1)] != null) NORTH else SOUTH

                        if (range.length() > 1 && start == end) {
                            val ranges = listOf(
                                LongRange(range.first, range.last - 1),
                                LongRange(range.last, range.last)
                            )
//                            ranges.println()
                            ranges
                        } else {
                            listOf(range)
                        }
                    }
                    .windowed(2, 2)
                    .sumOf { (first, second) ->
                        second.last - first.first + 1
                    }
            }
            .sum()
    }

    fun dostuff(
        first: Pair<Coordinate, Coordinate>,
        second: Pair<Coordinate, Coordinate>,
        third: Pair<Coordinate, Coordinate>,
        prevIteration: List<Pair<Coordinate, Coordinate>>
    )
            : Pair<Triple<Pair<Coordinate, Coordinate>, Pair<Coordinate, Coordinate>, Pair<Coordinate, Coordinate>>, Pair<LongRange, LongRange>?> {
        val (firstBoundX, firstBoundY) = first.first rangesBetween first.second
        val (secondBoundX, secondBoundY) = second.first rangesBetween second.second
        val (thirdBoundX, thirdBoundY) = third.first rangesBetween third.second
        if (thirdBoundX.contains(firstBoundX.first) || firstBoundX.contains(thirdBoundX.last)) {
            val xBounds = firstBoundX intersect secondBoundX
            val yBounds = secondBoundY

            val otherLines = prevIteration
                .filter { it != first && it != second && it != third }
            val isCleanSquare = otherLines
                .none { (start, end) ->
                    val (xBoundOther, yBoundOther) = start rangesBetween end
                    LongRange(xBoundOther.first + 1, xBoundOther.last - 1).overlap(xBounds)
                            && LongRange(yBoundOther.first + 1, yBoundOther.last - 1).overlap(yBounds)
                }

            val isInBounds = otherLines
                .none { it.first.rangesBetween(it.second).first.last > second.first.x }
                    || otherLines
                .none { it.first.rangesBetween(it.second).first.first < second.first.x }

            if (isCleanSquare && isInBounds) {
                return Triple(first, second, third) to Pair(xBounds, yBounds)
            }

        } else if (thirdBoundY.contains(firstBoundY.first) || firstBoundY.contains(thirdBoundY.last)) {
            val xBounds = secondBoundX
            val yBounds = firstBoundY intersect secondBoundY

            val otherLines = prevIteration
                .filter { it != first && it != second && it != third }
            val isCleanSquare = otherLines
                .none { (start, end) ->
                    val (xBoundOther, yBoundOther) = start rangesBetween end
                    LongRange(xBoundOther.first + 1, xBoundOther.last - 1).overlap(xBounds)
                            && LongRange(yBoundOther.first + 1, yBoundOther.last - 1).overlap(yBounds)
                }

            val isInBounds = otherLines
                .none { it.first.rangesBetween(it.second).second.last > second.first.y }
                    || otherLines
                .none { it.first.rangesBetween(it.second).second.first < second.first.y }

            if (isCleanSquare && isInBounds) {
                return Triple(first, second, third) to Pair(xBounds, yBounds)
            }
        }
        return Triple(first, second, third) to null
    }

    fun part2(input: List<String>): Long {
        val mapv2 = parseInputPart2V2(input)
//        mapv2.println()
        return LongRange(
            mapv2
                .minOf { (_, coords) -> min(coords.first.x, coords.second.x) },
            mapv2
                .maxOf { (_, coords) -> max(coords.first.x, coords.second.x) }
        )
            .sumOf { x ->
                val yRanges = mapv2
                    .map { (directions, coords) ->
                        var (xRange, yRange) = coords.first.rangesBetween(coords.second)
                        if (directions.second == EAST || directions.second == WEST) {
                            xRange = LongRange(
                                xRange.first + 1,
                                xRange.last - 1
                            )
                        }
                        directions to (xRange to yRange)
                    }
                    .filter { (_, ranges) -> ranges.first.contains(x) }
                    .sortedBy { (_, ranges) -> ranges.second.first }
                    .flatMap { (directions, ranges) ->
                        if ((directions.second == NORTH || directions.second == SOUTH) && directions.first != directions.third) {
                            listOf(
                                LongRange(ranges.second.first, ranges.second.last - 1),
                                LongRange(ranges.second.last, ranges.second.last)
                            )
                        } else {
                            listOf(ranges.second)
                        }
                    }
//                yRanges.println()
                yRanges
                    .windowed(2, 2)
                    .sumOf { (start, end) -> end.last - start.first + 1 }
            }
//        val map = parseInputPart2(input)
////        map.printMapWithDefaults(" ")
//
//        var area = 0L
//        var prevIteration = map
//        while (prevIteration.size >= 4) {
//            val (lines, square) = prevIteration
//                .windowed(3, 1)
//                .map { (first, second, third) -> dostuff(first, second, third, prevIteration) }
//                .first { it.second != null }
//        }
    }

    val testInput = readInput("18", "test_part1")
    check(part1(testInput) == 62L)

    val testInput2 = readInput("18", "test_part1")
    check(part2(testInput2) == 952408144115L)

    part1(readInput("18", "input")).println()
    part2(readInput("18", "input")).println()
}
