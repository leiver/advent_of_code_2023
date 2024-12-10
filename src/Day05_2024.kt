fun main() {

    fun part1(input: List<String>): Int {
        val (regler, updates) = input
            .joinToString(";")
            .split(";;")

        val regelMap: MutableMap<String, Set<String>> = regler
            .split(";")
            .groupBy({ it.split("|")[0] }, { it.split("|")[1] })
            .toMap()
            .mapValues { it.value.toSet() }
            .toMutableMap()

        return updates
            .split(";")
            .map { update ->
                update
                    .split(",")
            }
            .filter { update ->
                val gjennomgåtte: HashSet<String> = HashSet()
                update
                    .none { tall ->
                        gjennomgåtte.add(tall)
                        val regel = regelMap.computeIfAbsent(tall) { setOf() }
                        (gjennomgåtte intersect regel).isNotEmpty()
                    }
            }
            .sumOf { update -> update[update.size / 2].toInt() }
    }

    fun part2(input: List<String>): Int {
        val (regler, updates) = input
            .joinToString(";")
            .split(";;")

        val regelMap: MutableMap<String, Set<String>> = regler
            .split(";")
            .groupBy({ it.split("|")[0] }, { it.split("|")[1] })
            .toMap()
            .mapValues { it.value.toSet() }
            .toMutableMap()
        val omvendtRegelMap: MutableMap<String, Set<String>> = regler
            .split(";")
            .groupBy({ it.split("|")[1] }, { it.split("|")[0] })
            .toMap()
            .mapValues { it.value.toSet() }
            .toMutableMap()
        return updates
            .split(";")
            .map { update ->
                update
                    .split(",")
            }
            .filter { update ->
                val gjennomgåtte: HashSet<String> = HashSet()
                update
                    .any { tall ->
                        gjennomgåtte.add(tall)
                        val regel = regelMap.computeIfAbsent(tall) { setOf() }
                        (gjennomgåtte intersect regel).isNotEmpty()
                    }
            }
            .map { update ->
                val gjennstående = update.toMutableSet()
                val reordered = mutableListOf<String>()
                while (gjennstående.isNotEmpty()) {
                    val neste = gjennstående
                        .first { tall -> (gjennstående intersect omvendtRegelMap.computeIfAbsent(tall) { setOf() }).isEmpty() }
                    gjennstående.remove(neste)
                    reordered.add(neste)
                }
                reordered.reverse()
                reordered
            }
            .sumOf { update -> update[update.size / 2].toInt() }
    }

//    val testInput = readInput("01", "test_part1")
//    check(part1(testInput) == 142)
//
//    val testInput2 = readInput("01", "test_part2")
//    check(part2(testInput2) == 281)

    part1(readInput("05", "input_2024")).println()
    part2(readInput("05", "input_2024")).println()
}
