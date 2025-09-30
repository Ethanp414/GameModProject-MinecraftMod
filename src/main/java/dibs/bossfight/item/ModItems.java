package dibs.bossfight.item;

import dibs.bossfight.DePaulDibsBossFight;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IeventBus;

public class ModItems {

    public static final DeferredRegister<net.minecraft.world.item.Item> ITEMS = DeferredRegister.createItems(DePaulDibsBossFight.MODID);

    //public static final Deferred

    public static void register(IeventBus eventBus) {
        ITEMS.register(eventBus);
    }
}