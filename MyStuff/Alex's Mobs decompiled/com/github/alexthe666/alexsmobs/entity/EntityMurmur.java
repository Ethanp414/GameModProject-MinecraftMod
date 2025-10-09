package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityMurmur extends Monster implements ISemiAquatic {
   private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID = SynchedEntityData.m_135353_(EntityMurmur.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Integer> HEAD_ID = SynchedEntityData.m_135353_(EntityMurmur.class, EntityDataSerializers.f_135028_);
   private boolean renderFakeHead = true;

   protected EntityMurmur(EntityType<? extends Monster> type, Level level) {
      super(type, level);
      this.f_21364_ = 10;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22277_, 48.0)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22278_, 0.3F)
         .m_22268_(Attributes.f_22279_, 0.2F);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new AnimalAILeaveWater(this));
      this.f_21345_.m_25352_(2, new AnimalAIWanderRanged(this, 55, 1.0, 14, 7));
      this.f_21346_.m_25352_(0, new HurtByTargetGoal(this, new Class[0]));
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.MURMUR_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.MURMUR_HURT.get();
   }

   protected void m_7355_(BlockPos pos, BlockState blockIn) {
   }

   public static <T extends Mob> boolean checkMurmurSpawnRules(
      EntityType<EntityMurmur> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER
         || !iServerWorld.m_45527_(pos)
            && (pos.m_123342_() <= AMConfig.murmurSpawnHeight || iServerWorld.m_204166_(pos).m_203656_(AMTagRegistry.SPAWNS_MURMURS_IGNORE_HEIGHT))
            && m_219013_(entityType, iServerWorld, reason, pos, random);
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.murmurSpawnRolls, this.m_217043_(), spawnReasonIn) && super.m_5545_(worldIn, spawnReasonIn);
   }

   public boolean m_7307_(Entity entity) {
      return this.getHeadUUID() != null && entity.m_20148_().equals(this.getHeadUUID()) || super.m_7307_(entity);
   }

   public MobType m_6336_() {
      return MobType.f_21641_;
   }

   protected float m_6431_(Pose pose, EntityDimensions dimensions) {
      return dimensions.f_20378_ * 1.2F;
   }

   protected float m_6108_() {
      return 0.9F;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(HEAD_UUID, Optional.empty());
      this.f_19804_.m_135372_(HEAD_ID, -1);
   }

   @Nullable
   public UUID getHeadUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(HEAD_UUID)).orElse(null);
   }

   public void setHeadUUID(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(HEAD_UUID, Optional.ofNullable(uniqueId));
   }

   public Entity getHead() {
      if (!this.m_9236_().f_46443_) {
         UUID id = this.getHeadUUID();
         return id == null ? null : ((ServerLevel)this.m_9236_()).m_8791_(id);
      } else {
         int id = (Integer)this.f_19804_.m_135370_(HEAD_ID);
         return id == -1 ? null : this.m_9236_().m_6815_(id);
      }
   }

   public boolean shouldRenderFakeHead() {
      return this.renderFakeHead;
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.renderFakeHead) {
         this.renderFakeHead = false;
      }

      this.f_20883_ = this.m_146908_();
      this.f_20885_ = Mth.m_14036_(this.f_20885_, this.f_20883_ - 70.0F, this.f_20883_ + 70.0F);
      if (!this.m_9236_().f_46443_) {
         Entity head = this.getHead();
         if (head == null) {
            LivingEntity created = this.createHead();
            this.setHeadUUID(created.m_20148_());
            this.f_19804_.m_135381_(HEAD_ID, created.m_19879_());
         }
      }
   }

   public Vec3 getNeckBottom(float partialTick) {
      double d0 = Mth.m_14139_(partialTick, this.f_19854_, this.m_20185_());
      double d1 = Mth.m_14139_(partialTick, this.f_19855_, this.m_20186_());
      double d2 = Mth.m_14139_(partialTick, this.f_19856_, this.m_20189_());
      double height = this.m_20206_() - 0.4F + this.calculateWalkBounce(partialTick);
      Vec3 rotatedOnDeath = new Vec3(0.0, height, 0.0);
      if (this.f_20919_ > 0) {
         float f = (this.f_20919_ + partialTick - 1.0F) / 20.0F * 1.6F;
         f = Mth.m_14116_(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         rotatedOnDeath = rotatedOnDeath.m_82520_(f * 0.1F, f * 0.4F, 0.0)
            .m_82535_((float)(f * Math.PI / 2.0))
            .m_82524_(-this.f_20883_ * (float) (Math.PI / 180.0));
      }

      return new Vec3(d0, d1, d2).m_82549_(rotatedOnDeath);
   }

   public double calculateWalkBounce(float partialTick) {
      float limbSwingAmount = this.f_267362_.m_267711_(partialTick);
      float limbSwing = this.f_267362_.m_267756_() - this.f_267362_.m_267731_() * (1.0F - partialTick);
      return Math.abs(Math.sin(limbSwing * 0.9F) * limbSwingAmount * 0.25);
   }

   @Override
   public boolean shouldEnterWater() {
      return false;
   }

   @Override
   public boolean shouldLeaveWater() {
      return true;
   }

   @Override
   public boolean shouldStopMoving() {
      return false;
   }

   @Override
   public int getWaterSearchRange() {
      return 5;
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128403_("HeadUUID")) {
         this.setHeadUUID(compound.m_128342_("HeadUUID"));
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      if (this.getHeadUUID() != null) {
         compound.m_128362_("HeadUUID", this.getHeadUUID());
      }
   }

   private LivingEntity createHead() {
      EntityMurmurHead head = new EntityMurmurHead(this);
      this.m_9236_().m_7967_(head);
      return head;
   }

   public boolean isAngry() {
      Entity entity = this.getHead();
      return entity instanceof EntityMurmurHead ? ((EntityMurmurHead)entity).isAngry() : false;
   }
}
