package net.minecraft.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.Tag;

public class EncoderCache {
   final LoadingCache<EncoderCache.Key<?, ?>, DataResult<?>> cache;

   public EncoderCache(int $$0) {
      this.cache = CacheBuilder.newBuilder()
         .maximumSize((long)$$0)
         .concurrencyLevel(1)
         .softValues()
         .build(new CacheLoader<EncoderCache.Key<?, ?>, DataResult<?>>(this) {
            public DataResult<?> load(EncoderCache.Key<?, ?> $$0) {
               return $$0.resolve();
            }
         });
   }

   public <A> Codec<A> wrap(final Codec<A> $$0) {
      return new Codec<A>() {
         @Override
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> $$0x, T $$1) {
            return $$0.decode($$0, $$1);
         }

         @Override
         public <T> DataResult<T> encode(A $$0x, DynamicOps<T> $$1, T $$2) {
            return EncoderCache.this.cache
               .getUnchecked(new EncoderCache.Key<A, T>($$0, $$0, $$1))
               .map($$0xx -> $$0xx instanceof Tag $$1xx ? $$1xx.copy() : $$0xx);
         }
      };
   }

   static record Key<A, T>(Codec<A> codec, A value, DynamicOps<T> ops) {
      public DataResult<T> resolve() {
         return this.codec.encodeStart(this.ops, this.value);
      }

      public boolean equals(Object $$0) {
         if (this == $$0) {
            return true;
         } else if (!($$0 instanceof EncoderCache.Key)) {
            return false;
         } else {
            EncoderCache.Key<?, ?> $$1 = (EncoderCache.Key)$$0;
            return this.codec == $$1.codec && this.value.equals($$1.value) && this.ops.equals($$1.ops);
         }
      }

      public int hashCode() {
         int $$0 = System.identityHashCode(this.codec);
         $$0 = 31 * $$0 + this.value.hashCode();
         return 31 * $$0 + this.ops.hashCode();
      }
   }
}
