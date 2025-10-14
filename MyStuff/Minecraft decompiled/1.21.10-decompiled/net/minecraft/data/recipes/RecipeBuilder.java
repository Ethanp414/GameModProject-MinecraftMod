package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public interface RecipeBuilder {
   ResourceLocation ROOT_RECIPE_ADVANCEMENT = ResourceLocation.withDefaultNamespace("recipes/root");

   RecipeBuilder unlockedBy(String var1, Criterion<?> var2);

   RecipeBuilder group(@Nullable String var1);

   Item getResult();

   void save(RecipeOutput var1, ResourceKey<Recipe<?>> var2);

   default void save(RecipeOutput $$0) {
      this.save($$0, ResourceKey.create(Registries.RECIPE, getDefaultRecipeId(this.getResult())));
   }

   default void save(RecipeOutput $$0, String $$1) {
      ResourceLocation $$2 = getDefaultRecipeId(this.getResult());
      ResourceLocation $$3 = ResourceLocation.parse($$1);
      if ($$3.equals($$2)) {
         throw new IllegalStateException("Recipe " + $$1 + " should remove its 'save' argument as it is equal to default one");
      } else {
         this.save($$0, ResourceKey.create(Registries.RECIPE, $$3));
      }
   }

   static ResourceLocation getDefaultRecipeId(ItemLike $$0) {
      return BuiltInRegistries.ITEM.getKey($$0.asItem());
   }

   static CraftingBookCategory determineBookCategory(RecipeCategory $$0) {
      return switch($$0) {
         case BUILDING_BLOCKS -> CraftingBookCategory.BUILDING;
         case TOOLS, COMBAT -> CraftingBookCategory.EQUIPMENT;
         case REDSTONE -> CraftingBookCategory.REDSTONE;
         default -> CraftingBookCategory.MISC;
      };
   }
}
