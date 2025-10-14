package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class AmbientAdditionsSettings {
   public static final Codec<AmbientAdditionsSettings> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               SoundEvent.CODEC.fieldOf("sound").forGetter($$0x -> $$0x.soundEvent), Codec.DOUBLE.fieldOf("tick_chance").forGetter($$0x -> $$0x.tickChance)
            )
            .apply($$0, AmbientAdditionsSettings::new)
   );
   private final Holder<SoundEvent> soundEvent;
   private final double tickChance;

   public AmbientAdditionsSettings(Holder<SoundEvent> $$0, double $$1) {
      this.soundEvent = $$0;
      this.tickChance = $$1;
   }

   public Holder<SoundEvent> getSoundEvent() {
      return this.soundEvent;
   }

   public double getTickChance() {
      return this.tickChance;
   }
}
