package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlastingRecipe extends AbstractCookingRecipe {
   public BlastingRecipe(String $$0, CookingBookCategory $$1, Ingredient $$2, ItemStack $$3, float $$4, int $$5) {
      super($$0, $$1, $$2, $$3, $$4, $$5);
   }

   @Override
   protected Item furnaceIcon() {
      return Items.BLAST_FURNACE;
   }

   @Override
   public RecipeSerializer<BlastingRecipe> getSerializer() {
      return RecipeSerializer.BLASTING_RECIPE;
   }

   @Override
   public RecipeType<BlastingRecipe> getType() {
      return RecipeType.BLASTING;
   }

   @Override
   public RecipeBookCategory recipeBookCategory() {
      return switch(this.category()) {
         case BLOCKS -> RecipeBookCategories.BLAST_FURNACE_BLOCKS;
         case FOOD, MISC -> RecipeBookCategories.BLAST_FURNACE_MISC;
         default -> throw new MatchException(null, null);
      };
   }
}
