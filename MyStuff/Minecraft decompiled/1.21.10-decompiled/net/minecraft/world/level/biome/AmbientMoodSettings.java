package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class AmbientMoodSettings {
   public static final Codec<AmbientMoodSettings> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               SoundEvent.CODEC.fieldOf("sound").forGetter($$0x -> $$0x.soundEvent),
               Codec.INT.fieldOf("tick_delay").forGetter($$0x -> $$0x.tickDelay),
               Codec.INT.fieldOf("block_search_extent").forGetter($$0x -> $$0x.blockSearchExtent),
               Codec.DOUBLE.fieldOf("offset").forGetter($$0x -> $$0x.soundPositionOffset)
            )
            .apply($$0, AmbientMoodSettings::new)
   );
   public static final AmbientMoodSettings LEGACY_CAVE_SETTINGS = new AmbientMoodSettings(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
   private final Holder<SoundEvent> soundEvent;
   private final int tickDelay;
   private final int blockSearchExtent;
   private final double soundPositionOffset;

   public AmbientMoodSettings(Holder<SoundEvent> $$0, int $$1, int $$2, double $$3) {
      this.soundEvent = $$0;
      this.tickDelay = $$1;
      this.blockSearchExtent = $$2;
      this.soundPositionOffset = $$3;
   }

   public Holder<SoundEvent> getSoundEvent() {
      return this.soundEvent;
   }

   public int getTickDelay() {
      return this.tickDelay;
   }

   public int getBlockSearchExtent() {
      return this.blockSearchExtent;
   }

   public double getSoundPositionOffset() {
      return this.soundPositionOffset;
   }
}
