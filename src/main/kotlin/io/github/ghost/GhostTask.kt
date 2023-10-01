package io.github.ghost

import io.github.ghost.GhostTaskResult.EXPIRED
import io.github.ghost.GhostTaskResult.PASS
import io.github.util.tick
import kotlin.random.Random

abstract class GhostTask(min: Int, max: Int) {
    abstract val nameId: String
    val length: Double = Random.nextDouble(min.tick, max.tick)
    private var passed: Long = 0

    open fun action(ghost: Ghost): GhostTaskResult {
        passed++
        return if (length <= passed) EXPIRED else PASS
    }
}
