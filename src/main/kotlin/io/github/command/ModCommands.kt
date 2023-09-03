package io.github.command

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import io.github.MOD_ID
import io.github.ModRegister
import io.github.add
import io.github.util.RoomSystem
import io.github.util.RoomSystemResult.ALREADY
import io.github.util.RoomSystemResult.POINT_DOES_NOT_EXIST
import io.github.util.RoomSystemResult.ROOM_DOES_NOT_EXIST
import io.github.util.RoomSystemResult.SUCCESS
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable

private const val FAIL = 0

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
                                        val player = prepare(context)
                                            ?: return@executes FAIL
                                        roomCreate(context, player)
                                    },
                                ),
                        )
                        .then(
                            literal<ServerCommandSource>("delete")
                                .then(
                                    CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                        val player = prepare(context)
                                            ?: return@executes FAIL
                                        roomDelete(context, player)
                                    },
                                )
                                .then(
                                    literal<ServerCommandSource?>("all").executes { context ->
                                        val player = prepare(context)
                                            ?: return@executes FAIL
                                        roomDeleteAll(player)
                                    },
                                ),
                        )
                        .then(
                            literal<ServerCommandSource>("link")
                                .then(
                                    CommandManager.argument("first name", StringArgumentType.word())
                                        .then(
                                            CommandManager.argument("second name", StringArgumentType.word())
                                                .executes { context ->
                                                    val player = prepare(context)
                                                        ?: return@executes FAIL
                                                    roomLink(context, player)
                                                },
                                        ),
                                ),
                        )
                        .then(
                            literal<ServerCommandSource>("unlink")
                                .then(
                                    CommandManager.argument("first name", StringArgumentType.word())
                                        .then(
                                            CommandManager.argument("second name", StringArgumentType.word())
                                                .executes { context ->
                                                    val player = prepare(context)
                                                        ?: return@executes FAIL
                                                    roomUnlink(context, player)
                                                },
                                        ),
                                ),
                        )
                        .then(
                            literal<ServerCommandSource>("show")
                                .then(
                                    literal<ServerCommandSource>("rooms")
                                        .then(
                                            CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                                val player = prepare(context)
                                                    ?: return@executes FAIL
                                                roomShowRoomsWithName(context, player)
                                            },
                                        )
                                        .executes { context ->
                                            val player = prepare(context)
                                                ?: return@executes FAIL
                                            roomShowRooms(player)
                                        },
                                )
                                .then(
                                    literal<ServerCommandSource>("links")
                                        .then(
                                            CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                                val player = prepare(context)
                                                    ?: return@executes FAIL
                                                roomShowLinksWithName(context, player)
                                            },
                                        )
                                        .executes { context ->
                                            val player = prepare(context)
                                                ?: return@executes FAIL
                                            roomShowLinks(player)
                                        },
                                ),
                        ),
                ),
        )
    }

    private fun roomCreate(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val pair = roomSystem.createRoom(player.id, argument)

        if (pair == null) {
            player.sendMessage(translatable("command.$MOD_ID.room.create.fail").add(" ($argumentName: $argument)"))
            return FAIL
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
            return FAIL
        }
        player.sendMessage(translatable("command.$MOD_ID.room.delete.success").add(" ($argumentName: $argument)"))
        return SINGLE_SUCCESS
    }

    private fun roomDeleteAll(player: ServerPlayerEntity): Int {
        roomSystem.deleteAllRooms()
        player.sendMessage(translatable("command.$MOD_ID.room.delete_all.success"))
        return SINGLE_SUCCESS
    }

    private fun roomLink(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val firstArgument = context.getArgument("first name", String.toString().javaClass)
        val secondArgument = context.getArgument("second name", String.toString().javaClass)
        val result = roomSystem.link(firstArgument, secondArgument, player.id)
        val additionalMessage = " ($argumentName 1: $firstArgument $argumentName 2: $secondArgument)"

        return when (result) {
            SUCCESS -> sendReply(SINGLE_SUCCESS, player, "room.link.success", additionalMessage)
            ROOM_DOES_NOT_EXIST -> sendReply(FAIL, player, "room.link.fail.exist.room", additionalMessage)
            POINT_DOES_NOT_EXIST -> sendReply(FAIL, player, "room.link.fail.exist.point", additionalMessage)
            ALREADY -> sendReply(FAIL, player, "room.link.fail.already", additionalMessage)
        }
    }

    private fun roomUnlink(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val firstArgument = context.getArgument("first name", String.toString().javaClass)
        val secondArgument = context.getArgument("second name", String.toString().javaClass)
        val result = roomSystem.unlink(firstArgument, secondArgument)
        val additionalMessage = " ($argumentName 1: $firstArgument $argumentName 2: $secondArgument)"

        return when (result) {
            SUCCESS -> sendReply(SINGLE_SUCCESS, player, "room.unlink.success", additionalMessage)
            ROOM_DOES_NOT_EXIST -> sendReply(FAIL, player, "room.unlink.fail.exist", additionalMessage)
            ALREADY -> sendReply(FAIL, player, "room.unlink.fail.already", additionalMessage)
            else -> FAIL
        }
    }

    private fun roomShowRooms(player: ServerPlayerEntity): Int {
        val message = roomSystem.getAllRoomNames().joinToString(prefix = ": ")
        if (message == ": ") {
            player.sendMessage(translatable("command.$MOD_ID.room.show.rooms.nothing_found"))
        } else {
            player.sendMessage(translatable("command.$MOD_ID.room.show.rooms.found").add(message))
        }
        return SINGLE_SUCCESS
    }

    private fun roomShowRoomsWithName(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val room = roomSystem[argument]
        if (room == null) {
            player.sendMessage(translatable("command.$MOD_ID.room.show.rooms.with_name.fail.exist"))
            return FAIL
        }
        player.sendMessage(
            translatable("command.$MOD_ID.room.show.rooms.with_name.found")
                .add(" ($argumentPointFirst: ${room.firstPoint} $argumentPointSecond: ${room.secondPoint})"),
        )
        return SINGLE_SUCCESS
    }

    private fun roomShowLinks(player: ServerPlayerEntity): Int {
        val message = roomSystem.getAllLinkNames().joinToString(prefix = ": ")

        if (message == ": ") {
            player.sendMessage(translatable("command.$MOD_ID.room.show.links.nothing_found"))
        } else {
            player.sendMessage(translatable("command.$MOD_ID.room.show.links.found").add(message))
        }
        return SINGLE_SUCCESS
    }

    private fun roomShowLinksWithName(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val names = roomSystem.getAllLinkNamesByRoomName(argument)

        if (names == null) {
            player.sendMessage(translatable("command.$MOD_ID.room.show.links.with_name.fail.exist"))
            return FAIL
        }
        if (names.isEmpty()) {
            player.sendMessage(translatable("command.$MOD_ID.room.show.links.with_name.nothing_found"))
            return SINGLE_SUCCESS
        }

        player.sendMessage(translatable("command.$MOD_ID.room.show.links.with_name.found").add(names.joinToString(prefix = ": ")))
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

    private fun sendReply(type: Int, player: ServerPlayerEntity, path: String, message: String): Int {
        player.sendMessage(
            translatable("command.$MOD_ID.$path").add(message),
        )
        return type
    }
}
