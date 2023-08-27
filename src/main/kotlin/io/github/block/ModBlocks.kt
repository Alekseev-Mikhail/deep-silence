package io.github.block

import io.github.MOD_ID
import io.github.ModRegister
import io.github.block.custom.Chair
import io.github.registerItemGroup
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries.BLOCK
import net.minecraft.registry.Registries.ITEM
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

class ModBlocks : ModRegister() {
    override fun register() {
        val chair = registerBlock("chair", Chair())

        val path = "blocks"
        registerItemGroup(path, "itemgroup.mod.blocks", chair)
        logger.info("Registered item group. Path: $path")
    }

    private fun registerBlock(name: String, block: Block): Block {
        registerBlockItem(name, block)
        Registry.register(BLOCK, Identifier(MOD_ID, name), block)
        logger.info("Registered block. Name: ${block.name.string}")
        return block
    }

    private fun registerBlockItem(name: String, block: Block) {
        val blockItem = Registry.register(ITEM, Identifier(MOD_ID, name), BlockItem(block, FabricItemSettings()))
        logger.info("Registered block item. Name: ${blockItem.name.string}")
    }
}
