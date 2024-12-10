fun main() {

    fun part1(input: List<String>): Int {
        val map = BoundedCoordinateMap(
            input
                .map { it.split("") }
        )

        var currentStep = Pair(
            map.map.entries
                .first { it.value == "^" }
                .key, Direction.NORTH)
        val path = mutableSetOf<Coordinate>()

        while (true) {
            path.add(currentStep.first)
            var nextStep = map
                .stepInDirection(currentStep.first, currentStep.second)
            var currentDirection = currentStep.second
            while (nextStep.second == "#") {
                currentDirection = currentDirection.turnRight90()
                nextStep = map
                    .stepInDirection(currentStep.first, currentDirection)
            }
            if (nextStep.second == null) {
                return path
                    .count()
            }
            currentStep = Pair(nextStep.first, currentDirection)
        }
    }

    fun findNextStepForRock(
        map: BoundedCoordinateMap<String>,
        currentStep: Pair<Coordinate, Direction>,
        previousPath: List<Pair<Coordinate, Direction>>
    ): Pair<Pair<Coordinate, String?>, Direction> {
        var nextStep = map
            .stepInDirection(currentStep.first, currentStep.second)
        var currentDirection = currentStep.second
        while (nextStep.second == "#" && !previousPath.any { it.first == nextStep.first }) {
            currentDirection = currentDirection.turnRight90()
            nextStep = map
                .stepInDirection(currentStep.first, currentDirection)
        }
        return Pair(nextStep, currentDirection)
    }

    fun findNextStep(
        map: BoundedCoordinateMap<String>,
        currentStep: Pair<Coordinate, Direction>,
        extraRock: Coordinate?
    ): Pair<Pair<Coordinate, String?>, Direction> {
        var nextStep = map
            .stepInDirection(currentStep.first, currentStep.second)
        var currentDirection = currentStep.second
        while (nextStep.second == "#" || nextStep.first == extraRock) {
            currentDirection = currentDirection.turnRight90()
            nextStep = map
                .stepInDirection(currentStep.first, currentDirection)
        }
        return Pair(nextStep, currentDirection)
    }

    fun directionToArrow(direction: Direction): String {
        return when (direction) {
            Direction.NORTH -> "^"
            Direction.EAST -> ">"
            Direction.SOUTH -> "v"
            Direction.WEST -> "<"
            else -> "x"
        }
    }

    fun lookForLoop(map: BoundedCoordinateMap<String>, previousPath: List<Pair<Coordinate, Direction>>, initialStep: Pair<Coordinate, Direction>): Boolean {
        var loopPath = previousPath.toMutableSet()
        var currentStep = initialStep
        val extraRockStep = findNextStep(map, currentStep, null)
        if (extraRockStep.first.second == null || previousPath.any { it.first == extraRockStep.first.first }) {
            return false
        }
        var extraRock = extraRockStep.first.first

        while (true) {
            var (nextStep, currentDirection) = findNextStep(map, currentStep, extraRock)
            if (nextStep.second == null) {
                return false
            }
            currentStep = Pair(nextStep.first, currentDirection)
            if (loopPath.contains(currentStep)) {
//                println("")
//                println("LOOP:")
//                BoundedCoordinateMap(map.map
//                    .mapValues {
//                        if (loopPath.any { step -> step.first == it.key }) directionToArrow(loopPath.last { step -> step.first == it.key }.second)
//                        else if (extraRock == it.key) "x"
//                        else it.value
//                    }
//                ).printMapWithDefaults(".")
//                println("")
                return true
            }
            loopPath.add(currentStep)
        }
    }

    fun part2(input: List<String>): Int {
        val map = BoundedCoordinateMap(
            input
                .map { it.split("") }
        )

        var currentStep = Pair(
            map.map.entries
                .first { it.value == "^" }
                .key, Direction.NORTH)
        val path = mutableListOf<Pair<Coordinate, Direction>>()
        var loops = 0
        while (true) {
            path.add(currentStep)
            if (lookForLoop(map, path, currentStep)) loops++
            var (nextStep, currentDirection) = findNextStep(map, currentStep, null)
            if (nextStep.second == null) {
                break
            }
            currentStep = Pair(nextStep.first, currentDirection)
//            BoundedCoordinateMap(map.map
//                .mapValues {
//                    if (path.any { step -> step.first == it.key }) directionToArrow(path.last { step -> step.first == it.key }.second)
//                    else it.value
//                }
//            ).printMapWithDefaults(".")
//            readln()
        }
        return loops
    }

//    val testInput = readInput("06", "test")
//    check(part1(testInput) == 288)
//
    val testInput2 = readInput("06", "test_2024")
    check(part2(testInput2) == 6)

    part1(readInput("06", "input_2024")).println()
    part2(readInput("06", "input_2024")).println()
}
