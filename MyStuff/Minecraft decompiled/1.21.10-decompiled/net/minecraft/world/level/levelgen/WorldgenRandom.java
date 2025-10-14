package net.minecraft.world.level.levelgen;

import java.util.function.LongFunction;
import net.minecraft.util.RandomSource;

public class WorldgenRandom extends LegacyRandomSource {
   private final RandomSource randomSource;
   private int count;

   public WorldgenRandom(RandomSource $$0) {
      super(0L);
      this.randomSource = $$0;
   }

   public int getCount() {
      return this.count;
   }

   @Override
   public RandomSource fork() {
      return this.randomSource.fork();
   }

   @Override
   public PositionalRandomFactory forkPositional() {
      return this.randomSource.forkPositional();
   }

   @Override
   public int next(int $$0) {
      ++this.count;
      RandomSource var3 = this.randomSource;
      return var3 instanceof LegacyRandomSource $$1 ? $$1.next($$0) : (int)(this.randomSource.nextLong() >>> 64 - $$0);
   }

   @Override
   public synchronized void setSeed(long $$0) {
      if (this.randomSource != null) {
         this.randomSource.setSeed($$0);
      }
   }

   public long setDecorationSeed(long $$0, int $$1, int $$2) {
      this.setSeed($$0);
      long $$3 = this.nextLong() | 1L;
      long $$4 = this.nextLong() | 1L;
      long $$5 = (long)$$1 * $$3 + (long)$$2 * $$4 ^ $$0;
      this.setSeed($$5);
      return $$5;
   }

   public void setFeatureSeed(long $$0, int $$1, int $$2) {
      long $$3 = $$0 + (long)$$1 + (long)(10000 * $$2);
      this.setSeed($$3);
   }

   public void setLargeFeatureSeed(long $$0, int $$1, int $$2) {
      this.setSeed($$0);
      long $$3 = this.nextLong();
      long $$4 = this.nextLong();
      long $$5 = (long)$$1 * $$3 ^ (long)$$2 * $$4 ^ $$0;
      this.setSeed($$5);
   }

   public void setLargeFeatureWithSalt(long $$0, int $$1, int $$2, int $$3) {
      long $$4 = (long)$$1 * 341873128712L + (long)$$2 * 132897987541L + $$0 + (long)$$3;
      this.setSeed($$4);
   }

   public static RandomSource seedSlimeChunk(int $$0, int $$1, long $$2, long $$3) {
      return RandomSource.create($$2 + (long)($$0 * $$0 * 4987142) + (long)($$0 * 5947611) + (long)($$1 * $$1) * 4392871L + (long)($$1 * 389711) ^ $$3);
   }

   public static enum Algorithm {
      LEGACY(LegacyRandomSource::new),
      XOROSHIRO(XoroshiroRandomSource::new);

      private final LongFunction<RandomSource> constructor;

      private Algorithm(final LongFunction<RandomSource> param3) {
         this.constructor = $$0;
      }

      public RandomSource newInstance(long $$0) {
         return (RandomSource)this.constructor.apply($$0);
      }
   }
}
