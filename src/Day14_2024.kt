fun main() {

    fun part1(input: List<String>, mapSize: Coordinate): Int {
        return input
            .map { line ->
                var (position, speed) = line
                    .split(" ")
                    .map { it.split("=")[1].split(",") }
                    .map { Coordinate(it[0].toInt(), it[1].toInt()) }
                speed = speed plus mapSize
                var newPosition = ((position plus (speed multiply 100)) mod mapSize)
                newPosition
            }
            .filter { it.x != (mapSize.x / 2) && it.y != (mapSize.y / 2) }
            .groupBy {
                (if (it.x < (mapSize.x / 2)) 1 else 2) + (if (it.y < (mapSize.y / 2)) 10 else 20)
            }
            .entries
            .map { it.value.count() }
            .fold(1) { a, b -> a*b }
    }

    fun part2(input: List<String>, mapSize: Coordinate) {
        var positions = input
            .map { line ->
                var (position, speed) = line
                    .split(" ")
                    .map { it.split("=")[1].split(",") }
                    .map { Coordinate(it[0].toInt(), it[1].toInt()) }
                speed = speed plus mapSize
                var newPosition = ((position plus (speed multiply 8179)) mod mapSize)
                newPosition
            }
            BoundedCoordinateMap(
                positions.map { Pair(it, "*") }.associate { it },
                LongRange(0, mapSize.x-1),
                LongRange(0, mapSize.y-1)
            ).printMapWithDefaults(" ")
    }

    val testInput = readInput("14", "test_2024")
    val part1 = part1(testInput, Coordinate(11, 7))
//    println(part1)
    check(part1 == 12)
//
//    val testInput2 = readInput("14", "test_part1")
//    check(part2(testInput2) == 64L)

    part1(readInput("14", "input_2024"), Coordinate(101, 103)).println()
    part2(readInput("14", "input_2024"), Coordinate(101, 103))
}
