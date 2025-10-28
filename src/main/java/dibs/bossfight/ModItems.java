package dibs.bossfight;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(DePaulDibsBossFight.MOD_ID);


    public static final DeferredItem<Item> CHICAGO_DOG = ITEMS.registerSimpleItem(
            "chicago_dog",
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(7)
                    .saturationModifier(0.8f)
                    .build()));

    public static final DeferredItem<Item> BASKETBALL = ITEMS.registerItem(
            "basketball",
            properties -> new dibs.bossfight.item.custom.BasketballItem(properties.stacksTo(16)),
            new Item.Properties()
    );

     public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}