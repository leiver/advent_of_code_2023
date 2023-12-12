fun main() {

    data class Record(
        val fullRecord: List<List<Char>>,
        val springGroups: List<List<Int>>
    ) {
        fun get(): List<Pair<List<Char>, List<Int>>> {
            return fullRecord
                .zip(springGroups)
        }

        fun duplicate(times: Int): Record =
            Record(
                fullRecord
                    .map { fullRecordLine ->
                        IntRange(1, times - 1)
                            .map { fullRecordLine }
                            .fold(fullRecordLine.toMutableList()) { acc, next ->
                                acc.add('?')
                                acc.addAll(next)
                                acc
                            }
                    },
                springGroups
                    .map { springGroupsLine ->
                        IntRange(1, times - 1)
                            .map { springGroupsLine }
                            .fold(springGroupsLine.toMutableList()) { acc, next ->
                                acc.addAll(next)
                                acc
                            }
                    }
            )

    }

    fun findPossibleSpringArrangementsV2(
        fullRecord: List<Char>,
        springGroups: List<Int>
    ): Long {
        val knownDamagedSprings = fullRecord
            .zip(fullRecord.indices)
            .filter { it.first == '#' }
            .map { it.second }
        var placedGroups: List<Pair<Int, List<IntRange>>> = springGroups
            .map { group ->
                group to fullRecord
                    .windowed(group, 1)
                    .zip(fullRecord.indices)
                    .filter { (placedGroup, _) ->
                        placedGroup
                            .none { it == '.' }
                    }
                    .filter { (_, index) ->
                        index == 0 || fullRecord[index - 1] != '#'
                    }
                    .filter { (_, index) ->
                        index + group == fullRecord.size || fullRecord[index + group] != '#'
                    }
                    .map { (_, index) ->
                        IntRange(index, index + group - 1)
                    }
                    .fold(mutableListOf<IntRange>()) { acc, next ->
                        var prev = acc.lastOrNull()
                        if (prev != null && prev.last + 1 == next.last) {
                            acc.removeLast()
                            acc.add(prev union next)
                        } else {
                            acc.add(next)
                        }
                        acc
                    }
            }
        placedGroups = placedGroups
            .mapIndexed { index, firstOrLast ->
                firstOrLast.first to if (index == 0) {
                    firstOrLast.second.filter { knownDamagedSprings.isEmpty() || it.first <= knownDamagedSprings.first() }
                } else if (index == placedGroups.size - 1) {
                    firstOrLast.second.filter { knownDamagedSprings.isEmpty() || it.last >= knownDamagedSprings.last() }
                } else firstOrLast.second
            }

        val filteredPlacedGroupsOneWay = placedGroups
            .drop(1)
            .scan(placedGroups.first()) { prev, next ->
                val earliestPlacedPrev = IntRange(0, prev.second.first().first + prev.first + 1)
                val checkit = next
                    .second
                    .flatMap { placedGroup -> placedGroup except earliestPlacedPrev }
                    .filter { placedGroup -> placedGroup.length() >= next.first }
                next.first to checkit
            }
        val filteredPlacedGroups = filteredPlacedGroupsOneWay
            .reversed()
            .drop(1)
            .scan(filteredPlacedGroupsOneWay.last()) { prev, next ->
                val latestPlacedPrev = IntRange(prev.second.last().last - prev.first - 1, fullRecord.size)
                next.first to next
                    .second
                    .flatMap { placedGroup -> placedGroup except latestPlacedPrev }
                    .filter { placedGroup -> placedGroup.length() >= next.first }
            }
            .reversed()

        val result = filteredPlacedGroups
            .drop(1)
            .fold(
                filteredPlacedGroups
                    .first()
                    .second
                    .flatMap { it.windowed(filteredPlacedGroups.first().first, 1).map { index -> index.last() to 1L } }
            ) { prev, next ->
                val result = next
                    .second
                    .flatMap { it.windowed(next.first, 1).map { index -> IntRange(index.first(), index.last()) to 0L } }
                    .map { nextStep ->
                        val lastKnownSpring = knownDamagedSprings
                            .filter { it < nextStep.first.first }
                            .maxOrNull()
                        nextStep.first.last to
                                prev
                                    .filter { nextStep.first.first > it.first + 1 }
                                    .filter { lastKnownSpring == null || lastKnownSpring <= it.first }
                                    .sumOf { it.second } + nextStep.second
                    }
                result

            }
            .sumOf { it.second }
        return result
    }

    fun Record.findPossibleSpringArrangements(): Long {
        return get()
            .map { (fullRecord, springGroups) ->
                findPossibleSpringArrangementsV2(fullRecord, springGroups)
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

    fun part1(input: List<String>): Long {
        return parseInput(input)
            .findPossibleSpringArrangements()
    }

    fun part2(input: List<String>): Long {
        return parseInput(input)
            .duplicate(5)
            .findPossibleSpringArrangements()
    }

    val testInput = readInput("12", "test_part1")
    check(part1(testInput) == 21L)

    val testInput2 = readInput("12", "test_part1")
    check(part2(testInput2) == 525152L)

    part1(readInput("12", "input")).println()
    part2(readInput("12", "input")).println()
}
