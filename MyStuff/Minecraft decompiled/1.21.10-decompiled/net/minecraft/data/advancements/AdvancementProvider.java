package net.minecraft.data.advancements;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class AdvancementProvider implements DataProvider {
   private final PackOutput.PathProvider pathProvider;
   private final List<AdvancementSubProvider> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public AdvancementProvider(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1, List<AdvancementSubProvider> $$2) {
      this.pathProvider = $$0.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
      this.subProviders = $$2;
      this.registries = $$1;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput $$0) {
      return this.registries.thenCompose($$1 -> {
         Set<ResourceLocation> $$2 = new HashSet();
         List<CompletableFuture<?>> $$3 = new ArrayList();
         Consumer<AdvancementHolder> $$4 = $$4x -> {
            if (!$$2.add($$4x.id())) {
               throw new IllegalStateException("Duplicate advancement " + $$4x.id());
            } else {
               Path $$5xx = this.pathProvider.json($$4x.id());
               $$3.add(DataProvider.saveStable($$0, $$1, Advancement.CODEC, $$4x.value(), $$5xx));
            }
         };

         for(AdvancementSubProvider $$5 : this.subProviders) {
            $$5.generate($$1, $$4);
         }

         return CompletableFuture.allOf((CompletableFuture[])$$3.toArray($$0xx -> new CompletableFuture[$$0xx]));
      });
   }

   @Override
   public final String getName() {
      return "Advancements";
   }
}
