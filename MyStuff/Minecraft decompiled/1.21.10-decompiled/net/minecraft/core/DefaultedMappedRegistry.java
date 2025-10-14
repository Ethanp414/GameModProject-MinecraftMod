package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class DefaultedMappedRegistry<T> extends MappedRegistry<T> implements DefaultedRegistry<T> {
   private final ResourceLocation defaultKey;
   private Holder.Reference<T> defaultValue;

   public DefaultedMappedRegistry(String $$0, ResourceKey<? extends Registry<T>> $$1, Lifecycle $$2, boolean $$3) {
      super($$1, $$2, $$3);
      this.defaultKey = ResourceLocation.parse($$0);
   }

   @Override
   public Holder.Reference<T> register(ResourceKey<T> $$0, T $$1, RegistrationInfo $$2) {
      Holder.Reference<T> $$3 = super.register($$0, $$1, $$2);
      if (this.defaultKey.equals($$0.location())) {
         this.defaultValue = $$3;
      }

      return $$3;
   }

   @Override
   public int getId(@Nullable T $$0) {
      int $$1 = super.getId($$0);
      return $$1 == -1 ? super.getId(this.defaultValue.value()) : $$1;
   }

   @Nonnull
   @Override
   public ResourceLocation getKey(T $$0) {
      ResourceLocation $$1 = super.getKey($$0);
      return $$1 == null ? this.defaultKey : $$1;
   }

   @Nonnull
   @Override
   public T getValue(@Nullable ResourceLocation $$0) {
      T $$1 = super.getValue($$0);
      return (T)($$1 == null ? this.defaultValue.value() : $$1);
   }

   @Override
   public Optional<T> getOptional(@Nullable ResourceLocation $$0) {
      return Optional.ofNullable(super.getValue($$0));
   }

   @Override
   public Optional<Holder.Reference<T>> getAny() {
      return Optional.ofNullable(this.defaultValue);
   }

   @Nonnull
   @Override
   public T byId(int $$0) {
      T $$1 = super.byId($$0);
      return (T)($$1 == null ? this.defaultValue.value() : $$1);
   }

   @Override
   public Optional<Holder.Reference<T>> getRandom(RandomSource $$0) {
      return super.getRandom($$0).or(() -> Optional.of(this.defaultValue));
   }

   @Override
   public ResourceLocation getDefaultKey() {
      return this.defaultKey;
   }
}
