import Direction.*
import java.util.PriorityQueue
import kotlin.math.max
import kotlin.math.min

fun main() {

    fun findPath(
        start: Coordinate,
        end: Coordinate,
        currentDirection: Direction,
        straightLineCount: Int,
        currentHeatLoss: Long,
        map: BoundedCoordinateMap<Long>,
        visitedTiles: List<Coordinate>
    ): Pair<Long, List<Coordinate>> {
        val mutableVisitedTiles = visitedTiles.toMutableList()
        mutableVisitedTiles.add(start)
        val newHeatLoss = currentHeatLoss + map.map[start]!!
        if (start == end) {
            return newHeatLoss to mutableVisitedTiles
        }
        return map
            .orthogonalNeighbours(start)
            .asSequence()
            .filter { (direction, _) -> direction != currentDirection.opposite() }
            .filter { (direction, _) -> straightLineCount < 3 || direction != currentDirection }
            .filter { (_, mapEntry) -> !visitedTiles.contains(mapEntry.first) }
            .map { (direction, mapEntry) ->
                findPath(
                    mapEntry.first,
                    end,
                    direction,
                    if (direction == currentDirection) straightLineCount + 1 else 0,
                    newHeatLoss,
                    map,
                    mutableVisitedTiles
                )
            }
            .filter { it.first != -1L }
            .minByOrNull { it.first } ?: (-1L to listOf())
    }

    data class QueueEntry(
        val coordinate: Coordinate,
        val heatLoss: Long,
        val direction: Direction,
        val straightLineCount: Int,
        val visitedTiles: List<Coordinate>,
        val distanceToEnd: Long
    )

    fun findShortestPath(
        start: Coordinate,
        end: Coordinate,
        map: BoundedCoordinateMap<Long>
    ): Pair<Long, List<Coordinate>> {

        val queue: PriorityQueue<QueueEntry> = PriorityQueue(compareBy { it.heatLoss + it.distanceToEnd })
        queue.add(
            QueueEntry(
                start,
                0,
                SOUTH_EAST,
                0,
                listOf(),
                start.manhattenDistance(end)
            )
        )
        val queueMemory: MutableMap<String, Long> = mutableMapOf(
            Triple(start, NORTH, 0).toString().md5() to 0,
            Triple(start, NORTH, 1).toString().md5() to 0,
            Triple(start, NORTH, 2).toString().md5() to 0,
            Triple(start, WEST, 0).toString().md5() to 0,
            Triple(start, WEST, 1).toString().md5() to 0,
            Triple(start, WEST, 2).toString().md5() to 0,
        )


        while (true) {
            val (coordinate, heatLoss, direction, straightLineCount, visitedTiles) = queue.remove()
            if (coordinate == end) {
                return heatLoss to visitedTiles
            }
            map
                .orthogonalNeighbours(coordinate)
                .asSequence()
                .filter { (nextDirection, _) -> nextDirection != direction.opposite() }
                .filter { (nextDirection, _) -> straightLineCount < 2 || nextDirection != direction }
                .map { (nextDirection, mapEntry) -> Pair(mapEntry.first, Triple(nextDirection, if (nextDirection == direction) straightLineCount + 1 else 0, heatLoss + mapEntry.second!!)) }
//                .filter { (nextDirection, mapEntry) ->
//                    queue.none { queueItem ->
//                        queueItem.coordinate == mapEntry.first
//                                && queueItem.direction == nextDirection
//                                && queueItem.straightLineCount == (if (nextDirection == direction) straightLineCount + 1 else 0)
//                                && queueItem.heatLoss < heatLoss + mapEntry.second!!
//                    }
//                }
                .filter { (nextCoordinate, nextValues) ->
                    val (nextDirection, nextStraightLineCount, nextHeatLoss) = nextValues
                    queueMemory.merge(Triple(nextCoordinate, nextDirection, nextStraightLineCount).toString().md5(), nextHeatLoss) { prevHeatLoss, currHeatLoss ->
                        min(prevHeatLoss, currHeatLoss)
                    }!! == nextHeatLoss
                }
                .forEach { (nextCoordinate, nextValues) ->
                    val (nextDirection, nextStraightLineCount, nextHeatLoss) = nextValues
                    //val nextVisitedTiles = visitedTiles.toMutableList()
                    //nextVisitedTiles.add(nextCoordinate)

                    queue.add(
                        QueueEntry(
                            nextCoordinate,
                            nextHeatLoss,
                            nextDirection,
                            nextStraightLineCount,
                            listOf(),
                            nextCoordinate manhattenDistance end
                        )
                    )
                }
        }
    }

    fun findShortestPathPart2(
        start: Coordinate,
        end: Coordinate,
        map: BoundedCoordinateMap<Long>
    ): Pair<Long, List<Coordinate>> {

        val queue: PriorityQueue<QueueEntry> = PriorityQueue(compareBy { it.heatLoss })
        queue.add(
            QueueEntry(
                start.plus(EAST.delta multiply 4L),
                map.allBetween(start plus EAST.delta, start.plus(EAST.delta multiply 4L)).sumOf { it.second },
                EAST,
                4,
                listOf(),
                start.plus(EAST.delta multiply 4L).manhattenDistance(end)
            )
        )
        queue.add(
            QueueEntry(
                start.plus(SOUTH.delta multiply 4L),
                map.allBetween(start plus SOUTH.delta, start.plus(SOUTH.delta multiply 4L)).sumOf { it.second },
                SOUTH,
                4,
                listOf(),
                start.plus(SOUTH.delta multiply 4L).manhattenDistance(end)
            )
        )

//        queue.println()
        val queueMemory: MutableMap<String, Long> = mutableMapOf(
            Triple(start, NORTH, 4).toString().md5() to 0,
            Triple(start, NORTH, 5).toString().md5() to 0,
            Triple(start, NORTH, 6).toString().md5() to 0,
            Triple(start, NORTH, 7).toString().md5() to 0,
            Triple(start, NORTH, 8).toString().md5() to 0,
            Triple(start, NORTH, 9).toString().md5() to 0,
            Triple(start, NORTH, 10).toString().md5() to 0,
            Triple(start, WEST, 4).toString().md5() to 0,
            Triple(start, WEST, 5).toString().md5() to 0,
            Triple(start, WEST, 6).toString().md5() to 0,
            Triple(start, WEST, 7).toString().md5() to 0,
            Triple(start, WEST, 8).toString().md5() to 0,
            Triple(start, WEST, 9).toString().md5() to 0,
            Triple(start, WEST, 10).toString().md5() to 0,
            Triple(start.plus(EAST.delta multiply 4L), EAST, 4).toString().md5() to map.allBetween(start plus EAST.delta, start.plus(EAST.delta multiply 4L)).sumOf { it.second },
            Triple(start.plus(SOUTH.delta multiply 4L), SOUTH, 4).toString().md5() to map.allBetween(start plus SOUTH.delta, start.plus(SOUTH.delta multiply 4L)).sumOf { it.second },
        )


        while (true) {
            val queueItem = queue.remove()
            val (coordinate, heatLoss, direction, straightLineCount, visitedTiles) = queueItem
            if (coordinate == end) {
                return heatLoss to visitedTiles
            }
            map
                .orthogonalNeighbours(coordinate)
                .asSequence()
                .filter { (nextDirection, _) -> nextDirection != direction.opposite() }
                .filter { (nextDirection, _) -> straightLineCount < 10 || nextDirection != direction }
                .filter { (nextDirection, mapEntry) -> nextDirection == direction || map.inbounds((nextDirection.delta multiply 3L) plus mapEntry.first) }
                .map { (nextDirection, mapEntry) ->
                    val fourSteps = (nextDirection.delta multiply 3L) plus mapEntry.first
                    Pair(
                        if (nextDirection != direction) fourSteps else mapEntry.first,
                        Triple(
                            nextDirection,
                            if (nextDirection == direction) straightLineCount + 1 else 4,
                            if (nextDirection != direction) heatLoss + map.allBetween(mapEntry.first, fourSteps).sumOf { it.second } else heatLoss + mapEntry.second!!
                        )
                    )
                }
//                .filter { (nextDirection, mapEntry) ->
//                    queue.none { queueItem ->
//                        queueItem.coordinate == mapEntry.first
//                                && queueItem.direction == nextDirection
//                                && queueItem.straightLineCount == (if (nextDirection == direction) straightLineCount + 1 else 0)
//                                && queueItem.heatLoss < heatLoss + mapEntry.second!!
//                    }
//                }
                .filter { (nextCoordinate, nextValues) ->
                    val (nextDirection, nextStraightLineCount, nextHeatLoss) = nextValues
                    queueMemory.merge(Triple(nextCoordinate, nextDirection, nextStraightLineCount).toString().md5(), nextHeatLoss) { prevHeatLoss, currHeatLoss ->
                        min(prevHeatLoss, currHeatLoss)
                    }!! == nextHeatLoss
                }
                .forEach { (nextCoordinate, nextValues) ->
                    val (nextDirection, nextStraightLineCount, nextHeatLoss) = nextValues
                    //val nextVisitedTiles = visitedTiles.toMutableList()
                    //nextVisitedTiles.add(nextCoordinate)

                    val queueElement = QueueEntry(
                        nextCoordinate,
                        nextHeatLoss,
                        nextDirection,
                        nextStraightLineCount,
                        listOf(),
                        nextCoordinate manhattenDistance end
                    )
//                    println("Queueing ${queueElement} from ${queueItem}")
                    queue.add(
                        queueElement
                    )
                }
        }
    }

    fun parseInput(input: List<String>): BoundedCoordinateMap<Long> {
        return BoundedCoordinateMap(
            input
                .flatMapIndexed { y: Int, line: String ->
                    line.mapIndexed { x, heatLoss ->
                        Coordinate(x, y) to heatLoss.digitToInt().toLong()
                    }
                }
                .associate { it }
        )
    }

    fun part1(input: List<String>): Long {
        val map = parseInput(input)
        return findShortestPath(
            Coordinate(0, 0),
            Coordinate(map.xBounds.last, map.yBounds.last),
            map
        ).first
//        return findPath(
//            Coordinate(0,0),
//            Coordinate(map.xBounds.last, map.yBounds.last),
//            SOUTH_EAST,
//            0,
//            0,
//            map,
//            listOf()
//        ).first
    }

    fun part2(input: List<String>): Long {
        val map = parseInput(input)
        return findShortestPathPart2(
            Coordinate(0, 0),
            Coordinate(map.xBounds.last, map.yBounds.last),
            map
        ).first
    }

    val testInput = readInput("17", "test_part1")
    println(part1(testInput))
    check(part1(testInput) == 102L)

//    part2(testInput).println()
//    check(part2(testInput) == 94L)

    val testInput2 = readInput("17", "test_part2")
//    part2(testInput2).println()
    check(part2(testInput2) == 71L)

//    part1(readInput("17", "input")).println()
    part2(readInput("17", "input")).println()
}
