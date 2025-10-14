package net.minecraft.world.entity.player;

import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class PlayerEquipment extends EntityEquipment {
   private final Player player;

   public PlayerEquipment(Player $$0) {
      this.player = $$0;
   }

   @Override
   public ItemStack set(EquipmentSlot $$0, ItemStack $$1) {
      return $$0 == EquipmentSlot.MAINHAND ? this.player.getInventory().setSelectedItem($$1) : super.set($$0, $$1);
   }

   @Override
   public ItemStack get(EquipmentSlot $$0) {
      return $$0 == EquipmentSlot.MAINHAND ? this.player.getInventory().getSelectedItem() : super.get($$0);
   }

   @Override
   public boolean isEmpty() {
      return this.player.getInventory().getSelectedItem().isEmpty() && super.isEmpty();
   }
}
