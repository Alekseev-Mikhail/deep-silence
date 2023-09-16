package io.github.entity

import io.github.MOD_ID
import io.github.ModRegister
import io.github.entity.custom.NotebookEntity
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup.MISC
import net.minecraft.registry.Registries.ENTITY_TYPE
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

var notebookEntityType: EntityType<NotebookEntity>? = null
    set(value) {
        if (field == null) field = value
    }

class ModEntities : ModRegister() {
    override fun register() {
        notebookEntityType = registerEntity(
            "notebook",
            FabricEntityTypeBuilder.create(MISC) { type, world -> NotebookEntity(type, world) }
                .dimensions(EntityDimensions.fixed(0.8f, 0.15f))
                .build(),
        )
    }

    private fun <T : Entity?> registerEntity(name: String, type: EntityType<T>): EntityType<T> {
        Registry.register(
            ENTITY_TYPE,
            Identifier(MOD_ID, name),
            type,
        )
        logger.info("Registered item. Name: $name")
        return type
    }
}
