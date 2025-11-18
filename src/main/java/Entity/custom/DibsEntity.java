package Entity.custom;

import Entity.client.DibsCombatGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import dibs.bossfight.ModSounds;

public class DibsEntity extends Monster implements GeoEntity {

    // Animations
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static AnimationController<DibsEntity> controller;
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idol");
    protected static final RawAnimation PUNCH_ANIM = RawAnimation.begin().then("punch", Animation.LoopType.PLAY_ONCE);

    // Boss bar
    private final ServerBossEvent bossEvent =
        new ServerBossEvent(
            Component.translatable("entity.depauldibsbossfight.dibs"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS
        );

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    // Audio state
    private int hurtSoundCounter = 0;
    private int idleSoundTimer = 0;
    private int stepGate = 0;
    private static final int IDLE_INTERVAL_TICKS = 200;
    private static final double IDLE_RANGE = 48.0;

    // Synced data
    private static final EntityDataAccessor<Boolean> AGGRO =
            SynchedEntityData.defineId(DibsEntity.class, EntityDataSerializers.BOOLEAN);

    // Synced target ID so CLIENT knows whom Dibs is attacking
    private static final EntityDataAccessor<Integer> TARGET_ID =
            SynchedEntityData.defineId(DibsEntity.class, EntityDataSerializers.INT);

    // Track death position on client
private static final EntityDataAccessor<Integer> DEATH_X =
        SynchedEntityData.defineId(DibsEntity.class, EntityDataSerializers.INT);
private static final EntityDataAccessor<Integer> DEATH_Y =
        SynchedEntityData.defineId(DibsEntity.class, EntityDataSerializers.INT);
private static final EntityDataAccessor<Integer> DEATH_Z =
        SynchedEntityData.defineId(DibsEntity.class, EntityDataSerializers.INT);


    public DibsEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        bossEvent.setVisible(true);
        bossEvent.setDarkenScreen(true);
    }

    public DibsEntity(EntityType<? extends DibsEntity> type, Level level, double x, double y, double z) {
        this(type, level);
        this.setPos(x, y, z);
        bossEvent.setVisible(true);
        bossEvent.setDarkenScreen(true);
        bossEvent.setCreateWorldFog(true);
    }

    // Goals
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new DibsCombatGoal(this, 2.5, 15, 1, controller));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 10d)
            .add(Attributes.MOVEMENT_SPEED, 0.25d)
            .add(Attributes.FOLLOW_RANGE, 24d)
            .add(Attributes.ATTACK_DAMAGE, 1d)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1d);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }

        if (!level().isClientSide) {

            // Boss bar
            float progress = this.getHealth() / this.getMaxHealth();
            bossEvent.setProgress(Math.max(0, Math.min(1, progress)));

            // --- SERVER: determine target ---
            Entity target = this.getTarget();
            int targetId = -1;
            boolean combatNow = false;

            if (target instanceof Player p) {
                targetId = p.getId();
                combatNow = true;
            }

            // Sync to client
            this.entityData.set(TARGET_ID, targetId);
            this.entityData.set(AGGRO, combatNow);

            System.out.println("[SERVER] Target = " + target);
            System.out.println("[SERVER] Aggro = " + combatNow);

            // Idle sounds
            idleSoundTimer++;
            if (idleSoundTimer >= IDLE_INTERVAL_TICKS) {
                idleSoundTimer = 0;

                if (this.hurtTime <= 0) {
                    Player nearby = this.level().getNearestPlayer(this, IDLE_RANGE);
                    if (nearby != null) {
                        int r = this.getRandom().nextInt(3);
                        if (r == 0) {
                            this.playSound(ModSounds.DIBS_ROAR.get(), 1.0F, 1.0F);
                        } else if (r == 1) {
                            this.playSound(ModSounds.DIBS_SIGH1.get(), 1.0F, 1.0F);
                        } else {
                            this.playSound(ModSounds.DIBS_SIGH2.get(), 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    // Boss bar sync
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (!level().isClientSide) {
            bossEvent.removeAllPlayers();
        }
    }

    // Synced data
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AGGRO, Boolean.FALSE);
        builder.define(TARGET_ID, -1);
        builder.define(DEATH_X, 0);
builder.define(DEATH_Y, 0);
builder.define(DEATH_Z, 0);

    }

    // Getter for synced target
    public int getSyncedTargetId() {
        return this.entityData.get(TARGET_ID);
    }

    public boolean isAggroed() {
        return this.entityData.get(AGGRO);
    }

    private void setAggro(boolean value) {
        this.entityData.set(AGGRO, value);
    }

    // Animation
    protected <E extends GeoAnimatable> PlayState testAnimController(final AnimationTest<E> animTest) {
        if (animTest.isMoving()) {
            return animTest.setAndContinue(WALK_ANIM);
        } else {
            return animTest.setAndContinue(IDLE_ANIM);
        }
    }

    public BlockPos getDeathPos() {
    return new BlockPos(
        this.entityData.get(DEATH_X),
        this.entityData.get(DEATH_Y),
        this.entityData.get(DEATH_Z)
    );
}


    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controller = new AnimationController<>("testing", 20, this::testAnimController);
        controllers.add(controller);
        controller.triggerableAnim("punchAnim", PUNCH_ANIM);
    }

    public static AnimationController<DibsEntity> getAnimController() {
        return controller;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // Sounds
    @Override
    protected void playHurtSound(DamageSource source) {
        hurtSoundCounter++;
        if ((hurtSoundCounter & 1) == 1) {
            this.playSound(ModSounds.DIBS_HURT.get(), 1.0F, 1.0F);
        }
        idleSoundTimer = Math.max(idleSoundTimer, IDLE_INTERVAL_TICKS / 5);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        stepGate++;
        if (stepGate == 1) {
            this.playSound(ModSounds.DIBS_FOOTSTEP.get(), 1.0F, 1.0F);
        } else if (stepGate == 3) {
            this.playSound(ModSounds.DIBS_FOOTSTEP.get(), 1.0F, 1.0F);
            stepGate = 0;
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DIBS_HURT.get();
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

@Override
public void die(DamageSource cause) {
    super.die(cause);

    if (!this.level().isClientSide) {

        // Save death position for client
        BlockPos pos = this.blockPosition();
        this.entityData.set(DEATH_X, pos.getX());
        this.entityData.set(DEATH_Y, pos.getY());
        this.entityData.set(DEATH_Z, pos.getZ());

        // Play defeat sound on server
        this.level().playSound(
            null,
            pos,
            ModSounds.BOSSDEFEAT1.get(),
            SoundSource.MUSIC,
            1.0F, 1.0F
        );
    }
}


    
}
