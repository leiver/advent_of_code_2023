import Direction.*

fun main() {

    data class Pipe(val pipeType: Char, val coordinate: Coordinate, val connections: List<Coordinate>) {
        constructor(coordinate: Coordinate, pipe: Char) : this(
            pipe,
            coordinate,
            when (pipe) {
                '|' -> listOf(
                    coordinate neighbour NORTH,
                    coordinate neighbour SOUTH
                )

                '-' -> listOf(
                    coordinate neighbour WEST,
                    coordinate neighbour EAST
                )

                'L' -> listOf(
                    coordinate neighbour NORTH,
                    coordinate neighbour EAST
                )

                'J' -> listOf(
                    coordinate neighbour NORTH,
                    coordinate neighbour WEST
                )

                '7' -> listOf(
                    coordinate neighbour SOUTH,
                    coordinate neighbour WEST
                )

                'F' -> listOf(
                    coordinate neighbour SOUTH,
                    coordinate neighbour EAST
                )

                else -> {
                    listOf()
                }

            }
        )

        fun addConnections(connections: List<Coordinate>): Pipe =
            Pipe(
                pipeType,
                coordinate,
                connections
            )

        fun directions(): Pair<Direction, Direction> =
            Pair(
                coordinate directionTo connections[0],
                coordinate directionTo connections[1]
            )

        fun remapS(): Pipe =
            if (pipeType == 'S') {
                val pipetype = when (directions()) {
                    NORTH to SOUTH, SOUTH to NORTH -> '|'
                    EAST to WEST, WEST to EAST -> '-'
                    NORTH to WEST, WEST to NORTH -> 'J'
                    NORTH to EAST, EAST to NORTH -> 'L'
                    SOUTH to WEST, WEST to SOUTH -> '7'
                    SOUTH to EAST, EAST to SOUTH -> 'F'
                    else -> {
                        println("OOPSIE!")
                        '.'
                    }
                }
                Pipe(
                    coordinate,
                    pipetype
                )
            } else this

    }

    fun Map<Coordinate, Pipe>.addConnectionsToS(): Map<Coordinate, Pipe> {
        return mapValues { (coordinate, pipe) ->
            if (pipe.pipeType == 'S') {
                pipe
                    .addConnections(
                        coordinate
                            .orthogonalNeighbours()
                            .filter { get(it) != null }
                            .map { get(it)!! }
                            .filter { it.connections.contains(coordinate) }
                            .map(Pipe::coordinate)
                    )
            } else pipe
        }
    }

    fun parseInput(input: List<String>): Map<Coordinate, Pipe> {
        return input
            .flatMapIndexed { y, line ->
                line
                    .mapIndexed { x, pipe ->
                        Pair(Coordinate(x, y), pipe)
                    }
                    .filter { !it.second.equals('.') }
                    .map { (coordinate, pipe) -> Pipe(coordinate, pipe) }
            }
            .associateBy { it.coordinate }
            .addConnectionsToS()
    }

    fun findLoop(map: Map<Coordinate, Pipe>): List<Pipe> {
        var prevSection = map.values.first { it.pipeType == 'S' }
        var currentSection = map[prevSection.connections.first()]!!
        val loop = mutableListOf(currentSection)

        while (currentSection.pipeType != 'S') {
            val nextSection = map[
                currentSection
                    .connections
                    .first { it != prevSection.coordinate }
            ]!!
            prevSection = currentSection
            currentSection = nextSection
            loop.add(currentSection)
        }

        return loop
    }

    fun part1(input: List<String>): Int {
        return findLoop(
            parseInput(input)
        ).size / 2
    }

    fun findTilesInsideLoop(loop: List<Pipe>): Int {

        loop.map(Pipe::remapS)

        return LongRange(
            loop.minOf { it.coordinate.y },
            loop.maxOf { it.coordinate.y }
        )
            .sumOf { y ->
                val loopTilesInRow = loop
                    .filter { it.coordinate.y == y }
                    .sortedBy { it.coordinate.x }
                loopTilesInRow
                    .filter { it.pipeType != '-' }
                    .scan(Pipe('-', Coordinate(0, 0), listOf())) { prev, next ->
                        when (prev.pipeType.toString() + next.pipeType) {
                            "L7", "FJ" ->
                                Pipe(
                                    '-',
                                    next.coordinate,
                                    next.connections
                                )

                            else -> next
                        }
                    }
                    .filter { it.pipeType != '-' }
                    .windowed(2, 2)
                    .map { LongRange(it[0].coordinate.x + 1L, it[1].coordinate.x - 1L) }
                    .sumOf { xRange ->
                        xRange.count { x -> loopTilesInRow.none { pipe -> pipe.coordinate.x == x } }
                    }
            }
    }

    fun part2(input: List<String>): Int {
        return findTilesInsideLoop(
            findLoop(
                parseInput(input)
            )
        )
    }

    val testInput = readInput("10", "test_part1")
    check(part1(testInput) == 8)

    val testInput2 = readInput("10", "test_part2")
    check(part2(testInput2) == 8)

    part1(readInput("10", "input")).println()
    part2(readInput("10", "input")).println()
}
