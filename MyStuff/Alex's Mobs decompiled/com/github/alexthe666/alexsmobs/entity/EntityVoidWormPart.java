package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EntityVoidWormPart extends LivingEntity implements IHurtableMultipart {
   protected static final EntityDimensions SIZE_BASE = EntityDimensions.m_20395_(1.2F, 1.95F);
   protected static final EntityDimensions TAIL_SIZE = EntityDimensions.m_20395_(1.6F, 2.0F);
   private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.m_135353_(EntityVoidWormPart.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.m_135353_(EntityVoidWormPart.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Float> WORM_SCALE = SynchedEntityData.m_135353_(EntityVoidWormPart.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> WORM_YAW = SynchedEntityData.m_135353_(EntityVoidWormPart.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> WORM_ANGLE = SynchedEntityData.m_135353_(EntityVoidWormPart.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.m_135353_(EntityVoidWormPart.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.m_135353_(EntityVoidWormPart.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Integer> PORTAL_TICKS = SynchedEntityData.m_135353_(EntityVoidWormPart.class, EntityDataSerializers.f_135028_);
   public EntityDimensions multipartSize;
   public float prevWormAngle;
   protected float radius;
   protected float angleYaw;
   protected float offsetY;
   protected float damageMultiplier = 1.0F;
   private float prevWormYaw = 0.0F;
   private Vec3 teleportPos = null;
   private Vec3 enterPos = null;
   private boolean doesParentControlPos = false;

   public EntityVoidWormPart(EntityType t, Level world) {
      super(t, world);
      this.multipartSize = t.m_20680_();
   }

   public EntityVoidWormPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
      super(t, parent.m_9236_());
      this.setParent(parent);
      this.radius = radius;
      this.angleYaw = (angleYaw + 90.0F) * (float) (Math.PI / 180.0);
      this.offsetY = offsetY;
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 30.0).m_22268_(Attributes.f_22279_, 0.15F);
   }

   public void m_7334_(Entity entityIn) {
   }

   public void m_6074_() {
      this.m_142687_(RemovalReason.DISCARDED);
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return this.isTail() ? TAIL_SIZE.m_20388_(this.m_6134_()) : super.m_6972_(poseIn);
   }

   public float getWormScale() {
      return (Float)this.f_19804_.m_135370_(WORM_SCALE);
   }

   public void setWormScale(float scale) {
      this.f_19804_.m_135381_(WORM_SCALE, scale);
   }

   public float m_6134_() {
      return this.getWormScale() + 0.5F;
   }

   public boolean m_20329_(Entity entityIn) {
      return !(entityIn instanceof AbstractMinecart) && !(entityIn instanceof Boat) ? super.m_20329_(entityIn) : false;
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268671_)
         || source.m_276093_(DamageTypes.f_268722_)
         || source.m_276093_(DamageTypes.f_268724_)
         || source.m_276093_(DamageTypes.f_268612_)
         || source.m_276093_(DamageTypes.f_268546_)
         || source.m_269533_(DamageTypeTags.f_268745_)
         || super.m_6673_(source);
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      if (this.getParentId() != null) {
         compound.m_128362_("ParentUUID", this.getParentId());
      }

      if (this.getChildId() != null) {
         compound.m_128362_("ChildUUID", this.getChildId());
      }

      compound.m_128379_("TailPart", this.isTail());
      compound.m_128405_("BodyIndex", this.getBodyIndex());
      compound.m_128405_("PortalTicks", this.getPortalTicks());
      compound.m_128350_("PartAngle", this.angleYaw);
      compound.m_128350_("WormScale", this.getWormScale());
      compound.m_128350_("PartRadius", this.radius);
      compound.m_128350_("PartYOffset", this.offsetY);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128403_("ParentUUID")) {
         this.setParentId(compound.m_128342_("ParentUUID"));
      }

      if (compound.m_128403_("ChildUUID")) {
         this.setChildId(compound.m_128342_("ChildUUID"));
      }

      this.setTail(compound.m_128471_("TailPart"));
      this.setBodyIndex(compound.m_128451_("BodyIndex"));
      this.setPortalTicks(compound.m_128451_("PortalTicks"));
      this.angleYaw = compound.m_128457_("PartAngle");
      this.setWormScale(compound.m_128457_("WormScale"));
      this.radius = compound.m_128457_("PartRadius");
      this.offsetY = compound.m_128457_("PartYOffset");
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(PARENT_UUID, Optional.empty());
      this.f_19804_.m_135372_(CHILD_UUID, Optional.empty());
      this.f_19804_.m_135372_(TAIL, false);
      this.f_19804_.m_135372_(BODYINDEX, 0);
      this.f_19804_.m_135372_(WORM_SCALE, 1.0F);
      this.f_19804_.m_135372_(WORM_YAW, 0.0F);
      this.f_19804_.m_135372_(WORM_ANGLE, 0.0F);
      this.f_19804_.m_135372_(PORTAL_TICKS, 0);
   }

   @Nullable
   public UUID getParentId() {
      return (UUID)((Optional)this.f_19804_.m_135370_(PARENT_UUID)).orElse(null);
   }

   public void setParentId(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(PARENT_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.f_19804_.m_135370_(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public void setInitialPartPos(Entity parent) {
      this.m_6034_(
         parent.f_19854_ + this.radius * Math.cos(parent.m_146908_() * (Math.PI / 180.0) + this.angleYaw),
         parent.f_19855_ + this.offsetY,
         parent.f_19856_ + this.radius * Math.sin(parent.m_146908_() * (Math.PI / 180.0) + this.angleYaw)
      );
   }

   public float getWormAngle() {
      return (Float)this.f_19804_.m_135370_(WORM_ANGLE);
   }

   public void setWormAngle(float progress) {
      this.f_19804_.m_135381_(WORM_ANGLE, progress);
   }

   public int getPortalTicks() {
      return (Integer)this.f_19804_.m_135370_(PORTAL_TICKS);
   }

   public void setPortalTicks(int ticks) {
      this.f_19804_.m_135381_(PORTAL_TICKS, ticks);
   }

   public void m_8119_() {
      this.f_19817_ = false;
      this.prevWormAngle = this.getWormAngle();
      this.prevWormYaw = (Float)this.f_19804_.m_135370_(WORM_YAW);
      this.m_20256_(Vec3.f_82478_);
      this.radius = 1.0F + this.getWormScale() * (this.isTail() ? 0.65F : 0.3F) + (this.getBodyIndex() == 0 ? 0.8F : 0.0F);
      if (this.f_19797_ > 3) {
         Entity parent = this.getParent();
         this.m_6210_();
         if (parent != null && !this.m_9236_().f_46443_) {
            this.m_20242_(true);
            Vec3 parentVec = parent.m_20182_().m_82492_(parent.f_19854_, parent.f_19855_, parent.f_19856_);
            double restrictRadius = Mth.m_14008_(this.radius - parentVec.m_82556_() * 0.25, this.radius * 0.5F, this.radius);
            if (parent instanceof EntityVoidWorm) {
               restrictRadius *= this.isTail() ? 0.8F : 0.4F;
            }

            double x = parent.m_20185_() + restrictRadius * Math.cos(parent.m_146908_() * (Math.PI / 180.0) + this.angleYaw);
            double yStretch = Math.abs(parent.m_20186_() - parent.f_19855_) > this.m_20205_() ? parent.m_20186_() : parent.f_19855_;
            double y = yStretch + this.offsetY * this.getWormScale();
            double z = parent.m_20189_() + restrictRadius * Math.sin(parent.m_146908_() * (Math.PI / 180.0) + this.angleYaw);
            double d0 = parent.f_19854_ - this.m_20185_();
            double d1 = parent.f_19855_ - this.m_20186_();
            double d2 = parent.f_19856_ - this.m_20189_();
            float yaw = (float)(Mth.m_14136_(d2, d0) * 180.0F / (float)Math.PI) - 90.0F;
            float pitch = parent.m_146909_();
            if (this.getPortalTicks() <= 1 && !this.doesParentControlPos) {
               float f2 = -((float)(Mth.m_14136_(d1, Mth.m_14116_((float)(d0 * d0 + d2 * d2))) * 180.0F / (float)Math.PI));
               this.m_6034_(x, y, z);
               this.m_146926_(this.limitAngle(this.m_146909_(), f2, 5.0F));
               this.m_146922_(yaw);
               this.f_19804_.m_135381_(WORM_YAW, this.m_146908_());
            }

            this.m_5834_();
            this.f_20885_ = this.m_146908_();
            this.f_20883_ = pitch;
            if (parent instanceof LivingEntity && !this.m_9236_().f_46443_ && (((LivingEntity)parent).f_20916_ > 0 || ((LivingEntity)parent).f_20919_ > 0)) {
               AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.m_19879_(), parent.m_19879_(), 0.0F));
               this.f_20916_ = ((LivingEntity)parent).f_20916_;
               this.f_20919_ = ((LivingEntity)parent).f_20919_;
            }

            this.m_6138_();
            if (parent.m_213877_() && !this.m_9236_().f_46443_) {
               this.m_142687_(RemovalReason.DISCARDED);
            }

            if (parent instanceof EntityVoidWorm) {
               this.setWormAngle(((EntityVoidWorm)parent).prevWormAngle);
            } else if (parent instanceof EntityVoidWormPart) {
               this.setWormAngle(((EntityVoidWormPart)parent).prevWormAngle);
            }
         } else if (this.f_19797_ > 20 && !this.m_9236_().f_46443_) {
            this.m_142687_(RemovalReason.DISCARDED);
         }
      }

      if (this.f_19797_ % 400 == 0) {
         this.m_5634_(1.0F);
      }

      super.m_8119_();
      if (this.doesParentControlPos && this.enterPos != null) {
         this.m_6021_(this.enterPos.f_82479_, this.enterPos.f_82480_, this.enterPos.f_82481_);
      }

      if (this.getPortalTicks() > 0) {
         this.setPortalTicks(this.getPortalTicks() - 1);
         if (this.getPortalTicks() <= 5 && this.teleportPos != null) {
            Vec3 vec = this.teleportPos;
            this.m_6021_(vec.f_82479_, vec.f_82480_, vec.f_82481_);
            this.f_19790_ = vec.f_82479_;
            this.f_19791_ = vec.f_82480_;
            this.f_19792_ = vec.f_82481_;
            if (this.getPortalTicks() == 5 && this.getChild() instanceof EntityVoidWormPart) {
               ((EntityVoidWormPart)this.getChild()).teleportTo(this.enterPos, this.teleportPos);
            }

            this.teleportPos = null;
         } else if (this.getPortalTicks() > 5 && this.enterPos != null) {
            this.m_6021_(this.enterPos.f_82479_, this.enterPos.f_82480_, this.enterPos.f_82481_);
         }

         if (this.getPortalTicks() == 0) {
            this.doesParentControlPos = false;
         }
      }
   }

   protected void m_6153_() {
      this.f_20919_++;
      if (this.f_20919_ == 20) {
         this.m_142687_(RemovalReason.DISCARDED);

         for (int i = 0; i < 30; i++) {
            double d0 = this.f_19796_.m_188583_() * 0.02;
            double d1 = this.f_19796_.m_188583_() * 0.02;
            double d2 = this.f_19796_.m_188583_() * 0.02;
            this.m_9236_().m_7106_((ParticleOptions)AMParticleRegistry.WORM_PORTAL.get(), this.m_20208_(1.0), this.m_20187_(), this.m_20262_(1.0), d0, d1, d2);
         }
      }
   }

   public void m_6667_(DamageSource cause) {
      EntityVoidWorm worm = this.getWorm();
      if (worm != null) {
         int segments = Math.max(worm.getSegmentCount() / 2 - 1, 1);
         worm.setSegmentCount(segments);
         if (this.getChild() instanceof EntityVoidWormPart) {
            EntityVoidWormPart segment = (EntityVoidWormPart)this.getChild();
            EntityVoidWorm worm2 = (EntityVoidWorm)((EntityType)AMEntityRegistry.VOID_WORM.get()).m_20615_(this.m_9236_());
            worm2.m_21557_(worm.m_21525_());
            worm2.m_20331_(worm.m_20147_());
            worm2.m_20359_(this);
            segment.m_20359_(this);
            worm2.setChildId(segment.m_20148_());
            worm2.setSegmentCount(segments);
            segment.setParent(worm2);
            if (!this.m_9236_().f_46443_) {
               this.m_9236_().m_7967_(worm2);
            }

            worm2.setSplitter(true);
            worm2.setBaseMaxHealth(worm.getBaseMaxHealth() / 2.0, true);
            worm2.setSplitFromUuid(worm.m_20148_());
            worm2.setWormSpeed((float)Mth.m_14008_(worm.getWormSpeed() * 0.8, 0.4F, 1.0));
            worm2.resetWormScales();
            if (!this.m_9236_().f_46443_ && cause != null && cause.m_7639_() instanceof ServerPlayer) {
               AMAdvancementTriggerRegistry.VOID_WORM_SPLIT.trigger((ServerPlayer)cause.m_7639_());
            }
         }

         worm.resetWormScales();
      }
   }

   public boolean m_7307_(Entity entityIn) {
      EntityVoidWorm worm = this.getWorm();
      return super.m_7307_(entityIn) || worm != null && worm.m_7307_(entityIn);
   }

   public EntityVoidWorm getWorm() {
      Entity parent = this.getParent();

      while (parent instanceof EntityVoidWormPart) {
         parent = ((EntityVoidWormPart)parent).getParent();
      }

      return parent instanceof EntityVoidWorm ? (EntityVoidWorm)parent : null;
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.m_9236_().f_46443_ ? ((ServerLevel)this.m_9236_()).m_8791_(id) : null;
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

   @Nullable
   public ItemStack m_142340_() {
      Entity parent = this.getParent();
      return parent != null ? parent.m_142340_() : ItemStack.f_41583_;
   }

   public HumanoidArm m_5737_() {
      return null;
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public void m_6138_() {
      List<Entity> entities = this.m_9236_().m_45933_(this, this.m_20191_().m_82363_(0.2F, 0.0, 0.2F));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(entity -> !entity.m_7306_(parent) && !(entity instanceof EntityVoidWormPart) && entity.m_6094_())
            .forEach(entity -> entity.m_7334_(parent));
      }
   }

   public InteractionResult m_6096_(Player player, InteractionHand hand) {
      Entity parent = this.getParent();
      return parent != null ? parent.m_6096_(player, hand) : InteractionResult.PASS;
   }

   public boolean isHurt() {
      return this.m_21223_() <= this.getHealthThreshold();
   }

   public double getHealthThreshold() {
      return 5.0;
   }

   public boolean m_6469_(DamageSource source, float damage) {
      if (super.m_6469_(source, damage)) {
         EntityVoidWorm worm = this.getWorm();
         if (worm != null) {
            worm.playHurtSoundWorm(source);
         }

         return true;
      } else {
         return false;
      }
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

   public float getWormYaw(float partialTicks) {
      return partialTicks == 0.0F
         ? (Float)this.f_19804_.m_135370_(WORM_YAW)
         : this.prevWormYaw + ((Float)this.f_19804_.m_135370_(WORM_YAW) - this.prevWormYaw) * partialTicks;
   }

   public void teleportTo(Vec3 enterPos, Vec3 to) {
      this.setPortalTicks(10);
      this.teleportPos = to;
      this.enterPos = enterPos;
      EntityVoidWorm worm = this.getWorm();
      if (worm != null && this.getChild() == null) {
         worm.fullyThrough = true;
      }
   }
}
