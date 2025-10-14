package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;

public record Music(Holder<SoundEvent> event, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
   public static final Codec<Music> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               SoundEvent.CODEC.fieldOf("sound").forGetter($$0x -> $$0x.event),
               Codec.INT.fieldOf("min_delay").forGetter($$0x -> $$0x.minDelay),
               Codec.INT.fieldOf("max_delay").forGetter($$0x -> $$0x.maxDelay),
               Codec.BOOL.fieldOf("replace_current_music").forGetter($$0x -> $$0x.replaceCurrentMusic)
            )
            .apply($$0, Music::new)
   );
}
