package dibs.bossfight.item;

import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// https://github.com/neoforged/NeoForge/blob/1.21.x/src/main/java/net/neoforged/neoforge/registries/DeferredRegister.java

public class ModItems {

    public static final DeferredRegister<net.minecraft.world.item.Item> ITEMS = DeferredRegister.createItems(DePaulDibsBossFight.MODID);


    
    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}