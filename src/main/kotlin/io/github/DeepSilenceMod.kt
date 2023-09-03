package io.github

import io.github.block.ModBlocks
import io.github.command.ModCommands
import io.github.entity.ModEntities
import io.github.item.ModItems
import io.github.util.RoomSystem
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.text.Text
import org.slf4j.LoggerFactory

const val MOD_ID = "ds"

object DeepSilenceMod : ModInitializer, DedicatedServerModInitializer {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val roomSystem = RoomSystem()
    private val modItems = ModItems(roomSystem)
    private val modBlocks = ModBlocks()
    private val modEntities = ModEntities()
    private val modCommands = ModCommands(roomSystem)

    override fun onInitialize() {
        logger.info("Mod is being prepared. Id: $MOD_ID")

        logger.warn("Items are registering")
        modItems.register()
        logger.info("Items are registered")

        logger.warn("Blocks are registering")
        modBlocks.register()
        logger.info("Blocks are registered")

        logger.warn("Entities are registering")
        modEntities.register()
        logger.info("Entities are registered")

        logger.warn("Commands are registering")
        modCommands.register()
        logger.info("Commands are registered")

        logger.info("Mod is ready! Id: $MOD_ID")
    }

    override fun onInitializeServer() {

    }
}

fun Text.add(string: String): Text = Text.of(this.string + string)
