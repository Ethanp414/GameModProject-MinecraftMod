package dibs.bossfight.item;

import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// https://github.com/neoforged/NeoForge/blob/1.21.x/src/main/java/net/neoforged/neoforge/registries/DeferredRegister.java

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DePaulDibsBossFight.MODID);

    public static final DeferredItem<Item> BISMUTH = ITEMS.registerItem(
            "bismuth",
            Item::new,
            new Item.Properties()
    );


  /*   
    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth",
            () -> new Item(new Item.Properties()));
 */

    /* 
    // Reuse the ITEMS DeferredRegister declared in the main mod class so we don't create
    // duplicate listeners and so we can use the convenience methods that set the id
    // at the right time.
    public static final DeferredItem<Item> BISMUTH = DePaulDibsBossFight.ITEMS.registerSimpleItem("bismuth", new Item.Properties());
    */

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}