package net.minecraft.world.level.block.entity.trialspawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.slf4j.Logger;

public final class TrialSpawner {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
   private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 36000;
   private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;
   private static final int MAX_MOB_TRACKING_DISTANCE = 47;
   private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);
   private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;
   private final TrialSpawnerStateData data = new TrialSpawnerStateData();
   private TrialSpawner.FullConfig config;
   private final TrialSpawner.StateAccessor stateAccessor;
   private PlayerDetector playerDetector;
   private final PlayerDetector.EntitySelector entitySelector;
   private boolean overridePeacefulAndMobSpawnRule;
   private boolean isOminous;

   public TrialSpawner(TrialSpawner.FullConfig $$0, TrialSpawner.StateAccessor $$1, PlayerDetector $$2, PlayerDetector.EntitySelector $$3) {
      this.config = $$0;
      this.stateAccessor = $$1;
      this.playerDetector = $$2;
      this.entitySelector = $$3;
   }

   public TrialSpawnerConfig activeConfig() {
      return this.isOminous ? (TrialSpawnerConfig)this.config.ominous().value() : (TrialSpawnerConfig)this.config.normal.value();
   }

   public TrialSpawnerConfig normalConfig() {
      return (TrialSpawnerConfig)this.config.normal.value();
   }

   public TrialSpawnerConfig ominousConfig() {
      return (TrialSpawnerConfig)this.config.ominous.value();
   }

   public void load(ValueInput $$0) {
      $$0.read(TrialSpawnerStateData.Packed.MAP_CODEC).ifPresent(this.data::apply);
      this.config = (TrialSpawner.FullConfig)$$0.read(TrialSpawner.FullConfig.MAP_CODEC).orElse(TrialSpawner.FullConfig.DEFAULT);
   }

   public void store(ValueOutput $$0) {
      $$0.store(TrialSpawnerStateData.Packed.MAP_CODEC, this.data.pack());
      $$0.store(TrialSpawner.FullConfig.MAP_CODEC, this.config);
   }

   public void applyOminous(ServerLevel $$0, BlockPos $$1) {
      $$0.setBlock($$1, $$0.getBlockState($$1).setValue(TrialSpawnerBlock.OMINOUS, Boolean.valueOf(true)), 3);
      $$0.levelEvent(3020, $$1, 1);
      this.isOminous = true;
      this.data.resetAfterBecomingOminous(this, $$0);
   }

   public void removeOminous(ServerLevel $$0, BlockPos $$1) {
      $$0.setBlock($$1, $$0.getBlockState($$1).setValue(TrialSpawnerBlock.OMINOUS, Boolean.valueOf(false)), 3);
      this.isOminous = false;
   }

   public boolean isOminous() {
      return this.isOminous;
   }

   public int getTargetCooldownLength() {
      return this.config.targetCooldownLength;
   }

   public int getRequiredPlayerRange() {
      return this.config.requiredPlayerRange;
   }

   public TrialSpawnerState getState() {
      return this.stateAccessor.getState();
   }

   public TrialSpawnerStateData getStateData() {
      return this.data;
   }

   public void setState(Level $$0, TrialSpawnerState $$1) {
      this.stateAccessor.setState($$0, $$1);
   }

   public void markUpdated() {
      this.stateAccessor.markUpdated();
   }

   public PlayerDetector getPlayerDetector() {
      return this.playerDetector;
   }

   public PlayerDetector.EntitySelector getEntitySelector() {
      return this.entitySelector;
   }

   public boolean canSpawnInLevel(ServerLevel $$0) {
      if (!$$0.getServer().getGameRules().getBoolean(GameRules.RULE_SPAWNER_BLOCKS_ENABLED)) {
         return false;
      } else if (this.overridePeacefulAndMobSpawnRule) {
         return true;
      } else {
         return $$0.getDifficulty() == Difficulty.PEACEFUL ? false : $$0.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
      }
   }

   public Optional<UUID> spawnMob(ServerLevel $$0, BlockPos $$1) {
      RandomSource $$2 = $$0.getRandom();
      SpawnData $$3 = this.data.getOrCreateNextSpawnData(this, $$0.getRandom());

      Optional var24;
      try (ProblemReporter.ScopedCollector $$4 = new ProblemReporter.ScopedCollector(() -> "spawner@" + $$1, LOGGER)) {
         ValueInput $$5 = TagValueInput.create($$4, $$0.registryAccess(), $$3.entityToSpawn());
         Optional<EntityType<?>> $$6 = EntityType.by($$5);
         if ($$6.isEmpty()) {
            return Optional.empty();
         }

         Vec3 $$7 = (Vec3)$$5.read("Pos", Vec3.CODEC)
            .orElseGet(
               () -> {
                  TrialSpawnerConfig $$2xx = this.activeConfig();
                  return new Vec3(
                     (double)$$1.getX() + ($$2.nextDouble() - $$2.nextDouble()) * (double)$$2xx.spawnRange() + 0.5,
                     (double)($$1.getY() + $$2.nextInt(3) - 1),
                     (double)$$1.getZ() + ($$2.nextDouble() - $$2.nextDouble()) * (double)$$2xx.spawnRange() + 0.5
                  );
               }
            );
         if (!$$0.noCollision(((EntityType)$$6.get()).getSpawnAABB($$7.x, $$7.y, $$7.z))) {
            return Optional.empty();
         }

         if (!inLineOfSight($$0, $$1.getCenter(), $$7)) {
            return Optional.empty();
         }

         BlockPos $$8 = BlockPos.containing($$7);
         if (!SpawnPlacements.checkSpawnRules((EntityType)$$6.get(), $$0, EntitySpawnReason.TRIAL_SPAWNER, $$8, $$0.getRandom())) {
            return Optional.empty();
         }

         if ($$3.getCustomSpawnRules().isPresent()) {
            SpawnData.CustomSpawnRules $$9 = (SpawnData.CustomSpawnRules)$$3.getCustomSpawnRules().get();
            if (!$$9.isValidPosition($$8, $$0)) {
               return Optional.empty();
            }
         }

         Entity $$10 = EntityType.loadEntityRecursive($$5, $$0, EntitySpawnReason.TRIAL_SPAWNER, $$2x -> {
            $$2x.snapTo($$7.x, $$7.y, $$7.z, $$2.nextFloat() * 360.0F, 0.0F);
            return $$2x;
         });
         if ($$10 == null) {
            return Optional.empty();
         }

         if ($$10 instanceof Mob $$11) {
            if (!$$11.checkSpawnObstruction($$0)) {
               return Optional.empty();
            }

            boolean $$12 = $$3.getEntityToSpawn().size() == 1 && $$3.getEntityToSpawn().getString("id").isPresent();
            if ($$12) {
               $$11.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$11.blockPosition()), EntitySpawnReason.TRIAL_SPAWNER, null);
            }

            $$11.setPersistenceRequired();
            $$3.getEquipment().ifPresent($$11::equip);
         }

         if (!$$0.tryAddFreshEntityWithPassengers($$10)) {
            return Optional.empty();
         }

         TrialSpawner.FlameParticle $$13 = this.isOminous ? TrialSpawner.FlameParticle.OMINOUS : TrialSpawner.FlameParticle.NORMAL;
         $$0.levelEvent(3011, $$1, $$13.encode());
         $$0.levelEvent(3012, $$8, $$13.encode());
         $$0.gameEvent($$10, GameEvent.ENTITY_PLACE, $$8);
         var24 = Optional.of($$10.getUUID());
      }

      return var24;
   }

   public void ejectReward(ServerLevel $$0, BlockPos $$1, ResourceKey<LootTable> $$2) {
      LootTable $$3 = $$0.getServer().reloadableRegistries().getLootTable($$2);
      LootParams $$4 = new LootParams.Builder($$0).create(LootContextParamSets.EMPTY);
      ObjectArrayList<ItemStack> $$5 = $$3.getRandomItems($$4);
      if (!$$5.isEmpty()) {
         for(ItemStack $$6 : $$5) {
            DefaultDispenseItemBehavior.spawnItem($$0, $$6, 2, Direction.UP, Vec3.atBottomCenterOf($$1).relative(Direction.UP, 1.2));
         }

         $$0.levelEvent(3014, $$1, 0);
      }
   }

   public void tickClient(Level $$0, BlockPos $$1, boolean $$2) {
      TrialSpawnerState $$3 = this.getState();
      $$3.emitParticles($$0, $$1, $$2);
      if ($$3.hasSpinningMob()) {
         double $$4 = (double)Math.max(0L, this.data.nextMobSpawnsAt - $$0.getGameTime());
         this.data.oSpin = this.data.spin;
         this.data.spin = (this.data.spin + $$3.spinningMobSpeed() / ($$4 + 200.0)) % 360.0;
      }

      if ($$3.isCapableOfSpawning()) {
         RandomSource $$5 = $$0.getRandom();
         if ($$5.nextFloat() <= 0.02F) {
            SoundEvent $$6 = $$2 ? SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS : SoundEvents.TRIAL_SPAWNER_AMBIENT;
            $$0.playLocalSound($$1, $$6, SoundSource.BLOCKS, $$5.nextFloat() * 0.25F + 0.75F, $$5.nextFloat() + 0.5F, false);
         }
      }
   }

   public void tickServer(ServerLevel $$0, BlockPos $$1, boolean $$2) {
      this.isOminous = $$2;
      TrialSpawnerState $$3 = this.getState();
      if (this.data.currentMobs.removeIf($$2x -> shouldMobBeUntracked($$0, $$1, $$2x))) {
         this.data.nextMobSpawnsAt = $$0.getGameTime() + (long)this.activeConfig().ticksBetweenSpawn();
      }

      TrialSpawnerState $$4 = $$3.tickAndGetNext($$1, this, $$0);
      if ($$4 != $$3) {
         this.setState($$0, $$4);
      }
   }

   private static boolean shouldMobBeUntracked(ServerLevel $$0, BlockPos $$1, UUID $$2) {
      Entity $$3 = $$0.getEntity($$2);
      return $$3 == null
         || !$$3.isAlive()
         || !$$3.level().dimension().equals($$0.dimension())
         || $$3.blockPosition().distSqr($$1) > (double)MAX_MOB_TRACKING_DISTANCE_SQR;
   }

   private static boolean inLineOfSight(Level $$0, Vec3 $$1, Vec3 $$2) {
      BlockHitResult $$3 = $$0.clip(new ClipContext($$2, $$1, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
      return $$3.getBlockPos().equals(BlockPos.containing($$1)) || $$3.getType() == HitResult.Type.MISS;
   }

   public static void addSpawnParticles(Level $$0, BlockPos $$1, RandomSource $$2, SimpleParticleType $$3) {
      for(int $$4 = 0; $$4 < 20; ++$$4) {
         double $$5 = (double)$$1.getX() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
         double $$6 = (double)$$1.getY() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
         double $$7 = (double)$$1.getZ() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
         $$0.addParticle(ParticleTypes.SMOKE, $$5, $$6, $$7, 0.0, 0.0, 0.0);
         $$0.addParticle($$3, $$5, $$6, $$7, 0.0, 0.0, 0.0);
      }
   }

   public static void addBecomeOminousParticles(Level $$0, BlockPos $$1, RandomSource $$2) {
      for(int $$3 = 0; $$3 < 20; ++$$3) {
         double $$4 = (double)$$1.getX() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
         double $$5 = (double)$$1.getY() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
         double $$6 = (double)$$1.getZ() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
         double $$7 = $$2.nextGaussian() * 0.02;
         double $$8 = $$2.nextGaussian() * 0.02;
         double $$9 = $$2.nextGaussian() * 0.02;
         $$0.addParticle(ParticleTypes.TRIAL_OMEN, $$4, $$5, $$6, $$7, $$8, $$9);
         $$0.addParticle(ParticleTypes.SOUL_FIRE_FLAME, $$4, $$5, $$6, $$7, $$8, $$9);
      }
   }

   public static void addDetectPlayerParticles(Level $$0, BlockPos $$1, RandomSource $$2, int $$3, ParticleOptions $$4) {
      for(int $$5 = 0; $$5 < 30 + Math.min($$3, 10) * 5; ++$$5) {
         double $$6 = (double)(2.0F * $$2.nextFloat() - 1.0F) * 0.65;
         double $$7 = (double)(2.0F * $$2.nextFloat() - 1.0F) * 0.65;
         double $$8 = (double)$$1.getX() + 0.5 + $$6;
         double $$9 = (double)$$1.getY() + 0.1 + (double)$$2.nextFloat() * 0.8;
         double $$10 = (double)$$1.getZ() + 0.5 + $$7;
         $$0.addParticle($$4, $$8, $$9, $$10, 0.0, 0.0, 0.0);
      }
   }

   public static void addEjectItemParticles(Level $$0, BlockPos $$1, RandomSource $$2) {
      for(int $$3 = 0; $$3 < 20; ++$$3) {
         double $$4 = (double)$$1.getX() + 0.4 + $$2.nextDouble() * 0.2;
         double $$5 = (double)$$1.getY() + 0.4 + $$2.nextDouble() * 0.2;
         double $$6 = (double)$$1.getZ() + 0.4 + $$2.nextDouble() * 0.2;
         double $$7 = $$2.nextGaussian() * 0.02;
         double $$8 = $$2.nextGaussian() * 0.02;
         double $$9 = $$2.nextGaussian() * 0.02;
         $$0.addParticle(ParticleTypes.SMALL_FLAME, $$4, $$5, $$6, $$7, $$8, $$9 * 0.25);
         $$0.addParticle(ParticleTypes.SMOKE, $$4, $$5, $$6, $$7, $$8, $$9);
      }
   }

   public void overrideEntityToSpawn(EntityType<?> $$0, Level $$1) {
      this.data.reset();
      this.config = this.config.overrideEntity($$0);
      this.setState($$1, TrialSpawnerState.INACTIVE);
   }

   @Deprecated(
      forRemoval = true
   )
   @VisibleForTesting
   public void setPlayerDetector(PlayerDetector $$0) {
      this.playerDetector = $$0;
   }

   @Deprecated(
      forRemoval = true
   )
   @VisibleForTesting
   public void overridePeacefulAndMobSpawnRule() {
      this.overridePeacefulAndMobSpawnRule = true;
   }

   public static enum FlameParticle {
      NORMAL(ParticleTypes.FLAME),
      OMINOUS(ParticleTypes.SOUL_FIRE_FLAME);

      public final SimpleParticleType particleType;

      private FlameParticle(final SimpleParticleType param3) {
         this.particleType = $$0;
      }

      public static TrialSpawner.FlameParticle decode(int $$0) {
         TrialSpawner.FlameParticle[] $$1 = values();
         return $$0 <= $$1.length && $$0 >= 0 ? $$1[$$0] : NORMAL;
      }

      public int encode() {
         return this.ordinal();
      }
   }

   public static record FullConfig(Holder<TrialSpawnerConfig> normal, Holder<TrialSpawnerConfig> ominous, int targetCooldownLength, int requiredPlayerRange) {
      final Holder<TrialSpawnerConfig> normal;
      final Holder<TrialSpawnerConfig> ominous;
      final int targetCooldownLength;
      final int requiredPlayerRange;
      public static final MapCodec<TrialSpawner.FullConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  TrialSpawnerConfig.CODEC
                     .optionalFieldOf("normal_config", Holder.direct(TrialSpawnerConfig.DEFAULT))
                     .forGetter(TrialSpawner.FullConfig::normal),
                  TrialSpawnerConfig.CODEC
                     .optionalFieldOf("ominous_config", Holder.direct(TrialSpawnerConfig.DEFAULT))
                     .forGetter(TrialSpawner.FullConfig::ominous),
                  ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("target_cooldown_length", 36000).forGetter(TrialSpawner.FullConfig::targetCooldownLength),
                  Codec.intRange(1, 128).optionalFieldOf("required_player_range", 14).forGetter(TrialSpawner.FullConfig::requiredPlayerRange)
               )
               .apply($$0, TrialSpawner.FullConfig::new)
      );
      public static final TrialSpawner.FullConfig DEFAULT = new TrialSpawner.FullConfig(
         Holder.direct(TrialSpawnerConfig.DEFAULT), Holder.direct(TrialSpawnerConfig.DEFAULT), 36000, 14
      );

      public TrialSpawner.FullConfig overrideEntity(EntityType<?> $$0) {
         return new TrialSpawner.FullConfig(
            Holder.direct(((TrialSpawnerConfig)this.normal.value()).withSpawning($$0)),
            Holder.direct(((TrialSpawnerConfig)this.ominous.value()).withSpawning($$0)),
            this.targetCooldownLength,
            this.requiredPlayerRange
         );
      }
   }

   public interface StateAccessor {
      void setState(Level var1, TrialSpawnerState var2);

      TrialSpawnerState getState();

      void markUpdated();
   }
}
