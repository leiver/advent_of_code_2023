import java.math.BigInteger
import java.security.MessageDigest
import kotlin.collections.Map.Entry
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: String, name: String) = Path("inputs/$day/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


/**
 * RangeFunctions.
 */
infix fun LongRange.overlap(other: LongRange): Boolean =
    !this.isEmpty() && !other.isEmpty() && this.first < other.last && this.last > other.first

infix fun LongRange.intersect(other: LongRange): LongRange =
    LongRange(
        max(this.first, other.first),
        min(this.last, other.last)
    )

infix fun LongRange.except(other: LongRange): List<LongRange> =
    listOf(
        LongRange(this.first, min(this.last, other.first - 1)),
        LongRange(max(this.first, other.last + 1), this.last)
    )
        .filter { !it.isEmpty() }

infix fun LongRange.except(other: Iterable<LongRange>): List<LongRange> =
    other
        .fold(listOf(this)) { acc, next ->
            acc
                .flatMap { it except next }
        }

infix fun LongRange.continuous(other: LongRange): Boolean =
    first == other.last + 1 || last + 1 == other.first

infix fun LongRange.union(other: LongRange): LongRange =
    LongRange(
        min(first, other.first),
        max(last, other.last)
    )

fun LongRange.length(): Long = last - first

fun LongRange.flipped(): LongRange = LongRange(last, first)

infix fun IntRange.overlap(other: IntRange): Boolean =
    this.first < other.last && this.last > other.first

infix fun IntRange.intersect(other: IntRange): IntRange =
    IntRange(
        max(this.first, other.first),
        min(this.last, other.last)
    )


infix fun IntRange.except(other: IntRange): List<IntRange> =
    listOf(
        IntRange(this.first, min(this.last, other.first)),
        IntRange(max(this.first, other.last), this.last)
    )
        .filter { !it.isEmpty() }

infix fun IntRange.except(other: List<IntRange>): List<IntRange> =
    other
        .fold(listOf(this)) { acc, next ->
            acc
                .flatMap { it except next }
        }

infix fun IntRange.union(other: IntRange): IntRange =
    IntRange(
        min(first, other.first),
        max(last, other.last)
    )

infix fun IntRange.extendTo(other: Int): IntRange =
    IntRange(
        min(first, other),
        max(last, other)
    )

fun IntRange.length(): Int = last - first + 1

fun <T> List<List<T>>.rotate2DArray(): List<List<T>> =
    IntRange(0, minOf { it.size } - 1)
        .map { index ->
            map { list -> list[index] }
        }


fun <T> Set<T>.permutations(): List<Pair<T, T>> =
    toList()
        .permutations()

fun <T> List<T>.permutations(): List<Pair<T, T>> =
    IntRange(2, size)
        .flatMap { windowed(it, 1) }
        .map { it.first() to it.last() }

fun <T> List<List<T>>.permutationsOfLists(): List<List<T>> {
    return drop(1).fold(first().toMutableList().map { mutableListOf(it) }) { acc, next ->
        val result = next
            .flatMap { newElement ->
                acc
                    .map { prevElements ->
                        val copiedList = prevElements.toMutableList()
                        copiedList.add(newElement)
                        copiedList
                    }
            }
            .toMutableList()
        result
    }
}

fun gcd(a: Long, b: Long): Long {
    var biggest = max(a, b)
    var smallest = min(a, b)
    do {
        var mod = biggest % smallest
        biggest = max(smallest, mod)
        smallest = min(smallest, mod)
    } while (smallest > 0)
    return biggest
}

fun gcd(numbers: List<Long>): Long {
    val sorted = numbers
        .sorted()
    return sorted
        .drop(1)
        .fold(sorted.first()) { prev, next ->
            gcd(prev, next)
        }
}

fun lcm(numbers: List<Long>): Long {
    return numbers.reduce{acc, next -> acc * next}.absoluteValue / gcd(numbers)
}

data class Coordinate(val x: Long, val y: Long) {

    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())


    infix fun neighbour(direction: Direction): Coordinate = this plus direction.delta

    fun neighbours(): List<Coordinate> =
        Direction
            .all()
            .map { this plus it.delta }


    fun neighbours(xBound: LongRange, yBound: LongRange) =
        neighbours()
            .filter { xBound.contains(it.x) && yBound.contains(it.y) }

    fun orthogonalNeighbours(): List<Coordinate> =
        Direction
            .orthogonal()
            .map { this plus it.delta }

    fun orthogonalNeighbours(xBound: LongRange, yBound: LongRange) =
        orthogonalNeighbours()
            .filter { xBound.contains(it.x) && yBound.contains(it.y) }

    fun diagonalNeighbours(): List<Coordinate> =
        Direction
            .diagonal()
            .map { this plus it.delta }

    fun diagonalNeighbours(xBound: LongRange, yBound: LongRange) =
        diagonalNeighbours()
            .filter { xBound.contains(it.x) && yBound.contains(it.y) }

    infix fun directionTo(other: Coordinate): Direction =
        Direction.direction(
            other
                .minus(this)
                .normalise()
        )


    infix fun plus(other: Coordinate): Coordinate =
        Coordinate(
            x + other.x,
            y + other.y
        )

    infix fun minus(other: Coordinate): Coordinate =
        Coordinate(
            x - other.x,
            y - other.y
        )

    infix fun multiply(other: Coordinate): Coordinate =
        Coordinate(
            x * other.x,
            y * other.y
        )

    infix fun multiply(factor: Long): Coordinate =
        Coordinate(
            x * factor,
            y * factor
        )

    infix fun manhattenDistance(other: Coordinate): Long =
        this.minus(other)
            .abs()
            .sum()

    fun abs(): Coordinate =
        Coordinate(
            abs(x),
            abs(y)
        )

    fun normalise(): Coordinate =
        Coordinate(
            if (x != 0L) x / abs(x) else 0L,
            if (y != 0L) y / abs(y) else 0L
        )

    fun sum(): Long = x + y

    fun invert(): Coordinate =
        Coordinate(
            x * -1,
            y * -1
        )

    fun flip(): Coordinate =
        Coordinate(
            y,
            x
        )

    infix fun rangesBetween(other: Coordinate): Pair<LongRange, LongRange> {
        return LongRange(min(x, other.x), max(x, other.x)) to LongRange(min(y, other.y), max(y, other.y))
    }

    infix fun allBetween(other: Coordinate): List<Coordinate> {
        return LongRange(min(x, other.x), max(x, other.x))
            .flatMap { x ->
                LongRange(min(y, other.y) ,max(y, other.y))
                    .map { y -> Coordinate(x, y) }
            }
    }
}

