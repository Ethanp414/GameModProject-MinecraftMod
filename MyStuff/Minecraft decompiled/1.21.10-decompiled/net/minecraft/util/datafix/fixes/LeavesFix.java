package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.PackedBitStorage;

public class LeavesFix extends DataFix {
   private static final int NORTH_WEST_MASK = 128;
   private static final int WEST_MASK = 64;
   private static final int SOUTH_WEST_MASK = 32;
   private static final int SOUTH_MASK = 16;
   private static final int SOUTH_EAST_MASK = 8;
   private static final int EAST_MASK = 4;
   private static final int NORTH_EAST_MASK = 2;
   private static final int NORTH_MASK = 1;
   private static final int[][] DIRECTIONS = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
   private static final int DECAY_DISTANCE = 7;
   private static final int SIZE_BITS = 12;
   private static final int SIZE = 4096;
   static final Object2IntMap<String> LEAVES = DataFixUtils.make(new Object2IntOpenHashMap(), $$0 -> {
      $$0.put("minecraft:acacia_leaves", 0);
      $$0.put("minecraft:birch_leaves", 1);
      $$0.put("minecraft:dark_oak_leaves", 2);
      $$0.put("minecraft:jungle_leaves", 3);
      $$0.put("minecraft:oak_leaves", 4);
      $$0.put("minecraft:spruce_leaves", 5);
   });
   static final Set<String> LOGS = ImmutableSet.of(
      "minecraft:acacia_bark",
      "minecraft:birch_bark",
      "minecraft:dark_oak_bark",
      "minecraft:jungle_bark",
      "minecraft:oak_bark",
      "minecraft:spruce_bark",
      "minecraft:acacia_log",
      "minecraft:birch_log",
      "minecraft:dark_oak_log",
      "minecraft:jungle_log",
      "minecraft:oak_log",
      "minecraft:spruce_log",
      "minecraft:stripped_acacia_log",
      "minecraft:stripped_birch_log",
      "minecraft:stripped_dark_oak_log",
      "minecraft:stripped_jungle_log",
      "minecraft:stripped_oak_log",
      "minecraft:stripped_spruce_log"
   );

   public LeavesFix(Schema $$0, boolean $$1) {
      super($$0, $$1);
   }

