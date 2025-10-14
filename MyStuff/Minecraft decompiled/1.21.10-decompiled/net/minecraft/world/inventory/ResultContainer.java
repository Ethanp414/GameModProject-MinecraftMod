package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ResultContainer implements Container, RecipeCraftingHolder {
   private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
   @Nullable
   private RecipeHolder<?> recipeUsed;

   @Override
   public int getContainerSize() {
      return 1;
   }

   @Override
   public boolean isEmpty() {
      for(ItemStack $$0 : this.itemStacks) {
         if (!$$0.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public ItemStack getItem(int $$0) {
      return this.itemStacks.get(0);
   }

   @Override
   public ItemStack removeItem(int $$0, int $$1) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   @Override
   public ItemStack removeItemNoUpdate(int $$0) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   @Override
   public void setItem(int $$0, ItemStack $$1) {
      this.itemStacks.set(0, $$1);
   }

   @Override
   public void setChanged() {
   }

   @Override
   public boolean stillValid(Player $$0) {
      return true;
   }

   @Override
   public void clearContent() {
      this.itemStacks.clear();
   }

   @Override
   public void setRecipeUsed(@Nullable RecipeHolder<?> $$0) {
      this.recipeUsed = $$0;
   }

   @Nullable
   @Override
   public RecipeHolder<?> getRecipeUsed() {
      return this.recipeUsed;
   }
}
