package dibs.bossfight.item;

import java.util.function.Supplier;

import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.ShovelItem;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, DePaulDibsBossFight.MODID);
            
            
    public static final Supplier<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TAB.register("MEEP", () -> CreativeModeTab.builder()
    //Set the title of the tab. Don't forget to add a translation!
    .title(Component.translatable("itemGroup." + DePaulDibsBossFight.MODID + ".example"))
    //Set the icon of the tab.
                //.icon(() -> new ItemStack(MyItemsClass.EXAMPLE_ITEM.get()))
    //Add your items to the tab.
    .icon(() -> new ItemStack(ModItems.BISMUTH.get()))
    .displayItems((params, output) -> {
        //output.accept(MyItemsClass.MY_ITEM.get());
        //output.accept(ModItems.BISMUTH.get());
        output.accept(ModItems.TOMAHAWK.get());
        output.accept(ModItems.BASKETBALL.get());
        // Accepts an ItemLike. This assumes that MY_BLOCK has a corresponding item.
        //output.accept(MyBlocksClass.MY_BLOCK.get());
    })
    .build()
);



    /* 
    public static final DeferredRegister<ModCreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DePaulDibsBossFight.MODID); 
    */
}