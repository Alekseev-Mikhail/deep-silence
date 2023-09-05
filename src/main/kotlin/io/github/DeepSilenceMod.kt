package io.github

import io.github.block.ModBlocks
import io.github.command.ModCommands
import io.github.entity.ModEntities
import io.github.item.ModItems
import io.github.util.RoomSystemStorage
import net.fabricmc.api.ModInitializer
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import java.io.File

const val MOD_ID = "ds"

val PATH: String = getPath()

object DeepSilenceMod : ModInitializer {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val storage = RoomSystemStorage()
    private val modItems = ModItems(storage)
    private val modBlocks = ModBlocks()
    private val modEntities = ModEntities()
    private val modCommands = ModCommands(storage)

    override fun onInitialize() {
        logger.info("Mod is being prepared. Id: $MOD_ID")
        logger.info("Path: $PATH")

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
}

fun Text.add(string: String): Text = Text.of(this.string + string)

fun checkDirectory(path: String) {
    if (!File(path).exists()) {
        File(path).mkdir()
    }
}

private fun getPath(): String {
    var path = File(DeepSilenceMod::class.java.protectionDomain.codeSource.location.path).parentFile.absolutePath
    path = if (path.contains("classes")) "$path/../../../$MOD_ID" else "$path/../$MOD_ID"
    checkDirectory(path)
    return path
}