enum class Direction(val representations: List<String>, val delta: Coordinate) {
    NORTH(listOf("N"), Coordinate(0L, -1L)),
    SOUTH(listOf("S"), Coordinate(0L, 1L)),
    WEST(listOf("W"), Coordinate(-1L, 0L)),
    EAST(listOf("E"), Coordinate(1L, 0L)),
    NORTH_WEST(listOf("NW", "WN"), NORTH.delta plus WEST.delta),
    NORTH_EAST(listOf("NE", "EN"), NORTH.delta plus EAST.delta),
    SOUTH_WEST(listOf("SW", "WS"), SOUTH.delta plus WEST.delta),
    SOUTH_EAST(listOf("SE", "ES"), SOUTH.delta plus EAST.delta);

    companion object {
        fun direction(representation: String): Direction =
            Direction
                .entries
                .firstOrNull { it.representations.contains(representation) }
                ?: Direction
                    .entries
                    .first { it.name == representation.uppercase().replace(" ", "_") }

        fun direction(delta: Coordinate): Direction =
            Direction
                .entries
                .first { it.delta == delta }

        fun orthogonal(): List<Direction> =
            listOf(
                NORTH,
                SOUTH,
                WEST,
                EAST
            )

        fun diagonal(): List<Direction> =
            listOf(
                NORTH_WEST,
                NORTH_EAST,
                SOUTH_WEST,
                SOUTH_EAST
            )

        fun all(): List<Direction> = Direction.entries
    }

    fun opposite(): Direction =
        when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            WEST -> EAST
            EAST -> WEST
            NORTH_WEST -> SOUTH_EAST
            NORTH_EAST -> SOUTH_WEST
            SOUTH_EAST -> NORTH_WEST
            SOUTH_WEST -> NORTH_EAST
        }
}

