package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedList;

public class BiomeSpecialEffects {
   public static final Codec<BiomeSpecialEffects> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               Codec.INT.fieldOf("fog_color").forGetter($$0x -> $$0x.fogColor),
               Codec.INT.fieldOf("water_color").forGetter($$0x -> $$0x.waterColor),
               Codec.INT.fieldOf("water_fog_color").forGetter($$0x -> $$0x.waterFogColor),
               Codec.INT.fieldOf("sky_color").forGetter($$0x -> $$0x.skyColor),
               Codec.INT.optionalFieldOf("foliage_color").forGetter($$0x -> $$0x.foliageColorOverride),
               Codec.INT.optionalFieldOf("dry_foliage_color").forGetter($$0x -> $$0x.dryFoliageColorOverride),
               Codec.INT.optionalFieldOf("grass_color").forGetter($$0x -> $$0x.grassColorOverride),
               BiomeSpecialEffects.GrassColorModifier.CODEC
                  .optionalFieldOf("grass_color_modifier", BiomeSpecialEffects.GrassColorModifier.NONE)
                  .forGetter($$0x -> $$0x.grassColorModifier),
               AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter($$0x -> $$0x.ambientParticleSettings),
               SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter($$0x -> $$0x.ambientLoopSoundEvent),
               AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter($$0x -> $$0x.ambientMoodSettings),
               AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter($$0x -> $$0x.ambientAdditionsSettings),
               WeightedList.codec(Music.CODEC).optionalFieldOf("music").forGetter($$0x -> $$0x.backgroundMusic),
               Codec.FLOAT.fieldOf("music_volume").orElse(1.0F).forGetter($$0x -> $$0x.backgroundMusicVolume)
            )
            .apply($$0, BiomeSpecialEffects::new)
   );
   private final int fogColor;
   private final int waterColor;
   private final int waterFogColor;
   private final int skyColor;
   private final Optional<Integer> foliageColorOverride;
   private final Optional<Integer> dryFoliageColorOverride;
   private final Optional<Integer> grassColorOverride;
   private final BiomeSpecialEffects.GrassColorModifier grassColorModifier;
   private final Optional<AmbientParticleSettings> ambientParticleSettings;
   private final Optional<Holder<SoundEvent>> ambientLoopSoundEvent;
   private final Optional<AmbientMoodSettings> ambientMoodSettings;
   private final Optional<AmbientAdditionsSettings> ambientAdditionsSettings;
   private final Optional<WeightedList<Music>> backgroundMusic;
   private final float backgroundMusicVolume;

   BiomeSpecialEffects(
      int $$0,
      int $$1,
      int $$2,
      int $$3,
      Optional<Integer> $$4,
      Optional<Integer> $$5,
      Optional<Integer> $$6,
      BiomeSpecialEffects.GrassColorModifier $$7,
      Optional<AmbientParticleSettings> $$8,
      Optional<Holder<SoundEvent>> $$9,
      Optional<AmbientMoodSettings> $$10,
      Optional<AmbientAdditionsSettings> $$11,
      Optional<WeightedList<Music>> $$12,
      float $$13
   ) {
      this.fogColor = $$0;
      this.waterColor = $$1;
      this.waterFogColor = $$2;
      this.skyColor = $$3;
      this.foliageColorOverride = $$4;
      this.dryFoliageColorOverride = $$5;
      this.grassColorOverride = $$6;
      this.grassColorModifier = $$7;
      this.ambientParticleSettings = $$8;
      this.ambientLoopSoundEvent = $$9;
      this.ambientMoodSettings = $$10;
      this.ambientAdditionsSettings = $$11;
      this.backgroundMusic = $$12;
      this.backgroundMusicVolume = $$13;
   }

   public int getFogColor() {
      return this.fogColor;
   }

   public int getWaterColor() {
      return this.waterColor;
   }

   public int getWaterFogColor() {
      return this.waterFogColor;
   }

   public int getSkyColor() {
      return this.skyColor;
   }

   public Optional<Integer> getFoliageColorOverride() {
      return this.foliageColorOverride;
   }

   public Optional<Integer> getDryFoliageColorOverride() {
      return this.dryFoliageColorOverride;
   }

   public Optional<Integer> getGrassColorOverride() {
      return this.grassColorOverride;
   }

   public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
      return this.grassColorModifier;
   }

   public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
      return this.ambientParticleSettings;
   }

   public Optional<Holder<SoundEvent>> getAmbientLoopSoundEvent() {
      return this.ambientLoopSoundEvent;
   }

   public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
      return this.ambientMoodSettings;
   }

   public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
      return this.ambientAdditionsSettings;
   }

   public Optional<WeightedList<Music>> getBackgroundMusic() {
      return this.backgroundMusic;
   }

   public float getBackgroundMusicVolume() {
      return this.backgroundMusicVolume;
   }

   public static class Builder {
      private OptionalInt fogColor = OptionalInt.empty();
      private OptionalInt waterColor = OptionalInt.empty();
      private OptionalInt waterFogColor = OptionalInt.empty();
      private OptionalInt skyColor = OptionalInt.empty();
      private Optional<Integer> foliageColorOverride = Optional.empty();
      private Optional<Integer> dryFoliageColorOverride = Optional.empty();
      private Optional<Integer> grassColorOverride = Optional.empty();
      private BiomeSpecialEffects.GrassColorModifier grassColorModifier = BiomeSpecialEffects.GrassColorModifier.NONE;
      private Optional<AmbientParticleSettings> ambientParticle = Optional.empty();
      private Optional<Holder<SoundEvent>> ambientLoopSoundEvent = Optional.empty();
      private Optional<AmbientMoodSettings> ambientMoodSettings = Optional.empty();
      private Optional<AmbientAdditionsSettings> ambientAdditionsSettings = Optional.empty();
      private Optional<WeightedList<Music>> backgroundMusic = Optional.empty();
      private float backgroundMusicVolume = 1.0F;

      public BiomeSpecialEffects.Builder fogColor(int $$0) {
         this.fogColor = OptionalInt.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder waterColor(int $$0) {
         this.waterColor = OptionalInt.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder waterFogColor(int $$0) {
         this.waterFogColor = OptionalInt.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder skyColor(int $$0) {
         this.skyColor = OptionalInt.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder foliageColorOverride(int $$0) {
         this.foliageColorOverride = Optional.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder dryFoliageColorOverride(int $$0) {
         this.dryFoliageColorOverride = Optional.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder grassColorOverride(int $$0) {
         this.grassColorOverride = Optional.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder grassColorModifier(BiomeSpecialEffects.GrassColorModifier $$0) {
         this.grassColorModifier = $$0;
         return this;
      }

      public BiomeSpecialEffects.Builder ambientParticle(AmbientParticleSettings $$0) {
         this.ambientParticle = Optional.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder ambientLoopSound(Holder<SoundEvent> $$0) {
         this.ambientLoopSoundEvent = Optional.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder ambientMoodSound(AmbientMoodSettings $$0) {
         this.ambientMoodSettings = Optional.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder ambientAdditionsSound(AmbientAdditionsSettings $$0) {
         this.ambientAdditionsSettings = Optional.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder backgroundMusic(@Nullable Music $$0) {
         if ($$0 == null) {
            this.backgroundMusic = Optional.empty();
            return this;
         } else {
            this.backgroundMusic = Optional.of(WeightedList.of($$0));
            return this;
         }
      }

      public BiomeSpecialEffects.Builder silenceAllBackgroundMusic() {
         return this.backgroundMusic(WeightedList.of()).backgroundMusicVolume(0.0F);
      }

      public BiomeSpecialEffects.Builder backgroundMusic(WeightedList<Music> $$0) {
         this.backgroundMusic = Optional.of($$0);
         return this;
      }

      public BiomeSpecialEffects.Builder backgroundMusicVolume(float $$0) {
         this.backgroundMusicVolume = $$0;
         return this;
      }

      public BiomeSpecialEffects build() {
         return new BiomeSpecialEffects(
            this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")),
            this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")),
            this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")),
            this.skyColor.orElseThrow(() -> new IllegalStateException("Missing 'sky' color.")),
            this.foliageColorOverride,
            this.dryFoliageColorOverride,
            this.grassColorOverride,
            this.grassColorModifier,
            this.ambientParticle,
            this.ambientLoopSoundEvent,
            this.ambientMoodSettings,
            this.ambientAdditionsSettings,
            this.backgroundMusic,
            this.backgroundMusicVolume
         );
      }
   }

   public static enum GrassColorModifier implements StringRepresentable {
      NONE("none") {
         @Override
         public int modifyColor(double $$0, double $$1, int $$2) {
            return $$2;
         }
      },
      DARK_FOREST("dark_forest") {
         @Override
         public int modifyColor(double $$0, double $$1, int $$2) {
            return ($$2 & 16711422) + 2634762 >> 1;
         }
      },
      SWAMP("swamp") {
         @Override
         public int modifyColor(double $$0, double $$1, int $$2) {
            double $$3 = Biome.BIOME_INFO_NOISE.getValue($$0 * 0.0225, $$1 * 0.0225, false);
            return $$3 < -0.1 ? 5011004 : 6975545;
         }
      };

      private final String name;
      public static final Codec<BiomeSpecialEffects.GrassColorModifier> CODEC = StringRepresentable.fromEnum(BiomeSpecialEffects.GrassColorModifier::values);

      public abstract int modifyColor(double var1, double var3, int var5);

      GrassColorModifier(final String param3) {
         this.name = $$0;
      }

      public String getName() {
         return this.name;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
