package Entity.client;

import Entity.custom.DibsEntity;
import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DibsModel extends GeoModel<DibsEntity>
{

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(DePaulDibsBossFight.MODID, "geckolib/models/entity/dibsmodel_textured.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return ResourceLocation.fromNamespaceAndPath(DePaulDibsBossFight.MODID, "textures/entity/dibstexture_whole.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DibsEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DePaulDibsBossFight.MODID, "geckolib/animations/entity/dibsmodel.animation.json");
    }
}
