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
import java.util.Map;
import java.util.Objects;

public class ChunkStatusFix2 extends DataFix {
   private static final Map<String, String> RENAMES_AND_DOWNGRADES = ImmutableMap.builder()
      .put("structure_references", "empty")
      .put("biomes", "empty")
      .put("base", "surface")
      .put("carved", "carvers")
      .put("liquid_carved", "liquid_carvers")
      .put("decorated", "features")
      .put("lighted", "light")
      .put("mobs_spawned", "spawn")
      .put("finalized", "heightmaps")
      .put("fullchunk", "full")
      .build();

   public ChunkStatusFix2(Schema $$0, boolean $$1) {
      super($$0, $$1);
   }

   @Override
   protected TypeRewriteRule makeRule() {
      Type<?> $$0 = this.getInputSchema().getType(References.CHUNK);
      Type<?> $$1 = $$0.findFieldType("Level");
      OpticFinder<?> $$2 = DSL.fieldFinder("Level", $$1);
      return this.fixTypeEverywhereTyped("ChunkStatusFix2", $$0, this.getOutputSchema().getType(References.CHUNK), $$1x -> $$1x.updateTyped($$2, $$0xx -> {
            Dynamic<?> $$1xxx = $$0xx.get(DSL.remainderFinder());
            String $$2xx = $$1xxx.get("Status").asString("empty");
            String $$3 = (String)RENAMES_AND_DOWNGRADES.getOrDefault($$2xx, "empty");
            return Objects.equals($$2xx, $$3) ? $$0xx : $$0xx.set(DSL.remainderFinder(), $$1xxx.set("Status", $$1xxx.createString($$3)));
         }));
   }
}
