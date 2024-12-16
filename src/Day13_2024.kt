import Direction.NORTH
import Direction.SOUTH
import java.util.*

fun main() {

    fun part1(input: List<String>): Long {
        return input
            .joinToString(";")
            .split(";;")
            .map { rules ->
                var (a, b, prize) = rules
                    .split(";")
                    .map {
                        val (x, y) = it
                            .split(": ")[1].split(", ")
                        Coordinate(x.split("=", "+")[1].toInt(), y.split("=", "+")[1].toInt())
                    }
                var (worst, best) = if ((a.x + a.y) / 3 > b.x + b.y) {
                    Pair(a, b)
                } else {
                    Pair(b, a)
                }
                var result = 0L
                for (i in IntRange(0, 100)) {
                    var (remainderX, remainderY) = Pair(prize.x - (worst.x * i), prize.y - (worst.y * i))
                    if (remainderX < 0 || remainderY < 0) break
                    var (modX, modY) = Pair(remainderX % best.x, remainderY % best.y)
                    var (mutipleX, multipleY) = Pair(remainderX / best.x, remainderY / best.y)
                    if (modX == 0L && modY == 0L && mutipleX == multipleY) {
                        result = if (worst == a) {
                            (i * 3 + mutipleX)
                        } else {
                            (i + mutipleX * 3)
                        }
                        break
                    }
                }
                result
            }
            .sum()
    }

    fun part2(input: List<String>): Long {
        return input
            .joinToString(";")
            .split(";;")
            .map { rules ->
                var (a, b, prize) = rules
                    .split(";")
                    .map {
                        val (x, y) = it
                            .split(": ")[1].split(", ")
                        Coordinate(x.split("=", "+")[1].toInt(), y.split("=", "+")[1].toInt())
                    }
                prize = Coordinate(prize.x + 10000000000000, prize.y + 10000000000000)
                var (worst, best) = if ((a.x + a.y) / 3 > b.x + b.y) {
                    Pair(a, b)
                } else {
                    Pair(b, a)
                }
                var antallWorst = (prize.x * best.y - prize.y * best.x) / (worst.x * best.y - worst.y * best.x)
                var modFormel = (prize.x * best.y - prize.y * best.x) % (worst.x * best.y - worst.y * best.x)
                var (tilOversX, tilOversY) = Pair(prize.x - antallWorst * worst.x, prize.y - antallWorst * worst.y)
                if (modFormel == 0L && tilOversX % best.x == 0L && tilOversY % best.y == 0L && tilOversX / best.x == tilOversY / best.y) {
                    if (worst == a) {
                        (antallWorst * 3 + tilOversX / best.x)
                    } else {
                        (antallWorst + tilOversX / best.x * 3)
                    }
                } else {
                    0
                }
            }
            .sum()
    }

//    val testInput = readInput("12", "test_2024")
//    val resultPart1 = part1(testInput)
////    println(resultPart1)
//    check(resultPart1 == 1930L)
////
//    val testInput2 = readInput("12", "test_2024")
//    val resultPart2 = part2(testInput2)
////    println(resultPart2)
//    check(resultPart2 == 1206L)

    part1(readInput("13", "input_2024")).println()
    part2(readInput("13", "input_2024")).println()
}
