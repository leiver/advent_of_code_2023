fun main() {

    fun part1(input: List<String>): Int {
        val nestedList = input
            .map { it.replace("[^a-zA-Z0-9.]".toRegex(), "").split("").drop(1).dropLast(1) }
        val map = BoundedCoordinateMap(
            nestedList
        )
        val result = map.map
            .entries
            .filter { it.value != "." }
            .groupBy({ it.value }, { it.key })
//            .filter { "[a-zA-Z0-9]".toRegex().matches(it.key) }
            .flatMap { (type, spotsOfSameType) ->

                println("type: $type\nspots of same type:\n${spotsOfSameType.joinToString("\n")}")
                val permutations = spotsOfSameType
                    .permutations()
                println("type: $type\npairs:\n${permutations.joinToString("\n")}")
                permutations
                    .flatMap { (first, second) -> listOf(first.plus(first.minus(second)), second.plus(second.minus(first))) }
                    .distinct()
                    .filter { map.inbounds(it)  }
            }
            .distinct()
        println(result.joinToString("\n"))
        BoundedCoordinateMap(
            map.map
                .mapValues { if (result.contains(it.key)) "#" else it.value }
        )
            .printMapWithDefaults("")

        return result
            .count()
    }

    fun part2(input: List<String>): Int {
        val nestedList = input
            .map { it.replace("[^a-zA-Z0-9.]".toRegex(), "").split("").drop(1).dropLast(1) }
        val map = BoundedCoordinateMap(
            nestedList
        )
        val result = map.map
            .entries
            .asSequence()
            .filter { it.value != "." }
            .groupBy({ it.value }, { it.key })
            .filter { "[a-zA-Z0-9]".toRegex().matches(it.key) }
            .flatMap { (_, spotsOfSameType) ->

                val permutations = spotsOfSameType
                    .permutations()
                permutations
                    .flatMap { (first, second) ->
                        val distance = second.minus(first)
//                        println("$second-$first=$distance")
                        val pointsInMap = mutableListOf(first)
                        var pointer = first.plus(distance)
//                        print("adding:")
                        while (map.inbounds(pointer)) {
//                            print("$pointer, ")
                            pointsInMap.add(pointer)
                            pointer = pointer.plus(distance)
                        }
                        pointer = first.minus(distance)
                        while (map.inbounds(pointer)) {
                            pointsInMap.add(pointer)
                            pointer = pointer.minus(distance)
                        }
                        pointsInMap
                    }
                    .distinct()
            }
            .distinct()
//        println(result.joinToString("\n"))
//        BoundedCoordinateMap(
//            map.map
//                .mapValues { if (result.contains(it.key)) "#" else it.value }
//        )
//            .printMapWithDefaults("")

        return result
            .count()
    }

    val testInput = readInput("08", "test_2024")
    val testResult = part1(testInput)
    println(testResult)
    check(testResult == 14)
//
//    val testInput2 = readInput("08", "test_part2")
//    check(part2(testInput2) == 6L)

    part1(readInput("08", "input_2024")).println()
    part2(readInput("08", "input_2024")).println()
}
