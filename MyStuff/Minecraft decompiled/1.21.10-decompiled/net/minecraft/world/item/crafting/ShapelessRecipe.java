package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class ShapelessRecipe implements CraftingRecipe {
   final String group;
   final CraftingBookCategory category;
   final ItemStack result;
   final List<Ingredient> ingredients;
   @Nullable
   private PlacementInfo placementInfo;

   public ShapelessRecipe(String $$0, CraftingBookCategory $$1, ItemStack $$2, List<Ingredient> $$3) {
      this.group = $$0;
      this.category = $$1;
      this.result = $$2;
      this.ingredients = $$3;
   }

   @Override
   public RecipeSerializer<ShapelessRecipe> getSerializer() {
      return RecipeSerializer.SHAPELESS_RECIPE;
   }

   @Override
   public String group() {
      return this.group;
   }

   @Override
   public CraftingBookCategory category() {
      return this.category;
   }

   @Override
   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.create(this.ingredients);
      }

      return this.placementInfo;
   }

   public boolean matches(CraftingInput $$0, Level $$1) {
      if ($$0.ingredientCount() != this.ingredients.size()) {
         return false;
      } else {
         return $$0.size() == 1 && this.ingredients.size() == 1
            ? ((Ingredient)this.ingredients.getFirst()).test($$0.getItem(0))
            : $$0.stackedContents().canCraft(this, null);
      }
   }

   public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
      return this.result.copy();
   }

   @Override
   public List<RecipeDisplay> display() {
      return List.of(
         new ShapelessCraftingRecipeDisplay(
            this.ingredients.stream().map(Ingredient::display).toList(),
            new SlotDisplay.ItemStackSlotDisplay(this.result),
            new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
         )
      );
   }

   public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
      private static final MapCodec<ShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  Codec.STRING.optionalFieldOf("group", "").forGetter($$0x -> $$0x.group),
                  CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter($$0x -> $$0x.category),
                  ItemStack.STRICT_CODEC.fieldOf("result").forGetter($$0x -> $$0x.result),
                  Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter($$0x -> $$0x.ingredients)
               )
               .apply($$0, ShapelessRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8,
         $$0 -> $$0.group,
         CraftingBookCategory.STREAM_CODEC,
         $$0 -> $$0.category,
         ItemStack.STREAM_CODEC,
         $$0 -> $$0.result,
         Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
         $$0 -> $$0.ingredients,
         ShapelessRecipe::new
      );

      @Override
      public MapCodec<ShapelessRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
