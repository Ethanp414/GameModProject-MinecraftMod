package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate.MovementType;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class EntityPotoo extends Animal implements IFalconry {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.m_135353_(EntityPotoo.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> PERCHING = SynchedEntityData.m_135353_(EntityPotoo.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.m_135353_(EntityPotoo.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.m_135353_(EntityPotoo.class, EntityDataSerializers.f_135039_);
   private static final EntityDataAccessor<Direction> PERCH_DIRECTION = SynchedEntityData.m_135353_(EntityPotoo.class, EntityDataSerializers.f_135040_);
   private static final EntityDataAccessor<Integer> MOUTH_TICK = SynchedEntityData.m_135353_(EntityPotoo.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> TEMP_BRIGHTNESS = SynchedEntityData.m_135353_(EntityPotoo.class, EntityDataSerializers.f_135028_);
   public final float[] ringBuffer = new float[64];
   public float prevFlyProgress;
   public float flyProgress;
   public float mouthProgress;
   public float prevMouthProgress;
   public float prevPerchProgress;
   public float perchProgress;
   public int ringBufferIndex = -1;
   private int lastScreamTimestamp;
   private int perchCooldown = 100;
   private boolean isLandNavigator;
   private int timeFlying;

   protected EntityPotoo(EntityType type, Level level) {
      super(type, level);
      this.switchNavigator(true);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 8.0).m_22268_(Attributes.f_22281_, 1.0).m_22268_(Attributes.f_22279_, 0.2F);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new TemptGoal(this, 1.0, Ingredient.m_204132_(AMTagRegistry.POTOO_BREEDABLES), false));
      this.f_21345_.m_25352_(2, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(3, new PanicGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new EntityPotoo.AIPerch());
      this.f_21345_.m_25352_(5, new EntityPotoo.AIMelee());
      this.f_21345_.m_25352_(6, new EntityPotoo.AIFlyIdle());
      this.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(this, EntityFly.class, 100, true, true, null));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new AdvancedPathNavigateNoTeleport(this, this.m_9236_(), false);
         this.isLandNavigator = true;
      } else {
         this.f_21342_ = new FlightMoveController(this, 0.6F, false, true);
         this.f_21344_ = new AdvancedPathNavigateNoTeleport(this, this.m_9236_(), MovementType.FLYING, false, false) {
            public boolean m_6342_(BlockPos pos) {
               return !this.f_26495_.m_8055_(pos.m_6625_(2)).m_60795_();
            }
         };
         this.f_21344_.m_7008_(false);
         this.isLandNavigator = false;
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(FLYING, false);
      this.f_19804_.m_135372_(PERCHING, false);
      this.f_19804_.m_135372_(PERCH_POS, Optional.empty());
      this.f_19804_.m_135372_(PERCH_DIRECTION, Direction.NORTH);
      this.f_19804_.m_135372_(SLEEPING, false);
      this.f_19804_.m_135372_(MOUTH_TICK, 0);
      this.f_19804_.m_135372_(TEMP_BRIGHTNESS, 0);
   }

   public boolean m_5803_() {
      return (Boolean)this.f_19804_.m_135370_(SLEEPING);
   }

   public void setSleeping(boolean sleeping) {
      this.f_19804_.m_135381_(SLEEPING, sleeping);
   }

   public static boolean canPotooSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return m_186209_(worldIn, pos);
   }

   public boolean m_6914_(LevelReader reader) {
      if (reader.m_45784_(this) && !reader.m_46855_(this.m_20191_())) {
         BlockPos blockpos = this.m_20183_();
         BlockState blockstate2 = reader.m_8055_(blockpos.m_7495_());
         return blockstate2.m_204336_(BlockTags.f_13035_) || blockstate2.m_204336_(BlockTags.f_13106_);
      } else {
         return false;
      }
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.potooSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevPerchProgress = this.perchProgress;
      this.prevMouthProgress = this.mouthProgress;
      this.prevFlyProgress = this.flyProgress;
      if (this.isFlying()) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (this.isPerching()) {
         if (this.perchProgress < 5.0F) {
            this.perchProgress++;
         }
      } else if (this.perchProgress > 0.0F) {
         this.perchProgress--;
      }

      if (this.ringBufferIndex < 0) {
         Arrays.fill(this.ringBuffer, 15.0F);
      }

      this.ringBufferIndex++;
      if (this.ringBufferIndex == this.ringBuffer.length) {
         this.ringBufferIndex = 0;
      }

      this.ringBuffer[this.ringBufferIndex] = ((Integer)this.f_19804_.m_135370_(TEMP_BRIGHTNESS)).intValue();
      if (this.perchCooldown > 0) {
         this.perchCooldown--;
      }

      if (!this.m_9236_().f_46443_) {
         this.f_19804_.m_135381_(TEMP_BRIGHTNESS, this.m_9236_().m_46803_(this.m_20183_()));
         if (this.isFlying()) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (this.isFlying()) {
            if (!this.m_20096_()) {
               if (!this.m_20072_()) {
                  this.m_20256_(this.m_20184_().m_82542_(1.0, 0.6F, 1.0));
               }
            } else if (this.timeFlying > 20) {
               this.setFlying(false);
            }

            this.timeFlying++;
         } else {
            this.timeFlying = 0;
         }

         if (this.isPerching() && !this.m_20160_()) {
            this.setSleeping(this.m_9236_().m_46461_() && (this.m_5448_() == null || !this.m_5448_().m_6084_()));
         } else if (this.m_5803_()) {
            this.setSleeping(false);
         }

         if (this.isPerching() && this.getPerchPos() != null) {
            if (this.m_9236_().m_8055_(this.getPerchPos()).m_204336_(AMTagRegistry.POTOO_PERCHES) && !(this.m_20238_(Vec3.m_82512_(this.getPerchPos())) > 2.25)
               )
             {
               this.slideTowardsPerch();
            } else {
               this.setPerching(false);
            }
         }
      }

      if ((Integer)this.f_19804_.m_135370_(MOUTH_TICK) > 0) {
         this.f_19804_.m_135381_(MOUTH_TICK, (Integer)this.f_19804_.m_135370_(MOUTH_TICK) - 1);
         if (this.mouthProgress < 5.0F) {
            this.mouthProgress++;
         }
      } else if (this.mouthProgress > 0.0F) {
         this.mouthProgress--;
      }

      if (!this.m_5803_() && (this.m_5448_() == null || !this.m_5448_().m_6084_())) {
         int j = this.f_19797_ - this.lastScreamTimestamp;
         if (this.getEyeScale(10, 1.0F) == 0.0F) {
            if (j > 40) {
               this.openMouth(30);
               this.m_216990_((SoundEvent)AMSoundRegistry.POTOO_CALL.get());
               this.m_146850_(GameEvent.f_223709_);
            }
         } else if (this.getEyeScale(10, 1.0F) < 7.0F && j > 300 && j % 300 == 0 && this.f_19796_.m_188503_(4) == 0) {
            this.openMouth(30);
            this.m_216990_((SoundEvent)AMSoundRegistry.POTOO_CALL.get());
            this.m_146850_(GameEvent.f_223709_);
         }
      }
   }

   @Override
   public float getHandOffset() {
      return 1.0F;
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.POTOO_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.POTOO_HURT.get();
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev && source.m_7640_() instanceof LivingEntity) {
         this.setPerching(false);
      }

      return prev;
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   public void m_6083_() {
      Entity entity = this.m_20202_();
      if (!this.m_20159_() || entity.m_6084_() && this.m_6084_()) {
         if (entity instanceof LivingEntity) {
            this.m_20334_(0.0, 0.0, 0.0);
            this.m_8119_();
            this.setFlying(false);
            this.setSleeping(false);
            this.setPerching(false);
            if (this.m_20159_()) {
               Entity mount = this.m_20202_();
               if (mount instanceof Player) {
                  float yawAdd = 0.0F;
                  if (((Player)mount).m_21120_(InteractionHand.MAIN_HAND).m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                     yawAdd = ((Player)mount).m_5737_() == HumanoidArm.LEFT ? 135.0F : -135.0F;
                  } else if (((Player)mount).m_21120_(InteractionHand.OFF_HAND).m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                     yawAdd = ((Player)mount).m_5737_() == HumanoidArm.LEFT ? -135.0F : 135.0F;
                  } else {
                     this.m_6038_();
                     this.m_20359_(mount);
                  }

                  float birdYaw = yawAdd * 0.5F;
                  this.f_20883_ = Mth.m_14177_(((LivingEntity)mount).f_20883_ + birdYaw);
                  this.m_146922_(Mth.m_14177_(mount.m_146908_() + birdYaw));
                  this.f_20885_ = Mth.m_14177_(((LivingEntity)mount).f_20885_ + birdYaw);
                  float radius = 0.6F;
                  float angle = (float) (Math.PI / 180.0) * (((LivingEntity)mount).f_20883_ - 180.0F + yawAdd);
                  double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
                  double extraZ = radius * Mth.m_14089_(angle);
                  this.m_6034_(mount.m_20185_() + extraX, Math.max(mount.m_20186_() + mount.m_20206_() * 0.45F, mount.m_20186_()), mount.m_20189_() + extraZ);
               }

               if (!mount.m_6084_()) {
                  this.m_6038_();
               }
            }
         } else {
            super.m_6083_();
         }
      } else {
         this.m_8127_();
      }
   }

   public void openMouth(int duration) {
      this.f_19804_.m_135381_(MOUTH_TICK, duration);
      this.lastScreamTimestamp = this.f_19797_;
   }

   public boolean isFlying() {
      return (Boolean)this.f_19804_.m_135370_(FLYING);
   }

   public void setFlying(boolean flying) {
      if (!flying || !this.m_6162_()) {
         this.f_19804_.m_135381_(FLYING, flying);
      }
   }

   public BlockPos getPerchPos() {
      return (BlockPos)((Optional)this.f_19804_.m_135370_(PERCH_POS)).orElse(null);
   }

   public void setPerchPos(BlockPos pos) {
      this.f_19804_.m_135381_(PERCH_POS, Optional.ofNullable(pos));
   }

   public Direction getPerchDirection() {
      return (Direction)this.f_19804_.m_135370_(PERCH_DIRECTION);
   }

   public void setPerchDirection(Direction direction) {
      this.f_19804_.m_135381_(PERCH_DIRECTION, direction);
   }

   public boolean isPerching() {
      return (Boolean)this.f_19804_.m_135370_(PERCHING);
   }

   public void setPerching(boolean perching) {
      this.f_19804_.m_135381_(PERCHING, perching);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("Flying", this.isFlying());
      compound.m_128379_("Perching", this.isPerching());
      compound.m_128405_("PerchDir", this.getPerchDirection().ordinal());
      if (this.getPerchPos() != null) {
         compound.m_128405_("PerchX", this.getPerchPos().m_123341_());
         compound.m_128405_("PerchY", this.getPerchPos().m_123342_());
         compound.m_128405_("PerchZ", this.getPerchPos().m_123343_());
      }
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.setFlying(compound.m_128471_("Flying"));
      this.setPerching(compound.m_128471_("Perching"));
      this.setPerchDirection(Direction.m_122376_(compound.m_128451_("PerchDir")));
      if (compound.m_128441_("PerchX") && compound.m_128441_("PerchY") && compound.m_128441_("PerchZ")) {
         this.setPerchPos(new BlockPos(compound.m_128451_("PerchX"), compound.m_128451_("PerchY"), compound.m_128451_("PerchZ")));
      }
   }

   public boolean isValidPerchFromSide(BlockPos pos, Direction direction) {
      BlockPos offset = pos.m_121945_(direction);
      BlockState state = this.m_9236_().m_8055_(pos);
      return state.m_204336_(AMTagRegistry.POTOO_PERCHES)
         && (!this.m_9236_().m_8055_(pos.m_7494_()).m_60838_(this.m_9236_(), pos.m_7494_()) || this.m_9236_().m_46859_(pos.m_7494_()))
         && (
            !this.m_9236_().m_8055_(offset).m_60838_(this.m_9236_(), offset) && !this.m_9236_().m_8055_(offset).m_204336_(AMTagRegistry.POTOO_PERCHES)
               || this.m_9236_().m_46859_(offset)
         );
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverLevel, AgeableMob ageableMob) {
      return (AgeableMob)((EntityType)AMEntityRegistry.POTOO.get()).m_20615_(serverLevel);
   }

   public float getEyeScale(int bufferOffset, float partialTicks) {
      int i = this.ringBufferIndex - bufferOffset & 63;
      int j = this.ringBufferIndex - bufferOffset - 1 & 63;
      float prevBuffer = this.ringBuffer[j];
      float buffer = this.ringBuffer[i];
      return prevBuffer + (buffer - prevBuffer) * partialTicks;
   }

   private void slideTowardsPerch() {
      Vec3 block = Vec3.m_82514_(this.getPerchPos(), 1.0);
      Vec3 look = block.m_82546_(this.m_20182_()).m_82541_();
      Vec3 onBlock = block.m_82520_(this.getPerchDirection().m_122429_() * 0.35F, 0.0, this.getPerchDirection().m_122431_() * 0.35F);
      Vec3 diff = onBlock.m_82546_(this.m_20182_());
      float f = (float)diff.m_82553_();
      float f1 = f > 1.0F ? 0.25F : f * 0.1F;
      Vec3 sub = diff.m_82541_().m_82490_(f1);
      float f2 = -((float)(Mth.m_14136_(look.f_82479_, look.f_82481_) * 180.0F / (float)Math.PI));
      this.m_146922_(f2);
      this.f_20885_ = f2;
      this.f_20883_ = f2;
      this.m_20256_(this.m_20184_().m_82549_(sub));
   }

   public BlockPos getToucanGround(BlockPos in) {
      BlockPos position = new BlockPos(in.m_123341_(), (int)this.m_20186_(), in.m_123343_());

      while (position.m_123342_() < 320 && !this.m_9236_().m_6425_(position).m_76178_()) {
         position = position.m_7494_();
      }

      while (position.m_123342_() > -64 && !this.m_9236_().m_8055_(position).m_280296_() && this.m_9236_().m_6425_(position).m_76178_()) {
         position = position.m_7495_();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = 10 + this.m_217043_().m_188503_(15);
      float neg = this.m_217043_().m_188499_() ? 1.0F : -1.0F;
      float renderYawOffset = this.f_20883_;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.m_217043_().m_188501_() * neg;
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.m_7096_() + extraX, this.m_20186_(), fleePos.m_7094_() + extraZ);
      BlockPos ground = this.getToucanGround(radialPos);
      if (ground.m_123342_() < -64) {
         return null;
      } else {
         ground = this.m_20183_();

         while (ground.m_123342_() > -64 && !this.m_9236_().m_8055_(ground).m_280296_()) {
            ground = ground.m_7495_();
         }

         return !this.isTargetBlocked(Vec3.m_82512_(ground.m_7494_())) ? Vec3.m_82512_(ground.m_7495_()) : null;
      }
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      Item item = itemstack.m_41720_();
      InteractionResult type = super.m_6071_(player, hand);
      if (!this.m_6162_()
         && this.getRidingFalcons(player) <= 0
         && (
            player.m_21120_(InteractionHand.MAIN_HAND).m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()
               || player.m_21120_(InteractionHand.OFF_HAND).m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()
         )) {
         this.f_19851_ = 30;
         this.m_20153_();
         this.m_7998_(player, true);
         if (!this.m_9236_().f_46443_) {
            AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.m_19879_(), player.m_19879_()));
         }

         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      return this.m_9236_().m_45547_(new ClipContext(Vector3d, target, Block.COLLIDER, Fluid.NONE, this)).m_6662_() != Type.MISS;
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = 5.0F + radiusAdd + this.m_217043_().m_188503_(5);
      float neg = this.m_217043_().m_188499_() ? 1.0F : -1.0F;
      float renderYawOffset = this.f_20883_;
      float angle = (float) (Math.PI / 180.0) * renderYawOffset + 3.15F + this.m_217043_().m_188501_() * neg;
      double extraX = radius * Mth.m_14031_((float) Math.PI + angle);
      double extraZ = radius * Mth.m_14089_(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.m_7096_() + extraX), 0, (int)(fleePos.m_7094_() + extraZ));
      BlockPos ground = this.getToucanGround(radialPos);
      int distFromGround = (int)this.m_20186_() - ground.m_123342_();
      int flightHeight = 5 + this.m_217043_().m_188503_(5);
      int j = this.m_217043_().m_188503_(5) + 5;
      BlockPos newPos = ground.m_6630_(distFromGround > 5 ? flightHeight : j);
      if (this.m_9236_().m_8055_(ground).m_204336_(BlockTags.f_13035_)) {
         newPos = ground.m_6630_(1 + this.m_217043_().m_188503_(3));
      }

      return !this.isTargetBlocked(Vec3.m_82512_(newPos)) && this.m_20238_(Vec3.m_82512_(newPos)) > 1.0 ? Vec3.m_82512_(newPos) : null;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.m_20183_();

      while (position.m_123342_() > -65 && this.m_9236_().m_46859_(position)) {
         position = position.m_7495_();
      }

      return !this.m_9236_().m_6425_(position).m_76178_() || this.m_9236_().m_8055_(position).m_60713_(Blocks.f_50191_) || position.m_123342_() <= -65;
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.POTOO_BREEDABLES);
   }

   @Override
   public void onLaunch(Player player, Entity pointedEntity) {
   }

   private class AIFlyIdle extends Goal {
      protected double x;
      protected double y;
      protected double z;

      public AIFlyIdle() {
         this.m_7021_(EnumSet.of(Flag.MOVE));
      }

      public boolean m_8036_() {
         if (EntityPotoo.this.m_20160_()
            || EntityPotoo.this.isPerching()
            || EntityPotoo.this.m_5448_() != null && EntityPotoo.this.m_5448_().m_6084_()
            || EntityPotoo.this.m_20159_()) {
            return false;
         } else if (EntityPotoo.this.m_217043_().m_188503_(45) != 0 && !EntityPotoo.this.isFlying()) {
            return false;
         } else {
            Vec3 lvt_1_1_ = this.getPosition();
            if (lvt_1_1_ == null) {
               return false;
            } else {
               this.x = lvt_1_1_.f_82479_;
               this.y = lvt_1_1_.f_82480_;
               this.z = lvt_1_1_.f_82481_;
               return true;
            }
         }
      }

      public void m_8037_() {
         EntityPotoo.this.m_21566_().m_6849_(this.x, this.y, this.z, 1.0);
         if (EntityPotoo.this.isFlying() && EntityPotoo.this.m_20096_() && EntityPotoo.this.timeFlying > 10) {
            EntityPotoo.this.setFlying(false);
         }
      }

      @javax.annotation.Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = EntityPotoo.this.m_20182_();
         return EntityPotoo.this.timeFlying >= 200 && !EntityPotoo.this.isOverWaterOrVoid()
            ? EntityPotoo.this.getBlockGrounding(vector3d)
            : EntityPotoo.this.getBlockInViewAway(vector3d, 0.0F);
      }

      public boolean m_8045_() {
         return EntityPotoo.this.isFlying() && EntityPotoo.this.m_20275_(this.x, this.y, this.z) > 5.0;
      }

      public void m_8056_() {
         EntityPotoo.this.setFlying(true);
         EntityPotoo.this.m_21566_().m_6849_(this.x, this.y, this.z, 1.0);
      }

      public void m_8041_() {
         EntityPotoo.this.m_21573_().m_26573_();
         this.x = 0.0;
         this.y = 0.0;
         this.z = 0.0;
         super.m_8041_();
      }
   }

   private class AIMelee extends Goal {
      private int biteCooldown = 0;

      public AIMelee() {
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean m_8036_() {
         return !EntityPotoo.this.m_5803_() && !EntityPotoo.this.m_20159_() && EntityPotoo.this.m_5448_() != null && EntityPotoo.this.m_5448_().m_6084_();
      }

      public void m_8037_() {
         if (this.biteCooldown > 0) {
            this.biteCooldown--;
         }

         LivingEntity entity = EntityPotoo.this.m_5448_();
         if (entity != null) {
            EntityPotoo.this.setFlying(true);
            EntityPotoo.this.setPerching(false);
            EntityPotoo.this.m_21566_().m_6849_(entity.m_20185_(), entity.m_20227_(0.5), entity.m_20189_(), 1.5);
            if (EntityPotoo.this.m_20270_(entity) < 1.4F) {
               if (this.biteCooldown == 0) {
                  EntityPotoo.this.openMouth(7);
                  this.biteCooldown = 10;
               }

               if (EntityPotoo.this.mouthProgress >= 4.5F) {
                  entity.m_6469_(EntityPotoo.this.m_269291_().m_269333_(EntityPotoo.this), 2.0F);
                  if (entity.m_20205_() <= 0.5F) {
                     entity.m_142687_(RemovalReason.KILLED);
                  }
               }
            }
         }
      }
   }

   private class AIPerch extends Goal {
      private BlockPos perch = null;
      private Direction perchDirection = null;
      private int perchingTime = 0;
      private int runCooldown = 0;
      private int pathRecalcTime = 0;

      public AIPerch() {
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean m_8036_() {
         if (EntityPotoo.this.m_5448_() != null && EntityPotoo.this.m_5448_().m_6084_()) {
            return false;
         } else {
            if (this.runCooldown > 0) {
               this.runCooldown--;
            } else if (!EntityPotoo.this.isPerching() && EntityPotoo.this.perchCooldown == 0 && EntityPotoo.this.f_19796_.m_188503_(35) == 0) {
               this.perchingTime = 0;
               if (EntityPotoo.this.getPerchPos() != null
                  && EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.getPerchPos(), EntityPotoo.this.getPerchDirection())) {
                  this.perch = EntityPotoo.this.getPerchPos();
                  this.perchDirection = EntityPotoo.this.getPerchDirection();
               } else {
                  this.findPerch();
               }

               this.runCooldown = 120 + EntityPotoo.this.m_217043_().m_188503_(140);
               return this.perch != null && this.perchDirection != null;
            }

            return false;
         }
      }

      private void findPerch() {
         RandomSource random = EntityPotoo.this.m_217043_();
         Direction[] horiz = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
         if (EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.m_20099_(), EntityPotoo.this.m_6350_())) {
            this.perch = EntityPotoo.this.m_20099_();
            this.perchDirection = EntityPotoo.this.m_6350_();
         } else {
            for (Direction dir : horiz) {
               if (EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.m_20099_(), dir)) {
                  this.perch = EntityPotoo.this.m_20099_();
                  this.perchDirection = dir;
                  return;
               }
            }

            int range = 14;

            for (int i = 0; i < 15; i++) {
               BlockPos blockpos1 = EntityPotoo.this.m_20183_().m_7918_(random.m_188503_(range) - range / 2, 3, random.m_188503_(range) - range / 2);
               if (EntityPotoo.this.m_9236_().m_46749_(blockpos1)) {
                  while (EntityPotoo.this.m_9236_().m_46859_(blockpos1) && blockpos1.m_123342_() > -64) {
                     blockpos1 = blockpos1.m_7495_();
                  }

                  Direction dirx = Direction.m_122407_(random.m_188503_(3));
                  if (EntityPotoo.this.isValidPerchFromSide(blockpos1, dirx)) {
                     this.perch = blockpos1;
                     this.perchDirection = dirx;
                     break;
                  }
               }
            }
         }
      }

      public void m_8056_() {
         this.pathRecalcTime = 0;
      }

      public boolean m_8045_() {
         return (this.perchingTime < 300 || EntityPotoo.this.m_9236_().m_46461_())
            && (EntityPotoo.this.m_5448_() == null || !EntityPotoo.this.m_5448_().m_6084_())
            && !EntityPotoo.this.m_20159_();
      }

      public void m_8037_() {
         if (EntityPotoo.this.isPerching()) {
            this.perchingTime++;
            EntityPotoo.this.m_21573_().m_26573_();
            Vec3 block = Vec3.m_82514_(EntityPotoo.this.getPerchPos(), 1.0);
            Vec3 onBlock = block.m_82520_(
               EntityPotoo.this.getPerchDirection().m_122429_() * 0.35F, 0.0, EntityPotoo.this.getPerchDirection().m_122431_() * 0.35F
            );
            double dist = EntityPotoo.this.m_20238_(onBlock);
            Vec3 dirVec = block.m_82546_(EntityPotoo.this.m_20182_());
            if (this.perchingTime <= 10
               || !(dist > 2.3F) && EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.getPerchPos(), EntityPotoo.this.getPerchDirection())) {
               if (dist > 1.0) {
                  EntityPotoo.this.slideTowardsPerch();
                  if (EntityPotoo.this.getPerchPos().m_123342_() + 1.2F > EntityPotoo.this.m_20191_().f_82289_) {
                     EntityPotoo.this.m_20256_(EntityPotoo.this.m_20184_().m_82520_(0.0, 0.2F, 0.0));
                  }

                  float f = -((float)(Mth.m_14136_(dirVec.f_82479_, dirVec.f_82481_) * 180.0F / (float)Math.PI));
                  EntityPotoo.this.m_146922_(f);
                  EntityPotoo.this.f_20885_ = f;
                  EntityPotoo.this.f_20883_ = f;
               }
            } else {
               EntityPotoo.this.setPerching(false);
            }
         } else if (this.perch != null) {
            if (EntityPotoo.this.m_20238_(Vec3.m_82512_(this.perch)) > 100.0) {
               EntityPotoo.this.setFlying(true);
            }

            double distX = this.perch.m_123341_() + 0.5F - EntityPotoo.this.m_20185_();
            double distZ = this.perch.m_123343_() + 0.5F - EntityPotoo.this.m_20189_();
            if (!(distX * distX + distZ * distZ < 1.0) && EntityPotoo.this.isFlying()) {
               if (this.pathRecalcTime <= 0) {
                  this.pathRecalcTime = EntityPotoo.this.m_217043_().m_188503_(30) + 30;
                  EntityPotoo.this.m_21573_().m_26519_(this.perch.m_123341_() + 0.5F, this.perch.m_123342_() + 2.5F, this.perch.m_123343_() + 0.5F, 1.0);
               }
            } else {
               if (this.pathRecalcTime <= 0) {
                  this.pathRecalcTime = EntityPotoo.this.m_217043_().m_188503_(30) + 30;
                  EntityPotoo.this.m_21573_().m_26519_(this.perch.m_123341_() + 0.5F, this.perch.m_123342_() + 1.5F, this.perch.m_123343_() + 0.5F, 1.0);
               }

               if (EntityPotoo.this.m_21573_().m_26571_()) {
                  EntityPotoo.this.m_21566_().m_6849_(this.perch.m_123341_() + 0.5F, this.perch.m_123342_() + 1.5F, this.perch.m_123343_() + 0.5F, 1.0);
               }
            }

            if (EntityPotoo.this.m_20099_().equals(this.perch)) {
               EntityPotoo.this.m_20256_(Vec3.f_82478_);
               EntityPotoo.this.setPerching(true);
               EntityPotoo.this.setFlying(false);
               EntityPotoo.this.setPerchPos(this.perch);
               EntityPotoo.this.setPerchDirection(this.perchDirection);
               EntityPotoo.this.m_21573_().m_26573_();
               this.perch = null;
            } else {
               EntityPotoo.this.setPerching(false);
            }
         }

         if (this.pathRecalcTime > 0) {
            this.pathRecalcTime--;
         }
      }

      public void m_8041_() {
         EntityPotoo.this.setPerching(false);
         EntityPotoo.this.perchCooldown = 120 + EntityPotoo.this.f_19796_.m_188503_(1200);
         this.perch = null;
         this.perchDirection = null;
      }
   }
}