fun String.toDirection() = Direction.direction(this)

data class BoundedCoordinateMap<T>(val map: Map<Coordinate, T>, val xBounds: LongRange, val yBounds: LongRange) {
    constructor(map: Map<Coordinate, T>) : this(
        map,
        LongRange(
            map.keys.minOf { it.x },
            map.keys.maxOf { it.x }
        ),
        LongRange(
            map.keys.minOf { it.y },
            map.keys.maxOf { it.y }
        )
    )

    constructor(nestedList: List<List<T>>) : this(
        nestedList
            .flatMapIndexed { y, list ->
                list.mapIndexed { x, item ->
                    Coordinate(x, y) to item
                }
            }
            .associate { it },
        LongRange(0, nestedList[0].size - 1L),
        LongRange(0, nestedList.size - 1L)
    )

    fun orthogonalNeighbours(coordinate: Coordinate): List<Pair<Direction, Pair<Coordinate, T?>>> {
        return Direction
            .orthogonal()
            .map { it to coordinate.plus(it.delta) }
            .filter { it.second.x in xBounds && it.second.y in yBounds }
            .map { it.first to (it.second to map[it.second]) }
    }

    fun nextInDirection(coordinate: Coordinate, direction: Direction): Pair<Coordinate, T?> =
        map
            .entries
            .asSequence()
            .filter {
                it.key multiply direction.delta.flip().normalise() == coordinate multiply direction.delta.flip()
                    .normalise()
            }
            .filter {
                if (direction.delta.sum() < 0) {
                    it.key.sum() < coordinate.sum()
                } else {
                    it.key.sum() > coordinate.sum()
                }
            }
            .sortedWith(
                if (direction.delta.sum() < 0) {
                    compareByDescending { comp -> comp.key.sum() }
                } else {
                    compareBy { comp -> comp.key.sum() }
                }
            ).map { it.key to it.value }
            .firstOrNull() ?: (
                if (direction.delta.x != 0L) {
                    if (direction.delta.sum() < 0L) {
                        Coordinate(
                            xBounds.first,
                            coordinate.y
                        ) to null
                    } else {
                        Coordinate(
                            xBounds.last,
                            coordinate.y
                        ) to null
                    }
                } else {
                    if (direction.delta.sum() < 0L) {
                        Coordinate(
                            coordinate.x,
                            yBounds.first
                        ) to null
                    } else {
                        Coordinate(
                            coordinate.x,
                            yBounds.last
                        ) to null
                    }
                }
                )

    fun rows(): List<Pair<Long, List<Pair<Coordinate, T>>>> =
        map
            .entries
            .map { it.key to it.value }
            .groupBy { it.first.y }
            .map { it.key to it.value.sortedBy { (coordinate, _) -> coordinate.x } }
            .sortedBy { it.first }

    fun columns(): List<Pair<Long, List<Pair<Coordinate, T>>>> =
        map
            .entries
            .map { it.key to it.value }
            .groupBy { it.first.x }
            .map { it.key to it.value.sortedBy { (coordinate, _) -> coordinate.y } }
            .sortedBy { it.first }

    fun rotateClockwise90(): BoundedCoordinateMap<T> =
        BoundedCoordinateMap(
            map
                .map { (key, value) ->
                    Coordinate(
                        yBounds.last - key.y,
                        key.x
                    ) to value
                }
                .associate { it },
            yBounds,
            xBounds
        )

    fun printMapWithDefaults(defaultValue: T) =
        yBounds
            .forEach { y ->
                xBounds.joinToString(" ") { x ->
                    map.getOrDefault(Coordinate(x, y), defaultValue).toString()
                }
                    .println()
            }

    infix fun inbounds(coordinate: Coordinate): Boolean =
        xBounds.contains(coordinate.x)
                && yBounds.contains(coordinate.y)

    fun allBetween(start: Coordinate, end: Coordinate): List<Pair<Coordinate, T>> {
        return start
            .allBetween(end)
            .map { coordinate -> coordinate to map[coordinate] }
            .filter { (coordinate, value) -> value != null }
            .map { (coordinate, value) -> coordinate to value!! }
    }
}

fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}