import Direction.EAST
import java.util.PriorityQueue

fun main() {


    fun part1(input: List<String>): Long {
        var map = BoundedCoordinateMap(
            input
                .map { it.split("").drop(1).dropLast(1) }
        )

        var startPosition = map.map.entries.first { it.value == "S" }.key
        var startDirection = EAST
        var queue: PriorityQueue<Triple<Coordinate, Direction, Long>> = PriorityQueue { a, b -> (a.third - b.third).toInt()}
        queue.add(Triple(startPosition, startDirection, 0L))
        queue.add(Triple(startPosition, startDirection.opposite(), 2000))
        var processedTiles = mutableSetOf<Pair<Coordinate, Direction>>()
        while (true) {
            var (step, direction, cost) = queue.remove()
            if (map.map[step] == "E") {
                return cost
            }
            if (processedTiles.contains(Pair(step, direction))) {
                continue
            }
            val forward = map.stepInDirection(step, direction)
            if (forward.second == "." || forward.second == "E") {
                queue.add(Triple(forward.first, direction, cost + 1))
            }
            val left = map.stepInDirection(step, direction.turnLeft90())
            if (left.second == "." || left.second == "E") {
                queue.add(Triple(left.first, direction.turnLeft90(), cost + 1001))
            }
            var right = map.stepInDirection(step, direction.turnRight90())
            if (right.second == "." || right.second == "E") {
                queue.add(Triple(right.first, direction.turnRight90(), cost + 1001))
            }
            processedTiles.add(Pair(step, direction))
        }
    }

    fun part2(input: List<String>): Int {
        var map = BoundedCoordinateMap(
            input
                .map { it.split("").drop(1).dropLast(1) }
        )

        var startPosition = map.map.entries.first { it.value == "S" }.key
        var startDirection = EAST
        var queue: PriorityQueue<Pair<List<Coordinate>, Triple<Coordinate, Direction, Long>>> = PriorityQueue { a, b -> (a.second.third - b.second.third).toInt()}
        queue.add(Pair(listOf(startPosition), Triple(startPosition, startDirection, 0L)))
        var processedTiles = mutableMapOf<Pair<Coordinate, Direction>, Long>()
        while (true) {
            var (path, currentStep) = queue.remove()
            var (step, direction, cost) = currentStep
            if (map.map[step] == "E") {
                var tilesOnBestPath = mutableSetOf<Coordinate>()
                tilesOnBestPath.addAll(path)
                while (queue.isNotEmpty()) {
                    var (nextPath, nextStepTriple) = queue.remove()
                    var (nextStep, nextDirection, nextCost) = nextStepTriple
                    if (map.map[nextStep] == "E" && nextCost == cost) {
                        tilesOnBestPath.addAll(nextPath)
                    } else if (nextCost > cost) {
                        break
                    }
                }
                return tilesOnBestPath.count()
            }
            if (processedTiles.contains(Pair(step, direction)) && processedTiles[Pair(step, direction)]!! < cost) {
                continue
            }
            val forward = map.stepInDirection(step, direction)
            if (forward.second == "." || forward.second == "E") {
                var newPath = path.toMutableList()
                newPath.add(forward.first)
                queue.add(Pair(newPath, Triple(forward.first, direction, cost + 1)))
            }
            val left = map.stepInDirection(step, direction.turnLeft90())
            if (left.second == "." || left.second == "E") {
                var newPath = path.toMutableList()
                newPath.add(left.first)
                queue.add(Pair(newPath, Triple(left.first, direction.turnLeft90(), cost + 1001)))
            }
            var right = map.stepInDirection(step, direction.turnRight90())
            if (right.second == "." || right.second == "E") {
                var newPath = path.toMutableList()
                newPath.add(right.first)
                queue.add(Pair(newPath, Triple(right.first, direction.turnRight90(), cost + 1001)))
            }
            processedTiles[Pair(step, direction)] = cost
        }
    }

    part1(readInput("16", "input_2024")).println()
    part2(readInput("16", "input_2024")).println()
}
