package net.minecraft.resources;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;

public class ResourceKey<T> {
   private static final ConcurrentMap<ResourceKey.InternKey, ResourceKey<?>> VALUES = new MapMaker().weakValues().makeMap();
   private final ResourceLocation registryName;
   private final ResourceLocation location;

   public static <T> Codec<ResourceKey<T>> codec(ResourceKey<? extends Registry<T>> $$0) {
      return ResourceLocation.CODEC.xmap($$1 -> create($$0, $$1), ResourceKey::location);
   }

   public static <T> StreamCodec<ByteBuf, ResourceKey<T>> streamCodec(ResourceKey<? extends Registry<T>> $$0) {
      return ResourceLocation.STREAM_CODEC.map($$1 -> create($$0, $$1), ResourceKey::location);
   }

   public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> $$0, ResourceLocation $$1) {
      return create($$0.location, $$1);
   }

   public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation $$0) {
      return create(Registries.ROOT_REGISTRY_NAME, $$0);
   }

   private static <T> ResourceKey<T> create(ResourceLocation $$0, ResourceLocation $$1) {
      return (ResourceKey<T>)VALUES.computeIfAbsent(new ResourceKey.InternKey($$0, $$1), $$0x -> new ResourceKey($$0x.registry, $$0x.location));
   }

   private ResourceKey(ResourceLocation $$0, ResourceLocation $$1) {
      this.registryName = $$0;
      this.location = $$1;
   }

   public String toString() {
      return "ResourceKey[" + this.registryName + " / " + this.location + "]";
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> $$0) {
      return this.registryName.equals($$0.location());
   }

   public <E> Optional<ResourceKey<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
      return this.isFor($$0) ? Optional.of(this) : Optional.empty();
   }

   public ResourceLocation location() {
      return this.location;
   }

   public ResourceLocation registry() {
      return this.registryName;
   }

   public ResourceKey<Registry<T>> registryKey() {
      return createRegistryKey(this.registryName);
   }

   static record InternKey(ResourceLocation registry, ResourceLocation location) {
      final ResourceLocation registry;
      final ResourceLocation location;
   }
}
