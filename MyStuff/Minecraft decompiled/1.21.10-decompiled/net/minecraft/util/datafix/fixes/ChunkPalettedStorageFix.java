package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.PackedBitStorage;
import org.slf4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
   private static final int NORTH_WEST_MASK = 128;
   private static final int WEST_MASK = 64;
   private static final int SOUTH_WEST_MASK = 32;
   private static final int SOUTH_MASK = 16;
   private static final int SOUTH_EAST_MASK = 8;
   private static final int EAST_MASK = 4;
   private static final int NORTH_EAST_MASK = 2;
   private static final int NORTH_MASK = 1;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int SIZE = 4096;

   public ChunkPalettedStorageFix(Schema $$0, boolean $$1) {
      super($$0, $$1);
   }

   public static String getName(Dynamic<?> $$0) {
      return $$0.get("Name").asString("");
   }

   public static String getProperty(Dynamic<?> $$0, String $$1) {
      return $$0.get("Properties").get($$1).asString("");
   }

   public static int idFor(CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> $$0, Dynamic<?> $$1) {
      int $$2 = $$0.getId($$1);
      if ($$2 == -1) {
         $$2 = $$0.add($$1);
      }

      return $$2;
   }

   private Dynamic<?> fix(Dynamic<?> $$0) {
      Optional<? extends Dynamic<?>> $$1 = $$0.get("Level").result();
      return $$1.isPresent() && ((Dynamic)$$1.get()).get("Sections").asStreamOpt().result().isPresent()
         ? $$0.set("Level", new ChunkPalettedStorageFix.UpgradeChunk((Dynamic<?>)$$1.get()).write())
         : $$0;
   }

   @Override
   public TypeRewriteRule makeRule() {
      Type<?> $$0 = this.getInputSchema().getType(References.CHUNK);
      Type<?> $$1 = this.getOutputSchema().getType(References.CHUNK);
      return this.writeFixAndRead("ChunkPalettedStorageFix", $$0, $$1, this::fix);
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

   static class DataLayer {
      private static final int SIZE = 2048;
      private static final int NIBBLE_SIZE = 4;
      private final byte[] data;

      public DataLayer() {
         this.data = new byte[2048];
      }

      public DataLayer(byte[] $$0) {
         this.data = $$0;
         if ($$0.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + $$0.length);
         }
      }

      public int get(int $$0, int $$1, int $$2) {
         int $$3 = this.getPosition($$1 << 8 | $$2 << 4 | $$0);
         return this.isFirst($$1 << 8 | $$2 << 4 | $$0) ? this.data[$$3] & 15 : this.data[$$3] >> 4 & 15;
      }

      private boolean isFirst(int $$0) {
         return ($$0 & 1) == 0;
      }

      private int getPosition(int $$0) {
         return $$0 >> 1;
      }
   }

   public static enum Direction {
      DOWN(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      UP(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      NORTH(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      SOUTH(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      WEST(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.X),
      EAST(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.X);

      private final ChunkPalettedStorageFix.Direction.Axis axis;
      private final ChunkPalettedStorageFix.Direction.AxisDirection axisDirection;

      private Direction(final ChunkPalettedStorageFix.Direction.AxisDirection param3, final ChunkPalettedStorageFix.Direction.Axis param4) {
         this.axis = $$1;
         this.axisDirection = $$0;
      }

      public ChunkPalettedStorageFix.Direction.AxisDirection getAxisDirection() {
         return this.axisDirection;
      }

      public ChunkPalettedStorageFix.Direction.Axis getAxis() {
         return this.axis;
      }

      public static enum Axis {
         X,
         Y,
         Z;
      }

      public static enum AxisDirection {
         POSITIVE(1),
         NEGATIVE(-1);

         private final int step;

         private AxisDirection(final int param3) {
            this.step = $$0;
         }

         public int getStep() {
            return this.step;
         }
      }
   }

   static class MappingConstants {
      static final BitSet VIRTUAL = new BitSet(256);
      static final BitSet FIX = new BitSet(256);
      static final Dynamic<?> PUMPKIN = ExtraDataFixUtils.blockState("minecraft:pumpkin");
      static final Dynamic<?> SNOWY_PODZOL = ExtraDataFixUtils.blockState("minecraft:podzol", Map.of("snowy", "true"));
      static final Dynamic<?> SNOWY_GRASS = ExtraDataFixUtils.blockState("minecraft:grass_block", Map.of("snowy", "true"));
      static final Dynamic<?> SNOWY_MYCELIUM = ExtraDataFixUtils.blockState("minecraft:mycelium", Map.of("snowy", "true"));
      static final Dynamic<?> UPPER_SUNFLOWER = ExtraDataFixUtils.blockState("minecraft:sunflower", Map.of("half", "upper"));
      static final Dynamic<?> UPPER_LILAC = ExtraDataFixUtils.blockState("minecraft:lilac", Map.of("half", "upper"));
      static final Dynamic<?> UPPER_TALL_GRASS = ExtraDataFixUtils.blockState("minecraft:tall_grass", Map.of("half", "upper"));
      static final Dynamic<?> UPPER_LARGE_FERN = ExtraDataFixUtils.blockState("minecraft:large_fern", Map.of("half", "upper"));
      static final Dynamic<?> UPPER_ROSE_BUSH = ExtraDataFixUtils.blockState("minecraft:rose_bush", Map.of("half", "upper"));
      static final Dynamic<?> UPPER_PEONY = ExtraDataFixUtils.blockState("minecraft:peony", Map.of("half", "upper"));
      static final Map<String, Dynamic<?>> FLOWER_POT_MAP = DataFixUtils.make(Maps.newHashMap(), $$0 -> {
         $$0.put("minecraft:air0", ExtraDataFixUtils.blockState("minecraft:flower_pot"));
         $$0.put("minecraft:red_flower0", ExtraDataFixUtils.blockState("minecraft:potted_poppy"));
         $$0.put("minecraft:red_flower1", ExtraDataFixUtils.blockState("minecraft:potted_blue_orchid"));
         $$0.put("minecraft:red_flower2", ExtraDataFixUtils.blockState("minecraft:potted_allium"));
         $$0.put("minecraft:red_flower3", ExtraDataFixUtils.blockState("minecraft:potted_azure_bluet"));
         $$0.put("minecraft:red_flower4", ExtraDataFixUtils.blockState("minecraft:potted_red_tulip"));
         $$0.put("minecraft:red_flower5", ExtraDataFixUtils.blockState("minecraft:potted_orange_tulip"));
         $$0.put("minecraft:red_flower6", ExtraDataFixUtils.blockState("minecraft:potted_white_tulip"));
         $$0.put("minecraft:red_flower7", ExtraDataFixUtils.blockState("minecraft:potted_pink_tulip"));
         $$0.put("minecraft:red_flower8", ExtraDataFixUtils.blockState("minecraft:potted_oxeye_daisy"));
         $$0.put("minecraft:yellow_flower0", ExtraDataFixUtils.blockState("minecraft:potted_dandelion"));
         $$0.put("minecraft:sapling0", ExtraDataFixUtils.blockState("minecraft:potted_oak_sapling"));
         $$0.put("minecraft:sapling1", ExtraDataFixUtils.blockState("minecraft:potted_spruce_sapling"));
         $$0.put("minecraft:sapling2", ExtraDataFixUtils.blockState("minecraft:potted_birch_sapling"));
         $$0.put("minecraft:sapling3", ExtraDataFixUtils.blockState("minecraft:potted_jungle_sapling"));
         $$0.put("minecraft:sapling4", ExtraDataFixUtils.blockState("minecraft:potted_acacia_sapling"));
         $$0.put("minecraft:sapling5", ExtraDataFixUtils.blockState("minecraft:potted_dark_oak_sapling"));
         $$0.put("minecraft:red_mushroom0", ExtraDataFixUtils.blockState("minecraft:potted_red_mushroom"));
         $$0.put("minecraft:brown_mushroom0", ExtraDataFixUtils.blockState("minecraft:potted_brown_mushroom"));
         $$0.put("minecraft:deadbush0", ExtraDataFixUtils.blockState("minecraft:potted_dead_bush"));
         $$0.put("minecraft:tallgrass2", ExtraDataFixUtils.blockState("minecraft:potted_fern"));
         $$0.put("minecraft:cactus0", ExtraDataFixUtils.blockState("minecraft:potted_cactus"));
      });
      static final Map<String, Dynamic<?>> SKULL_MAP = DataFixUtils.make(Maps.newHashMap(), $$0 -> {
         mapSkull($$0, 0, "skeleton", "skull");
         mapSkull($$0, 1, "wither_skeleton", "skull");
         mapSkull($$0, 2, "zombie", "head");
         mapSkull($$0, 3, "player", "head");
         mapSkull($$0, 4, "creeper", "head");
         mapSkull($$0, 5, "dragon", "head");
      });
      static final Map<String, Dynamic<?>> DOOR_MAP = DataFixUtils.make(Maps.newHashMap(), $$0 -> {
         mapDoor($$0, "oak_door");
         mapDoor($$0, "iron_door");
         mapDoor($$0, "spruce_door");
         mapDoor($$0, "birch_door");
         mapDoor($$0, "jungle_door");
         mapDoor($$0, "acacia_door");
         mapDoor($$0, "dark_oak_door");
      });
      static final Map<String, Dynamic<?>> NOTE_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), $$0 -> {
         for(int $$1 = 0; $$1 < 26; ++$$1) {
            $$0.put("true" + $$1, ExtraDataFixUtils.blockState("minecraft:note_block", Map.of("powered", "true", "note", String.valueOf($$1))));
            $$0.put("false" + $$1, ExtraDataFixUtils.blockState("minecraft:note_block", Map.of("powered", "false", "note", String.valueOf($$1))));
         }
      });
      private static final Int2ObjectMap<String> DYE_COLOR_MAP = DataFixUtils.make(new Int2ObjectOpenHashMap(), $$0 -> {
         $$0.put(0, "white");
         $$0.put(1, "orange");
         $$0.put(2, "magenta");
         $$0.put(3, "light_blue");
         $$0.put(4, "yellow");
         $$0.put(5, "lime");
         $$0.put(6, "pink");
         $$0.put(7, "gray");
         $$0.put(8, "light_gray");
         $$0.put(9, "cyan");
         $$0.put(10, "purple");
         $$0.put(11, "blue");
         $$0.put(12, "brown");
         $$0.put(13, "green");
         $$0.put(14, "red");
         $$0.put(15, "black");
      });
      static final Map<String, Dynamic<?>> BED_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), $$0 -> {
         for(Entry<String> $$1 : DYE_COLOR_MAP.int2ObjectEntrySet()) {
            if (!Objects.equals($$1.getValue(), "red")) {
               addBeds($$0, $$1.getIntKey(), (String)$$1.getValue());
            }
         }
      });
      static final Map<String, Dynamic<?>> BANNER_BLOCK_MAP = DataFixUtils.make(Maps.newHashMap(), $$0 -> {
         for(Entry<String> $$1 : DYE_COLOR_MAP.int2ObjectEntrySet()) {
            if (!Objects.equals($$1.getValue(), "white")) {
               addBanners($$0, 15 - $$1.getIntKey(), (String)$$1.getValue());
            }
         }
      });
      static final Dynamic<?> AIR = ExtraDataFixUtils.blockState("minecraft:air");

      private MappingConstants() {
      }

      private static void mapSkull(Map<String, Dynamic<?>> $$0, int $$1, String $$2, String $$3) {
         $$0.put($$1 + "north", ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_wall_" + $$3, Map.of("facing", "north")));
         $$0.put($$1 + "east", ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_wall_" + $$3, Map.of("facing", "east")));
         $$0.put($$1 + "south", ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_wall_" + $$3, Map.of("facing", "south")));
         $$0.put($$1 + "west", ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_wall_" + $$3, Map.of("facing", "west")));

         for(int $$4 = 0; $$4 < 16; ++$$4) {
            $$0.put("" + $$1 + $$4, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_" + $$3, Map.of("rotation", String.valueOf($$4))));
         }
      }

      private static void mapDoor(Map<String, Dynamic<?>> $$0, String $$1) {
         String $$2 = "minecraft:" + $$1;
         $$0.put(
            "minecraft:" + $$1 + "eastlowerleftfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastlowerleftfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastlowerlefttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastlowerlefttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastlowerrightfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastlowerrightfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastlowerrighttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastlowerrighttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastupperleftfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastupperleftfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastupperlefttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastupperlefttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastupperrightfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastupperrightfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastupperrighttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "eastupperrighttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northlowerleftfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northlowerleftfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northlowerlefttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northlowerlefttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northlowerrightfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northlowerrightfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northlowerrighttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northlowerrighttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northupperleftfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northupperleftfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northupperlefttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northupperlefttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northupperrightfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northupperrightfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northupperrighttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "northupperrighttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southlowerleftfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southlowerleftfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southlowerlefttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southlowerlefttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southlowerrightfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southlowerrightfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southlowerrighttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southlowerrighttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southupperleftfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southupperleftfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southupperlefttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southupperlefttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southupperrightfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southupperrightfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southupperrighttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "southupperrighttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westlowerleftfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westlowerleftfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westlowerlefttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westlowerlefttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westlowerrightfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westlowerrightfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westlowerrighttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westlowerrighttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westupperleftfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westupperleftfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westupperlefttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westupperlefttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westupperrightfalsefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westupperrightfalsetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "true"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westupperrighttruefalse",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "false"))
         );
         $$0.put(
            "minecraft:" + $$1 + "westupperrighttruetrue",
            ExtraDataFixUtils.blockState($$2, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "true"))
         );
      }

      private static void addBeds(Map<String, Dynamic<?>> $$0, int $$1, String $$2) {
         $$0.put(
            "southfalsefoot" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "south", "occupied", "false", "part", "foot"))
         );
         $$0.put(
            "westfalsefoot" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "west", "occupied", "false", "part", "foot"))
         );
         $$0.put(
            "northfalsefoot" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "north", "occupied", "false", "part", "foot"))
         );
         $$0.put(
            "eastfalsefoot" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "east", "occupied", "false", "part", "foot"))
         );
         $$0.put(
            "southfalsehead" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "south", "occupied", "false", "part", "head"))
         );
         $$0.put(
            "westfalsehead" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "west", "occupied", "false", "part", "head"))
         );
         $$0.put(
            "northfalsehead" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "north", "occupied", "false", "part", "head"))
         );
         $$0.put(
            "eastfalsehead" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "east", "occupied", "false", "part", "head"))
         );
         $$0.put(
            "southtruehead" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "south", "occupied", "true", "part", "head"))
         );
         $$0.put("westtruehead" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "west", "occupied", "true", "part", "head")));
         $$0.put(
            "northtruehead" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "north", "occupied", "true", "part", "head"))
         );
         $$0.put("easttruehead" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_bed", Map.of("facing", "east", "occupied", "true", "part", "head")));
      }

      private static void addBanners(Map<String, Dynamic<?>> $$0, int $$1, String $$2) {
         for(int $$3 = 0; $$3 < 16; ++$$3) {
            $$0.put($$3 + "_" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_banner", Map.of("rotation", String.valueOf($$3))));
         }

         $$0.put("north_" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_wall_banner", Map.of("facing", "north")));
         $$0.put("south_" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_wall_banner", Map.of("facing", "south")));
         $$0.put("west_" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_wall_banner", Map.of("facing", "west")));
         $$0.put("east_" + $$1, ExtraDataFixUtils.blockState("minecraft:" + $$2 + "_wall_banner", Map.of("facing", "east")));
      }

      static {
         FIX.set(2);
         FIX.set(3);
         FIX.set(110);
         FIX.set(140);
         FIX.set(144);
         FIX.set(25);
         FIX.set(86);
         FIX.set(26);
         FIX.set(176);
         FIX.set(177);
         FIX.set(175);
         FIX.set(64);
         FIX.set(71);
         FIX.set(193);
         FIX.set(194);
         FIX.set(195);
         FIX.set(196);
         FIX.set(197);
         VIRTUAL.set(54);
         VIRTUAL.set(146);
         VIRTUAL.set(25);
         VIRTUAL.set(26);
         VIRTUAL.set(51);
         VIRTUAL.set(53);
         VIRTUAL.set(67);
         VIRTUAL.set(108);
         VIRTUAL.set(109);
         VIRTUAL.set(114);
         VIRTUAL.set(128);
         VIRTUAL.set(134);
         VIRTUAL.set(135);
         VIRTUAL.set(136);
         VIRTUAL.set(156);
         VIRTUAL.set(163);
         VIRTUAL.set(164);
         VIRTUAL.set(180);
         VIRTUAL.set(203);
         VIRTUAL.set(55);
         VIRTUAL.set(85);
         VIRTUAL.set(113);
         VIRTUAL.set(188);
         VIRTUAL.set(189);
         VIRTUAL.set(190);
         VIRTUAL.set(191);
         VIRTUAL.set(192);
         VIRTUAL.set(93);
         VIRTUAL.set(94);
         VIRTUAL.set(101);
         VIRTUAL.set(102);
         VIRTUAL.set(160);
         VIRTUAL.set(106);
         VIRTUAL.set(107);
         VIRTUAL.set(183);
         VIRTUAL.set(184);
         VIRTUAL.set(185);
         VIRTUAL.set(186);
         VIRTUAL.set(187);
         VIRTUAL.set(132);
         VIRTUAL.set(139);
         VIRTUAL.set(199);
      }
   }

   static class Section {
      private final CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> palette = CrudeIncrementalIntIdentityHashBiMap.create(32);
      private final List<Dynamic<?>> listTag;
      private final Dynamic<?> section;
      private final boolean hasData;
      final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap<>();
      final IntList update = new IntArrayList();
      public final int y;
      private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
      private final int[] buffer = new int[4096];

      public Section(Dynamic<?> $$0) {
         this.listTag = Lists.<Dynamic<?>>newArrayList();
         this.section = $$0;
         this.y = $$0.get("Y").asInt(0);
         this.hasData = $$0.get("Blocks").result().isPresent();
      }

      public Dynamic<?> getBlock(int $$0) {
         if ($$0 >= 0 && $$0 <= 4095) {
            Dynamic<?> $$1 = this.palette.byId(this.buffer[$$0]);
            return $$1 == null ? ChunkPalettedStorageFix.MappingConstants.AIR : $$1;
         } else {
            return ChunkPalettedStorageFix.MappingConstants.AIR;
         }
      }

      public void setBlock(int $$0, Dynamic<?> $$1) {
         if (this.seen.add($$1)) {
            this.listTag.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName($$1)) ? ChunkPalettedStorageFix.MappingConstants.AIR : $$1);
         }

         this.buffer[$$0] = ChunkPalettedStorageFix.idFor(this.palette, $$1);
      }

      public int upgrade(int $$0) {
         if (!this.hasData) {
            return $$0;
         } else {
            ByteBuffer $$1 = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
            ChunkPalettedStorageFix.DataLayer $$2 = (ChunkPalettedStorageFix.DataLayer)this.section
               .get("Data")
               .asByteBufferOpt()
               .map($$0x -> new ChunkPalettedStorageFix.DataLayer(DataFixUtils.toArray($$0x)))
               .result()
               .orElseGet(ChunkPalettedStorageFix.DataLayer::new);
            ChunkPalettedStorageFix.DataLayer $$3 = (ChunkPalettedStorageFix.DataLayer)this.section
               .get("Add")
               .asByteBufferOpt()
               .map($$0x -> new ChunkPalettedStorageFix.DataLayer(DataFixUtils.toArray($$0x)))
               .result()
               .orElseGet(ChunkPalettedStorageFix.DataLayer::new);
            this.seen.add(ChunkPalettedStorageFix.MappingConstants.AIR);
            ChunkPalettedStorageFix.idFor(this.palette, ChunkPalettedStorageFix.MappingConstants.AIR);
            this.listTag.add(ChunkPalettedStorageFix.MappingConstants.AIR);

            for(int $$4 = 0; $$4 < 4096; ++$$4) {
               int $$5 = $$4 & 15;
               int $$6 = $$4 >> 8 & 15;
               int $$7 = $$4 >> 4 & 15;
               int $$8 = $$3.get($$5, $$6, $$7) << 12 | ($$1.get($$4) & 255) << 4 | $$2.get($$5, $$6, $$7);
               if (ChunkPalettedStorageFix.MappingConstants.FIX.get($$8 >> 4)) {
                  this.addFix($$8 >> 4, $$4);
               }

               if (ChunkPalettedStorageFix.MappingConstants.VIRTUAL.get($$8 >> 4)) {
                  int $$9 = ChunkPalettedStorageFix.getSideMask($$5 == 0, $$5 == 15, $$7 == 0, $$7 == 15);
                  if ($$9 == 0) {
                     this.update.add($$4);
                  } else {
                     $$0 |= $$9;
                  }
               }

               this.setBlock($$4, BlockStateData.getTag($$8));
            }

            return $$0;
         }
      }

      private void addFix(int $$0, int $$1) {
         IntList $$2 = this.toFix.get($$0);
         if ($$2 == null) {
            $$2 = new IntArrayList();
            this.toFix.put($$0, $$2);
         }

         $$2.add($$1);
      }

      public Dynamic<?> write() {
         Dynamic<?> $$0 = this.section;
         if (!this.hasData) {
            return $$0;
         } else {
            $$0 = $$0.set("Palette", $$0.createList(this.listTag.stream()));
            int $$1 = Math.max(4, DataFixUtils.ceillog2(this.seen.size()));
            PackedBitStorage $$2 = new PackedBitStorage($$1, 4096);

            for(int $$3 = 0; $$3 < this.buffer.length; ++$$3) {
               $$2.set($$3, this.buffer[$$3]);
            }

            $$0 = $$0.set("BlockStates", $$0.createLongList(Arrays.stream($$2.getRaw())));
            $$0 = $$0.remove("Blocks");
            $$0 = $$0.remove("Data");
            return $$0.remove("Add");
         }
      }
   }

   static final class UpgradeChunk {
      private int sides;
      private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
      private final Dynamic<?> level;
      private final int x;
      private final int z;
      private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap<>(16);

      public UpgradeChunk(Dynamic<?> $$0) {
         this.level = $$0;
         this.x = $$0.get("xPos").asInt(0) << 4;
         this.z = $$0.get("zPos").asInt(0) << 4;
         $$0.get("TileEntities")
            .asStreamOpt()
            .ifSuccess(
               $$0x -> $$0x.forEach(
                     $$0xx -> {
                        int $$1xx = $$0xx.get("x").asInt(0) - this.x & 15;
                        int $$2xx = $$0xx.get("y").asInt(0);
                        int $$3xx = $$0xx.get("z").asInt(0) - this.z & 15;
                        int $$4xx = $$2xx << 8 | $$3xx << 4 | $$1xx;
                        if (this.blockEntities.put($$4xx, $$0xx) != null) {
                           ChunkPalettedStorageFix.LOGGER
                              .warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, $$1xx, $$2xx, $$3xx);
                        }
                     }
                  )
            );
         boolean $$1 = $$0.get("convertedFromAlphaFormat").asBoolean(false);
         $$0.get("Sections").asStreamOpt().ifSuccess($$0x -> $$0x.forEach($$0xx -> {
               ChunkPalettedStorageFix.Section $$1xx = new ChunkPalettedStorageFix.Section($$0xx);
               this.sides = $$1xx.upgrade(this.sides);
               this.sections[$$1xx.y] = $$1xx;
            }));

         for(ChunkPalettedStorageFix.Section $$2 : this.sections) {
            if ($$2 != null) {
               for(Entry<IntList> $$3 : $$2.toFix.int2ObjectEntrySet()) {
                  int $$4 = $$2.y << 12;
                  switch($$3.getIntKey()) {
                     case 2:
                        for(int $$5 : (IntList)$$3.getValue()) {
                           $$5 |= $$4;
                           Dynamic<?> $$6 = this.getBlock($$5);
                           if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName($$6))) {
                              String $$7 = ChunkPalettedStorageFix.getName(this.getBlock(relative($$5, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals($$7) || "minecraft:snow_layer".equals($$7)) {
                                 this.setBlock($$5, ChunkPalettedStorageFix.MappingConstants.SNOWY_GRASS);
                              }
                           }
                        }
                        break;
                     case 3:
                        for(int $$8 : (IntList)$$3.getValue()) {
                           $$8 |= $$4;
                           Dynamic<?> $$9 = this.getBlock($$8);
                           if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName($$9))) {
                              String $$10 = ChunkPalettedStorageFix.getName(this.getBlock(relative($$8, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals($$10) || "minecraft:snow_layer".equals($$10)) {
                                 this.setBlock($$8, ChunkPalettedStorageFix.MappingConstants.SNOWY_PODZOL);
                              }
                           }
                        }
                        break;
                     case 25:
                        for(int $$14 : (IntList)$$3.getValue()) {
                           $$14 |= $$4;
                           Dynamic<?> $$15 = this.removeBlockEntity($$14);
                           if ($$15 != null) {
                              String $$16 = Boolean.toString($$15.get("powered").asBoolean(false))
                                 + (byte)Math.min(Math.max($$15.get("note").asInt(0), 0), 24);
                              this.setBlock(
                                 $$14,
                                 (Dynamic<?>)ChunkPalettedStorageFix.MappingConstants.NOTE_BLOCK_MAP
                                    .getOrDefault($$16, (Dynamic)ChunkPalettedStorageFix.MappingConstants.NOTE_BLOCK_MAP.get("false0"))
                              );
                           }
                        }
                        break;
                     case 26:
                        for(int $$17 : (IntList)$$3.getValue()) {
                           $$17 |= $$4;
                           Dynamic<?> $$18 = this.getBlockEntity($$17);
                           Dynamic<?> $$19 = this.getBlock($$17);
                           if ($$18 != null) {
                              int $$20 = $$18.get("color").asInt(0);
                              if ($$20 != 14 && $$20 >= 0 && $$20 < 16) {
                                 String $$21 = ChunkPalettedStorageFix.getProperty($$19, "facing")
                                    + ChunkPalettedStorageFix.getProperty($$19, "occupied")
                                    + ChunkPalettedStorageFix.getProperty($$19, "part")
                                    + $$20;
                                 if (ChunkPalettedStorageFix.MappingConstants.BED_BLOCK_MAP.containsKey($$21)) {
                                    this.setBlock($$17, (Dynamic<?>)ChunkPalettedStorageFix.MappingConstants.BED_BLOCK_MAP.get($$21));
                                 }
                              }
                           }
                        }
                        break;
                     case 64:
                     case 71:
                     case 193:
                     case 194:
                     case 195:
                     case 196:
                     case 197:
                        for(int $$39 : (IntList)$$3.getValue()) {
                           $$39 |= $$4;
                           Dynamic<?> $$40 = this.getBlock($$39);
                           if (ChunkPalettedStorageFix.getName($$40).endsWith("_door")) {
                              Dynamic<?> $$41 = this.getBlock($$39);
                              if ("lower".equals(ChunkPalettedStorageFix.getProperty($$41, "half"))) {
                                 int $$42 = relative($$39, ChunkPalettedStorageFix.Direction.UP);
                                 Dynamic<?> $$43 = this.getBlock($$42);
                                 String $$44 = ChunkPalettedStorageFix.getName($$41);
                                 if ($$44.equals(ChunkPalettedStorageFix.getName($$43))) {
                                    String $$45 = ChunkPalettedStorageFix.getProperty($$41, "facing");
                                    String $$46 = ChunkPalettedStorageFix.getProperty($$41, "open");
                                    String $$47 = $$1 ? "left" : ChunkPalettedStorageFix.getProperty($$43, "hinge");
                                    String $$48 = $$1 ? "false" : ChunkPalettedStorageFix.getProperty($$43, "powered");
                                    this.setBlock(
                                       $$39, (Dynamic<?>)ChunkPalettedStorageFix.MappingConstants.DOOR_MAP.get($$44 + $$45 + "lower" + $$47 + $$46 + $$48)
                                    );
                                    this.setBlock(
                                       $$42, (Dynamic<?>)ChunkPalettedStorageFix.MappingConstants.DOOR_MAP.get($$44 + $$45 + "upper" + $$47 + $$46 + $$48)
                                    );
                                 }
                              }
                           }
                        }
                        break;
                     case 86:
                        for(int $$27 : (IntList)$$3.getValue()) {
                           $$27 |= $$4;
                           Dynamic<?> $$28 = this.getBlock($$27);
                           if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName($$28))) {
                              String $$29 = ChunkPalettedStorageFix.getName(this.getBlock(relative($$27, ChunkPalettedStorageFix.Direction.DOWN)));
                              if ("minecraft:grass_block".equals($$29) || "minecraft:dirt".equals($$29)) {
                                 this.setBlock($$27, ChunkPalettedStorageFix.MappingConstants.PUMPKIN);
                              }
                           }
                        }
                        break;
                     case 110:
                        for(int $$11 : (IntList)$$3.getValue()) {
                           $$11 |= $$4;
                           Dynamic<?> $$12 = this.getBlock($$11);
                           if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName($$12))) {
                              String $$13 = ChunkPalettedStorageFix.getName(this.getBlock(relative($$11, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals($$13) || "minecraft:snow_layer".equals($$13)) {
                                 this.setBlock($$11, ChunkPalettedStorageFix.MappingConstants.SNOWY_MYCELIUM);
                              }
                           }
                        }
                        break;
                     case 140:
                        for(int $$30 : (IntList)$$3.getValue()) {
                           $$30 |= $$4;
                           Dynamic<?> $$31 = this.removeBlockEntity($$30);
                           if ($$31 != null) {
                              String $$32 = $$31.get("Item").asString("") + $$31.get("Data").asInt(0);
                              this.setBlock(
                                 $$30,
                                 (Dynamic<?>)ChunkPalettedStorageFix.MappingConstants.FLOWER_POT_MAP
                                    .getOrDefault($$32, (Dynamic)ChunkPalettedStorageFix.MappingConstants.FLOWER_POT_MAP.get("minecraft:air0"))
                              );
                           }
                        }
                        break;
                     case 144:
                        for(int $$33 : (IntList)$$3.getValue()) {
                           $$33 |= $$4;
                           Dynamic<?> $$34 = this.getBlockEntity($$33);
                           if ($$34 != null) {
                              String $$35 = String.valueOf($$34.get("SkullType").asInt(0));
                              String $$36 = ChunkPalettedStorageFix.getProperty(this.getBlock($$33), "facing");
                              String $$38;
                              if (!"up".equals($$36) && !"down".equals($$36)) {
                                 $$38 = $$35 + $$36;
                              } else {
                                 $$38 = $$35 + $$34.get("Rot").asInt(0);
                              }

                              $$34.remove("SkullType");
                              $$34.remove("facing");
                              $$34.remove("Rot");
                              this.setBlock(
                                 $$33,
                                 (Dynamic<?>)ChunkPalettedStorageFix.MappingConstants.SKULL_MAP
                                    .getOrDefault($$38, (Dynamic)ChunkPalettedStorageFix.MappingConstants.SKULL_MAP.get("0north"))
                              );
                           }
                        }
                        break;
                     case 175:
                        for(int $$49 : (IntList)$$3.getValue()) {
                           $$49 |= $$4;
                           Dynamic<?> $$50 = this.getBlock($$49);
                           if ("upper".equals(ChunkPalettedStorageFix.getProperty($$50, "half"))) {
                              Dynamic<?> $$51 = this.getBlock(relative($$49, ChunkPalettedStorageFix.Direction.DOWN));
                              String $$52 = ChunkPalettedStorageFix.getName($$51);
                              switch($$52) {
                                 case "minecraft:sunflower":
                                    this.setBlock($$49, ChunkPalettedStorageFix.MappingConstants.UPPER_SUNFLOWER);
                                    break;
                                 case "minecraft:lilac":
                                    this.setBlock($$49, ChunkPalettedStorageFix.MappingConstants.UPPER_LILAC);
                                    break;
                                 case "minecraft:tall_grass":
                                    this.setBlock($$49, ChunkPalettedStorageFix.MappingConstants.UPPER_TALL_GRASS);
                                    break;
                                 case "minecraft:large_fern":
                                    this.setBlock($$49, ChunkPalettedStorageFix.MappingConstants.UPPER_LARGE_FERN);
                                    break;
                                 case "minecraft:rose_bush":
                                    this.setBlock($$49, ChunkPalettedStorageFix.MappingConstants.UPPER_ROSE_BUSH);
                                    break;
                                 case "minecraft:peony":
                                    this.setBlock($$49, ChunkPalettedStorageFix.MappingConstants.UPPER_PEONY);
                              }
                           }
                        }
                        break;
                     case 176:
                     case 177:
                        for(int $$22 : (IntList)$$3.getValue()) {
                           $$22 |= $$4;
                           Dynamic<?> $$23 = this.getBlockEntity($$22);
                           Dynamic<?> $$24 = this.getBlock($$22);
                           if ($$23 != null) {
                              int $$25 = $$23.get("Base").asInt(0);
                              if ($$25 != 15 && $$25 >= 0 && $$25 < 16) {
                                 String $$26 = ChunkPalettedStorageFix.getProperty($$24, $$3.getIntKey() == 176 ? "rotation" : "facing") + "_" + $$25;
                                 if (ChunkPalettedStorageFix.MappingConstants.BANNER_BLOCK_MAP.containsKey($$26)) {
                                    this.setBlock($$22, (Dynamic<?>)ChunkPalettedStorageFix.MappingConstants.BANNER_BLOCK_MAP.get($$26));
                                 }
                              }
                           }
                        }
                  }
               }
            }
         }
      }

      @Nullable
      private Dynamic<?> getBlockEntity(int $$0) {
         return this.blockEntities.get($$0);
      }

      @Nullable
      private Dynamic<?> removeBlockEntity(int $$0) {
         return this.blockEntities.remove($$0);
      }

      public static int relative(int $$0, ChunkPalettedStorageFix.Direction $$1) {
         int var10000;
         switch($$1.getAxis().ordinal()) {
            case 0:
               int $$2 = ($$0 & 15) + $$1.getAxisDirection().getStep();
               var10000 = $$2 >= 0 && $$2 <= 15 ? $$0 & -16 | $$2 : -1;
               break;
            case 1:
               int $$3 = ($$0 >> 8) + $$1.getAxisDirection().getStep();
               var10000 = $$3 >= 0 && $$3 <= 255 ? $$0 & 0xFF | $$3 << 8 : -1;
               break;
            case 2:
               int $$4 = ($$0 >> 4 & 15) + $$1.getAxisDirection().getStep();
               var10000 = $$4 >= 0 && $$4 <= 15 ? $$0 & -241 | $$4 << 4 : -1;
               break;
            default:
               throw new MatchException(null, null);
         }

         return var10000;
      }

      private void setBlock(int $$0, Dynamic<?> $$1) {
         if ($$0 >= 0 && $$0 <= 65535) {
            ChunkPalettedStorageFix.Section $$2 = this.getSection($$0);
            if ($$2 != null) {
               $$2.setBlock($$0 & 4095, $$1);
            }
         }
      }

      @Nullable
      private ChunkPalettedStorageFix.Section getSection(int $$0) {
         int $$1 = $$0 >> 12;
         return $$1 < this.sections.length ? this.sections[$$1] : null;
      }

      public Dynamic<?> getBlock(int $$0) {
         if ($$0 >= 0 && $$0 <= 65535) {
            ChunkPalettedStorageFix.Section $$1 = this.getSection($$0);
            return $$1 == null ? ChunkPalettedStorageFix.MappingConstants.AIR : $$1.getBlock($$0 & 4095);
         } else {
            return ChunkPalettedStorageFix.MappingConstants.AIR;
         }
      }

      public Dynamic<?> write() {
         Dynamic<?> $$0 = this.level;
         if (this.blockEntities.isEmpty()) {
            $$0 = $$0.remove("TileEntities");
         } else {
            $$0 = $$0.set("TileEntities", $$0.createList(this.blockEntities.values().stream()));
         }

         Dynamic<?> $$1 = $$0.emptyMap();
         List<Dynamic<?>> $$2 = Lists.<Dynamic<?>>newArrayList();

         for(ChunkPalettedStorageFix.Section $$3 : this.sections) {
            if ($$3 != null) {
               $$2.add($$3.write());
               $$1 = $$1.set(String.valueOf($$3.y), $$1.createIntList(Arrays.stream($$3.update.toIntArray())));
            }
         }

         Dynamic<?> $$4 = $$0.emptyMap();
         $$4 = $$4.set("Sides", $$4.createByte((byte)this.sides));
         $$4 = $$4.set("Indices", $$1);
         return $$0.set("UpgradeData", $$4).set("Sections", $$4.createList($$2.stream()));
      }
   }
}
