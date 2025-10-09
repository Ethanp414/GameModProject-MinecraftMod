package com.github.alexthe666.alexsmobs.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeModifier.Phase;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class AMMobSpawnBiomeModifier implements BiomeModifier {
   private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(
      new ResourceLocation("alexsmobs", "am_mob_spawns"), Keys.BIOME_MODIFIER_SERIALIZERS, "alexsmobs"
   );

   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         AMWorldRegistry.addBiomeSpawns(biome, builder);
      }
   }

   public Codec<? extends BiomeModifier> codec() {
      return (Codec<? extends BiomeModifier>)SERIALIZER.get();
   }

   public static Codec<AMMobSpawnBiomeModifier> makeCodec() {
      return Codec.unit(AMMobSpawnBiomeModifier::new);
   }
}
