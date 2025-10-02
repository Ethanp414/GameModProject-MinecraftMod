package dibs.bossfight.item;

import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, DePaulDibsBossFight.MODID);


    /* 
    public static final DeferredRegister<ModCreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DePaulDibsBossFight.MODID); 
    */
}