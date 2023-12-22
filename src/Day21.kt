import java.util.Queue

fun main() {

    data class LinkedNode(val coordinate: Coordinate) {
        val shortestDistances: MutableMap<Coordinate, Long> = mutableMapOf()
    }

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

//    fun BoundedCoordinateMap<Char>.connectShortestPaths(): BoundedCoordinateMap<LinkedNode> {
//        val startNode = LinkedNode(Coordinate(0,0))
//        val queue: ArrayDeque<LinkedNode> = ArrayDeque()
//        queue.add(startNode)
//        val linkedNodeMap: MutableMap<Coordinate, LinkedNode> = mutableMapOf(startNode.coordinate to startNode)
//
//        while (queue.isNotEmpty()) {
//            val currentNode = queue.removeFirst()
//
//            orthogonalNeighbours(currentNode.coordinate)
//                .map { it.second }
//                .filter { it.second!! != '#' }
//                .map { (coordinate, _) ->
//                    if (linkedNodeMap.containsKey(coordinate)) {
//                        val existingNode = linkedNodeMap[coordinate]!!
//                        existingNode.shortestDistances.merge()
//                    }
//                    LinkedNode()
//                }
//        }
//    }

    fun BoundedCoordinateMap<Char>.pathFind(steps: Long): List<Coordinate> {
//        printMapWithDefaults(' ')
        val startingPosition = map.entries.first { entry -> entry.value == 'S' }.key
        val visitedCoordinates: MutableMap<Coordinate, Long> = mutableMapOf(startingPosition to steps)
        val queue: ArrayDeque<Pair<Coordinate, Long>> = ArrayDeque()
        queue.add(startingPosition to steps)

        while (queue.isNotEmpty()) {
            val (currentPosition, stepsLeft) = queue.removeFirst()
            orthogonalNeighbours(currentPosition)
                .filter { neighbour -> neighbour.second.second == '.' }
                .forEach { neighbour ->
//                    println("walking to ${neighbour.second.first} from ${currentPosition} with ${stepsLeft} steps left")
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
        return 0L
    }

    val testInput = readInput("21", "test_part1")
    part1(testInput, 6).println()
    check(part1(testInput, 6) == 16L)

    val testInput2 = readInput("21", "test_part2")
    check(part2(testInput2) == 0L)

    part1(readInput("21", "input"), 64).println()
    part2(readInput("21", "input")).println()
}
