package Entity.client;

import Entity.custom.DibsEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DibsRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<DibsEntity, R>
{
    public DibsRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DibsModel());
        this.shadowRadius = 0.4f;
    }
}
