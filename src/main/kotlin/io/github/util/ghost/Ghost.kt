package io.github.util.ghost

import io.github.tickListeners
import io.github.util.Location
import io.github.util.ghost.GhostTaskResult.EXPIRED
import io.github.util.room.Room
import io.github.util.room.RoomSystem
import net.minecraft.world.World
import kotlin.random.Random

abstract class Ghost(private val world: World, private val roomSystem: RoomSystem, private var currentRoom: Room) {
    private var currentLocation = Location(currentRoom.firstPoint.x.toDouble(), currentRoom.firstPoint.y.toDouble(), currentRoom.firstPoint.z.toDouble())
    private var task: Pair<GhostTaskType, GhostTask>
    private val actionId: Int

    init {
        task = GhostTaskType.generate()
        tickListeners.add { action() }
        actionId = tickListeners.size - 1
    }

    private fun action() {
        if (task.second.step(this@Ghost) == EXPIRED) {
            task = GhostTaskType.generate()
        }
    }

    val location
        get() = currentLocation

    fun kill() {
        tickListeners.removeAt(actionId)
    }

    fun getTask(): Pair<GhostTaskType, GhostTask> = task

    fun addX(x: Double) {
        currentLocation = Location(currentLocation.x + x, currentLocation.y, currentLocation.z)
    }

    fun addY(y: Double) {
        currentLocation = Location(currentLocation.x, currentLocation.y + y, currentLocation.z)
    }

    fun addZ(z: Double) {
        currentLocation = Location(currentLocation.x, currentLocation.y, currentLocation.z + z)
    }

    private fun getRandomLocation(room: Room): Location {
        val x = Random.nextDouble(room.firstPoint.x.toDouble(), room.secondPoint.x.toDouble())
        val y = if (room.firstPoint.y < room.secondPoint.y) room.firstPoint.y.toDouble() else room.secondPoint.y.toDouble()
        val z = Random.nextDouble(room.firstPoint.z.toDouble(), room.secondPoint.z.toDouble())
        return Location(x, y, z)
    }
}
