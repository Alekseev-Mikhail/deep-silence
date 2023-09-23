package io.github.ghost

import io.github.ghost.GhostTaskResult.EXPIRED
import io.github.ghost.task.WaitTask
import io.github.room.Room
import io.github.tickListeners
import io.github.util.Location
import kotlin.math.abs
import kotlin.math.min

abstract class Ghost(val currentRoom: Room) {
    val location = getInitLocation(currentRoom)

    private var task: GhostTask = WaitTask(1, 2)
    private val actionId: Int

    init {
        tickListeners.add { think() }
        actionId = tickListeners.size - 1
    }

    protected abstract fun generateTask(): GhostTask

    private fun think() {
        if (task.step(this) == EXPIRED) {
            task = generateTask()
        }
    }

    private fun getInitLocation(room: Room): Location {
        val firstPoint = room.firstPoint
        val secondPoint = room.secondPoint
        val x = min(firstPoint.x, secondPoint.x) + abs(firstPoint.x - secondPoint.x) / 2
        val y = min(firstPoint.y, secondPoint.y)
        val z = min(firstPoint.z, secondPoint.z) + abs(firstPoint.z - secondPoint.z) / 2
        return Location(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun kill() = tickListeners.removeAt(actionId)

    fun task() = task
}
