package Entity.custom;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.player.Player;

public class DibsEntity extends Monster
{
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimemout = 0;

    public DibsEntity(EntityType<? extends Monster> entityType, Level level) 
    {
        super(entityType, level);
        //TODO Auto-generated constructor stub
    }

    public DibsEntity(EntityType<? extends DibsEntity> type, Level level, double x, double y, double z) 
    {
        this(type, level);
        this.setPos(x, y, z);
    }

    @Override
    protected void registerGoals() 
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Animal.createLivingAttributes()
        .add(Attributes.MAX_HEALTH, 10d)
        .add(Attributes.MOVEMENT_SPEED, 0.25d)
        .add(Attributes.FOLLOW_RANGE, 24d);
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
}
