import java.util.PriorityQueue

@OptIn(ExperimentalStdlibApi::class)
fun main() {

    fun pathsToOpenSpace(start: Coordinate, map: BoundedCoordinateMap<String>): List<Pair<Coordinate, Int>> {
        var queue = PriorityQueue<Pair<Coordinate, Int>>({ a, b -> a.second - b.second })
        queue.add(Pair(start, 0))
        var processedTiles = mutableSetOf<Coordinate>()
        var paths = mutableListOf<Pair<Coordinate, Int>>()
        while (queue.isNotEmpty()) {
            val (nextStep, steps) = queue.remove()
            if (processedTiles.contains(nextStep)) {
                continue
            }
            if (nextStep.neighbours().all{ map.inbounds(it) && map.map[it] != "#" }) {
                paths.add(Pair(nextStep, steps))
                processedTiles.add(nextStep)
                continue
            }
            nextStep.orthogonalNeighbours()
                .filter { map.inbounds(it) }
                .filter { map.map[it] != "#" }
                .filter { !processedTiles.contains(it) }
                .forEach { queue.add(Pair(it, steps + 1)) }
            processedTiles.add(nextStep)
        }
        return paths
    }

    fun part1(input: List<String>): Int {
        var map = BoundedCoordinateMap(
            input
                .filterIndexed { index, coord -> index < 1024 }
                .map { it.split(",").map { it.toLong() } }
                .map { (x, y) -> Pair(Coordinate(x, y), "#") }
                .associate { it },
            LongRange(0L, 70L),
            LongRange(0L, 70L)
        )
//        map.printMapWithDefaults(".")
        val endTile = Coordinate(70, 70)
        val startTile = Coordinate(0, 0)
        var queue = PriorityQueue<Triple<Coordinate, Int, Long>>({ a, b -> (a.third - b.third).toInt() })
        queue.add(Triple(startTile, 0, startTile.manhattenDistance(endTile)))
        var processedTiles = mutableSetOf<Coordinate>()
        while (queue.isNotEmpty()) {
            val (nextStep, steps, _) = queue.remove()
            if (processedTiles.contains(nextStep)) {
                continue
            }
            if (nextStep == endTile) {
                return steps
            }
            nextStep.orthogonalNeighbours()
                .filter { map.inbounds(it) }
                .filter { map.map[it] != "#" }
                .filter { !processedTiles.contains(it) }
                .forEach { queue.add(Triple(it, steps + 1, steps + 1 + it.manhattenDistance(endTile))) }
            processedTiles.add(nextStep)
        }
        return 0
    }

    fun part2(input: List<String>): String {
        val bytes = input
            .map { it.split(",").map { it.toLong() } }
            .map { (x, y) -> Coordinate(x, y) }
        val corners = listOf(Coordinate(0, 70), Coordinate(70, 0))
        val chunks = mutableMapOf<Coordinate, Set<Coordinate>>()
        for (byte in bytes) {
            val neighbourBytes = byte
                .neighbours()
                .filter { chunks.contains(it) }
            val newChunk = neighbourBytes
                .map { chunks[it]!! }
                .fold(setOf<Coordinate>(), { a, b -> a + b }) + setOf(byte)
            var chunkBlocksPath = newChunk
                .fold(
                    Pair(false, false)
                ) { (touchesBottomLeft, touchesTopRight), chunkByte ->
                    Pair(
                        touchesBottomLeft || chunkByte.x == corners[0].x || chunkByte.y == corners[0].y,
                        touchesTopRight || chunkByte.x == corners[1].x || chunkByte.y == corners[1].y
                    )
                }

            if (chunkBlocksPath.first && chunkBlocksPath.second) {
                return "${byte.x},${byte.y}"
            }
            newChunk
                .forEach { chunks[it] = newChunk }
        }
        return "fant ikke"
    }

//    val testInput = readInput("18", "test_part1")
//    check(part1(testInput) == 62L)
//
//    val testInput2 = readInput("18", "test_part1")
//    check(part2(testInput2) == 952408144115L)

    part1(readInput("18", "input_2024")).println()
    part2(readInput("18", "input_2024")).println()
}
