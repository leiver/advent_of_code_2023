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

    fun List<Pair<Long,List<Pair<Coordinate, Char>>>>.expandDirection(
        expansion: Long,
        mapperFunction: (Coordinate, Long) -> Coordinate
    ): Map<Coordinate, Char> {
        return flatMapIndexed { xWithoutEmptySpace, (realX, column) ->
                column
                    .map { (coordinates, galaxy) ->
                        mapperFunction(
                            coordinates,
                            realX + (realX - xWithoutEmptySpace) * (expansion - 1)
                        ) to galaxy
                    }
            }
            .associate { it }
    }

    fun Map<Coordinate, Char>.expandX(expansion: Long): Map<Coordinate, Char> =
        columns().expandDirection(expansion) {coordinate, expandedX -> Coordinate(expandedX, coordinate.y) }

    fun Map<Coordinate, Char>.expandY(expansion: Long): Map<Coordinate, Char> =
        rows().expandDirection(expansion) {coordinate, expandedY -> Coordinate(coordinate.x, expandedY) }


    fun Map<Coordinate, Char>.expandUniverse(expansion: Long): Map<Coordinate, Char> {
        return expandX(expansion)
            .expandY(expansion)
    }

    fun Map<Coordinate, Char>.distanceBetweenGalaxies(): Long {
        return keys
            .permutations()
            .sumOf { it.first manhattenDistance it.second }
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
