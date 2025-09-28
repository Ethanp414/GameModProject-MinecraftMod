package Entity.custom;

import Entity.ModEntities;
import dibs.bossfight.DePaulDibsBossFight;
import io.netty.util.AttributeMap;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage.Player;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.registries.DeferredItem;

public class GeckoEntity extends Animal
{

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimemout = 0;

    public GeckoEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        //TODO Auto-generated constructor stub
    }

    public GeckoEntity(EntityType<? extends GeckoEntity> type, Level level, double x, double y, double z) 
    {
        this(type, level);
        this.setPos(x, y, z);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Animal.createLivingAttributes()
        .add(Attributes.MAX_HEALTH, 10d)
        .add(Attributes.MOVEMENT_SPEED, 0.25d)
        .add(Attributes.FOLLOW_RANGE, 24d);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COCOA_BEANS);
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
    public void tick() {
        super.tick();

        if(this.level().isClientSide())
        {
            this.setupAnimationStates();
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.GECKO.get().create(level, getSpawnType());
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
