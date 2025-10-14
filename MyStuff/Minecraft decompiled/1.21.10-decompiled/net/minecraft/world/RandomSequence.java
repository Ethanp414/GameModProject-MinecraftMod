package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class RandomSequence {
   public static final Codec<RandomSequence> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(XoroshiroRandomSource.CODEC.fieldOf("source").forGetter($$0x -> $$0x.source)).apply($$0, RandomSequence::new)
   );
   private final XoroshiroRandomSource source;

   public RandomSequence(XoroshiroRandomSource $$0) {
      this.source = $$0;
   }

   public RandomSequence(long $$0, ResourceLocation $$1) {
      this(createSequence($$0, Optional.of($$1)));
   }

   public RandomSequence(long $$0, Optional<ResourceLocation> $$1) {
      this(createSequence($$0, $$1));
   }

   private static XoroshiroRandomSource createSequence(long $$0, Optional<ResourceLocation> $$1) {
      RandomSupport.Seed128bit $$2 = RandomSupport.upgradeSeedTo128bitUnmixed($$0);
      if ($$1.isPresent()) {
         $$2 = $$2.xor(seedForKey((ResourceLocation)$$1.get()));
      }

      return new XoroshiroRandomSource($$2.mixed());
   }

   public static RandomSupport.Seed128bit seedForKey(ResourceLocation $$0) {
      return RandomSupport.seedFromHashOf($$0.toString());
   }

   public RandomSource random() {
      return this.source;
   }
}
