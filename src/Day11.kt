fun main() {

    fun List<String>.parseInput(): BoundedCoordinateMap<Char> {
        return BoundedCoordinateMap(
            flatMapIndexed { y, line ->
                line
                    .mapIndexed { x, char ->
                        Coordinate(x.toLong(), y.toLong()) to char
                    }
                    .filter { it.second == '#' }
            }
                .associate { it }
        )
    }

    fun List<Pair<Long, List<Pair<Coordinate, Char>>>>.expandDirection(
        expansion: Long,
        mapperFunction: (Coordinate, Long) -> Coordinate
    ): BoundedCoordinateMap<Char> {
        return BoundedCoordinateMap(
            flatMapIndexed { xWithoutEmptySpace, (realX, column) ->
                column
                    .map { (coordinates, galaxy) ->
                        mapperFunction(
                            coordinates,
                            realX + (realX - xWithoutEmptySpace) * (expansion - 1)
                        ) to galaxy
                    }
            }
                .associate { it }
        )
    }

    fun BoundedCoordinateMap<Char>.expandX(expansion: Long): BoundedCoordinateMap<Char> =
        columns().expandDirection(expansion) { coordinate, expandedX -> Coordinate(expandedX, coordinate.y) }

    fun BoundedCoordinateMap<Char>.expandY(expansion: Long): BoundedCoordinateMap<Char> =
        rows().expandDirection(expansion) { coordinate, expandedY -> Coordinate(coordinate.x, expandedY) }


    fun BoundedCoordinateMap<Char>.expandUniverse(expansion: Long): BoundedCoordinateMap<Char> {
        return expandX(expansion)
            .expandY(expansion)
    }

    fun BoundedCoordinateMap<Char>.distanceBetweenGalaxies(): Long {
        return map
            .keys
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
