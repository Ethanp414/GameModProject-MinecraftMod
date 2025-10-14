package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.LevelDebugSynchronizers;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathTypeCache;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.slf4j.Logger;

public class ServerLevel extends Level implements ServerEntityGetter, WorldGenLevel {
   public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
   public static final IntProvider RAIN_DELAY = UniformInt.of(12000, 180000);
   public static final IntProvider RAIN_DURATION = UniformInt.of(12000, 24000);
   private static final IntProvider THUNDER_DELAY = UniformInt.of(12000, 180000);
   public static final IntProvider THUNDER_DURATION = UniformInt.of(3600, 15600);
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int EMPTY_TIME_NO_TICK = 300;
   private static final int MAX_SCHEDULED_TICKS_PER_TICK = 65536;
   final List<ServerPlayer> players = Lists.<ServerPlayer>newArrayList();
   private final ServerChunkCache chunkSource;
   private final MinecraftServer server;
   private final ServerLevelData serverLevelData;
   final EntityTickList entityTickList = new EntityTickList();
   private final ServerWaypointManager waypointManager;
   private final PersistentEntitySectionManager<Entity> entityManager;
   private final GameEventDispatcher gameEventDispatcher;
   public boolean noSave;
   private final SleepStatus sleepStatus;
   private int emptyTime;
   private final PortalForcer portalForcer;
   private final LevelTicks<Block> blockTicks = new LevelTicks<>(this::isPositionTickingWithEntitiesLoaded);
   private final LevelTicks<Fluid> fluidTicks = new LevelTicks<>(this::isPositionTickingWithEntitiesLoaded);
   private final PathTypeCache pathTypesByPosCache = new PathTypeCache();
   final Set<Mob> navigatingMobs = new ObjectOpenHashSet<>();
   volatile boolean isUpdatingNavigations;
   protected final Raids raids;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents = new ObjectLinkedOpenHashSet();
   private final List<BlockEventData> blockEventsToReschedule = new ArrayList(64);
   private boolean handlingTick;
   private final List<CustomSpawner> customSpawners;
   @Nullable
   private EndDragonFight dragonFight;
   final Int2ObjectMap<EnderDragonPart> dragonParts = new Int2ObjectOpenHashMap<>();
   private final StructureManager structureManager;
   private final StructureCheck structureCheck;
   private final boolean tickTime;
   private final RandomSequences randomSequences;
   final LevelDebugSynchronizers debugSynchronizers = new LevelDebugSynchronizers(this);

   public ServerLevel(
      MinecraftServer $$0,
      Executor $$1,
      LevelStorageSource.LevelStorageAccess $$2,
      ServerLevelData $$3,
      ResourceKey<Level> $$4,
      LevelStem $$5,
      boolean $$6,
      long $$7,
      List<CustomSpawner> $$8,
      boolean $$9,
      @Nullable RandomSequences $$10
   ) {
      super($$3, $$4, $$0.registryAccess(), $$5.type(), false, $$6, $$7, $$0.getMaxChainedNeighborUpdates());
      this.tickTime = $$9;
      this.server = $$0;
      this.customSpawners = $$8;
      this.serverLevelData = $$3;
      ChunkGenerator $$11 = $$5.generator();
      boolean $$12 = $$0.forceSynchronousWrites();
      DataFixer $$13 = $$0.getFixerUpper();
      EntityPersistentStorage<Entity> $$14 = new EntityStorage(
         new SimpleRegionStorage(
            new RegionStorageInfo($$2.getLevelId(), $$4, "entities"), $$2.getDimensionPath($$4).resolve("entities"), $$13, $$12, DataFixTypes.ENTITY_CHUNK
         ),
         this,
         $$0
      );
      this.entityManager = new PersistentEntitySectionManager<>(Entity.class, new ServerLevel.EntityCallbacks(), $$14);
      this.chunkSource = new ServerChunkCache(
         this,
         $$2,
         $$13,
         $$0.getStructureManager(),
         $$1,
         $$11,
         $$0.getPlayerList().getViewDistance(),
         $$0.getPlayerList().getSimulationDistance(),
         $$12,
         this.entityManager::updateChunkStatus,
         () -> $$0.overworld().getDataStorage()
      );
      this.chunkSource.getGeneratorState().ensureStructuresGenerated();
      this.portalForcer = new PortalForcer(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.raids = this.getDataStorage().computeIfAbsent(Raids.getType(this.dimensionTypeRegistration()));
      if (!$$0.isSingleplayer()) {
         $$3.setGameType($$0.getDefaultGameType());
      }

      long $$15 = $$0.getWorldData().worldGenOptions().seed();
      this.structureCheck = new StructureCheck(
         this.chunkSource.chunkScanner(),
         this.registryAccess(),
         $$0.getStructureManager(),
         $$4,
         $$11,
         this.chunkSource.randomState(),
         this,
         $$11.getBiomeSource(),
         $$15,
         $$13
      );
      this.structureManager = new StructureManager(this, $$0.getWorldData().worldGenOptions(), this.structureCheck);
      if (this.dimension() == Level.END && this.dimensionTypeRegistration().is(BuiltinDimensionTypes.END)) {
         this.dragonFight = new EndDragonFight(this, $$15, $$0.getWorldData().endDragonFightData());
      } else {
         this.dragonFight = null;
      }

      this.sleepStatus = new SleepStatus();
      this.gameEventDispatcher = new GameEventDispatcher(this);
      this.randomSequences = (RandomSequences)Objects.requireNonNullElseGet($$10, () -> this.getDataStorage().computeIfAbsent(RandomSequences.TYPE));
      this.waypointManager = new ServerWaypointManager();
   }

   @Deprecated
   @VisibleForTesting
   public void setDragonFight(@Nullable EndDragonFight $$0) {
      this.dragonFight = $$0;
   }

   public void setWeatherParameters(int $$0, int $$1, boolean $$2, boolean $$3) {
      this.serverLevelData.setClearWeatherTime($$0);
      this.serverLevelData.setRainTime($$1);
      this.serverLevelData.setThunderTime($$1);
      this.serverLevelData.setRaining($$2);
      this.serverLevelData.setThundering($$3);
   }

   @Override
   public Holder<Biome> getUncachedNoiseBiome(int $$0, int $$1, int $$2) {
      return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome($$0, $$1, $$2, this.getChunkSource().randomState().sampler());
   }

   public StructureManager structureManager() {
      return this.structureManager;
   }

   public void tick(BooleanSupplier $$0) {
      ProfilerFiller $$1 = Profiler.get();
      this.handlingTick = true;
      TickRateManager $$2 = this.tickRateManager();
      boolean $$3 = $$2.runsNormally();
      if ($$3) {
         $$1.push("world border");
         this.getWorldBorder().tick();
         $$1.popPush("weather");
         this.advanceWeatherCycle();
         $$1.pop();
      }

      int $$4 = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
      if (this.sleepStatus.areEnoughSleeping($$4) && this.sleepStatus.areEnoughDeepSleeping($$4, this.players)) {
         if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            long $$5 = this.levelData.getDayTime() + 24000L;
            this.setDayTime($$5 - $$5 % 24000L);
         }

         this.wakeUpAllPlayers();
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && this.isRaining()) {
            this.resetWeatherCycle();
         }
      }

      this.updateSkyBrightness();
      if ($$3) {
         this.tickTime();
      }

      $$1.push("tickPending");
      if (!this.isDebug() && $$3) {
         long $$6 = this.getGameTime();
         $$1.push("blockTicks");
         this.blockTicks.tick($$6, 65536, this::tickBlock);
         $$1.popPush("fluidTicks");
         this.fluidTicks.tick($$6, 65536, this::tickFluid);
         $$1.pop();
      }

