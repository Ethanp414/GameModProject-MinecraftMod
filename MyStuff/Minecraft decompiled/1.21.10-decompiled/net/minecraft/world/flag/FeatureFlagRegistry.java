package net.minecraft.world.flag;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class FeatureFlagRegistry {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final FeatureFlagUniverse universe;
   private final Map<ResourceLocation, FeatureFlag> names;
   private final FeatureFlagSet allFlags;

   FeatureFlagRegistry(FeatureFlagUniverse $$0, FeatureFlagSet $$1, Map<ResourceLocation, FeatureFlag> $$2) {
      this.universe = $$0;
      this.names = $$2;
      this.allFlags = $$1;
   }

   public boolean isSubset(FeatureFlagSet $$0) {
      return $$0.isSubsetOf(this.allFlags);
   }

   public FeatureFlagSet allFlags() {
      return this.allFlags;
   }

   public FeatureFlagSet fromNames(Iterable<ResourceLocation> $$0) {
      return this.fromNames($$0, $$0x -> LOGGER.warn("Unknown feature flag: {}", $$0x));
   }

   public FeatureFlagSet subset(FeatureFlag... $$0) {
      return FeatureFlagSet.create(this.universe, Arrays.asList($$0));
   }

   public FeatureFlagSet fromNames(Iterable<ResourceLocation> $$0, Consumer<ResourceLocation> $$1) {
      Set<FeatureFlag> $$2 = Sets.newIdentityHashSet();

      for(ResourceLocation $$3 : $$0) {
         FeatureFlag $$4 = (FeatureFlag)this.names.get($$3);
         if ($$4 == null) {
            $$1.accept($$3);
         } else {
            $$2.add($$4);
         }
      }

      return FeatureFlagSet.create(this.universe, $$2);
   }

   public Set<ResourceLocation> toNames(FeatureFlagSet $$0) {
      Set<ResourceLocation> $$1 = new HashSet();
      this.names.forEach(($$2, $$3) -> {
         if ($$0.contains($$3)) {
            $$1.add($$2);
         }
      });
      return $$1;
   }

   public Codec<FeatureFlagSet> codec() {
      return ResourceLocation.CODEC.listOf().comapFlatMap($$0 -> {
         Set<ResourceLocation> $$1 = new HashSet();
         FeatureFlagSet $$2 = this.fromNames($$0, $$1::add);
         return !$$1.isEmpty() ? DataResult.error(() -> "Unknown feature ids: " + $$1, $$2) : DataResult.success($$2);
      }, $$0 -> List.copyOf(this.toNames($$0)));
   }

   public static class Builder {
      private final FeatureFlagUniverse universe;
      private int id;
      private final Map<ResourceLocation, FeatureFlag> flags = new LinkedHashMap();

      public Builder(String $$0) {
         this.universe = new FeatureFlagUniverse($$0);
      }

      public FeatureFlag createVanilla(String $$0) {
         return this.create(ResourceLocation.withDefaultNamespace($$0));
      }

      public FeatureFlag create(ResourceLocation $$0) {
         if (this.id >= 64) {
            throw new IllegalStateException("Too many feature flags");
         } else {
            FeatureFlag $$1 = new FeatureFlag(this.universe, this.id++);
            FeatureFlag $$2 = (FeatureFlag)this.flags.put($$0, $$1);
            if ($$2 != null) {
               throw new IllegalStateException("Duplicate feature flag " + $$0);
            } else {
               return $$1;
            }
         }
      }

      public FeatureFlagRegistry build() {
         FeatureFlagSet $$0 = FeatureFlagSet.create(this.universe, this.flags.values());
         return new FeatureFlagRegistry(this.universe, $$0, Map.copyOf(this.flags));
      }
   }
}
