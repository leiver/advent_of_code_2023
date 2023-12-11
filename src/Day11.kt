import java.util.Map.Entry
import kotlin.math.abs

fun main() {

    fun List<String>.parseInput(): Map<Coordinate, Char> {
        return flatMapIndexed { y, line ->
                line
                    .mapIndexed { x, char ->
                        Coordinate(x.toLong(), y.toLong()) to char
                    }
                    .filter { it.second == '#' }
            }
            .associate { it }
    }

    fun Map<Coordinate, Char>.expandDirection(
        expansion: Long,
        directionFunction: (Coordinate) -> Long,
        mapperFunction: (Coordinate, Long) -> Coordinate
    ): Map<Coordinate, Char> {
        return entries
            .groupBy { directionFunction(it.key) }
            .entries
            .sortedBy { it.key }
            .flatMapIndexed { x, column ->
                column
                    .value
                    .map { galaxy ->
                        mapperFunction(galaxy.key, directionFunction(galaxy.key) + (directionFunction(galaxy.key) - x) * (expansion - 1)) to
                                galaxy.value
                    }
            }
            .associate { it }
    }

    fun Map<Coordinate, Char>.expandUniverse(expansion: Long): Map<Coordinate, Char> {
        return expandDirection(expansion, Coordinate::x) {coordinate, expandedX -> Coordinate(expandedX, coordinate.y) }
            .expandDirection(expansion, Coordinate::y) {coordinate, expandedY -> Coordinate(coordinate.x, expandedY) }
    }

    fun Map<Coordinate, Char>.distanceBetweenGalaxies(): Long {
        return keys
            .permutations()
            .sumOf { abs(it.first.x - it.second.x) + abs(it.first.y - it.second.y) }
    }

    fun part1(input: List<String>): Long {
        return input
            .parseInput()
            .expandUniverse(2)
            .distanceBetweenGalaxies()
    }

    fun part2(input: List<String>): Long {
        return input
            .parseInput()
            .expandUniverse(1000000)
            .distanceBetweenGalaxies()
    }

    val testInput = readInput("11", "test_part1")
    check(part1(testInput) == 374L)

    part1(readInput("11", "input")).println()
    part2(readInput("11", "input")).println()
}
