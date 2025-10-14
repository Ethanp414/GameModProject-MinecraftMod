package net.minecraft.data.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

public class RegistriesDatapackGenerator implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public RegistriesDatapackGenerator(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
      this.registries = $$1;
      this.output = $$0;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput $$0) {
      return this.registries
         .thenCompose(
            $$1 -> {
               DynamicOps<JsonElement> $$2 = $$1.createSerializationContext(JsonOps.INSTANCE);
               return CompletableFuture.allOf(
                  (CompletableFuture[])RegistryDataLoader.WORLDGEN_REGISTRIES
                     .stream()
                     .flatMap($$3 -> this.dumpRegistryCap($$0, $$1, $$2, $$3).stream())
                     .toArray($$0xx -> new CompletableFuture[$$0xx])
               );
            }
         );
   }

   private <T> Optional<CompletableFuture<?>> dumpRegistryCap(
      CachedOutput $$0, HolderLookup.Provider $$1, DynamicOps<JsonElement> $$2, RegistryDataLoader.RegistryData<T> $$3
   ) {
      ResourceKey<? extends Registry<T>> $$4 = $$3.key();
      return $$1.lookup($$4)
         .map(
            $$4x -> {
               PackOutput.PathProvider $$5 = this.output.createRegistryElementsPathProvider($$4);
               return CompletableFuture.allOf(
                  (CompletableFuture[])$$4x.listElements()
                     .map($$4xx -> dumpValue($$5.json($$4xx.key().location()), $$0, $$2, $$3.elementCodec(), (T)$$4xx.value()))
                     .toArray($$0xx -> new CompletableFuture[$$0xx])
               );
            }
         );
   }

   private static <E> CompletableFuture<?> dumpValue(Path $$0, CachedOutput $$1, DynamicOps<JsonElement> $$2, Encoder<E> $$3, E $$4) {
      return $$3.encodeStart($$2, $$4)
         .mapOrElse(
            $$2x -> DataProvider.saveStable($$1, $$2x, $$0),
            $$1x -> CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + $$0 + "': " + $$1x.message()))
         );
   }

   @Override
   public final String getName() {
      return "Registries";
   }
}
