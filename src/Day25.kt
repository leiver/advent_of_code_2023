import java.util.*

fun main() {

    data class Component(val name: String, val connectionStrings: List<String>, val connections: MutableList<Component> = mutableListOf()) {
        override fun toString(): String = "${name}: ${connections.map { it.name }}"
        override fun hashCode(): Int = Objects.hash(name, connections.map { it.name })
    }

    fun parseInput(input: List<String>): Map<String, Component> {
        val map = input
            .map { line ->
                val (component, connectionString) = line.split(": ")
                val connections = connectionString.split(" ")

                Component(component, connections)
            }
            .associateBy { it.name }
        val mapCopy = map.toMutableMap()
        map.values
            .forEach { component ->
                val connectedComponents = component
                    .connectionStrings
                    .map { connectionString ->
                        mapCopy.computeIfAbsent(connectionString) { Component(connectionString, listOf()) }
                    }
                component.connections += connectedComponents
                connectedComponents.forEach { connectedComponent -> connectedComponent.connections += component }
            }
        return mapCopy
    }

    fun part1(input: List<String>): Long {
        val components = parseInput(input)

        val startComponent = components
            .values
            .minBy { it.connections.size }

        val pickedComponents = mutableSetOf(startComponent)
        var connections: MutableMap<Component, List<Component>> = startComponent.connections.map { it to listOf(startComponent) }.associate { it }.toMutableMap()
        while (connections.values.sumOf { it.size } > 3) {
            val nextComponent = connections
                .entries
                .sortedWith(
                    Comparator.comparing<Map.Entry<Component, List<Component>>, Int> {
                        it.value.size
                    }
                        .reversed()
                        .thenComparing(
                            Comparator.comparing {
                                it.key.connections.count { connection -> !pickedComponents.contains(connection) }
                            }
                        )
                )
                .first()
            pickedComponents.add(nextComponent.key)
            connections.remove(nextComponent.key)
            nextComponent
                .key
                .connections
                .filter { !pickedComponents.contains(it) }
                .forEach { connection ->
                    connections.merge(connection, listOf(nextComponent.key)) {prev, next -> prev + next}
                }
        }

        return pickedComponents.size.toLong() * (components.size - pickedComponents.size).toLong()
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val testInput = readInput("25", "test_part1")
//    part1(testInput).println()
    check(part1(testInput) == 54L)

    val testInput2 = readInput("25", "test_part1")
    check(part2(testInput2) == 0L)

    part1(readInput("25", "input")).println()
    part2(readInput("25", "input")).println()
}
