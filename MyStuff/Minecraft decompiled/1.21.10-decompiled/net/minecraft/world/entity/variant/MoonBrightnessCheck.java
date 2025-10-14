package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.advancements.critereon.MinMaxBounds;

public record MoonBrightnessCheck(MinMaxBounds.Doubles range) implements SpawnCondition {
   public static final MapCodec<MoonBrightnessCheck> MAP_CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(MinMaxBounds.Doubles.CODEC.fieldOf("range").forGetter(MoonBrightnessCheck::range)).apply($$0, MoonBrightnessCheck::new)
   );

   public boolean test(SpawnContext $$0) {
      return this.range.matches((double)$$0.level().getLevel().getMoonBrightness());
   }

   @Override
   public MapCodec<MoonBrightnessCheck> codec() {
      return MAP_CODEC;
   }
}
