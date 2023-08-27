package io.github.item

import io.github.MOD_ID
import io.github.ModRegister
import io.github.registerItemGroup
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.registry.Registries.ITEM
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

class ModItems : ModRegister() {
    override fun register() {
        val roomTool = registerItem("room_tool", Item(FabricItemSettings()))

        val path = "devitems"
        registerItemGroup(path, "itemgroup.mod.items.dev", roomTool)
        logger.info("Registered item group. Path: $path")
    }

    private fun registerItem(name: String, item: Item): Item {
        Registry.register(ITEM, Identifier(MOD_ID, name), item)
        logger.info("Registered item. Name: ${item.name.string}")
        return item
    }
}
