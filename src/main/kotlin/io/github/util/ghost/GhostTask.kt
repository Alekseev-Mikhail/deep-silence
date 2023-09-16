package io.github.util.ghost

import io.github.util.ghost.GhostTaskResult.EXPIRED
import io.github.util.ghost.GhostTaskResult.PASS

abstract class GhostTask(min: Int, max: Int) {
    val length: Long = (min.tick..max.tick).random()
    private var passed: Long = 0

    open fun step(ghost: Ghost): GhostTaskResult {
        passed++
        return if (length <= passed) EXPIRED else PASS
    }
}

val Int.tick: Long
    get() = (this * 20).toLong()
