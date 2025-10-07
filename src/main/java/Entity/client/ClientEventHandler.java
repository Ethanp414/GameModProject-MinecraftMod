package Entity.client;

import Entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class ClientEventHandler {
    private ClientEventHandler() {}

    public static void init(IEventBus modBus) {
        // These are *client* events on the MOD bus
        modBus.addListener(ClientEventHandler::registerLayerDefinitions);
        modBus.addListener(ClientEventHandler::registerRenderers);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions e) 
    {
        e.registerLayerDefinition(DibsModel.MY_LAYER, DibsModel::createBodyLayer);
        
        e.registerLayerDefinition(GeckoModel.MY_LAYER, GeckoModel::createBodyLayer);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) 
    {
        e.registerEntityRenderer(ModEntities.DIBS.get(), (EntityRendererProvider.Context ctx) -> new DibsRenderer(ctx));

        e.registerEntityRenderer(ModEntities.GECKO.get(), (EntityRendererProvider.Context ctx) -> new GeckoRenderer(ctx));
    }
}
