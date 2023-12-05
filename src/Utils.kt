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
