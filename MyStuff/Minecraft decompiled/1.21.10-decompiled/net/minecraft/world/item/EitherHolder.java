package net.minecraft.world.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record EitherHolder<T>(Either<Holder<T>, ResourceKey<T>> contents) {
   public EitherHolder(Holder<T> $$0) {
      this(Either.left($$0));
   }

   public EitherHolder(ResourceKey<T> $$0) {
      this(Either.right($$0));
   }

   public static <T> Codec<EitherHolder<T>> codec(ResourceKey<Registry<T>> $$0, Codec<Holder<T>> $$1) {
      return Codec.either(
            $$1, ResourceKey.codec($$0).comapFlatMap($$0x -> DataResult.error(() -> "Cannot parse as key without registry"), Function.identity())
         )
         .xmap(EitherHolder::new, EitherHolder::contents);
   }

   public static <T> StreamCodec<RegistryFriendlyByteBuf, EitherHolder<T>> streamCodec(
      ResourceKey<Registry<T>> $$0, StreamCodec<RegistryFriendlyByteBuf, Holder<T>> $$1
   ) {
      return StreamCodec.composite(ByteBufCodecs.either($$1, ResourceKey.streamCodec($$0)), EitherHolder::contents, EitherHolder::new);
   }

   public Optional<T> unwrap(Registry<T> $$0) {
      return this.contents.map($$0x -> Optional.of($$0x.value()), $$0::getOptional);
   }

   public Optional<Holder<T>> unwrap(HolderLookup.Provider $$0) {
      return this.contents.map(Optional::of, $$1 -> $$0.get($$1).map($$0xx -> $$0xx));
   }

   public Optional<ResourceKey<T>> key() {
      return this.contents.map(Holder::unwrapKey, Optional::of);
   }
}
