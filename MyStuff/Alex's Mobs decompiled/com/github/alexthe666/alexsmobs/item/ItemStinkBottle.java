package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class ItemStinkBottle extends AMBlockItem {
   public ItemStinkBottle(RegistryObject<Block> blockSupplier, Properties props) {
      super(blockSupplier, props);
   }

   public InteractionResult m_40576_(BlockPlaceContext context) {
      InteractionResult result = super.m_40576_(context);
      if (result.m_19077_()) {
         ItemStack bottle = new ItemStack(Items.f_42590_);
         if (context.m_43723_() == null) {
            context.m_43725_()
               .m_7967_(
                  new ItemEntity(
                     context.m_43725_(),
                     context.m_8083_().m_123341_() + 0.5F,
                     context.m_8083_().m_123342_() + 0.5F,
                     context.m_8083_().m_123343_() + 0.5F,
                     bottle
                  )
               );
         } else if (!context.m_43723_().m_36356_(bottle)) {
            context.m_43723_().m_36176_(bottle, false);
         }
      }

      return result;
   }

   public String m_5524_() {
      return this.m_41467_();
   }
}
