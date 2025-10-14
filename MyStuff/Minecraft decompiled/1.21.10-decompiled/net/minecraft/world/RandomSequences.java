package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class RandomSequences extends SavedData {
   public static final SavedDataType<RandomSequences> TYPE = new SavedDataType<>(
      "random_sequences", $$0 -> new RandomSequences($$0.worldSeed()), $$0 -> codec($$0.worldSeed()), DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES
   );
   private final long worldSeed;
   private int salt;
   private boolean includeWorldSeed = true;
   private boolean includeSequenceId = true;
   private final Map<ResourceLocation, RandomSequence> sequences = new Object2ObjectOpenHashMap<>();

   public RandomSequences(long $$0) {
      this.worldSeed = $$0;
   }

   private RandomSequences(long $$0, int $$1, boolean $$2, boolean $$3, Map<ResourceLocation, RandomSequence> $$4) {
      this.worldSeed = $$0;
      this.salt = $$1;
      this.includeWorldSeed = $$2;
      this.includeSequenceId = $$3;
      this.sequences.putAll($$4);
   }

   public static Codec<RandomSequences> codec(long $$0) {
      return RecordCodecBuilder.create(
         $$1 -> $$1.group(
                  RecordCodecBuilder.point($$0),
                  Codec.INT.fieldOf("salt").forGetter($$0xx -> $$0xx.salt),
                  Codec.BOOL.optionalFieldOf("include_world_seed", Boolean.valueOf(true)).forGetter($$0xx -> $$0xx.includeWorldSeed),
                  Codec.BOOL.optionalFieldOf("include_sequence_id", Boolean.valueOf(true)).forGetter($$0xx -> $$0xx.includeSequenceId),
                  Codec.unboundedMap(ResourceLocation.CODEC, RandomSequence.CODEC).fieldOf("sequences").forGetter($$0xx -> $$0xx.sequences)
               )
               .apply($$1, RandomSequences::new)
      );
   }

   public RandomSource get(ResourceLocation $$0) {
      RandomSource $$1 = ((RandomSequence)this.sequences.computeIfAbsent($$0, this::createSequence)).random();
      return new RandomSequences.DirtyMarkingRandomSource($$1);
   }

   private RandomSequence createSequence(ResourceLocation $$0) {
      return this.createSequence($$0, this.salt, this.includeWorldSeed, this.includeSequenceId);
   }

   private RandomSequence createSequence(ResourceLocation $$0, int $$1, boolean $$2, boolean $$3) {
      long $$4 = ($$2 ? this.worldSeed : 0L) ^ (long)$$1;
      return new RandomSequence($$4, $$3 ? Optional.of($$0) : Optional.empty());
   }

   public void forAllSequences(BiConsumer<ResourceLocation, RandomSequence> $$0) {
      this.sequences.forEach($$0);
   }

   public void setSeedDefaults(int $$0, boolean $$1, boolean $$2) {
      this.salt = $$0;
      this.includeWorldSeed = $$1;
      this.includeSequenceId = $$2;
   }

   public int clear() {
      int $$0 = this.sequences.size();
      this.sequences.clear();
      return $$0;
   }

   public void reset(ResourceLocation $$0) {
      this.sequences.put($$0, this.createSequence($$0));
   }

   public void reset(ResourceLocation $$0, int $$1, boolean $$2, boolean $$3) {
      this.sequences.put($$0, this.createSequence($$0, $$1, $$2, $$3));
   }

   class DirtyMarkingRandomSource implements RandomSource {
      private final RandomSource random;

      DirtyMarkingRandomSource(final RandomSource param2) {
         this.random = $$0;
      }

      @Override
      public RandomSource fork() {
         RandomSequences.this.setDirty();
         return this.random.fork();
      }

      @Override
      public PositionalRandomFactory forkPositional() {
         RandomSequences.this.setDirty();
         return this.random.forkPositional();
      }

      @Override
      public void setSeed(long $$0) {
         RandomSequences.this.setDirty();
         this.random.setSeed($$0);
      }

      @Override
      public int nextInt() {
         RandomSequences.this.setDirty();
         return this.random.nextInt();
      }

      @Override
      public int nextInt(int $$0) {
         RandomSequences.this.setDirty();
         return this.random.nextInt($$0);
      }

      @Override
      public long nextLong() {
         RandomSequences.this.setDirty();
         return this.random.nextLong();
      }

      @Override
      public boolean nextBoolean() {
         RandomSequences.this.setDirty();
         return this.random.nextBoolean();
      }

      @Override
      public float nextFloat() {
         RandomSequences.this.setDirty();
         return this.random.nextFloat();
      }

      @Override
      public double nextDouble() {
         RandomSequences.this.setDirty();
         return this.random.nextDouble();
      }

      @Override
      public double nextGaussian() {
         RandomSequences.this.setDirty();
         return this.random.nextGaussian();
      }

      public boolean equals(Object $$0) {
         if (this == $$0) {
            return true;
         } else {
            return $$0 instanceof RandomSequences.DirtyMarkingRandomSource $$1 ? this.random.equals($$1.random) : false;
         }
      }
   }
}
