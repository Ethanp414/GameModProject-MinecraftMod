package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class PlayerUUIDFix extends AbstractUUIDFix {
   public PlayerUUIDFix(Schema $$0) {
      super($$0, References.PLAYER);
   }

   @Override
   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         "PlayerUUIDFix",
         this.getInputSchema().getType(this.typeReference),
         $$0 -> {
            OpticFinder<?> $$1 = $$0.getType().findField("RootVehicle");
            return $$0.updateTyped(
                  $$1, $$1.type(), $$0x -> $$0x.update(DSL.remainderFinder(), $$0xx -> (Dynamic)replaceUUIDLeastMost($$0xx, "Attach", "Attach").orElse($$0xx))
               )
               .update(DSL.remainderFinder(), $$0x -> EntityUUIDFix.updateEntityUUID(EntityUUIDFix.updateLivingEntity($$0x)));
         }
      );
   }
}
