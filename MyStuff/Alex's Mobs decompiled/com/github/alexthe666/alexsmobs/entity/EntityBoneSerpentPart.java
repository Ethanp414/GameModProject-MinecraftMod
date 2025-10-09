package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class EntityBoneSerpentPart extends LivingEntity implements IHurtableMultipart {
   private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.m_135353_(EntityBoneSerpentPart.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.m_135353_(EntityBoneSerpentPart.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.m_135353_(
      EntityBoneSerpentPart.class, EntityDataSerializers.f_135041_
   );
   public EntityDimensions multipartSize;
   protected float radius;
   protected float angleYaw;
   protected float offsetY;
   protected float damageMultiplier = 1.0F;

   public EntityBoneSerpentPart(EntityType t, Level world) {
      super(t, world);
      this.multipartSize = t.m_20680_();
   }

   public EntityBoneSerpentPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
      super(t, parent.m_9236_());
      this.setParent(parent);
      this.radius = radius;
      this.angleYaw = (angleYaw + 90.0F) * (float) (Math.PI / 180.0);
      this.offsetY = offsetY;
   }

   public MobType m_6336_() {
      return MobType.f_21641_;
   }

   public boolean m_20329_(Entity entityIn) {
      return !(entityIn instanceof AbstractMinecart) && !(entityIn instanceof Boat) ? super.m_20329_(entityIn) : false;
   }

   @Nullable
   public ItemStack m_142340_() {
      Entity parent = this.getParent();
      return parent != null ? parent.m_142340_() : ItemStack.f_41583_;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 10.0).m_22268_(Attributes.f_22279_, 0.15F);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      if (this.getParentId() != null) {
         compound.m_128362_("ParentUUID", this.getParentId());
      }

      compound.m_128379_("TailPart", this.isTail());
      compound.m_128405_("BodyIndex", this.getBodyIndex());
      compound.m_128350_("PartAngle", this.angleYaw);
      compound.m_128350_("PartRadius", this.radius);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128403_("ParentUUID")) {
         this.setParentId(compound.m_128342_("ParentUUID"));
      }

      this.setTail(compound.m_128471_("TailPart"));
      this.setBodyIndex(compound.m_128451_("BodyIndex"));
      this.angleYaw = compound.m_128457_("PartAngle");
      this.radius = compound.m_128457_("PartRadius");
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(PARENT_UUID, Optional.empty());
      this.f_19804_.m_135372_(TAIL, false);
      this.f_19804_.m_135372_(BODYINDEX, 0);
   }

   @Nullable
   public UUID getParentId() {
      return (UUID)((Optional)this.f_19804_.m_135370_(PARENT_UUID)).orElse(null);
   }

   public void setParentId(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(PARENT_UUID, Optional.ofNullable(uniqueId));
   }

   public void setInitialPartPos(Entity parent) {
      this.m_6034_(
         parent.f_19854_ + this.radius * Math.cos(parent.m_146908_() * (float) (Math.PI / 180.0) + this.angleYaw),
         parent.f_19855_ + this.offsetY,
         parent.f_19856_ + this.radius * Math.sin(parent.m_146908_() * (float) (Math.PI / 180.0) + this.angleYaw)
      );
   }

   public void m_8119_() {
      this.f_19817_ = false;
      if (this.f_19797_ > 10) {
         Entity parent = this.getParent();
         this.m_6210_();
         if (parent != null && !this.m_9236_().f_46443_) {
            this.m_20242_(true);
            this.m_6034_(
               parent.f_19854_ + this.radius * Math.cos(parent.f_19859_ * (float) (Math.PI / 180.0) + this.angleYaw),
               parent.f_19855_ + this.offsetY,
               parent.f_19856_ + this.radius * Math.sin(parent.f_19859_ * (float) (Math.PI / 180.0) + this.angleYaw)
            );
            double d0 = parent.m_20185_() - this.m_20185_();
            double d1 = parent.m_20186_() - this.m_20186_();
            double d2 = parent.m_20189_() - this.m_20189_();
            float f2 = -((float)(Mth.m_14136_(d1, Mth.m_14116_((float)(d0 * d0 + d2 * d2))) * 180.0F / (float)Math.PI));
            this.m_146926_(this.limitAngle(this.m_146909_(), f2, 5.0F));
            this.m_5834_();
            this.m_146922_(parent.f_19859_);
            this.f_20885_ = this.m_146908_();
            this.f_20883_ = this.f_19859_;
            if (parent instanceof LivingEntity && !this.m_9236_().f_46443_ && (((LivingEntity)parent).f_20916_ > 0 || ((LivingEntity)parent).f_20919_ > 0)) {
               AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.m_19879_(), parent.m_19879_(), 0.0F));
               this.f_20916_ = ((LivingEntity)parent).f_20916_;
               this.f_20919_ = ((LivingEntity)parent).f_20919_;
            }

            this.m_6138_();
            if (parent.m_213877_() && !this.m_9236_().f_46443_) {
               this.m_142687_(RemovalReason.DISCARDED);
            }
         } else if (this.f_19797_ > 20 && !this.m_9236_().f_46443_) {
            this.m_142687_(RemovalReason.DISCARDED);
         }
      }

      super.m_8119_();
   }

   public Entity getParent() {
      UUID id = this.getParentId();
      return id != null && !this.m_9236_().f_46443_ ? ((ServerLevel)this.m_9236_()).m_8791_(id) : null;
   }

   public void setParent(Entity entity) {
      this.setParentId(entity.m_20148_());
   }

   public boolean m_7306_(Entity entity) {
      return this == entity || this.getParent() == entity;
   }

   public boolean m_6087_() {
      return true;
   }

   public HumanoidArm m_5737_() {
      return null;
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public void m_6138_() {
      List<Entity> entities = this.m_9236_().m_45933_(this, this.m_20191_().m_82363_(0.2, 0.0, 0.2));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(entity -> entity != parent && !(entity instanceof EntityBoneSerpentPart) && entity.m_6094_())
            .forEach(entity -> entity.m_7334_(parent));
      }
   }

   public InteractionResult m_6096_(Player player, InteractionHand hand) {
      Entity parent = this.getParent();
      return parent != null ? parent.m_6096_(player, hand) : InteractionResult.PASS;
   }

   public boolean m_6469_(DamageSource source, float damage) {
      Entity parent = this.getParent();
      boolean prev = parent != null && parent.m_6469_(source, damage * this.damageMultiplier);
      if (prev && !this.m_9236_().f_46443_) {
         AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.m_19879_(), parent.m_19879_(), damage * this.damageMultiplier));
      }

      return prev;
   }

   public Iterable<ItemStack> m_6168_() {
      return ImmutableList.of();
   }

   public ItemStack m_6844_(EquipmentSlot slotIn) {
      return ItemStack.f_41583_;
   }

   public void m_8061_(EquipmentSlot slotIn, ItemStack stack) {
   }

   public boolean isTail() {
      return (Boolean)this.f_19804_.m_135370_(TAIL);
   }

   public void setTail(boolean tail) {
      this.f_19804_.m_135381_(TAIL, tail);
   }

   public int getBodyIndex() {
      return (Integer)this.f_19804_.m_135370_(BODYINDEX);
   }

   public void setBodyIndex(int index) {
      this.f_19804_.m_135381_(BODYINDEX, index);
   }

   public boolean shouldNotExist() {
      Entity parent = this.getParent();
      return !parent.m_6084_();
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

   public boolean shouldContinuePersisting() {
      return this.isAddedToWorld() || this.m_213877_();
   }
}
