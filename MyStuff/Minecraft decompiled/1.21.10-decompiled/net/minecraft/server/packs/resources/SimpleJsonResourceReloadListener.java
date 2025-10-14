package net.minecraft.server.packs.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DynamicOps<JsonElement> ops;
   private final Codec<T> codec;
   private final FileToIdConverter lister;

   protected SimpleJsonResourceReloadListener(HolderLookup.Provider $$0, Codec<T> $$1, ResourceKey<? extends Registry<T>> $$2) {
      this($$0.createSerializationContext(JsonOps.INSTANCE), $$1, FileToIdConverter.registry($$2));
   }

   protected SimpleJsonResourceReloadListener(Codec<T> $$0, FileToIdConverter $$1) {
      this(JsonOps.INSTANCE, $$0, $$1);
   }

   private SimpleJsonResourceReloadListener(DynamicOps<JsonElement> $$0, Codec<T> $$1, FileToIdConverter $$2) {
      this.ops = $$0;
      this.codec = $$1;
      this.lister = $$2;
   }

   protected Map<ResourceLocation, T> prepare(ResourceManager $$0, ProfilerFiller $$1) {
      Map<ResourceLocation, T> $$2 = new HashMap();
      scanDirectory($$0, this.lister, this.ops, this.codec, $$2);
      return $$2;
   }

   public static <T> void scanDirectory(
      ResourceManager $$0, ResourceKey<? extends Registry<T>> $$1, DynamicOps<JsonElement> $$2, Codec<T> $$3, Map<ResourceLocation, T> $$4
   ) {
      scanDirectory($$0, FileToIdConverter.registry($$1), $$2, $$3, $$4);
   }

   public static <T> void scanDirectory(ResourceManager $$0, FileToIdConverter $$1, DynamicOps<JsonElement> $$2, Codec<T> $$3, Map<ResourceLocation, T> $$4) {
      for(Entry<ResourceLocation, Resource> $$5 : $$1.listMatchingResources($$0).entrySet()) {
         ResourceLocation $$6 = (ResourceLocation)$$5.getKey();
         ResourceLocation $$7 = $$1.fileToId($$6);

         try {
            Reader $$8 = ((Resource)$$5.getValue()).openAsReader();

            try {
               $$3.parse($$2, StrictJsonParser.parse($$8)).ifSuccess($$2x -> {
                  if ($$4.putIfAbsent($$7, $$2x) != null) {
                     throw new IllegalStateException("Duplicate data file ignored with ID " + $$7);
                  }
               }).ifError($$2x -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", $$7, $$6, $$2x));
            } catch (Throwable var13) {
               if ($$8 != null) {
                  try {
                     $$8.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if ($$8 != null) {
               $$8.close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var14) {
            LOGGER.error("Couldn't parse data file '{}' from '{}'", $$7, $$6, var14);
         }
      }
   }
}
