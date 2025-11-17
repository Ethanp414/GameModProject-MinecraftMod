package Entity.custom;

import Entity.client.DibsCombatGoal;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
import net.minecraft.world.entity.player.Player;

import dibs.bossfight.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

// >>> ADDED imports for synced data flag
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;

public class DibsEntity extends Monster implements GeoEntity {

    // Animations
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    static AnimationController controller;
    protected static final RawAnimation walk_anim = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation idle_anim = RawAnimation.begin().thenLoop("idol");
    protected static final RawAnimation punch_anim = RawAnimation.begin().then("punch", Animation.LoopType.PLAY_ONCE);

    private final ServerBossEvent bossEvent =
        new ServerBossEvent(
            Component.translatable("entity.depauldibsbossfight.dibs"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimemout = 0;

    // ===== Audio state & tuning =====
    private int  hurtSoundCounter = 0;
    private int  idleSoundTimer   = 0;
    private int stepGate = 0;
    private static final int    IDLE_INTERVAL_TICKS = 200; // ~10s
    private static final double IDLE_RANGE          = 48.0; // player proximity for idle vocals

    // >>> ADDED: Client-visible ?쏿ggro??bit we?셪l keep synced
    private static final EntityDataAccessor<Boolean> AGGRO =
            SynchedEntityData.defineId(DibsEntity.class, EntityDataSerializers.BOOLEAN);

    public DibsEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        bossEvent.setVisible(true);
        bossEvent.setDarkenScreen(true);
        controller = new AnimationController<>("testing", 20, this::testAnimController);
        // bossEvent.setCreateWorldFog(true);
    }

    public DibsEntity(EntityType<? extends DibsEntity> type, Level level, double x, double y, double z) {
        this(type, level);
        this.setPos(x, y, z);
        bossEvent.setVisible(true);
        bossEvent.setDarkenScreen(true);
        bossEvent.setCreateWorldFog(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new DibsCombatGoal(this, 2.5, 15, 1, controller));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
        if (this.idleAnimationTimemout <= 0) {
            this.idleAnimationTimemout = 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimemout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }

        if (!level().isClientSide) {
            float progress = this.getHealth() / this.getMaxHealth();

            // clamp to [0,1] in case of rounding
            if (progress < 0f) progress = 0f;
            if (progress > 1f) progress = 1f;
            bossEvent.setProgress(progress);

            // >>> ADDED: update synced aggro flag *server-side*
            boolean combatNow = false;
            var tgt = this.getTarget();
            if (tgt instanceof Player p) {
                double d2 = this.distanceToSqr(p);
                // treat as aggro if we actually have a target and it's not super far
                combatNow = d2 <= (40.0 * 40.0);
            }
            setAggro(combatNow);

            // Idle roar / sigh / sigh2 every ~10 seconds when a player is nearby
            idleSoundTimer++;
            if (idleSoundTimer >= IDLE_INTERVAL_TICKS) { // ~10 seconds at 20 tps
                idleSoundTimer = 0;

                // Do not play idle vocal if currently in hurt animation
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

            // Optional: dynamic title (e.g., phase)
            // bossEvent.setName(Component.literal("My Boss - Phase " + currentPhase));
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!level().isClientSide) {
            bossEvent.removeAllPlayers(); // clean up when the entity is gone
        }
    }

    // might be required?
    @Override
    protected void readAdditionalSaveData(ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {}

    // >>> CHANGED: defineSynchedData to include AGGRO (kept your call to super)
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AGGRO, Boolean.FALSE);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return super.hurtServer(level, damageSource, amount);
    }

    protected <E extends GeoAnimatable> PlayState testAnimController(final AnimationTest<E> animTest) {
        if (animTest.isMoving()) {
            return animTest.setAndContinue(walk_anim);
        } else if (!animTest.isMoving()) {
            return animTest.setAndContinue(idle_anim);
        }

        return PlayState.STOP;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        // empty for now no animations
        // controller = new AnimationController<>("testing", 20, this::testAnimController);
        controllers.add(controller);

        controller.triggerableAnim("punchAnim", punch_anim);
    }

    public static AnimationController GetAnimController() {
        return controller;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // ---------------------------------------------------------------------
    // Sound overrides
    // ---------------------------------------------------------------------

    // Plays hurt1?? every other hit (1st, 3rd, 5th, ...)
    @Override
    protected void playHurtSound(DamageSource source) {
        hurtSoundCounter++;
        if ((hurtSoundCounter & 1) == 1) { // odd hits only
            this.playSound(ModSounds.DIBS_HURT.get(), 1.0F, 1.0F);
        }
        // keep idle vocals from overlapping right after a hurt
        idleSoundTimer = Math.max(idleSoundTimer, IDLE_INTERVAL_TICKS / 5); // ~2s grace
    }

    // Footstep sound for both feet
@Override
protected void playStepSound(BlockPos pos, BlockState state) {
    // Pattern 1, skip, 1, skip... but alternating 2,1,2,1 total-step gaps (≈1.5x longer)
    stepGate++;
    if (stepGate == 1) {
        this.playSound(ModSounds.DIBS_FOOTSTEP.get(), 1.0F, 1.0F);
    } else if (stepGate == 3) {
        this.playSound(ModSounds.DIBS_FOOTSTEP.get(), 1.0F, 1.0F);
        stepGate = 0; // reset every third call to get 2,1,2,1 spacing
    }
    // else: skip this step
}


    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        // Randomly picks from hurt1..4 as defined in sounds.json
        return ModSounds.DIBS_HURT.get();
    }

    

    @Override
    public SoundSource getSoundSource() {
        // Uses the Hostile Creatures volume slider
        return SoundSource.HOSTILE;
    }

    // >>> ADDED: client-readable aggro accessors (BossMusicController reads this)
    public boolean isAggroed() {
        return this.entityData.get(AGGRO);
    }
    private void setAggro(boolean value) {
        this.entityData.set(AGGRO, value);
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide) {
            this.level().playSound(
                null,
                this.blockPosition(),
                dibs.bossfight.ModSounds.BOSSDEFEAT1.get(),
                net.minecraft.sounds.SoundSource.MUSIC,
                1.0F, 1.0F
            );
        }
    }
}
