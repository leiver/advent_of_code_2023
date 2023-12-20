import ModuleType.*
import java.util.*
import kotlin.collections.ArrayDeque

enum class ModuleType {
    BROADCAST,
    FLIP_FLOP,
    CONJUNCTION
}

fun main() {

    data class Module(val name: String, val outputs: List<String>, val type: ModuleType) {
        val memory: MutableMap<String, Int> = mutableMapOf()
        var state: Int = 0
        var linkedOutputs: List<Module> = listOf()

        fun initializeMemory(from: String) {
            memory[from] = 0
        }

        fun linkOutputs(modules: Map<String, Module>) {
            linkedOutputs = outputs
                .filter { modules.containsKey(it) }
                .map { modules[it]!! }
        }

        private fun handleBroadcastSignal(signal: Int): List<Pair<Int, String>> {
            return outputs.map { output -> signal to output }
        }

        private fun handleFlipFlopSignal(signal: Int): List<Pair<Int, String>> {
            if (signal == 0) {
                state = (state + 1) % 2
                return outputs.map { output -> state to output }
            }
            return listOf()
        }

        private fun handleConjunctionModule(signal: Int, from: String): List<Pair<Int, String>> {
            memory[from] = signal
            if (memory.values.any { it == 0 }) {
                return outputs.map { output -> 1 to output }
            } else {
                return outputs.map { output -> 0 to output }
            }
        }

        fun receiveSignal(signal: Int, from: String): List<Pair<Int, String>> {
            return when (type) {
                BROADCAST -> handleBroadcastSignal(signal)
                FLIP_FLOP -> handleFlipFlopSignal(signal)
                CONJUNCTION -> handleConjunctionModule(signal, from)
            }
        }
    }

    fun parseInput(input: List<String>): Map<String, Module> {
        val map = input
            .map { line ->
                val (name, output) = line.split(" -> ")
                val outputList = output.split(", ")
                val type = if (name.startsWith("%")) {
                    FLIP_FLOP
                } else if (name.startsWith("&")) {
                    CONJUNCTION
                } else {
                    BROADCAST
                }

                Module(name.removePrefix("%").removePrefix("&"), outputList, type)
            }
            .associateBy { it.name }
        map.values
            .forEach { module ->
                module
                    .outputs
                    .filter { output -> map[output] != null && map[output]!!.type == CONJUNCTION }
                    .forEach { output ->
                        map[output]!!.initializeMemory(module.name)
                    }
                module.linkOutputs(map)
            }
        return map
    }

    fun pushButton(modules: Map<String, Module>): Pair<Long, Long> {
        val queue: ArrayDeque<Pair<String, Pair<Int, String>>> = ArrayDeque()
        queue.add("button" to (0 to "broadcaster"))
        var lowSignalsSent = 0L
        var highSignalsSent = 0L
        while (queue.isNotEmpty()) {
            val (from, transmission) = queue.removeFirst()
            val (signal, output) = transmission
            if (signal == 0) {
                lowSignalsSent++
            } else {
                highSignalsSent++
            }
            if (modules.containsKey(output)) {
                val module = modules[output]!!
                val signals = module.receiveSignal(signal, from)
                    .map { signalOutput -> module.name to signalOutput }
                queue.addAll(
                    signals
                )
            }
        }
        return lowSignalsSent to highSignalsSent
    }

    fun pushButton(times: Long, modules: Map<String, Module>): Long {
        val highsAndLows = LongRange(1, times)
            .map {
                pushButton(modules)
            }
            .reduce { (accLow, accHigh), (nextLow, nextHigh) ->
                (accLow + nextLow) to (accHigh + nextHigh)
            }
        return highsAndLows.first * highsAndLows.second
    }

    fun part1(input: List<String>): Long {
        return pushButton(
            1000L,
            parseInput(input)
        )
    }

    fun findLoops(modules: Map<String, Module>): List<Long> {
        val branches = modules["broadcaster"]!!.linkedOutputs
        return branches
            .map { startNode ->
                var currentNode = startNode
                var binaryNumber = "1"

                while (currentNode.linkedOutputs.any { it.type == FLIP_FLOP }) {
                    currentNode = currentNode.linkedOutputs.first { it.type == FLIP_FLOP }
                    if (currentNode.linkedOutputs.any { it.type == CONJUNCTION }) {
                        binaryNumber += "1"
                    } else {
                        binaryNumber += "0"
                    }
                }

                binaryNumber.reversed().toLong(2)
            }
    }

    fun part2(input: List<String>): Long {
        return lcmFromGcd(
            findLoops(
                parseInput(input)
            )
        )
    }


    val testInput = readInput("20", "test_part1")
    check(part1(testInput) == 32000000L)

    val testInput2 = readInput("20", "test_part2")
    check(part1(testInput2) == 11687500L)

    part1(readInput("20", "input")).println()
    part2(readInput("20", "input")).println()
}
