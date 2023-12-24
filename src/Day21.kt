import java.util.Queue

fun main() {

    fun parseInput(input: List<String>): BoundedCoordinateMap<Char> {
        return BoundedCoordinateMap(
            input
                .flatMapIndexed { y: Int, line: String ->
                    line.mapIndexed { x, char ->
                        Coordinate(x, y) to char
                    }
                }
                .associate { it },
            LongRange(0, input[0].length - 1L),
            LongRange(0, input.size.toLong() - 1L)
        )
    }

    fun BoundedCoordinateMap<Char>.pathFind(steps: Long): List<Coordinate> {
        val startingPosition = map.entries.first { entry -> entry.value == 'S' }.key
        val visitedCoordinates: MutableMap<Coordinate, Long> = mutableMapOf(startingPosition to steps)
        val queue: ArrayDeque<Pair<Coordinate, Long>> = ArrayDeque()
        queue.add(startingPosition to steps)

        while (queue.isNotEmpty()) {
            val (currentPosition, stepsLeft) = queue.removeFirst()
            orthogonalNeighbours(currentPosition)
                .filter { neighbour -> neighbour.second.second == '.' }
                .forEach { neighbour ->
                    val moreStepsLeft = !visitedCoordinates.containsKey(neighbour.second.first)
                            || (stepsLeft - 1) > visitedCoordinates[neighbour.second.first]!!
                    if (moreStepsLeft) {
                        visitedCoordinates[neighbour.second.first] = stepsLeft - 1
                    }
                    if (stepsLeft - 1 > 0 && moreStepsLeft) {
                        queue.add(neighbour.second.first to stepsLeft - 1)
                    }
                }
        }

        return visitedCoordinates.filter { it.value % 2 == steps % 2 }.keys.toList()
    }

    fun part1(input: List<String>, steps: Long): Long {
        return parseInput(input)
            .pathFind(steps)
            .count()
            .toLong()
    }

    fun part2(input: List<String>): Long {
        val map = parseInput(input)
        val mapTilesOneDirection = 26501365.0 / (map.xBounds.length() + 1)
        val mapTilesSquareSide = Math.sqrt(Math.pow(mapTilesOneDirection, 2.0) * 2.0).toLong()
        mapTilesSquareSide.println()
        val mapTilescircumference = 4L * mapTilesSquareSide
        val mapTilesArea = mapTilesSquareSide * mapTilesSquareSide - mapTilescircumference

        val remainingSteps = 26501365 % (map.xBounds.length() + 1)
        val stepsCoveredOnEdge = map
            .pathFind(remainingSteps)
            .count()
        val gardenTiles = map.map.entries.count { it.value != '#' }
        return mapTilesArea * (gardenTiles / 2) + stepsCoveredOnEdge * (mapTilescircumference / 2) - stepsCoveredOnEdge * 2
    }

    val testInput = readInput("21", "test_part1")
    check(part1(testInput, 6) == 16L)

//    val testInput2 = readInput("21", "test_part2")
//    check(part2(testInput2) == 0L)

    part1(readInput("21", "input"), 64).println()
    part2(readInput("21", "input")).println()
}
