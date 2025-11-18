package dibs.bossfight.clients;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class ClientTicks {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        BossMusicController.clientTick();
    }
}
