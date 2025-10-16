package Entity.client;

import Entity.ModEntities;
import dibs.bossfight.DePaulDibsBossFight;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
    modid = DePaulDibsBossFight.MOD_ID,
    value = Dist.CLIENT   // default bus is MOD, so this is fine
)
public final class ClientEventHandler {
    private ClientEventHandler() {}

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(DibsModel.MY_LAYER, DibsModel::createBodyLayer);
        e.registerLayerDefinition(GeckoModel.MY_LAYER, GeckoModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModEntities.DIBS.get(), (EntityRendererProvider.Context ctx) -> new DibsRenderer(ctx));
        e.registerEntityRenderer(ModEntities.GECKO.get(), (EntityRendererProvider.Context ctx) -> new GeckoRenderer(ctx));
    }
}
