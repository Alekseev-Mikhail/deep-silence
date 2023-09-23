package io.github.ghost

import io.github.ghost.GhostTaskResult.EXPIRED
import io.github.ghost.GhostTaskResult.PASS
import io.github.tick

abstract class GhostTask(min: Int, max: Int) {
    val length: Long = (min.tick..max.tick).random()
    private var passed: Long = 0

    open fun step(ghost: Ghost): GhostTaskResult {
        passed++
        return if (length <= passed) EXPIRED else PASS
    }
}
