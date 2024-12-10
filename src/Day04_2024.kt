import kotlin.math.max
import kotlin.math.min

fun main() {

    fun part1(input: List<String>): Int {
        val boundedCoordinateMap = BoundedCoordinateMap(
            input
                .map { linje -> linje.split("") }
        )
        return boundedCoordinateMap
            .map
            .entries
            .asSequence()
            .filter { (_, character) -> character == "X" }
            .map { (coordinate, _) ->
                Direction
                    .all()
                    .map { Triple(it, boundedCoordinateMap.map.get(coordinate neighbour it), coordinate neighbour it) }
                    .filter { it.second == "M" }
                    .map { Triple(it.first, boundedCoordinateMap.map.get(it.third neighbour it.first), it.third neighbour it.first) }
                    .filter { it.second == "A" }
                    .map { Triple(it.first, boundedCoordinateMap.map.get(it.third neighbour it.first), it.third neighbour it.first) }
                    .filter { it.second == "S" }
                    .count()
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val boundedCoordinateMap = BoundedCoordinateMap(
            input
                .map { linje -> linje.split("") }
        )
        return boundedCoordinateMap
            .map
            .entries
            .asSequence()
            .filter { (_, character) -> character == "A" }
            .filter { (coordinate, _) ->
                val diagonalM = Direction
                    .diagonal()
                    .map { Triple(it, boundedCoordinateMap.map.get(coordinate neighbour it), coordinate neighbour it) }
                    .filter { it.second == "M" }
                    .toList()
                diagonalM.count() == 2 && diagonalM.get(0).first.opposite() != diagonalM.get(1).first &&
                        boundedCoordinateMap.map.get(coordinate neighbour diagonalM.get(0).first.opposite()) == "S" &&
                        boundedCoordinateMap.map.get(coordinate neighbour diagonalM.get(1).first.opposite()) == "S"

            }
            .count()
    }

//    val testInput = readInput("04", "test")
//    check(part1(testInput) == 13)
//
//    val testInput2 = readInput("04", "test")
//    check(part2(testInput2) == 30)

    part1(readInput("04", "input_2024")).println()
    part2(readInput("04", "input_2024")).println()
}
