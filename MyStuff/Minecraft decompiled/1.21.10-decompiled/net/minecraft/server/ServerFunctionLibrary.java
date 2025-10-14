package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceKey<Registry<CommandFunction<CommandSourceStack>>> TYPE_KEY = ResourceKey.createRegistryKey(
      ResourceLocation.withDefaultNamespace("function")
   );
   private static final FileToIdConverter LISTER = new FileToIdConverter(Registries.elementsDirPath(TYPE_KEY), ".mcfunction");
   private volatile Map<ResourceLocation, CommandFunction<CommandSourceStack>> functions = ImmutableMap.of();
   private final TagLoader<CommandFunction<CommandSourceStack>> tagsLoader = new TagLoader<>(
      ($$0x, $$1x) -> this.getFunction($$0x), Registries.tagsDirPath(TYPE_KEY)
   );
   private volatile Map<ResourceLocation, List<CommandFunction<CommandSourceStack>>> tags = Map.of();
   private final int functionCompilationLevel;
   private final CommandDispatcher<CommandSourceStack> dispatcher;

   public Optional<CommandFunction<CommandSourceStack>> getFunction(ResourceLocation $$0) {
      return Optional.ofNullable((CommandFunction)this.functions.get($$0));
   }

   public Map<ResourceLocation, CommandFunction<CommandSourceStack>> getFunctions() {
      return this.functions;
   }

   public List<CommandFunction<CommandSourceStack>> getTag(ResourceLocation $$0) {
      return (List<CommandFunction<CommandSourceStack>>)this.tags.getOrDefault($$0, List.of());
   }

   public Iterable<ResourceLocation> getAvailableTags() {
      return this.tags.keySet();
   }

   public ServerFunctionLibrary(int $$0, CommandDispatcher<CommandSourceStack> $$1) {
      this.functionCompilationLevel = $$0;
      this.dispatcher = $$1;
   }

   @Override
   public CompletableFuture<Void> reload(PreparableReloadListener.SharedState $$0, Executor $$1, PreparableReloadListener.PreparationBarrier $$2, Executor $$3) {
      ResourceManager $$4 = $$0.resourceManager();
      CompletableFuture<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> $$5 = CompletableFuture.supplyAsync(() -> this.tagsLoader.load($$4), $$1);
      CompletableFuture<Map<ResourceLocation, CompletableFuture<CommandFunction<CommandSourceStack>>>> $$6 = CompletableFuture.supplyAsync(
            () -> LISTER.listMatchingResources($$4), $$1
         )
         .thenCompose(
            $$1x -> {
               Map<ResourceLocation, CompletableFuture<CommandFunction<CommandSourceStack>>> $$2xx = Maps.newHashMap();
               CommandSourceStack $$3xx = new CommandSourceStack(
                  CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null, this.functionCompilationLevel, "", CommonComponents.EMPTY, null, null
               );
      
               for(Entry<ResourceLocation, Resource> $$4xx : $$1x.entrySet()) {
                  ResourceLocation $$5xx = (ResourceLocation)$$4xx.getKey();
                  ResourceLocation $$6xx = LISTER.fileToId($$5xx);
                  $$2xx.put($$6xx, CompletableFuture.supplyAsync(() -> {
                     List<String> $$3xxx = readLines((Resource)$$4x.getValue());
                     return CommandFunction.fromLines($$6x, this.dispatcher, $$3x, $$3xxx);
                  }, $$1));
               }
      
               CompletableFuture<?>[] $$7 = (CompletableFuture[])$$2xx.values().toArray(new CompletableFuture[0]);
               return CompletableFuture.allOf($$7).handle(($$1xx, $$2xx) -> $$2x);
            }
         );
      return $$5.thenCombine($$6, Pair::of).thenCompose($$2::wait).thenAcceptAsync($$0x -> {
         Map<ResourceLocation, CompletableFuture<CommandFunction<CommandSourceStack>>> $$1xx = (Map)$$0x.getSecond();
         Builder<ResourceLocation, CommandFunction<CommandSourceStack>> $$2xx = ImmutableMap.builder();
         $$1xx.forEach(($$1xx, $$2xx) -> $$2xx.handle(($$2xxx, $$3x) -> {
               if ($$3x != null) {
                  LOGGER.error("Failed to load function {}", $$1xx, $$3x);
               } else {
                  $$2x.put($$1xx, $$2xxx);
               }

               return null;
            }).join());
         this.functions = $$2xx.build();
         this.tags = this.tagsLoader.build((Map<ResourceLocation, List<TagLoader.EntryWithSource>>)$$0x.getFirst());
      }, $$3);
   }

   private static List<String> readLines(Resource $$0) {
      try {
         BufferedReader $$1 = $$0.openAsReader();

         List var2;
         try {
            var2 = $$1.lines().toList();
         } catch (Throwable var5) {
            if ($$1 != null) {
               try {
                  $$1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if ($$1 != null) {
            $$1.close();
         }

         return var2;
      } catch (IOException var6) {
         throw new CompletionException(var6);
      }
   }
}
