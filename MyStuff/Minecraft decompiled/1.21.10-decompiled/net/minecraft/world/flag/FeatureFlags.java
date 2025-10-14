package net.minecraft.world.flag;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public class FeatureFlags {
   public static final FeatureFlag VANILLA;
   public static final FeatureFlag TRADE_REBALANCE;
   public static final FeatureFlag REDSTONE_EXPERIMENTS;
   public static final FeatureFlag MINECART_IMPROVEMENTS;
   public static final FeatureFlagRegistry REGISTRY;
   public static final Codec<FeatureFlagSet> CODEC = REGISTRY.codec();
   public static final FeatureFlagSet VANILLA_SET = FeatureFlagSet.of(VANILLA);
   public static final FeatureFlagSet DEFAULT_FLAGS = VANILLA_SET;

   public static String printMissingFlags(FeatureFlagSet $$0, FeatureFlagSet $$1) {
      return printMissingFlags(REGISTRY, $$0, $$1);
   }

   public static String printMissingFlags(FeatureFlagRegistry $$0, FeatureFlagSet $$1, FeatureFlagSet $$2) {
      Set<ResourceLocation> $$3 = $$0.toNames($$2);
      Set<ResourceLocation> $$4 = $$0.toNames($$1);
      return (String)$$3.stream().filter($$1x -> !$$4.contains($$1x)).map(ResourceLocation::toString).collect(Collectors.joining(", "));
   }

   public static boolean isExperimental(FeatureFlagSet $$0) {
      return !$$0.isSubsetOf(VANILLA_SET);
   }

   static {
      FeatureFlagRegistry.Builder $$0 = new FeatureFlagRegistry.Builder("main");
      VANILLA = $$0.createVanilla("vanilla");
      TRADE_REBALANCE = $$0.createVanilla("trade_rebalance");
      REDSTONE_EXPERIMENTS = $$0.createVanilla("redstone_experiments");
      MINECART_IMPROVEMENTS = $$0.createVanilla("minecart_improvements");
      REGISTRY = $$0.build();
   }
}
