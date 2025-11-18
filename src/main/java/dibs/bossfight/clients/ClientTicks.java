package dibs.bossfight.clients;

import dibs.bossfight.DePaulDibsBossFight;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(
        modid = DePaulDibsBossFight.MOD_ID,
        value = Dist.CLIENT
)
public final class ClientTicks {

    private ClientTicks() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        System.out.println("[CLIENT TICK] Fired POST");
        BossMusicController.clientTick();
    }
}
