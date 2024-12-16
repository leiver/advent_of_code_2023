import Direction.*
import javax.swing.text.Position

fun main() {

    fun part1(input: List<String>): Long {
        var (mapString, instructionsString) = input
            .joinToString(";")
            .split(";;")
        var map =
            mapString
                .split(";")
                .map { it.split("").drop(1).dropLast(1) }
                .flatMapIndexed { y, list ->
                    list.mapIndexed { x, item ->
                        Coordinate(x, y) to item
                    }
                }
                .filter { it.second != "." }
                .associate { it }
                .toMutableMap()
        var boundedMap = BoundedCoordinateMap(map)
        var instructions = instructionsString
            .split(";")
            .flatMap { it.split("").drop(1).dropLast(1) }
        var currentPosition = map
            .entries
            .first { it.value == "@" }
            .key
        instructions
            .map {
                when (it) {
                    "^" -> NORTH
                    ">" -> EAST
                    "v" -> SOUTH
                    "<" -> WEST
                    else -> throw IllegalArgumentException()
                }
            }
            .forEach { instruction ->
                var nextEmptyStepOrWall = boundedMap.stepInDirection(currentPosition, instruction)
                while(nextEmptyStepOrWall.second == "O") {
                    nextEmptyStepOrWall = boundedMap.stepInDirection(nextEmptyStepOrWall.first, instruction)
                }
                if (nextEmptyStepOrWall.second == null) {
                    val nextStep = boundedMap.stepInDirection(currentPosition, instruction)
                    map[nextStep.first] = "@"
                    map.remove(currentPosition)
                    if (nextEmptyStepOrWall.first != nextStep.first) {
                        map[nextEmptyStepOrWall.first] = "O"
                    }
                    currentPosition = nextStep.first
                }
//                boundedMap.printMapWithDefaults(".")
            }
        return map
            .entries
            .filter { it.value == "O" }
            .sumOf { it.key.x + (it.key.y * 100) }
    }

    fun canMoveInDirection(map: BoundedCoordinateMap<String>, currentPosition: Coordinate, direction: Direction): Boolean {
        var nextStep = map.stepInDirection(currentPosition, direction)
        return if (nextStep.second == "#") {
            false
        }
        else if (nextStep.second == null) {
            true
        }
        else {
            if (direction == WEST || direction == EAST) {
                canMoveInDirection(map, nextStep.first, direction)
            } else {
                var otherSideOfBox = if (nextStep.second == "[") {
                    nextStep.first.neighbour(EAST)
                } else {
                    nextStep.first.neighbour(WEST)
                }
                canMoveInDirection(map, nextStep.first, direction) && canMoveInDirection(map, otherSideOfBox, direction)
            }
        }
    }

    fun moveInDirection(map: BoundedCoordinateMap<String>, hashMap: MutableMap<Coordinate, String>, currentPosition: Coordinate, direction: Direction) {
        var nextStep = map.stepInDirection(currentPosition, direction)
        if (nextStep.second != null) {
            if (direction == WEST || direction == EAST) {
                moveInDirection(map, hashMap, nextStep.first, direction)
            } else {
                var otherSideOfBox = if (nextStep.second == "[") {
                    nextStep.first.neighbour(EAST)
                } else {
                    nextStep.first.neighbour(WEST)
                }
                moveInDirection(map, hashMap, nextStep.first, direction)
                moveInDirection(map,hashMap, otherSideOfBox, direction)
            }
        }
        hashMap[nextStep.first] = hashMap[currentPosition]!!
        hashMap.remove(currentPosition)
    }

    fun part2(input: List<String>): Long {
        var (mapString, instructionsString) = input
            .joinToString(";")
            .split(";;")
        var map =
            mapString
                .split(";")
                .map { line ->
                    line
                        .split("")
                        .drop(1)
                        .dropLast(1)
                        .flatMap {
                            when (it) {
                                "#" -> listOf("#", "#")
                                "O" -> listOf("[", "]")
                                "." -> listOf(".", ".")
                                "@" -> listOf("@", ".")
                                else -> throw IllegalArgumentException()
                            }
                        }
                }
                .flatMapIndexed { y, list ->
                    list.mapIndexed { x, item ->
                        Coordinate(x, y) to item
                    }
                }
                .filter { it.second != "." }
                .associate { it }
                .toMutableMap()
        var boundedMap = BoundedCoordinateMap(map)
        var instructions = instructionsString
            .split(";")
            .flatMap { it.split("").drop(1).dropLast(1) }
        var currentPosition = map
            .entries
            .first { it.value == "@" }
            .key
        instructions
            .map {
                when (it) {
                    "^" -> NORTH
                    ">" -> EAST
                    "v" -> SOUTH
                    "<" -> WEST
                    else -> throw IllegalArgumentException()
                }
            }
            .forEach { instruction ->
                if (canMoveInDirection(boundedMap, currentPosition, instruction)) {
                    moveInDirection(boundedMap, map, currentPosition, instruction)
                    currentPosition = currentPosition.neighbour(instruction)
                }
            }
        return map
            .entries
            .filter { it.value == "[" }
            .sumOf { it.key.x + (it.key.y * 100) }
    }

//    val testInput = readInput("15", "test_part1")
//    check(part1(testInput) == 1320L)
//
//    val testInput2 = readInput("15", "test_part1")
//    check(part2(testInput2) == 145L)

    part1(readInput("15", "input_2024")).println()
    part2(readInput("15", "input_2024")).println()
}
