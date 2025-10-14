package net.minecraft.data.loot;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput.PathProvider pathProvider;
   private final Set<ResourceKey<LootTable>> requiredTables;
   private final List<LootTableProvider.SubProviderEntry> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public LootTableProvider(
      PackOutput $$0, Set<ResourceKey<LootTable>> $$1, List<LootTableProvider.SubProviderEntry> $$2, CompletableFuture<HolderLookup.Provider> $$3
   ) {
      this.pathProvider = $$0.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
      this.subProviders = $$2;
      this.requiredTables = $$1;
      this.registries = $$3;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput $$0) {
      return this.registries.thenCompose($$1 -> this.run($$0, $$1));
   }

   private CompletableFuture<?> run(CachedOutput $$0, HolderLookup.Provider $$1) {
      WritableRegistry<LootTable> $$2 = new MappedRegistry<>(Registries.LOOT_TABLE, Lifecycle.experimental());
      Map<RandomSupport.Seed128bit, ResourceLocation> $$3 = new Object2ObjectOpenHashMap<>();
      this.subProviders.forEach($$3x -> ((LootTableSubProvider)$$3x.provider().apply($$1)).generate(($$3xx, $$4x) -> {
            ResourceLocation $$5xx = sequenceIdForLootTable($$3xx);
            ResourceLocation $$6xx = (ResourceLocation)$$3.put(RandomSequence.seedForKey($$5xx), $$5xx);
            if ($$6xx != null) {
               Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + $$6xx + " and " + $$3xx.location());
            }

            $$4x.setRandomSequence($$5xx);
            LootTable $$7 = $$4x.setParamSet($$3x.paramSet).build();
            $$2.register($$3xx, $$7, RegistrationInfo.BUILT_IN);
         }));
      $$2.freeze();
      ProblemReporter.Collector $$4 = new ProblemReporter.Collector();
      HolderGetter.Provider $$5 = new RegistryAccess.ImmutableRegistryAccess(List.of($$2)).freeze();
      ValidationContext $$6 = new ValidationContext($$4, LootContextParamSets.ALL_PARAMS, $$5);

      for(ResourceKey<LootTable> $$8 : Sets.difference(this.requiredTables, $$2.registryKeySet())) {
         $$4.report(new LootTableProvider.MissingTableProblem($$8));
      }

      $$2.listElements()
         .forEach(
            $$1x -> ((LootTable)$$1x.value())
                  .validate(
                     $$6.setContextKeySet(((LootTable)$$1x.value()).getParamSet())
                        .enterElement(new ProblemReporter.RootElementPathElement($$1x.key()), $$1x.key())
                  )
         );
      if (!$$4.isEmpty()) {
         $$4.forEach(($$0x, $$1x) -> LOGGER.warn("Found validation problem in {}: {}", $$0x, $$1x.description()));
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf((CompletableFuture[])$$2.entrySet().stream().map($$2x -> {
            ResourceKey<LootTable> $$3xx = (ResourceKey)$$2x.getKey();
            LootTable $$4xx = (LootTable)$$2x.getValue();
            Path $$5xx = this.pathProvider.json($$3xx.location());
            return DataProvider.saveStable($$0, $$1, LootTable.DIRECT_CODEC, $$4xx, $$5xx);
         }).toArray($$0x -> new CompletableFuture[$$0x]));
      }
   }

   private static ResourceLocation sequenceIdForLootTable(ResourceKey<LootTable> $$0) {
      return $$0.location();
   }

   @Override
   public final String getName() {
      return "Loot Tables";
   }

   public static record MissingTableProblem(ResourceKey<LootTable> id) implements ProblemReporter.Problem {
      @Override
      public String description() {
         return "Missing built-in table: " + this.id.location();
      }
   }

   public static record SubProviderEntry(Function<HolderLookup.Provider, LootTableSubProvider> provider, ContextKeySet paramSet) {
      final ContextKeySet paramSet;
   }
}
