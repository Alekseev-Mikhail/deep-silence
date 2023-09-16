package io.github.util.ghost

import io.github.util.ghost.type.Soul
import io.github.util.room.Room
import io.github.util.room.RoomSystem
import net.minecraft.world.World

enum class GhostType {
    SOUL {
        override fun get(world: World, roomSystem: RoomSystem, initRoom: Room): Ghost = Soul(world, roomSystem, initRoom)
    },
    ;

    abstract fun get(world: World, roomSystem: RoomSystem, initRoom: Room): Ghost

    companion object {
        fun generate(world: World, roomSystem: RoomSystem, initRoom: Room) = GhostType.entries.random().get(world, roomSystem, initRoom)
    }
}
