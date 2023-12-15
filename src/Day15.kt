import Direction.*

fun main() {

    data class Lens(val label: String, val focalLength: Int)

    fun hash(label: String): Long = label
        .fold(0L) { sum, next ->
            ((sum + next.code) * 17) % 256
        }

    fun part1(input: List<String>): Long {
        return input[0]
            .split(",")
            .map(::hash).sum()

    }

    fun part2(input: List<String>): Long {
        return input[0]
            .split(",")
            .fold(mutableMapOf<Long, MutableList<Lens>>()) { lensMap, instruction ->
                val (label, operator, focalLength) = "(.*)(=|-)(\\d)?".toRegex().matchEntire(instruction)!!.destructured
                val box = hash(label)
                if (operator == "=") {
                    val lens = Lens(label, focalLength.toInt())
                    val lenses = lensMap.getOrDefault(box, mutableListOf())
                    if (lenses.any { it.label == label }) {
                        val existingIndex = lenses.indexOfFirst { it.label == label }
                        lenses[existingIndex] = lens
                    } else {
                        lenses.add(lens)
                    }
                    lensMap[box] = lenses
                } else {
                    lensMap[box]?.removeAll { it.label == label }
                }
                lensMap
            }
            .map { (box, lenses) ->
                lenses.zip(lenses.indices).sumOf { (lens, index) -> (box + 1L) * (index + 1L) * lens.focalLength.toLong() }
            }
            .sum()
    }

    val testInput = readInput("15", "test_part1")
    check(part1(testInput) == 1320L)

    val testInput2 = readInput("15", "test_part1")
    check(part2(testInput2) == 145L)

    part1(readInput("15", "input")).println()
    part2(readInput("15", "input")).println()
}
