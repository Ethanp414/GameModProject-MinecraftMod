package net.minecraft.world.entity.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;

public class Raids extends SavedData {
   private static final String RAID_FILE_ID = "raids";
   public static final Codec<Raids> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               Raids.RaidWithId.CODEC
                  .listOf()
                  .optionalFieldOf("raids", List.of())
                  .forGetter($$0x -> $$0x.raidMap.int2ObjectEntrySet().stream().map(Raids.RaidWithId::from).toList()),
               Codec.INT.fieldOf("next_id").forGetter($$0x -> $$0x.nextId),
               Codec.INT.fieldOf("tick").forGetter($$0x -> $$0x.tick)
            )
            .apply($$0, Raids::new)
   );
   public static final SavedDataType<Raids> TYPE = new SavedDataType<>("raids", Raids::new, CODEC, DataFixTypes.SAVED_DATA_RAIDS);
   public static final SavedDataType<Raids> TYPE_END = new SavedDataType<>("raids_end", Raids::new, CODEC, DataFixTypes.SAVED_DATA_RAIDS);
   private final Int2ObjectMap<Raid> raidMap = new Int2ObjectOpenHashMap<>();
   private int nextId = 1;
   private int tick;

   public static SavedDataType<Raids> getType(Holder<DimensionType> $$0) {
      return $$0.is(BuiltinDimensionTypes.END) ? TYPE_END : TYPE;
   }

   public Raids() {
      this.setDirty();
   }

   private Raids(List<Raids.RaidWithId> $$0, int $$1, int $$2) {
      for(Raids.RaidWithId $$3 : $$0) {
         this.raidMap.put($$3.id, $$3.raid);
      }

      this.nextId = $$1;
      this.tick = $$2;
   }

   @Nullable
   public Raid get(int $$0) {
      return this.raidMap.get($$0);
   }

   public OptionalInt getId(Raid $$0) {
      for(Entry<Raid> $$1 : this.raidMap.int2ObjectEntrySet()) {
         if ($$1.getValue() == $$0) {
            return OptionalInt.of($$1.getIntKey());
         }
      }

      return OptionalInt.empty();
   }

   public void tick(ServerLevel $$0) {
      ++this.tick;
      Iterator<Raid> $$1 = this.raidMap.values().iterator();

      while($$1.hasNext()) {
         Raid $$2 = (Raid)$$1.next();
         if ($$0.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            $$2.stop();
         }

         if ($$2.isStopped()) {
            $$1.remove();
            this.setDirty();
         } else {
            $$2.tick($$0);
         }
      }

      if (this.tick % 200 == 0) {
         this.setDirty();
      }
   }

   public static boolean canJoinRaid(Raider $$0) {
      return $$0.isAlive() && $$0.canJoinRaid() && $$0.getNoActionTime() <= 2400;
   }

   @Nullable
   public Raid createOrExtendRaid(ServerPlayer $$0, BlockPos $$1) {
      if ($$0.isSpectator()) {
         return null;
      } else {
         ServerLevel $$2 = $$0.level();
         if ($$2.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
         } else {
            DimensionType $$3 = $$2.dimensionType();
            if (!$$3.hasRaids()) {
               return null;
            } else {
               List<PoiRecord> $$4 = $$2.getPoiManager().getInRange($$0x -> $$0x.is(PoiTypeTags.VILLAGE), $$1, 64, PoiManager.Occupancy.IS_OCCUPIED).toList();
               int $$5 = 0;
               Vec3 $$6 = Vec3.ZERO;

               for(PoiRecord $$7 : $$4) {
                  BlockPos $$8 = $$7.getPos();
                  $$6 = $$6.add((double)$$8.getX(), (double)$$8.getY(), (double)$$8.getZ());
                  ++$$5;
               }

               BlockPos $$9;
               if ($$5 > 0) {
                  $$6 = $$6.scale(1.0 / (double)$$5);
                  $$9 = BlockPos.containing($$6);
               } else {
                  $$9 = $$1;
               }

               Raid $$11 = this.getOrCreateRaid($$2, $$9);
               if (!$$11.isStarted() && !this.raidMap.containsValue($$11)) {
                  this.raidMap.put(this.getUniqueId(), $$11);
               }

               if (!$$11.isStarted() || $$11.getRaidOmenLevel() < $$11.getMaxRaidOmenLevel()) {
                  $$11.absorbRaidOmen($$0);
               }

               this.setDirty();
               return $$11;
            }
         }
      }
   }

   private Raid getOrCreateRaid(ServerLevel $$0, BlockPos $$1) {
      Raid $$2 = $$0.getRaidAt($$1);
      return $$2 != null ? $$2 : new Raid($$1, $$0.getDifficulty());
   }

   public static Raids load(CompoundTag $$0) {
      return (Raids)CODEC.parse(NbtOps.INSTANCE, $$0).resultOrPartial().orElseGet(Raids::new);
   }

   private int getUniqueId() {
      return ++this.nextId;
   }

   @Nullable
   public Raid getNearbyRaid(BlockPos $$0, int $$1) {
      Raid $$2 = null;
      double $$3 = (double)$$1;

      for(Raid $$4 : this.raidMap.values()) {
         double $$5 = $$4.getCenter().distSqr($$0);
         if ($$4.isActive() && $$5 < $$3) {
            $$2 = $$4;
            $$3 = $$5;
         }
      }

      return $$2;
   }

   @VisibleForDebug
   public List<BlockPos> getRaidCentersInChunk(ChunkPos $$0) {
      return this.raidMap.values().stream().map(Raid::getCenter).filter($$0::contains).toList();
   }

   static record RaidWithId(int id, Raid raid) {
      final int id;
      final Raid raid;
      public static final Codec<Raids.RaidWithId> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(Codec.INT.fieldOf("id").forGetter(Raids.RaidWithId::id), Raid.MAP_CODEC.forGetter(Raids.RaidWithId::raid))
               .apply($$0, Raids.RaidWithId::new)
      );

      public static Raids.RaidWithId from(Entry<Raid> $$0) {
         return new Raids.RaidWithId($$0.getIntKey(), (Raid)$$0.getValue());
      }
   }
}
