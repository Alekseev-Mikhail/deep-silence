package io.github

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text.translatable
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class ModRegister {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    abstract fun register()
}

fun registerItemGroup(path: String, translateKey: String, vararg itemConvertibles: ItemConvertible) {
    Registry.register(
        Registries.ITEM_GROUP,
        Identifier(MOD_ID, path),
        FabricItemGroup
            .builder()
            .displayName(translatable(translateKey))
            .icon { ItemStack(itemConvertibles.first()) }.entries { _, entries ->
                itemConvertibles.forEach { item ->
                    entries.add(item)
                }
            }.build(),
    )
}
