package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo.Builder;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(
   modid = "alexsmobs",
   bus = Bus.MOD
)
public class AMWorldRegistry {
   public static void modifyStructure(Holder<Structure> structure, Builder builder) {
      if (AMConfig.mimicubeSpawnInEndCity && structure.m_203565_(BuiltinStructures.f_209861_) && AMConfig.mimicubeSpawnWeight > 0) {
         builder.getStructureSettings()
            .getOrAddSpawnOverrides(MobCategory.MONSTER)
            .addSpawn(new SpawnerData((EntityType)AMEntityRegistry.MIMICUBE.get(), AMConfig.mimicubeSpawnWeight, 1, 3));
      }

      if (AMConfig.soulVultureSpawnOnFossil && structure.m_203565_(BuiltinStructures.f_209860_) && AMConfig.soulVultureSpawnWeight > 0) {
         builder.getStructureSettings()
            .getOrAddSpawnOverrides(MobCategory.MONSTER)
            .addSpawn(new SpawnerData((EntityType)AMEntityRegistry.SOUL_VULTURE.get(), AMConfig.soulVultureSpawnWeight, 1, 1));
      }

      if (AMConfig.restrictSkelewagSpawns && structure.m_203565_(BuiltinStructures.f_209852_) && AMConfig.skelewagSpawnWeight > 0) {
         builder.getStructureSettings()
            .getOrAddSpawnOverrides(MobCategory.MONSTER)
            .addSpawn(new SpawnerData((EntityType)AMEntityRegistry.SKELEWAG.get(), AMConfig.skelewagSpawnWeight, 1, 2));
      }

      if (AMConfig.restrictUnderminerSpawns && structure.m_203656_(AMTagRegistry.SPAWNS_UNDERMINERS) && AMConfig.underminerSpawnWeight > 0) {
         builder.getStructureSettings()
            .getOrAddSpawnOverrides(MobCategory.AMBIENT)
            .addSpawn(new SpawnerData((EntityType)AMEntityRegistry.UNDERMINER.get(), AMConfig.underminerSpawnWeight, 1, 1));
      }
   }

   private static ResourceLocation getBiomeName(Holder<Biome> biome) {
      return (ResourceLocation)biome.m_203439_().map(resourceKey -> resourceKey.m_135782_(), noKey -> null);
   }

   public static boolean testBiome(Pair<String, SpawnBiomeData> entry, Holder<Biome> biome) {
      boolean result = false;

      try {
         result = BiomeConfig.test(entry, biome, getBiomeName(biome));
      } catch (Exception var4) {
         AlexsMobs.LOGGER.warn("could not test biome config for " + (String)entry.getLeft() + ", defaulting to no spawns for mob");
         result = false;
      }

      return result;
   }

