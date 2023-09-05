package io.github.item.custom

import io.github.MOD_ID
import io.github.add
import io.github.util.Point
import io.github.util.RoomSystemStorage
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text.translatable
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult.FAIL
import net.minecraft.util.ActionResult.SUCCESS

class RoomTool(private var storage: RoomSystemStorage) : Item(FabricItemSettings()) {
    private var lastIsFirst = false
    private val roomSystem
        get() = storage.roomSystem

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (context.world.isClient) return super.useOnBlock(context)
        val player = context.player ?: return FAIL
        val blockPos = context.blockPos
        val point = Point(blockPos.x, blockPos.y, blockPos.z)

        return if (!lastIsFirst) {
            lastIsFirst = true
            roomSystem.setFirstPoint(player.id, point)
            player.sendMessage(translatable("item.$MOD_ID.room_tool.action.first").add(" ($point)"))
            SUCCESS
        } else {
            lastIsFirst = false
            roomSystem.setSecondPoint(player.id, point)
            player.sendMessage(translatable("item.$MOD_ID.room_tool.action.second").add(" ($point)"))
            SUCCESS
        }
    }
}
