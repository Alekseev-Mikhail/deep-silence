package io.github

import com.mojang.brigadier.context.CommandContext
import io.github.block.ModBlocks
import io.github.command.ModCommands
import io.github.entity.ModEntities
import io.github.entity.custom.NotebookEntity
import io.github.entity.notebookEntityType
import io.github.ghost.Ghost
import io.github.ghost.GhostType
import io.github.item.ModItems
import io.github.room.RoomSystem
import io.github.room.withoutPoints
import io.github.util.DeepSilenceResult
import io.github.util.DeepSilenceResult.FAIL
import io.github.util.DeepSilenceResult.SUCCESS
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.server.ServerTickCallback
import net.minecraft.entity.Entity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter
import java.util.Scanner

const val MOD_ID = "ds"

val PATH: String = getPath()

val tickListeners = mutableListOf<Runnable>()

val Int.tick: Long
    get() = (this * 20).toLong()

class DeepSilence : ModInitializer {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val modItems = ModItems(this)
    private val modBlocks = ModBlocks()
    private val modEntities = ModEntities()
    private val modCommands = ModCommands(this)
    private var insideRoomSystem = RoomSystem()
    var ghost: Ghost? = null

    val roomSystem
        get() = insideRoomSystem

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

        ServerTickCallback.EVENT.register { tickListeners.forEach { func -> func.run() } }
        logger.info("Mod is ready! Id: $MOD_ID")
    }

    fun save(name: String) {
        val path = "$PATH/$name"
        checkDirectory(path)
        val file = FileWriter("$path/rs.json")
        val string = Json.encodeToString(withoutPoints(insideRoomSystem))
        file.write(string)
        file.close()
    }

    fun read(name: String): DeepSilenceResult {
        val file = File("$PATH/$name/rs.json")
        if (file.exists()) {
            val obj = Json.decodeFromString<RoomSystem>(Scanner(file).nextLine())
            insideRoomSystem = obj
            return SUCCESS
        }
        return FAIL
    }

    private var entity: Entity? = null
    private var id = -1
    fun start(context: CommandContext<ServerCommandSource>): DeepSilenceResult {
        val entity = NotebookEntity(notebookEntityType!!, context.source.world)
        val room = roomSystem.getRandomRoom() ?: return FAIL
        val ghost = GhostType.generate(room)
        this.entity = entity
        this.ghost = ghost

        context.source.world.spawnEntity(entity)
        tickListeners.add { this.ghost?.location?.let { entity.setPos(it.x, it.y + 1, it.z) } }
        id = tickListeners.size - 1

        return SUCCESS
    }

    fun stop() {
        tickListeners.removeAt(id)
        entity?.kill()
        this.ghost?.kill()
        this.ghost = null
        println(tickListeners.size)
    }
}

private fun getPath(): String {
    var path = File(DeepSilence::class.java.protectionDomain.codeSource.location.path).parentFile.absolutePath
    path = if (path.contains("classes")) "$path/../../../$MOD_ID" else "$path/../$MOD_ID"
    checkDirectory(path)
    return path
}

fun checkDirectory(path: String) {
    if (!File(path).exists()) {
        File(path).mkdir()
    }
}

fun Text.add(string: String): Text = Text.of(this.string + string)

fun <T> random(vararg element: T) = element.random()
