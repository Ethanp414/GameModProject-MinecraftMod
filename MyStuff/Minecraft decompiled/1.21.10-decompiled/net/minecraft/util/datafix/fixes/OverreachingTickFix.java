package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;

public class OverreachingTickFix extends DataFix {
   public OverreachingTickFix(Schema $$0) {
      super($$0, false);
   }

   @Override
   protected TypeRewriteRule makeRule() {
      Type<?> $$0 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> $$1 = $$0.findField("block_ticks");
      return this.fixTypeEverywhereTyped("Handle ticks saved in the wrong chunk", $$0, $$1x -> {
         Optional<? extends Typed<?>> $$2 = $$1x.getOptionalTyped($$1);
         Optional<? extends Dynamic<?>> $$3 = $$2.isPresent() ? ((Typed)$$2.get()).write().result() : Optional.empty();
         return $$1x.update(DSL.remainderFinder(), $$1xx -> {
            int $$2xx = $$1xx.get("xPos").asInt(0);
            int $$3xx = $$1xx.get("zPos").asInt(0);
            Optional<? extends Dynamic<?>> $$4 = $$1xx.get("fluid_ticks").get().result();
            $$1xx = extractOverreachingTicks($$1xx, $$2xx, $$3xx, $$3, "neighbor_block_ticks");
            return extractOverreachingTicks($$1xx, $$2xx, $$3xx, $$4, "neighbor_fluid_ticks");
         });
      });
   }

   private static Dynamic<?> extractOverreachingTicks(Dynamic<?> $$0, int $$1, int $$2, Optional<? extends Dynamic<?>> $$3, String $$4) {
      if ($$3.isPresent()) {
         List<? extends Dynamic<?>> $$5 = ((Dynamic)$$3.get()).asStream().filter($$2x -> {
            int $$3xx = $$2x.get("x").asInt(0);
            int $$4xx = $$2x.get("z").asInt(0);
            int $$5xx = Math.abs($$1 - ($$3xx >> 4));
            int $$6 = Math.abs($$2 - ($$4xx >> 4));
            return ($$5xx != 0 || $$6 != 0) && $$5xx <= 1 && $$6 <= 1;
         }).toList();
         if (!$$5.isEmpty()) {
            $$0 = $$0.set("UpgradeData", $$0.get("UpgradeData").orElseEmptyMap().set($$4, $$0.createList($$5.stream())));
         }
      }

      return $$0;
   }
}
