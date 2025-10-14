package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;

public final class RegistryFixedCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;

   public static <E> RegistryFixedCodec<E> create(ResourceKey<? extends Registry<E>> $$0) {
      return new RegistryFixedCodec<>($$0);
   }

   private RegistryFixedCodec(ResourceKey<? extends Registry<E>> $$0) {
      this.registryKey = $$0;
   }

   public <T> DataResult<T> encode(Holder<E> $$0, DynamicOps<T> $$1, T $$2) {
      if ($$1 instanceof RegistryOps $$3) {
         Optional<HolderOwner<E>> $$4 = $$3.owner(this.registryKey);
         if ($$4.isPresent()) {
            if (!$$0.canSerializeIn((HolderOwner<E>)$$4.get())) {
               return DataResult.error(() -> "Element " + $$0 + " is not valid in current registry set");
            }

            return $$0.unwrap()
               .map(
                  $$2x -> ResourceLocation.CODEC.encode($$2x.location(), $$1, $$2),
                  $$0x -> DataResult.error(() -> "Elements from registry " + this.registryKey + " can't be serialized to a value")
               );
         }
      }

      return DataResult.error(() -> "Can't access registry " + this.registryKey);
   }

   @Override
   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> $$0, T $$1) {
      if ($$0 instanceof RegistryOps $$2) {
         Optional<HolderGetter<E>> $$3 = $$2.getter(this.registryKey);
         if ($$3.isPresent()) {
            return ResourceLocation.CODEC
               .decode($$0, $$1)
               .flatMap(
                  $$1x -> {
                     ResourceLocation $$2xx = (ResourceLocation)$$1x.getFirst();
                     return ((DataResult)((HolderGetter)$$3.get())
                           .get(ResourceKey.create(this.registryKey, $$2xx))
                           .map(DataResult::success)
                           .orElseGet(() -> DataResult.error(() -> "Failed to get element " + $$2x)))
                        .map($$1xx -> Pair.of($$1xx, $$1x.getSecond()))
                        .setLifecycle(Lifecycle.stable());
                  }
               );
         }
      }

      return DataResult.error(() -> "Can't access registry " + this.registryKey);
   }

   public String toString() {
      return "RegistryFixedCodec[" + this.registryKey + "]";
   }
}
