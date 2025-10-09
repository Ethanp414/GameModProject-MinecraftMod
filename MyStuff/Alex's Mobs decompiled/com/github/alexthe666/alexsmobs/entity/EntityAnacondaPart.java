package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.util.AnacondaPartIndex;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class EntityAnacondaPart extends LivingEntity implements IHurtableMultipart {
   private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> BODY_TYPE = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Float> TARGET_YAW = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Float> SWELL = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135029_);
   public EntityDimensions multipartSize;
   private float strangleProgess;
   private float prevSwell;
   private float prevStrangleProgess;
   private int headEntityId = -1;
   private double prevHeight = 0.0;
   private static final EntityDataAccessor<Boolean> YELLOW = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SHEDDING = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> BABY = SynchedEntityData.m_135353_(EntityAnacondaPart.class, EntityDataSerializers.f_135035_);

   public EntityAnacondaPart(EntityType t, Level world) {
      super(t, world);
      this.multipartSize = t.m_20680_();
   }

   public EntityAnacondaPart(EntityType t, LivingEntity parent) {
      super(t, parent.m_9236_());
      this.setParent(parent);
   }

   public InteractionResult m_6096_(Player p_19978_, InteractionHand p_19979_) {
      return this.getParent() == null ? super.m_6096_(p_19978_, p_19979_) : this.getParent().m_6096_(p_19978_, p_19979_);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 10.0).m_22268_(Attributes.f_22279_, 0.15F);
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   public boolean m_20068_() {
      return false;
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevStrangleProgess = this.strangleProgess;
      this.prevSwell = this.getSwell();
      this.f_19817_ = false;
      this.m_20256_(Vec3.f_82478_);
      if (this.f_19797_ > 1) {
         Entity parent = this.getParent();
         this.m_6210_();
         if (!this.m_9236_().f_46443_) {
            if (parent == null) {
               this.m_142687_(RemovalReason.DISCARDED);
            }

            if (parent == null) {
               if (this.f_19797_ > 20) {
                  this.m_142687_(RemovalReason.DISCARDED);
               }
            } else {
               if (parent instanceof LivingEntity livingEntityParent && (livingEntityParent.f_20916_ > 0 || livingEntityParent.f_20919_ > 0)) {
                  AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.m_19879_(), parent.m_19879_(), 0.0F));
                  this.f_20916_ = livingEntityParent.f_20916_;
                  this.f_20919_ = livingEntityParent.f_20919_;
               }

               if (parent.m_213877_()) {
                  this.m_142687_(RemovalReason.DISCARDED);
               }
            }

            if (this.getSwell() > 0.0F) {
               float swellInc = 0.25F;
               if (parent instanceof EntityAnaconda || parent instanceof EntityAnacondaPart && ((EntityAnacondaPart)parent).getSwell() == 0.0F) {
                  if (this.getChild() != null) {
                     EntityAnacondaPart child = (EntityAnacondaPart)this.getChild();
                     if (child.getPartType() == AnacondaPartIndex.TAIL) {
                        if (this.getSwell() == 0.25F) {
                           this.feedAnaconda();
                        }
                     } else {
                        child.setSwell(child.getSwell() + 0.25F);
                     }
                  }

                  this.setSwell(this.getSwell() - 0.25F);
               }
            }
         }
      }
   }

   private void feedAnaconda() {
      Entity e = this.getParent();

      while (e instanceof EntityAnacondaPart) {
         e = ((EntityAnacondaPart)e).getParent();
      }

      if (e instanceof EntityAnaconda) {
         ((EntityAnaconda)e).feed();
      }
   }

   public Vec3 tickMultipartPosition(
      int headId, AnacondaPartIndex parentIndex, Vec3 parentPosition, float parentXRot, float parentYRot, float ourYRot, boolean doHeight
   ) {
      Vec3 parentButt = parentPosition.m_82549_(this.calcOffsetVec(-parentIndex.getBackOffset() * this.m_6134_(), parentXRot, parentYRot));
      Vec3 ourButt = parentButt.m_82549_(
         this.calcOffsetVec((-this.getPartType().getBackOffset() - 0.5F * this.m_20205_()) * this.m_6134_(), this.m_146909_(), ourYRot)
      );
      Vec3 avg = new Vec3(
         (parentButt.f_82479_ + ourButt.f_82479_) / 2.0, (parentButt.f_82480_ + ourButt.f_82480_) / 2.0, (parentButt.f_82481_ + ourButt.f_82481_) / 2.0
      );
      double d0 = parentButt.f_82479_ - ourButt.f_82479_;
      double d2 = parentButt.f_82481_ - ourButt.f_82481_;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      double hgt = doHeight
         ? this.getLowPartHeight(parentButt.f_82479_, parentButt.f_82480_, parentButt.f_82481_)
            + this.getHighPartHeight(ourButt.f_82479_, ourButt.f_82480_, ourButt.f_82481_)
         : 0.0;
      if (Math.abs(hgt - this.prevHeight) > 0.2F) {
         this.prevHeight = hgt;
      }

      double partYDest = Mth.m_14008_(this.m_6134_() * this.prevHeight, -0.6F, 0.6F);
      float f = (float)(Mth.m_14136_(d2, d0) * 180.0F / (float)Math.PI) - 90.0F;
      float rawAngle = Mth.m_14177_((float)(-(Mth.m_14136_(partYDest, d3) * 180.0F / (float)Math.PI)));
      float f2 = this.limitAngle(this.m_146909_(), rawAngle, 10.0F);
      this.m_146926_(f2);
      this.m_146922_(f);
      this.f_20885_ = f;
      this.m_7678_(avg.f_82479_, avg.f_82480_, avg.f_82481_, f, f2);
      this.headEntityId = headId;
      return avg;
   }

   public double getLowPartHeight(double x, double yIn, double z) {
      if (this.isFluidAt(x, yIn, z)) {
         return 0.0;
      } else {
         double checkAt = 0.0;

         while (checkAt > -3.0 && !this.isOpaqueBlockAt(x, yIn + checkAt, z)) {
            checkAt -= 0.2;
         }

         return checkAt;
      }
   }

   public double getHighPartHeight(double x, double yIn, double z) {
      if (this.isFluidAt(x, yIn, z)) {
         return 0.0;
      } else {
         double checkAt = 0.0;

         while (checkAt <= 3.0 && this.isOpaqueBlockAt(x, yIn + checkAt, z)) {
            checkAt += 0.2;
         }

         return checkAt;
      }
   }

   public boolean isOpaqueBlockAt(double x, double y, double z) {
      if (this.f_19794_) {
         return false;
      } else {
         double d = 1.0;
         Vec3 vec3 = new Vec3(x, y, z);
         AABB axisAlignedBB = AABB.m_165882_(vec3, 1.0, 1.0E-6, 1.0);
         return this.m_9236_()
            .m_45556_(axisAlignedBB)
            .filter(Predicate.not(BlockStateBase::m_60795_))
            .anyMatch(
               p_185969_ -> {
                  BlockPos blockpos = AMBlockPos.fromVec3(vec3);
                  return p_185969_.m_60828_(this.m_9236_(), blockpos)
                     && Shapes.m_83157_(
                        p_185969_.m_60812_(this.m_9236_(), blockpos).m_83216_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_),
                        Shapes.m_83064_(axisAlignedBB),
                        BooleanOp.f_82689_
                     );
               }
            );
      }
   }

   public boolean m_6040_() {
      return true;
   }

   public boolean m_6063_() {
      return false;
   }

   public boolean isFluidAt(double x, double y, double z) {
      return this.f_19794_ ? false : !this.m_9236_().m_6425_(AMBlockPos.fromCoords(x, y, z)).m_76178_();
   }

   public boolean hurtHeadId(DamageSource source, float f) {
      if (this.headEntityId != -1) {
         Entity e = this.m_9236_().m_6815_(this.headEntityId);
         if (e instanceof EntityAnaconda) {
            return e.m_6469_(source, f);
         }
      }

      return false;
   }

   public boolean m_6469_(DamageSource source, float damage) {
      return this.hurtHeadId(source, damage);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(CHILD_UUID, Optional.empty());
      this.f_19804_.m_135372_(PARENT_UUID, Optional.empty());
      this.f_19804_.m_135372_(BODYINDEX, 0);
      this.f_19804_.m_135372_(BODY_TYPE, AnacondaPartIndex.NECK.ordinal());
      this.f_19804_.m_135372_(TARGET_YAW, 0.0F);
      this.f_19804_.m_135372_(SWELL, 0.0F);
      this.f_19804_.m_135372_(YELLOW, false);
      this.f_19804_.m_135372_(SHEDDING, false);
      this.f_19804_.m_135372_(BABY, false);
   }

   public void m_6138_() {
      List<Entity> entities = this.m_9236_().m_45933_(this, this.m_20191_().m_82363_(0.2, 0.0, 0.2));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(entity -> !entity.m_7306_(parent) && !(entity instanceof EntityAnacondaPart) && !(entity instanceof EntityAnaconda) && entity.m_6094_())
            .forEach(entity -> entity.m_7334_(parent));
      }
   }

   public Iterable<ItemStack> m_6168_() {
      return ImmutableList.of();
   }

   public ItemStack m_6844_(EquipmentSlot slotIn) {
      return ItemStack.f_41583_;
   }

   public void m_8061_(EquipmentSlot p_21036_, ItemStack p_21037_) {
   }

   public HumanoidArm m_5737_() {
      return HumanoidArm.RIGHT;
   }

   @Override
   public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
      if (parent.f_20919_ > 0) {
         this.f_20919_ = parent.f_20919_;
      }

      if (parent.f_20916_ > 0) {
         this.f_20916_ = parent.f_20916_;
      }
   }

   public Entity getParent() {
      if (!this.m_9236_().f_46443_) {
         UUID id = this.getParentId();
         if (id != null) {
            return ((ServerLevel)this.m_9236_()).m_8791_(id);
         }
      }

      return null;
   }

   public void setParent(Entity entity) {
      this.setParentId(entity.m_20148_());
   }

   @Nullable
   public UUID getParentId() {
      return (UUID)((Optional)this.f_19804_.m_135370_(PARENT_UUID)).orElse(null);
   }

   public void setParentId(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(PARENT_UUID, Optional.ofNullable(uniqueId));
   }

   public Entity getChild() {
      if (!this.m_9236_().f_46443_) {
         UUID id = this.getChildId();
         if (id != null) {
            return ((ServerLevel)this.m_9236_()).m_8791_(id);
         }
      }

      return null;
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.f_19804_.m_135370_(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      if (this.getParentId() != null) {
         compound.m_128362_("ParentUUID", this.getParentId());
      }

      if (this.getChildId() != null) {
         compound.m_128362_("ChildUUID", this.getChildId());
      }

      compound.m_128405_("BodyModel", this.getPartType().ordinal());
      compound.m_128405_("BodyIndex", this.getBodyIndex());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128403_("ParentUUID")) {
         this.setParentId(compound.m_128342_("ParentUUID"));
      }

      if (compound.m_128403_("ChildUUID")) {
         this.setChildId(compound.m_128342_("ChildUUID"));
      }

      this.setPartType(AnacondaPartIndex.fromOrdinal(compound.m_128451_("BodyModel")));
      this.setBodyIndex(compound.m_128451_("BodyIndex"));
   }

   public boolean m_7306_(Entity entity) {
      return this == entity || this.getParent() == entity;
   }

   public boolean m_6087_() {
      return true;
   }

   @Nullable
   public ItemStack m_142340_() {
      Entity parent = this.getParent();
      return parent != null ? parent.m_142340_() : ItemStack.f_41583_;
   }

   public int getBodyIndex() {
      return (Integer)this.f_19804_.m_135370_(BODYINDEX);
   }

   public void setBodyIndex(int index) {
      this.f_19804_.m_135381_(BODYINDEX, index);
   }

   public AnacondaPartIndex getPartType() {
      return AnacondaPartIndex.fromOrdinal((Integer)this.f_19804_.m_135370_(BODY_TYPE));
   }

   public void setPartType(AnacondaPartIndex index) {
      this.f_19804_.m_135381_(BODY_TYPE, index.ordinal());
   }

   public void setTargetYaw(float f) {
      this.f_19804_.m_135381_(TARGET_YAW, f);
   }

   public void setSwell(float f) {
      this.f_19804_.m_135381_(SWELL, f);
   }

   public float getSwell() {
      return Math.min((Float)this.f_19804_.m_135370_(SWELL), 5.0F);
   }

   public float getSwellLerp(float partialTick) {
      return this.prevSwell + (Math.max(this.getSwell(), 0.0F) - this.prevSwell) * partialTick;
   }

   public float m_146908_() {
      return super.m_146908_();
   }

   public void setStrangleProgress(float f) {
      this.strangleProgess = f;
   }

   public float getStrangleProgress(float partialTick) {
      return this.prevStrangleProgess + (this.strangleProgess - this.prevStrangleProgess) * partialTick;
   }

   public void copyDataFrom(EntityAnaconda anaconda) {
      this.f_19804_.m_135381_(YELLOW, anaconda.isYellow());
      this.f_19804_.m_135381_(SHEDDING, anaconda.isShedding());
      this.f_19804_.m_135381_(BABY, anaconda.m_6162_());
   }

   public boolean isYellow() {
      return (Boolean)this.f_19804_.m_135370_(YELLOW);
   }

   public boolean isShedding() {
      return (Boolean)this.f_19804_.m_135370_(SHEDDING);
   }

   public boolean m_6162_() {
      return (Boolean)this.f_19804_.m_135370_(BABY);
   }
}
