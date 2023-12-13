import kotlin.math.max
import kotlin.math.min

fun main() {

    data class MirrorMap(val map: List<List<Char>>) {
        fun rotate(): MirrorMap =
            MirrorMap(
                map.rotate2DArray()
            )
    }

    fun parseInput(input: List<String>): List<MirrorMap> =
        input
            .joinToString(";")
            .split(";;")
            .map {
                MirrorMap(
                    it
                        .split(";")
                        .map { it.toList() }
                )
            }

    fun List<Char>.splitLineIntoReflections(): List<Pair<Int, Pair<List<Char>, List<Char>>>> =
        zip(indices)
            .windowed(2, 1)
            .map { window ->
                val shortestDistanceToEdge = min(
                    window.first().second,
                    size - window.last().second - 1
                )
                val firstHalf = subList(
                    max(0, window.first().second - shortestDistanceToEdge),
                    window.first().second + 1
                )
                val secondHalf = subList(
                    window.last().second,
                    min(size, window.last().second + shortestDistanceToEdge + 1)
                )
                window.first().second to (firstHalf to secondHalf.reversed())
            }

    fun MirrorMap.findAllReflectedLines(): Map<Int, List<Pair<List<Char>, List<Char>>>> =
        map
            .flatMap { line ->
                line
                    .splitLineIntoReflections()
                    .filter { (_, halves) -> halves.first == halves.second }
            }
            .groupBy(Pair<Int, Pair<List<Char>, List<Char>>>::first) { (_, halves) -> halves }

    fun MirrorMap.findReflection(): Int? {
        return findAllReflectedLines()
            .filterValues { it.size == map.size }
            .keys
            .map { it + 1 }
            .firstOrNull()
    }

    fun MirrorMap.findOtherReflection(): Int? {
        val indexesToSearch = findAllReflectedLines()
            .filterValues { it.size == map.size - 1 }
            .keys
        return map
            .flatMap { line ->
                line
                    .splitLineIntoReflections()
                    .filter { (index, _) -> indexesToSearch.contains(index) }
                    .filter { (_, halves) -> halves.first != halves.second }
            }
            .map { (index, halves) -> index + 1 to halves }
            .firstOrNull { (_, halves) ->
                halves.first
                    .zip(halves.first.indices)
                    .map { (char, index) ->
                        val copy = halves.first.toMutableList()
                        copy[index] = if (char == '#') '.' else '#'
                        copy
                    }
                    .any { unsmudgedHalf -> unsmudgedHalf == halves.second }
            }
            ?.first
    }

    fun List<MirrorMap>.findReflections(): Int =
        sumOf {
            it.findReflection() ?: (it.rotate().findReflection()!! * 100)
        }

    fun List<MirrorMap>.findOtherReflections(): Int =
        sumOf {
            it.findOtherReflection() ?: (it.rotate().findOtherReflection()!! * 100)
        }

    fun part1(input: List<String>): Int {
        return parseInput(input)
            .findReflections()

    }

    fun part2(input: List<String>): Int {
        return parseInput(input)
            .findOtherReflections()
    }

    val testInput = readInput("13", "test_part1")
    check(part1(testInput) == 405)

    val testInput2 = readInput("13", "test_part1")
    check(part2(testInput2) == 400)

    part1(readInput("13", "input")).println()
    part2(readInput("13", "input")).println()
}
