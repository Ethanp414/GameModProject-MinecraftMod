package net.minecraft.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Block;

public class BlockFamily {
   private final Block baseBlock;
   final Map<BlockFamily.Variant, Block> variants = Maps.newHashMap();
   boolean generateModel = true;
   boolean generateRecipe = true;
   @Nullable
   String recipeGroupPrefix;
   @Nullable
   String recipeUnlockedBy;

   BlockFamily(Block $$0) {
      this.baseBlock = $$0;
   }

   public Block getBaseBlock() {
      return this.baseBlock;
   }

   public Map<BlockFamily.Variant, Block> getVariants() {
      return this.variants;
   }

   public Block get(BlockFamily.Variant $$0) {
      return (Block)this.variants.get($$0);
   }

   public boolean shouldGenerateModel() {
      return this.generateModel;
   }

   public boolean shouldGenerateRecipe() {
      return this.generateRecipe;
   }

   public Optional<String> getRecipeGroupPrefix() {
      return StringUtil.isBlank(this.recipeGroupPrefix) ? Optional.empty() : Optional.of(this.recipeGroupPrefix);
   }

   public Optional<String> getRecipeUnlockedBy() {
      return StringUtil.isBlank(this.recipeUnlockedBy) ? Optional.empty() : Optional.of(this.recipeUnlockedBy);
   }

   public static class Builder {
      private final BlockFamily family;

      public Builder(Block $$0) {
         this.family = new BlockFamily($$0);
      }

      public BlockFamily getFamily() {
         return this.family;
      }

      public BlockFamily.Builder button(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.BUTTON, $$0);
         return this;
      }

      public BlockFamily.Builder chiseled(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.CHISELED, $$0);
         return this;
      }

      public BlockFamily.Builder mosaic(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.MOSAIC, $$0);
         return this;
      }

      public BlockFamily.Builder cracked(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.CRACKED, $$0);
         return this;
      }

      public BlockFamily.Builder cut(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.CUT, $$0);
         return this;
      }

      public BlockFamily.Builder door(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.DOOR, $$0);
         return this;
      }

      public BlockFamily.Builder customFence(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE, $$0);
         return this;
      }

      public BlockFamily.Builder fence(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.FENCE, $$0);
         return this;
      }

      public BlockFamily.Builder customFenceGate(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, $$0);
         return this;
      }

      public BlockFamily.Builder fenceGate(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.FENCE_GATE, $$0);
         return this;
      }

      public BlockFamily.Builder sign(Block $$0, Block $$1) {
         this.family.variants.put(BlockFamily.Variant.SIGN, $$0);
         this.family.variants.put(BlockFamily.Variant.WALL_SIGN, $$1);
         return this;
      }

      public BlockFamily.Builder slab(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.SLAB, $$0);
         return this;
      }

      public BlockFamily.Builder stairs(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.STAIRS, $$0);
         return this;
      }

      public BlockFamily.Builder pressurePlate(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.PRESSURE_PLATE, $$0);
         return this;
      }

      public BlockFamily.Builder polished(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.POLISHED, $$0);
         return this;
      }

      public BlockFamily.Builder trapdoor(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.TRAPDOOR, $$0);
         return this;
      }

      public BlockFamily.Builder wall(Block $$0) {
         this.family.variants.put(BlockFamily.Variant.WALL, $$0);
         return this;
      }

      public BlockFamily.Builder dontGenerateModel() {
         this.family.generateModel = false;
         return this;
      }

      public BlockFamily.Builder dontGenerateRecipe() {
         this.family.generateRecipe = false;
         return this;
      }

      public BlockFamily.Builder recipeGroupPrefix(String $$0) {
         this.family.recipeGroupPrefix = $$0;
         return this;
      }

      public BlockFamily.Builder recipeUnlockedBy(String $$0) {
         this.family.recipeUnlockedBy = $$0;
         return this;
      }
   }

   public static enum Variant {
      BUTTON("button"),
      CHISELED("chiseled"),
      CRACKED("cracked"),
      CUT("cut"),
      DOOR("door"),
      CUSTOM_FENCE("fence"),
      FENCE("fence"),
      CUSTOM_FENCE_GATE("fence_gate"),
      FENCE_GATE("fence_gate"),
      MOSAIC("mosaic"),
      SIGN("sign"),
      SLAB("slab"),
      STAIRS("stairs"),
      PRESSURE_PLATE("pressure_plate"),
      POLISHED("polished"),
      TRAPDOOR("trapdoor"),
      WALL("wall"),
      WALL_SIGN("wall_sign");

      private final String recipeGroup;

      private Variant(final String param3) {
         this.recipeGroup = $$0;
      }

      public String getRecipeGroup() {
         return this.recipeGroup;
      }
   }
}
