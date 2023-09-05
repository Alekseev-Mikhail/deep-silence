package io.github.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import io.github.MOD_ID
import io.github.ModRegister
import io.github.add
import io.github.util.RoomSystemResult.ALREADY
import io.github.util.RoomSystemResult.FAIL
import io.github.util.RoomSystemResult.ONE_WAY_LINK
import io.github.util.RoomSystemResult.POINT_DOES_NOT_EXIST
import io.github.util.RoomSystemResult.ROOM_DOES_NOT_EXIST
import io.github.util.RoomSystemResult.SUCCESS
import io.github.util.RoomSystemStorage
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable

private const val COMMAND_SUCCESS = 1
private const val COMMAND_FAIL = 2
private const val COMMAND_WARNING = 3
private const val COMMAND_UNKNOWN = 4

private val argumentName = translatable("command.ds.argument.name").string
private val argumentPointFirst = translatable("command.ds.argument.point.first").string
private val argumentPointSecond = translatable("command.ds.argument.point.second").string

class ModCommands(private val storage: RoomSystemStorage) : ModRegister() {
    private val roomSystem
        get() = storage.roomSystem

    override fun register() = CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        dispatcher.register(
            literal<ServerCommandSource>(MOD_ID)
//                .requires { source -> source.hasPermissionLevel(4) }
                .then(
                    literal<ServerCommandSource>("save")
                        .then(
                            CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                val player = prepare(context)
                                    ?: return@executes COMMAND_FAIL
                                save(context, player)
                            },
                        ),
                )
                .then(
                    literal<ServerCommandSource>("read")
                        .then(
                            CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                val player = prepare(context)
                                    ?: return@executes COMMAND_FAIL
                                read(context, player)
                            },
                        ),
                )
                .then(
                    literal<ServerCommandSource>("room")
                        .then(
                            literal<ServerCommandSource>("create")
                                .then(
                                    CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                        val player = prepare(context)
                                            ?: return@executes COMMAND_FAIL
                                        roomCreate(context, player)
                                    },
                                ),
                        )
                        .then(
                            literal<ServerCommandSource>("delete")
                                .then(
                                    CommandManager.argument("name", StringArgumentType.word()).executes { context ->
                                        val player = prepare(context)
                                            ?: return@executes COMMAND_FAIL
                                        roomDelete(context, player)
                                    },
                                )
                                .then(
                                    literal<ServerCommandSource?>("all").executes { context ->
                                        val player = prepare(context)
                                            ?: return@executes COMMAND_FAIL
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
                                                        ?: return@executes COMMAND_FAIL
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
                                                        ?: return@executes COMMAND_FAIL
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
                                            CommandManager.argument("name", StringArgumentType.word())
                                                .executes { context ->
                                                    val player = prepare(context)
                                                        ?: return@executes COMMAND_FAIL
                                                    roomShowRoomsWithName(context, player)
                                                },
                                        )
                                        .executes { context ->
                                            val player = prepare(context)
                                                ?: return@executes COMMAND_FAIL
                                            roomShowRooms(player)
                                        },
                                )
                                .then(
                                    literal<ServerCommandSource>("links")
                                        .then(
                                            CommandManager.argument("name", StringArgumentType.word())
                                                .executes { context ->
                                                    val player = prepare(context)
                                                        ?: return@executes COMMAND_FAIL
                                                    roomShowLinksWithName(context, player)
                                                },
                                        )
                                        .executes { context ->
                                            val player = prepare(context)
                                                ?: return@executes COMMAND_FAIL
                                            roomShowLinks(player)
                                        },
                                ),
                        ),
                ),
        )
    }

    private fun save(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val additionalMessage = " ($argumentName: $argument)"
        return when (storage.save(argument)) {
            SUCCESS -> sendReply(player, "save.success", additionalMessage, COMMAND_SUCCESS)
            FAIL -> sendReply(player, "save.fail", additionalMessage, COMMAND_FAIL)
            else -> sendReply(player)
        }
    }

    private fun read(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val additionalMessage = " ($argumentName: $argument)"
        return when (storage.read(argument)) {
            SUCCESS -> sendReply(player, "read.success", additionalMessage, COMMAND_SUCCESS)
            FAIL -> sendReply(player, "read.fail", additionalMessage, COMMAND_FAIL)
            else -> sendReply(player)
        }
    }

    private fun roomCreate(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val pair = roomSystem.createRoom(player.id, argument)

        if (pair == null) {
            player.sendMessage(translatable("command.$MOD_ID.room.create.fail").add(" ($argumentName: $argument)"))
            return COMMAND_FAIL
        }
        player.sendMessage(
            translatable("command.$MOD_ID.room.create.success")
                .add(" ($argumentName: $argument $argumentPointFirst: ${pair.first} $argumentPointSecond: ${pair.second})"),
        )
        return COMMAND_SUCCESS
    }

    private fun roomDelete(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)

        return when (roomSystem.deleteRoom(argument)) {
            ROOM_DOES_NOT_EXIST -> sendReply(player, "room.delete.fail.exist", COMMAND_FAIL)
            ONE_WAY_LINK -> sendReply(player, "room.delete.warning", COMMAND_WARNING)
            SUCCESS -> sendReply(player, "room.delete.success", COMMAND_SUCCESS)
            else -> sendReply(player)
        }
    }

    private fun roomDeleteAll(player: ServerPlayerEntity): Int {
        roomSystem.deleteAllRooms()
        player.sendMessage(translatable("command.$MOD_ID.room.delete_all.success"))
        return COMMAND_SUCCESS
    }

    private fun roomLink(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val firstArgument = context.getArgument("first name", String.toString().javaClass)
        val secondArgument = context.getArgument("second name", String.toString().javaClass)
        val result = roomSystem.link(firstArgument, secondArgument, player.id)
        val additionalMessage = " ($argumentName 1: $firstArgument $argumentName 2: $secondArgument)"

        return when (result) {
            SUCCESS -> sendReply(player, "room.link.success", additionalMessage, COMMAND_SUCCESS)
            ROOM_DOES_NOT_EXIST -> sendReply(player, "room.link.fail.exist.room", additionalMessage, COMMAND_FAIL)
            POINT_DOES_NOT_EXIST -> sendReply(player, "room.link.fail.exist.point", additionalMessage, COMMAND_FAIL)
            ALREADY -> sendReply(player, "room.link.fail.already", additionalMessage, COMMAND_FAIL)
            else -> sendReply(player)
        }
    }

    private fun roomUnlink(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val firstArgument = context.getArgument("first name", String.toString().javaClass)
        val secondArgument = context.getArgument("second name", String.toString().javaClass)
        val result = roomSystem.unlink(firstArgument, secondArgument)
        val additionalMessage = " ($argumentName 1: $firstArgument $argumentName 2: $secondArgument)"

        return when (result) {
            SUCCESS -> sendReply(player, "room.unlink.success", additionalMessage, COMMAND_SUCCESS)
            ROOM_DOES_NOT_EXIST -> sendReply(player, "room.unlink.fail.exist", additionalMessage, COMMAND_FAIL)
            ALREADY -> sendReply(player, "room.unlink.fail.already", additionalMessage, COMMAND_FAIL)
            else -> sendReply(player)
        }
    }

    private fun roomShowRooms(player: ServerPlayerEntity): Int {
        val message = roomSystem.getAllRoomNames().joinToString(prefix = ": ")
        if (message == ": ") {
            player.sendMessage(translatable("command.$MOD_ID.room.show.rooms.nothing_found"))
        } else {
            player.sendMessage(translatable("command.$MOD_ID.room.show.rooms.found").add(message))
        }
        return COMMAND_SUCCESS
    }

    private fun roomShowRoomsWithName(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val room = roomSystem[argument]
        if (room == null) {
            player.sendMessage(translatable("command.$MOD_ID.room.show.rooms.with_name.fail.exist"))
            return COMMAND_FAIL
        }
        player.sendMessage(
            translatable("command.$MOD_ID.room.show.rooms.with_name.found")
                .add(" ($argumentPointFirst: ${room.firstPoint} $argumentPointSecond: ${room.secondPoint})"),
        )
        return COMMAND_SUCCESS
    }

    private fun roomShowLinks(player: ServerPlayerEntity): Int {
        val message = roomSystem.getAllLinkNames().joinToString(prefix = ": ")

        if (message == ": ") {
            player.sendMessage(translatable("command.$MOD_ID.room.show.links.nothing_found"))
        } else {
            player.sendMessage(translatable("command.$MOD_ID.room.show.links.found").add(message))
        }
        return COMMAND_SUCCESS
    }

    private fun roomShowLinksWithName(context: CommandContext<ServerCommandSource>, player: ServerPlayerEntity): Int {
        val argument = context.getArgument("name", String.toString().javaClass)
        val names = roomSystem.getAllLinkNamesByRoomName(argument)

        if (names == null) {
            player.sendMessage(translatable("command.$MOD_ID.room.show.links.with_name.fail.exist"))
            return COMMAND_FAIL
        }
        if (names.isEmpty()) {
            player.sendMessage(translatable("command.$MOD_ID.room.show.links.with_name.nothing_found"))
            return COMMAND_SUCCESS
        }

        player.sendMessage(translatable("command.$MOD_ID.room.show.links.with_name.found").add(names.joinToString(prefix = ": ")))
        return COMMAND_SUCCESS
    }

    private fun prepare(context: CommandContext<ServerCommandSource>): ServerPlayerEntity? {
        val player = context.source.player
        if (player == null) {
            context.source.sendMessage(translatable("command.$MOD_ID.error.player_null"))
        }

        player?.sendMessage(Text.empty())
        return player
    }

    private fun sendReply(player: ServerPlayerEntity, path: String, message: String, type: Int): Int {
        player.sendMessage(translatable("command.$MOD_ID.$path").add(message))
        return type
    }

    private fun sendReply(player: ServerPlayerEntity, path: String, type: Int): Int {
        player.sendMessage(translatable("command.$MOD_ID.$path"))
        return type
    }

    private fun sendReply(player: ServerPlayerEntity): Int {
        player.sendMessage(translatable("command.$MOD_ID.error.unknown"))
        return COMMAND_UNKNOWN
    }
}
