package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CosmicCodAIFollowLeader;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity;

public class EntityCosmicCod extends Mob implements Bucketable {
   private static final EntityDataAccessor<Float> FISH_PITCH = SynchedEntityData.m_135353_(EntityCosmicCod.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.m_135353_(EntityCosmicCod.class, EntityDataSerializers.f_135035_);
   public float prevFishPitch;
   private int baitballCooldown = 100 + this.f_19796_.m_188503_(100);
   private int circleTime = 0;
   private int maxCircleTime = 300;
   private BlockPos circlePos;
   private int teleportIn;
   private EntityCosmicCod groupLeader;
   private int groupSize = 1;

   protected EntityCosmicCod(EntityType<? extends Mob> mob, Level level) {
      super(mob, level);
      this.m_21441_(BlockPathTypes.WATER, -1.0F);
      this.f_21342_ = new FlightMoveController(this, 1.0F, false, true);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.cosmicCodSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.COSMIC_COD_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.COSMIC_COD_HURT.get();
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 4.0).m_22268_(Attributes.f_22279_, 0.35F);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(0, new EntityCosmicCod.AISwimIdle(this));
      this.f_21345_.m_25352_(1, new CosmicCodAIFollowLeader(this));
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(FISH_PITCH, 0.0F);
      this.f_19804_.m_135372_(FROM_BUCKET, false);
   }

   public boolean m_27487_() {
      return (Boolean)this.f_19804_.m_135370_(FROM_BUCKET);
   }

   public void m_27497_(boolean p_203706_1_) {
      this.f_19804_.m_135381_(FROM_BUCKET, p_203706_1_);
   }

   @Nonnull
   public SoundEvent m_142623_() {
      return SoundEvents.f_11782_;
   }

   @Nonnull
   public ItemStack m_28282_() {
      ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.COSMIC_COD_BUCKET.get());
      if (this.m_8077_()) {
         stack.m_41714_(this.m_7770_());
      }

