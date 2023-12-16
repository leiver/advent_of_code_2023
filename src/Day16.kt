import Direction.*
import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Mirror(val inputOutputMapping: Map<Direction, List<Direction>>) {
        val visitedDirections: MutableList<Direction> = mutableListOf()

        constructor(input: Char) : this(
            when (input) {
                '/' -> mapOf(
                    NORTH to listOf(WEST),
                    WEST to listOf(NORTH),
                    EAST to listOf(SOUTH),
                    SOUTH to listOf(EAST)
                )

                '\\' -> mapOf(
                    NORTH to listOf(EAST),
                    EAST to listOf(NORTH),
                    WEST to listOf(SOUTH),
                    SOUTH to listOf(WEST)
                )

                '-' -> mapOf(
                    NORTH to listOf(WEST, EAST),
                    SOUTH to listOf(WEST, EAST),
                    EAST to listOf(WEST),
                    WEST to listOf(EAST)
                )

                '|' -> mapOf(
                    EAST to listOf(NORTH, SOUTH),
                    WEST to listOf(NORTH, SOUTH),
                    NORTH to listOf(SOUTH),
                    SOUTH to listOf(NORTH)
                )

                else -> mapOf()
            }
        )

        fun directionIsVisited(direction: Direction): Boolean {
            return visitedDirections.contains(direction.opposite());
        }

        fun reset() {
            visitedDirections.clear()
        }

        fun directBeam(direction: Direction): List<Direction> {
            visitedDirections.add(direction.opposite())
            return inputOutputMapping[direction.opposite()]!!
        }

    }

    data class Beam(var position: Coordinate, var direction: Direction) {
        val visitedPositions: MutableMap<Long, MutableList<LongRange>> = mutableMapOf()

        fun goTo(newPosition: Coordinate, newDirection: Direction) {
            if (direction.delta.x != 0L) {
                LongRange(min(position.x, newPosition.x), max(position.x, newPosition.x))
                    .forEach { visitedX ->
                        visitedPositions
                            .merge(visitedX, mutableListOf(LongRange(position.y, position.y))) { old, new ->
                                old.addAll(new)
                                old
                            }
                    }
            } else {
                visitedPositions
                    .merge(
                        position.x,
                        mutableListOf(LongRange(min(position.y, newPosition.y), max(position.y, newPosition.y)))
                    ) { old, new ->
                        old.addAll(new)
                        old
                    }
            }
            position = newPosition
            direction = newDirection
        }
    }

    class BeamCache() {
        val cache: MutableMap<Pair<Coordinate, Direction>, List<Beam>> = mutableMapOf()

        fun reset() {
            cache.clear()
        }
    }

    fun trackBeam(beam: Beam, mirrorMap: BoundedCoordinateMap<Mirror>, cache: BeamCache): List<Beam> {
//        println("tracking beam from ${beam.position} in direction ${beam.direction}")
//        println("continuing beam from ${beam.position} in direction ${beam.direction}")
        val startPosition = beam.position
        val startDirection = beam.direction
        if (cache.cache.contains(beam.position to beam.direction)) {
            return concatenate(
                listOf(beam),
                cache.cache[beam.position to beam.direction]!!
            )
        }
        val (newPosition, mirror) = mirrorMap
            .nextInDirection(beam.position, beam.direction)
        if (mirror == null) {
            beam.goTo(newPosition, beam.direction)
//            println("hit wall! visitedPosition: ${beam.visitedPositions}")
            return listOf(beam)
        }
        val directionAlreadyVisited = mirror.directionIsVisited(beam.direction)
        val newDirections = mirror
            .directBeam(beam.direction)

        if (directionAlreadyVisited) {
//            println("had already visited ${newPosition} in direction ${beam.direction}")
            beam.goTo(newPosition, newDirections.first())
            return listOf(beam)
        }
        beam.goTo(newPosition, newDirections.first())
//        println("splitting in directions ${newDirections} from ${newPosition}")
        val splitBeams = newDirections
            .drop(1)
            .map { newDirection -> Beam(newPosition, newDirection) }
            .flatMap { newBeam -> trackBeam(newBeam, mirrorMap, cache) }
            .toMutableList()
        splitBeams.addAll(trackBeam(Beam(beam.position, beam.direction), mirrorMap, cache))
        cache.cache[startPosition to startDirection] = splitBeams
        splitBeams.add(beam)
        return splitBeams
    }

    fun parseInput(input: List<String>): BoundedCoordinateMap<Mirror> {
        return BoundedCoordinateMap(
            input
                .flatMapIndexed { y, line ->
                    line
                        .mapIndexed { x, char ->
                            Coordinate(x, y) to char
                        }
                        .filter { (_, char) ->
                            char != '.'
                        }
                        .map { (coordinate, char) ->
                            coordinate to Mirror(char)
                        }
                }
                .associate { it },
            LongRange(0, input[0].length.toLong() - 1L),
            LongRange(0, input.size.toLong() - 1L)
        )
    }

    fun part1(input: List<String>): Long {
        return trackBeam(
            Beam(Coordinate(-1, 0), EAST),
            parseInput(input),
            BeamCache()
        )
            .flatMap { beam ->
                beam.visitedPositions
                    .flatMap { (x, yRanges) ->
                        yRanges
                            .flatMap { yRange -> yRange.map { y -> Coordinate(x, y) } }
                    }
            }
            .toSet()
            .count()
            .toLong() - 1
    }

    fun part2(input: List<String>): Long {
        val mirrorMap: BoundedCoordinateMap<Mirror> = parseInput(input)
        val cache = BeamCache()
        return concatenate(
            mirrorMap
                .xBounds
                .flatMap { x ->
                    listOf(
                        Beam(Coordinate(x, mirrorMap.yBounds.first - 1), SOUTH),
                        Beam(Coordinate(x, mirrorMap.yBounds.last + 1), NORTH)
                    )
                },
            mirrorMap
                .yBounds
                .flatMap { y ->
                    listOf(
                        Beam(Coordinate(mirrorMap.xBounds.first - 1, y), EAST),
                        Beam(Coordinate(mirrorMap.xBounds.last + 1, y), WEST)
                    )
                }
        )
            .map { beam ->
                mirrorMap.map.values.forEach(Mirror::reset)
                cache.reset()
                trackBeam(beam, mirrorMap, cache)
            }
            .map {
                it.flatMap { beam ->
                    beam.visitedPositions
                        .flatMap { (x, yRanges) ->
                            yRanges
                                .flatMap { yRange -> yRange.map { y -> Coordinate(x, y) } }
                        }
                }
                    .toSet()
                    .count()
                    .toLong() - 1
            }
            .max()
    }

    val testInput = readInput("16", "test_part1")
    part1(testInput).println()
    check(part1(testInput) == 46L)

    val testInput2 = readInput("16", "test_part1")
    check(part2(testInput2) == 51L)

    part1(readInput("16", "input")).println()
    part2(readInput("16", "input")).println()
}
