package net.minecraft.world.item.alchemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;

public class PotionBrewing {
   public static final int BREWING_TIME_SECONDS = 20;
   public static final PotionBrewing EMPTY = new PotionBrewing(List.of(), List.of(), List.of());
   private final List<Ingredient> containers;
   private final List<PotionBrewing.Mix<Potion>> potionMixes;
   private final List<PotionBrewing.Mix<Item>> containerMixes;

   PotionBrewing(List<Ingredient> $$0, List<PotionBrewing.Mix<Potion>> $$1, List<PotionBrewing.Mix<Item>> $$2) {
      this.containers = $$0;
      this.potionMixes = $$1;
      this.containerMixes = $$2;
   }

   public boolean isIngredient(ItemStack $$0) {
      return this.isContainerIngredient($$0) || this.isPotionIngredient($$0);
   }

   private boolean isContainer(ItemStack $$0) {
      for(Ingredient $$1 : this.containers) {
         if ($$1.test($$0)) {
            return true;
         }
      }

      return false;
   }

   public boolean isContainerIngredient(ItemStack $$0) {
      for(PotionBrewing.Mix<Item> $$1 : this.containerMixes) {
         if ($$1.ingredient.test($$0)) {
            return true;
         }
      }

      return false;
   }

   public boolean isPotionIngredient(ItemStack $$0) {
      for(PotionBrewing.Mix<Potion> $$1 : this.potionMixes) {
         if ($$1.ingredient.test($$0)) {
            return true;
         }
      }

      return false;
   }

