package io.github

import io.github.client.NotebookRenderer
import io.github.entity.notebookEntityType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

class DeepSilenceClient : ClientModInitializer {
    override fun onInitializeClient() {
        EntityRendererRegistry.register(notebookEntityType) { context -> NotebookRenderer(context) }
    }
}
