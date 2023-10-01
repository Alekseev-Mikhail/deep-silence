package io.github.ghost.task

import io.github.ghost.GhostTask

class WaitTask(min: Int, max: Int) : GhostTask(min, max) {
    override val nameId: String = "Wait"
}
