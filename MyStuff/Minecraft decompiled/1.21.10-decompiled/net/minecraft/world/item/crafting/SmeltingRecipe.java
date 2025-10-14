package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SmeltingRecipe extends AbstractCookingRecipe {
   public SmeltingRecipe(String $$0, CookingBookCategory $$1, Ingredient $$2, ItemStack $$3, float $$4, int $$5) {
      super($$0, $$1, $$2, $$3, $$4, $$5);
   }

   @Override
   protected Item furnaceIcon() {
      return Items.FURNACE;
   }

   @Override
   public RecipeSerializer<SmeltingRecipe> getSerializer() {
      return RecipeSerializer.SMELTING_RECIPE;
   }

   @Override
   public RecipeType<SmeltingRecipe> getType() {
      return RecipeType.SMELTING;
   }

   @Override
   public RecipeBookCategory recipeBookCategory() {
      return switch(this.category()) {
         case BLOCKS -> RecipeBookCategories.FURNACE_BLOCKS;
         case FOOD -> RecipeBookCategories.FURNACE_FOOD;
         case MISC -> RecipeBookCategories.FURNACE_MISC;
         default -> throw new MatchException(null, null);
      };
   }
}
