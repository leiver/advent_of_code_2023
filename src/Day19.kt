@OptIn(ExperimentalStdlibApi::class)
fun main() {

    data class Part(val variables: Map<Char, Long>)

    data class Rule(val variable: Char, val operation: Char, val amount: Long, val destination: String) {
        fun partMatches(part: Part): Boolean {
            val amountForVariable = part.variables[variable]!!
            return when (operation) {
                '<' -> amountForVariable < amount
                '>' -> amountForVariable > amount
                else -> false
            }
        }

        fun acceptedRange(): LongRange {
            return when (operation) {
                '<' -> LongRange(0, amount - 1)
                '>' -> LongRange(amount + 1, 4000)
                else -> LongRange(-1, 0)
            }
        }

        fun declinedRange(): LongRange {
            return when (operation) {
                '>' -> LongRange(0, amount)
                '<' -> LongRange(amount, 4000)
                else -> LongRange(-1, 0)
            }
        }
    }

    data class RuleSet(val name: String, val rules: List<Rule>, val default: String) {

        fun sendPart(part: Part): String {
            return rules
                .firstOrNull() { rule -> rule.partMatches(part) }
                ?.destination ?: default
        }
    }

    fun parseInput(input: List<String>): Pair<Map<String, RuleSet>, List<Part>> {
        val sections = input
            .joinToString(";")
            .split(";;")

        return Pair(
            sections[0]
                .split(";")
                .map { line ->
                    "(.*)\\{([xmas][<>]\\d+:.*,)+(.*)}".toRegex().matchEntire(line)!!.groupValues
                }
                .map { regexResult ->
                    val ruleName = regexResult[1]
                    val default = regexResult.last()
                    val rules = regexResult[2]
                        .split(",")
                        .dropLast(1)
                        .map { rule ->
                            "([xmas])([<>])(\\d+):(.*)".toRegex().matchEntire(rule)!!.destructured
                        }
                        .map { (variable, operation, amount, destination) ->
                            Rule(variable.first(), operation.first(), amount.toLong(), destination)
                        }
                    RuleSet(ruleName, rules, default)
                }
                .associateBy { it.name },
            sections[1]
                .split(";")
                .map { line ->
                    Part(
                        line
                            .split(",")
                            .map { "([xmas])=(\\d+)".toRegex().find(it)!!.destructured }
                            .map { (variable, amount) ->
                                variable.first() to amount.toLong()
                            }
                            .associate { it }
                    )
                }
        )

    }

    fun acceptedParts(parts: List<Part>, ruleSets: Map<String, RuleSet>): List<Part> {
        return parts
            .filter { part ->
                var currentWorkflow = "in"
                while (currentWorkflow != "A" && currentWorkflow != "R") {
                    currentWorkflow = ruleSets[currentWorkflow]!!.sendPart(part)
                }
                currentWorkflow == "A"
            }
    }

    fun findCombinationsTo(
        destination: String,
        rulesets: Map<String, RuleSet>,
        variableRanges: Map<Char, List<LongRange>>
    ): Map<Char, List<LongRange>> {
        if (destination == "in") {
            return variableRanges
        }
        val modifiedMap = rulesets
            .values
            .filter { ruleset ->
                ruleset.default == destination || ruleset.rules.any { rule -> rule.destination == destination }
            }
            .map { ruleset ->
                val mapFromRules = ruleset
                    .rules
                    .filter { rule -> rule.destination == destination }
                    .map { rule ->
                        variableRanges
                            .entries
                            .map { entry ->
                                if (entry.key == rule.variable) {
                                    entry.key to entry.value.map { range -> range.intersect(rule.acceptedRange()) }
                                } else entry.key to entry.value
                            }
                            .associate { it }
                            .toMutableMap()
                    }
                    .reduceOrNull { acc, map ->
                        map
                            .entries
                            .forEach { (key, value) ->
                                acc.merge(key, value) { prev, next ->
                                    prev.map { range ->
                                        next.fold(range) { acc2, next2 -> if (acc2.overlap(next2)) acc2.union(next2) else acc2 }
                                    }
                                }
                            }
                        acc
                    }

                if (ruleset.default == destination) {
                    val mapFromDefault = variableRanges
                        .entries
                        .map { entry ->
                            entry.key to ruleset
                                .rules
                                .filter { rule -> rule.variable == entry.key }
                                .fold(entry.value) { acc, rule -> acc.map { range -> range.intersect(rule.declinedRange()) } }
                        }
                        .associate { it }

                    if (mapFromRules != null) {
                        mapFromDefault
                            .entries
                            .forEach { (key, value) ->
                                mapFromRules.merge(key, value) { prev, next ->
                                    prev.map { range ->
                                        next.fold(range) { acc, next2 -> if (acc.overlap(next2)) acc.union(next2) else acc }
                                    }
                                }
                            }
                    } else {
                        return@map ruleset to mapFromDefault.toMutableMap()
                    }
                }
                return@map ruleset to mapFromRules!!
            }
            .filter { it.second.values.any { ranges -> ranges.any { range -> !range.isEmpty() } } }
            .map { findCombinationsTo(it.first.name, rulesets, it.second) }
            .reduceOrNull { acc, map ->
                val result = acc.toMutableMap()
                map
                    .entries
                    .forEach { (key, value) ->
                        result.merge(key, value) { prev, next ->
                            prev.map { range ->
                                next.fold(range) { acc2, next2 -> if (acc2.overlap(next2)) acc2.union(next2) else acc2 }
                            }
                        }
                    }
                result
            }
        return mapOf()
    }

    fun possibleAcceptedCombinations(
        from: String,
        visitedRules: List<String>,
        currentlyAcceptedRanges: Map<Char, LongRange>,
        rulesets: Map<String, RuleSet>
    ): Long {
        if (from == "A") {
            return currentlyAcceptedRanges
                .values
                .fold(1L) { acc, next -> acc * (next.length() + 1) }
        } else if (from == "R" || visitedRules.contains(from)) {
            return 0
        }
        val addedVisitedRules = visitedRules + listOf(from)

        val ruleset = rulesets[from]!!

        val rangesAndCombinations = ruleset
            .rules
            .scan(currentlyAcceptedRanges to 0L) { (ranges, _), rule ->
                var acceptedCombinations = 0L
                if (ranges[rule.variable]!!.overlap(rule.acceptedRange()) && ranges.values.none { it.isEmpty() }) {
                    val newRanges = ranges
                        .entries
                        .map { (key, value) ->
                            if (key == rule.variable) {
                                key to value.intersect(rule.acceptedRange())
                            } else key to value
                        }
                        .associate { it }

                    if (newRanges.values.none { it.isEmpty() }) {
                        acceptedCombinations =
                            possibleAcceptedCombinations(rule.destination, addedVisitedRules, newRanges, rulesets)
                    }
                }
                ranges
                    .entries
                    .map { (key, value) ->
                        if (key == rule.variable && !value.isEmpty()) {
                            key to value.intersect(rule.declinedRange())
                        } else key to value
                    }
                    .associate { it } to acceptedCombinations
            }

        var defaultCombinations = 0L
        if (rangesAndCombinations.last().first.values.none { it.isEmpty() }) {
            defaultCombinations =
                possibleAcceptedCombinations(ruleset.default, addedVisitedRules, rangesAndCombinations.last().first, rulesets)
        }

        return rangesAndCombinations
            .sumOf { it.second } + defaultCombinations
    }

    fun part1(input: List<String>): Long {
        val parsedInput = parseInput(input)
        return acceptedParts(parsedInput.second, parsedInput.first)
            .sumOf { part ->
                part.variables.values.sum()
            }
    }

    fun part2(input: List<String>): Long {
        val parsedInput = parseInput(input)
        return possibleAcceptedCombinations(
            "in",
            listOf(),
            mapOf(
                'x' to LongRange(1, 4000),
                'm' to LongRange(1, 4000),
                'a' to LongRange(1, 4000),
                's' to LongRange(1, 4000)
            ),
            parsedInput.first
        )
    }

    val testInput = readInput("19", "test_part1")
    check(part1(testInput) == 19114L)

    val testInput2 = readInput("19", "test_part1")
    check(part2(testInput2) == 167409079868000L)

    part1(readInput("19", "input")).println()
    part2(readInput("19", "input")).println()
}
