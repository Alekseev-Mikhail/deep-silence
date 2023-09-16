package io.github.util.ghost.task

import io.github.util.ghost.Ghost
import io.github.util.ghost.GhostTask
import io.github.util.ghost.GhostTaskResult

class WalkTask(min: Int, max: Int) : GhostTask(min, max) {
    override fun step(ghost: Ghost): GhostTaskResult {
        ghost.addX(0.01)
        return super.step(ghost)
    }
}
