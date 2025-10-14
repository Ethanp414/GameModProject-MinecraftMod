package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LegacyStructureDataHandler {
   private static final Map<String, String> CURRENT_TO_LEGACY_MAP = Util.make(Maps.newHashMap(), $$0 -> {
      $$0.put("Village", "Village");
      $$0.put("Mineshaft", "Mineshaft");
      $$0.put("Mansion", "Mansion");
      $$0.put("Igloo", "Temple");
      $$0.put("Desert_Pyramid", "Temple");
      $$0.put("Jungle_Pyramid", "Temple");
      $$0.put("Swamp_Hut", "Temple");
      $$0.put("Stronghold", "Stronghold");
      $$0.put("Monument", "Monument");
      $$0.put("Fortress", "Fortress");
      $$0.put("EndCity", "EndCity");
   });
   private static final Map<String, String> LEGACY_TO_CURRENT_MAP = Util.make(Maps.newHashMap(), $$0 -> {
      $$0.put("Iglu", "Igloo");
      $$0.put("TeDP", "Desert_Pyramid");
      $$0.put("TeJP", "Jungle_Pyramid");
      $$0.put("TeSH", "Swamp_Hut");
   });
   private static final Set<String> OLD_STRUCTURE_REGISTRY_KEYS = Set.of(
      "pillager_outpost",
      "mineshaft",
      "mansion",
      "jungle_pyramid",
      "desert_pyramid",
      "igloo",
      "ruined_portal",
      "shipwreck",
      "swamp_hut",
      "stronghold",
      "monument",
      "ocean_ruin",
      "fortress",
      "endcity",
      "buried_treasure",
      "village",
      "nether_fossil",
      "bastion_remnant"
   );
   private final boolean hasLegacyData;
   private final Map<String, Long2ObjectMap<CompoundTag>> dataMap = Maps.newHashMap();
   private final Map<String, StructureFeatureIndexSavedData> indexMap = Maps.newHashMap();
   private final List<String> legacyKeys;
   private final List<String> currentKeys;

   public LegacyStructureDataHandler(@Nullable DimensionDataStorage $$0, List<String> $$1, List<String> $$2) {
      this.legacyKeys = $$1;
      this.currentKeys = $$2;
      this.populateCaches($$0);
      boolean $$3 = false;

      for(String $$4 : this.currentKeys) {
         $$3 |= this.dataMap.get($$4) != null;
      }

      this.hasLegacyData = $$3;
   }

   public void removeIndex(long $$0) {
      for(String $$1 : this.legacyKeys) {
         StructureFeatureIndexSavedData $$2 = (StructureFeatureIndexSavedData)this.indexMap.get($$1);
         if ($$2 != null && $$2.hasUnhandledIndex($$0)) {
            $$2.removeIndex($$0);
         }
      }
   }

   public CompoundTag updateFromLegacy(CompoundTag $$0) {
      CompoundTag $$1 = $$0.getCompoundOrEmpty("Level");
      ChunkPos $$2 = new ChunkPos($$1.getIntOr("xPos", 0), $$1.getIntOr("zPos", 0));
      if (this.isUnhandledStructureStart($$2.x, $$2.z)) {
         $$0 = this.updateStructureStart($$0, $$2);
      }

      CompoundTag $$3 = $$1.getCompoundOrEmpty("Structures");
      CompoundTag $$4 = $$3.getCompoundOrEmpty("References");

      for(String $$5 : this.currentKeys) {
         boolean $$6 = OLD_STRUCTURE_REGISTRY_KEYS.contains($$5.toLowerCase(Locale.ROOT));
         if (!$$4.getLongArray($$5).isPresent() && $$6) {
            int $$7 = 8;
            LongList $$8 = new LongArrayList();

            for(int $$9 = $$2.x - 8; $$9 <= $$2.x + 8; ++$$9) {
               for(int $$10 = $$2.z - 8; $$10 <= $$2.z + 8; ++$$10) {
                  if (this.hasLegacyStart($$9, $$10, $$5)) {
                     $$8.add(ChunkPos.asLong($$9, $$10));
                  }
               }
            }

            $$4.putLongArray($$5, $$8.toLongArray());
         }
      }

      $$3.put("References", $$4);
      $$1.put("Structures", $$3);
      $$0.put("Level", $$1);
      return $$0;
   }

   private boolean hasLegacyStart(int $$0, int $$1, String $$2) {
      if (!this.hasLegacyData) {
         return false;
      } else {
         return this.dataMap.get($$2) != null
            && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get($$2))).hasStartIndex(ChunkPos.asLong($$0, $$1));
      }
   }

   private boolean isUnhandledStructureStart(int $$0, int $$1) {
      if (!this.hasLegacyData) {
         return false;
      } else {
         for(String $$2 : this.currentKeys) {
            if (this.dataMap.get($$2) != null
               && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get($$2))).hasUnhandledIndex(ChunkPos.asLong($$0, $$1))) {
               return true;
            }
         }

         return false;
      }
   }

   private CompoundTag updateStructureStart(CompoundTag $$0, ChunkPos $$1) {
      CompoundTag $$2 = $$0.getCompoundOrEmpty("Level");
      CompoundTag $$3 = $$2.getCompoundOrEmpty("Structures");
      CompoundTag $$4 = $$3.getCompoundOrEmpty("Starts");

      for(String $$5 : this.currentKeys) {
         Long2ObjectMap<CompoundTag> $$6 = (Long2ObjectMap)this.dataMap.get($$5);
         if ($$6 != null) {
            long $$7 = $$1.toLong();
            if (((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get($$5))).hasUnhandledIndex($$7)) {
               CompoundTag $$8 = $$6.get($$7);
               if ($$8 != null) {
                  $$4.put($$5, $$8);
               }
            }
         }
      }

      $$3.put("Starts", $$4);
      $$2.put("Structures", $$3);
      $$0.put("Level", $$2);
      return $$0;
   }

   private void populateCaches(@Nullable DimensionDataStorage $$0) {
      if ($$0 != null) {
         for(String $$1 : this.legacyKeys) {
            CompoundTag $$2 = new CompoundTag();

            try {
               $$2 = $$0.readTagFromDisk($$1, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES, 1493)
                  .getCompoundOrEmpty("data")
                  .getCompoundOrEmpty("Features");
               if ($$2.isEmpty()) {
                  continue;
               }
            } catch (IOException var8) {
            }

            $$2.forEach(
               ($$0x, $$1x) -> {
                  if ($$1x instanceof CompoundTag $$2xx) {
                     long $$4xx = ChunkPos.asLong($$2xx.getIntOr("ChunkX", 0), $$2xx.getIntOr("ChunkZ", 0));
                     ListTag $$5xx = $$2xx.getListOrEmpty("Children");
                     if (!$$5xx.isEmpty()) {
                        Optional<String> $$6 = $$5xx.getCompound(0).flatMap($$0xx -> $$0xx.getString("id"));
                        $$6.map(LEGACY_TO_CURRENT_MAP::get).ifPresent($$1xx -> $$2x.putString("id", $$1xx));
                     }
   
                     $$2xx.getString("id")
                        .ifPresent($$2xx -> ((Long2ObjectMap)this.dataMap.computeIfAbsent($$2xx, $$0xxx -> new Long2ObjectOpenHashMap())).put($$4x, $$2x));
                  }
               }
            );
            String $$3 = $$1 + "_index";
            StructureFeatureIndexSavedData $$4 = $$0.computeIfAbsent(StructureFeatureIndexSavedData.type($$3));
            if ($$4.getAll().isEmpty()) {
               StructureFeatureIndexSavedData $$5 = new StructureFeatureIndexSavedData();
               this.indexMap.put($$1, $$5);
               $$2.forEach(($$1x, $$2x) -> {
                  if ($$2x instanceof CompoundTag $$3xx) {
                     $$5.addIndex(ChunkPos.asLong($$3xx.getIntOr("ChunkX", 0), $$3xx.getIntOr("ChunkZ", 0)));
                  }
               });
            } else {
               this.indexMap.put($$1, $$4);
            }
         }
      }
   }

   public static LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> $$0, @Nullable DimensionDataStorage $$1) {
      if ($$0 == Level.OVERWORLD) {
         return new LegacyStructureDataHandler(
            $$1,
            ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"),
            ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument")
         );
      } else if ($$0 == Level.NETHER) {
         List<String> $$2 = ImmutableList.of("Fortress");
         return new LegacyStructureDataHandler($$1, $$2, $$2);
      } else if ($$0 == Level.END) {
         List<String> $$3 = ImmutableList.of("EndCity");
         return new LegacyStructureDataHandler($$1, $$3, $$3);
      } else {
         throw new RuntimeException(String.format(Locale.ROOT, "Unknown dimension type : %s", $$0));
      }
   }
}
