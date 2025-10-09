package com.github.alexthe666.alexsmobs.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo.Builder;
import net.minecraftforge.common.world.StructureModifier.Phase;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class AMMobSpawnStructureModifier implements StructureModifier {
   private static final RegistryObject<Codec<? extends StructureModifier>> SERIALIZER = RegistryObject.create(
      new ResourceLocation("alexsmobs", "am_structure_spawns"), Keys.STRUCTURE_MODIFIER_SERIALIZERS, "alexsmobs"
   );

   public void modify(Holder<Structure> structure, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         AMWorldRegistry.modifyStructure(structure, builder);
      }
   }

   public Codec<? extends StructureModifier> codec() {
      return (Codec<? extends StructureModifier>)SERIALIZER.get();
   }

   public static Codec<AMMobSpawnStructureModifier> makeCodec() {
      return Codec.unit(AMMobSpawnStructureModifier::new);
   }
}
