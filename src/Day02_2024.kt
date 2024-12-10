fun main() {

    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .map { it.split(" ").map { it.toInt() } }
            .filter { numbers ->
                val differences = IntRange(0, numbers.size - 2)
                    .map { numbers.get(it) - numbers.get(it + 1) }
                    .toList()
                val direction = Math.signum(differences.first().toDouble()).toInt()
                val result = differences
                    .all { Math.signum(it.toDouble()).toInt().equals(direction) && Math.abs(it) > 0 && Math.abs(it) < 4 }
                result
            }.count()
    }

    fun part2(input: List<String>): Int {
        return input
            .asSequence()
            .map { it.split(" ").map { it.toInt() } }
            .filter { numbers ->
                IntRange(0, numbers.size - 1)
                    .any { removedIndex ->
                        val differences = IntRange(0, numbers.size - 3)
                            .map { numbers.get(if (it >= removedIndex) it+1 else it) - numbers.get(if (it+1 >= removedIndex) it+2 else it+1) }
                            .toList()
                        val direction = Math.signum(differences.first().toDouble()).toInt()
                        val result = differences
                            .all { Math.signum(it.toDouble()).toInt().equals(direction) && Math.abs(it) > 0 && Math.abs(it) < 4 }
                        result
                    }
            }.count()
    }

    val testInput = readInput("02", "2024_test")
    check(part1(testInput) == 2)

    val testInput2 = readInput("02", "2024_test")
    check(part2(testInput2) == 4)

    part1(readInput("02", "2024")).println()
    part2(readInput("02", "2024")).println()
}
