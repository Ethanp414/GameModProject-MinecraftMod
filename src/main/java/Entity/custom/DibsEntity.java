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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.world.entity.player.Player;

public class DibsEntity extends Monster implements GeoEntity
{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent bossEvent =
        new ServerBossEvent(
            Component.translatable("entity.depauldibsbossfight.dibs"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS
        );

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimemout = 0;

    public DibsEntity(EntityType<? extends Monster> entityType, Level level) 
    {
        super(entityType, level);
        bossEvent.setVisible(true);
    }

    public DibsEntity(EntityType<? extends DibsEntity> type, Level level, double x, double y, double z) 
    {
        this(type, level);
        this.setPos(x, y, z);
        bossEvent.setVisible(true);
        bossEvent.setDarkenScreen(true);
        bossEvent.setCreateWorldFog(true);
    }

    @Override
    protected void registerGoals() 
    {
        this.goalSelector.addGoal(1, new DibsCombatGoal(this, 2.5, 15, 1));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Animal.createLivingAttributes()
        .add(Attributes.MAX_HEALTH, 10d)
        .add(Attributes.MOVEMENT_SPEED, 0.25d)
        .add(Attributes.FOLLOW_RANGE, 24d)
        .add(Attributes.ATTACK_DAMAGE, 4d)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1d);
    }

    private void setupAnimationStates()
    {
        if(this.idleAnimationTimemout <= 0)
        {
            this.idleAnimationTimemout = 80;
            this.idleAnimationState.start(this.tickCount);
        }
        else
        {
            --this.idleAnimationTimemout;
        }
    }

    @Override
    public void tick() 
    {
        super.tick();

        if(this.level().isClientSide())
        {
            this.setupAnimationStates();
        }

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

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        //empty for now no animations
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
