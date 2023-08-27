package io.github

import io.github.block.ModBlocks
import io.github.entity.ModEntities
import io.github.item.ModItems
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

const val MOD_ID = "phasmophobia"

object PhasmophobiaMod : ModInitializer {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val modItems = ModItems()
    private val modBlocks = ModBlocks()
    private val modEntities = ModEntities()

    override fun onInitialize() {
        modItems.register()
        modBlocks.register()
        modEntities.register()

        logger.info("Mod is ready! Id: $MOD_ID")
    }
}
