package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public final class NaturalSpawner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MIN_SPAWN_DISTANCE = 24;
   public static final int SPAWN_DISTANCE_CHUNK = 8;
   public static final int SPAWN_DISTANCE_BLOCK = 128;
   public static final int INSCRIBED_SQUARE_SPAWN_DISTANCE_CHUNK = Mth.floor(8.0F / Mth.SQRT_OF_TWO);
   static final int MAGIC_NUMBER = (int)Math.pow(17.0, 2.0);
   private static final MobCategory[] SPAWNING_CATEGORIES = (MobCategory[])Stream.of(MobCategory.values())
      .filter($$0 -> $$0 != MobCategory.MISC)
      .toArray($$0 -> new MobCategory[$$0]);

   private NaturalSpawner() {
   }

   public static NaturalSpawner.SpawnState createState(int $$0, Iterable<Entity> $$1, NaturalSpawner.ChunkGetter $$2, LocalMobCapCalculator $$3) {
      PotentialCalculator $$4 = new PotentialCalculator();
      Object2IntOpenHashMap<MobCategory> $$5 = new Object2IntOpenHashMap<>();

      for(Entity $$6 : $$1) {
         if ($$6 instanceof Mob $$7 && ($$7.isPersistenceRequired() || $$7.requiresCustomPersistence())) {
            continue;
         }

         MobCategory $$8 = $$6.getType().getCategory();
         if ($$8 != MobCategory.MISC) {
            BlockPos $$9 = $$6.blockPosition();
            $$2.query(ChunkPos.asLong($$9), $$6x -> {
               MobSpawnSettings.MobSpawnCost $$7 = getRoughBiome($$9, $$6x).getMobSettings().getMobSpawnCost($$6.getType());
               if ($$7 != null) {
                  $$4.addCharge($$6.blockPosition(), $$7.charge());
               }

               if ($$6 instanceof Mob) {
                  $$3.addMob($$6x.getPos(), $$8);
               }

               $$5.addTo($$8, 1);
            });
         }
      }

      return new NaturalSpawner.SpawnState($$0, $$5, $$4, $$3);
   }

   static Biome getRoughBiome(BlockPos $$0, ChunkAccess $$1) {
      return $$1.getNoiseBiome(QuartPos.fromBlock($$0.getX()), QuartPos.fromBlock($$0.getY()), QuartPos.fromBlock($$0.getZ())).value();
   }

   public static List<MobCategory> getFilteredSpawningCategories(NaturalSpawner.SpawnState $$0, boolean $$1, boolean $$2, boolean $$3) {
      List<MobCategory> $$4 = new ArrayList(SPAWNING_CATEGORIES.length);

      for(MobCategory $$5 : SPAWNING_CATEGORIES) {
         if (($$1 || !$$5.isFriendly()) && ($$2 || $$5.isFriendly()) && ($$3 || !$$5.isPersistent()) && $$0.canSpawnForCategoryGlobal($$5)) {
            $$4.add($$5);
         }
      }

      return $$4;
   }

   public static void spawnForChunk(ServerLevel $$0, LevelChunk $$1, NaturalSpawner.SpawnState $$2, List<MobCategory> $$3) {
      ProfilerFiller $$4 = Profiler.get();
      $$4.push("spawner");

      for(MobCategory $$5 : $$3) {
         if ($$2.canSpawnForCategoryLocal($$5, $$1.getPos())) {
            spawnCategoryForChunk($$5, $$0, $$1, $$2::canSpawn, $$2::afterSpawn);
         }
      }

      $$4.pop();
   }

   public static void spawnCategoryForChunk(
      MobCategory $$0, ServerLevel $$1, LevelChunk $$2, NaturalSpawner.SpawnPredicate $$3, NaturalSpawner.AfterSpawnCallback $$4
   ) {
      BlockPos $$5 = getRandomPosWithin($$1, $$2);
      if ($$5.getY() >= $$1.getMinY() + 1) {
         spawnCategoryForPosition($$0, $$1, $$2, $$5, $$3, $$4);
      }
   }

   @VisibleForDebug
   public static void spawnCategoryForPosition(MobCategory $$0, ServerLevel $$1, BlockPos $$2) {
      spawnCategoryForPosition($$0, $$1, $$1.getChunk($$2), $$2, ($$0x, $$1x, $$2x) -> true, ($$0x, $$1x) -> {
      });
   }

   public static void spawnCategoryForPosition(
      MobCategory $$0, ServerLevel $$1, ChunkAccess $$2, BlockPos $$3, NaturalSpawner.SpawnPredicate $$4, NaturalSpawner.AfterSpawnCallback $$5
   ) {
      StructureManager $$6 = $$1.structureManager();
      ChunkGenerator $$7 = $$1.getChunkSource().getGenerator();
      int $$8 = $$3.getY();
      BlockState $$9 = $$2.getBlockState($$3);
      if (!$$9.isRedstoneConductor($$2, $$3)) {
         BlockPos.MutableBlockPos $$10 = new BlockPos.MutableBlockPos();
         int $$11 = 0;

         for(int $$12 = 0; $$12 < 3; ++$$12) {
            int $$13 = $$3.getX();
            int $$14 = $$3.getZ();
            int $$15 = 6;
            MobSpawnSettings.SpawnerData $$16 = null;
            SpawnGroupData $$17 = null;
            int $$18 = Mth.ceil($$1.random.nextFloat() * 4.0F);
            int $$19 = 0;

            for(int $$20 = 0; $$20 < $$18; ++$$20) {
               $$13 += $$1.random.nextInt(6) - $$1.random.nextInt(6);
               $$14 += $$1.random.nextInt(6) - $$1.random.nextInt(6);
               $$10.set($$13, $$8, $$14);
               double $$21 = (double)$$13 + 0.5;
               double $$22 = (double)$$14 + 0.5;
               Player $$23 = $$1.getNearestPlayer($$21, (double)$$8, $$22, -1.0, false);
               if ($$23 != null) {
                  double $$24 = $$23.distanceToSqr($$21, (double)$$8, $$22);
                  if (isRightDistanceToPlayerAndSpawnPoint($$1, $$2, $$10, $$24)) {
                     if ($$16 == null) {
                        Optional<MobSpawnSettings.SpawnerData> $$25 = getRandomSpawnMobAt($$1, $$6, $$7, $$0, $$1.random, $$10);
                        if ($$25.isEmpty()) {
                           break;
                        }

                        $$16 = (MobSpawnSettings.SpawnerData)$$25.get();
                        $$18 = $$16.minCount() + $$1.random.nextInt(1 + $$16.maxCount() - $$16.minCount());
                     }

                     if (isValidSpawnPostitionForType($$1, $$0, $$6, $$7, $$16, $$10, $$24) && $$4.test($$16.type(), $$10, $$2)) {
                        Mob $$26 = getMobForSpawn($$1, $$16.type());
                        if ($$26 == null) {
                           return;
                        }

                        $$26.snapTo($$21, (double)$$8, $$22, $$1.random.nextFloat() * 360.0F, 0.0F);
                        if (isValidPositionForMob($$1, $$26, $$24)) {
                           $$17 = $$26.finalizeSpawn($$1, $$1.getCurrentDifficultyAt($$26.blockPosition()), EntitySpawnReason.NATURAL, $$17);
                           ++$$11;
                           ++$$19;
                           $$1.addFreshEntityWithPassengers($$26);
                           $$5.run($$26, $$2);
                           if ($$11 >= $$26.getMaxSpawnClusterSize()) {
                              return;
                           }

                           if ($$26.isMaxGroupSizeReached($$19)) {
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel $$0, ChunkAccess $$1, BlockPos.MutableBlockPos $$2, double $$3) {
      if ($$3 <= 576.0) {
         return false;
      } else {
         LevelData.RespawnData $$4 = $$0.getRespawnData();
         if ($$4.dimension() == $$0.dimension()
            && $$4.pos().closerToCenterThan(new Vec3((double)$$2.getX() + 0.5, (double)$$2.getY(), (double)$$2.getZ() + 0.5), 24.0)) {
            return false;
         } else {
            ChunkPos $$5 = new ChunkPos($$2);
            return Objects.equals($$5, $$1.getPos()) || $$0.canSpawnEntitiesInChunk($$5);
         }
      }
   }

   private static boolean isValidSpawnPostitionForType(
      ServerLevel $$0, MobCategory $$1, StructureManager $$2, ChunkGenerator $$3, MobSpawnSettings.SpawnerData $$4, BlockPos.MutableBlockPos $$5, double $$6
   ) {
      EntityType<?> $$7 = $$4.type();
      if ($$7.getCategory() == MobCategory.MISC) {
         return false;
      } else if (!$$7.canSpawnFarFromPlayer() && $$6 > (double)($$7.getCategory().getDespawnDistance() * $$7.getCategory().getDespawnDistance())) {
         return false;
      } else if (!$$7.canSummon() || !canSpawnMobAt($$0, $$2, $$3, $$1, $$4, $$5)) {
         return false;
      } else if (!SpawnPlacements.isSpawnPositionOk($$7, $$0, $$5)) {
         return false;
      } else if (!SpawnPlacements.checkSpawnRules($$7, $$0, EntitySpawnReason.NATURAL, $$5, $$0.random)) {
         return false;
      } else {
         return $$0.noCollision($$7.getSpawnAABB((double)$$5.getX() + 0.5, (double)$$5.getY(), (double)$$5.getZ() + 0.5));
      }
   }

   @Nullable
   private static Mob getMobForSpawn(ServerLevel $$0, EntityType<?> $$1) {
      try {
         Entity var3 = $$1.create($$0, EntitySpawnReason.NATURAL);
         if (var3 instanceof Mob) {
            return (Mob)var3;
         }

         LOGGER.warn("Can't spawn entity of type: {}", BuiltInRegistries.ENTITY_TYPE.getKey($$1));
      } catch (Exception var4) {
         LOGGER.warn("Failed to create mob", var4);
      }

      return null;
   }

   private static boolean isValidPositionForMob(ServerLevel $$0, Mob $$1, double $$2) {
      if ($$2 > (double)($$1.getType().getCategory().getDespawnDistance() * $$1.getType().getCategory().getDespawnDistance()) && $$1.removeWhenFarAway($$2)) {
         return false;
      } else {
         return $$1.checkSpawnRules($$0, EntitySpawnReason.NATURAL) && $$1.checkSpawnObstruction($$0);
      }
   }

   private static Optional<MobSpawnSettings.SpawnerData> getRandomSpawnMobAt(
      ServerLevel $$0, StructureManager $$1, ChunkGenerator $$2, MobCategory $$3, RandomSource $$4, BlockPos $$5
   ) {
      Holder<Biome> $$6 = $$0.getBiome($$5);
      return $$3 == MobCategory.WATER_AMBIENT && $$6.is(BiomeTags.REDUCED_WATER_AMBIENT_SPAWNS) && $$4.nextFloat() < 0.98F
         ? Optional.empty()
         : mobsAt($$0, $$1, $$2, $$3, $$5, $$6).getRandom($$4);
   }

   private static boolean canSpawnMobAt(
      ServerLevel $$0, StructureManager $$1, ChunkGenerator $$2, MobCategory $$3, MobSpawnSettings.SpawnerData $$4, BlockPos $$5
   ) {
      return mobsAt($$0, $$1, $$2, $$3, $$5, null).contains($$4);
   }

   private static WeightedList<MobSpawnSettings.SpawnerData> mobsAt(
      ServerLevel $$0, StructureManager $$1, ChunkGenerator $$2, MobCategory $$3, BlockPos $$4, @Nullable Holder<Biome> $$5
   ) {
      return isInNetherFortressBounds($$4, $$0, $$3, $$1)
         ? NetherFortressStructure.FORTRESS_ENEMIES
         : $$2.getMobsAt($$5 != null ? $$5 : $$0.getBiome($$4), $$1, $$3, $$4);
   }

   public static boolean isInNetherFortressBounds(BlockPos $$0, ServerLevel $$1, MobCategory $$2, StructureManager $$3) {
      if ($$2 == MobCategory.MONSTER && $$1.getBlockState($$0.below()).is(Blocks.NETHER_BRICKS)) {
         Structure $$4 = $$3.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(BuiltinStructures.FORTRESS);
         return $$4 == null ? false : $$3.getStructureAt($$0, $$4).isValid();
      } else {
         return false;
      }
   }

   private static BlockPos getRandomPosWithin(Level $$0, LevelChunk $$1) {
      ChunkPos $$2 = $$1.getPos();
      int $$3 = $$2.getMinBlockX() + $$0.random.nextInt(16);
      int $$4 = $$2.getMinBlockZ() + $$0.random.nextInt(16);
      int $$5 = $$1.getHeight(Heightmap.Types.WORLD_SURFACE, $$3, $$4) + 1;
      int $$6 = Mth.randomBetweenInclusive($$0.random, $$0.getMinY(), $$5);
      return new BlockPos($$3, $$6, $$4);
   }

   public static boolean isValidEmptySpawnBlock(BlockGetter $$0, BlockPos $$1, BlockState $$2, FluidState $$3, EntityType<?> $$4) {
      if ($$2.isCollisionShapeFullBlock($$0, $$1)) {
         return false;
      } else if ($$2.isSignalSource()) {
         return false;
      } else if (!$$3.isEmpty()) {
         return false;
      } else if ($$2.is(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
         return false;
      } else {
         return !$$4.isBlockDangerous($$2);
      }
   }

   public static void spawnMobsForChunkGeneration(ServerLevelAccessor $$0, Holder<Biome> $$1, ChunkPos $$2, RandomSource $$3) {
      MobSpawnSettings $$4 = $$1.value().getMobSettings();
      WeightedList<MobSpawnSettings.SpawnerData> $$5 = $$4.getMobs(MobCategory.CREATURE);
      if (!$$5.isEmpty()) {
         int $$6 = $$2.getMinBlockX();
         int $$7 = $$2.getMinBlockZ();

         while($$3.nextFloat() < $$4.getCreatureProbability()) {
            Optional<MobSpawnSettings.SpawnerData> $$8 = $$5.getRandom($$3);
            if (!$$8.isEmpty()) {
               MobSpawnSettings.SpawnerData $$9 = (MobSpawnSettings.SpawnerData)$$8.get();
               int $$10 = $$9.minCount() + $$3.nextInt(1 + $$9.maxCount() - $$9.minCount());
               SpawnGroupData $$11 = null;
               int $$12 = $$6 + $$3.nextInt(16);
               int $$13 = $$7 + $$3.nextInt(16);
               int $$14 = $$12;
               int $$15 = $$13;

               for(int $$16 = 0; $$16 < $$10; ++$$16) {
                  boolean $$17 = false;

                  for(int $$18 = 0; !$$17 && $$18 < 4; ++$$18) {
                     BlockPos $$19 = getTopNonCollidingPos($$0, $$9.type(), $$12, $$13);
                     if ($$9.type().canSummon() && SpawnPlacements.isSpawnPositionOk($$9.type(), $$0, $$19)) {
                        float $$20 = $$9.type().getWidth();
                        double $$21 = Mth.clamp((double)$$12, (double)$$6 + (double)$$20, (double)$$6 + 16.0 - (double)$$20);
                        double $$22 = Mth.clamp((double)$$13, (double)$$7 + (double)$$20, (double)$$7 + 16.0 - (double)$$20);
                        if (!$$0.noCollision($$9.type().getSpawnAABB($$21, (double)$$19.getY(), $$22))
                           || !SpawnPlacements.checkSpawnRules(
                              $$9.type(), $$0, EntitySpawnReason.CHUNK_GENERATION, BlockPos.containing($$21, (double)$$19.getY(), $$22), $$0.getRandom()
                           )) {
                           continue;
                        }

                        Entity $$23;
                        try {
                           $$23 = $$9.type().create($$0.getLevel(), EntitySpawnReason.NATURAL);
                        } catch (Exception var27) {
                           LOGGER.warn("Failed to create mob", var27);
                           continue;
                        }

                        if ($$23 == null) {
                           continue;
                        }

                        $$23.snapTo($$21, (double)$$19.getY(), $$22, $$3.nextFloat() * 360.0F, 0.0F);
                        if ($$23 instanceof Mob $$26 && $$26.checkSpawnRules($$0, EntitySpawnReason.CHUNK_GENERATION) && $$26.checkSpawnObstruction($$0)) {
                           $$11 = $$26.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$26.blockPosition()), EntitySpawnReason.CHUNK_GENERATION, $$11);
                           $$0.addFreshEntityWithPassengers($$26);
                           $$17 = true;
                        }
                     }

                     $$12 += $$3.nextInt(5) - $$3.nextInt(5);

                     for($$13 += $$3.nextInt(5) - $$3.nextInt(5);
                        $$12 < $$6 || $$12 >= $$6 + 16 || $$13 < $$7 || $$13 >= $$7 + 16;
                        $$13 = $$15 + $$3.nextInt(5) - $$3.nextInt(5)
                     ) {
                        $$12 = $$14 + $$3.nextInt(5) - $$3.nextInt(5);
                     }
                  }
               }
            }
         }
      }
   }

   private static BlockPos getTopNonCollidingPos(LevelReader $$0, EntityType<?> $$1, int $$2, int $$3) {
      int $$4 = $$0.getHeight(SpawnPlacements.getHeightmapType($$1), $$2, $$3);
      BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos($$2, $$4, $$3);
      if ($$0.dimensionType().hasCeiling()) {
         do {
            $$5.move(Direction.DOWN);
         } while(!$$0.getBlockState($$5).isAir());

         do {
            $$5.move(Direction.DOWN);
         } while($$0.getBlockState($$5).isAir() && $$5.getY() > $$0.getMinY());
      }

      return SpawnPlacements.getPlacementType($$1).adjustSpawnPosition($$0, $$5.immutable());
   }

   @FunctionalInterface
   public interface AfterSpawnCallback {
      void run(Mob var1, ChunkAccess var2);
   }

   @FunctionalInterface
   public interface ChunkGetter {
      void query(long var1, Consumer<LevelChunk> var3);
   }

   @FunctionalInterface
   public interface SpawnPredicate {
      boolean test(EntityType<?> var1, BlockPos var2, ChunkAccess var3);
   }

   public static class SpawnState {
      private final int spawnableChunkCount;
      private final Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
      private final PotentialCalculator spawnPotential;
      private final Object2IntMap<MobCategory> unmodifiableMobCategoryCounts;
      private final LocalMobCapCalculator localMobCapCalculator;
      @Nullable
      private BlockPos lastCheckedPos;
      @Nullable
      private EntityType<?> lastCheckedType;
      private double lastCharge;

      SpawnState(int $$0, Object2IntOpenHashMap<MobCategory> $$1, PotentialCalculator $$2, LocalMobCapCalculator $$3) {
         this.spawnableChunkCount = $$0;
         this.mobCategoryCounts = $$1;
         this.spawnPotential = $$2;
         this.localMobCapCalculator = $$3;
         this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable($$1);
      }

      private boolean canSpawn(EntityType<?> $$0, BlockPos $$1, ChunkAccess $$2) {
         this.lastCheckedPos = $$1;
         this.lastCheckedType = $$0;
         MobSpawnSettings.MobSpawnCost $$3 = NaturalSpawner.getRoughBiome($$1, $$2).getMobSettings().getMobSpawnCost($$0);
         if ($$3 == null) {
            this.lastCharge = 0.0;
            return true;
         } else {
            double $$4 = $$3.charge();
            this.lastCharge = $$4;
            double $$5 = this.spawnPotential.getPotentialEnergyChange($$1, $$4);
            return $$5 <= $$3.energyBudget();
         }
      }

      private void afterSpawn(Mob $$0, ChunkAccess $$1) {
         EntityType<?> $$2 = $$0.getType();
         BlockPos $$3 = $$0.blockPosition();
         double $$4;
         if ($$3.equals(this.lastCheckedPos) && $$2 == this.lastCheckedType) {
            $$4 = this.lastCharge;
         } else {
            MobSpawnSettings.MobSpawnCost $$5 = NaturalSpawner.getRoughBiome($$3, $$1).getMobSettings().getMobSpawnCost($$2);
            if ($$5 != null) {
               $$4 = $$5.charge();
            } else {
               $$4 = 0.0;
            }
         }

         this.spawnPotential.addCharge($$3, $$4);
         MobCategory $$8 = $$2.getCategory();
         this.mobCategoryCounts.addTo($$8, 1);
         this.localMobCapCalculator.addMob(new ChunkPos($$3), $$8);
      }

      public int getSpawnableChunkCount() {
         return this.spawnableChunkCount;
      }

      public Object2IntMap<MobCategory> getMobCategoryCounts() {
         return this.unmodifiableMobCategoryCounts;
      }

      boolean canSpawnForCategoryGlobal(MobCategory $$0) {
         int $$1 = $$0.getMaxInstancesPerChunk() * this.spawnableChunkCount / NaturalSpawner.MAGIC_NUMBER;
         return this.mobCategoryCounts.getInt($$0) < $$1;
      }

      boolean canSpawnForCategoryLocal(MobCategory $$0, ChunkPos $$1) {
         return this.localMobCapCalculator.canSpawn($$0, $$1) || SharedConstants.DEBUG_IGNORE_LOCAL_MOB_CAP;
      }
   }
}