   @Override
   protected TypeRewriteRule makeRule() {
      Type<?> $$0 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> $$1 = $$0.findField("Level");
      OpticFinder<?> $$2 = $$1.type().findField("Sections");
      Type<?> $$3 = $$2.type();
      if (!($$3 instanceof ListType)) {
         throw new IllegalStateException("Expecting sections to be a list.");
      } else {
         Type<?> $$4 = ((ListType)$$3).getElement();
         OpticFinder<?> $$5 = DSL.typeFinder($$4);
         return this.fixTypeEverywhereTyped(
            "Leaves fix",
            $$0,
            $$3x -> $$3x.updateTyped(
                  $$1,
                  $$2xx -> {
                     int[] $$3xxx = new int[]{0};
                     Typed<?> $$4xx = $$2xx.updateTyped(
                        $$2,
                        $$2xxx -> {
                           Int2ObjectMap<LeavesFix.LeavesSection> $$3xxxx = new Int2ObjectOpenHashMap<>(
                              (Map<? extends Integer, ? extends LeavesFix.LeavesSection>)$$2xxx.getAllTyped($$5)
                                 .stream()
                                 .map($$0xxxx -> new LeavesFix.LeavesSection($$0xxxx, this.getInputSchema()))
                                 .collect(Collectors.toMap(LeavesFix.Section::getIndex, $$0xxxx -> $$0xxxx))
                           );
                           if ($$3xxxx.values().stream().allMatch(LeavesFix.Section::isSkippable)) {
                              return $$2xxx;
                           } else {
                              List<IntSet> $$4xxx = Lists.<IntSet>newArrayList();
         
                              for(int $$5xx = 0; $$5xx < 7; ++$$5xx) {
                                 $$4xxx.add(new IntOpenHashSet());
                              }
         
                              for(LeavesFix.LeavesSection $$6 : $$3xxxx.values()) {
                                 if (!$$6.isSkippable()) {
                                    for(int $$7 = 0; $$7 < 4096; ++$$7) {
                                       int $$8 = $$6.getBlock($$7);
                                       if ($$6.isLog($$8)) {
                                          ((IntSet)$$4xxx.get(0)).add($$6.getIndex() << 12 | $$7);
                                       } else if ($$6.isLeaf($$8)) {
                                          int $$9 = this.getX($$7);
                                          int $$10 = this.getZ($$7);
                                          $$3xx[0] |= getSideMask($$9 == 0, $$9 == 15, $$10 == 0, $$10 == 15);
                                       }
                                    }
                                 }
                              }
         
                              for(int $$11 = 1; $$11 < 7; ++$$11) {
                                 IntSet $$12 = (IntSet)$$4xxx.get($$11 - 1);
                                 IntSet $$13 = (IntSet)$$4xxx.get($$11);
                                 IntIterator $$14 = $$12.iterator();
         
                                 while($$14.hasNext()) {
                                    int $$15 = $$14.nextInt();
                                    int $$16 = this.getX($$15);
                                    int $$17 = this.getY($$15);
                                    int $$18 = this.getZ($$15);
         
                                    for(int[] $$19 : DIRECTIONS) {
                                       int $$20 = $$16 + $$19[0];
                                       int $$21 = $$17 + $$19[1];
                                       int $$22 = $$18 + $$19[2];
                                       if ($$20 >= 0 && $$20 <= 15 && $$22 >= 0 && $$22 <= 15 && $$21 >= 0 && $$21 <= 255) {
                                          LeavesFix.LeavesSection $$23 = $$3xxxx.get($$21 >> 4);
                                          if ($$23 != null && !$$23.isSkippable()) {
                                             int $$24 = getIndex($$20, $$21 & 15, $$22);
                                             int $$25 = $$23.getBlock($$24);
                                             if ($$23.isLeaf($$25)) {
                                                int $$26 = $$23.getDistance($$25);
                                                if ($$26 > $$11) {
                                                   $$23.setDistance($$24, $$25, $$11);
                                                   $$13.add(getIndex($$20, $$21, $$22));
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
         
                              return $$2xxx.updateTyped($$5, $$1xxxx -> $$3xxx.get($$1xxxx.get(DSL.remainderFinder()).get("Y").asInt(0)).write($$1xxxx));
                           }
                        }
                     );
                     if ($$3xxx[0] != 0) {
                        $$4xx = $$4xx.update(DSL.remainderFinder(), $$1xxx -> {
                           Dynamic<?> $$2xxxx = DataFixUtils.orElse($$1xxx.get("UpgradeData").result(), $$1xxx.emptyMap());
                           return $$1xxx.set("UpgradeData", $$2xxxx.set("Sides", $$1xxx.createByte((byte)($$2xxxx.get("Sides").asByte((byte)0) | $$3xx[0]))));
                        });
                     }
      
                     return $$4xx;
                  }
               )
         );
      }
   }

   public static int getIndex(int $$0, int $$1, int $$2) {
      return $$1 << 8 | $$2 << 4 | $$0;
   }

   private int getX(int $$0) {
      return $$0 & 15;
   }

   private int getY(int $$0) {
      return $$0 >> 8 & 0xFF;
   }

   private int getZ(int $$0) {
      return $$0 >> 4 & 15;
   }

   public static int getSideMask(boolean $$0, boolean $$1, boolean $$2, boolean $$3) {
      int $$4 = 0;
      if ($$2) {
         if ($$1) {
            $$4 |= 2;
         } else if ($$0) {
            $$4 |= 128;
         } else {
            $$4 |= 1;
         }
      } else if ($$3) {
         if ($$0) {
            $$4 |= 32;
         } else if ($$1) {
            $$4 |= 8;
         } else {
            $$4 |= 16;
         }
      } else if ($$1) {
         $$4 |= 4;
      } else if ($$0) {
         $$4 |= 64;
      }

      return $$4;
   }

   public static final class LeavesSection extends LeavesFix.Section {
      private static final String PERSISTENT = "persistent";
      private static final String DECAYABLE = "decayable";
      private static final String DISTANCE = "distance";
      @Nullable
      private IntSet leaveIds;
      @Nullable
      private IntSet logIds;
      @Nullable
      private Int2IntMap stateToIdMap;

      public LeavesSection(Typed<?> $$0, Schema $$1) {
         super($$0, $$1);
      }

      @Override
      protected boolean skippable() {
         this.leaveIds = new IntOpenHashSet();
         this.logIds = new IntOpenHashSet();
         this.stateToIdMap = new Int2IntOpenHashMap();

         for(int $$0 = 0; $$0 < this.palette.size(); ++$$0) {
            Dynamic<?> $$1 = (Dynamic)this.palette.get($$0);
            String $$2 = $$1.get("Name").asString("");
            if (LeavesFix.LEAVES.containsKey($$2)) {
               boolean $$3 = Objects.equals($$1.get("Properties").get("decayable").asString(""), "false");
               this.leaveIds.add($$0);
               this.stateToIdMap.put(this.getStateId($$2, $$3, 7), $$0);
               this.palette.set($$0, this.makeLeafTag($$1, $$2, $$3, 7));
            }

            if (LeavesFix.LOGS.contains($$2)) {
               this.logIds.add($$0);
            }
         }

         return this.leaveIds.isEmpty() && this.logIds.isEmpty();
      }

      private Dynamic<?> makeLeafTag(Dynamic<?> $$0, String $$1, boolean $$2, int $$3) {
         Dynamic<?> $$4 = $$0.emptyMap();
         $$4 = $$4.set("persistent", $$4.createString($$2 ? "true" : "false"));
         $$4 = $$4.set("distance", $$4.createString(Integer.toString($$3)));
         Dynamic<?> $$5 = $$0.emptyMap();
         $$5 = $$5.set("Properties", $$4);
         return $$5.set("Name", $$5.createString($$1));
      }

      public boolean isLog(int $$0) {
         return this.logIds.contains($$0);
      }

      public boolean isLeaf(int $$0) {
         return this.leaveIds.contains($$0);
      }

      int getDistance(int $$0) {
         return this.isLog($$0) ? 0 : Integer.parseInt(((Dynamic)this.palette.get($$0)).get("Properties").get("distance").asString(""));
      }

      void setDistance(int $$0, int $$1, int $$2) {
         Dynamic<?> $$3 = (Dynamic)this.palette.get($$1);
         String $$4 = $$3.get("Name").asString("");
         boolean $$5 = Objects.equals($$3.get("Properties").get("persistent").asString(""), "true");
         int $$6 = this.getStateId($$4, $$5, $$2);
         if (!this.stateToIdMap.containsKey($$6)) {
            int $$7 = this.palette.size();
            this.leaveIds.add($$7);
            this.stateToIdMap.put($$6, $$7);
            this.palette.add(this.makeLeafTag($$3, $$4, $$5, $$2));
         }

         int $$8 = this.stateToIdMap.get($$6);
         if (1 << this.storage.getBits() <= $$8) {
            PackedBitStorage $$9 = new PackedBitStorage(this.storage.getBits() + 1, 4096);

            for(int $$10 = 0; $$10 < 4096; ++$$10) {
               $$9.set($$10, this.storage.get($$10));
            }

            this.storage = $$9;
         }

         this.storage.set($$0, $$8);
      }
   }

   public abstract static class Section {
      protected static final String BLOCK_STATES_TAG = "BlockStates";
      protected static final String NAME_TAG = "Name";
      protected static final String PROPERTIES_TAG = "Properties";
      private final Type<Pair<String, Dynamic<?>>> blockStateType = DSL.named(References.BLOCK_STATE.typeName(), DSL.remainderType());
      protected final OpticFinder<List<Pair<String, Dynamic<?>>>> paletteFinder = DSL.fieldFinder("Palette", DSL.list(this.blockStateType));
      protected final List<Dynamic<?>> palette;
      protected final int index;
      @Nullable
      protected PackedBitStorage storage;

      public Section(Typed<?> $$0, Schema $$1) {
         if (!Objects.equals($$1.getType(References.BLOCK_STATE), this.blockStateType)) {
            throw new IllegalStateException("Block state type is not what was expected.");
         } else {
            Optional<List<Pair<String, Dynamic<?>>>> $$2 = $$0.getOptional(this.paletteFinder);
            this.palette = (List)$$2.map($$0x -> (List)$$0x.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse(ImmutableList.of());
            Dynamic<?> $$3 = $$0.get(DSL.remainderFinder());
            this.index = $$3.get("Y").asInt(0);
            this.readStorage($$3);
         }
      }

      protected void readStorage(Dynamic<?> $$0) {
         if (this.skippable()) {
            this.storage = null;
         } else {
            long[] $$1 = $$0.get("BlockStates").asLongStream().toArray();
            int $$2 = Math.max(4, DataFixUtils.ceillog2(this.palette.size()));
            this.storage = new PackedBitStorage($$2, 4096, $$1);
         }
      }

      public Typed<?> write(Typed<?> $$0) {
         return this.isSkippable()
            ? $$0
            : $$0.update(DSL.remainderFinder(), $$0x -> $$0x.set("BlockStates", $$0x.createLongList(Arrays.stream(this.storage.getRaw()))))
               .set(this.paletteFinder, (List)this.palette.stream().map($$0x -> Pair.of(References.BLOCK_STATE.typeName(), $$0x)).collect(Collectors.toList()));
      }

      public boolean isSkippable() {
         return this.storage == null;
      }

      public int getBlock(int $$0) {
         return this.storage.get($$0);
      }

      protected int getStateId(String $$0, boolean $$1, int $$2) {
         return LeavesFix.LEAVES.get($$0) << 5 | ($$1 ? 16 : 0) | $$2;
      }

      int getIndex() {
         return this.index;
      }

      protected abstract boolean skippable();
   }
}
