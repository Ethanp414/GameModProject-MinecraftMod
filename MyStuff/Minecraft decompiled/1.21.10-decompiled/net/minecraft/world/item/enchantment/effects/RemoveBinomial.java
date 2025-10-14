package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record RemoveBinomial(LevelBasedValue chance) implements EnchantmentValueEffect {
   public static final MapCodec<RemoveBinomial> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(LevelBasedValue.CODEC.fieldOf("chance").forGetter(RemoveBinomial::chance)).apply($$0, RemoveBinomial::new)
   );

   @Override
   public float process(int $$0, RandomSource $$1, float $$2) {
      float $$3 = this.chance.calculate($$0);
      int $$4 = 0;

      for(int $$5 = 0; (float)$$5 < $$2; ++$$5) {
         if ($$1.nextFloat() < $$3) {
            ++$$4;
         }
      }

      return $$2 - (float)$$4;
   }

   @Override
   public MapCodec<RemoveBinomial> codec() {
      return CODEC;
   }
}
