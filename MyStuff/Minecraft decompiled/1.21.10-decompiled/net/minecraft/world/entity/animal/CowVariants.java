package net.minecraft.world.entity.animal;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public class CowVariants {
   public static final ResourceKey<CowVariant> TEMPERATE = createKey(TemperatureVariants.TEMPERATE);
   public static final ResourceKey<CowVariant> WARM = createKey(TemperatureVariants.WARM);
   public static final ResourceKey<CowVariant> COLD = createKey(TemperatureVariants.COLD);
   public static final ResourceKey<CowVariant> DEFAULT = TEMPERATE;

   private static ResourceKey<CowVariant> createKey(ResourceLocation $$0) {
      return ResourceKey.create(Registries.COW_VARIANT, $$0);
   }

   public static void bootstrap(BootstrapContext<CowVariant> $$0) {
      register($$0, TEMPERATE, CowVariant.ModelType.NORMAL, "temperate_cow", SpawnPrioritySelectors.fallback(0));
      register($$0, WARM, CowVariant.ModelType.WARM, "warm_cow", BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS);
      register($$0, COLD, CowVariant.ModelType.COLD, "cold_cow", BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS);
   }

   private static void register(BootstrapContext<CowVariant> $$0, ResourceKey<CowVariant> $$1, CowVariant.ModelType $$2, String $$3, TagKey<Biome> $$4) {
      HolderSet<Biome> $$5 = $$0.lookup(Registries.BIOME).getOrThrow($$4);
      register($$0, $$1, $$2, $$3, SpawnPrioritySelectors.single(new BiomeCheck($$5), 1));
   }

   private static void register(
      BootstrapContext<CowVariant> $$0, ResourceKey<CowVariant> $$1, CowVariant.ModelType $$2, String $$3, SpawnPrioritySelectors $$4
   ) {
      ResourceLocation $$5 = ResourceLocation.withDefaultNamespace("entity/cow/" + $$3);
      $$0.register($$1, new CowVariant(new ModelAndTexture<>($$2, $$5), $$4));
   }
}
