fun main() {

    data class Node(val name: String, val left: String, val right: String) {
        var linkLeft: Node = this
        var linkRight: Node = this
        val memoization: MutableMap<String, Pair<List<Node>, List<Int>>> = mutableMapOf()
        val visitedNodes: MutableMap<Node, MutableList<Int>> = mutableMapOf(this to mutableListOf(0))

        fun linkNodes(leftNode: Node, rightNode: Node) {
            linkLeft = leftNode
            linkRight = rightNode
        }

        fun getDirection(instruction: Char): String {
            if (instruction == 'R') {
                return right
            } else {
                return left
            }
        }

        fun getDirectionLinked(instruction: Char): Node {
            if (instruction == 'R') {
                return linkRight
            } else {
                return linkLeft
            }
        }
    }

    class DesertMap(
        val map: Map<String, Node>,
        val instructions: String
    ) {
        var currentInstruction: Int = 0
        var stepsTaken: Int = 0
        var currentNodes: List<Node> = listOf()

        fun traverseMapPart1(): Int {
            currentNodes = listOf(map["AAA"]!!)
            while (currentNodes[0].name != "ZZZ") {
                currentNodes = listOf(map[currentNodes[0].getDirection(instructions[currentInstruction])]!!)
                currentInstruction = (currentInstruction + 1) % instructions.length
                stepsTaken++
            }
            return stepsTaken
        }

        fun linkNodes(): DesertMap {
            map
                .values
                .forEach { it.linkNodes(map[it.left]!!, map[it.right]!!) }
            return this
        }

        fun findFirstZ(node: Node): Long {
            var currentNode = node
            var currentIndex = 0

            while (true) {
                currentNode = currentNode.getDirectionLinked(instructions[currentIndex % instructions.length])
                currentIndex++
                if (currentNode.name.endsWith("Z")) {
                    return currentIndex.toLong()
                }
            }
        }

        fun traverseAToZ(): List<Long> {
            val nodes = map
                .values
                .filter { it.name.endsWith("A") }
            return nodes.map(::findFirstZ)
        }
    }

    fun parseInput(input: List<String>): DesertMap {
        val instructions = input.first()

        val nodeMap = input
            .drop(2)
            .map { "(...) = \\((...), (...)\\)".toRegex().matchEntire(it)!!.destructured }
            .map { (node, left, right) -> Node(node, left, right) }
            .associateBy(Node::name)

        return DesertMap(
            nodeMap,
            instructions
        )
    }

    fun part1(input: List<String>): Int {
        return parseInput(input)
            .traverseMapPart1()
    }

    fun part2(input: List<String>): Long {
        return lcm(
            parseInput(input)
                .linkNodes()
                .traverseAToZ()
        )
    }

    val testInput = readInput("08", "test_part1")
    check(part1(testInput) == 2)

    val testInput2 = readInput("08", "test_part2")
    check(part2(testInput2) == 6L)

    part1(readInput("08", "input")).println()
    part2(readInput("08", "input")).println()
}
