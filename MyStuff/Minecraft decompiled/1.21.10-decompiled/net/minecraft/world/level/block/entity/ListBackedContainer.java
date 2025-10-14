package net.minecraft.world.level.block.entity;

import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public interface ListBackedContainer extends Container {
   NonNullList<ItemStack> getItems();

   default int count() {
      return (int)this.getItems().stream().filter(Predicate.not(ItemStack::isEmpty)).count();
   }

   @Override
   default int getContainerSize() {
      return this.getItems().size();
   }

   @Override
   default void clearContent() {
      this.getItems().clear();
   }

   @Override
   default boolean isEmpty() {
      return this.getItems().stream().allMatch(ItemStack::isEmpty);
   }

   @Override
   default ItemStack getItem(int $$0) {
      return this.getItems().get($$0);
   }

   @Override
   default ItemStack removeItem(int $$0, int $$1) {
      ItemStack $$2 = ContainerHelper.removeItem(this.getItems(), $$0, $$1);
      if (!$$2.isEmpty()) {
         this.setChanged();
      }

      return $$2;
   }

   @Override
   default ItemStack removeItemNoUpdate(int $$0) {
      return ContainerHelper.removeItem(this.getItems(), $$0, this.getMaxStackSize());
   }

   @Override
   default boolean canPlaceItem(int $$0, ItemStack $$1) {
      return this.acceptsItemType($$1) && (this.getItem($$0).isEmpty() || this.getItem($$0).getCount() < this.getMaxStackSize($$1));
   }

   default boolean acceptsItemType(ItemStack $$0) {
      return true;
   }

   @Override
   default void setItem(int $$0, ItemStack $$1) {
      this.setItemNoUpdate($$0, $$1);
      this.setChanged();
   }

   default void setItemNoUpdate(int $$0, ItemStack $$1) {
      this.getItems().set($$0, $$1);
      $$1.limitSize(this.getMaxStackSize($$1));
   }
}
