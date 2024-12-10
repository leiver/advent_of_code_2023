fun main() {

    fun part1(input: List<String>): Int {
        val første = input
            .asSequence()
            .map { it.split(" ") }
            .map { it.first().toInt() }
            .sorted()
            .toList()
        val andre = input
            .asSequence()
            .map { it.split(" ") }
            .map { it.last().toInt() }
            .sorted()
            .toList()
        return IntRange(0, første.size-1)
            .map { Math.abs(første.get(it) - andre.get(it)) }
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
        val første = input
            .asSequence()
            .map { it.split(" ") }
            .map { it.first().toInt() }
            .toList()
        val andre = input
            .asSequence()
            .map { it.split(" ") }
            .map { it.last().toInt() }
            .groupingBy { it }
            .eachCount()
        println(andre)
        return IntRange(0, første.size-1)
            .map { Math.abs(første.get(it) * (andre[første.get(it)]?:0)) }
            .sum()
    }

//    val testInput = readInput("01", "test_part1")
//    check(part1(testInput) == 142)
//
//    val testInput2 = readInput("01", "test_part2")
//    check(part2(testInput2) == 281)

    part1(readInput("01", "2024")).println()
    part2(readInput("01", "2024")).println()
}
