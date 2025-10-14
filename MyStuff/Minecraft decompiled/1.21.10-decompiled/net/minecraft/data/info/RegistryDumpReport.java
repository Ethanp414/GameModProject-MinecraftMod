package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public class RegistryDumpReport implements DataProvider {
   private final PackOutput output;

   public RegistryDumpReport(PackOutput $$0) {
      this.output = $$0;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput $$0) {
      JsonObject $$1 = new JsonObject();
      BuiltInRegistries.REGISTRY.listElements().forEach($$1x -> $$1.add($$1x.key().location().toString(), dumpRegistry((Registry)$$1x.value())));
      Path $$2 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("registries.json");
      return DataProvider.saveStable($$0, $$1, $$2);
   }

   private static <T> JsonElement dumpRegistry(Registry<T> $$0) {
      JsonObject $$1 = new JsonObject();
      if ($$0 instanceof DefaultedRegistry) {
         ResourceLocation $$2 = ((DefaultedRegistry)$$0).getDefaultKey();
         $$1.addProperty("default", $$2.toString());
      }

      int $$3 = BuiltInRegistries.REGISTRY.getId($$0);
      $$1.addProperty("protocol_id", $$3);
      JsonObject $$4 = new JsonObject();
      $$0.listElements().forEach($$2 -> {
         T $$3xx = (T)$$2.value();
         int $$4xx = $$0.getId($$3xx);
         JsonObject $$5 = new JsonObject();
         $$5.addProperty("protocol_id", $$4xx);
         $$4.add($$2.key().location().toString(), $$5);
      });
      $$1.add("entries", $$4);
      return $$1;
   }

   @Override
   public final String getName() {
      return "Registry Dump";
   }
}
