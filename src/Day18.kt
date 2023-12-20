import Direction.*
import kotlin.math.absoluteValue

@OptIn(ExperimentalStdlibApi::class)
fun main() {

    fun parseInputPointsPart1(input: List<String>): List<Coordinate> {
        val instructions = input
            .map { "(.) (\\d+) \\(#.{6}\\)".toRegex().matchEntire(it)!!.destructured }
            .map { (direction, times) ->
                when (direction) {
                    "U" -> NORTH
                    "L" -> WEST
                    "R" -> EAST
                    "D" -> SOUTH
                    else -> NORTH
                } to times.toLong()
            }

        return instructions
            .scan(Coordinate(0,0)) { prevPoint, (direction, times) ->
                prevPoint.plus(direction.delta.multiply(times))
            }
    }

    fun parseInputPointsPart2(input: List<String>): List<Coordinate> {
        val instructions = input
            .map { ". \\d+ \\(#(.{6})\\)".toRegex().matchEntire(it)!!.destructured }
            .map { (color) ->
                when (color.last()) {
                    '3' -> NORTH
                    '2' -> WEST
                    '0' -> EAST
                    '1' -> SOUTH
                    else -> NORTH
                } to color.substring(0, 5).hexToLong()
            }

        return instructions
            .scan(Coordinate(0,0)) { prevPoint, (direction, times) ->
                prevPoint.plus(direction.delta.multiply(times))
            }
    }

    fun picksTheoremWithShoelace(points: List<Coordinate>): Long {
        val perimeter = points
            .windowed(2, 1)
            .sumOf { (first, second) -> first.manhattenDistance(second) }
        val interior = points
            .windowed(2, 1)
            .sumOf { (current, next) -> current.x * next.y - current.y * next.x }
            .absoluteValue / 2
        return interior + perimeter / 2 + 1
    }

    fun part1(input: List<String>): Long {
        return picksTheoremWithShoelace(
            parseInputPointsPart1(input)
        )
    }

    fun part2(input: List<String>): Long {
        return picksTheoremWithShoelace(
            parseInputPointsPart2(input)
        )
    }

    val testInput = readInput("18", "test_part1")
    check(part1(testInput) == 62L)

    val testInput2 = readInput("18", "test_part1")
    check(part2(testInput2) == 952408144115L)

    part1(readInput("18", "input")).println()
    part2(readInput("18", "input")).println()
}
