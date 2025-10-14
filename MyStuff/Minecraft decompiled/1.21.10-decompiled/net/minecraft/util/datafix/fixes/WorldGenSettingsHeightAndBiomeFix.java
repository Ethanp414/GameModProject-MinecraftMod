package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.stream.Stream;
import net.minecraft.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenSettingsHeightAndBiomeFix extends DataFix {
   private static final String NAME = "WorldGenSettingsHeightAndBiomeFix";
   public static final String WAS_PREVIOUSLY_INCREASED_KEY = "has_increased_height_already";

   public WorldGenSettingsHeightAndBiomeFix(Schema $$0) {
      super($$0, true);
   }

   @Override
   protected TypeRewriteRule makeRule() {
      Type<?> $$0 = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
      OpticFinder<?> $$1 = $$0.findField("dimensions");
      Type<?> $$2 = this.getOutputSchema().getType(References.WORLD_GEN_SETTINGS);
      Type<?> $$3 = $$2.findFieldType("dimensions");
      return this.fixTypeEverywhereTyped(
         "WorldGenSettingsHeightAndBiomeFix",
         $$0,
         $$2,
         $$2x -> {
            OptionalDynamic<?> $$3xx = $$2x.get(DSL.remainderFinder()).get("has_increased_height_already");
            boolean $$4 = $$3xx.result().isEmpty();
            boolean $$5 = $$3xx.asBoolean(true);
            return $$2x.update(DSL.remainderFinder(), $$0xx -> $$0xx.remove("has_increased_height_already"))
               .updateTyped(
                  $$1,
                  $$3,
                  $$3xx -> Util.writeAndReadTypedOrThrow(
                        $$3xx,
                        $$3,
                        $$2xxx -> $$2xxx.update(
                              "minecraft:overworld",
                              $$2xxxx -> $$2xxxx.update(
                                    "generator",
                                    $$2xxxxx -> {
                                       String $$3xxxx = $$2xxxxx.get("type").asString("");
                                       if ("minecraft:noise".equals($$3xxxx)) {
                                          MutableBoolean $$4xx = new MutableBoolean();
                                          $$2xxxxx = $$2xxxxx.update(
                                             "biome_source",
                                             $$2xxxxxx -> {
                                                String $$3xxxxx = $$2xxxxxx.get("type").asString("");
                                                if ("minecraft:vanilla_layered".equals($$3xxxxx) || $$4 && "minecraft:multi_noise".equals($$3xxxxx)) {
                                                   if ($$2xxxxxx.get("large_biomes").asBoolean(false)) {
                                                      $$4x.setTrue();
                                                   }
                     
                                                   return $$2xxxxxx.createMap(
                                                      ImmutableMap.of(
                                                         $$2xxxxxx.createString("preset"),
                                                         $$2xxxxxx.createString("minecraft:overworld"),
                                                         $$2xxxxxx.createString("type"),
                                                         $$2xxxxxx.createString("minecraft:multi_noise")
                                                      )
                                                   );
                                                } else {
                                                   return $$2xxxxxx;
                                                }
                                             }
                                          );
                                          return $$4xx.booleanValue()
                                             ? $$2xxxxx.update(
                                                "settings",
                                                $$0xxxxxx -> "minecraft:overworld".equals($$0xxxxxx.asString(""))
                                                      ? $$0xxxxxx.createString("minecraft:large_biomes")
                                                      : $$0xxxxxx
                                             )
                                             : $$2xxxxx;
                                       } else if ("minecraft:flat".equals($$3xxxx)) {
                                          return $$5
                                             ? $$2xxxxx
                                             : $$2xxxxx.update(
                                                "settings", $$0xxxxxx -> $$0xxxxxx.update("layers", WorldGenSettingsHeightAndBiomeFix::updateLayers)
                                             );
                                       } else {
                                          return $$2xxxxx;
                                       }
                                    }
                                 )
                           )
                     )
               );
         }
      );
   }

   private static Dynamic<?> updateLayers(Dynamic<?> $$0) {
      Dynamic<?> $$1 = $$0.createMap(
         ImmutableMap.of($$0.createString("height"), $$0.createInt(64), $$0.createString("block"), $$0.createString("minecraft:air"))
      );
      return $$0.createList(Stream.concat(Stream.of($$1), $$0.asStream()));
   }
}
