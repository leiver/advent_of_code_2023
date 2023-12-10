fun main() {

    data class Pipe(val pipeType: Char, val coordinate: Pair<Int, Int>, val connections: List<Pair<Int, Int>>) {
        constructor(coordinate: Pair<Int, Int>, pipe: Char) : this(
            pipe,
            coordinate,
            when (pipe) {
                '|' -> listOf(
                    Pair(coordinate.first, coordinate.second - 1),
                    Pair(coordinate.first, coordinate.second + 1)
                )

                '-' -> listOf(
                    Pair(coordinate.first - 1, coordinate.second),
                    Pair(coordinate.first + 1, coordinate.second)
                )

                'L' -> listOf(
                    Pair(coordinate.first, coordinate.second - 1),
                    Pair(coordinate.first + 1, coordinate.second)
                )

                'J' -> listOf(
                    Pair(coordinate.first, coordinate.second - 1),
                    Pair(coordinate.first - 1, coordinate.second)
                )

                '7' -> listOf(
                    Pair(coordinate.first, coordinate.second + 1),
                    Pair(coordinate.first - 1, coordinate.second)
                )

                'F' -> listOf(
                    Pair(coordinate.first, coordinate.second + 1),
                    Pair(coordinate.first + 1, coordinate.second)
                )

                else -> {
                    listOf()
                }

            }
        )
    }

    fun parseInput(input: List<String>): Map<Pair<Int, Int>, Pipe> {
        return input
            .flatMapIndexed { y, line ->
                line
                    .mapIndexed { x, pipe ->
                        Pair(Pair(x, y), pipe)
                    }
                    .filter { !it.second.equals('.') }
                    .map { Pipe(it.first, it.second) }
            }
            .associateBy { it.coordinate }
    }

    fun findLoop(map: Map<Pair<Int, Int>, Pipe>): List<Pipe> {
        var prevSection = map.values.first { it.pipeType == 'S' }
        var currentSection = IntRange(prevSection.coordinate.first - 1, prevSection.coordinate.first + 1)
            .flatMap { x ->
                IntRange(
                    prevSection.coordinate.second - 1,
                    prevSection.coordinate.second + 1
                ).map { y -> Pair(x, y) }
            }
            .filter { it != prevSection.coordinate }
            .filter { it.first == prevSection.coordinate.first || it.second == prevSection.coordinate.second }
            .filter { map[it] != null }
            .map { map[it]!! }
            .first { it.connections.any { connection -> prevSection.coordinate == connection } }
        val loop = mutableListOf(currentSection)

        while (currentSection.pipeType != 'S') {
            val nextSection = map[
                currentSection
                    .connections
                    .first { it != prevSection.coordinate }
            ]!!
            prevSection = currentSection
            currentSection = nextSection
            loop.add(currentSection)
        }

        return loop
    }

    fun part1(input: List<String>): Int {
        return findLoop(
            parseInput(input)
        ).size / 2
    }

    fun directionBetweenConnections(from: Pipe, to: Pipe) =
        if (to.coordinate.first != from.coordinate.first) {
            if (to.coordinate.first < from.coordinate.first) {
                "W"
            } else {
                "E"
            }
        } else {
            if (to.coordinate.second < from.coordinate.second) {
                "N"
            } else {
                "S"
            }
        }

    fun findTilesInsideLoop(loop: List<Pipe>): Int {

        loop
            .map {
                if (it.pipeType == 'S') {
                    val firstDirection = directionBetweenConnections(it, loop.first())
                    val secondDirection = directionBetweenConnections(it, loop[loop.size - 2])

                    val pipetype = when (firstDirection + secondDirection) {
                        "SN", "NS" -> '|'
                        "EW", "WE" -> '-'
                        "NW", "WN" -> 'J'
                        "NE", "EN" -> 'L'
                        "SW", "WS" -> '7'
                        "SE", "ES" -> 'F'
                        else -> {
                            println("OOPSIE!")
                            '.'
                        }
                    }
                    Pipe(
                        it.coordinate,
                        pipetype
                    )
                }
            }

        val yRange = IntRange(
            loop.minOf { it.coordinate.second },
            loop.maxOf { it.coordinate.second }
        )

        return yRange
            .sumOf { y ->
                val loopTilesInRow = loop
                    .filter { it.coordinate.second == y }
                    .sortedBy { it.coordinate.first }
                loopTilesInRow
                    .filter { it.pipeType != '-' }
                    .scan(Pipe('-', Pair(0, 0), listOf())) { prev, next ->
                        when (prev.pipeType.toString() + next.pipeType) {
                            "L7", "FJ" ->
                                Pipe(
                                    '-',
                                    next.coordinate,
                                    next.connections
                                )

                            else -> next
                        }
                    }
                    .filter { it.pipeType != '-' }
                    .windowed(2, 2)
                    .map { IntRange(it[0].coordinate.first + 1, it[1].coordinate.first - 1) }
                    .sumOf { xRange ->
                        xRange.count { x -> loopTilesInRow.none { pipe -> pipe.coordinate.first == x } }
                    }
            }
    }

    fun part2(input: List<String>): Int {
        return findTilesInsideLoop(
            findLoop(
                parseInput(input)
            )
        )
    }

    val testInput = readInput("10", "test_part1")
    check(part1(testInput) == 8)

    val testInput2 = readInput("10", "test_part2")
    check(part2(testInput2) == 8)

    part1(readInput("10", "input")).println()
    part2(readInput("10", "input")).println()
}
