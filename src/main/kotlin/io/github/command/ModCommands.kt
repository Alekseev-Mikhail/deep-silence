package io.github.command

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import io.github.MOD_ID
import io.github.ModRegister
import io.github.add
import io.github.util.RoomSystem
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable

private val argumentName = translatable("command.ds.argument.name").string
private val argumentPointFirst = translatable("command.ds.argument.point.first").string
private val argumentPointSecond = translatable("command.ds.argument.point.second").string

class ModCommands(private val roomSystem: RoomSystem) : ModRegister() {
    override fun register() = CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        dispatcher.register(
            literal<ServerCommandSource>(MOD_ID)
//                .requires { source -> source.hasPermissionLevel(4) }
                .then(
                    literal<ServerCommandSource>("room")
                        .then(
                            literal<ServerCommandSource>("create")
                                .then(
                                    CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                        val player = prepare(context) ?: return@executes 0
                                        roomCreate(context, player)
                                    },
                                ),
                        )
                        .then(
                            literal<ServerCommandSource>("delete")
                                .then(
                                    CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                        val player = prepare(context) ?: return@executes 0
                                        roomDelete(context, player)
                                    },
                                )
                                .then(
                                    literal<ServerCommandSource?>("all").executes { context ->
                                        val player = prepare(context) ?: return@executes 0
                                        roomDeleteAll(player)
                                    },
                                ),
                        )
                        .then(
                            literal<ServerCommandSource>("show")
                                .then(
                                    CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                        val player = prepare(context) ?: return@executes 0
                                        roomShowWithName(context, player)
                                    },
                                )
                                .executes { context ->
                                    val player = prepare(context) ?: return@executes 0
                                    roomShow(player)
                                },
                        ),
                ),
        )
    }

    private fun roomCreate(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val pair = roomSystem.addRoom(player.id, argument)

        if (pair == null) {
            player.sendMessage(translatable("command.$MOD_ID.room.create.fail").add(" ($argumentName: $argument)"))
            return 0
        }
        player.sendMessage(
            translatable("command.$MOD_ID.room.create.success")
                .add(" ($argumentName: $argument $argumentPointFirst: ${pair.first} $argumentPointSecond: ${pair.second})"),
        )
        return SINGLE_SUCCESS
    }

    private fun roomDelete(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)

        if (!roomSystem.deleteRoom(argument)) {
            player.sendMessage(translatable("command.$MOD_ID.room.delete.fail").add(" ($argumentName: $argument)"))
            return 0
        }
        player.sendMessage(translatable("command.$MOD_ID.room.delete.success").add(" ($argumentName: $argument)"))
        return SINGLE_SUCCESS
    }

    private fun roomDeleteAll(player: ServerPlayerEntity): Int {
        roomSystem.deleteAllRoom()
        player.sendMessage(translatable("command.$MOD_ID.room.delete_all.success"))
        return SINGLE_SUCCESS
    }

    private fun roomShow(player: ServerPlayerEntity): Int {
        val message = roomSystem.getAllName().joinToString(prefix = ": ")
        if (message == ": ") {
            player.sendMessage(translatable("command.$MOD_ID.room.show.fail"))
            return 0
        }
        player.sendMessage(translatable("command.$MOD_ID.room.show.success").add(message))
        return SINGLE_SUCCESS
    }

    private fun roomShowWithName(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val room = roomSystem[argument]
        if (room == null) {
            player.sendMessage(translatable("command.$MOD_ID.room.show.with_name.fail"))
            return 0
        }
        player.sendMessage(
            translatable("command.$MOD_ID.room.show.with_name.success")
                .add(": $argumentPointFirst: ${room.firstPoint} $argumentPointSecond: ${room.secondPoint}"),
        )
        return SINGLE_SUCCESS
    }

    private fun prepare(context: CommandContext<ServerCommandSource>): ServerPlayerEntity? {
        val player = context.source.player
        if (player == null) {
            context.source.sendMessage(translatable("command.$MOD_ID.error.player_null"))
        }
        player?.sendMessage(Text.empty())
        return player
    }
}