      $$1.popPush("raid");
      if ($$3) {
         this.raids.tick(this);
      }

      $$1.popPush("chunkSource");
      this.getChunkSource().tick($$0, true);
      $$1.popPush("blockEvents");
      if ($$3) {
         this.runBlockEvents();
      }

      this.handlingTick = false;
      $$1.pop();
      boolean $$7 = this.chunkSource.hasActiveTickets();
      if ($$7) {
         this.resetEmptyTime();
      }

      if ($$3) {
         ++this.emptyTime;
      }

      if (this.emptyTime < 300) {
         $$1.push("entities");
         if (this.dragonFight != null && $$3) {
            $$1.push("dragonFight");
            this.dragonFight.tick();
            $$1.pop();
         }

         this.entityTickList.forEach($$2x -> {
            if (!$$2x.isRemoved()) {
               if (!$$2.isEntityFrozen($$2x)) {
                  $$1.push("checkDespawn");
                  $$2x.checkDespawn();
                  $$1.pop();
                  if ($$2x instanceof ServerPlayer || this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange($$2x.chunkPosition().toLong())) {
                     Entity $$3xx = $$2x.getVehicle();
                     if ($$3xx != null) {
                        if (!$$3xx.isRemoved() && $$3xx.hasPassenger($$2x)) {
                           return;
                        }

                        $$2x.stopRiding();
                     }

                     $$1.push("tick");
                     this.guardEntityTick(this::tickNonPassenger, $$2x);
                     $$1.pop();
                  }
               }
            }
         });
         $$1.popPush("blockEntities");
         this.tickBlockEntities();
         $$1.pop();
      }

      $$1.push("entityManagement");
      this.entityManager.tick();
      $$1.pop();
      $$1.push("debugSynchronizers");
      if (this.debugSynchronizers.hasAnySubscriberFor(DebugSubscriptions.NEIGHBOR_UPDATES)) {
         this.neighborUpdater.setDebugListener($$0x -> this.debugSynchronizers.broadcastEventToTracking($$0x, DebugSubscriptions.NEIGHBOR_UPDATES, $$0x));
      } else {
         this.neighborUpdater.setDebugListener(null);
      }

