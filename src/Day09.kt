fun main() {

    fun createListChain(list: List<Int>): MutableList<List<Int>> {
        val lists = mutableListOf(list)
        var currentList = list
        while (currentList.any { it != 0 }) {
            currentList = currentList
                .windowed(2, 1)
                .map { it[1] - it[0] }
            lists.add(currentList)
        }
        return lists
    }

    fun findNextInSequence(list: List<Int>): Int {
        return createListChain(list)
            .reversed()
            .map(List<Int>::last)
            .sum()
    }

    fun findPrevInSequence(list: List<Int>): Int {
        return createListChain(list)
            .reversed()
            .map(List<Int>::first)
            .reduce { acc, i -> i - acc }
    }

    fun parseInput(input: List<String>) = input
        .map { it.split(" ") }
        .map { it.map(String::toInt) }

    fun part1(input: List<String>): Int {
        return parseInput(input)
            .map(::findNextInSequence)
            .sum()
    }

    fun part2(input: List<String>): Int {
        return parseInput(input)
            .map(::findPrevInSequence)
            .sum()
    }

//    val testInput = readInput("09", "test_part1")
//    check(part1(testInput) == 0)
//
//    val testInput2 = readInput("09", "test_part2")
//    check(part2(testInput2) == 0)

    part1(readInput("09", "input")).println()
    part2(readInput("09", "input")).println()
}
