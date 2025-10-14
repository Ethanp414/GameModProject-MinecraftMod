package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.math.NumberUtils;

public class LevelFlatGeneratorInfoFix extends DataFix {
   private static final String GENERATOR_OPTIONS = "generatorOptions";
   @VisibleForTesting
   static final String DEFAULT = "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
   private static final Splitter SPLITTER = Splitter.on(';').limit(5);
   private static final Splitter LAYER_SPLITTER = Splitter.on(',');
   private static final Splitter OLD_AMOUNT_SPLITTER = Splitter.on('x').limit(2);
   private static final Splitter AMOUNT_SPLITTER = Splitter.on('*').limit(2);
   private static final Splitter BLOCK_SPLITTER = Splitter.on(':').limit(3);

   public LevelFlatGeneratorInfoFix(Schema $$0, boolean $$1) {
      super($$0, $$1);
   }

   @Override
   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         "LevelFlatGeneratorInfoFix", this.getInputSchema().getType(References.LEVEL), $$0 -> $$0.update(DSL.remainderFinder(), this::fix)
      );
   }

   private Dynamic<?> fix(Dynamic<?> $$0) {
      return $$0.get("generatorName").asString("").equalsIgnoreCase("flat")
         ? $$0.update("generatorOptions", $$0x -> DataFixUtils.orElse($$0x.asString().map(this::fixString).map($$0x::createString).result(), $$0x))
         : $$0;
   }

   @VisibleForTesting
   String fixString(String $$0) {
      if ($$0.isEmpty()) {
         return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
      } else {
         Iterator<String> $$1 = SPLITTER.split($$0).iterator();
         String $$2 = (String)$$1.next();
         int $$3;
         String $$4;
         if ($$1.hasNext()) {
            $$3 = NumberUtils.toInt($$2, 0);
            $$4 = (String)$$1.next();
         } else {
            $$3 = 0;
            $$4 = $$2;
         }

         if ($$3 >= 0 && $$3 <= 3) {
            StringBuilder $$7 = new StringBuilder();
            Splitter $$8 = $$3 < 3 ? OLD_AMOUNT_SPLITTER : AMOUNT_SPLITTER;
            $$7.append((String)StreamSupport.stream(LAYER_SPLITTER.split($$4).spliterator(), false).map($$2x -> {
               List<String> $$3 = $$8.splitToList($$2x);
               int $$4;
               String $$5xx;
               if ($$3.size() == 2) {
                  $$4 = NumberUtils.toInt((String)$$3.get(0));
                  $$5xx = (String)$$3.get(1);
               } else {
                  $$4 = 1;
                  $$5xx = (String)$$3.get(0);
               }

               List<String> $$8xx = BLOCK_SPLITTER.splitToList($$5xx);
               int $$9 = ((String)$$8xx.get(0)).equals("minecraft") ? 1 : 0;
               String $$10 = (String)$$8xx.get($$9);
               int $$11 = $$3 == 3 ? EntityBlockStateFix.getBlockId("minecraft:" + $$10) : NumberUtils.toInt($$10, 0);
               int $$12 = $$9 + 1;
               int $$13 = $$8xx.size() > $$12 ? NumberUtils.toInt((String)$$8xx.get($$12), 0) : 0;
               return ($$4 == 1 ? "" : $$4 + "*") + BlockStateData.getTag($$11 << 4 | $$13).get("Name").asString("");
            }).collect(Collectors.joining(",")));

            while($$1.hasNext()) {
               $$7.append(';').append((String)$$1.next());
            }

            return $$7.toString();
         } else {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
         }
      }
   }
}
