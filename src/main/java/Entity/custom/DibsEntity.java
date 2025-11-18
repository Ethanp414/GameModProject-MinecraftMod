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

public class DibsEntity extends Monster implements GeoEntity {

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

    // --------------------------
    // EXTRA SOUND STATE (NEW)
    // --------------------------
    private int idleSoundTimer = 0;
    private int stepGate = 0;
    private int hurtGate = 0;

    private static final int IDLE_INTERVAL_TICKS = 140;  // every ~7 seconds
    private static final double IDLE_SOUND_RANGE = 48.0;

    public DibsEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        bossEvent.setVisible(true);
        bossEvent.setDarkenScreen(true);
        controller = new AnimationController<>("testing", 20, this::testAnimController);
    }

    public DibsEntity(EntityType<? extends DibsEntity> type, Level level, double x, double y, double z) {
        this(type, level);
        this.setPos(x, y, z);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new DibsCombatGoal(this, 2.5, 15, 1, controller));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 400d)
            .add(Attributes.MOVEMENT_SPEED, 0.25d)
            .add(Attributes.FOLLOW_RANGE, 24d)
            .add(Attributes.ATTACK_DAMAGE, 5d)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1d);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {

            // Boss bar
            float progress = this.getHealth() / this.getMaxHealth();
            progress = Math.max(0, Math.min(1, progress));
            bossEvent.setProgress(progress);

            // -------- IDLE / ROAMING SOUND LOGIC --------
            idleSoundTimer++;
            if (idleSoundTimer >= IDLE_INTERVAL_TICKS) {
                idleSoundTimer = 0;

                Player nearby = level().getNearestPlayer(this, IDLE_SOUND_RANGE);
                if (nearby != null) {

                    int r = this.getRandom().nextInt(4);

                    if (r == 0) {
                        this.playSound(ModSounds.DIBS_ROAR.get(), 1.7F, 1.0F);
                    } else if (r == 1) {
                        this.playSound(ModSounds.DIBS_SIGH1.get(), 1.2F, 1.0F);
                    } else if (r == 2) {
                        this.playSound(ModSounds.DIBS_SIGH2.get(), 1.2F, 1.0F);
                    } 
                }
            }
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
            bossEvent.removeAllPlayers();
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {}

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    protected <E extends GeoAnimatable> PlayState testAnimController(final AnimationTest<E> animTest) {
        if (animTest.isMoving()) {
            return animTest.setAndContinue(walk_anim);
        } else {
            return animTest.setAndContinue(idle_anim);
        }
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
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

    // ---------------------------------------
    // SOUND OVERRIDES
    // ---------------------------------------



    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DIBS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DIBS_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        stepGate++;
        if (stepGate == 2) {
            this.playSound(ModSounds.DIBS_FOOTSTEP.get(), 1.0F, 1.0F);
            stepGate = 0;
        }
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        hurtGate++;
        if ((hurtGate & 1) == 1) {
            this.playSound(ModSounds.DIBS_HURT.get(), 1.0F, 1.0F);
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);

        if (!this.level().isClientSide) {
            this.level().playSound(
                null,
                this.blockPosition(),
                ModSounds.BOSSDEFEAT1.get(),
                SoundSource.MUSIC,
                1.0F,
                1.0F
            );
        }
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }
}
