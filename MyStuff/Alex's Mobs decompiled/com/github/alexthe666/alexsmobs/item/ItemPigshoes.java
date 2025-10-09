package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class ItemPigshoes extends Item {
   public ItemPigshoes(Properties props) {
      super(props);
   }

   public int m_6473_() {
      return 1;
   }

   public boolean m_8120_(ItemStack stack) {
      return true;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment.f_44672_ == EnchantmentCategory.ARMOR_FEET
         && !enchantment.m_6589_()
         && enchantment != Enchantments.f_44986_
         && enchantment != Enchantments.f_44962_;
   }
}
