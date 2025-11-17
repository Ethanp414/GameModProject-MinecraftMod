package dibs.bossfight.clients;

import dibs.bossfight.DePaulDibsBossFight;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = DePaulDibsBossFight.MOD_ID, value = Dist.CLIENT)
public final class ClientTicks {

    private ClientTicks() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        try {
            BossMusicController.clientTick();
        } catch (Throwable t) {
            // prevent accidental hard-crash if something goes wrong in music code
        }
    }
}
