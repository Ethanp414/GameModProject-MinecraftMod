package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.Mth;

public class VillagerRebuildLevelAndXpFix extends DataFix {
   private static final int TRADES_PER_LEVEL = 2;
   private static final int[] LEVEL_XP_THRESHOLDS = new int[]{0, 10, 50, 100, 150};

   public static int getMinXpPerLevel(int $$0) {
      return LEVEL_XP_THRESHOLDS[Mth.clamp($$0 - 1, 0, LEVEL_XP_THRESHOLDS.length - 1)];
   }

   public VillagerRebuildLevelAndXpFix(Schema $$0, boolean $$1) {
      super($$0, $$1);
   }

   @Override
   public TypeRewriteRule makeRule() {
      Type<?> $$0 = this.getInputSchema().getChoiceType(References.ENTITY, "minecraft:villager");
      OpticFinder<?> $$1 = DSL.namedChoice("minecraft:villager", $$0);
      OpticFinder<?> $$2 = $$0.findField("Offers");
      Type<?> $$3 = $$2.type();
      OpticFinder<?> $$4 = $$3.findField("Recipes");
      ListType<?> $$5 = (ListType)$$4.type();
      OpticFinder<?> $$6 = $$5.getElement().finder();
      return this.fixTypeEverywhereTyped(
         "Villager level and xp rebuild", this.getInputSchema().getType(References.ENTITY), $$5x -> $$5x.updateTyped($$1, $$0, $$3xx -> {
               Dynamic<?> $$4xxx = $$3xx.get(DSL.remainderFinder());
               int $$5xxx = $$4xxx.get("VillagerData").get("level").asInt(0);
               Typed<?> $$6xx = $$3xx;
               if ($$5xxx == 0 || $$5xxx == 1) {
                  int $$7 = $$3xx.getOptionalTyped($$2)
                     .flatMap($$1xxx -> $$1xxx.getOptionalTyped($$4))
                     .map($$1xxx -> $$1xxx.getAllTyped($$6).size())
                     .orElse(0);
                  $$5xxx = Mth.clamp($$7 / 2, 1, 5);
                  if ($$5xxx > 1) {
                     $$6xx = addLevel($$3xx, $$5xxx);
                  }
               }
   
               Optional<Number> $$8 = $$4xxx.get("Xp").asNumber().result();
               if ($$8.isEmpty()) {
                  $$6xx = addXpFromLevel($$6xx, $$5xxx);
               }
   
               return $$6xx;
            })
      );
   }

   private static Typed<?> addLevel(Typed<?> $$0, int $$1) {
      return $$0.update(DSL.remainderFinder(), $$1x -> $$1x.update("VillagerData", $$1xx -> $$1xx.set("level", $$1xx.createInt($$1))));
   }

   private static Typed<?> addXpFromLevel(Typed<?> $$0, int $$1) {
      int $$2 = getMinXpPerLevel($$1);
      return $$0.update(DSL.remainderFinder(), $$1x -> $$1x.set("Xp", $$1x.createInt($$2)));
   }
}