      this.debugSynchronizers.tick(this.server.debugSubscribers());
      $$1.pop();
   }

   @Override
   public boolean shouldTickBlocksAt(long $$0) {
      return this.chunkSource.chunkMap.getDistanceManager().inBlockTickingRange($$0);
   }

   protected void tickTime() {
      if (this.tickTime) {
         long $$0 = this.levelData.getGameTime() + 1L;
         this.serverLevelData.setGameTime($$0);
         Profiler.get().push("scheduledFunctions");
         this.serverLevelData.getScheduledEvents().tick(this.server, $$0);
         Profiler.get().pop();
         if (this.serverLevelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
         }
      }
   }

   public void setDayTime(long $$0) {
      this.serverLevelData.setDayTime($$0);
   }

   public void tickCustomSpawners(boolean $$0) {
      for(CustomSpawner $$1 : this.customSpawners) {
         $$1.tick(this, $$0);
      }
   }

   private void wakeUpAllPlayers() {
      this.sleepStatus.removeAllSleepers();
      ((List)this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList())).forEach($$0 -> $$0.stopSleepInBed(false, false));
   }

   public void tickChunk(LevelChunk $$0, int $$1) {
      ChunkPos $$2 = $$0.getPos();
      int $$3 = $$2.getMinBlockX();
      int $$4 = $$2.getMinBlockZ();
      ProfilerFiller $$5 = Profiler.get();
      $$5.push("iceandsnow");

      for(int $$6 = 0; $$6 < $$1; ++$$6) {
         if (this.random.nextInt(48) == 0) {
            this.tickPrecipitation(this.getBlockRandomPos($$3, 0, $$4, 15));
         }
      }

      $$5.popPush("tickBlocks");
      if ($$1 > 0) {
         LevelChunkSection[] $$7 = $$0.getSections();

         for(int $$8 = 0; $$8 < $$7.length; ++$$8) {
            LevelChunkSection $$9 = $$7[$$8];
            if ($$9.isRandomlyTicking()) {
               int $$10 = $$0.getSectionYFromSectionIndex($$8);
               int $$11 = SectionPos.sectionToBlockCoord($$10);

               for(int $$12 = 0; $$12 < $$1; ++$$12) {
                  BlockPos $$13 = this.getBlockRandomPos($$3, $$11, $$4, 15);
                  $$5.push("randomTick");
                  BlockState $$14 = $$9.getBlockState($$13.getX() - $$3, $$13.getY() - $$11, $$13.getZ() - $$4);
                  if ($$14.isRandomlyTicking()) {
                     $$14.randomTick(this, $$13, this.random);
                  }

                  FluidState $$15 = $$14.getFluidState();
                  if ($$15.isRandomlyTicking()) {
                     $$15.randomTick(this, $$13, this.random);
                  }

                  $$5.pop();
               }
            }
         }
      }

      $$5.pop();
   }

   public void tickThunder(LevelChunk $$0) {
      ChunkPos $$1 = $$0.getPos();
      boolean $$2 = this.isRaining();
      int $$3 = $$1.getMinBlockX();
      int $$4 = $$1.getMinBlockZ();
      ProfilerFiller $$5 = Profiler.get();
      $$5.push("thunder");
      if ($$2 && this.isThundering() && this.random.nextInt(100000) == 0) {
         BlockPos $$6 = this.findLightningTargetAround(this.getBlockRandomPos($$3, 0, $$4, 15));
         if (this.isRainingAt($$6)) {
            DifficultyInstance $$7 = this.getCurrentDifficultyAt($$6);
            boolean $$8 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
               && this.random.nextDouble() < (double)$$7.getEffectiveDifficulty() * 0.01
               && !this.getBlockState($$6.below()).is(BlockTags.LIGHTNING_RODS);
            if ($$8) {
               SkeletonHorse $$9 = EntityType.SKELETON_HORSE.create(this, EntitySpawnReason.EVENT);
               if ($$9 != null) {
                  $$9.setTrap(true);
                  $$9.setAge(0);
                  $$9.setPos((double)$$6.getX(), (double)$$6.getY(), (double)$$6.getZ());
                  this.addFreshEntity($$9);
               }
            }

            LightningBolt $$10 = EntityType.LIGHTNING_BOLT.create(this, EntitySpawnReason.EVENT);
            if ($$10 != null) {
               $$10.snapTo(Vec3.atBottomCenterOf($$6));
               $$10.setVisualOnly($$8);
               this.addFreshEntity($$10);
            }
         }
      }

      $$5.pop();
   }

   @VisibleForTesting
   public void tickPrecipitation(BlockPos $$0) {
      BlockPos $$1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$0);
      BlockPos $$2 = $$1.below();
      Biome $$3 = this.getBiome($$1).value();
      if ($$3.shouldFreeze(this, $$2)) {
         this.setBlockAndUpdate($$2, Blocks.ICE.defaultBlockState());
      }

      if (this.isRaining()) {
         int $$4 = this.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
         if ($$4 > 0 && $$3.shouldSnow(this, $$1)) {
            BlockState $$5 = this.getBlockState($$1);
            if ($$5.is(Blocks.SNOW)) {
               int $$6 = $$5.getValue(SnowLayerBlock.LAYERS);
               if ($$6 < Math.min($$4, 8)) {
                  BlockState $$7 = $$5.setValue(SnowLayerBlock.LAYERS, Integer.valueOf($$6 + 1));
                  Block.pushEntitiesUp($$5, $$7, this, $$1);
                  this.setBlockAndUpdate($$1, $$7);
               }
            } else {
               this.setBlockAndUpdate($$1, Blocks.SNOW.defaultBlockState());
            }
         }

         Biome.Precipitation $$8 = $$3.getPrecipitationAt($$2, this.getSeaLevel());
         if ($$8 != Biome.Precipitation.NONE) {
            BlockState $$9 = this.getBlockState($$2);
            $$9.getBlock().handlePrecipitation($$9, this, $$2, $$8);
         }
      }
   }

   private Optional<BlockPos> findLightningRod(BlockPos $$0) {
      Optional<BlockPos> $$1 = this.getPoiManager()
         .findClosest(
            $$0x -> $$0x.is(PoiTypes.LIGHTNING_ROD),
            $$0x -> $$0x.getY() == this.getHeight(Heightmap.Types.WORLD_SURFACE, $$0x.getX(), $$0x.getZ()) - 1,
            $$0,
            128,
            PoiManager.Occupancy.ANY
         );
      return $$1.map($$0x -> $$0x.above(1));
   }

   protected BlockPos findLightningTargetAround(BlockPos $$0) {
      BlockPos $$1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$0);
      Optional<BlockPos> $$2 = this.findLightningRod($$1);
      if ($$2.isPresent()) {
         return (BlockPos)$$2.get();
      } else {
         AABB $$3 = AABB.encapsulatingFullBlocks($$1, $$1.atY(this.getMaxY() + 1)).inflate(3.0);
         List<LivingEntity> $$4 = this.getEntitiesOfClass(
            LivingEntity.class, $$3, $$0x -> $$0x != null && $$0x.isAlive() && this.canSeeSky($$0x.blockPosition())
         );
         if (!$$4.isEmpty()) {
            return ((LivingEntity)$$4.get(this.random.nextInt($$4.size()))).blockPosition();
         } else {
            if ($$1.getY() == this.getMinY() - 1) {
               $$1 = $$1.above(2);
            }

            return $$1;
         }
      }
   }

   public boolean isHandlingTick() {
      return this.handlingTick;
   }

   public boolean canSleepThroughNights() {
      return this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE) <= 100;
   }

   private void announceSleepStatus() {
      if (this.canSleepThroughNights()) {
         if (!this.getServer().isSingleplayer() || this.getServer().isPublished()) {
            int $$0 = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
            Component $$1;
            if (this.sleepStatus.areEnoughSleeping($$0)) {
               $$1 = Component.translatable("sleep.skipping_night");
            } else {
               $$1 = Component.translatable("sleep.players_sleeping", this.sleepStatus.amountSleeping(), this.sleepStatus.sleepersNeeded($$0));
            }

            for(ServerPlayer $$3 : this.players) {
               $$3.displayClientMessage($$1, true);
            }
         }
      }
   }

   public void updateSleepingPlayerList() {
      if (!this.players.isEmpty() && this.sleepStatus.update(this.players)) {
         this.announceSleepStatus();
      }
   }

   public ServerScoreboard getScoreboard() {
      return this.server.getScoreboard();
   }

   public ServerWaypointManager getWaypointManager() {
      return this.waypointManager;
   }

   private void advanceWeatherCycle() {
      boolean $$0 = this.isRaining();
      if (this.dimensionType().hasSkyLight()) {
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            int $$1 = this.serverLevelData.getClearWeatherTime();
            int $$2 = this.serverLevelData.getThunderTime();
            int $$3 = this.serverLevelData.getRainTime();
            boolean $$4 = this.levelData.isThundering();
            boolean $$5 = this.levelData.isRaining();
            if ($$1 > 0) {
               --$$1;
               $$2 = $$4 ? 0 : 1;
               $$3 = $$5 ? 0 : 1;
               $$4 = false;
               $$5 = false;
            } else {
               if ($$2 > 0) {
                  if (--$$2 == 0) {
                     $$4 = !$$4;
                  }
               } else if ($$4) {
                  $$2 = THUNDER_DURATION.sample(this.random);
               } else {
                  $$2 = THUNDER_DELAY.sample(this.random);
               }

               if ($$3 > 0) {
                  if (--$$3 == 0) {
                     $$5 = !$$5;
                  }
               } else if ($$5) {
                  $$3 = RAIN_DURATION.sample(this.random);
               } else {
                  $$3 = RAIN_DELAY.sample(this.random);
               }
            }

            this.serverLevelData.setThunderTime($$2);
            this.serverLevelData.setRainTime($$3);
            this.serverLevelData.setClearWeatherTime($$1);
            this.serverLevelData.setThundering($$4);
            this.serverLevelData.setRaining($$5);
         }

         this.oThunderLevel = this.thunderLevel;
         if (this.levelData.isThundering()) {
            this.thunderLevel += 0.01F;
         } else {
            this.thunderLevel -= 0.01F;
         }

         this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0F, 1.0F);
         this.oRainLevel = this.rainLevel;
         if (this.levelData.isRaining()) {
            this.rainLevel += 0.01F;
         } else {
            this.rainLevel -= 0.01F;
         }

         this.rainLevel = Mth.clamp(this.rainLevel, 0.0F, 1.0F);
      }

      if (this.oRainLevel != this.rainLevel) {
         this.server
            .getPlayerList()
            .broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
      }

      if (this.oThunderLevel != this.thunderLevel) {
         this.server
            .getPlayerList()
            .broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
      }

      if ($$0 != this.isRaining()) {
         if ($$0) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F));
         } else {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
         }

         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel));
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel));
      }
   }

   @VisibleForTesting
   public void resetWeatherCycle() {
      this.serverLevelData.setRainTime(0);
      this.serverLevelData.setRaining(false);
      this.serverLevelData.setThunderTime(0);
      this.serverLevelData.setThundering(false);
   }

   public void resetEmptyTime() {
      this.emptyTime = 0;
   }

   private void tickFluid(BlockPos $$0, Fluid $$1) {
      BlockState $$2 = this.getBlockState($$0);
      FluidState $$3 = $$2.getFluidState();
      if ($$3.is($$1)) {
         $$3.tick(this, $$0, $$2);
      }
   }

   private void tickBlock(BlockPos $$0, Block $$1) {
      BlockState $$2 = this.getBlockState($$0);
      if ($$2.is($$1)) {
         $$2.tick(this, $$0, this.random);
      }
   }

   public void tickNonPassenger(Entity $$0) {
      $$0.setOldPosAndRot();
      ProfilerFiller $$1 = Profiler.get();
      ++$$0.tickCount;
      $$1.push((Supplier<String>)(() -> BuiltInRegistries.ENTITY_TYPE.getKey($$0.getType()).toString()));
      $$1.incrementCounter("tickNonPassenger");
      $$0.tick();
      $$1.pop();

      for(Entity $$2 : $$0.getPassengers()) {
         this.tickPassenger($$0, $$2);
      }
   }

   private void tickPassenger(Entity $$0, Entity $$1) {
      if ($$1.isRemoved() || $$1.getVehicle() != $$0) {
         $$1.stopRiding();
      } else if ($$1 instanceof Player || this.entityTickList.contains($$1)) {
         $$1.setOldPosAndRot();
         ++$$1.tickCount;
         ProfilerFiller $$2 = Profiler.get();
         $$2.push((Supplier<String>)(() -> BuiltInRegistries.ENTITY_TYPE.getKey($$1.getType()).toString()));
         $$2.incrementCounter("tickPassenger");
         $$1.rideTick();
         $$2.pop();

         for(Entity $$3 : $$1.getPassengers()) {
            this.tickPassenger($$1, $$3);
         }
      }
   }

   public void updateNeighboursOnBlockSet(BlockPos $$0, BlockState $$1) {
      BlockState $$2 = this.getBlockState($$0);
      Block $$3 = $$2.getBlock();
      boolean $$4 = !$$1.is($$3);
      if ($$4) {
         $$1.affectNeighborsAfterRemoval(this, $$0, false);
      }

      this.updateNeighborsAt($$0, $$2.getBlock());
      if ($$2.hasAnalogOutputSignal()) {
         this.updateNeighbourForOutputSignal($$0, $$3);
      }
   }

   @Override
   public boolean mayInteract(Entity $$0, BlockPos $$1) {
      if ($$0 instanceof Player $$2 && (this.server.isUnderSpawnProtection(this, $$1, $$2) || !this.getWorldBorder().isWithinBounds($$1))) {
         return false;
      }

      return true;
   }

   public void save(@Nullable ProgressListener $$0, boolean $$1, boolean $$2) {
      ServerChunkCache $$3 = this.getChunkSource();
      if (!$$2) {
         if ($$0 != null) {
            $$0.progressStartNoAbort(Component.translatable("menu.savingLevel"));
         }

         this.saveLevelData($$1);
         if ($$0 != null) {
            $$0.progressStage(Component.translatable("menu.savingChunks"));
         }

         $$3.save($$1);
         if ($$1) {
            this.entityManager.saveAll();
         } else {
            this.entityManager.autoSave();
         }
      }
   }

   private void saveLevelData(boolean $$0) {
      if (this.dragonFight != null) {
         this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
      }

      DimensionDataStorage $$1 = this.getChunkSource().getDataStorage();
      if ($$0) {
         $$1.saveAndJoin();
      } else {
         $$1.scheduleSave();
      }
   }

   public <T extends Entity> List<? extends T> getEntities(EntityTypeTest<Entity, T> $$0, Predicate<? super T> $$1) {
      List<T> $$2 = Lists.<T>newArrayList();
      this.getEntities($$0, $$1, $$2);
      return $$2;
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> $$0, Predicate<? super T> $$1, List<? super T> $$2) {
      this.getEntities($$0, $$1, $$2, Integer.MAX_VALUE);
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> $$0, Predicate<? super T> $$1, List<? super T> $$2, int $$3) {
      this.getEntities().get($$0, $$3x -> {
         if ($$1.test($$3x)) {
            $$2.add($$3x);
            if ($$2.size() >= $$3) {
               return AbortableIterationConsumer.Continuation.ABORT;
            }
         }

         return AbortableIterationConsumer.Continuation.CONTINUE;
      });
   }

   public List<? extends EnderDragon> getDragons() {
      return this.getEntities(EntityType.ENDER_DRAGON, LivingEntity::isAlive);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> $$0) {
      return this.getPlayers($$0, Integer.MAX_VALUE);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> $$0, int $$1) {
      List<ServerPlayer> $$2 = Lists.<ServerPlayer>newArrayList();

      for(ServerPlayer $$3 : this.players) {
         if ($$0.test($$3)) {
            $$2.add($$3);
            if ($$2.size() >= $$1) {
               return $$2;
            }
         }
      }

      return $$2;
   }

   @Nullable
   public ServerPlayer getRandomPlayer() {
      List<ServerPlayer> $$0 = this.getPlayers(LivingEntity::isAlive);
      return $$0.isEmpty() ? null : (ServerPlayer)$$0.get(this.random.nextInt($$0.size()));
   }

   @Override
   public boolean addFreshEntity(Entity $$0) {
      return this.addEntity($$0);
   }

   public boolean addWithUUID(Entity $$0) {
      return this.addEntity($$0);
   }

   public void addDuringTeleport(Entity $$0) {
      if ($$0 instanceof ServerPlayer $$1) {
         this.addPlayer($$1);
      } else {
         this.addEntity($$0);
      }
   }

   public void addNewPlayer(ServerPlayer $$0) {
      this.addPlayer($$0);
   }

   public void addRespawnedPlayer(ServerPlayer $$0) {
      this.addPlayer($$0);
   }

   private void addPlayer(ServerPlayer $$0) {
      Entity $$1 = this.getEntity($$0.getUUID());
      if ($$1 != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", $$0.getUUID());
         $$1.unRide();
         this.removePlayerImmediately((ServerPlayer)$$1, Entity.RemovalReason.DISCARDED);
      }

      this.entityManager.addNewEntity($$0);
   }

   private boolean addEntity(Entity $$0) {
      if ($$0.isRemoved()) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getKey($$0.getType()));
         return false;
      } else {
         return this.entityManager.addNewEntity($$0);
      }
   }

   public boolean tryAddFreshEntityWithPassengers(Entity $$0) {
      if ($$0.getSelfAndPassengers().map(Entity::getUUID).anyMatch(this.entityManager::isLoaded)) {
         return false;
      } else {
         this.addFreshEntityWithPassengers($$0);
         return true;
      }
   }

   public void unload(LevelChunk $$0) {
      $$0.clearAllBlockEntities();
      $$0.unregisterTickContainerFromLevel(this);
      this.debugSynchronizers.dropChunk($$0.getPos());
   }

   public void removePlayerImmediately(ServerPlayer $$0, Entity.RemovalReason $$1) {
      $$0.remove($$1);
   }

   @Override
   public void destroyBlockProgress(int $$0, BlockPos $$1, int $$2) {
      for(ServerPlayer $$3 : this.server.getPlayerList().getPlayers()) {
         if ($$3 != null && $$3.level() == this && $$3.getId() != $$0) {
            double $$4 = (double)$$1.getX() - $$3.getX();
            double $$5 = (double)$$1.getY() - $$3.getY();
            double $$6 = (double)$$1.getZ() - $$3.getZ();
            if ($$4 * $$4 + $$5 * $$5 + $$6 * $$6 < 1024.0) {
               $$3.connection.send(new ClientboundBlockDestructionPacket($$0, $$1, $$2));
            }
         }
      }
   }

   @Override
   public void playSeededSound(
      @Nullable Entity $$0, double $$1, double $$2, double $$3, Holder<SoundEvent> $$4, SoundSource $$5, float $$6, float $$7, long $$8
   ) {
      this.server
         .getPlayerList()
         .broadcast(
            $$0 instanceof Player $$9 ? $$9 : null,
            $$1,
            $$2,
            $$3,
            (double)((SoundEvent)$$4.value()).getRange($$6),
            this.dimension(),
            new ClientboundSoundPacket($$4, $$5, $$1, $$2, $$3, $$6, $$7, $$8)
         );
   }

   @Override
   public void playSeededSound(@Nullable Entity $$0, Entity $$1, Holder<SoundEvent> $$2, SoundSource $$3, float $$4, float $$5, long $$6) {
      this.server
         .getPlayerList()
         .broadcast(
            $$0 instanceof Player $$7 ? $$7 : null,
            $$1.getX(),
            $$1.getY(),
            $$1.getZ(),
            (double)((SoundEvent)$$2.value()).getRange($$4),
            this.dimension(),
            new ClientboundSoundEntityPacket($$2, $$3, $$1, $$4, $$5, $$6)
         );
   }

   @Override
   public void globalLevelEvent(int $$0, BlockPos $$1, int $$2) {
      if (this.getGameRules().getBoolean(GameRules.RULE_GLOBAL_SOUND_EVENTS)) {
         this.server.getPlayerList().getPlayers().forEach($$3 -> {
            Vec3 $$5;
            if ($$3.level() == this) {
               Vec3 $$4 = Vec3.atCenterOf($$1);
               if ($$3.distanceToSqr($$4) < (double)Mth.square(32)) {
                  $$5 = $$4;
               } else {
                  Vec3 $$6 = $$4.subtract($$3.position()).normalize();
                  $$5 = $$3.position().add($$6.scale(32.0));
               }
            } else {
               $$5 = $$3.position();
            }

            $$3.connection.send(new ClientboundLevelEventPacket($$0, BlockPos.containing($$5), $$2, true));
         });
      } else {
         this.levelEvent(null, $$0, $$1, $$2);
      }
   }

   @Override
   public void levelEvent(@Nullable Entity $$0, int $$1, BlockPos $$2, int $$3) {
      this.server
         .getPlayerList()
         .broadcast(
            $$0 instanceof Player $$4 ? $$4 : null,
            (double)$$2.getX(),
            (double)$$2.getY(),
            (double)$$2.getZ(),
            64.0,
            this.dimension(),
            new ClientboundLevelEventPacket($$1, $$2, $$3, false)
         );
   }

   public int getLogicalHeight() {
      return this.dimensionType().logicalHeight();
   }

   @Override
   public void gameEvent(Holder<GameEvent> $$0, Vec3 $$1, GameEvent.Context $$2) {
      this.gameEventDispatcher.post($$0, $$1, $$2);
   }

   @Override
   public void sendBlockUpdated(BlockPos $$0, BlockState $$1, BlockState $$2, int $$3) {
      if (this.isUpdatingNavigations) {
         String $$4 = "recursive call to sendBlockUpdated";
         Util.logAndPauseIfInIde("recursive call to sendBlockUpdated", new IllegalStateException("recursive call to sendBlockUpdated"));
      }

      this.getChunkSource().blockChanged($$0);
      this.pathTypesByPosCache.invalidate($$0);
      VoxelShape $$5 = $$1.getCollisionShape(this, $$0);
      VoxelShape $$6 = $$2.getCollisionShape(this, $$0);
      if (Shapes.joinIsNotEmpty($$5, $$6, BooleanOp.NOT_SAME)) {
         List<PathNavigation> $$7 = new ObjectArrayList<>();

         for(Mob $$8 : this.navigatingMobs) {
            PathNavigation $$9 = $$8.getNavigation();
            if ($$9.shouldRecomputePath($$0)) {
               $$7.add($$9);
            }
         }

         try {
            this.isUpdatingNavigations = true;

            for(PathNavigation $$10 : $$7) {
               $$10.recomputePath();
            }
         } finally {
            this.isUpdatingNavigations = false;
         }
      }
   }

   @Override
   public void updateNeighborsAt(BlockPos $$0, Block $$1) {
      this.updateNeighborsAt($$0, $$1, ExperimentalRedstoneUtils.initialOrientation(this, null, null));
   }

   @Override
   public void updateNeighborsAt(BlockPos $$0, Block $$1, @Nullable Orientation $$2) {
      this.neighborUpdater.updateNeighborsAtExceptFromFacing($$0, $$1, null, $$2);
   }

   @Override
   public void updateNeighborsAtExceptFromFacing(BlockPos $$0, Block $$1, Direction $$2, @Nullable Orientation $$3) {
      this.neighborUpdater.updateNeighborsAtExceptFromFacing($$0, $$1, $$2, $$3);
   }

   @Override
   public void neighborChanged(BlockPos $$0, Block $$1, @Nullable Orientation $$2) {
      this.neighborUpdater.neighborChanged($$0, $$1, $$2);
   }

   @Override
   public void neighborChanged(BlockState $$0, BlockPos $$1, Block $$2, @Nullable Orientation $$3, boolean $$4) {
      this.neighborUpdater.neighborChanged($$0, $$1, $$2, $$3, $$4);
   }

   @Override
   public void broadcastEntityEvent(Entity $$0, byte $$1) {
      this.getChunkSource().sendToTrackingPlayersAndSelf($$0, new ClientboundEntityEventPacket($$0, $$1));
   }

   @Override
   public void broadcastDamageEvent(Entity $$0, DamageSource $$1) {
      this.getChunkSource().sendToTrackingPlayersAndSelf($$0, new ClientboundDamageEventPacket($$0, $$1));
   }

   public ServerChunkCache getChunkSource() {
      return this.chunkSource;
   }

   @Override
   public void explode(
      @Nullable Entity $$0,
      @Nullable DamageSource $$1,
      @Nullable ExplosionDamageCalculator $$2,
      double $$3,
      double $$4,
      double $$5,
      float $$6,
      boolean $$7,
      Level.ExplosionInteraction $$8,
      ParticleOptions $$9,
      ParticleOptions $$10,
      WeightedList<ExplosionParticleInfo> $$11,
      Holder<SoundEvent> $$12
   ) {
      Explosion.BlockInteraction $$13 = switch($$8) {
         case NONE -> Explosion.BlockInteraction.KEEP;
         case BLOCK -> this.getDestroyType(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
         case MOB -> this.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
         ? this.getDestroyType(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY)
         : Explosion.BlockInteraction.KEEP;
         case TNT -> this.getDestroyType(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
         case TRIGGER -> Explosion.BlockInteraction.TRIGGER_BLOCK;
         default -> throw new MatchException(null, null);
      };
      Vec3 $$14 = new Vec3($$3, $$4, $$5);
      ServerExplosion $$15 = new ServerExplosion(this, $$0, $$1, $$2, $$14, $$6, $$7, $$13);
      int $$16 = $$15.explode();
      ParticleOptions $$17 = $$15.isSmall() ? $$9 : $$10;

      for(ServerPlayer $$18 : this.players) {
         if ($$18.distanceToSqr($$14) < 4096.0) {
            Optional<Vec3> $$19 = Optional.ofNullable((Vec3)$$15.getHitPlayers().get($$18));
            $$18.connection.send(new ClientboundExplodePacket($$14, $$6, $$16, $$19, $$17, $$12, $$11));
         }
      }
   }

   private Explosion.BlockInteraction getDestroyType(GameRules.Key<GameRules.BooleanValue> $$0) {
      return this.getGameRules().getBoolean($$0) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
   }

   @Override
   public void blockEvent(BlockPos $$0, Block $$1, int $$2, int $$3) {
      this.blockEvents.add(new BlockEventData($$0, $$1, $$2, $$3));
   }

   private void runBlockEvents() {
      this.blockEventsToReschedule.clear();

      while(!this.blockEvents.isEmpty()) {
         BlockEventData $$0 = (BlockEventData)this.blockEvents.removeFirst();
         if (this.shouldTickBlocksAt($$0.pos())) {
            if (this.doBlockEvent($$0)) {
               this.server
                  .getPlayerList()
                  .broadcast(
                     null,
                     (double)$$0.pos().getX(),
                     (double)$$0.pos().getY(),
                     (double)$$0.pos().getZ(),
                     64.0,
                     this.dimension(),
                     new ClientboundBlockEventPacket($$0.pos(), $$0.block(), $$0.paramA(), $$0.paramB())
                  );
            }
         } else {
            this.blockEventsToReschedule.add($$0);
         }
      }

      this.blockEvents.addAll(this.blockEventsToReschedule);
   }

   private boolean doBlockEvent(BlockEventData $$0) {
      BlockState $$1 = this.getBlockState($$0.pos());
      return $$1.is($$0.block()) ? $$1.triggerEvent(this, $$0.pos(), $$0.paramA(), $$0.paramB()) : false;
   }

   public LevelTicks<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public LevelTicks<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   @Nonnull
   @Override
   public MinecraftServer getServer() {
      return this.server;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public StructureTemplateManager getStructureManager() {
      return this.server.getStructureManager();
   }

   public <T extends ParticleOptions> int sendParticles(T $$0, double $$1, double $$2, double $$3, int $$4, double $$5, double $$6, double $$7, double $$8) {
      return this.sendParticles($$0, false, false, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
   }

   public <T extends ParticleOptions> int sendParticles(
      T $$0, boolean $$1, boolean $$2, double $$3, double $$4, double $$5, int $$6, double $$7, double $$8, double $$9, double $$10
   ) {
      ClientboundLevelParticlesPacket $$11 = new ClientboundLevelParticlesPacket(
         $$0, $$1, $$2, $$3, $$4, $$5, (float)$$7, (float)$$8, (float)$$9, (float)$$10, $$6
      );
      int $$12 = 0;

      for(int $$13 = 0; $$13 < this.players.size(); ++$$13) {
         ServerPlayer $$14 = (ServerPlayer)this.players.get($$13);
         if (this.sendParticles($$14, $$1, $$3, $$4, $$5, $$11)) {
            ++$$12;
         }
      }

      return $$12;
   }

   public <T extends ParticleOptions> boolean sendParticles(
      ServerPlayer $$0, T $$1, boolean $$2, boolean $$3, double $$4, double $$5, double $$6, int $$7, double $$8, double $$9, double $$10, double $$11
   ) {
      Packet<?> $$12 = new ClientboundLevelParticlesPacket($$1, $$2, $$3, $$4, $$5, $$6, (float)$$8, (float)$$9, (float)$$10, (float)$$11, $$7);
      return this.sendParticles($$0, $$2, $$4, $$5, $$6, $$12);
   }

   private boolean sendParticles(ServerPlayer $$0, boolean $$1, double $$2, double $$3, double $$4, Packet<?> $$5) {
      if ($$0.level() != this) {
         return false;
      } else {
         BlockPos $$6 = $$0.blockPosition();
         if ($$6.closerToCenterThan(new Vec3($$2, $$3, $$4), $$1 ? 512.0 : 32.0)) {
            $$0.connection.send($$5);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   @Override
   public Entity getEntity(int $$0) {
      return this.getEntities().get($$0);
   }

   @Nullable
   @Override
   public Entity getEntityInAnyDimension(UUID $$0) {
      Entity $$1 = this.getEntity($$0);
      if ($$1 != null) {
         return $$1;
      } else {
         for(ServerLevel $$2 : this.getServer().getAllLevels()) {
            if ($$2 != this) {
               Entity $$3 = $$2.getEntity($$0);
               if ($$3 != null) {
                  return $$3;
               }
            }
         }

         return null;
      }
   }

   @Nullable
   @Override
   public Player getPlayerInAnyDimension(UUID $$0) {
      return this.getServer().getPlayerList().getPlayer($$0);
   }

   @Deprecated
   @Nullable
   public Entity getEntityOrPart(int $$0) {
      Entity $$1 = this.getEntities().get($$0);
      return $$1 != null ? $$1 : this.dragonParts.get($$0);
   }

   @Override
   public Collection<EnderDragonPart> dragonParts() {
      return this.dragonParts.values();
   }

   @Nullable
   public BlockPos findNearestMapStructure(TagKey<Structure> $$0, BlockPos $$1, int $$2, boolean $$3) {
      if (!this.server.getWorldData().worldGenOptions().generateStructures()) {
         return null;
      } else {
         Optional<HolderSet.Named<Structure>> $$4 = this.registryAccess().lookupOrThrow(Registries.STRUCTURE).get($$0);
         if ($$4.isEmpty()) {
            return null;
         } else {
            Pair<BlockPos, Holder<Structure>> $$5 = this.getChunkSource()
               .getGenerator()
               .findNearestMapStructure(this, (HolderSet<Structure>)$$4.get(), $$1, $$2, $$3);
            return $$5 != null ? $$5.getFirst() : null;
         }
      }
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(Predicate<Holder<Biome>> $$0, BlockPos $$1, int $$2, int $$3, int $$4) {
      return this.getChunkSource()
         .getGenerator()
         .getBiomeSource()
         .findClosestBiome3d($$1, $$2, $$3, $$4, $$0, this.getChunkSource().randomState().sampler(), this);
   }

   @Override
   public WorldBorder getWorldBorder() {
      return this.getDataStorage().computeIfAbsent(WorldBorder.TYPE);
   }

   public RecipeManager recipeAccess() {
      return this.server.getRecipeManager();
   }

   @Override
   public TickRateManager tickRateManager() {
      return this.server.tickRateManager();
   }

   @Override
   public boolean noSave() {
      return this.noSave;
   }

   public DimensionDataStorage getDataStorage() {
      return this.getChunkSource().getDataStorage();
   }

   @Nullable
   @Override
   public MapItemSavedData getMapData(MapId $$0) {
      return this.getServer().overworld().getDataStorage().get(MapItemSavedData.type($$0));
   }

   public void setMapData(MapId $$0, MapItemSavedData $$1) {
      this.getServer().overworld().getDataStorage().set(MapItemSavedData.type($$0), $$1);
   }

   public MapId getFreeMapId() {
      return this.getServer().overworld().getDataStorage().computeIfAbsent(MapIndex.TYPE).getNextMapId();
   }

   @Override
   public void setRespawnData(LevelData.RespawnData $$0) {
      this.getServer().setRespawnData($$0);
   }

   @Override
   public LevelData.RespawnData getRespawnData() {
      return this.getServer().getRespawnData();
   }

   public LongSet getForceLoadedChunks() {
      return this.chunkSource.getForceLoadedChunks();
   }

   public boolean setChunkForced(int $$0, int $$1, boolean $$2) {
      boolean $$3 = this.chunkSource.updateChunkForced(new ChunkPos($$0, $$1), $$2);
      if ($$2 && $$3) {
         this.getChunk($$0, $$1);
      }

      return $$3;
   }

   @Override
   public List<ServerPlayer> players() {
      return this.players;
   }

   @Override
   public void updatePOIOnBlockStateChange(BlockPos $$0, BlockState $$1, BlockState $$2) {
      Optional<Holder<PoiType>> $$3 = PoiTypes.forState($$1);
      Optional<Holder<PoiType>> $$4 = PoiTypes.forState($$2);
      if (!Objects.equals($$3, $$4)) {
         BlockPos $$5 = $$0.immutable();
         $$3.ifPresent($$1x -> this.getServer().execute(() -> {
               this.getPoiManager().remove($$5);
               this.debugSynchronizers.dropPoi($$5);
            }));
         $$4.ifPresent($$1x -> this.getServer().execute(() -> {
               PoiRecord $$2xx = this.getPoiManager().add($$5, $$1x);
               if ($$2xx != null) {
                  this.debugSynchronizers.registerPoi($$2xx);
               }
            }));
      }
   }

   public PoiManager getPoiManager() {
      return this.getChunkSource().getPoiManager();
   }

   public boolean isVillage(BlockPos $$0) {
      return this.isCloseToVillage($$0, 1);
   }

   public boolean isVillage(SectionPos $$0) {
      return this.isVillage($$0.center());
   }

   public boolean isCloseToVillage(BlockPos $$0, int $$1) {
      if ($$1 > 6) {
         return false;
      } else {
         return this.sectionsToVillage(SectionPos.of($$0)) <= $$1;
      }
   }

   public int sectionsToVillage(SectionPos $$0) {
      return this.getPoiManager().sectionsToVillage($$0);
   }

   public Raids getRaids() {
      return this.raids;
   }

   @Nullable
   public Raid getRaidAt(BlockPos $$0) {
      return this.raids.getNearbyRaid($$0, 9216);
   }

   public boolean isRaided(BlockPos $$0) {
      return this.getRaidAt($$0) != null;
   }

   public void onReputationEvent(ReputationEventType $$0, Entity $$1, ReputationEventHandler $$2) {
      $$2.onReputationEventFrom($$0, $$1);
   }

   public void saveDebugReport(Path $$0) throws IOException {
      ChunkMap $$1 = this.getChunkSource().chunkMap;
      Writer $$2 = Files.newBufferedWriter($$0.resolve("stats.txt"));

      try {
         $$2.write(String.format(Locale.ROOT, "spawning_chunks: %d\n", $$1.getDistanceManager().getNaturalSpawnChunkCount()));
         NaturalSpawner.SpawnState $$3 = this.getChunkSource().getLastSpawnState();
         if ($$3 != null) {
            for(Entry<MobCategory> $$4 : $$3.getMobCategoryCounts().object2IntEntrySet()) {
               $$2.write(String.format(Locale.ROOT, "spawn_count.%s: %d\n", ((MobCategory)$$4.getKey()).getName(), $$4.getIntValue()));
            }
         }

         $$2.write(String.format(Locale.ROOT, "entities: %s\n", this.entityManager.gatherStats()));
         $$2.write(String.format(Locale.ROOT, "block_entity_tickers: %d\n", this.blockEntityTickers.size()));
         $$2.write(String.format(Locale.ROOT, "block_ticks: %d\n", this.getBlockTicks().count()));
         $$2.write(String.format(Locale.ROOT, "fluid_ticks: %d\n", this.getFluidTicks().count()));
         $$2.write("distance_manager: " + $$1.getDistanceManager().getDebugStatus() + "\n");
         $$2.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
      } catch (Throwable var22) {
         if ($$2 != null) {
            try {
               $$2.close();
            } catch (Throwable var16) {
               var22.addSuppressed(var16);
            }
         }

         throw var22;
      }

      if ($$2 != null) {
         $$2.close();
      }

      CrashReport $$5 = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails($$5);
      Writer $$6 = Files.newBufferedWriter($$0.resolve("example_crash.txt"));

      try {
         $$6.write($$5.getFriendlyReport(ReportType.TEST));
      } catch (Throwable var21) {
         if ($$6 != null) {
            try {
               $$6.close();
            } catch (Throwable var15) {
               var21.addSuppressed(var15);
            }
         }

         throw var21;
      }

      if ($$6 != null) {
         $$6.close();
      }

      Path $$7 = $$0.resolve("chunks.csv");
      Writer $$8 = Files.newBufferedWriter($$7);

      try {
         $$1.dumpChunks($$8);
      } catch (Throwable var20) {
         if ($$8 != null) {
            try {
               $$8.close();
            } catch (Throwable var14) {
               var20.addSuppressed(var14);
            }
         }

         throw var20;
      }

      if ($$8 != null) {
         $$8.close();
      }

      Path $$9 = $$0.resolve("entity_chunks.csv");
      Writer $$10 = Files.newBufferedWriter($$9);

      try {
         this.entityManager.dumpSections($$10);
      } catch (Throwable var19) {
         if ($$10 != null) {
            try {
               $$10.close();
            } catch (Throwable var13) {
               var19.addSuppressed(var13);
            }
         }

         throw var19;
      }

      if ($$10 != null) {
         $$10.close();
      }

      Path $$11 = $$0.resolve("entities.csv");
      Writer $$12 = Files.newBufferedWriter($$11);

      try {
         dumpEntities($$12, this.getEntities().getAll());
      } catch (Throwable var18) {
         if ($$12 != null) {
            try {
               $$12.close();
            } catch (Throwable var12) {
               var18.addSuppressed(var12);
            }
         }

         throw var18;
      }

      if ($$12 != null) {
         $$12.close();
      }

      Path $$13 = $$0.resolve("block_entities.csv");
      Writer $$14 = Files.newBufferedWriter($$13);

      try {
         this.dumpBlockEntityTickers($$14);
      } catch (Throwable var17) {
         if ($$14 != null) {
            try {
               $$14.close();
            } catch (Throwable var11) {
               var17.addSuppressed(var11);
            }
         }

         throw var17;
      }

      if ($$14 != null) {
         $$14.close();
      }
   }

   private static void dumpEntities(Writer $$0, Iterable<Entity> $$1) throws IOException {
      CsvOutput $$2 = CsvOutput.builder()
         .addColumn("x")
         .addColumn("y")
         .addColumn("z")
         .addColumn("uuid")
         .addColumn("type")
         .addColumn("alive")
         .addColumn("display_name")
         .addColumn("custom_name")
         .build($$0);

      for(Entity $$3 : $$1) {
         Component $$4 = $$3.getCustomName();
         Component $$5 = $$3.getDisplayName();
         $$2.writeRow(
            $$3.getX(),
            $$3.getY(),
            $$3.getZ(),
            $$3.getUUID(),
            BuiltInRegistries.ENTITY_TYPE.getKey($$3.getType()),
            $$3.isAlive(),
            $$5.getString(),
            $$4 != null ? $$4.getString() : null
         );
      }
   }

   private void dumpBlockEntityTickers(Writer $$0) throws IOException {
      CsvOutput $$1 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build($$0);

      for(TickingBlockEntity $$2 : this.blockEntityTickers) {
         BlockPos $$3 = $$2.getPos();
         $$1.writeRow($$3.getX(), $$3.getY(), $$3.getZ(), $$2.getType());
      }
   }

   @VisibleForTesting
   public void clearBlockEvents(BoundingBox $$0) {
      this.blockEvents.removeIf($$1 -> $$0.isInside($$1.pos()));
   }

   @Override
   public float getShade(Direction $$0, boolean $$1) {
      return 1.0F;
   }

   public Iterable<Entity> getAllEntities() {
      return this.getEntities().getAll();
   }

   public String toString() {
      return "ServerLevel[" + this.serverLevelData.getLevelName() + "]";
   }

   public boolean isFlat() {
      return this.server.getWorldData().isFlatWorld();
   }

   @Override
   public long getSeed() {
      return this.server.getWorldData().worldGenOptions().seed();
   }

   @Nullable
   public EndDragonFight getDragonFight() {
      return this.dragonFight;
   }

   @Override
   public ServerLevel getLevel() {
      return this;
   }

   @VisibleForTesting
   public String getWatchdogStats() {
      return String.format(
         Locale.ROOT,
         "players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s",
         this.players.size(),
         this.entityManager.gatherStats(),
         getTypeCount(this.entityManager.getEntityGetter().getAll(), $$0 -> BuiltInRegistries.ENTITY_TYPE.getKey($$0.getType()).toString()),
         this.blockEntityTickers.size(),
         getTypeCount(this.blockEntityTickers, TickingBlockEntity::getType),
         this.getBlockTicks().count(),
         this.getFluidTicks().count(),
         this.gatherChunkSourceStats()
      );
   }

   private static <T> String getTypeCount(Iterable<T> $$0, Function<T, String> $$1) {
      try {
         Object2IntOpenHashMap<String> $$2 = new Object2IntOpenHashMap();

         for(T $$3 : $$0) {
            String $$4 = (String)$$1.apply($$3);
            $$2.addTo($$4, 1);
         }

         return (String)$$2.object2IntEntrySet()
            .stream()
            .sorted(Comparator.comparing(Entry::getIntValue).reversed())
            .limit(5L)
            .map($$0x -> (String)$$0x.getKey() + ":" + $$0x.getIntValue())
            .collect(Collectors.joining(","));
      } catch (Exception var6) {
         return "";
      }
   }

   @Override
   protected LevelEntityGetter<Entity> getEntities() {
      return this.entityManager.getEntityGetter();
   }

   public void addLegacyChunkEntities(Stream<Entity> $$0) {
      this.entityManager.addLegacyChunkEntities($$0);
   }

   public void addWorldGenChunkEntities(Stream<Entity> $$0) {
      this.entityManager.addWorldGenChunkEntities($$0);
   }

   public void startTickingChunk(LevelChunk $$0) {
      $$0.unpackTicks(this.getLevelData().getGameTime());
   }

   public void onStructureStartsAvailable(ChunkAccess $$0) {
      this.server.execute(() -> this.structureCheck.onStructureLoad($$0.getPos(), $$0.getAllStarts()));
   }

   public PathTypeCache getPathTypeCache() {
      return this.pathTypesByPosCache;
   }

   public void waitForEntities(ChunkPos $$0, int $$1) {
      List<ChunkPos> $$2 = ChunkPos.rangeClosed($$0, $$1).toList();
      this.server.managedBlock(() -> {
         this.entityManager.processPendingLoads();

         for(ChunkPos $$1xx : $$2) {
            if (!this.areEntitiesLoaded($$1xx.toLong())) {
               return false;
            }
         }

         return true;
      });
   }

   public boolean isSpawningMonsters() {
      return this.server.isSpawningMonsters();
   }

   @Override
   public void close() throws IOException {
      super.close();
      this.entityManager.close();
   }

   @Override
   public String gatherChunkSourceStats() {
      return "Chunks[S] W: " + this.chunkSource.gatherStats() + " E: " + this.entityManager.gatherStats();
   }

   public boolean areEntitiesLoaded(long $$0) {
      return this.entityManager.areEntitiesLoaded($$0);
   }

   public boolean isPositionTickingWithEntitiesLoaded(long $$0) {
      return this.areEntitiesLoaded($$0) && this.chunkSource.isPositionTicking($$0);
   }

   public boolean isPositionEntityTicking(BlockPos $$0) {
      return this.entityManager.canPositionTick($$0) && this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(ChunkPos.asLong($$0));
   }

   public boolean areEntitiesActuallyLoadedAndTicking(ChunkPos $$0) {
      return this.entityManager.isTicking($$0) && this.entityManager.areEntitiesLoaded($$0.toLong());
   }

   public boolean anyPlayerCloseEnoughForSpawning(BlockPos $$0) {
      return this.anyPlayerCloseEnoughForSpawning(new ChunkPos($$0));
   }

   public boolean anyPlayerCloseEnoughForSpawning(ChunkPos $$0) {
      return this.chunkSource.chunkMap.anyPlayerCloseEnoughForSpawning($$0);
   }

   public boolean canSpawnEntitiesInChunk(ChunkPos $$0) {
      return this.entityManager.canPositionTick($$0) && this.getWorldBorder().isWithinBounds($$0);
   }

   @Override
   public FeatureFlagSet enabledFeatures() {
      return this.server.getWorldData().enabledFeatures();
   }

   @Override
   public PotionBrewing potionBrewing() {
      return this.server.potionBrewing();
   }

   @Override
   public FuelValues fuelValues() {
      return this.server.fuelValues();
   }

   public RandomSource getRandomSequence(ResourceLocation $$0) {
      return this.randomSequences.get($$0);
   }

   public RandomSequences getRandomSequences() {
      return this.randomSequences;
   }

   public GameRules getGameRules() {
      return this.serverLevelData.getGameRules();
   }

   @Override
   public CrashReportCategory fillReportDetails(CrashReport $$0) {
      CrashReportCategory $$1 = super.fillReportDetails($$0);
      $$1.setDetail("Loaded entity count", (CrashReportDetail<String>)(() -> String.valueOf(this.entityManager.count())));
      return $$1;
   }

   @Override
   public int getSeaLevel() {
      return this.chunkSource.getGenerator().getSeaLevel();
   }

   @Override
   public void onBlockEntityAdded(BlockEntity $$0) {
      super.onBlockEntityAdded($$0);
      this.debugSynchronizers.registerBlockEntity($$0);
   }

   public LevelDebugSynchronizers debugSynchronizers() {
      return this.debugSynchronizers;
   }

   final class EntityCallbacks implements LevelCallback<Entity> {
      public void onCreated(Entity $$0) {
         if ($$0 instanceof WaypointTransmitter $$1 && $$1.isTransmittingWaypoint()) {
            ServerLevel.this.getWaypointManager().trackWaypoint($$1);
         }
      }

      public void onDestroyed(Entity $$0) {
         if ($$0 instanceof WaypointTransmitter $$1) {
            ServerLevel.this.getWaypointManager().untrackWaypoint($$1);
         }

         ServerLevel.this.getScoreboard().entityRemoved($$0);
      }

      public void onTickingStart(Entity $$0) {
         ServerLevel.this.entityTickList.add($$0);
      }

      public void onTickingEnd(Entity $$0) {
         ServerLevel.this.entityTickList.remove($$0);
      }

      public void onTrackingStart(Entity $$0) {
         ServerLevel.this.getChunkSource().addEntity($$0);
         if ($$0 instanceof ServerPlayer $$1) {
            ServerLevel.this.players.add($$1);
            if ($$1.isReceivingWaypoints()) {
               ServerLevel.this.getWaypointManager().addPlayer($$1);
            }

            ServerLevel.this.updateSleepingPlayerList();
         }

         if ($$0 instanceof WaypointTransmitter $$2 && $$2.isTransmittingWaypoint()) {
            ServerLevel.this.getWaypointManager().trackWaypoint($$2);
         }

         if ($$0 instanceof Mob $$3) {
            if (ServerLevel.this.isUpdatingNavigations) {
               String $$4 = "onTrackingStart called during navigation iteration";
               Util.logAndPauseIfInIde(
                  "onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration")
               );
            }

            ServerLevel.this.navigatingMobs.add($$3);
         }

         if ($$0 instanceof EnderDragon $$5) {
            for(EnderDragonPart $$6 : $$5.getSubEntities()) {
               ServerLevel.this.dragonParts.put($$6.getId(), $$6);
            }
         }

         $$0.updateDynamicGameEventListener(DynamicGameEventListener::add);
      }

      public void onTrackingEnd(Entity $$0) {
         ServerLevel.this.getChunkSource().removeEntity($$0);
         if ($$0 instanceof ServerPlayer $$1) {
            ServerLevel.this.players.remove($$1);
            ServerLevel.this.getWaypointManager().removePlayer($$1);
            ServerLevel.this.updateSleepingPlayerList();
         }

         if ($$0 instanceof Mob $$2) {
            if (ServerLevel.this.isUpdatingNavigations) {
               String $$3 = "onTrackingStart called during navigation iteration";
               Util.logAndPauseIfInIde(
                  "onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration")
               );
            }

            ServerLevel.this.navigatingMobs.remove($$2);
         }

         if ($$0 instanceof EnderDragon $$4) {
            for(EnderDragonPart $$5 : $$4.getSubEntities()) {
               ServerLevel.this.dragonParts.remove($$5.getId());
            }
         }

         $$0.updateDynamicGameEventListener(DynamicGameEventListener::remove);
         ServerLevel.this.debugSynchronizers.dropEntity($$0);
      }

      public void onSectionChange(Entity $$0) {
         $$0.updateDynamicGameEventListener(DynamicGameEventListener::move);
      }
   }
}
