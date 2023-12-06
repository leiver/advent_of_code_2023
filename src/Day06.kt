fun main() {

    data class Race(val milliseconds: Long, val record: Long)

    fun parseInput(input: List<String>): List<Race> {
        val parsedInput = input
            .map { it.split(" ") }
            .map { it.map(String::trim) }
            .map { it.filter(String::isNotEmpty) }
            .map { it.drop(1) }
            .map { it.map(String::toLong) }

        return parsedInput[0]
            .zip(parsedInput[1])
            .map { Race(it.first, it.second) }
    }

    fun simulateRace(race: Race): Int {
        return LongRange(1L, race.milliseconds)
            .map {
                it * (race.milliseconds - it)
            }
            .filter { it > race.record }
            .size
    }

    fun part1(input: List<String>): Int {
        return parseInput(input)
            .map { simulateRace(it) }
            .reduce { acc, i -> acc * i }
    }

    fun parseInputPart2(input: List<String>): Race {
        val parsedInput = input
            .map { it.split(" ") }
            .map { it.map(String::trim) }
            .map { it.filter(String::isNotEmpty) }
            .map { it.drop(1) }
            .map { it.joinToString("") }
            .map (String::toLong)

        return Race(parsedInput[0], parsedInput[1])
    }

    fun part2(input: List<String>): Int {
        return simulateRace(
            parseInputPart2(input)
        )
    }

    val testInput = readInput("06", "test")
    check(part1(testInput) == 288)

    val testInput2 = readInput("06", "test")
    check(part2(testInput2) == 71503)

    part1(readInput("06", "input")).println()
    part2(readInput("06", "input")).println()
}
