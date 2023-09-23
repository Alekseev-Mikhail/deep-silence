package io.github.ghost

import io.github.ghost.type.child.Kikimora
import io.github.room.Room

enum class GhostType {
    KIKIMORA {
        override fun get(initRoom: Room): Ghost = Kikimora(initRoom)
    },
    ;

    abstract fun get(initRoom: Room): Ghost

    companion object {
        fun generate(initRoom: Room) = GhostType.entries.random().get(initRoom)
    }
}
