package io.github.client

import io.github.MOD_ID
import io.github.entity.custom.NotebookEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory.Context
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import software.bernie.geckolib.renderer.GeoEntityRenderer

class NotebookRenderer(renderManager: Context) :
    GeoEntityRenderer<NotebookEntity>(renderManager, NotebookModel()) {
    override fun getTextureLocation(animatable: NotebookEntity): Identifier {
        return Identifier(MOD_ID, "textures/entity/notebook.png")
    }

    override fun render(entity: NotebookEntity?, entityYaw: Float, partialTick: Float, poseStack: MatrixStack, bufferSource: VertexConsumerProvider, packedLight: Int) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }
}
