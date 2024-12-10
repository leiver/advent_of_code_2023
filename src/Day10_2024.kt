import Direction.*

fun main() {

    fun traverseFrom(map: BoundedCoordinateMap<Int>, currentPosition: Coordinate, currentNumber: Int): List<Coordinate> {
        if (currentNumber == 9) {
            return listOf(currentPosition)
        }

        return map.orthogonalNeighbours(currentPosition)
            .filter { it.second.second!! == currentNumber+1 }
            .flatMap { traverseFrom(map, it.second.first, currentNumber+1) }
            .distinct()
    }

    fun part1(input: List<String>): Int {
        var map = BoundedCoordinateMap(
            input
                .map { it.split("").drop(1).dropLast(1).map { number -> number.toInt() } }
        )

        return map
            .map
            .entries
            .filter { it.value == 0 }
            .flatMap { start ->
                traverseFrom(map, start.key, 0)
            }
            .count()
    }

    fun traverseFromCount(map: BoundedCoordinateMap<Int>, currentPosition: Coordinate, currentNumber: Int): Int {
        if (currentNumber == 9) {
            return 1
        }

        return map.orthogonalNeighbours(currentPosition)
            .filter { it.second.second!! == currentNumber+1 }
            .map { traverseFromCount(map, it.second.first, currentNumber+1) }
            .sum()
    }

    fun part2(input: List<String>): Int {
        var map = BoundedCoordinateMap(
            input
                .map { it.split("").drop(1).dropLast(1).map { number -> number.toInt() } }
        )

        return map
            .map
            .entries
            .filter { it.value == 0 }
            .map { start ->
                traverseFromCount(map, start.key, 0)
            }
            .sum()
    }

//    val testInput = readInput("10", "test_part1")
//    check(part1(testInput) == 8)
//
//    val testInput2 = readInput("10", "test_part2")
//    check(part2(testInput2) == 8)

    part1(readInput("10", "input_2024")).println()
    part2(readInput("10", "input_2024")).println()
}
