package net.minecraft.data.worldgen.biome;

import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class OverworldBiomes {
   protected static final int NORMAL_WATER_COLOR = 4159204;
   protected static final int NORMAL_WATER_FOG_COLOR = 329011;
   private static final int OVERWORLD_FOG_COLOR = 12638463;
   private static final int DARK_DRY_FOLIAGE_COLOR = 8082228;
   @Nullable
   private static final Music NORMAL_MUSIC = null;
   public static final int SWAMP_SKELETON_WEIGHT = 70;

   protected static int calculateSkyColor(float $$0) {
      float $$1 = $$0 / 3.0F;
      $$1 = Mth.clamp($$1, -1.0F, 1.0F);
      return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
   }

   private static Biome biome(boolean $$0, float $$1, float $$2, MobSpawnSettings.Builder $$3, BiomeGenerationSettings.Builder $$4, @Nullable Music $$5) {
      return biome($$0, $$1, $$2, 4159204, 329011, null, null, null, $$3, $$4, $$5);
   }

   private static Biome biome(
      boolean $$0,
      float $$1,
      float $$2,
      int $$3,
      int $$4,
      @Nullable Integer $$5,
      @Nullable Integer $$6,
      @Nullable Integer $$7,
      MobSpawnSettings.Builder $$8,
      BiomeGenerationSettings.Builder $$9,
      @Nullable Music $$10
   ) {
      BiomeSpecialEffects.Builder $$11 = new BiomeSpecialEffects.Builder()
         .waterColor($$3)
         .waterFogColor($$4)
         .fogColor(12638463)
         .skyColor(calculateSkyColor($$1))
         .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
         .backgroundMusic($$10);
      if ($$5 != null) {
         $$11.grassColorOverride($$5);
      }

      if ($$6 != null) {
         $$11.foliageColorOverride($$6);
      }

      if ($$7 != null) {
         $$11.dryFoliageColorOverride($$7);
      }

      return new Biome.BiomeBuilder()
         .hasPrecipitation($$0)
         .temperature($$1)
         .downfall($$2)
         .specialEffects($$11.build())
         .mobSpawnSettings($$8.build())
         .generationSettings($$9.build())
         .build();
   }

   private static void globalOverworldGeneration(BiomeGenerationSettings.Builder $$0) {
      BiomeDefaultFeatures.addDefaultCarversAndLakes($$0);
      BiomeDefaultFeatures.addDefaultCrystalFormations($$0);
      BiomeDefaultFeatures.addDefaultMonsterRoom($$0);
      BiomeDefaultFeatures.addDefaultUndergroundVariety($$0);
      BiomeDefaultFeatures.addDefaultSprings($$0);
      BiomeDefaultFeatures.addSurfaceFreezing($$0);
   }

   public static Biome oldGrowthTaiga(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals($$3);
      $$3.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 4));
      $$3.addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3));
      $$3.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.FOX, 2, 4));
      if ($$2) {
         BiomeDefaultFeatures.commonSpawns($$3);
      } else {
         BiomeDefaultFeatures.caveSpawns($$3);
         BiomeDefaultFeatures.monsters($$3, 100, 25, 100, false);
      }

      BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$4);
      BiomeDefaultFeatures.addMossyStoneBlock($$4);
      BiomeDefaultFeatures.addFerns($$4);
      BiomeDefaultFeatures.addDefaultOres($$4);
      BiomeDefaultFeatures.addDefaultSoftDisks($$4);
      $$4.addFeature(
         GenerationStep.Decoration.VEGETAL_DECORATION,
         $$2 ? VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA
      );
      BiomeDefaultFeatures.addDefaultFlowers($$4);
      BiomeDefaultFeatures.addGiantTaigaVegetation($$4);
      BiomeDefaultFeatures.addDefaultMushrooms($$4);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$4, true);
      BiomeDefaultFeatures.addCommonBerryBushes($$4);
      Music $$5 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA);
      return biome(true, $$2 ? 0.25F : 0.3F, 0.8F, $$3, $$4, $$5);
   }

   public static Biome sparseJungle(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns($$2);
      $$2.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 2, 4));
      return baseJungle($$0, $$1, 0.8F, false, true, false, $$2, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SPARSE_JUNGLE));
   }

   public static Biome jungle(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns($$2);
      $$2.addSpawn(MobCategory.CREATURE, 40, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 1, 2))
         .addSpawn(MobCategory.MONSTER, 2, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 1, 3))
         .addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 2));
      return baseJungle($$0, $$1, 0.9F, false, false, true, $$2, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE));
   }

   public static Biome bambooJungle(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.baseJungleSpawns($$2);
      $$2.addSpawn(MobCategory.CREATURE, 40, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 1, 2))
         .addSpawn(MobCategory.CREATURE, 80, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 1, 2))
         .addSpawn(MobCategory.MONSTER, 2, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 1, 1));
      return baseJungle($$0, $$1, 0.9F, true, false, true, $$2, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BAMBOO_JUNGLE));
   }

   private static Biome baseJungle(
      HolderGetter<PlacedFeature> $$0,
      HolderGetter<ConfiguredWorldCarver<?>> $$1,
      float $$2,
      boolean $$3,
      boolean $$4,
      boolean $$5,
      MobSpawnSettings.Builder $$6,
      Music $$7
   ) {
      BiomeGenerationSettings.Builder $$8 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$8);
      BiomeDefaultFeatures.addDefaultOres($$8);
      BiomeDefaultFeatures.addDefaultSoftDisks($$8);
      if ($$3) {
         BiomeDefaultFeatures.addBambooVegetation($$8);
      } else {
         if ($$5) {
            BiomeDefaultFeatures.addLightBambooVegetation($$8);
         }

         if ($$4) {
            BiomeDefaultFeatures.addSparseJungleTrees($$8);
         } else {
            BiomeDefaultFeatures.addJungleTrees($$8);
         }
      }

      BiomeDefaultFeatures.addWarmFlowers($$8);
      BiomeDefaultFeatures.addJungleGrass($$8);
      BiomeDefaultFeatures.addDefaultMushrooms($$8);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$8, true);
      BiomeDefaultFeatures.addJungleVines($$8);
      if ($$4) {
         BiomeDefaultFeatures.addSparseJungleMelons($$8);
      } else {
         BiomeDefaultFeatures.addJungleMelons($$8);
      }

      return biome(true, 0.95F, $$2, $$6, $$8, $$7);
   }

   public static Biome windsweptHills(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals($$3);
      $$3.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 4, 6));
      BiomeDefaultFeatures.commonSpawns($$3);
      BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$4);
      BiomeDefaultFeatures.addDefaultOres($$4);
      BiomeDefaultFeatures.addDefaultSoftDisks($$4);
      if ($$2) {
         BiomeDefaultFeatures.addMountainForestTrees($$4);
      } else {
         BiomeDefaultFeatures.addMountainTrees($$4);
      }

      BiomeDefaultFeatures.addBushes($$4);
      BiomeDefaultFeatures.addDefaultFlowers($$4);
      BiomeDefaultFeatures.addDefaultGrass($$4);
      BiomeDefaultFeatures.addDefaultMushrooms($$4);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$4, true);
      BiomeDefaultFeatures.addExtraEmeralds($$4);
      BiomeDefaultFeatures.addInfestedStone($$4);
      return biome(true, 0.2F, 0.3F, $$3, $$4, NORMAL_MUSIC);
   }

   public static Biome desert(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.desertSpawns($$2);
      BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
      BiomeDefaultFeatures.addFossilDecoration($$3);
      globalOverworldGeneration($$3);
      BiomeDefaultFeatures.addDefaultOres($$3);
      BiomeDefaultFeatures.addDefaultSoftDisks($$3);
      BiomeDefaultFeatures.addDefaultFlowers($$3);
      BiomeDefaultFeatures.addDefaultGrass($$3);
      BiomeDefaultFeatures.addDesertVegetation($$3);
      BiomeDefaultFeatures.addDefaultMushrooms($$3);
      BiomeDefaultFeatures.addDesertExtraVegetation($$3);
      BiomeDefaultFeatures.addDesertExtraDecoration($$3);
      return biome(false, 2.0F, 0.0F, $$2, $$3, Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DESERT));
   }

   public static Biome plains(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2, boolean $$3, boolean $$4) {
      MobSpawnSettings.Builder $$5 = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder $$6 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$6);
      if ($$3) {
         $$5.creatureGenerationProbability(0.07F);
         BiomeDefaultFeatures.snowySpawns($$5);
         if ($$4) {
            $$6.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_SPIKE);
            $$6.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MiscOverworldPlacements.ICE_PATCH);
         }
      } else {
         BiomeDefaultFeatures.plainsSpawns($$5);
         BiomeDefaultFeatures.addPlainGrass($$6);
         if ($$2) {
            $$6.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUNFLOWER);
         } else {
            BiomeDefaultFeatures.addBushes($$6);
         }
      }

      BiomeDefaultFeatures.addDefaultOres($$6);
      BiomeDefaultFeatures.addDefaultSoftDisks($$6);
      if ($$3) {
         BiomeDefaultFeatures.addSnowyTrees($$6);
         BiomeDefaultFeatures.addDefaultFlowers($$6);
         BiomeDefaultFeatures.addDefaultGrass($$6);
      } else {
         BiomeDefaultFeatures.addPlainVegetation($$6);
      }

      BiomeDefaultFeatures.addDefaultMushrooms($$6);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$6, true);
      float $$7 = $$3 ? 0.0F : 0.8F;
      return biome(true, $$7, $$3 ? 0.5F : 0.4F, $$5, $$6, NORMAL_MUSIC);
   }

   public static Biome mushroomFields(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.mooshroomSpawns($$2);
      BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$3);
      BiomeDefaultFeatures.addDefaultOres($$3);
      BiomeDefaultFeatures.addDefaultSoftDisks($$3);
      BiomeDefaultFeatures.addMushroomFieldVegetation($$3);
      BiomeDefaultFeatures.addNearWaterVegetation($$3);
      return biome(true, 0.9F, 1.0F, $$2, $$3, NORMAL_MUSIC);
   }

   public static Biome savanna(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2, boolean $$3) {
      BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$4);
      if (!$$2) {
         BiomeDefaultFeatures.addSavannaGrass($$4);
      }

      BiomeDefaultFeatures.addDefaultOres($$4);
      BiomeDefaultFeatures.addDefaultSoftDisks($$4);
      if ($$2) {
         BiomeDefaultFeatures.addShatteredSavannaTrees($$4);
         BiomeDefaultFeatures.addDefaultFlowers($$4);
         BiomeDefaultFeatures.addShatteredSavannaGrass($$4);
      } else {
         BiomeDefaultFeatures.addSavannaTrees($$4);
         BiomeDefaultFeatures.addWarmFlowers($$4);
         BiomeDefaultFeatures.addSavannaExtraGrass($$4);
      }

      BiomeDefaultFeatures.addDefaultMushrooms($$4);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$4, true);
      MobSpawnSettings.Builder $$5 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals($$5);
      $$5.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 2, 6))
         .addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1))
         .addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 2, 3));
      BiomeDefaultFeatures.commonSpawns($$5);
      if ($$3) {
         $$5.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 4, 4));
         $$5.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 8));
      }

      return biome(false, 2.0F, 0.0F, $$5, $$4, NORMAL_MUSIC);
   }

   public static Biome badlands(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals($$3);
      BiomeDefaultFeatures.commonSpawns($$3);
      $$3.addSpawn(MobCategory.CREATURE, 6, new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 1, 2));
      $$3.creatureGenerationProbability(0.03F);
      if ($$2) {
         $$3.addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 8));
         $$3.creatureGenerationProbability(0.04F);
      }

      BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$4);
      BiomeDefaultFeatures.addDefaultOres($$4);
      BiomeDefaultFeatures.addExtraGold($$4);
      BiomeDefaultFeatures.addDefaultSoftDisks($$4);
      if ($$2) {
         BiomeDefaultFeatures.addBadlandsTrees($$4);
      }

      BiomeDefaultFeatures.addBadlandGrass($$4);
      BiomeDefaultFeatures.addDefaultMushrooms($$4);
      BiomeDefaultFeatures.addBadlandExtraVegetation($$4);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(false)
         .temperature(2.0F)
         .downfall(0.0F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(4159204)
               .waterFogColor(329011)
               .fogColor(12638463)
               .skyColor(calculateSkyColor(2.0F))
               .foliageColorOverride(10387789)
               .grassColorOverride(9470285)
               .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
               .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS))
               .build()
         )
         .mobSpawnSettings($$3.build())
         .generationSettings($$4.build())
         .build();
   }

   private static Biome baseOcean(MobSpawnSettings.Builder $$0, int $$1, int $$2, BiomeGenerationSettings.Builder $$3) {
      return biome(true, 0.5F, 0.5F, $$1, $$2, null, null, null, $$0, $$3, NORMAL_MUSIC);
   }

   private static BiomeGenerationSettings.Builder baseOceanGeneration(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$2);
      BiomeDefaultFeatures.addDefaultOres($$2);
      BiomeDefaultFeatures.addDefaultSoftDisks($$2);
      BiomeDefaultFeatures.addWaterTrees($$2);
      BiomeDefaultFeatures.addDefaultFlowers($$2);
      BiomeDefaultFeatures.addDefaultGrass($$2);
      BiomeDefaultFeatures.addDefaultMushrooms($$2);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$2, true);
      return $$2;
   }

   public static Biome coldOcean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns($$3, 3, 4, 15);
      $$3.addSpawn(MobCategory.WATER_AMBIENT, 15, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 1, 5));
      BiomeGenerationSettings.Builder $$4 = baseOceanGeneration($$0, $$1);
      $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, $$2 ? AquaticPlacements.SEAGRASS_DEEP_COLD : AquaticPlacements.SEAGRASS_COLD);
      BiomeDefaultFeatures.addColdOceanExtraVegetation($$4);
      return baseOcean($$3, 4020182, 329011, $$4);
   }

   public static Biome ocean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.oceanSpawns($$3, 1, 4, 10);
      $$3.addSpawn(MobCategory.WATER_CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 2));
      BiomeGenerationSettings.Builder $$4 = baseOceanGeneration($$0, $$1);
      $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, $$2 ? AquaticPlacements.SEAGRASS_DEEP : AquaticPlacements.SEAGRASS_NORMAL);
      BiomeDefaultFeatures.addColdOceanExtraVegetation($$4);
      return baseOcean($$3, 4159204, 329011, $$4);
   }

   public static Biome lukeWarmOcean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      if ($$2) {
         BiomeDefaultFeatures.oceanSpawns($$3, 8, 4, 8);
      } else {
         BiomeDefaultFeatures.oceanSpawns($$3, 10, 2, 15);
      }

      $$3.addSpawn(MobCategory.WATER_AMBIENT, 5, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 1, 3))
         .addSpawn(MobCategory.WATER_AMBIENT, 25, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 8, 8))
         .addSpawn(MobCategory.WATER_CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 1, 2));
      BiomeGenerationSettings.Builder $$4 = baseOceanGeneration($$0, $$1);
      $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, $$2 ? AquaticPlacements.SEAGRASS_DEEP_WARM : AquaticPlacements.SEAGRASS_WARM);
      BiomeDefaultFeatures.addLukeWarmKelp($$4);
      return baseOcean($$3, 4566514, 267827, $$4);
   }

   public static Biome warmOcean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder()
         .addSpawn(MobCategory.WATER_AMBIENT, 15, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 1, 3));
      BiomeDefaultFeatures.warmOceanSpawns($$2, 10, 4);
      BiomeGenerationSettings.Builder $$3 = baseOceanGeneration($$0, $$1)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.WARM_OCEAN_VEGETATION)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_WARM)
         .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEA_PICKLE);
      return baseOcean($$2, 4445678, 270131, $$3);
   }

   public static Biome frozenOcean(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder()
         .addSpawn(MobCategory.WATER_CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 4))
         .addSpawn(MobCategory.WATER_AMBIENT, 15, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 1, 5))
         .addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 2));
      BiomeDefaultFeatures.commonSpawns($$3);
      $$3.addSpawn(MobCategory.MONSTER, 5, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 1, 1));
      float $$4 = $$2 ? 0.5F : 0.0F;
      BiomeGenerationSettings.Builder $$5 = new BiomeGenerationSettings.Builder($$0, $$1);
      BiomeDefaultFeatures.addIcebergs($$5);
      globalOverworldGeneration($$5);
      BiomeDefaultFeatures.addBlueIce($$5);
      BiomeDefaultFeatures.addDefaultOres($$5);
      BiomeDefaultFeatures.addDefaultSoftDisks($$5);
      BiomeDefaultFeatures.addWaterTrees($$5);
      BiomeDefaultFeatures.addDefaultFlowers($$5);
      BiomeDefaultFeatures.addDefaultGrass($$5);
      BiomeDefaultFeatures.addDefaultMushrooms($$5);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$5, true);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(true)
         .temperature($$4)
         .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
         .downfall(0.5F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(3750089)
               .waterFogColor(329011)
               .fogColor(12638463)
               .skyColor(calculateSkyColor($$4))
               .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
               .build()
         )
         .mobSpawnSettings($$3.build())
         .generationSettings($$5.build())
         .build();
   }

   public static Biome forest(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2, boolean $$3, boolean $$4) {
      BiomeGenerationSettings.Builder $$5 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$5);
      Music $$6;
      if ($$4) {
         $$6 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST);
         $$5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
      } else {
         $$6 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST);
         BiomeDefaultFeatures.addForestFlowers($$5);
      }

      BiomeDefaultFeatures.addDefaultOres($$5);
      BiomeDefaultFeatures.addDefaultSoftDisks($$5);
      if ($$4) {
         $$5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_FLOWER_FOREST);
         $$5.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FLOWER_FOREST);
         BiomeDefaultFeatures.addDefaultGrass($$5);
      } else {
         if ($$2) {
            BiomeDefaultFeatures.addBirchForestFlowers($$5);
            if ($$3) {
               BiomeDefaultFeatures.addTallBirchTrees($$5);
            } else {
               BiomeDefaultFeatures.addBirchTrees($$5);
            }
         } else {
            BiomeDefaultFeatures.addOtherBirchTrees($$5);
         }

         BiomeDefaultFeatures.addBushes($$5);
         BiomeDefaultFeatures.addDefaultFlowers($$5);
         BiomeDefaultFeatures.addForestGrass($$5);
      }

      BiomeDefaultFeatures.addDefaultMushrooms($$5);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$5, true);
      MobSpawnSettings.Builder $$8 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals($$8);
      BiomeDefaultFeatures.commonSpawns($$8);
      if ($$4) {
         $$8.addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3));
      } else if (!$$2) {
         $$8.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 4));
      }

      float $$9 = $$2 ? 0.6F : 0.7F;
      return biome(true, $$9, $$2 ? 0.6F : 0.8F, $$8, $$5, $$6);
   }

   public static Biome taiga(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals($$3);
      $$3.addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 4, 4))
         .addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3))
         .addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.FOX, 2, 4));
      BiomeDefaultFeatures.commonSpawns($$3);
      float $$4 = $$2 ? -0.5F : 0.25F;
      BiomeGenerationSettings.Builder $$5 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$5);
      BiomeDefaultFeatures.addFerns($$5);
      BiomeDefaultFeatures.addDefaultOres($$5);
      BiomeDefaultFeatures.addDefaultSoftDisks($$5);
      BiomeDefaultFeatures.addTaigaTrees($$5);
      BiomeDefaultFeatures.addDefaultFlowers($$5);
      BiomeDefaultFeatures.addTaigaGrass($$5);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$5, true);
      if ($$2) {
         BiomeDefaultFeatures.addRareBerryBushes($$5);
      } else {
         BiomeDefaultFeatures.addCommonBerryBushes($$5);
      }

      return biome(true, $$4, $$2 ? 0.4F : 0.8F, $$2 ? 4020182 : 4159204, 329011, null, null, null, $$3, $$5, NORMAL_MUSIC);
   }

   public static Biome darkForest(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      if (!$$2) {
         BiomeDefaultFeatures.farmAnimals($$3);
      }

      BiomeDefaultFeatures.commonSpawns($$3);
      BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$4);
      $$4.addFeature(
         GenerationStep.Decoration.VEGETAL_DECORATION, $$2 ? VegetationPlacements.PALE_GARDEN_VEGETATION : VegetationPlacements.DARK_FOREST_VEGETATION
      );
      if (!$$2) {
         BiomeDefaultFeatures.addForestFlowers($$4);
      } else {
         $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PALE_MOSS_PATCH);
         $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PALE_GARDEN_FLOWERS);
      }

      BiomeDefaultFeatures.addDefaultOres($$4);
      BiomeDefaultFeatures.addDefaultSoftDisks($$4);
      if (!$$2) {
         BiomeDefaultFeatures.addDefaultFlowers($$4);
      } else {
         $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PALE_GARDEN);
      }

      BiomeDefaultFeatures.addForestGrass($$4);
      if (!$$2) {
         BiomeDefaultFeatures.addDefaultMushrooms($$4);
         BiomeDefaultFeatures.addLeafLitterPatch($$4);
      }

      BiomeDefaultFeatures.addDefaultExtraVegetation($$4, true);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(true)
         .temperature(0.7F)
         .downfall(0.8F)
         .specialEffects(
            $$2
               ? new BiomeSpecialEffects.Builder()
                  .waterColor(7768221)
                  .waterFogColor(5597568)
                  .fogColor(8484720)
                  .skyColor(12171705)
                  .grassColorOverride(7832178)
                  .foliageColorOverride(8883574)
                  .dryFoliageColorOverride(10528412)
                  .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                  .silenceAllBackgroundMusic()
                  .build()
               : new BiomeSpecialEffects.Builder()
                  .waterColor(4159204)
                  .waterFogColor(329011)
                  .fogColor(12638463)
                  .skyColor(calculateSkyColor(0.7F))
                  .dryFoliageColorOverride(8082228)
                  .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST)
                  .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                  .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST))
                  .build()
         )
         .mobSpawnSettings($$3.build())
         .generationSettings($$4.build())
         .build();
   }

   public static Biome swamp(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.farmAnimals($$2);
      BiomeDefaultFeatures.commonSpawns($$2, 70);
      $$2.addSpawn(MobCategory.MONSTER, 1, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1));
      $$2.addSpawn(MobCategory.MONSTER, 30, new MobSpawnSettings.SpawnerData(EntityType.BOGGED, 4, 4));
      $$2.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.FROG, 2, 5));
      BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
      BiomeDefaultFeatures.addFossilDecoration($$3);
      globalOverworldGeneration($$3);
      BiomeDefaultFeatures.addDefaultOres($$3);
      BiomeDefaultFeatures.addSwampClayDisk($$3);
      BiomeDefaultFeatures.addSwampVegetation($$3);
      BiomeDefaultFeatures.addDefaultMushrooms($$3);
      BiomeDefaultFeatures.addSwampExtraVegetation($$3);
      $$3.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_SWAMP);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(true)
         .temperature(0.8F)
         .downfall(0.9F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(6388580)
               .waterFogColor(2302743)
               .fogColor(12638463)
               .skyColor(calculateSkyColor(0.8F))
               .foliageColorOverride(6975545)
               .dryFoliageColorOverride(8082228)
               .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP)
               .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
               .backgroundMusic($$4)
               .build()
         )
         .mobSpawnSettings($$2.build())
         .generationSettings($$3.build())
         .build();
   }

   public static Biome mangroveSwamp(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns($$2, 70);
      $$2.addSpawn(MobCategory.MONSTER, 1, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 1, 1));
      $$2.addSpawn(MobCategory.MONSTER, 30, new MobSpawnSettings.SpawnerData(EntityType.BOGGED, 4, 4));
      $$2.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.FROG, 2, 5));
      $$2.addSpawn(MobCategory.WATER_AMBIENT, 25, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 8, 8));
      BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
      BiomeDefaultFeatures.addFossilDecoration($$3);
      globalOverworldGeneration($$3);
      BiomeDefaultFeatures.addDefaultOres($$3);
      BiomeDefaultFeatures.addMangroveSwampDisks($$3);
      BiomeDefaultFeatures.addMangroveSwampVegetation($$3);
      BiomeDefaultFeatures.addMangroveSwampExtraVegetation($$3);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP);
      return new Biome.BiomeBuilder()
         .hasPrecipitation(true)
         .temperature(0.8F)
         .downfall(0.9F)
         .specialEffects(
            new BiomeSpecialEffects.Builder()
               .waterColor(3832426)
               .waterFogColor(5077600)
               .fogColor(12638463)
               .skyColor(calculateSkyColor(0.8F))
               .foliageColorOverride(9285927)
               .dryFoliageColorOverride(8082228)
               .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP)
               .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
               .backgroundMusic($$4)
               .build()
         )
         .mobSpawnSettings($$2.build())
         .generationSettings($$3.build())
         .build();
   }

   public static Biome river(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder()
         .addSpawn(MobCategory.WATER_CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 1, 4))
         .addSpawn(MobCategory.WATER_AMBIENT, 5, new MobSpawnSettings.SpawnerData(EntityType.SALMON, 1, 5));
      BiomeDefaultFeatures.commonSpawns($$3);
      $$3.addSpawn(MobCategory.MONSTER, $$2 ? 1 : 100, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 1, 1));
      BiomeGenerationSettings.Builder $$4 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$4);
      BiomeDefaultFeatures.addDefaultOres($$4);
      BiomeDefaultFeatures.addDefaultSoftDisks($$4);
      BiomeDefaultFeatures.addWaterTrees($$4);
      BiomeDefaultFeatures.addBushes($$4);
      BiomeDefaultFeatures.addDefaultFlowers($$4);
      BiomeDefaultFeatures.addDefaultGrass($$4);
      BiomeDefaultFeatures.addDefaultMushrooms($$4);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$4, true);
      if (!$$2) {
         $$4.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);
      }

      float $$5 = $$2 ? 0.0F : 0.5F;
      return biome(true, $$5, 0.5F, $$2 ? 3750089 : 4159204, 329011, null, null, null, $$3, $$4, NORMAL_MUSIC);
   }

   public static Biome beach(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2, boolean $$3) {
      MobSpawnSettings.Builder $$4 = new MobSpawnSettings.Builder();
      boolean $$5 = !$$3 && !$$2;
      if ($$5) {
         $$4.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 2, 5));
      }

      BiomeDefaultFeatures.commonSpawns($$4);
      BiomeGenerationSettings.Builder $$6 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$6);
      BiomeDefaultFeatures.addDefaultOres($$6);
      BiomeDefaultFeatures.addDefaultSoftDisks($$6);
      BiomeDefaultFeatures.addDefaultFlowers($$6);
      BiomeDefaultFeatures.addDefaultGrass($$6);
      BiomeDefaultFeatures.addDefaultMushrooms($$6);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$6, true);
      float $$7;
      if ($$2) {
         $$7 = 0.05F;
      } else if ($$3) {
         $$7 = 0.2F;
      } else {
         $$7 = 0.8F;
      }

      return biome(true, $$7, $$5 ? 0.4F : 0.3F, $$2 ? 4020182 : 4159204, 329011, null, null, null, $$4, $$6, NORMAL_MUSIC);
   }

   public static Biome theVoid(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
      $$2.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, MiscOverworldPlacements.VOID_START_PLATFORM);
      return biome(false, 0.5F, 0.5F, new MobSpawnSettings.Builder(), $$2, NORMAL_MUSIC);
   }

   public static Biome meadowOrCherryGrove(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1, boolean $$2) {
      BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
      MobSpawnSettings.Builder $$4 = new MobSpawnSettings.Builder();
      $$4.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData($$2 ? EntityType.PIG : EntityType.DONKEY, 1, 2))
         .addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 6))
         .addSpawn(MobCategory.CREATURE, 2, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 2, 4));
      BiomeDefaultFeatures.commonSpawns($$4);
      globalOverworldGeneration($$3);
      BiomeDefaultFeatures.addPlainGrass($$3);
      BiomeDefaultFeatures.addDefaultOres($$3);
      BiomeDefaultFeatures.addDefaultSoftDisks($$3);
      if ($$2) {
         BiomeDefaultFeatures.addCherryGroveVegetation($$3);
      } else {
         BiomeDefaultFeatures.addMeadowVegetation($$3);
      }

      BiomeDefaultFeatures.addExtraEmeralds($$3);
      BiomeDefaultFeatures.addInfestedStone($$3);
      Music $$5 = Musics.createGameMusic($$2 ? SoundEvents.MUSIC_BIOME_CHERRY_GROVE : SoundEvents.MUSIC_BIOME_MEADOW);
      return $$2
         ? biome(true, 0.5F, 0.8F, 6141935, 6141935, 11983713, 11983713, null, $$4, $$3, $$5)
         : biome(true, 0.5F, 0.8F, 937679, 329011, null, null, null, $$4, $$3, $$5);
   }

   public static Biome frozenPeaks(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      $$3.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3));
      BiomeDefaultFeatures.commonSpawns($$3);
      globalOverworldGeneration($$2);
      BiomeDefaultFeatures.addFrozenSprings($$2);
      BiomeDefaultFeatures.addDefaultOres($$2);
      BiomeDefaultFeatures.addDefaultSoftDisks($$2);
      BiomeDefaultFeatures.addExtraEmeralds($$2);
      BiomeDefaultFeatures.addInfestedStone($$2);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FROZEN_PEAKS);
      return biome(true, -0.7F, 0.9F, $$3, $$2, $$4);
   }

   public static Biome jaggedPeaks(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      $$3.addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3));
      BiomeDefaultFeatures.commonSpawns($$3);
      globalOverworldGeneration($$2);
      BiomeDefaultFeatures.addFrozenSprings($$2);
      BiomeDefaultFeatures.addDefaultOres($$2);
      BiomeDefaultFeatures.addDefaultSoftDisks($$2);
      BiomeDefaultFeatures.addExtraEmeralds($$2);
      BiomeDefaultFeatures.addInfestedStone($$2);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS);
      return biome(true, -0.7F, 0.9F, $$3, $$2, $$4);
   }

   public static Biome stonyPeaks(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.commonSpawns($$3);
      globalOverworldGeneration($$2);
      BiomeDefaultFeatures.addDefaultOres($$2);
      BiomeDefaultFeatures.addDefaultSoftDisks($$2);
      BiomeDefaultFeatures.addExtraEmeralds($$2);
      BiomeDefaultFeatures.addInfestedStone($$2);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS);
      return biome(true, 1.0F, 0.3F, $$3, $$2, $$4);
   }

   public static Biome snowySlopes(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      $$3.addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3))
         .addSpawn(MobCategory.CREATURE, 5, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3));
      BiomeDefaultFeatures.commonSpawns($$3);
      globalOverworldGeneration($$2);
      BiomeDefaultFeatures.addFrozenSprings($$2);
      BiomeDefaultFeatures.addDefaultOres($$2);
      BiomeDefaultFeatures.addDefaultSoftDisks($$2);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$2, false);
      BiomeDefaultFeatures.addExtraEmeralds($$2);
      BiomeDefaultFeatures.addInfestedStone($$2);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SNOWY_SLOPES);
      return biome(true, -0.3F, 0.9F, $$3, $$2, $$4);
   }

   public static Biome grove(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      BiomeGenerationSettings.Builder $$2 = new BiomeGenerationSettings.Builder($$0, $$1);
      MobSpawnSettings.Builder $$3 = new MobSpawnSettings.Builder();
      $$3.addSpawn(MobCategory.CREATURE, 1, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 1, 1))
         .addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 3))
         .addSpawn(MobCategory.CREATURE, 4, new MobSpawnSettings.SpawnerData(EntityType.FOX, 2, 4));
      BiomeDefaultFeatures.commonSpawns($$3);
      globalOverworldGeneration($$2);
      BiomeDefaultFeatures.addFrozenSprings($$2);
      BiomeDefaultFeatures.addDefaultOres($$2);
      BiomeDefaultFeatures.addDefaultSoftDisks($$2);
      BiomeDefaultFeatures.addGroveTrees($$2);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$2, false);
      BiomeDefaultFeatures.addExtraEmeralds($$2);
      BiomeDefaultFeatures.addInfestedStone($$2);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE);
      return biome(true, -0.2F, 0.8F, $$3, $$2, $$4);
   }

   public static Biome lushCaves(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      $$2.addSpawn(MobCategory.AXOLOTLS, 10, new MobSpawnSettings.SpawnerData(EntityType.AXOLOTL, 4, 6));
      $$2.addSpawn(MobCategory.WATER_AMBIENT, 25, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 8, 8));
      BiomeDefaultFeatures.commonSpawns($$2);
      BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$3);
      BiomeDefaultFeatures.addPlainGrass($$3);
      BiomeDefaultFeatures.addDefaultOres($$3);
      BiomeDefaultFeatures.addLushCavesSpecialOres($$3);
      BiomeDefaultFeatures.addDefaultSoftDisks($$3);
      BiomeDefaultFeatures.addLushCavesVegetationFeatures($$3);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES);
      return biome(true, 0.5F, 0.5F, $$2, $$3, $$4);
   }

   public static Biome dripstoneCaves(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeDefaultFeatures.dripstoneCavesSpawns($$2);
      BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
      globalOverworldGeneration($$3);
      BiomeDefaultFeatures.addPlainGrass($$3);
      BiomeDefaultFeatures.addDefaultOres($$3, true);
      BiomeDefaultFeatures.addDefaultSoftDisks($$3);
      BiomeDefaultFeatures.addPlainVegetation($$3);
      BiomeDefaultFeatures.addDefaultMushrooms($$3);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$3, false);
      BiomeDefaultFeatures.addDripstone($$3);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);
      return biome(true, 0.8F, 0.4F, $$2, $$3, $$4);
   }

   public static Biome deepDark(HolderGetter<PlacedFeature> $$0, HolderGetter<ConfiguredWorldCarver<?>> $$1) {
      MobSpawnSettings.Builder $$2 = new MobSpawnSettings.Builder();
      BiomeGenerationSettings.Builder $$3 = new BiomeGenerationSettings.Builder($$0, $$1);
      $$3.addCarver(Carvers.CAVE);
      $$3.addCarver(Carvers.CAVE_EXTRA_UNDERGROUND);
      $$3.addCarver(Carvers.CANYON);
      BiomeDefaultFeatures.addDefaultCrystalFormations($$3);
      BiomeDefaultFeatures.addDefaultMonsterRoom($$3);
      BiomeDefaultFeatures.addDefaultUndergroundVariety($$3);
      BiomeDefaultFeatures.addSurfaceFreezing($$3);
      BiomeDefaultFeatures.addPlainGrass($$3);
      BiomeDefaultFeatures.addDefaultOres($$3);
      BiomeDefaultFeatures.addDefaultSoftDisks($$3);
      BiomeDefaultFeatures.addPlainVegetation($$3);
      BiomeDefaultFeatures.addDefaultMushrooms($$3);
      BiomeDefaultFeatures.addDefaultExtraVegetation($$3, false);
      BiomeDefaultFeatures.addSculk($$3);
      Music $$4 = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK);
      return biome(true, 0.8F, 0.4F, $$2, $$3, $$4);
   }
}
