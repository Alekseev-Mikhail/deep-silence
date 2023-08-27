package io.github.client

import io.github.MOD_ID
import io.github.entity.custom.NotebookEntity
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.GeoModel

class NotebookModel : GeoModel<NotebookEntity>() {
    override fun getModelResource(animatable: NotebookEntity): Identifier {
        return Identifier(MOD_ID, "geo/notebook.geo.json")
    }

    override fun getTextureResource(animatable: NotebookEntity): Identifier {
        return Identifier(MOD_ID, "textures/entity/notebook.png")
    }

    override fun getAnimationResource(animatable: NotebookEntity): Identifier {
        return Identifier(MOD_ID, "animations/notebook.animation.json")
    }
}
