package io.github.ghost.task

import io.github.ghost.Ghost
import io.github.ghost.GhostTask
import io.github.ghost.GhostTaskResult
import io.github.util.Location
import io.github.util.distanceIn2
import io.github.util.stepIn2
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class WalkTask(min: Int, max: Int, ghost: Ghost) : GhostTask(min, max) {
    override val nameId: String = "Walk"
    private var target = getRandomLocationInRoom(ghost)

    override fun action(ghost: Ghost): GhostTaskResult {
        if (distanceIn2(ghost.location, target) <= ghost.speed) target = getRandomLocationInRoom(ghost)
        ghost.location.add(stepIn2(ghost.location, target, ghost.speed))
        return super.action(ghost)
    }

    private fun getRandomLocationInRoom(ghost: Ghost): Location {
        val firstX = ghost.currentRoom.firstPoint.x.toDouble()
        val secondX = ghost.currentRoom.secondPoint.x.toDouble()
        val firstZ = ghost.currentRoom.firstPoint.z.toDouble()
        val secondZ = ghost.currentRoom.secondPoint.z.toDouble()
        return Location(
            Random.nextDouble(min(firstX, secondX), max(firstX, secondX)),
            ghost.location.y,
            Random.nextDouble(min(firstZ, secondZ), max(firstZ, secondZ)),
        )
    }
}
