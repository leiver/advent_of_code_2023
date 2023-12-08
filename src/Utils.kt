import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
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
fun LongRange.overlap(other: LongRange): Boolean {
    return this.first < other.last && this.last > other.first
}

fun LongRange.intersect(other: LongRange): LongRange {
    return LongRange(max(this.first, other.first), min(this.last, other.last))
}

fun LongRange.except(other: LongRange): List<LongRange> {
    return listOf(
        LongRange(this.first, min(this.last, other.first-1)),
        LongRange(max(this.first, other.last + 1), this.last)
    )
        .filter { !it.isEmpty() }
}

fun LongRange.except(other: Iterable<LongRange>): List<LongRange> {
    return other
        .fold(listOf(this)) { acc, next ->
            acc
                .flatMap { it.except(next) }
        }
}

fun LongRange.length(): Long {
    return last - first
}

fun IntRange.overlap(other: IntRange): Boolean {
    return this.first < other.last && this.last > other.first
}

fun IntRange.intersect(other: IntRange): IntRange {
    return IntRange(max(this.first, other.first), min(this.last, other.last))
}

fun IntRange.except(other: IntRange): List<IntRange> {
    return listOf(
        IntRange(this.first, min(this.last, other.first)),
        IntRange(max(this.first, other.last), this.last)
    )
        .filter { !it.isEmpty() }
}

fun IntRange.except(other: List<IntRange>): List<IntRange> {
    return other
        .fold(listOf(this)) { acc, next ->
            acc
                .flatMap { it.except(next) }
        }
}
fun <T> rotate2DArray(lists: List<List<T>>): List<List<T>> {
    return IntRange(0, lists.minOf { it.size } - 1)
        .map {index ->
            lists.map { list -> list[index] }
        }
}

fun <T> List<T>.permutations(): List<Pair<T, T>> {
    return IntRange(2, size)
        .flatMap { windowed(it, 1) }
        .map { it.first() to it.last() }
}

fun lcm(numbers: List<Long>): Long {
    val map = numbers
        .associateWith { it }
        .toMutableMap()

    while (map.values.toSet().size > 1) {
        val lowestEntry = map
            .minBy { it.value }
        val highestValue = map
            .maxOf { it.value }
        val addition = (highestValue - lowestEntry.value) / lowestEntry.key
        val remainder = if ((highestValue - lowestEntry.value) % lowestEntry.key > 0) 1 else 0
        map.put(lowestEntry.key, lowestEntry.value + lowestEntry.key * (addition + remainder))
    }

    return map.values.first()
}
