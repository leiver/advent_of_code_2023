import kotlin.math.absoluteValue

fun main() {

    data class Hail(val initialPoint: Coordinate3D, val velocity: Coordinate3D) {

        private var nextPoint: Coordinate3D? = null

        constructor(
            x: String,
            y: String,
            z: String,
            xVelocity: String,
            yVelocity: String,
            zVelocity: String
        ) : this(
            Coordinate3D(
                x.toLong(),
                y.toLong(),
                z.toLong()
            ),
            Coordinate3D(
                xVelocity.toLong(),
                yVelocity.toLong(),
                zVelocity.toLong()
            )
        )

        fun nextPoint(): Coordinate3D {
            if (nextPoint == null) {
                nextPoint = initialPoint.plus(velocity)
            }
            return nextPoint!!
        }

        fun intersectsXAndY(other: Hail, bounds: LongRange): Boolean {
            val slope = velocity.y.toDouble() / velocity.x.toDouble()
            val otherSlope = other.velocity.y.toDouble() / other.velocity.x.toDouble()

            val yAt0 = initialPoint.y.toDouble() + velocity.y.toDouble() * initialPoint.x.toDouble() / velocity.x.toDouble() * -1.0
            val otherYAt0 = other.initialPoint.y.toDouble() + other.velocity.y.toDouble() * other.initialPoint.x.toDouble() / other.velocity.x.toDouble() * -1.0
            kotlin.io.println()
            println("slope: ${slope}, otherSlope: ${otherSlope}, y at 0: ${yAt0}, other y at 0: ${otherYAt0}")
            if (slope == otherSlope) {
                println("${this} and ${other}\n\tis parallell")
                return false
            } else {
                val intersection = Coordinate(
                    ((otherYAt0 - yAt0) / (slope - otherSlope)).toLong(),
                    (slope * ((otherYAt0 - yAt0) / (slope - otherSlope)) + yAt0).toLong()
                )
                println("${this} and ${other}\n\tintersects at ${intersection}")
                if (bounds.contains(intersection.x) && bounds.contains(intersection.y)) {
                    val inFutureForX = intersection.x - initialPoint.x == 0L ||
                                (intersection.x - initialPoint.x) / (intersection.x - initialPoint.x).absoluteValue == velocity.x / velocity.x.absoluteValue
                    val inFutureForY = intersection.y - initialPoint.y == 0L ||
                            (intersection.y - initialPoint.y) / (intersection.y - initialPoint.y).absoluteValue == velocity.y / velocity.y.absoluteValue
                    val inFutureForOtherX = intersection.x - other.initialPoint.x == 0L ||
                            (intersection.x - other.initialPoint.x) / (intersection.x - other.initialPoint.x).absoluteValue == other.velocity.x / other.velocity.x.absoluteValue
                    val inFutureForOtherY = intersection.y - other.initialPoint.y == 0L ||
                            (intersection.y - other.initialPoint.y) / (intersection.y - other.initialPoint.y).absoluteValue == other.velocity.y / other.velocity.y.absoluteValue
                    return inFutureForX && inFutureForY && inFutureForOtherX && inFutureForOtherY
                } else return false
            }
        }
    }

    fun parseInput(input: List<String>): List<Hail> {
        return input
            .map {
                "(\\d+), (\\d+), (\\d+) @  ?(-?\\d+),  ?(-?\\d+),  ?(-?\\d+)".toRegex().matchEntire(it)!!.destructured
            }
            .map { (x, y, z, xVel, yVel, zVel) ->
                Hail(
                    x, y, z,
                    xVel, yVel, zVel
                )
            }
    }

    fun List<Hail>.findIntersectionsInXAndY(bounds: LongRange): Long {
        return dropLast(1)
            .mapIndexed { index, hail ->
                val nextPoint = hail.nextPoint()
                subList(index + 1, size)
                    .count { otherHail -> hail.intersectsXAndY(otherHail, bounds) }
                    .toLong()
            }.sum()
    }

    fun part1(input: List<String>, bounds: LongRange): Long {
        return parseInput(input)
            .findIntersectionsInXAndY(bounds)
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val testInput = readInput("24", "test_part1")
    part1(testInput, LongRange(7, 27)).println()
    check(part1(testInput, LongRange(7, 27)) == 2L)

    val testInput2 = readInput("24", "test_part1")
    check(part2(testInput2) == 0L)

    part1(readInput("24", "input"), LongRange(200000000000000, 400000000000000)).println()
    part2(readInput("24", "input")).println()
}

//                        println("${hail} -> ${otherHail}")
//                        val otherNextPoint = otherHail.nextPoint()
//                        val denominator =
//                            (hail.initialPoint.x - nextPoint.x) * (otherHail.initialPoint.y - otherNextPoint.y) - (hail.initialPoint.y - nextPoint.y) * (otherHail.initialPoint.x - otherNextPoint.x)
//                        if (denominator == 0L) {
//                            null
//                        } else {
//                            val intersection.x =
//                                (hail.initialPoint.x * nextPoint.y - hail.initialPoint.y * nextPoint.x) * (otherHail.initialPoint.x - otherNextPoint.x) - (hail.initialPoint.x - nextPoint.x) * (otherHail.initialPoint.x * otherNextPoint.y - otherHail.initialPoint.y * otherNextPoint.x) / denominator
//                            val intersectionY =
//                                (hail.initialPoint.x * nextPoint.y - hail.initialPoint.y * nextPoint.x) * (otherHail.initialPoint.y - otherNextPoint.y) - (hail.initialPoint.y - nextPoint.y) * (otherHail.initialPoint.x * otherNextPoint.y - otherHail.initialPoint.y * otherNextPoint.x) / denominator
//
//                            println("${hail} and ${otherHail} \n\tintersects at ${intersectionX}, ${intersectionY}")
//                            val inFutureForX = intersectionX - hail.initialPoint.x == 0L ||
//                                (intersectionX - hail.initialPoint.x) / (intersectionX - hail.initialPoint.x).absoluteValue == hail.velocity.x / hail.velocity.x.absoluteValue
//                            val inFutureForY = intersectionY - hail.initialPoint.y == 0L ||
//                                (intersectionY - hail.initialPoint.y) / (intersectionY - hail.initialPoint.y).absoluteValue == hail.velocity.y / hail.velocity.y.absoluteValue
//                            val inFutureForOtherX = intersectionX - otherHail.initialPoint.x == 0L ||
//                                (intersectionX - otherHail.initialPoint.x) / (intersectionX - otherHail.initialPoint.x).absoluteValue == otherHail.velocity.x / otherHail.velocity.x.absoluteValue
//                            val inFutureForOtherY = intersectionY - otherHail.initialPoint.y == 0L ||
//                                (intersectionY - otherHail.initialPoint.y) / (intersectionY - otherHail.initialPoint.y).absoluteValue == otherHail.velocity.y / otherHail.velocity.y.absoluteValue
//                            if (inFutureForX && inFutureForY && inFutureForOtherX && inFutureForOtherY) {
//                                Coordinate(intersectionX, intersectionY)
//                            } else {
//                                null
//                            }
//                        }