package Entity.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class DibsRenderLayer extends RenderLayer<DibsRenderState, DibsModel>
{
    private final DibsModel model;

    // Create the render layer. The renderer parameter is required for passing to super.
    // Other parameters can be added as needed. For example, we need the EntityModelSet for model baking.
    public DibsRenderLayer(DibsRenderer renderer, EntityModelSet models) 
    {
        super(renderer); // now matches RenderLayerParent<GeckoRenderState, GeckoModel>
        this.model = new DibsModel(models.bakeLayer(DibsModel.MY_LAYER));
    }
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, DibsRenderState state, float yRot, float xRot) 
    {
        // draw your layer here using `model`
    }

}
