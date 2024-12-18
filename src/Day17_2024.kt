import java.util.stream.IntStream
import kotlin.streams.toList

fun main() {

    fun comboOperand(a: Int, b: Int, c: Int, operand: Int): Int {
        if (operand < 4) {
            return operand
        } else if (operand == 4) {
            return a % 8
        } else if (operand == 5) {
            return b % 8
        } else {
            return c % 8
        }
    }

    fun part1(input: List<String>): String {
        var result = ""
        var (registers, instructionString) = input
            .joinToString(";")
            .split(";;")
            .map { it.split(";").map { it.split(": ")[1] } }
        var (aRegister, bRegister, cRegister) = registers
            .map { it.toInt() }
        var instructions = instructionString
            .first()
            .split(",")
            .map { it.toInt() }

        var pointer = 0
        while (pointer < instructions.size) {
            var instruction = instructions[pointer]
            var operand = instructions[pointer + 1]
            val comboOperand = comboOperand(aRegister, bRegister, cRegister, operand)
            var jump = false

            if (instruction == 0) {
                //adv
                aRegister = (aRegister / Math.pow(2.0, comboOperand.toDouble())).toInt()
            } else if (instruction == 1) {
                //bxl
                bRegister = bRegister.xor(operand)
            } else if (instruction == 2) {
                //bst
                bRegister = comboOperand % 8
            } else if (instruction == 3) {
                //jnz
                if (aRegister != 0) {
                    pointer = operand
                    jump = true
                }
            } else if (instruction == 4) {
                //bxc
                bRegister = bRegister.xor(cRegister)
            } else if (instruction == 5) {
                //out
                if (result.isNotEmpty()) {
                    result += ","
                }
                result += comboOperand
            } else if (instruction == 6) {
                //bdv
                bRegister = (aRegister / Math.pow(2.0, comboOperand.toDouble())).toInt()
            } else if (instruction == 7) {
                //cdv
                cRegister = (aRegister / Math.pow(2.0, comboOperand.toDouble())).toInt()
            }

            if (!jump) {
                pointer += 2
            }
        }
        return result
    }

    fun findContinuingAValue(a: String, nextOutput: Int): List<String> {
        var possibleValues: List<Pair<Int, Int>> = IntStream
            .range(0, 8)
            .mapToObj { Pair(it, nextOutput.xor(it)) }
            .toList()
        return possibleValues
            .asSequence()
            .map { (b, c) ->
                var res = Triple(b.xor(6).xor(5), b.xor(6), c)
                res
            }
            .map { (b, cIndex, c) ->
                var res = Triple(a + b.toString(2).padStart(3, '0'), cIndex, c)
                res
            }
            .filter { (newA, cIndex, c) ->
                newA.substring(newA.length - cIndex - 3, newA.length - cIndex) == c.toString(2).padStart(3, '0')
            }
            .map { it.first }
            .toList()
    }

    fun part2(input: List<String>): Long {
        var outputs = input[4].split(": ")[1].split(",").map { it.toInt() }
        return outputs
            .reversed()
            .asSequence()
            .fold(listOf("0000000"), { possibilities, nextResult -> possibilities.flatMap { findContinuingAValue(it, nextResult) } } )
            .map { it.toLong(2) }
            .min()
    }

    part1(readInput("17", "input_2024")).println()
    part2(readInput("17", "input_2024")).println()
}