      return stack;
   }

   public void m_6872_(@Nonnull ItemStack bucket) {
      if (this.m_8077_()) {
         bucket.m_41714_(this.m_7770_());
      }

      CompoundTag platTag = new CompoundTag();
      this.m_7380_(platTag);
      CompoundTag compound = bucket.m_41784_();
      compound.m_128365_("CosmicCodData", platTag);
   }

   public void m_142278_(@Nonnull CompoundTag compound) {
      if (compound.m_128441_("CosmicCodData")) {
         this.m_7378_(compound.m_128469_("CosmicCodData"));
      }
   }

   public boolean m_8023_() {
      return super.m_8023_() || this.m_8077_() || this.m_27487_();
   }

   public boolean m_6785_(double p_213397_1_) {
      return !this.m_27487_() && !this.m_8077_();
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("FromBucket", this.m_27487_());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_27497_(compound.m_128471_("FromBucket"));
   }

   public boolean m_6126_() {
      return true;
   }

   private void doInitialPosing(LevelAccessor world, EntityCosmicCod.GroupData data) {
      BlockPos down = this.m_20183_();

      while (world.m_46859_(down) && down.m_123342_() > -62) {
         down = down.m_7495_();
      }

      if (down.m_123342_() <= -60) {
         if (data != null && data.groupLeader != null) {
            this.m_6034_(down.m_123341_() + 0.5F, data.groupLeader.m_20186_() - 1.0 + this.f_19796_.m_188503_(1), down.m_123343_() + 0.5F);
         } else {
            this.m_6034_(down.m_123341_() + 0.5F, down.m_123342_() + 90 + this.f_19796_.m_188503_(60), down.m_123343_() + 0.5F);
         }
      } else {
         this.m_6034_(down.m_123341_() + 0.5F, down.m_123342_() + 1, down.m_123343_() + 0.5F);
      }
   }

   protected MovementEmission m_142319_() {
      return MovementEmission.EVENTS;
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevFishPitch = this.getFishPitch();
      if (!this.m_9236_().f_46443_) {
         double ydist = this.f_19855_ - this.m_20186_();
         float fishDist = (float)((Math.abs(this.m_20184_().f_82479_) + Math.abs(this.m_20184_().f_82481_)) * 6.0) / this.getPitchSensitivity();
         this.incrementFishPitch((float)ydist * 10.0F * this.getPitchSensitivity());
         this.setFishPitch(Mth.m_14036_(this.getFishPitch(), -60.0F, 40.0F));
         if (this.getFishPitch() > 2.0F) {
            this.decrementFishPitch(fishDist * Math.abs(this.getFishPitch()) / 90.0F);
         }

         if (this.getFishPitch() < -2.0F) {
            this.incrementFishPitch(fishDist * Math.abs(this.getFishPitch()) / 90.0F);
         }

         if (this.getFishPitch() > 2.0F) {
            this.decrementFishPitch(1.0F);
         } else if (this.getFishPitch() < -2.0F) {
            this.incrementFishPitch(1.0F);
         }

         if (this.baitballCooldown > 0) {
            this.baitballCooldown--;
         }
      }

      if (this.teleportIn > 0) {
         this.teleportIn--;
         if (this.teleportIn == 0 && !this.m_9236_().f_46443_) {
            double range = 8.0;
            AABB bb = new AABB(
               this.m_20185_() - 8.0, this.m_20186_() - 8.0, this.m_20189_() - 8.0, this.m_20185_() + 8.0, this.m_20186_() + 8.0, this.m_20189_() + 8.0
            );
            List<EntityCosmicCod> list = this.m_9236_().m_45976_(EntityCosmicCod.class, bb);
            Vec3 vec3 = this.teleport();
            if (vec3 != null) {
               this.baitballCooldown = 5;

               for (EntityCosmicCod cod : list) {
                  if (cod != this) {
                     cod.baitballCooldown = 5;
                     cod.teleport(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
                  }
               }
            }
         }
      }
   }

   public void m_7822_(byte msg) {
      if (msg == 46) {
         this.m_146850_(GameEvent.f_238175_);
         this.m_5496_(SoundEvents.f_11852_, 1.0F, 1.0F);
      }

      super.m_7822_(msg);
   }

   public void resetBaitballCooldown() {
      this.baitballCooldown = 120 + this.f_19796_.m_188503_(100);
   }

   public boolean m_6469_(DamageSource source, float amount) {
      boolean prev = super.m_6469_(source, amount);
      if (prev) {
         this.teleportIn = 5;
      }

      return prev;
   }

   private float getPitchSensitivity() {
      return 3.0F;
   }

   public boolean m_20068_() {
      return true;
   }

   public boolean m_6040_() {
      return true;
   }

   public boolean isPushedByWater() {
      return false;
   }

   public float getFishPitch() {
      return (Float)this.f_19804_.m_135370_(FISH_PITCH);
   }

   public void setFishPitch(float pitch) {
      this.f_19804_.m_135381_(FISH_PITCH, pitch);
   }

   public void incrementFishPitch(float pitch) {
      this.f_19804_.m_135381_(FISH_PITCH, this.getFishPitch() + pitch);
   }

   public void decrementFishPitch(float pitch) {
      this.f_19804_.m_135381_(FISH_PITCH, this.getFishPitch() - pitch);
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public boolean canBlockPosBeSeen(BlockPos pos) {
      double x = pos.m_123341_() + 0.5F;
      double y = pos.m_123342_() + 0.5F;
      double z = pos.m_123343_() + 0.5F;
      HitResult result = this.m_9236_().m_45547_(new ClipContext(this.m_146892_(), new Vec3(x, y, z), Block.COLLIDER, Fluid.NONE, this));
      double dist = result.m_82450_().m_82531_(x, y, z);
      return dist <= 1.0 || result.m_6662_() == Type.MISS;
   }

   protected Vec3 teleport() {
      if (!this.m_9236_().m_5776_() && this.m_6084_()) {
         double d0 = this.m_20185_() + (this.f_19796_.m_188500_() - 0.5) * 64.0;
         double d1 = this.m_20186_() + (this.f_19796_.m_188503_(64) - 32);
         double d2 = this.m_20189_() + (this.f_19796_.m_188500_() - 0.5) * 64.0;
         if (this.teleport(d0, d1, d2)) {
            this.circlePos = null;
            return new Vec3(d0, d1, d2);
         }
      }

      return null;
   }

   private boolean teleport(double x, double y, double z) {
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos(x, y, z);
      BlockState blockstate = this.m_9236_().m_8055_(blockpos$mutableblockpos);
      boolean flag = blockstate.m_60795_();
      if (flag && !blockstate.m_60819_().m_205070_(FluidTags.f_13131_)) {
         this.m_5496_(SoundEvents.f_11852_, 1.0F, 1.0F);
         EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
         if (event.isCanceled()) {
            return false;
         } else {
            this.m_9236_().m_7605_(this, (byte)46);
            this.m_6021_(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            return true;
         }
      } else {
         return false;
      }
   }

   public void leaveGroup() {
      if (this.groupLeader != null) {
         this.groupLeader.decreaseGroupSize();
      }

      this.groupLeader = null;
   }

   protected boolean hasNoLeader() {
      return !this.hasGroupLeader();
   }

   public boolean hasGroupLeader() {
      return this.groupLeader != null && this.groupLeader.m_6084_();
   }

   private void increaseGroupSize() {
      this.groupSize++;
   }

   private void decreaseGroupSize() {
      this.groupSize--;
   }

   public boolean canGroupGrow() {
      return this.isGroupLeader() && this.groupSize < this.getMaxGroupSize();
   }

   private int getMaxGroupSize() {
      return 15;
   }

   public int m_5792_() {
      return 7;
   }

   public boolean m_7296_(int sizeIn) {
      return false;
   }

   public boolean isGroupLeader() {
      return this.groupSize > 1;
   }

   public boolean inRangeOfGroupLeader() {
      return this.m_20280_(this.groupLeader) <= 121.0;
   }

   public void moveToGroupLeader() {
      if (this.hasGroupLeader()) {
         this.m_21566_().m_6849_(this.groupLeader.m_20185_(), this.groupLeader.m_20186_(), this.groupLeader.m_20189_(), 1.0);
      }
   }

   public EntityCosmicCod createAndSetLeader(EntityCosmicCod leader) {
      this.groupLeader = leader;
      leader.increaseGroupSize();
      return leader;
   }

   public void createFromStream(Stream<EntityCosmicCod> stream) {
      stream.limit(this.getMaxGroupSize() - this.groupSize).filter(fishe -> fishe != this).forEach(fishe -> fishe.createAndSetLeader(this));
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      if (spawnDataIn == null) {
         spawnDataIn = new EntityCosmicCod.GroupData(this);
      } else {
         this.createAndSetLeader(((EntityCosmicCod.GroupData)spawnDataIn).groupLeader);
      }

      if (reason == MobSpawnType.NATURAL && spawnDataIn instanceof EntityCosmicCod.GroupData) {
         this.doInitialPosing(worldIn, (EntityCosmicCod.GroupData)spawnDataIn);
      }

      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public boolean isCircling() {
      return this.circlePos != null && this.circleTime < this.maxCircleTime;
   }

   @Nonnull
   protected InteractionResult m_6071_(@Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      if (itemstack.m_41720_() == Items.f_42446_ && this.m_6084_()) {
         this.m_146850_(GameEvent.f_223708_);
         this.m_5496_(this.m_142623_(), 1.0F, 1.0F);
         ItemStack itemstack1 = this.m_28282_();
         this.m_6872_(itemstack1);
         ItemStack itemstack2 = ItemUtils.m_41817_(itemstack, player, itemstack1, false);
         player.m_21008_(hand, itemstack2);
         Level level = this.m_9236_();
         if (!this.m_9236_().f_46443_) {
            CriteriaTriggers.f_10576_.m_38772_((ServerPlayer)player, itemstack1);
         }

         this.m_146870_();
         return InteractionResult.m_19078_(this.m_9236_().f_46443_);
      } else {
         return super.m_6071_(player, hand);
      }
   }

   private static class AISwimIdle extends Goal {
      private final EntityCosmicCod cod;
      float circleDistance = 5.0F;
      boolean clockwise = false;

      public AISwimIdle(EntityCosmicCod cod) {
         this.cod = cod;
      }

      public boolean m_8036_() {
         return this.cod.isGroupLeader() || this.cod.hasNoLeader() || this.cod.hasGroupLeader() && this.cod.groupLeader.circlePos != null;
      }

      public void m_8037_() {
         if (this.cod.circleTime > this.cod.maxCircleTime) {
            this.cod.circleTime = 0;
            this.cod.circlePos = null;
         }

         if (this.cod.circlePos != null && this.cod.circleTime <= this.cod.maxCircleTime) {
            this.cod.circleTime++;
            Vec3 movePos = this.getSharkCirclePos(this.cod.circlePos);
            this.cod.m_21566_().m_6849_(movePos.m_7096_(), movePos.m_7098_(), movePos.m_7094_(), 1.0);
         } else if (this.cod.isGroupLeader()) {
            if (this.cod.baitballCooldown == 0) {
               this.cod.resetBaitballCooldown();
               if (this.cod.circlePos == null || this.cod.circleTime >= this.cod.maxCircleTime) {
                  this.cod.circleTime = 0;
                  this.cod.maxCircleTime = 360 + this.cod.f_19796_.m_188503_(80);
                  this.circleDistance = 1.0F + this.cod.f_19796_.m_188501_();
                  this.clockwise = this.cod.f_19796_.m_188499_();
                  this.cod.circlePos = this.cod.m_20183_().m_7494_();
               }
            }
         } else if (this.cod.f_19796_.m_188503_(40) == 0 || this.cod.hasNoLeader()) {
            Vec3 movepos = this.cod
               .m_20182_()
               .m_82520_(
                  this.cod.f_19796_.m_188503_(4) - 2, this.cod.m_20186_() < 0.0 ? 1.0 : this.cod.f_19796_.m_188503_(4) - 2, this.cod.f_19796_.m_188503_(4) - 2
               );
            this.cod.m_21566_().m_6849_(movepos.f_82479_, movepos.f_82480_, movepos.f_82481_, 1.0);
         } else if (this.cod.hasGroupLeader() && this.cod.groupLeader.circlePos != null && this.cod.circlePos == null) {
            this.cod.circlePos = this.cod.groupLeader.circlePos;
            this.cod.circleTime = this.cod.groupLeader.circleTime;
            this.cod.maxCircleTime = this.cod.groupLeader.maxCircleTime;
            this.circleDistance = 1.0F + this.cod.f_19796_.m_188501_();
            this.clockwise = this.cod.f_19796_.m_188499_();
         }
      }

      public Vec3 getSharkCirclePos(BlockPos target) {
         float prog = 1.0F - (float)this.cod.circleTime / this.cod.maxCircleTime;
         float angle = (float) (Math.PI / 18) * (this.clockwise ? -this.cod.circleTime : this.cod.circleTime);
         float circleDistanceTimesProg = this.circleDistance * prog;
         double extraX = (circleDistanceTimesProg + 0.75F) * Mth.m_14031_(angle);
         double extraZ = (circleDistanceTimesProg + 0.75F) * prog * Mth.m_14089_(angle);
         return new Vec3(
            target.m_123341_() + 0.5F + extraX, Math.max(target.m_123342_() + this.cod.f_19796_.m_188503_(4) - 2, -62), target.m_123343_() + 0.5F + extraZ
         );
      }
   }

   public static class GroupData extends AgeableMobGroupData {
      public final EntityCosmicCod groupLeader;

      public GroupData(EntityCosmicCod groupLeaderIn) {
         super(0.05F);
         this.groupLeader = groupLeaderIn;
      }
   }
}
