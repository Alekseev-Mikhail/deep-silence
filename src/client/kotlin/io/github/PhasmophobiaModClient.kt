package io.github

import io.github.client.NotebookRenderer
import io.github.entity.entityNotebook
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object PhasmophobiaModClient : ClientModInitializer {
    override fun onInitializeClient() {
        EntityRendererRegistry.register(entityNotebook) { context -> NotebookRenderer(context) }
    }
}