   public boolean isBrewablePotion(Holder<Potion> $$0) {
      for(PotionBrewing.Mix<Potion> $$1 : this.potionMixes) {
         if ($$1.to.is($$0)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasMix(ItemStack $$0, ItemStack $$1) {
      if (!this.isContainer($$0)) {
         return false;
      } else {
         return this.hasContainerMix($$0, $$1) || this.hasPotionMix($$0, $$1);
      }
   }

   public boolean hasContainerMix(ItemStack $$0, ItemStack $$1) {
      for(PotionBrewing.Mix<Item> $$2 : this.containerMixes) {
         if ($$0.is($$2.from) && $$2.ingredient.test($$1)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasPotionMix(ItemStack $$0, ItemStack $$1) {
      Optional<Holder<Potion>> $$2 = $$0.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
      if ($$2.isEmpty()) {
         return false;
      } else {
         for(PotionBrewing.Mix<Potion> $$3 : this.potionMixes) {
            if ($$3.from.is((Holder<Potion>)$$2.get()) && $$3.ingredient.test($$1)) {
               return true;
            }
         }

         return false;
      }
   }

   public ItemStack mix(ItemStack $$0, ItemStack $$1) {
      if ($$1.isEmpty()) {
         return $$1;
      } else {
         Optional<Holder<Potion>> $$2 = $$1.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
         if ($$2.isEmpty()) {
            return $$1;
         } else {
            for(PotionBrewing.Mix<Item> $$3 : this.containerMixes) {
               if ($$1.is($$3.from) && $$3.ingredient.test($$0)) {
                  return PotionContents.createItemStack($$3.to.value(), (Holder<Potion>)$$2.get());
               }
            }

            for(PotionBrewing.Mix<Potion> $$4 : this.potionMixes) {
               if ($$4.from.is((Holder<Potion>)$$2.get()) && $$4.ingredient.test($$0)) {
                  return PotionContents.createItemStack($$1.getItem(), $$4.to);
               }
            }

            return $$1;
         }
      }
   }

   public static PotionBrewing bootstrap(FeatureFlagSet $$0) {
      PotionBrewing.Builder $$1 = new PotionBrewing.Builder($$0);
      addVanillaMixes($$1);
      return $$1.build();
   }

   public static void addVanillaMixes(PotionBrewing.Builder $$0) {
      $$0.addContainer(Items.POTION);
      $$0.addContainer(Items.SPLASH_POTION);
      $$0.addContainer(Items.LINGERING_POTION);
      $$0.addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
      $$0.addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
      $$0.addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
      $$0.addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
      $$0.addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
      $$0.addStartMix(Items.BREEZE_ROD, Potions.WIND_CHARGED);
      $$0.addStartMix(Items.SLIME_BLOCK, Potions.OOZING);
      $$0.addStartMix(Items.STONE, Potions.INFESTED);
      $$0.addStartMix(Items.COBWEB, Potions.WEAVING);
      $$0.addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
      $$0.addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
      $$0.addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
      $$0.addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
      $$0.addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
      $$0.addStartMix(Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
      $$0.addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
      $$0.addStartMix(Items.RABBIT_FOOT, Potions.LEAPING);
      $$0.addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
      $$0.addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
      $$0.addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      $$0.addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      $$0.addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
      $$0.addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
      $$0.addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
      $$0.addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
      $$0.addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
      $$0.addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      $$0.addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      $$0.addStartMix(Items.SUGAR, Potions.SWIFTNESS);
      $$0.addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
      $$0.addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
      $$0.addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
      $$0.addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
      $$0.addStartMix(Items.GLISTERING_MELON_SLICE, Potions.HEALING);
      $$0.addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
      $$0.addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      $$0.addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      $$0.addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
      $$0.addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      $$0.addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      $$0.addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      $$0.addStartMix(Items.SPIDER_EYE, Potions.POISON);
      $$0.addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
      $$0.addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
      $$0.addStartMix(Items.GHAST_TEAR, Potions.REGENERATION);
      $$0.addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
      $$0.addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
      $$0.addStartMix(Items.BLAZE_POWDER, Potions.STRENGTH);
      $$0.addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
      $$0.addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
      $$0.addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
      $$0.addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
      $$0.addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
      $$0.addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
   }

   public static class Builder {
      private final List<Ingredient> containers = new ArrayList();
      private final List<PotionBrewing.Mix<Potion>> potionMixes = new ArrayList();
      private final List<PotionBrewing.Mix<Item>> containerMixes = new ArrayList();
      private final FeatureFlagSet enabledFeatures;

      public Builder(FeatureFlagSet $$0) {
         this.enabledFeatures = $$0;
      }

      private static void expectPotion(Item $$0) {
         if (!($$0 instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey($$0));
         }
      }

      public void addContainerRecipe(Item $$0, Item $$1, Item $$2) {
         if ($$0.isEnabled(this.enabledFeatures) && $$1.isEnabled(this.enabledFeatures) && $$2.isEnabled(this.enabledFeatures)) {
            expectPotion($$0);
            expectPotion($$2);
            this.containerMixes.add(new PotionBrewing.Mix<Item>($$0.builtInRegistryHolder(), Ingredient.of($$1), $$2.builtInRegistryHolder()));
         }
      }

      public void addContainer(Item $$0) {
         if ($$0.isEnabled(this.enabledFeatures)) {
            expectPotion($$0);
            this.containers.add(Ingredient.of($$0));
         }
      }

      public void addMix(Holder<Potion> $$0, Item $$1, Holder<Potion> $$2) {
         if ($$0.value().isEnabled(this.enabledFeatures) && $$1.isEnabled(this.enabledFeatures) && $$2.value().isEnabled(this.enabledFeatures)) {
            this.potionMixes.add(new PotionBrewing.Mix<Potion>($$0, Ingredient.of($$1), $$2));
         }
      }

      public void addStartMix(Item $$0, Holder<Potion> $$1) {
         if ($$1.value().isEnabled(this.enabledFeatures)) {
            this.addMix(Potions.WATER, $$0, Potions.MUNDANE);
            this.addMix(Potions.AWKWARD, $$0, $$1);
         }
      }

      public PotionBrewing build() {
         return new PotionBrewing(List.copyOf(this.containers), List.copyOf(this.potionMixes), List.copyOf(this.containerMixes));
      }
   }

   static record Mix<T>(Holder<T> from, Ingredient ingredient, Holder<T> to) {
      final Holder<T> from;
      final Ingredient ingredient;
      final Holder<T> to;
   }
}
