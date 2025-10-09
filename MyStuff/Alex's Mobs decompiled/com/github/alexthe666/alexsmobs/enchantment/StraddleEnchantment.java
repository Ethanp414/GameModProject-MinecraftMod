package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class StraddleEnchantment extends Enchantment {
   protected StraddleEnchantment(Rarity r, EnchantmentCategory type, EquipmentSlot... types) {
      super(r, type, types);
   }

   public int m_6183_(int i) {
      return 6 + (i + 1) * 6;
   }

   public int m_6175_(int i) {
      return super.m_6183_(i) + 10;
   }

   public int m_6586_() {
      return 1;
   }

   public boolean m_6594_() {
      return super.m_6594_() && AMConfig.straddleboardEnchants;
   }

   public boolean m_6592_() {
      return super.m_6592_() && AMConfig.straddleboardEnchants;
   }

   public boolean isAllowedOnBooks() {
      return super.isAllowedOnBooks() && AMConfig.straddleboardEnchants;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return super.canApplyAtEnchantingTable(stack) && AMConfig.straddleboardEnchants;
   }
}
