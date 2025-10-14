package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record DimensionType(
   OptionalLong fixedTime,
   boolean hasSkyLight,
   boolean hasCeiling,
   boolean ultraWarm,
   boolean natural,
   double coordinateScale,
   boolean bedWorks,
   boolean respawnAnchorWorks,
   int minY,
   int height,
   int logicalHeight,
   TagKey<Block> infiniburn,
   ResourceLocation effectsLocation,
   float ambientLight,
   Optional<Integer> cloudHeight,
   DimensionType.MonsterSettings monsterSettings
) {
   public static final int BITS_FOR_Y = BlockPos.PACKED_Y_LENGTH;
   public static final int MIN_HEIGHT = 16;
   public static final int Y_SIZE = (1 << BITS_FOR_Y) - 32;
   public static final int MAX_Y = (Y_SIZE >> 1) - 1;
   public static final int MIN_Y = MAX_Y - Y_SIZE + 1;
   public static final int WAY_ABOVE_MAX_Y = MAX_Y << 4;
   public static final int WAY_BELOW_MIN_Y = MIN_Y << 4;
   public static final Codec<DimensionType> DIRECT_CODEC = ExtraCodecs.catchDecoderException(
      RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  ExtraCodecs.asOptionalLong(Codec.LONG.lenientOptionalFieldOf("fixed_time")).forGetter(DimensionType::fixedTime),
                  Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight),
                  Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling),
                  Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultraWarm),
                  Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural),
                  Codec.doubleRange(1.0E-5F, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale),
                  Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks),
                  Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks),
                  Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY),
                  Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height),
                  Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight),
                  TagKey.hashedCodec(Registries.BLOCK).fieldOf("infiniburn").forGetter(DimensionType::infiniburn),
                  ResourceLocation.CODEC.fieldOf("effects").orElse(BuiltinDimensionTypes.OVERWORLD_EFFECTS).forGetter(DimensionType::effectsLocation),
                  Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight),
                  Codec.intRange(MIN_Y, MAX_Y).optionalFieldOf("cloud_height").forGetter(DimensionType::cloudHeight),
                  DimensionType.MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings)
               )
               .apply($$0, DimensionType::new)
      )
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DimensionType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.DIMENSION_TYPE);
   public static final int MOON_PHASES = 8;
   public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public static final Codec<Holder<DimensionType>> CODEC = RegistryFileCodec.create(Registries.DIMENSION_TYPE, DIRECT_CODEC);

   public DimensionType(
      OptionalLong param1,
      boolean param2,
      boolean param3,
      boolean param4,
      boolean param5,
      double param6,
      boolean param8,
      boolean param9,
      int param10,
      int param11,
      int param12,
      TagKey<Block> param13,
      ResourceLocation param14,
      float param15,
      Optional<Integer> param16,
      DimensionType.MonsterSettings param17
   ) {
      if ($$9 < 16) {
         throw new IllegalStateException("height has to be at least 16");
      } else if ($$8 + $$9 > MAX_Y + 1) {
         throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_Y + 1));
      } else if ($$10 > $$9) {
         throw new IllegalStateException("logical_height cannot be higher than height");
      } else if ($$9 % 16 != 0) {
         throw new IllegalStateException("height has to be multiple of 16");
      } else if ($$8 % 16 != 0) {
         throw new IllegalStateException("min_y has to be a multiple of 16");
      } else {
         this.fixedTime = $$0;
         this.hasSkyLight = $$1;
         this.hasCeiling = $$2;
         this.ultraWarm = $$3;
         this.natural = $$4;
         this.coordinateScale = $$5;
         this.bedWorks = $$6;
         this.respawnAnchorWorks = $$7;
         this.minY = $$8;
         this.height = $$9;
         this.logicalHeight = $$10;
         this.infiniburn = $$11;
         this.effectsLocation = $$12;
         this.ambientLight = $$13;
         this.cloudHeight = $$14;
         this.monsterSettings = $$15;
      }
   }

   public static double getTeleportationScale(DimensionType $$0, DimensionType $$1) {
      double $$2 = $$0.coordinateScale();
      double $$3 = $$1.coordinateScale();
      return $$2 / $$3;
   }

   public static Path getStorageFolder(ResourceKey<Level> $$0, Path $$1) {
      if ($$0 == Level.OVERWORLD) {
         return $$1;
      } else if ($$0 == Level.END) {
         return $$1.resolve("DIM1");
      } else {
         return $$0 == Level.NETHER
            ? $$1.resolve("DIM-1")
            : $$1.resolve("dimensions").resolve($$0.location().getNamespace()).resolve($$0.location().getPath());
      }
   }

   public boolean hasFixedTime() {
      return this.fixedTime.isPresent();
   }

   public float timeOfDay(long $$0) {
      double $$1 = Mth.frac((double)this.fixedTime.orElse($$0) / 24000.0 - 0.25);
      double $$2 = 0.5 - Math.cos($$1 * Math.PI) / 2.0;
      return (float)($$1 * 2.0 + $$2) / 3.0F;
   }

   public int moonPhase(long $$0) {
      return (int)($$0 / 24000L % 8L + 8L) % 8;
   }

   public boolean piglinSafe() {
      return this.monsterSettings.piglinSafe();
   }

   public boolean hasRaids() {
      return this.monsterSettings.hasRaids();
   }

   public IntProvider monsterSpawnLightTest() {
      return this.monsterSettings.monsterSpawnLightTest();
   }

   public int monsterSpawnBlockLightLimit() {
      return this.monsterSettings.monsterSpawnBlockLightLimit();
   }

   public static record MonsterSettings(boolean piglinSafe, boolean hasRaids, IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {
      public static final MapCodec<DimensionType.MonsterSettings> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType.MonsterSettings::piglinSafe),
                  Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType.MonsterSettings::hasRaids),
                  IntProvider.codec(0, 15).fieldOf("monster_spawn_light_level").forGetter(DimensionType.MonsterSettings::monsterSpawnLightTest),
                  Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit").forGetter(DimensionType.MonsterSettings::monsterSpawnBlockLightLimit)
               )
               .apply($$0, DimensionType.MonsterSettings::new)
      );
   }
}
