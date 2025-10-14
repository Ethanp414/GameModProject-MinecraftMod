package net.minecraft.data.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public BlockListReport(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
      this.output = $$0;
      this.registries = $$1;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput $$0) {
      Path $$1 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("blocks.json");
      return this.registries
         .thenCompose(
            $$2 -> {
               JsonObject $$3 = new JsonObject();
               RegistryOps<JsonElement> $$4 = $$2.createSerializationContext(JsonOps.INSTANCE);
               $$2.lookupOrThrow(Registries.BLOCK)
                  .listElements()
                  .forEach(
                     $$2x -> {
                        JsonObject $$3xx = new JsonObject();
                        StateDefinition<Block, BlockState> $$4xx = ((Block)$$2x.value()).getStateDefinition();
                        if (!$$4xx.getProperties().isEmpty()) {
                           JsonObject $$5 = new JsonObject();
            
                           for(Property<?> $$6 : $$4xx.getProperties()) {
                              JsonArray $$7 = new JsonArray();
            
                              for(Comparable<?> $$8 : $$6.getPossibleValues()) {
                                 $$7.add(Util.getPropertyName($$6, $$8));
                              }
            
                              $$5.add($$6.getName(), $$7);
                           }
            
                           $$3xx.add("properties", $$5);
                        }
            
                        JsonArray $$9 = new JsonArray();
            
                        for(BlockState $$10 : $$4xx.getPossibleStates()) {
                           JsonObject $$11 = new JsonObject();
                           JsonObject $$12 = new JsonObject();
            
                           for(Property<?> $$13 : $$4xx.getProperties()) {
                              $$12.addProperty($$13.getName(), Util.getPropertyName($$13, $$10.getValue($$13)));
                           }
            
                           if ($$12.size() > 0) {
                              $$11.add("properties", $$12);
                           }
            
                           $$11.addProperty("id", Block.getId($$10));
                           if ($$10 == ((Block)$$2x.value()).defaultBlockState()) {
                              $$11.addProperty("default", true);
                           }
            
                           $$9.add($$11);
                        }
            
                        $$3xx.add("states", $$9);
                        String $$14 = $$2x.getRegisteredName();
                        JsonElement $$15 = BlockTypes.CODEC
                           .codec()
                           .encodeStart($$4, (Block)$$2x.value())
                           .getOrThrow($$1xxx -> new AssertionError("Failed to serialize block " + $$14 + " (is type registered in BlockTypes?): " + $$1xxx));
                        $$3xx.add("definition", $$15);
                        $$3.add($$14, $$3xx);
                     }
                  );
               return DataProvider.saveStable($$0, $$3, $$1);
            }
         );
   }

   @Override
   public final String getName() {
      return "Block List";
   }
}
