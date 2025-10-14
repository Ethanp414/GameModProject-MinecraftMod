package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

public class AmbientParticleSettings {
   public static final Codec<AmbientParticleSettings> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               ParticleTypes.CODEC.fieldOf("options").forGetter($$0x -> $$0x.options), Codec.FLOAT.fieldOf("probability").forGetter($$0x -> $$0x.probability)
            )
            .apply($$0, AmbientParticleSettings::new)
   );
   private final ParticleOptions options;
   private final float probability;

   public AmbientParticleSettings(ParticleOptions $$0, float $$1) {
      this.options = $$0;
      this.probability = $$1;
   }

   public ParticleOptions getOptions() {
      return this.options;
   }

   public boolean canSpawn(RandomSource $$0) {
      return $$0.nextFloat() <= this.probability;
   }
}
