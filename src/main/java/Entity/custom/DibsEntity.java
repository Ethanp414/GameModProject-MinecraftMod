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
import net.minecraft.sounds.SoundEvent;     //Added by Evan
import net.minecraft.sounds.SoundSource;

public class DibsEntity extends Monster implements GeoEntity
{
    //Animations
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

    public DibsEntity(EntityType<? extends Monster> entityType, Level level) 
    {
        super(entityType, level);
        bossEvent.setVisible(true);
        bossEvent.setDarkenScreen(true);
        controller = new AnimationController<>("testing", 20, this::testAnimController);
        //bossEvent.setCreateWorldFog(true);
    }

    public DibsEntity(EntityType<? extends DibsEntity> type, Level level, double x, double y, double z) 
    {
        this(type, level);
        this.setPos(x, y, z);
        bossEvent.setVisible(true);
        bossEvent.setDarkenScreen(true);
        //bossEvent.setCreateWorldFog(true);
        controller = new AnimationController<>("testing", 20, this::testAnimController);
    }

    @Override
    protected void registerGoals() 
    {
        this.goalSelector.addGoal(1, new DibsCombatGoal(this, 2.5, 15, 1, controller));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Animal.createLivingAttributes()
        .add(Attributes.MAX_HEALTH, 4d) //400
        .add(Attributes.MOVEMENT_SPEED, 0.25d)
        .add(Attributes.FOLLOW_RANGE, 24d)
        .add(Attributes.ATTACK_DAMAGE, 5d)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1d);
    }

    @Override
    public void tick() 
    {
        super.tick();

        if (!level().isClientSide) {
            float progress = this.getHealth() / this.getMaxHealth();

            // clamp to [0,1] in case of rounding
            if (progress < 0f) progress = 0f;
            if (progress > 1f) progress = 1f;
            bossEvent.setProgress(progress);

            // Optional: dynamic title (e.g., phase)
            // bossEvent.setName(Component.literal("My Boss - Phase " + currentPhase));
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) 
    {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) 
    {
        super.stopSeenByPlayer(serverPlayer);
        bossEvent.removePlayer(serverPlayer);
    }

    @Override
    public void remove(RemovalReason reason) 
    {
        super.remove(reason);   
        if (!level().isClientSide) {
            bossEvent.removeAllPlayers(); // clean up when the entity is gone
        }
    }

    //might be required?
    @Override
    protected void readAdditionalSaveData(ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {}

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) 
    {
        super.defineSynchedData(builder);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) 
    {
        return super.hurtServer(level, damageSource, amount);
    }

    
    protected <E extends GeoAnimatable> PlayState testAnimController(final AnimationTest<E> animTest) 
    {
        if (animTest.isMoving())
        {
            return animTest.setAndContinue(walk_anim);
        }
        else if(!animTest.isMoving())
        {
            return animTest.setAndContinue(idle_anim);
        }

        return PlayState.STOP;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        //controller = new AnimationController<>("testing", 20, this::testAnimController);   
        controllers.add(controller);  
        
        controller.triggerableAnim("punchAnim", punch_anim);
    }

    public static AnimationController GetAnimController()
    {
        return controller;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
protected SoundEvent getAmbientSound() {
    // Idle mumbling while roaming/idle
    return ModSounds.DIBS_MUMBLE.get();
}

@Override
protected SoundEvent getHurtSound(DamageSource source) {
    // Will randomly pick from hurt1/2/3 as defined in sounds.json
    return ModSounds.DIBS_HURT.get();
}

@Override
protected SoundEvent getDeathSound() {
    return ModSounds.DIBS_DEATH.get();
}

@Override
public SoundSource getSoundSource() {
    // Uses the Hostile Creatures volume slider
    return SoundSource.HOSTILE;
}

}
