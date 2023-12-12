fun main() {

    data class Record(
        val fullRecord: List<List<Char>>,
        val springGroups: List<List<Int>>
    ) {
        fun get(): List<Pair<List<Char>, List<Int>>> {
            return fullRecord
                .zip(springGroups)
        }
    }

    fun findPossibleSpringArrangements(
        fullRecord: List<Char>,
        springGroups: List<Int>
    ): Int {
        val map = springGroups
            .map { group ->
                fullRecord
                    .windowed(group, 1)
                    .zip(fullRecord.indices)
                    .filter { (placedGroup, index) ->
                        placedGroup
                            .none { it == '.' }
                    }
                    .filter { (placedGroup, index) ->
                        index == 0 || fullRecord[index - 1] != '#'
                    }
                    .filter { (placedGroup, index) ->
                        index + group == fullRecord.size || fullRecord[index + group] != '#'
                    }
                    .map { (placedGroup, index) ->
                        IntRange(index, index + group - 1)
                    }
            }
        map.println()
        val filteredMap = map
            .drop(1)
            .scan(map.first()) { prev, group ->
            group
                .filter { placement ->
                    placement.first > prev.first().last + 1
                }
        }
        filteredMap.println()
        val permutationsOfLists = filteredMap
            .permutationsOfLists()
        permutationsOfLists.println()
        val result = permutationsOfLists
            .filter { permutations ->
                permutations
                    .windowed(2, 1)
                    .all { window -> window[0].last + 1 < window[1].first }
            }
        result.println()
        println("")
        return result
            .count()
    }

    fun Record.findPossibleSpringArrangements(): Int {
        return get()
            .map { (fullRecord, springGroups) ->
                findPossibleSpringArrangements(fullRecord, springGroups)
            }.sum()
    }

    fun parseInput(input: List<String>): Record {
        return Record(
            input
                .map { line -> line.split(" ")[0].toList() },
            input
                .map { line -> line.split(" ")[1].split(",").map(String::toInt) }
        )
    }

    fun part1(input: List<String>): Int {
        return parseInput(input)
            .findPossibleSpringArrangements()
    }

    fun part2(input: List<String>): Long {
        return 0
    }

//    listOf(
//        listOf(1, 2, 3),
//        listOf(4, 5, 6)
//    )
//        .permutationsOfLists()
//        .println()

    val testInput = readInput("12", "test_part1")
    check(part1(testInput) == 21)

    val testInput2 = readInput("12", "test_part2")
    check(part2(testInput2) == 0L)

    part1(readInput("12", "input")).println()
    part2(readInput("12", "input")).println()
}
