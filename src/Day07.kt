import java.util.Comparator

fun main() {

    data class Hand(val hand: String, val bid: Int) {
        constructor(input: String): this (
            input.split(" ")[0]
                .replace("T", "a")
                .replace("J", "b")
                .replace("Q", "c")
                .replace("K", "d")
                .replace("A", "e"),
            input.split(" ")[1].toInt()
        )

        fun replaceJWith1(): Hand {
            return Hand(
                hand.replace("b", "1"),
                bid
            )
        }

        fun getRank(): Int {
            val cardCounts = hand
                .fold(HashMap<Char, Int>()) { acc, card ->
                    acc.put(
                        card,
                        acc.getOrDefault(card, 0) + 1
                    )
                    acc
                }
            val jokers: Int = cardCounts.remove('1') ?: 0
            return if (jokers == 5 || cardCounts.values.max() + jokers == 5) {
                7
            } else if (cardCounts.values.max() + jokers == 4) {
                6
            } else if (cardCounts.values.contains(3) && cardCounts.values.contains(2)) {
                5
            } else if (cardCounts.values.filter { it == 2 }.count() == 2 && jokers == 1) {
                5
            } else if (cardCounts.values.max() + jokers == 3) {
                4
            } else if (cardCounts.values.filter { it == 2 }.count() == 2) {
                3
            } else if (cardCounts.values.max() + jokers == 2) {
                2
            } else {
                1
            }
        }

        fun combineRankWithHand(): String {
            return "${getRank()}:${hand}"
        }
    }

    fun part1(input: List<String>): Int {
        return input
            .map { Hand(it) }
            .sortedWith(Comparator.comparing(Hand::combineRankWithHand))
            .zip(input.indices)
            .map { it.first.bid * (it.second + 1) }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input
            .map { Hand(it) }
            .map(Hand::replaceJWith1)
            .sortedWith(Comparator.comparing(Hand::combineRankWithHand))
            .zip(input.indices)
            .map { it.first.bid * (it.second + 1) }
            .sum()
    }

    val testInput = readInput("07", "test")
    check(part1(testInput) == 6440)

    val testInput2 = readInput("07", "test")
    check(part2(testInput2) == 5905)

    part1(readInput("07", "input")).println()
    part2(readInput("07", "input")).println()
}
