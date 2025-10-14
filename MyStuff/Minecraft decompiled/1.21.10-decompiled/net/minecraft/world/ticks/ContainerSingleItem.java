package net.minecraft.world.ticks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ContainerSingleItem extends Container {
   ItemStack getTheItem();

   default ItemStack splitTheItem(int $$0) {
      return this.getTheItem().split($$0);
   }

   void setTheItem(ItemStack var1);

   default ItemStack removeTheItem() {
      return this.splitTheItem(this.getMaxStackSize());
   }

   @Override
   default int getContainerSize() {
      return 1;
   }

   @Override
   default boolean isEmpty() {
      return this.getTheItem().isEmpty();
   }

   @Override
   default void clearContent() {
      this.removeTheItem();
   }

   @Override
   default ItemStack removeItemNoUpdate(int $$0) {
      return this.removeItem($$0, this.getMaxStackSize());
   }

   @Override
   default ItemStack getItem(int $$0) {
      return $$0 == 0 ? this.getTheItem() : ItemStack.EMPTY;
   }

   @Override
   default ItemStack removeItem(int $$0, int $$1) {
      return $$0 != 0 ? ItemStack.EMPTY : this.splitTheItem($$1);
   }

   @Override
   default void setItem(int $$0, ItemStack $$1) {
      if ($$0 == 0) {
         this.setTheItem($$1);
      }
   }

   public interface BlockContainerSingleItem extends ContainerSingleItem {
      BlockEntity getContainerBlockEntity();

      @Override
      default boolean stillValid(Player $$0) {
         return Container.stillValidBlockEntity(this.getContainerBlockEntity(), $$0);
      }
   }
}
