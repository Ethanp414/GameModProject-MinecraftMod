package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

public record SoundEvent(ResourceLocation location, Optional<Float> fixedRange) {
   public static final Codec<SoundEvent> DIRECT_CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               ResourceLocation.CODEC.fieldOf("sound_id").forGetter(SoundEvent::location),
               Codec.FLOAT.lenientOptionalFieldOf("range").forGetter(SoundEvent::fixedRange)
            )
            .apply($$0, SoundEvent::create)
   );
   public static final Codec<Holder<SoundEvent>> CODEC = RegistryFileCodec.create(Registries.SOUND_EVENT, DIRECT_CODEC);
   public static final StreamCodec<ByteBuf, SoundEvent> DIRECT_STREAM_CODEC = StreamCodec.composite(
      ResourceLocation.STREAM_CODEC, SoundEvent::location, ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), SoundEvent::fixedRange, SoundEvent::create
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SoundEvent>> STREAM_CODEC = ByteBufCodecs.holder(
      Registries.SOUND_EVENT, DIRECT_STREAM_CODEC
   );

   private static SoundEvent create(ResourceLocation $$0, Optional<Float> $$1) {
      return (SoundEvent)$$1.map($$1x -> createFixedRangeEvent($$0, $$1x)).orElseGet(() -> createVariableRangeEvent($$0));
   }

   public static SoundEvent createVariableRangeEvent(ResourceLocation $$0) {
      return new SoundEvent($$0, Optional.empty());
   }

   public static SoundEvent createFixedRangeEvent(ResourceLocation $$0, float $$1) {
      return new SoundEvent($$0, Optional.of($$1));
   }

   public float getRange(float $$0) {
      return this.fixedRange.orElse($$0 > 1.0F ? 16.0F * $$0 : 16.0F);
   }
}
