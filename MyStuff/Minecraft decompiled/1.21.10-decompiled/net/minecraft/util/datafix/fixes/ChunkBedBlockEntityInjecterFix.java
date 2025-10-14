package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ChunkBedBlockEntityInjecterFix extends DataFix {
   public ChunkBedBlockEntityInjecterFix(Schema $$0, boolean $$1) {
      super($$0, $$1);
   }

   @Override
   public TypeRewriteRule makeRule() {
      Type<?> $$0 = this.getOutputSchema().getType(References.CHUNK);
      Type<?> $$1 = $$0.findFieldType("Level");
      Type<?> $$2 = $$1.findFieldType("TileEntities");
      if (!($$2 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType<?> $$3 = (ListType)$$2;
         return this.cap($$1, $$3);
      }
   }

   private <TE> TypeRewriteRule cap(Type<?> $$0, ListType<TE> $$1) {
      Type<TE> $$2 = $$1.getElement();
      OpticFinder<?> $$3 = DSL.fieldFinder("Level", $$0);
      OpticFinder<List<TE>> $$4 = DSL.fieldFinder("TileEntities", $$1);
      int $$5 = 416;
      return TypeRewriteRule.seq(
         this.fixTypeEverywhere(
            "InjectBedBlockEntityType",
            this.getInputSchema().findChoiceType(References.BLOCK_ENTITY),
            this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY),
            $$0x -> $$0xx -> $$0xx
         ),
         this.fixTypeEverywhereTyped(
            "BedBlockEntityInjecter",
            this.getOutputSchema().getType(References.CHUNK),
            $$3x -> {
               Typed<?> $$4xx = $$3x.getTyped($$3);
               Dynamic<?> $$5xx = $$4xx.get(DSL.remainderFinder());
               int $$6 = $$5xx.get("xPos").asInt(0);
               int $$7 = $$5xx.get("zPos").asInt(0);
               List<TE> $$8 = Lists.<TE>newArrayList($$4xx.getOrCreate($$4));
      
               for(Dynamic<?> $$10 : $$5xx.get("Sections").asList(Function.identity())) {
                  int $$11 = $$10.get("Y").asInt(0);
                  Streams.mapWithIndex($$10.get("Blocks").asIntStream(), ($$4xx, $$5xx) -> {
                        if (416 == ($$4xx & 0xFF) << 4) {
                           int $$6xx = (int)$$5xx;
                           int $$7xx = $$6xx & 15;
                           int $$8xx = $$6xx >> 8 & 15;
                           int $$9 = $$6xx >> 4 & 15;
                           Map<Dynamic<?>, Dynamic<?>> $$10xx = Maps.<Dynamic<?>, Dynamic<?>>newHashMap();
                           $$10xx.put($$10.createString("id"), $$10.createString("minecraft:bed"));
                           $$10xx.put($$10.createString("x"), $$10.createInt($$7xx + ($$6 << 4)));
                           $$10xx.put($$10.createString("y"), $$10.createInt($$8xx + ($$11 << 4)));
                           $$10xx.put($$10.createString("z"), $$10.createInt($$9 + ($$7 << 4)));
                           $$10xx.put($$10.createString("color"), $$10.createShort((short)14));
                           return $$10xx;
                        } else {
                           return null;
                        }
                     })
                     .forEachOrdered(
                        $$3xx -> {
                           if ($$3xx != null) {
                              $$8.add(
                                 ((Pair)$$2.read($$10.createMap($$3xx))
                                       .result()
                                       .orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity.")))
                                    .getFirst()
                              );
                           }
                        }
                     );
               }
      
               return !$$8.isEmpty() ? $$3x.set($$3, $$4xx.set($$4, $$8)) : $$3x;
            }
         )
      );
   }
}
