package net.minecraft.data.registries;

import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Cloner;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RegistryPatchGenerator {
   public static CompletableFuture<RegistrySetBuilder.PatchedRegistries> createLookup(CompletableFuture<HolderLookup.Provider> $$0, RegistrySetBuilder $$1) {
      return $$0.thenApply(
         $$1x -> {
            RegistryAccess.Frozen $$2 = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            Cloner.Factory $$3 = new Cloner.Factory();
            RegistryDataLoader.WORLDGEN_REGISTRIES.forEach($$1xx -> $$1xx.runWithArguments($$3::addCodec));
            RegistrySetBuilder.PatchedRegistries $$4 = $$1.buildPatch($$2, $$1x, $$3);
            HolderLookup.Provider $$5 = $$4.full();
            Optional<? extends HolderLookup.RegistryLookup<Biome>> $$6 = $$5.lookup(Registries.BIOME);
            Optional<? extends HolderLookup.RegistryLookup<PlacedFeature>> $$7 = $$5.lookup(Registries.PLACED_FEATURE);
            if ($$6.isPresent() || $$7.isPresent()) {
               VanillaRegistries.validateThatAllBiomeFeaturesHaveBiomeFilter(
                  DataFixUtils.orElseGet($$7, () -> $$1x.lookupOrThrow(Registries.PLACED_FEATURE)),
                  DataFixUtils.orElseGet($$6, () -> $$1x.lookupOrThrow(Registries.BIOME))
               );
            }
   
            return $$4;
         }
      );
   }
}
