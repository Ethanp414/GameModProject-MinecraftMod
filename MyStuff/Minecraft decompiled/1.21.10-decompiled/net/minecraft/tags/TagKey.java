package net.minecraft.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record TagKey<T>(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
   private static final Interner<TagKey<?>> VALUES = Interners.newWeakInterner();

   @Deprecated
   public TagKey(ResourceKey<? extends Registry<T>> param1, ResourceLocation param2) {
      this.registry = $$0;
      this.location = $$1;
   }

   public static <T> Codec<TagKey<T>> codec(ResourceKey<? extends Registry<T>> $$0) {
      return ResourceLocation.CODEC.xmap($$1 -> create($$0, $$1), TagKey::location);
   }

   public static <T> Codec<TagKey<T>> hashedCodec(ResourceKey<? extends Registry<T>> $$0) {
      return Codec.STRING
         .comapFlatMap(
            $$1 -> $$1.startsWith("#") ? ResourceLocation.read($$1.substring(1)).map($$1x -> create($$0, $$1x)) : DataResult.error(() -> "Not a tag id"),
            $$0x -> "#" + $$0x.location
         );
   }

   public static <T> StreamCodec<ByteBuf, TagKey<T>> streamCodec(ResourceKey<? extends Registry<T>> $$0) {
      return ResourceLocation.STREAM_CODEC.map($$1 -> create($$0, $$1), TagKey::location);
   }

   public static <T> TagKey<T> create(ResourceKey<? extends Registry<T>> $$0, ResourceLocation $$1) {
      return (TagKey<T>)VALUES.intern(new TagKey<T>($$0, $$1));
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> $$0) {
      return this.registry == $$0;
   }

   public <E> Optional<TagKey<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
      return this.isFor($$0) ? Optional.of(this) : Optional.empty();
   }

   public String toString() {
      return "TagKey[" + this.registry.location() + " / " + this.location + "]";
   }
}
