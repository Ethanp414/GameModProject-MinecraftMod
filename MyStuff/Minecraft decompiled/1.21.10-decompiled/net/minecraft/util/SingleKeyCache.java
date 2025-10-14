package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;

public class SingleKeyCache<K, V> {
   private final Function<K, V> computeValue;
   @Nullable
   private K cacheKey = (K)null;
   @Nullable
   private V cachedValue;

   public SingleKeyCache(Function<K, V> $$0) {
      this.computeValue = $$0;
   }

   public V getValue(K $$0) {
      if (this.cachedValue == null || !Objects.equals(this.cacheKey, $$0)) {
         this.cachedValue = (V)this.computeValue.apply($$0);
         this.cacheKey = $$0;
      }

      return this.cachedValue;
   }
}
