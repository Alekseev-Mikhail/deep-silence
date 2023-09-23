package io.github.ghost.task

import io.github.ghost.Ghost
import io.github.ghost.GhostTask
import io.github.ghost.GhostTaskResult

class WalkTask(min: Int, max: Int) : GhostTask(min, max) {
    override fun step(ghost: Ghost): GhostTaskResult {
        ghost.location.x = ghost.location.x + 0.01
        return super.step(ghost)
    }
}
