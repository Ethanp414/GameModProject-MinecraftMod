package Entity.client;

import com.mojang.blaze3d.vertex.PoseStack;

import Entity.custom.GeckoEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class GeckoRenderer extends LivingEntityRenderer<GeckoEntity, LivingEntityRenderState, GeckoModel>
{   
    public GeckoRenderer(EntityRendererProvider.Context ctx) 
    {
        super(ctx, new GeckoModel(ctx.bakeLayer(GeckoModel.MY_LAYER)), 0.4f);
        //this.addLayer(new GeckoRenderLayer(this, ctx.getModelSet()));
    }

    @Override
    public LivingEntityRenderState createRenderState() 
    {
        return new LivingEntityRenderState();
    }

    @Override
    public void render(LivingEntityRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) 
    {
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    @Override
    public void extractRenderState(GeckoEntity entity, LivingEntityRenderState state, float partialTick) 
    {
        super.extractRenderState(entity, state, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntityRenderState state) {
        return ResourceLocation.fromNamespaceAndPath("depauldibsbossfight", "textures/entity/gecko_green.png");
    }
}
