import Direction.NORTH
import Direction.SOUTH
import java.util.*

fun main() {

    fun findFencePriceForType(flowerType: Char, flowers: Set<Coordinate>): Long {
        var result = 0L
        var parsedFlowers = mutableSetOf<Coordinate>()
        flowers
            .forEach { startFlower ->
                if (!parsedFlowers.contains(startFlower)) {
                    var area = 0L
                    var circ = 0L
                    var stack = Stack<Coordinate>()
                    stack.push(startFlower)

                    while (stack.isNotEmpty()) {
                        var nextFlower = stack.pop()
                        if (parsedFlowers.contains(nextFlower)) continue
                        val neighbourFlowers = nextFlower.orthogonalNeighbours()
                            .filter { flowers.contains(it) }
                        area += 1
                        circ += (4 - neighbourFlowers.size)
                        neighbourFlowers
                            .filter { !parsedFlowers.contains(it) }
                            .forEach { stack.push(it) }
                        parsedFlowers.add(nextFlower)
                    }
//                    println("found area of type $flowerType with area: $area and circumference: $circ")
                    result += area * circ

                }
            }
        return result;
    }

    fun findFencePriceForTypePart2(flowerType: Char, flowers: Set<Coordinate>): Long {
        var result = 0L
        var parsedFlowers = mutableSetOf<Coordinate>()
        flowers
            .forEach { startFlower ->
                if (!parsedFlowers.contains(startFlower)) {
                    var area = 0L
                    var circ = 0L
                    var stack = Stack<Coordinate>()
                    stack.push(startFlower)
                    var perimiter = mutableSetOf<Pair<Coordinate, Direction>>()

                    while (stack.isNotEmpty()) {
                        var nextFlower = stack.pop()
                        if (parsedFlowers.contains(nextFlower)) continue
                        val neighbourFlowers = nextFlower.orthogonalNeighbours()
                            .filter { flowers.contains(it) }
                        perimiter.addAll(
                            Direction.orthogonal()
                                .map { Pair(nextFlower.plus(it.delta), it) }
                                .filter { !flowers.contains(it.first) }
                        )
                        area += 1
                        circ += (4 - neighbourFlowers.size)
                        neighbourFlowers
                            .filter { !parsedFlowers.contains(it) }
                            .forEach { stack.push(it) }
                        parsedFlowers.add(nextFlower)
                    }

                    var sides = perimiter
                        .groupBy { it.second }
                        .map { direction ->
                            direction.value
                                .groupBy { if (it.second == NORTH || it.second == SOUTH) it.first.y else it.first.x }
                                .map { sides ->
//                                    println("parsing side for type $flowerType in direction ${direction.key} with coords ${sides.value.map { it.first }}")
                                    sides.value
                                        .map { if (direction.key == NORTH || direction.key == SOUTH) it.first.x else it.first.y }
                                        .sorted()
                                        .windowed(2)
                                        .map { it.last() - it.first() }
                                        .filter { it > 1 }
                                        .count() + 1
                                }
                                .sum()
                        }
                        .sum()

//                    println("found area of type $flowerType with area: $area and sides: $sides")
                    result += area * sides

                }
            }
        return result;
    }

    fun part1(input: List<String>): Long {
        var fullMap = BoundedCoordinateMap(
            input
                .map { it.toList() }
        )

        return fullMap
            .map.entries
            .groupBy({ it.value }, { it.key })
            .map { findFencePriceForType(it.key, it.value.toSet()) }
            .sum()
    }

    fun part2(input: List<String>): Long {
        var fullMap = BoundedCoordinateMap(
            input
                .map { it.toList() }
        )

        return fullMap
            .map.entries
            .groupBy({ it.value }, { it.key })
            .map { findFencePriceForTypePart2(it.key, it.value.toSet()) }
            .sum()
    }

    val testInput = readInput("12", "test_2024")
    val resultPart1 = part1(testInput)
//    println(resultPart1)
    check(resultPart1 == 1930L)
//
    val testInput2 = readInput("12", "test_2024")
    val resultPart2 = part2(testInput2)
//    println(resultPart2)
    check(resultPart2 == 1206L)

    part1(readInput("12", "input_2024")).println()
    part2(readInput("12", "input_2024")).println()
}
