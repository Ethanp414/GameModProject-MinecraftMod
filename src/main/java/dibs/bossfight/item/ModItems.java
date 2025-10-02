package dibs.bossfight.item;

import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
// ...existing code...

// https://github.com/neoforged/NeoForge/blob/1.21.x/src/main/java/net/neoforged/neoforge/registries/DeferredRegister.java

public class ModItems {

    // Reuse the ITEMS DeferredRegister declared in the main mod class so we don't create
    // duplicate listeners and so we can use the convenience methods that set the id
    // at the right time.
    public static final DeferredItem<Item> BISMUTH = DePaulDibsBossFight.ITEMS.registerSimpleItem("bismuth", new Item.Properties());

    // No-op: DePaulDibsBossFight already registers its DeferredRegister to the mod event bus.
    public static void register(IEventBus eventBus) {
        // Intentionally empty
    }
}