package io.github.ghost.type.main

import io.github.ghost.Ghost
import io.github.ghost.GhostTask
import io.github.ghost.task.WaitTask
import io.github.ghost.task.WalkTask
import io.github.random
import io.github.room.Room

abstract class Spirit(initRoom: Room) : Ghost(initRoom) {
    private val wait
        get() = WaitTask(3, 5)

    private val walk
        get() = WalkTask(5, 7)

    override fun generateTask(): GhostTask {
        return random(wait, walk)
    }
}
