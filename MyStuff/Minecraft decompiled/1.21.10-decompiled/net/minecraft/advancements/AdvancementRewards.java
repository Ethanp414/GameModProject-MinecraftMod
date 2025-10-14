package net.minecraft.advancements;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record AdvancementRewards(int experience, List<ResourceKey<LootTable>> loot, List<ResourceKey<Recipe<?>>> recipes, Optional<CacheableFunction> function) {
   public static final Codec<AdvancementRewards> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               Codec.INT.optionalFieldOf("experience", Integer.valueOf(0)).forGetter(AdvancementRewards::experience),
               LootTable.KEY_CODEC.listOf().optionalFieldOf("loot", List.of()).forGetter(AdvancementRewards::loot),
               Recipe.KEY_CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(AdvancementRewards::recipes),
               CacheableFunction.CODEC.optionalFieldOf("function").forGetter(AdvancementRewards::function)
            )
            .apply($$0, AdvancementRewards::new)
   );
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, List.of(), List.of(), Optional.empty());

   public void grant(ServerPlayer $$0) {
      $$0.giveExperiencePoints(this.experience);
      ServerLevel $$1 = $$0.level();
      MinecraftServer $$2 = $$1.getServer();
      LootParams $$3 = new LootParams.Builder($$1)
         .withParameter(LootContextParams.THIS_ENTITY, $$0)
         .withParameter(LootContextParams.ORIGIN, $$0.position())
         .create(LootContextParamSets.ADVANCEMENT_REWARD);
      boolean $$4 = false;

      for(ResourceKey<LootTable> $$5 : this.loot) {
         for(ItemStack $$6 : $$2.reloadableRegistries().getLootTable($$5).getRandomItems($$3)) {
            if ($$0.addItem($$6)) {
               $$1.playSound(
                  null,
                  $$0.getX(),
                  $$0.getY(),
                  $$0.getZ(),
                  SoundEvents.ITEM_PICKUP,
                  SoundSource.PLAYERS,
                  0.2F,
                  (($$0.getRandom().nextFloat() - $$0.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
               );
               $$4 = true;
            } else {
               ItemEntity $$7 = $$0.drop($$6, false);
               if ($$7 != null) {
                  $$7.setNoPickUpDelay();
                  $$7.setTarget($$0.getUUID());
               }
            }
         }
      }

      if ($$4) {
         $$0.containerMenu.broadcastChanges();
      }

      if (!this.recipes.isEmpty()) {
         $$0.awardRecipesByKey(this.recipes);
      }

      this.function
         .flatMap($$1x -> $$1x.get($$2.getFunctions()))
         .ifPresent($$2x -> $$2.getFunctions().execute($$2x, $$0.createCommandSourceStack().withSuppressedOutput().withPermission(2)));
   }

   public static class Builder {
      private int experience;
      private final ImmutableList.Builder<ResourceKey<LootTable>> loot = ImmutableList.builder();
      private final ImmutableList.Builder<ResourceKey<Recipe<?>>> recipes = ImmutableList.builder();
      private Optional<ResourceLocation> function = Optional.empty();

      public static AdvancementRewards.Builder experience(int $$0) {
         return new AdvancementRewards.Builder().addExperience($$0);
      }

      public AdvancementRewards.Builder addExperience(int $$0) {
         this.experience += $$0;
         return this;
      }

      public static AdvancementRewards.Builder loot(ResourceKey<LootTable> $$0) {
         return new AdvancementRewards.Builder().addLootTable($$0);
      }

      public AdvancementRewards.Builder addLootTable(ResourceKey<LootTable> $$0) {
         this.loot.add($$0);
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceKey<Recipe<?>> $$0) {
         return new AdvancementRewards.Builder().addRecipe($$0);
      }

      public AdvancementRewards.Builder addRecipe(ResourceKey<Recipe<?>> $$0) {
         this.recipes.add($$0);
         return this;
      }

      public static AdvancementRewards.Builder function(ResourceLocation $$0) {
         return new AdvancementRewards.Builder().runs($$0);
      }

      public AdvancementRewards.Builder runs(ResourceLocation $$0) {
         this.function = Optional.of($$0);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, this.loot.build(), this.recipes.build(), this.function.map(CacheableFunction::new));
      }
   }
}
