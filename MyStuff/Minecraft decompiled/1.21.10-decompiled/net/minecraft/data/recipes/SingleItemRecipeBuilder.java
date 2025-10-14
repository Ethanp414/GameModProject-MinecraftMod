package net.minecraft.data.recipes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
   @Nullable
   private String group;
   private final SingleItemRecipe.Factory<?> factory;

   public SingleItemRecipeBuilder(RecipeCategory $$0, SingleItemRecipe.Factory<?> $$1, Ingredient $$2, ItemLike $$3, int $$4) {
      this.category = $$0;
      this.factory = $$1;
      this.result = $$3.asItem();
      this.ingredient = $$2;
      this.count = $$4;
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient $$0, RecipeCategory $$1, ItemLike $$2) {
      return new SingleItemRecipeBuilder($$1, StonecutterRecipe::new, $$0, $$2, 1);
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient $$0, RecipeCategory $$1, ItemLike $$2, int $$3) {
      return new SingleItemRecipeBuilder($$1, StonecutterRecipe::new, $$0, $$2, $$3);
   }

   public SingleItemRecipeBuilder unlockedBy(String $$0, Criterion<?> $$1) {
      this.criteria.put($$0, $$1);
      return this;
   }

   public SingleItemRecipeBuilder group(@Nullable String $$0) {
      this.group = $$0;
      return this;
   }

   @Override
   public Item getResult() {
      return this.result;
   }

   @Override
   public void save(RecipeOutput $$0, ResourceKey<Recipe<?>> $$1) {
      this.ensureValid($$1);
      Advancement.Builder $$2 = $$0.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked($$1))
         .rewards(AdvancementRewards.Builder.recipe($$1))
         .requirements(AdvancementRequirements.Strategy.OR);
      this.criteria.forEach($$2::addCriterion);
      SingleItemRecipe $$3 = this.factory.create((String)Objects.requireNonNullElse(this.group, ""), this.ingredient, new ItemStack(this.result, this.count));
      $$0.accept($$1, $$3, $$2.build($$1.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private void ensureValid(ResourceKey<Recipe<?>> $$0) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + $$0.location());
      }
   }
}
