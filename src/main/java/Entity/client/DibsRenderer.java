package Entity.client;

import com.mojang.blaze3d.vertex.PoseStack;

import Entity.custom.DibsEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class DibsRenderer extends LivingEntityRenderer<DibsEntity, DibsRenderState, DibsModel>
{
    public DibsRenderer(EntityRendererProvider.Context ctx) 
    {
        super(ctx, new DibsModel(ctx.bakeLayer(GeckoModel.MY_LAYER)), 0.4f);
        //this.addLayer(new GeckoRenderLayer(this, ctx.getModelSet()));
    }

    @Override
    public DibsRenderState createRenderState() 
    {
        return new DibsRenderState();
    }

    @Override
    public void render(DibsRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) 
    {
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    @Override
    public void extractRenderState(DibsEntity entity, DibsRenderState state, float partialTick) 
    {
        super.extractRenderState(entity, state, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(DibsRenderState state) {
        return ResourceLocation.fromNamespaceAndPath("depauldibsbossfight", "textures/entity/gecko_green.png");
    }
}
