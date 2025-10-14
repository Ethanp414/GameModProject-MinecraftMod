package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class CarvingStepRemoveFix extends DataFix {
   public CarvingStepRemoveFix(Schema $$0) {
      super($$0, false);
   }

   @Override
   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("CarvingStepRemoveFix", this.getInputSchema().getType(References.CHUNK), CarvingStepRemoveFix::fixChunk);
   }

   private static Typed<?> fixChunk(Typed<?> $$0) {
      return $$0.update(DSL.remainderFinder(), $$0x -> {
         Dynamic<?> $$1 = $$0x;
         Optional<? extends Dynamic<?>> $$2 = $$0x.get("CarvingMasks").result();
         if ($$2.isPresent()) {
            Optional<? extends Dynamic<?>> $$3 = ((Dynamic)$$2.get()).get("AIR").result();
            if ($$3.isPresent()) {
               $$1 = $$0x.set("carving_mask", (Dynamic<?>)$$3.get());
            }
         }

         return $$1.remove("CarvingMasks");
      });
   }
}
