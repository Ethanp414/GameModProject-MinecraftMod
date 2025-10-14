package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Item;

public class ItemListReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public ItemListReport(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
      this.output = $$0;
      this.registries = $$1;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput $$0) {
      Path $$1 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("items.json");
      return this.registries
         .thenCompose(
            $$2 -> {
               JsonObject $$3 = new JsonObject();
               RegistryOps<JsonElement> $$4 = $$2.createSerializationContext(JsonOps.INSTANCE);
               $$2.lookupOrThrow(Registries.ITEM)
                  .listElements()
                  .forEach(
                     $$2x -> {
                        JsonObject $$3xx = new JsonObject();
                        $$3xx.add(
                           "components",
                           DataComponentMap.CODEC
                              .encodeStart($$4, ((Item)$$2x.value()).components())
                              .getOrThrow($$0xxx -> new IllegalStateException("Failed to encode components: " + $$0xxx))
                        );
                        $$3.add($$2x.getRegisteredName(), $$3xx);
                     }
                  );
               return DataProvider.saveStable($$0, $$3, $$1);
            }
         );
   }

   @Override
   public final String getName() {
      return "Item List";
   }
}