   public static void addBiomeSpawns(Holder<Biome> biome, net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder builder) {
      if (testBiome(BiomeConfig.grizzlyBear, biome) && AMConfig.grizzlyBearSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.GRIZZLY_BEAR.get(), AMConfig.grizzlyBearSpawnWeight, 2, 3));
      }

      if (testBiome(BiomeConfig.roadrunner, biome) && AMConfig.roadrunnerSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ROADRUNNER.get(), AMConfig.roadrunnerSpawnWeight, 2, 2));
      }

      if (testBiome(BiomeConfig.boneSerpent, biome) && AMConfig.boneSerpentSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.BONE_SERPENT.get(), AMConfig.boneSerpentSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.gazelle, biome) && AMConfig.gazelleSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.GAZELLE.get(), AMConfig.gazelleSpawnWeight, 7, 7));
      }

      if (testBiome(BiomeConfig.crocodile, biome) && AMConfig.crocodileSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.CROCODILE.get(), AMConfig.crocodileSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.fly, biome) && AMConfig.flySpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.FLY.get(), AMConfig.flySpawnWeight, 2, 3));
      }

      if (testBiome(BiomeConfig.hummingbird, biome) && AMConfig.hummingbirdSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.HUMMINGBIRD.get(), AMConfig.hummingbirdSpawnWeight, 7, 7));
      }

      if (testBiome(BiomeConfig.orca, biome) && AMConfig.orcaSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ORCA.get(), AMConfig.orcaSpawnWeight, 3, 4));
      }

      if (testBiome(BiomeConfig.sunbird, biome) && AMConfig.sunbirdSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SUNBIRD.get(), AMConfig.sunbirdSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.gorilla, biome) && AMConfig.gorillaSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.GORILLA.get(), AMConfig.gorillaSpawnWeight, 7, 7));
      }

      if (testBiome(BiomeConfig.crimsonMosquito, biome) && AMConfig.crimsonMosquitoSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.CRIMSON_MOSQUITO.get(), AMConfig.crimsonMosquitoSpawnWeight, 4, 4));
      }

      if (testBiome(BiomeConfig.rattlesnake, biome) && AMConfig.rattlesnakeSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.RATTLESNAKE.get(), AMConfig.rattlesnakeSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.endergrade, biome) && AMConfig.endergradeSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ENDERGRADE.get(), AMConfig.endergradeSpawnWeight, 2, 6));
      }

      if (testBiome(BiomeConfig.hammerheadShark, biome) && AMConfig.hammerheadSharkSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.HAMMERHEAD_SHARK.get(), AMConfig.hammerheadSharkSpawnWeight, 2, 3));
      }

      if (testBiome(BiomeConfig.lobster, biome) && AMConfig.lobsterSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.LOBSTER.get(), AMConfig.lobsterSpawnWeight, 3, 5));
      }

      if (testBiome(BiomeConfig.komodoDragon, biome) && AMConfig.komodoDragonSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.KOMODO_DRAGON.get(), AMConfig.komodoDragonSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.capuchinMonkey, biome) && AMConfig.capuchinMonkeySpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.CAPUCHIN_MONKEY.get(), AMConfig.capuchinMonkeySpawnWeight, 9, 16));
      }

      if (testBiome(BiomeConfig.caveCentipede, biome) && AMConfig.caveCentipedeSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.CENTIPEDE_HEAD.get(), AMConfig.caveCentipedeSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.warpedToad, biome) && AMConfig.warpedToadSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.WARPED_TOAD.get(), AMConfig.warpedToadSpawnWeight, 5, 5));
      }

      if (testBiome(BiomeConfig.moose, biome) && AMConfig.mooseSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.MOOSE.get(), AMConfig.mooseSpawnWeight, 3, 4));
      }

      if (testBiome(BiomeConfig.mimicube, biome) && AMConfig.mimicubeSpawnWeight > 0 && !AMConfig.mimicubeSpawnInEndCity) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.MIMICUBE.get(), AMConfig.mimicubeSpawnWeight, 1, 3));
      }

      if (testBiome(BiomeConfig.raccoon, biome) && AMConfig.raccoonSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.RACCOON.get(), AMConfig.raccoonSpawnWeight, 2, 4));
      }

      if (testBiome(BiomeConfig.blobfish, biome) && AMConfig.blobfishSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.BLOBFISH.get(), AMConfig.blobfishSpawnWeight, 2, 2));
      }

      if (testBiome(BiomeConfig.seal, biome) && AMConfig.sealSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SEAL.get(), AMConfig.sealSpawnWeight, 3, 8));
      }

      if (testBiome(BiomeConfig.cockroach, biome) && AMConfig.cockroachSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.COCKROACH.get(), AMConfig.cockroachSpawnWeight, 5, 5));
      }

      if (testBiome(BiomeConfig.shoebill, biome) && AMConfig.shoebillSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SHOEBILL.get(), AMConfig.shoebillSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.elephant, biome) && AMConfig.elephantSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ELEPHANT.get(), AMConfig.elephantSpawnWeight, 3, 5));
      }

      if (testBiome(BiomeConfig.soulVulture, biome) && AMConfig.soulVultureSpawnWeight > 0 && !AMConfig.soulVultureSpawnOnFossil) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SOUL_VULTURE.get(), AMConfig.soulVultureSpawnWeight, 2, 3));
      }

      if (testBiome(BiomeConfig.snowLeopard, biome) && AMConfig.snowLeopardSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SNOW_LEOPARD.get(), AMConfig.snowLeopardSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.spectre, biome) && AMConfig.spectreSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SPECTRE.get(), AMConfig.spectreSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.crow, biome) && AMConfig.crowSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.CROW.get(), AMConfig.crowSpawnWeight, 3, 5));
      }

      if (testBiome(BiomeConfig.alligatorSnappingTurtle, biome) && AMConfig.alligatorSnappingTurtleSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get(), AMConfig.alligatorSnappingTurtleSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.mungus, biome) && AMConfig.mungusSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.MUNGUS.get(), AMConfig.mungusSpawnWeight, 3, 5));
      }

      if (testBiome(BiomeConfig.mantisShrimp, biome) && AMConfig.mantisShrimpSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.MANTIS_SHRIMP.get(), AMConfig.mantisShrimpSpawnWeight, 1, 4));
      }

      if (testBiome(BiomeConfig.guster, biome) && AMConfig.gusterSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.GUSTER.get(), AMConfig.gusterSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.warpedMosco, biome) && AMConfig.warpedMoscoSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.WARPED_MOSCO.get(), AMConfig.warpedMoscoSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.straddler, biome) && AMConfig.straddlerSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.STRADDLER.get(), AMConfig.straddlerSpawnWeight, 1, 3));
      }

      if (testBiome(BiomeConfig.stradpole, biome) && AMConfig.stradpoleSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.STRADPOLE.get(), AMConfig.stradpoleSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.emu, biome) && AMConfig.emuSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.EMU.get(), AMConfig.emuSpawnWeight, 2, 5));
      }

      if (testBiome(BiomeConfig.platypus, biome) && AMConfig.platypusSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.PLATYPUS.get(), AMConfig.platypusSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.dropbear, biome) && AMConfig.dropbearSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.DROPBEAR.get(), AMConfig.dropbearSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.tasmanianDevil, biome) && AMConfig.tasmanianDevilSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.TASMANIAN_DEVIL.get(), AMConfig.tasmanianDevilSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.kangaroo, biome) && AMConfig.kangarooSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.KANGAROO.get(), AMConfig.kangarooSpawnWeight, 3, 5));
      }

      if (testBiome(BiomeConfig.cachalot_whale_spawns, biome) && AMConfig.cachalotWhaleSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.CACHALOT_WHALE.get(), AMConfig.cachalotWhaleSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.enderiophage_spawns, biome) && AMConfig.enderiophageSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ENDERIOPHAGE.get(), AMConfig.enderiophageSpawnWeight, 2, 2));
      }

      if (testBiome(BiomeConfig.baldEagle, biome) && AMConfig.baldEagleSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.BALD_EAGLE.get(), AMConfig.baldEagleSpawnWeight, 2, 4));
      }

      if (testBiome(BiomeConfig.tiger, biome) && AMConfig.tigerSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.TIGER.get(), AMConfig.tigerSpawnWeight, 1, 3));
      }

      if (testBiome(BiomeConfig.tarantula_hawk, biome) && AMConfig.tarantulaHawkSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.TARANTULA_HAWK.get(), AMConfig.tarantulaHawkSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.void_worm, biome) && AMConfig.voidWormSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.VOID_WORM.get(), AMConfig.voidWormSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.frilled_shark, biome) && AMConfig.frilledSharkSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.FRILLED_SHARK.get(), AMConfig.frilledSharkSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.mimic_octopus, biome) && AMConfig.mimicOctopusSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.MIMIC_OCTOPUS.get(), AMConfig.mimicOctopusSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.seagull, biome) && AMConfig.seagullSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SEAGULL.get(), AMConfig.seagullSpawnWeight, 3, 6));
      }

      if (testBiome(BiomeConfig.froststalker, biome) && AMConfig.froststalkerSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.FROSTSTALKER.get(), AMConfig.froststalkerSpawnWeight, 5, 7));
      }

      if (testBiome(BiomeConfig.tusklin, biome) && AMConfig.tusklinSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.TUSKLIN.get(), AMConfig.tusklinSpawnWeight, 3, 5));
      }

      if (testBiome(BiomeConfig.laviathan, biome) && AMConfig.laviathanSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.LAVIATHAN.get(), AMConfig.laviathanSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.cosmaw, biome) && AMConfig.cosmawSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.COSMAW.get(), AMConfig.cosmawSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.toucan, biome) && AMConfig.toucanSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.TOUCAN.get(), AMConfig.toucanSpawnWeight, 5, 5));
      }

      if (testBiome(BiomeConfig.maned_wolf, biome) && AMConfig.manedWolfSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.MANED_WOLF.get(), AMConfig.manedWolfSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.anaconda, biome) && AMConfig.anacondaSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ANACONDA.get(), AMConfig.anacondaSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.anteater, biome) && AMConfig.anteaterSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ANTEATER.get(), AMConfig.anteaterSpawnWeight, 1, 3));
      }

      if (testBiome(BiomeConfig.rocky_roller, biome) && AMConfig.rockyRollerSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.ROCKY_ROLLER.get(), AMConfig.rockyRollerSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.flutter, biome) && AMConfig.flutterSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.FLUTTER.get(), AMConfig.flutterSpawnWeight, 2, 4));
      }

      if (testBiome(BiomeConfig.gelada_monkey, biome) && AMConfig.geladaMonkeySpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.GELADA_MONKEY.get(), AMConfig.geladaMonkeySpawnWeight, 9, 16));
      }

      if (testBiome(BiomeConfig.jerboa, biome) && AMConfig.jerboaSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.JERBOA.get(), AMConfig.jerboaSpawnWeight, 1, 3));
      }

      if (testBiome(BiomeConfig.terrapin, biome) && AMConfig.terrapinSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.TERRAPIN.get(), AMConfig.terrapinSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.comb_jelly, biome) && AMConfig.combJellySpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.COMB_JELLY.get(), AMConfig.combJellySpawnWeight, 2, 3));
      }

      if (testBiome(BiomeConfig.cosmic_cod, biome) && AMConfig.cosmicCodSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.COSMIC_COD.get(), AMConfig.cosmicCodSpawnWeight, 9, 13));
      }

      if (testBiome(BiomeConfig.bunfungus, biome) && AMConfig.bunfungusSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.BUNFUNGUS.get(), AMConfig.bunfungusSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.bison, biome) && AMConfig.bisonSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.BISON.get(), AMConfig.bisonSpawnWeight, 6, 10));
      }

      if (testBiome(BiomeConfig.giant_squid, biome) && AMConfig.giantSquidSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.GIANT_SQUID.get(), AMConfig.giantSquidSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.devils_hole_pupfish, biome) && AMConfig.devilsHolePupfishSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.DEVILS_HOLE_PUPFISH.get(), AMConfig.devilsHolePupfishSpawnWeight, 5, 12));
      }

      if (testBiome(BiomeConfig.catfish, biome) && AMConfig.catfishSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.CATFISH.get(), AMConfig.catfishSpawnWeight, 1, 3));
      }

      if (testBiome(BiomeConfig.flying_fish, biome) && AMConfig.flyingFishSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.FLYING_FISH.get(), AMConfig.flyingFishSpawnWeight, 3, 6));
      }

      if (testBiome(BiomeConfig.skelewag, biome) && AMConfig.skelewagSpawnWeight > 0 && !AMConfig.restrictSkelewagSpawns) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SKELEWAG.get(), AMConfig.skelewagSpawnWeight, 2, 3));
      }

      if (testBiome(BiomeConfig.rain_frog, biome) && AMConfig.rainFrogSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.RAIN_FROG.get(), AMConfig.rainFrogSpawnWeight, 1, 3));
      }

      if (testBiome(BiomeConfig.potoo, biome) && AMConfig.potooSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.POTOO.get(), AMConfig.potooSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.mudskipper, biome) && AMConfig.mudskipperSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.MUDSKIPPER.get(), AMConfig.mudskipperSpawnWeight, 2, 4));
      }

      if (testBiome(BiomeConfig.rhinoceros, biome) && AMConfig.rhinocerosSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.RHINOCEROS.get(), AMConfig.rhinocerosSpawnWeight, 3, 5));
      }

      if (testBiome(BiomeConfig.sugar_glider, biome) && AMConfig.sugarGliderSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SUGAR_GLIDER.get(), AMConfig.sugarGliderSpawnWeight, 2, 4));
      }

      if (testBiome(BiomeConfig.farseer, biome) && AMConfig.farseerSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.FARSEER.get(), AMConfig.farseerSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.skreecher, biome) && AMConfig.skreecherSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SKREECHER.get(), AMConfig.skreecherSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.underminer, biome) && AMConfig.underminerSpawnWeight > 0 && !AMConfig.restrictUnderminerSpawns) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.UNDERMINER.get(), AMConfig.underminerSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.murmur, biome) && AMConfig.murmurSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.MONSTER)
            .add(new SpawnerData((EntityType)AMEntityRegistry.MURMUR.get(), AMConfig.murmurSpawnWeight, 1, 1));
      }

      if (testBiome(BiomeConfig.skunk, biome) && AMConfig.skunkSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.SKUNK.get(), AMConfig.skunkSpawnWeight, 1, 2));
      }

      if (testBiome(BiomeConfig.banana_slug, biome) && AMConfig.bananaSlugSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.BANANA_SLUG.get(), AMConfig.bananaSlugSpawnWeight, 2, 3));
      }

      if (testBiome(BiomeConfig.blue_jay, biome) && AMConfig.blueJaySpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.BLUE_JAY.get(), AMConfig.blueJaySpawnWeight, 2, 4));
      }

      if (testBiome(BiomeConfig.caiman, biome) && AMConfig.caimanSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.CREATURE)
            .add(new SpawnerData((EntityType)AMEntityRegistry.CAIMAN.get(), AMConfig.caimanSpawnWeight, 2, 4));
      }

      if (testBiome(BiomeConfig.triops, biome) && AMConfig.triopsSpawnWeight > 0) {
         builder.getMobSpawnSettings()
            .getSpawner(MobCategory.WATER_AMBIENT)
            .add(new SpawnerData((EntityType)AMEntityRegistry.TRIOPS.get(), AMConfig.triopsSpawnWeight, 2, 6));
      }
   }

   public static void addLeafcutterAntSpawns(
      Holder<Biome> biome, HolderSet<PlacedFeature> features, net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder builder
   ) {
      if (testBiome(BiomeConfig.leafcutter_anthill_spawns, biome) && AMConfig.leafcutterAnthillSpawnChance > 0.0) {
         features.forEach(feature -> builder.getGenerationSettings().getFeatures(Decoration.SURFACE_STRUCTURES).add(feature));
      }
   }
}
