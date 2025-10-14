package net.minecraft.commands;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext extends HolderLookup.Provider {
   static CommandBuildContext simple(final HolderLookup.Provider $$0, final FeatureFlagSet $$1) {
      return new CommandBuildContext() {
         @Override
         public Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
            return $$0.listRegistryKeys();
         }

         @Override
         public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0x) {
            return $$0.lookup($$0).map($$1xx -> $$1xx.filterFeatures($$1));
         }

         @Override
         public FeatureFlagSet enabledFeatures() {
            return $$1;
         }
      };
   }

   FeatureFlagSet enabledFeatures();
}
