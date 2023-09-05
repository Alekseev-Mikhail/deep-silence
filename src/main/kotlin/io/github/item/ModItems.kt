package io.github.item

import io.github.MOD_ID
import io.github.ModRegister
import io.github.item.custom.RoomTool
import io.github.registerItemGroup
import io.github.util.RoomSystemStorage
import net.minecraft.item.Item
import net.minecraft.registry.Registries.ITEM
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

class ModItems(private var storage: RoomSystemStorage) : ModRegister() {
    override fun register() {
        val roomTool = registerItem("room_tool", RoomTool(storage))

        val path = "devitems"
        registerItemGroup(path, "itemgroup.$MOD_ID.items.dev", roomTool)
        logger.info("Registered item group. Path: $path")
    }

    private fun registerItem(name: String, item: Item): Item {
        Registry.register(ITEM, Identifier(MOD_ID, name), item)
        logger.info("Registered item. Name: ${item.name.string}")
        return item
    }
}
