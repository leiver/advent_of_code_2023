import java.util.Comparator
import java.util.Comparator.comparing
import java.util.PriorityQueue
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Brick(val start: Coordinate3D, val end: Coordinate3D) {

        val supportedBy: MutableList<Brick> = mutableListOf()
        var supports: Set<Brick> = setOf()

        fun linkSupports(bricks: List<Brick>) {
            val (xRange, yRange, zRange) = ranges()
            supports = xRange
                .flatMap { x ->
                    yRange.flatMap { y ->
                        bricks
                            .filter { it != this }
                            .map { it to it.ranges() }
                            .filter { (_, ranges) ->
                                ranges.first.contains(x) && ranges.second.contains(y)
                            }
                            .filter { (_, ranges) ->
                                zRange.last + 1 == ranges.third.first
                            }
                            .map { it.first }
                    }
                }
                .toSet()
            supports
                .forEach { brick ->
                    brick.addSupport(this)
                }
        }

        fun addSupport(brick: Brick) {
            supportedBy.add(brick)
        }

        fun directionalLength(): Coordinate3D {
            return start.distanceTo(end)
                .plus(1)
        }

        fun ranges(): Triple<LongRange, LongRange, LongRange> {
            return Triple(
                LongRange(min(start.x, end.x), max(start.x, end.x)),
                LongRange(min(start.y, end.y), max(start.y, end.y)),
                LongRange(min(start.z, end.z), max(start.z, end.z))
            )
        }

        fun wouldFall(): Long {
            val queue: PriorityQueue<Brick> = PriorityQueue(comparing{ min(it.start.z, it.end.z) })
            queue.add(this)
            val fallenBricks: MutableSet<Brick> = mutableSetOf(this)

            while (queue.isNotEmpty()) {
                val currentBrick = queue.remove()

                val bricksThatWillFall = currentBrick
                    .supports
                    .filter { support ->
                        support
                            .supportedBy
                            .none { !fallenBricks.contains(it) }
                    }

                fallenBricks.addAll(bricksThatWillFall)
                queue.addAll(bricksThatWillFall)
            }
            return fallenBricks.size.toLong() - 1
        }
    }

    fun parseInput(input: List<String>): List<Brick> {
        return input
            .map { line ->
                val (start, end) = line.split("~")
                val (startX, startY, startZ) = start.split(",").map { it.toLong() }
                val (endX, endY, endZ) = end.split(",").map { it.toLong() }

                Brick(
                    Coordinate3D(startX, startY, startZ),
                    Coordinate3D(endX, endY, endZ)
                )
            }
    }

    fun List<Brick>.simulateFalling(): List<Brick> {
        val result = sortedBy { min(it.start.z, it.end.z) }
            .fold<Brick, MutableList<Brick>>(mutableListOf()) { fallenBricks, nextBrick ->
                val (xRange, yRange, zRange) = nextBrick.ranges()

                val highestZPlaced = xRange
                    .flatMap { x ->
                        yRange
                            .map { y ->
                                fallenBricks
                                    .map { it.ranges() }
                                    .filter { fallenBrickRanges ->
                                        x in fallenBrickRanges.first && y in fallenBrickRanges.second
                                    }
                                    .maxOfOrNull { it.third.last } ?: -1
                            }
                    }.max()
                fallenBricks.add(
                    Brick(
                        Coordinate3D(
                            nextBrick.start.x,
                            nextBrick.start.y,
                            highestZPlaced + 1
                        ),
                        Coordinate3D(
                            nextBrick.end.x,
                            nextBrick.end.y,
                            highestZPlaced + 1 + zRange.length()
                        )
                    )
                )
                fallenBricks
            }
        return result
    }

    fun List<Brick>.linkSupports(): List<Brick> {
        sortedBy { min(it.start.z, it.end.z) }
            .forEach { brick ->
                brick.linkSupports(this)
            }
        return this
    }

    fun List<Brick>.canBeDisintegrated(): Long {
        return filter { brick ->
            brick
                .supports
                .none { supportedBrick ->
                    supportedBrick.supportedBy.size == 1
                }
        }
            .count()
            .toLong()
    }

    fun List<Brick>.bricksThatWillFall(): Long {
        return sumOf { brick -> brick.wouldFall() }
    }

    fun part1(input: List<String>): Long {
        return parseInput(input)
            .simulateFalling()
            .linkSupports()
            .canBeDisintegrated()
    }

    fun part2(input: List<String>): Long {
        return parseInput(input)
            .simulateFalling()
            .linkSupports()
            .bricksThatWillFall()
    }

    val testInput = readInput("22", "test_part1")
    check(part1(testInput) == 5L)

    val testInput2 = readInput("22", "test_part1")
    check(part2(testInput2) == 7L)

    part1(readInput("22", "input")).println()
    part2(readInput("22", "input")).println()
}
