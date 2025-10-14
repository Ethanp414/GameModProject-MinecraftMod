package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SmokingRecipe extends AbstractCookingRecipe {
   public SmokingRecipe(String $$0, CookingBookCategory $$1, Ingredient $$2, ItemStack $$3, float $$4, int $$5) {
      super($$0, $$1, $$2, $$3, $$4, $$5);
   }

   @Override
   protected Item furnaceIcon() {
      return Items.SMOKER;
   }

   @Override
   public RecipeType<SmokingRecipe> getType() {
      return RecipeType.SMOKING;
   }

   @Override
   public RecipeSerializer<SmokingRecipe> getSerializer() {
      return RecipeSerializer.SMOKING_RECIPE;
   }

   @Override
   public RecipeBookCategory recipeBookCategory() {
      return RecipeBookCategories.SMOKER_FOOD;
   }
}
