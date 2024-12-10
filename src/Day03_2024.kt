fun main() {

    fun part1(input: List<String>): Int {
        return input
            .flatMap { line -> "mul\\((\\d+),(\\d+)\\)".toRegex().findAll(line) }
//            .map { println(it); it }
            .map { it.destructured }
            .map { (x, y) -> x.toInt() * y.toInt() }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input
            .joinToString(",")
            .split("don't()").drop(1)
//            .map { println(it); it }
            .flatMap { line -> line.split("do()").drop(1) }
            .map { println(it); it }
            .flatMap { line -> "mul\\((\\d+),(\\d+)\\)".toRegex().findAll(line) }
            .map { it.destructured }
            .map { (x, y) -> println(x + ", " + y) ;x.toInt() * y.toInt() }
            .sum() +
                "mul\\((\\d+),(\\d+)\\)".toRegex().findAll(input.joinToString(",").split("don't()").first())
                    .map { it.destructured }
                    .map { (x, y) -> x.toInt() * y.toInt() }
                    .sum()

    }

//    val testInput = readInput("03", "test")
//    check(part1(testInput) == 4361)
//
    val testInput2 = readInput("03", "test_2024")
    check(part2(testInput2) == 48)

    part1(readInput("03", "2024")).println()
    part2(readInput("03", "2024")).println()
}
