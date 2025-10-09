package com.github.alexthe666.alexsmobs.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class StraddleJumpEnchantment extends StraddleEnchantment {
   protected StraddleJumpEnchantment(Rarity p_i46729_1_, EnchantmentCategory p_i46729_2_, EquipmentSlot... p_i46729_3_) {
      super(p_i46729_1_, p_i46729_2_, p_i46729_3_);
   }

   @Override
   public int m_6183_(int p_77321_1_) {
      return 4 + (p_77321_1_ - 1) * 5;
   }

   @Override
   public int m_6175_(int p_223551_1_) {
      return super.m_6183_(p_223551_1_) + 10;
   }

   @Override
   public int m_6586_() {
      return 3;
   }
}
