package Entity.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class GeckoRenderLayer extends RenderLayer<LivingEntityRenderState, GeckoModel>
{
    private final GeckoModel model;

    // Create the render layer. The renderer parameter is required for passing to super.
    // Other parameters can be added as needed. For example, we need the EntityModelSet for model baking.
    public GeckoRenderLayer(GeckoRenderer renderer, EntityModelSet models) 
    {
        super(renderer); // now matches RenderLayerParent<GeckoRenderState, GeckoModel>
        this.model = new GeckoModel(models.bakeLayer(GeckoModel.MY_LAYER));
    }
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, LivingEntityRenderState state, float yRot, float xRot) 
    {
        // draw your layer here using `model`
    }

}
