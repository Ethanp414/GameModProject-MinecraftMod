package com.github.alexthe666.alexsmobs.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeModifier.Phase;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class AMLeafcutterAntBiomeModifier implements BiomeModifier {
   private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(
      new ResourceLocation("alexsmobs", "am_leafcutter_ant_spawns"), Keys.BIOME_MODIFIER_SERIALIZERS, "alexsmobs"
   );
   private final HolderSet<PlacedFeature> features;

   public AMLeafcutterAntBiomeModifier(HolderSet<PlacedFeature> features) {
      this.features = features;
   }

   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         AMWorldRegistry.addLeafcutterAntSpawns(biome, this.features, builder);
      }
   }

   public Codec<? extends BiomeModifier> codec() {
      return (Codec<? extends BiomeModifier>)SERIALIZER.get();
   }

   public static Codec<AMLeafcutterAntBiomeModifier> makeCodec() {
      return RecordCodecBuilder.create(
         config -> config.group(PlacedFeature.f_191774_.fieldOf("features").forGetter(otherConfig -> otherConfig.features))
            .apply(config, AMLeafcutterAntBiomeModifier::new)
      );
   }
}
