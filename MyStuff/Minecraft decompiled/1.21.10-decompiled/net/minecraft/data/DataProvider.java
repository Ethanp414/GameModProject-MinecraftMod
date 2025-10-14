package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public interface DataProvider {
   ToIntFunction<String> FIXED_ORDER_FIELDS = Util.make(new Object2IntOpenHashMap(), $$0 -> {
      $$0.put("type", 0);
      $$0.put("parent", 1);
      $$0.defaultReturnValue(2);
   });
   Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing($$0 -> $$0);
   Logger LOGGER = LogUtils.getLogger();

   CompletableFuture<?> run(CachedOutput var1);

   String getName();

   static <T> CompletableFuture<?> saveAll(CachedOutput $$0, Codec<T> $$1, PackOutput.PathProvider $$2, Map<ResourceLocation, T> $$3) {
      return saveAll($$0, $$1, $$2::json, $$3);
   }

   static <T, E> CompletableFuture<?> saveAll(CachedOutput $$0, Codec<E> $$1, Function<T, Path> $$2, Map<T, E> $$3) {
      return saveAll($$0, $$1x -> $$1.encodeStart(JsonOps.INSTANCE, (E)$$1x).getOrThrow(), $$2, $$3);
   }

   static <T, E> CompletableFuture<?> saveAll(CachedOutput $$0, Function<E, JsonElement> $$1, Function<T, Path> $$2, Map<T, E> $$3) {
      return CompletableFuture.allOf((CompletableFuture[])$$3.entrySet().stream().map($$3x -> {
         Path $$4 = (Path)$$2.apply($$3x.getKey());
         JsonElement $$5 = (JsonElement)$$1.apply($$3x.getValue());
         return saveStable($$0, $$5, $$4);
      }).toArray($$0x -> new CompletableFuture[$$0x]));
   }

   static <T> CompletableFuture<?> saveStable(CachedOutput $$0, HolderLookup.Provider $$1, Codec<T> $$2, T $$3, Path $$4) {
      RegistryOps<JsonElement> $$5 = $$1.createSerializationContext(JsonOps.INSTANCE);
      return saveStable($$0, $$5, $$2, $$3, $$4);
   }

   static <T> CompletableFuture<?> saveStable(CachedOutput $$0, Codec<T> $$1, T $$2, Path $$3) {
      return saveStable($$0, JsonOps.INSTANCE, $$1, $$2, $$3);
   }

   private static <T> CompletableFuture<?> saveStable(CachedOutput $$0, DynamicOps<JsonElement> $$1, Codec<T> $$2, T $$3, Path $$4) {
      JsonElement $$5 = $$2.encodeStart($$1, $$3).getOrThrow();
      return saveStable($$0, $$5, $$4);
   }

   static CompletableFuture<?> saveStable(CachedOutput $$0, JsonElement $$1, Path $$2) {
      return CompletableFuture.runAsync(() -> {
         try {
            ByteArrayOutputStream $$3 = new ByteArrayOutputStream();
            HashingOutputStream $$4 = new HashingOutputStream(Hashing.sha1(), $$3);
            JsonWriter $$5 = new JsonWriter(new OutputStreamWriter($$4, StandardCharsets.UTF_8));

            try {
               $$5.setSerializeNulls(false);
               $$5.setIndent("  ");
               GsonHelper.writeValue($$5, $$1, KEY_COMPARATOR);
            } catch (Throwable var9) {
               try {
                  $$5.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            $$5.close();
            $$0.writeIfNeeded($$2, $$3.toByteArray(), $$4.hash());
         } catch (IOException var10) {
            LOGGER.error("Failed to save file to {}", $$2, var10);
         }
      }, Util.backgroundExecutor().forName("saveStable"));
   }

   @FunctionalInterface
   public interface Factory<T extends DataProvider> {
      T create(PackOutput var1);
   }
}
