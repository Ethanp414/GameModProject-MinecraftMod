package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemModArrow extends ArrowItem {
   public ItemModArrow(Properties group) {
      super(group);
   }

   public AbstractArrow m_6394_(Level worldIn, ItemStack stack, LivingEntity shooter) {
      if (this == AMItemRegistry.SHARK_TOOTH_ARROW.get()) {
         Arrow arrowentity = new EntitySharkToothArrow(worldIn, shooter);
         arrowentity.m_36878_(stack);
         return arrowentity;
      } else {
         return super.m_6394_(worldIn, stack, shooter);
      }
   }
}
