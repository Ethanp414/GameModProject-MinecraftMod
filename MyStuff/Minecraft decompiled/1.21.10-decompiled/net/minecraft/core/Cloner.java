package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;

public class Cloner<T> {
   private final Codec<T> directCodec;

   Cloner(Codec<T> $$0) {
      this.directCodec = $$0;
   }

   public T clone(T $$0, HolderLookup.Provider $$1, HolderLookup.Provider $$2) {
      DynamicOps<Object> $$3 = $$1.createSerializationContext(JavaOps.INSTANCE);
      DynamicOps<Object> $$4 = $$2.createSerializationContext(JavaOps.INSTANCE);
      Object $$5 = this.directCodec.encodeStart($$3, $$0).getOrThrow($$0x -> new IllegalStateException("Failed to encode: " + $$0x));
      return this.directCodec.parse($$4, $$5).getOrThrow($$0x -> new IllegalStateException("Failed to decode: " + $$0x));
   }

   public static class Factory {
      private final Map<ResourceKey<? extends Registry<?>>, Cloner<?>> codecs = new HashMap();

      public <T> Cloner.Factory addCodec(ResourceKey<? extends Registry<? extends T>> $$0, Codec<T> $$1) {
         this.codecs.put($$0, new Cloner<>($$1));
         return this;
      }

      @Nullable
      public <T> Cloner<T> cloner(ResourceKey<? extends Registry<? extends T>> $$0) {
         return (Cloner<T>)this.codecs.get($$0);
      }
   }
}
