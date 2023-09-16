package io.github.util.ghost

import io.github.util.ghost.task.WaitTask
import io.github.util.ghost.task.WalkTask

enum class GhostTaskType {
    WAIT {
        override fun get(): GhostTask = WaitTask(4, 6)
    },
    WALK {
        override fun get(): GhostTask = WalkTask(4, 6)
    },
    ;

    abstract fun get(): GhostTask

    companion object {
        fun generate(): Pair<GhostTaskType, GhostTask> {
            val random = GhostTaskType.entries.random()
            return Pair(random, random.get())
        }
    }
}
