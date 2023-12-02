fun main() {

    fun biggestForGivenColor(rounds: String, color: String) = rounds
        .split(";")
        .flatMap {
            it
                .split(",")
                .filter { it.contains(color) }
                .map { it.strip().split(" ")[0].strip().toInt() }
        }.max()

    fun parseGamePart1(game: String) : Int {
        val (id, rounds) = "Game (\\d+):( \\d+ .+,?;?)+".toRegex().matchEntire(game)!!.destructured
        val biggestGreen = biggestForGivenColor(rounds, "green")
        val biggestRed = biggestForGivenColor(rounds, "red")
        val biggestBlue = biggestForGivenColor(rounds, "blue")
        if (biggestGreen <= 13 && biggestBlue <= 14 && biggestRed <= 12) {
            return id.toInt()
        }
        return 0
    }

    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .map { parseGamePart1(it) }
            .sum()

    }

    fun parseGamePart2(game: String) : Int {
        val (id, rounds) = "Game (\\d+):( \\d+ .+,?;?)+".toRegex().matchEntire(game)!!.destructured
        val biggestGreen = biggestForGivenColor(rounds, "green")
        val biggestRed = biggestForGivenColor(rounds, "red")
        val biggestBlue = biggestForGivenColor(rounds, "blue")
        return biggestGreen * biggestRed * biggestBlue
    }

    fun part2(input: List<String>): Int {
        return input
            .asSequence()
            .map { parseGamePart2(it) }
            .sum()
    }

    val testInput = readInput("02", "test_part1")
    check(part1(testInput) == 8)

    val testInput2 = readInput("02", "test_part2")
    check(part2(testInput2) == 2286)

    part1(readInput("02", "input")).println()
    part2(readInput("02", "input")).println()
}
