package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.CustomTabBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AMCreativeTabRegistry {
   public static final DeferredRegister<CreativeModeTab> DEF_REG = DeferredRegister.create(Registries.f_279569_, "alexsmobs");
   public static final RegistryObject<CreativeModeTab> TAB = DEF_REG.register(
      "alexsmobs",
      () -> CreativeModeTab.builder()
         .m_257941_(Component.m_237115_("itemGroup.alexsmobs"))
         .withTabsBefore(new ResourceKey[]{CreativeModeTabs.f_256731_})
         .m_257737_(() -> new ItemStack((ItemLike)AMItemRegistry.TAB_ICON.get()))
         .m_257501_((enabledFeatures, output) -> {
            for (RegistryObject<Item> item : AMItemRegistry.DEF_REG.getEntries()) {
               if (item.get() instanceof CustomTabBehavior customTabBehavior) {
                  customTabBehavior.fillItemCategory(output);
               } else {
                  output.m_246326_((ItemLike)item.get());
               }
            }
         })
         .m_257652_()
   );
}
