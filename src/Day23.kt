import Direction.*
import java.util.Stack
import kotlin.math.max

fun main() {

    fun parseInput(input: List<String>): BoundedCoordinateMap<Char> {
        return BoundedCoordinateMap(
            input
            .flatMapIndexed { y: Int, line: String ->
                line.mapIndexed { x, char ->
                    Coordinate(x, y) to char
                }
            }
            .filter { it.second != '#' }
            .associate { it }
        )
    }

    fun BoundedCoordinateMap<Char>.reduceGraph(): Map<Coordinate, List<Pair<Coordinate, Long>>> {
        val start = map.entries.first { entry -> entry.key.y == 0L }.key

        val parsedNodes = mutableMapOf<Coordinate, List<Pair<Coordinate, Long>>>()
        val queue = ArrayDeque<Coordinate>()
        queue.add(start)

        while (queue.isNotEmpty()) {
            val currentNode = queue.removeFirst()

            val neighbourNodes = orthogonalNeighbours(currentNode)
                .filter { it.second.second != null }
                .map { (direction, tile) ->
                    var currentDirection = direction
                    var currentTile = tile.first
                    var length = 1L
                    while (orthogonalNeighbours(currentTile).filter { it.second.second != null }.size == 2) {
                        val nextNode = orthogonalNeighbours(currentTile)
                            .filter { it.second.second != null }
                            .first { it.first.opposite() != currentDirection }
                        currentTile = nextNode.second.first
                        currentDirection = nextNode.first
                        length++
                    }
                    currentTile to length
                }

            parsedNodes[currentNode] = neighbourNodes

            neighbourNodes
                .filter { !parsedNodes.containsKey(it.first) }
                .filter { !queue.contains(it.first) }
                .forEach { queue.add(it.first) }
        }

        return parsedNodes
    }

    fun Map<Coordinate, List<Pair<Coordinate, Long>>>.findLongestPath(): Long {
        val start = entries.first { entry -> entry.key.y == 0L }.key
        val end = entries.maxBy { entry -> entry.key.y }.key

        val queue = ArrayDeque<Pair<List<Coordinate>, Long>>()
        queue.add(listOf(start) to 0L)

        var longestPath = 0L
        while (queue.isNotEmpty()) {
            val (visitedNodes, pathLength) = queue.removeFirst()
            val currentNode = visitedNodes.last()
            val neighbours: List<Pair<Coordinate, Long>> = get(currentNode)!!
            neighbours
                .filter { !visitedNodes.contains(it.first) }
                .forEach {
                    if (it.first == end) {
                        longestPath = max(longestPath, pathLength + it.second)
                    } else {
                        queue.add((visitedNodes + listOf(it.first)) to (pathLength + it.second))
                    }
                }
        }
        return longestPath
    }

    fun BoundedCoordinateMap<Char>.findLongestPath(slipperySlopes: Boolean): Long {

        val start = map.entries.first { entry -> entry.key.y == 0L }.key
        val stack = Stack<List<Coordinate>>()
        stack.add(listOf(start))

        var longestPath = 0L
        while (stack.isNotEmpty()) {
            val visitedNodes = stack.pop()
            val currentNode = visitedNodes.last()

            orthogonalNeighbours(currentNode)
                .filter { (direction, entry) ->
                    if (slipperySlopes) {
                        when (entry.second) {
                            '^' -> direction == NORTH
                            '>' -> direction == EAST
                            'v' -> direction == SOUTH
                            '<' -> direction == WEST
                            null -> false
                            else -> true
                        }
                    } else {
                        entry.second != null
                    }
                }
                .filter { (_, entry) -> !visitedNodes.contains(entry.first) }
                .forEach { (_, entry) ->
                    if (entry.first.y == yBounds.last) {
                        longestPath = max(visitedNodes.size.toLong(), longestPath)
                    } else {
                        stack.add(visitedNodes + listOf(entry.first))
                    }
                }
        }

        return longestPath
    }

    fun part1(input: List<String>): Long {
        return parseInput(input)
            .findLongestPath(true)
    }

    fun part2(input: List<String>): Long {
        return parseInput(input)
            .reduceGraph()
            .findLongestPath()
    }

    val testInput = readInput("23", "test_part1")
    check(part1(testInput) == 94L)

    val testInput2 = readInput("23", "test_part1")
    check(part2(testInput2) == 154L)

    part1(readInput("23", "input")).println()
    part2(readInput("23", "input")).println()
}
