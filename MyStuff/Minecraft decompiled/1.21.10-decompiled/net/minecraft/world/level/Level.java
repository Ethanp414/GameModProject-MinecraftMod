package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class Level implements LevelAccessor, AutoCloseable {
   public static final Codec<ResourceKey<Level>> RESOURCE_KEY_CODEC = ResourceKey.codec(Registries.DIMENSION);
   public static final ResourceKey<Level> OVERWORLD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("overworld"));
   public static final ResourceKey<Level> NETHER = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("the_nether"));
   public static final ResourceKey<Level> END = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("the_end"));
   public static final int MAX_LEVEL_SIZE = 30000000;
   public static final int LONG_PARTICLE_CLIP_RANGE = 512;
   public static final int SHORT_PARTICLE_CLIP_RANGE = 32;
   public static final int MAX_BRIGHTNESS = 15;
   public static final int TICKS_PER_DAY = 24000;
   public static final int MAX_ENTITY_SPAWN_Y = 20000000;
   public static final int MIN_ENTITY_SPAWN_Y = -20000000;
   private static final WeightedList<ExplosionParticleInfo> DEFAULT_EXPLOSION_BLOCK_PARTICLES = WeightedList.builder()
      .add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
      .add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
      .build();
   protected final List<TickingBlockEntity> blockEntityTickers = Lists.<TickingBlockEntity>newArrayList();
   protected final CollectingNeighborUpdater neighborUpdater;
   private final List<TickingBlockEntity> pendingBlockEntityTickers = Lists.<TickingBlockEntity>newArrayList();
   private boolean tickingBlockEntities;
   private final Thread thread;
   private final boolean isDebug;
   private int skyDarken;
   protected int randValue = RandomSource.create().nextInt();
   protected final int addend = 1013904223;
   protected float oRainLevel;
   protected float rainLevel;
   protected float oThunderLevel;
   protected float thunderLevel;
   public final RandomSource random = RandomSource.create();
   @Deprecated
   private final RandomSource threadSafeRandom = RandomSource.createThreadSafe();
   private final Holder<DimensionType> dimensionTypeRegistration;
   protected final WritableLevelData levelData;
   private final boolean isClientSide;
   private final BiomeManager biomeManager;
   private final ResourceKey<Level> dimension;
   private final RegistryAccess registryAccess;
   private final DamageSources damageSources;
   private final PalettedContainerFactory palettedContainerFactory;
   private long subTickCount;

   protected Level(WritableLevelData $$0, ResourceKey<Level> $$1, RegistryAccess $$2, Holder<DimensionType> $$3, boolean $$4, boolean $$5, long $$6, int $$7) {
      this.levelData = $$0;
      this.dimensionTypeRegistration = $$3;
      DimensionType $$8 = (DimensionType)$$3.value();
      this.dimension = $$1;
      this.isClientSide = $$4;
      this.thread = Thread.currentThread();
      this.biomeManager = new BiomeManager(this, $$6);
      this.isDebug = $$5;
      this.neighborUpdater = new CollectingNeighborUpdater(this, $$7);
      this.registryAccess = $$2;
      this.palettedContainerFactory = PalettedContainerFactory.create($$2);
      this.damageSources = new DamageSources($$2);
   }

   @Override
   public boolean isClientSide() {
      return this.isClientSide;
   }

   @Nullable
   @Override
   public MinecraftServer getServer() {
      return null;
   }

   public boolean isInWorldBounds(BlockPos $$0) {
      return !this.isOutsideBuildHeight($$0) && isInWorldBoundsHorizontal($$0);
   }

   public static boolean isInSpawnableBounds(BlockPos $$0) {
      return !isOutsideSpawnableHeight($$0.getY()) && isInWorldBoundsHorizontal($$0);
   }

   private static boolean isInWorldBoundsHorizontal(BlockPos $$0) {
      return $$0.getX() >= -30000000 && $$0.getZ() >= -30000000 && $$0.getX() < 30000000 && $$0.getZ() < 30000000;
   }

   private static boolean isOutsideSpawnableHeight(int $$0) {
      return $$0 < -20000000 || $$0 >= 20000000;
   }

   public LevelChunk getChunkAt(BlockPos $$0) {
      return this.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
   }

   public LevelChunk getChunk(int $$0, int $$1) {
      return (LevelChunk)this.getChunk($$0, $$1, ChunkStatus.FULL);
   }

   @Nullable
   @Override
   public ChunkAccess getChunk(int $$0, int $$1, ChunkStatus $$2, boolean $$3) {
      ChunkAccess $$4 = this.getChunkSource().getChunk($$0, $$1, $$2, $$3);
      if ($$4 == null && $$3) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return $$4;
      }
   }

   @Override
   public boolean setBlock(BlockPos $$0, BlockState $$1, int $$2) {
      return this.setBlock($$0, $$1, $$2, 512);
   }

   @Override
   public boolean setBlock(BlockPos $$0, BlockState $$1, int $$2, int $$3) {
      if (this.isOutsideBuildHeight($$0)) {
         return false;
      } else if (!this.isClientSide() && this.isDebug()) {
         return false;
      } else {
         LevelChunk $$4 = this.getChunkAt($$0);
         Block $$5 = $$1.getBlock();
         BlockState $$6 = $$4.setBlockState($$0, $$1, $$2);
         if ($$6 == null) {
            return false;
         } else {
            BlockState $$7 = this.getBlockState($$0);
            if ($$7 == $$1) {
               if ($$6 != $$7) {
                  this.setBlocksDirty($$0, $$6, $$7);
               }

               if (($$2 & 2) != 0
                  && (!this.isClientSide() || ($$2 & 4) == 0)
                  && (this.isClientSide() || $$4.getFullStatus() != null && $$4.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
                  this.sendBlockUpdated($$0, $$6, $$1, $$2);
               }

               if (($$2 & 1) != 0) {
                  this.updateNeighborsAt($$0, $$6.getBlock());
                  if (!this.isClientSide() && $$1.hasAnalogOutputSignal()) {
                     this.updateNeighbourForOutputSignal($$0, $$5);
                  }
               }

               if (($$2 & 16) == 0 && $$3 > 0) {
                  int $$8 = $$2 & -34;
                  $$6.updateIndirectNeighbourShapes(this, $$0, $$8, $$3 - 1);
                  $$1.updateNeighbourShapes(this, $$0, $$8, $$3 - 1);
                  $$1.updateIndirectNeighbourShapes(this, $$0, $$8, $$3 - 1);
               }

               this.updatePOIOnBlockStateChange($$0, $$6, $$7);
            }

            return true;
         }
      }
   }

   public void updatePOIOnBlockStateChange(BlockPos $$0, BlockState $$1, BlockState $$2) {
   }

   @Override
   public boolean removeBlock(BlockPos $$0, boolean $$1) {
      FluidState $$2 = this.getFluidState($$0);
      return this.setBlock($$0, $$2.createLegacyBlock(), 3 | ($$1 ? 64 : 0));
   }

   @Override
   public boolean destroyBlock(BlockPos $$0, boolean $$1, @Nullable Entity $$2, int $$3) {
      BlockState $$4 = this.getBlockState($$0);
      if ($$4.isAir()) {
         return false;
      } else {
         FluidState $$5 = this.getFluidState($$0);
         if (!($$4.getBlock() instanceof BaseFireBlock)) {
            this.levelEvent(2001, $$0, Block.getId($$4));
         }

         if ($$1) {
            BlockEntity $$6 = $$4.hasBlockEntity() ? this.getBlockEntity($$0) : null;
            Block.dropResources($$4, this, $$0, $$6, $$2, ItemStack.EMPTY);
         }

         boolean $$7 = this.setBlock($$0, $$5.createLegacyBlock(), 3, $$3);
         if ($$7) {
            this.gameEvent(GameEvent.BLOCK_DESTROY, $$0, GameEvent.Context.of($$2, $$4));
         }

         return $$7;
      }
   }

   public void addDestroyBlockEffect(BlockPos $$0, BlockState $$1) {
   }

   public boolean setBlockAndUpdate(BlockPos $$0, BlockState $$1) {
      return this.setBlock($$0, $$1, 3);
   }

   public abstract void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4);

   public void setBlocksDirty(BlockPos $$0, BlockState $$1, BlockState $$2) {
   }

   public void updateNeighborsAt(BlockPos $$0, Block $$1, @Nullable Orientation $$2) {
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos $$0, Block $$1, Direction $$2, @Nullable Orientation $$3) {
   }

   public void neighborChanged(BlockPos $$0, Block $$1, @Nullable Orientation $$2) {
   }

   public void neighborChanged(BlockState $$0, BlockPos $$1, Block $$2, @Nullable Orientation $$3, boolean $$4) {
   }

   @Override
   public void neighborShapeChanged(Direction $$0, BlockPos $$1, BlockPos $$2, BlockState $$3, int $$4, int $$5) {
      this.neighborUpdater.shapeUpdate($$0, $$3, $$1, $$2, $$4, $$5);
   }

   @Override
   public int getHeight(Heightmap.Types $$0, int $$1, int $$2) {
      int $$4;
      if ($$1 >= -30000000 && $$2 >= -30000000 && $$1 < 30000000 && $$2 < 30000000) {
         if (this.hasChunk(SectionPos.blockToSectionCoord($$1), SectionPos.blockToSectionCoord($$2))) {
            $$4 = this.getChunk(SectionPos.blockToSectionCoord($$1), SectionPos.blockToSectionCoord($$2)).getHeight($$0, $$1 & 15, $$2 & 15) + 1;
         } else {
            $$4 = this.getMinY();
         }
      } else {
         $$4 = this.getSeaLevel() + 1;
      }

      return $$4;
   }

   @Override
   public LevelLightEngine getLightEngine() {
      return this.getChunkSource().getLightEngine();
   }

   @Override
   public BlockState getBlockState(BlockPos $$0) {
      if (this.isOutsideBuildHeight($$0)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunk $$1 = this.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
         return $$1.getBlockState($$0);
      }
   }

   @Override
   public FluidState getFluidState(BlockPos $$0) {
      if (this.isOutsideBuildHeight($$0)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunk $$1 = this.getChunkAt($$0);
         return $$1.getFluidState($$0);
      }
   }

   public boolean isBrightOutside() {
      return !this.dimensionType().hasFixedTime() && this.skyDarken < 4;
   }

   public boolean isDarkOutside() {
      return !this.dimensionType().hasFixedTime() && !this.isBrightOutside();
   }

   public boolean isMoonVisible() {
      if (!this.dimensionType().natural()) {
         return false;
      } else {
         int $$0 = (int)(this.getDayTime() % 24000L);
         return $$0 >= 12600 && $$0 <= 23400;
      }
   }

   @Override
   public void playSound(@Nullable Entity $$0, BlockPos $$1, SoundEvent $$2, SoundSource $$3, float $$4, float $$5) {
      this.playSound($$0, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, $$2, $$3, $$4, $$5);
   }

   public abstract void playSeededSound(
      @Nullable Entity var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12
   );

   public void playSeededSound(@Nullable Entity $$0, double $$1, double $$2, double $$3, SoundEvent $$4, SoundSource $$5, float $$6, float $$7, long $$8) {
      this.playSeededSound($$0, $$1, $$2, $$3, BuiltInRegistries.SOUND_EVENT.wrapAsHolder($$4), $$5, $$6, $$7, $$8);
   }

   public abstract void playSeededSound(@Nullable Entity var1, Entity var2, Holder<SoundEvent> var3, SoundSource var4, float var5, float var6, long var7);

   public void playSound(@Nullable Entity $$0, double $$1, double $$2, double $$3, SoundEvent $$4, SoundSource $$5) {
      this.playSound($$0, $$1, $$2, $$3, $$4, $$5, 1.0F, 1.0F);
   }

   public void playSound(@Nullable Entity $$0, double $$1, double $$2, double $$3, SoundEvent $$4, SoundSource $$5, float $$6, float $$7) {
      this.playSeededSound($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, this.threadSafeRandom.nextLong());
   }

   public void playSound(@Nullable Entity $$0, double $$1, double $$2, double $$3, Holder<SoundEvent> $$4, SoundSource $$5, float $$6, float $$7) {
      this.playSeededSound($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, this.threadSafeRandom.nextLong());
   }

   public void playSound(@Nullable Entity $$0, Entity $$1, SoundEvent $$2, SoundSource $$3, float $$4, float $$5) {
      this.playSeededSound($$0, $$1, BuiltInRegistries.SOUND_EVENT.wrapAsHolder($$2), $$3, $$4, $$5, this.threadSafeRandom.nextLong());
   }

   public void playLocalSound(BlockPos $$0, SoundEvent $$1, SoundSource $$2, float $$3, float $$4, boolean $$5) {
      this.playLocalSound((double)$$0.getX() + 0.5, (double)$$0.getY() + 0.5, (double)$$0.getZ() + 0.5, $$1, $$2, $$3, $$4, $$5);
   }

   public void playLocalSound(Entity $$0, SoundEvent $$1, SoundSource $$2, float $$3, float $$4) {
   }

   public void playLocalSound(double $$0, double $$1, double $$2, SoundEvent $$3, SoundSource $$4, float $$5, float $$6, boolean $$7) {
   }

   public void playPlayerSound(SoundEvent $$0, SoundSource $$1, float $$2, float $$3) {
   }

   @Override
   public void addParticle(ParticleOptions $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
   }

   public void addParticle(ParticleOptions $$0, boolean $$1, boolean $$2, double $$3, double $$4, double $$5, double $$6, double $$7, double $$8) {
   }

   public void addAlwaysVisibleParticle(ParticleOptions $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
   }

   public void addAlwaysVisibleParticle(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
   }

   public float getSunAngle(float $$0) {
      float $$1 = this.getTimeOfDay($$0);
      return $$1 * (float) (Math.PI * 2);
   }

   public void addBlockEntityTicker(TickingBlockEntity $$0) {
      (this.tickingBlockEntities ? this.pendingBlockEntityTickers : this.blockEntityTickers).add($$0);
   }

   public void tickBlockEntities() {
      this.tickingBlockEntities = true;
      if (!this.pendingBlockEntityTickers.isEmpty()) {
         this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
         this.pendingBlockEntityTickers.clear();
      }

      Iterator<TickingBlockEntity> $$0 = this.blockEntityTickers.iterator();
      boolean $$1 = this.tickRateManager().runsNormally();

      while($$0.hasNext()) {
         TickingBlockEntity $$2 = (TickingBlockEntity)$$0.next();
         if ($$2.isRemoved()) {
            $$0.remove();
         } else if ($$1 && this.shouldTickBlocksAt($$2.getPos())) {
            $$2.tick();
         }
      }

      this.tickingBlockEntities = false;
   }

   public <T extends Entity> void guardEntityTick(Consumer<T> $$0, T $$1) {
      try {
         $$0.accept($$1);
      } catch (Throwable var6) {
         CrashReport $$3 = CrashReport.forThrowable(var6, "Ticking entity");
         CrashReportCategory $$4 = $$3.addCategory("Entity being ticked");
         $$1.fillCrashReportCategory($$4);
         throw new ReportedException($$3);
      }
   }

   public boolean shouldTickDeath(Entity $$0) {
      return true;
   }

   public boolean shouldTickBlocksAt(long $$0) {
      return true;
   }

   public boolean shouldTickBlocksAt(BlockPos $$0) {
      return this.shouldTickBlocksAt(ChunkPos.asLong($$0));
   }

   public void explode(@Nullable Entity $$0, double $$1, double $$2, double $$3, float $$4, Level.ExplosionInteraction $$5) {
      this.explode(
         $$0,
         Explosion.getDefaultDamageSource(this, $$0),
         null,
         $$1,
         $$2,
         $$3,
         $$4,
         false,
         $$5,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         DEFAULT_EXPLOSION_BLOCK_PARTICLES,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   public void explode(@Nullable Entity $$0, double $$1, double $$2, double $$3, float $$4, boolean $$5, Level.ExplosionInteraction $$6) {
      this.explode(
         $$0,
         Explosion.getDefaultDamageSource(this, $$0),
         null,
         $$1,
         $$2,
         $$3,
         $$4,
         $$5,
         $$6,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         DEFAULT_EXPLOSION_BLOCK_PARTICLES,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   public void explode(
      @Nullable Entity $$0,
      @Nullable DamageSource $$1,
      @Nullable ExplosionDamageCalculator $$2,
      Vec3 $$3,
      float $$4,
      boolean $$5,
      Level.ExplosionInteraction $$6
   ) {
      this.explode(
         $$0,
         $$1,
         $$2,
         $$3.x(),
         $$3.y(),
         $$3.z(),
         $$4,
         $$5,
         $$6,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         DEFAULT_EXPLOSION_BLOCK_PARTICLES,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   public void explode(
      @Nullable Entity $$0,
      @Nullable DamageSource $$1,
      @Nullable ExplosionDamageCalculator $$2,
      double $$3,
      double $$4,
      double $$5,
      float $$6,
      boolean $$7,
      Level.ExplosionInteraction $$8
   ) {
      this.explode(
         $$0,
         $$1,
         $$2,
         $$3,
         $$4,
         $$5,
         $$6,
         $$7,
         $$8,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         DEFAULT_EXPLOSION_BLOCK_PARTICLES,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   public abstract void explode(
      @Nullable Entity var1,
      @Nullable DamageSource var2,
      @Nullable ExplosionDamageCalculator var3,
      double var4,
      double var6,
      double var8,
      float var10,
      boolean var11,
      Level.ExplosionInteraction var12,
      ParticleOptions var13,
      ParticleOptions var14,
      WeightedList<ExplosionParticleInfo> var15,
      Holder<SoundEvent> var16
   );

   public abstract String gatherChunkSourceStats();

   @Nullable
   @Override
   public BlockEntity getBlockEntity(BlockPos $$0) {
      if (this.isOutsideBuildHeight($$0)) {
         return null;
      } else {
         return !this.isClientSide() && Thread.currentThread() != this.thread
            ? null
            : this.getChunkAt($$0).getBlockEntity($$0, LevelChunk.EntityCreationType.IMMEDIATE);
      }
   }

   public void setBlockEntity(BlockEntity $$0) {
      BlockPos $$1 = $$0.getBlockPos();
      if (!this.isOutsideBuildHeight($$1)) {
         this.getChunkAt($$1).addAndRegisterBlockEntity($$0);
      }
   }

   public void removeBlockEntity(BlockPos $$0) {
      if (!this.isOutsideBuildHeight($$0)) {
         this.getChunkAt($$0).removeBlockEntity($$0);
      }
   }

   public boolean isLoaded(BlockPos $$0) {
      return this.isOutsideBuildHeight($$0)
         ? false
         : this.getChunkSource().hasChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
   }

   public boolean loadedAndEntityCanStandOnFace(BlockPos $$0, Entity $$1, Direction $$2) {
      if (this.isOutsideBuildHeight($$0)) {
         return false;
      } else {
         ChunkAccess $$3 = this.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()), ChunkStatus.FULL, false);
         return $$3 == null ? false : $$3.getBlockState($$0).entityCanStandOnFace(this, $$0, $$1, $$2);
      }
   }

   public boolean loadedAndEntityCanStandOn(BlockPos $$0, Entity $$1) {
      return this.loadedAndEntityCanStandOnFace($$0, $$1, Direction.UP);
   }

   public void updateSkyBrightness() {
      double $$0 = 1.0 - (double)(this.getRainLevel(1.0F) * 5.0F) / 16.0;
      double $$1 = 1.0 - (double)(this.getThunderLevel(1.0F) * 5.0F) / 16.0;
      double $$2 = 0.5 + 2.0 * Mth.clamp((double)Mth.cos(this.getTimeOfDay(1.0F) * (float) (Math.PI * 2)), -0.25, 0.25);
      this.skyDarken = (int)((1.0 - $$2 * $$0 * $$1) * 11.0);
   }

   public void setSpawnSettings(boolean $$0) {
      this.getChunkSource().setSpawnSettings($$0);
   }

   public abstract void setRespawnData(LevelData.RespawnData var1);

   public abstract LevelData.RespawnData getRespawnData();

   public LevelData.RespawnData getWorldBorderAdjustedRespawnData(LevelData.RespawnData $$0) {
      WorldBorder $$1 = this.getWorldBorder();
      if (!$$1.isWithinBounds($$0.pos())) {
         BlockPos $$2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.containing($$1.getCenterX(), 0.0, $$1.getCenterZ()));
         return LevelData.RespawnData.of($$0.dimension(), $$2, $$0.yaw(), $$0.pitch());
      } else {
         return $$0;
      }
   }

   protected void prepareWeather() {
      if (this.levelData.isRaining()) {
         this.rainLevel = 1.0F;
         if (this.levelData.isThundering()) {
            this.thunderLevel = 1.0F;
         }
      }
   }

   public void close() throws IOException {
      this.getChunkSource().close();
   }

   @Nullable
   @Override
   public BlockGetter getChunkForCollisions(int $$0, int $$1) {
      return this.getChunk($$0, $$1, ChunkStatus.FULL, false);
   }

   @Override
   public List<Entity> getEntities(@Nullable Entity $$0, AABB $$1, Predicate<? super Entity> $$2) {
      Profiler.get().incrementCounter("getEntities");
      List<Entity> $$3 = Lists.<Entity>newArrayList();
      this.getEntities().get($$1, $$3x -> {
         if ($$3x != $$0 && $$2.test($$3x)) {
            $$3.add($$3x);
         }
      });

      for(EnderDragonPart $$4 : this.dragonParts()) {
         if ($$4 != $$0 && $$4.parentMob != $$0 && $$2.test($$4) && $$1.intersects($$4.getBoundingBox())) {
            $$3.add($$4);
         }
      }

      return $$3;
   }

   @Override
   public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> $$0, AABB $$1, Predicate<? super T> $$2) {
      List<T> $$3 = Lists.<T>newArrayList();
      this.getEntities($$0, $$1, $$2, $$3);
      return $$3;
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> $$0, AABB $$1, Predicate<? super T> $$2, List<? super T> $$3) {
      this.getEntities($$0, $$1, $$2, $$3, Integer.MAX_VALUE);
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> $$0, AABB $$1, Predicate<? super T> $$2, List<? super T> $$3, int $$4) {
      Profiler.get().incrementCounter("getEntities");
      this.getEntities().get($$0, $$1, $$4x -> {
         if ($$2.test($$4x)) {
            $$3.add($$4x);
            if ($$3.size() >= $$4) {
               return AbortableIterationConsumer.Continuation.ABORT;
            }
         }

         if ($$4x instanceof EnderDragon $$5) {
            for(EnderDragonPart $$6 : $$5.getSubEntities()) {
               T $$7 = $$0.tryCast($$6);
               if ($$7 != null && $$2.test($$7)) {
                  $$3.add($$7);
                  if ($$3.size() >= $$4) {
                     return AbortableIterationConsumer.Continuation.ABORT;
                  }
               }
            }
         }

         return AbortableIterationConsumer.Continuation.CONTINUE;
      });
   }

   public <T extends Entity> boolean hasEntities(EntityTypeTest<Entity, T> $$0, AABB $$1, Predicate<? super T> $$2) {
      Profiler.get().incrementCounter("hasEntities");
      MutableBoolean $$3 = new MutableBoolean();
      this.getEntities().get($$0, $$1, $$3x -> {
         if ($$2.test($$3x)) {
            $$3.setTrue();
            return AbortableIterationConsumer.Continuation.ABORT;
         } else {
            if ($$3x instanceof EnderDragon $$4) {
               for(EnderDragonPart $$5 : $$4.getSubEntities()) {
                  T $$6 = $$0.tryCast($$5);
                  if ($$6 != null && $$2.test($$6)) {
                     $$3.setTrue();
                     return AbortableIterationConsumer.Continuation.ABORT;
                  }
               }
            }

            return AbortableIterationConsumer.Continuation.CONTINUE;
         }
      });
      return $$3.isTrue();
   }

   public List<Entity> getPushableEntities(Entity $$0, AABB $$1) {
      return this.getEntities($$0, $$1, EntitySelector.pushableBy($$0));
   }

   @Nullable
   public abstract Entity getEntity(int var1);

   @Nullable
   public Entity getEntity(UUID $$0) {
      return this.getEntities().get($$0);
   }

   @Nullable
   public Entity getEntityInAnyDimension(UUID $$0) {
      return this.getEntity($$0);
   }

   @Nullable
   public Player getPlayerInAnyDimension(UUID $$0) {
      return this.getPlayerByUUID($$0);
   }

   public abstract Collection<EnderDragonPart> dragonParts();

   public void blockEntityChanged(BlockPos $$0) {
      if (this.hasChunkAt($$0)) {
         this.getChunkAt($$0).markUnsaved();
      }
   }

   public void onBlockEntityAdded(BlockEntity $$0) {
   }

   public long getGameTime() {
      return this.levelData.getGameTime();
   }

   public long getDayTime() {
      return this.levelData.getDayTime();
   }

   public boolean mayInteract(Entity $$0, BlockPos $$1) {
      return true;
   }

   public void broadcastEntityEvent(Entity $$0, byte $$1) {
   }

   public void broadcastDamageEvent(Entity $$0, DamageSource $$1) {
   }

   public void blockEvent(BlockPos $$0, Block $$1, int $$2, int $$3) {
      this.getBlockState($$0).triggerEvent(this, $$0, $$2, $$3);
   }

   @Override
   public LevelData getLevelData() {
      return this.levelData;
   }

   public abstract TickRateManager tickRateManager();

   public float getThunderLevel(float $$0) {
      return Mth.lerp($$0, this.oThunderLevel, this.thunderLevel) * this.getRainLevel($$0);
   }

   public void setThunderLevel(float $$0) {
      float $$1 = Mth.clamp($$0, 0.0F, 1.0F);
      this.oThunderLevel = $$1;
      this.thunderLevel = $$1;
   }

   public float getRainLevel(float $$0) {
      return Mth.lerp($$0, this.oRainLevel, this.rainLevel);
   }

   public void setRainLevel(float $$0) {
      float $$1 = Mth.clamp($$0, 0.0F, 1.0F);
      this.oRainLevel = $$1;
      this.rainLevel = $$1;
   }

   private boolean canHaveWeather() {
      return this.dimensionType().hasSkyLight() && !this.dimensionType().hasCeiling();
   }

   public boolean isThundering() {
      return this.canHaveWeather() && (double)this.getThunderLevel(1.0F) > 0.9;
   }

   public boolean isRaining() {
      return this.canHaveWeather() && (double)this.getRainLevel(1.0F) > 0.2;
   }

   public boolean isRainingAt(BlockPos $$0) {
      return this.precipitationAt($$0) == Biome.Precipitation.RAIN;
   }

   public Biome.Precipitation precipitationAt(BlockPos $$0) {
      if (!this.isRaining()) {
         return Biome.Precipitation.NONE;
      } else if (!this.canSeeSky($$0)) {
         return Biome.Precipitation.NONE;
      } else if (this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$0).getY() > $$0.getY()) {
         return Biome.Precipitation.NONE;
      } else {
         Biome $$1 = this.getBiome($$0).value();
         return $$1.getPrecipitationAt($$0, this.getSeaLevel());
      }
   }

   @Nullable
   public abstract MapItemSavedData getMapData(MapId var1);

   public void globalLevelEvent(int $$0, BlockPos $$1, int $$2) {
   }

   public CrashReportCategory fillReportDetails(CrashReport $$0) {
      CrashReportCategory $$1 = $$0.addCategory("Affected level", 1);
      $$1.setDetail("All players", (CrashReportDetail<String>)(() -> {
         List<? extends Player> $$0xx = this.players();
         return $$0xx.size() + " total; " + (String)$$0xx.stream().map(Player::debugInfo).collect(Collectors.joining(", "));
      }));
      $$1.setDetail("Chunk stats", this.getChunkSource()::gatherStats);
      $$1.setDetail("Level dimension", (CrashReportDetail<String>)(() -> this.dimension().location().toString()));

      try {
         this.levelData.fillCrashReportCategory($$1, this);
      } catch (Throwable var4) {
         $$1.setDetailError("Level Data Unobtainable", var4);
      }

      return $$1;
   }

   public abstract void destroyBlockProgress(int var1, BlockPos var2, int var3);

   public void createFireworks(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5, List<FireworkExplosion> $$6) {
   }

   public abstract Scoreboard getScoreboard();

   public void updateNeighbourForOutputSignal(BlockPos $$0, Block $$1) {
      for(Direction $$2 : Direction.Plane.HORIZONTAL) {
         BlockPos $$3 = $$0.relative($$2);
         if (this.hasChunkAt($$3)) {
            BlockState $$4 = this.getBlockState($$3);
            if ($$4.is(Blocks.COMPARATOR)) {
               this.neighborChanged($$4, $$3, $$1, null, false);
            } else if ($$4.isRedstoneConductor(this, $$3)) {
               $$3 = $$3.relative($$2);
               $$4 = this.getBlockState($$3);
               if ($$4.is(Blocks.COMPARATOR)) {
                  this.neighborChanged($$4, $$3, $$1, null, false);
               }
            }
         }
      }
   }

   @Override
   public DifficultyInstance getCurrentDifficultyAt(BlockPos $$0) {
      long $$1 = 0L;
      float $$2 = 0.0F;
      if (this.hasChunkAt($$0)) {
         $$2 = this.getMoonBrightness();
         $$1 = this.getChunkAt($$0).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), $$1, $$2);
   }

   @Override
   public int getSkyDarken() {
      return this.skyDarken;
   }

   public void setSkyFlashTime(int $$0) {
   }

   public void sendPacketToServer(Packet<?> $$0) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   @Override
   public DimensionType dimensionType() {
      return (DimensionType)this.dimensionTypeRegistration.value();
   }

   public Holder<DimensionType> dimensionTypeRegistration() {
      return this.dimensionTypeRegistration;
   }

   public ResourceKey<Level> dimension() {
      return this.dimension;
   }

   @Override
   public RandomSource getRandom() {
      return this.random;
   }

   @Override
   public boolean isStateAtPosition(BlockPos $$0, Predicate<BlockState> $$1) {
      return $$1.test(this.getBlockState($$0));
   }

   @Override
   public boolean isFluidAtPosition(BlockPos $$0, Predicate<FluidState> $$1) {
      return $$1.test(this.getFluidState($$0));
   }

   public abstract RecipeAccess recipeAccess();

   public BlockPos getBlockRandomPos(int $$0, int $$1, int $$2, int $$3) {
      this.randValue = this.randValue * 3 + 1013904223;
      int $$4 = this.randValue >> 2;
      return new BlockPos($$0 + ($$4 & 15), $$1 + ($$4 >> 16 & $$3), $$2 + ($$4 >> 8 & 15));
   }

   public boolean noSave() {
      return false;
   }

   @Override
   public BiomeManager getBiomeManager() {
      return this.biomeManager;
   }

   public final boolean isDebug() {
      return this.isDebug;
   }

   protected abstract LevelEntityGetter<Entity> getEntities();

   @Override
   public long nextSubTickCount() {
      return (long)(this.subTickCount++);
   }

   @Override
   public RegistryAccess registryAccess() {
      return this.registryAccess;
   }

   public DamageSources damageSources() {
      return this.damageSources;
   }

   public abstract PotionBrewing potionBrewing();

   public abstract FuelValues fuelValues();

   public int getClientLeafTintColor(BlockPos $$0) {
      return 0;
   }

   public PalettedContainerFactory palettedContainerFactory() {
      return this.palettedContainerFactory;
   }

   public static enum ExplosionInteraction implements StringRepresentable {
      NONE("none"),
      BLOCK("block"),
      MOB("mob"),
      TNT("tnt"),
      TRIGGER("trigger");

      public static final Codec<Level.ExplosionInteraction> CODEC = StringRepresentable.fromEnum(Level.ExplosionInteraction::values);
      private final String id;

      private ExplosionInteraction(final String param3) {
         this.id = $$0;
      }

      @Override
      public String getSerializedName() {
         return this.id;
      }
   }
}
