package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EquipmentTable;

public record SpawnData(CompoundTag entityToSpawn, Optional<SpawnData.CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
   public static final String ENTITY_TAG = "entity";
   public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               CompoundTag.CODEC.fieldOf("entity").forGetter($$0x -> $$0x.entityToSpawn),
               SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter($$0x -> $$0x.customSpawnRules),
               EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter($$0x -> $$0x.equipment)
            )
            .apply($$0, SpawnData::new)
   );
   public static final Codec<WeightedList<SpawnData>> LIST_CODEC = WeightedList.codec(CODEC);

   public SpawnData() {
      this(new CompoundTag(), Optional.empty(), Optional.empty());
   }

   public SpawnData(CompoundTag param1, Optional<SpawnData.CustomSpawnRules> param2, Optional<EquipmentTable> param3) {
      Optional<ResourceLocation> $$3 = $$0.read("id", ResourceLocation.CODEC);
      if ($$3.isPresent()) {
         $$0.store("id", ResourceLocation.CODEC, (ResourceLocation)$$3.get());
      } else {
         $$0.remove("id");
      }

      this.entityToSpawn = $$0;
      this.customSpawnRules = $$1;
      this.equipment = $$2;
   }

   public CompoundTag getEntityToSpawn() {
      return this.entityToSpawn;
   }

   public Optional<SpawnData.CustomSpawnRules> getCustomSpawnRules() {
      return this.customSpawnRules;
   }

   public Optional<EquipmentTable> getEquipment() {
      return this.equipment;
   }

   public static record CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
      private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange(0, 15);
      public static final Codec<SpawnData.CustomSpawnRules> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  lightLimit("block_light_limit").forGetter($$0x -> $$0x.blockLightLimit), lightLimit("sky_light_limit").forGetter($$0x -> $$0x.skyLightLimit)
               )
               .apply($$0, SpawnData.CustomSpawnRules::new)
      );

      private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> $$0) {
         return !LIGHT_RANGE.contains($$0) ? DataResult.error(() -> "Light values must be withing range " + LIGHT_RANGE) : DataResult.success($$0);
      }

      private static MapCodec<InclusiveRange<Integer>> lightLimit(String $$0) {
         return InclusiveRange.INT.lenientOptionalFieldOf($$0, LIGHT_RANGE).validate(SpawnData.CustomSpawnRules::checkLightBoundaries);
      }

      public boolean isValidPosition(BlockPos $$0, ServerLevel $$1) {
         return this.blockLightLimit.isValueInRange($$1.getBrightness(LightLayer.BLOCK, $$0))
            && this.skyLightLimit.isValueInRange($$1.getBrightness(LightLayer.SKY, $$0));
      }
   }
}
