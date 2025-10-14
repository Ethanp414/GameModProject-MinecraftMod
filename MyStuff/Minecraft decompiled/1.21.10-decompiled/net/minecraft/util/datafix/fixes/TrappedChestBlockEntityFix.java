package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class TrappedChestBlockEntityFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SIZE = 4096;
   private static final short SIZE_BITS = 12;

   public TrappedChestBlockEntityFix(Schema $$0, boolean $$1) {
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
         OpticFinder<? extends List<?>> $$4 = DSL.fieldFinder("TileEntities", $$3);
         Type<?> $$5 = this.getInputSchema().getType(References.CHUNK);
         OpticFinder<?> $$6 = $$5.findField("Level");
         OpticFinder<?> $$7 = $$6.type().findField("Sections");
         Type<?> $$8 = $$7.type();
         if (!($$8 instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
         } else {
            Type<?> $$9 = ((ListType)$$8).getElement();
            OpticFinder<?> $$10 = DSL.typeFinder($$9);
            return TypeRewriteRule.seq(
               new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", References.BLOCK_ENTITY).makeRule(),
               this.fixTypeEverywhereTyped(
                  "Trapped Chest fix",
                  $$5,
                  $$4x -> $$4x.updateTyped(
                        $$6,
                        $$3xx -> {
                           Optional<? extends Typed<?>> $$4xxx = $$3xx.getOptionalTyped($$7);
                           if ($$4xxx.isEmpty()) {
                              return $$3xx;
                           } else {
                              List<? extends Typed<?>> $$5xx = ((Typed)$$4xxx.get()).getAllTyped($$10);
                              IntSet $$6xx = new IntOpenHashSet();
         
                              for(Typed<?> $$7xx : $$5xx) {
                                 TrappedChestBlockEntityFix.TrappedChestSection $$8xx = new TrappedChestBlockEntityFix.TrappedChestSection(
                                    $$7xx, this.getInputSchema()
                                 );
                                 if (!$$8xx.isSkippable()) {
                                    for(int $$9xx = 0; $$9xx < 4096; ++$$9xx) {
                                       int $$10xx = $$8xx.getBlock($$9xx);
                                       if ($$8xx.isTrappedChest($$10xx)) {
                                          $$6xx.add($$8xx.getIndex() << 12 | $$9xx);
                                       }
                                    }
                                 }
                              }
         
                              Dynamic<?> $$11 = $$3xx.get(DSL.remainderFinder());
                              int $$12 = $$11.get("xPos").asInt(0);
                              int $$13 = $$11.get("zPos").asInt(0);
                              TaggedChoiceType<String> $$14 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
                              return $$3xx.updateTyped(
                                 $$4,
                                 $$4xxx -> $$4xxx.updateTyped(
                                       $$14.finder(),
                                       $$4xxxx -> {
                                          Dynamic<?> $$5xxx = $$4xxxx.getOrCreate(DSL.remainderFinder());
                                          int $$6xxx = $$5xxx.get("x").asInt(0) - ($$12 << 4);
                                          int $$7xxx = $$5xxx.get("y").asInt(0);
                                          int $$8xxx = $$5xxx.get("z").asInt(0) - ($$13 << 4);
                                          return $$6x.contains(LeavesFix.getIndex($$6xxx, $$7xxx, $$8xxx))
                                             ? $$4xxxx.update($$14.finder(), $$0xxxxx -> $$0xxxxx.mapFirst($$0xxxxxx -> {
                                                   if (!Objects.equals($$0xxxxxx, "minecraft:chest")) {
                                                      LOGGER.warn("Block Entity was expected to be a chest");
                                                   }
                  
                                                   return "minecraft:trapped_chest";
                                                }))
                                             : $$4xxxx;
                                       }
                                    )
                              );
                           }
                        }
                     )
               )
            );
         }
      }
   }

   public static final class TrappedChestSection extends LeavesFix.Section {
      @Nullable
      private IntSet chestIds;

      public TrappedChestSection(Typed<?> $$0, Schema $$1) {
         super($$0, $$1);
      }

      @Override
      protected boolean skippable() {
         this.chestIds = new IntOpenHashSet();

         for(int $$0 = 0; $$0 < this.palette.size(); ++$$0) {
            Dynamic<?> $$1 = (Dynamic)this.palette.get($$0);
            String $$2 = $$1.get("Name").asString("");
            if (Objects.equals($$2, "minecraft:trapped_chest")) {
               this.chestIds.add($$0);
            }
         }

         return this.chestIds.isEmpty();
      }

      public boolean isTrappedChest(int $$0) {
         return this.chestIds.contains($$0);
      }
   }
}
