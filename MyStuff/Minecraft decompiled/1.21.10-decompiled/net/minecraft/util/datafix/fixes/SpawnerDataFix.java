package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.List;

public class SpawnerDataFix extends DataFix {
   public SpawnerDataFix(Schema $$0) {
      super($$0, true);
   }

   @Override
   protected TypeRewriteRule makeRule() {
      Type<?> $$0 = this.getInputSchema().getType(References.UNTAGGED_SPAWNER);
      Type<?> $$1 = this.getOutputSchema().getType(References.UNTAGGED_SPAWNER);
      OpticFinder<?> $$2 = $$0.findField("SpawnData");
      Type<?> $$3 = $$1.findField("SpawnData").type();
      OpticFinder<?> $$4 = $$0.findField("SpawnPotentials");
      Type<?> $$5 = $$1.findField("SpawnPotentials").type();
      return this.fixTypeEverywhereTyped(
         "Fix mob spawner data structure",
         $$0,
         $$1,
         $$4x -> $$4x.updateTyped($$2, $$3, $$1xx -> this.wrapEntityToSpawnData($$3, $$1xx))
               .updateTyped($$4, $$5, $$1xx -> this.wrapSpawnPotentialsToWeightedEntries($$5, $$1xx))
      );
   }

   private <T> Typed<T> wrapEntityToSpawnData(Type<T> $$0, Typed<?> $$1) {
      DynamicOps<?> $$2 = $$1.getOps();
      return new Typed<>($$0, $$2, (T)Pair.<Object, Dynamic>of($$1.getValue(), new Dynamic<>($$2)));
   }

   private <T> Typed<T> wrapSpawnPotentialsToWeightedEntries(Type<T> $$0, Typed<?> $$1) {
      DynamicOps<?> $$2 = $$1.getOps();
      List<?> $$3 = (List)$$1.getValue();
      List<?> $$4 = $$3.stream().map($$1x -> {
         Pair<Object, Dynamic<?>> $$2xx = (Pair)$$1x;
         int $$3xx = ((Number)$$2xx.getSecond().get("Weight").asNumber().result().orElse(1)).intValue();
         Dynamic<?> $$4xx = new Dynamic<>($$2);
         $$4xx = $$4xx.set("weight", $$4xx.createInt($$3xx));
         Dynamic<?> $$5 = $$2xx.getSecond().remove("Weight").remove("Entity");
         return Pair.of(Pair.of($$2xx.getFirst(), $$5), $$4xx);
      }).toList();
      return new Typed<>($$0, $$2, (T)$$4);
   }
}
