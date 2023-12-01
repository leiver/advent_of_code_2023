fun main() {

    fun part1(input: List<String>): Int {
        return input
            .map { it.split("") }
            .map { it.filter { it.matches("\\d".toRegex()) } }
            .map { it.first() + it.last() }
            .map { it.toInt() }
            .sum()

    }

    fun textToNumber(input: String): String {
        return input
            .replace("one", "1")
            .replace("two", "2")
            .replace("three", "3")
            .replace("four", "4")
            .replace("five", "5")
            .replace("six", "6")
            .replace("seven", "7")
            .replace("eight", "8")
            .replace("nine", "9")
    }

    fun part2(input: List<String>): Int {
        return input
            .map { "(?=(\\d|one|two|three|four|five|six|seven|eight|nine))".toRegex().findAll(it) }
            .map { textToNumber(it.first().groups.last()!!.value) + textToNumber(it.last().groups.last()!!.value) }
            .map { it.toInt() }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("01", "test_part1")
    check(part1(testInput) == 142)

    val testInput2 = readInput("01", "test_part2")
    check(part2(testInput2) == 281)

    part1(readInput("01", "input")).println()
    part2(readInput("01", "input")).println()
}
