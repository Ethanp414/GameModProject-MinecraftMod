package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.level.WorldDataConfiguration;
import org.slf4j.Logger;

public class WorldLoader {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static <D, R> CompletableFuture<R> load(
      WorldLoader.InitConfig $$0, WorldLoader.WorldDataSupplier<D> $$1, WorldLoader.ResultFactory<D, R> $$2, Executor $$3, Executor $$4
   ) {
      try {
         Pair<WorldDataConfiguration, CloseableResourceManager> $$5 = $$0.packConfig.createResourceManager();
         CloseableResourceManager $$6 = $$5.getSecond();
         LayeredRegistryAccess<RegistryLayer> $$7 = RegistryLayer.createRegistryAccess();
         List<Registry.PendingTags<?>> $$8 = TagLoader.loadTagsForExistingRegistries($$6, $$7.getLayer(RegistryLayer.STATIC));
         RegistryAccess.Frozen $$9 = $$7.getAccessForLoading(RegistryLayer.WORLDGEN);
         List<HolderLookup.RegistryLookup<?>> $$10 = TagLoader.buildUpdatedLookups($$9, $$8);
         RegistryAccess.Frozen $$11 = RegistryDataLoader.load($$6, $$10, RegistryDataLoader.WORLDGEN_REGISTRIES);
         List<HolderLookup.RegistryLookup<?>> $$12 = Stream.concat($$10.stream(), $$11.listRegistries()).toList();
         RegistryAccess.Frozen $$13 = RegistryDataLoader.load($$6, $$12, RegistryDataLoader.DIMENSION_REGISTRIES);
         WorldDataConfiguration $$14 = (WorldDataConfiguration)$$5.getFirst();
         HolderLookup.Provider $$15 = HolderLookup.Provider.create($$12.stream());
         WorldLoader.DataLoadOutput<D> $$16 = $$1.get(new WorldLoader.DataLoadContext($$6, $$14, $$15, $$13));
         LayeredRegistryAccess<RegistryLayer> $$17 = $$7.replaceFrom(RegistryLayer.WORLDGEN, $$11, $$16.finalDimensions);
         return ReloadableServerResources.loadResources(
               $$6, $$17, $$8, $$14.enabledFeatures(), $$0.commandSelection(), $$0.functionCompilationLevel(), $$3, $$4
            )
            .whenComplete(($$1x, $$2x) -> {
               if ($$2x != null) {
                  $$6.close();
               }
            })
            .thenApplyAsync($$4x -> {
               $$4x.updateStaticRegistryTags();
               return $$2.create($$6, $$4x, $$17, $$16.cookie);
            }, $$4);
      } catch (Exception var18) {
         return CompletableFuture.failedFuture(var18);
      }
   }

   public static record DataLoadContext(
      ResourceManager resources, WorldDataConfiguration dataConfiguration, HolderLookup.Provider datapackWorldgen, RegistryAccess.Frozen datapackDimensions
   ) {
   }

   public static record DataLoadOutput<D>(D cookie, RegistryAccess.Frozen finalDimensions) {
      final D cookie;
      final RegistryAccess.Frozen finalDimensions;
   }

   public static record InitConfig(WorldLoader.PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
      final WorldLoader.PackConfig packConfig;
   }

   public static record PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
      public Pair<WorldDataConfiguration, CloseableResourceManager> createResourceManager() {
         WorldDataConfiguration $$0 = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig, this.initMode, this.safeMode);
         List<PackResources> $$1 = this.packRepository.openAllSelected();
         CloseableResourceManager $$2 = new MultiPackResourceManager(PackType.SERVER_DATA, $$1);
         return Pair.of($$0, $$2);
      }
   }

   @FunctionalInterface
   public interface ResultFactory<D, R> {
      R create(CloseableResourceManager var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, D var4);
   }

   @FunctionalInterface
   public interface WorldDataSupplier<D> {
      WorldLoader.DataLoadOutput<D> get(WorldLoader.DataLoadContext var1);
   }
}
