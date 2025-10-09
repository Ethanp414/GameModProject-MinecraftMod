package com.github.alexthe666.alexsmobs.entity;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class EntitySquidGrapple extends Entity {
   private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.m_135353_(EntitySquidGrapple.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.m_135353_(EntitySquidGrapple.class, EntityDataSerializers.f_135040_);
   private static final EntityDataAccessor<Boolean> WITHDRAWING = SynchedEntityData.m_135353_(EntitySquidGrapple.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Optional<BlockPos>> ATTACHED_POS = SynchedEntityData.m_135353_(
      EntitySquidGrapple.class, EntityDataSerializers.f_135039_
   );
   private int ticksWithdrawing = 0;

   public EntitySquidGrapple(EntityType type, Level level) {
      super(type, level);
   }

   public EntitySquidGrapple(Level worldIn, LivingEntity player, boolean rightHand) {
      this((EntityType)AMEntityRegistry.SQUID_GRAPPLE.get(), worldIn);
      this.setOwnerId(player.m_20148_());
      float rot = player.f_20885_ + (rightHand ? 60 : -60);
      this.m_6034_(
         player.m_20185_() - player.m_20205_() * 0.5 * Mth.m_14031_(rot * (float) (Math.PI / 180.0)),
         player.m_20188_() - 0.2F,
         player.m_20189_() + player.m_20205_() * 0.5 * Mth.m_14089_(rot * (float) (Math.PI / 180.0))
      );
   }

   public EntitySquidGrapple(SpawnEntity spawnEntity, Level level) {
      this((EntityType)AMEntityRegistry.SQUID_GRAPPLE.get(), level);
   }

   protected static float lerpRotation(float f2, float f3) {
      while (f3 - f2 < -180.0F) {
         f2 -= 360.0F;
      }

      while (f3 - f2 >= 180.0F) {
         f2 += 360.0F;
      }

      return Mth.m_14179_(0.2F, f2, f3);
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      Vec3 vector3d = new Vec3(x, y, z)
         .m_82541_()
         .m_82520_(
            this.f_19796_.m_188583_() * 0.0075F * inaccuracy,
            this.f_19796_.m_188583_() * 0.0075F * inaccuracy,
            this.f_19796_.m_188583_() * 0.0075F * inaccuracy
         )
         .m_82490_(velocity);
      this.m_20256_(vector3d);
      float f = Mth.m_14116_((float)(vector3d.f_82479_ * vector3d.f_82479_ + vector3d.f_82481_ * vector3d.f_82481_));
      this.m_146922_(Mth.m_14177_((float)(Mth.m_14136_(vector3d.f_82479_, vector3d.f_82481_) * 180.0F / (float)Math.PI) + 180.0F));
      this.m_146926_((float)(Mth.m_14136_(vector3d.f_82480_, f) * 180.0F / (float)Math.PI));
      this.f_19859_ = this.m_146908_();
      this.f_19860_ = this.m_146909_();
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.f_19804_.m_135370_(ATTACHED_FACE);
   }

   public void setAttachmentFacing(Direction direction) {
      this.f_19804_.m_135381_(ATTACHED_FACE, direction);
   }

   @Nullable
   public UUID getOwnerId() {
      return (UUID)((Optional)this.f_19804_.m_135370_(OWNER_UUID)).orElse(null);
   }

   public void setOwnerId(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(OWNER_UUID, Optional.ofNullable(uniqueId));
   }

   public BlockPos getStuckToPos() {
      return (BlockPos)((Optional)this.f_19804_.m_135370_(ATTACHED_POS)).orElse(null);
   }

   public void setStuckToPos(BlockPos harvestedPos) {
      this.f_19804_.m_135381_(ATTACHED_POS, Optional.ofNullable(harvestedPos));
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(OWNER_UUID, Optional.empty());
      this.f_19804_.m_135372_(ATTACHED_FACE, Direction.DOWN);
      this.f_19804_.m_135372_(ATTACHED_POS, Optional.empty());
      this.f_19804_.m_135372_(WITHDRAWING, false);
   }

   public Entity getOwner() {
      UUID id = this.getOwnerId();
      if (id != null && !this.m_9236_().f_46443_) {
         return ((ServerLevel)this.m_9236_()).m_8791_(id);
      } else {
         return this.getOwnerId() == null ? null : this.m_9236_().m_46003_(this.getOwnerId());
      }
   }

   public boolean isWithdrawing() {
      return (Boolean)this.f_19804_.m_135370_(WITHDRAWING);
   }

   public void setWithdrawing(boolean withdrawing) {
      this.f_19804_.m_135381_(WITHDRAWING, withdrawing);
   }

   public void m_8119_() {
      this.f_19860_ = this.m_146909_();
      this.f_19859_ = this.m_146908_();
      Entity entity = this.getOwner();
      if (!this.m_9236_().f_46443_) {
         if (entity == null || !entity.m_6084_()) {
            this.m_146870_();
         } else if (entity.m_6144_()) {
            this.setWithdrawing(true);
         }
      }

      if (this.isWithdrawing() && entity != null) {
         super.m_8119_();
         this.ticksWithdrawing++;
         this.setStuckToPos(null);
         Vec3 withDrawTo = entity.m_146892_().m_82520_(0.0, -0.2F, 0.0);
         if (withDrawTo.m_82554_(this.m_20182_()) > 1.2F && this.ticksWithdrawing < 200) {
            Vec3 move = new Vec3(withDrawTo.f_82479_ - this.m_20185_(), withDrawTo.f_82480_ - this.m_20186_(), withDrawTo.f_82481_ - this.m_20189_());
            Vec3 vector3d = move.m_82541_().m_82490_(1.2);
            this.m_20256_(vector3d.m_82490_(0.99));
            double d0 = this.m_20185_() + vector3d.f_82479_;
            double d1 = this.m_20186_() + vector3d.f_82480_;
            double d2 = this.m_20189_() + vector3d.f_82481_;
            float f = Mth.m_14116_((float)(move.f_82479_ * move.f_82479_ + move.f_82481_ * move.f_82481_));
            if (!this.m_9236_().f_46443_) {
               this.m_146922_(Mth.m_14177_((float)(-Mth.m_14136_(move.f_82479_, move.f_82481_) * 180.0F / (float)Math.PI)) - 180.0F);
               this.m_146926_((float)(Mth.m_14136_(move.f_82480_, f) * 180.0F / (float)Math.PI));
               this.f_19859_ = this.m_146908_();
               this.f_19860_ = this.m_146909_();
            }

            this.m_6034_(d0, d1, d2);
         } else {
            this.m_146870_();
         }
      } else if (!this.m_9236_().f_46443_ && !this.m_9236_().m_46805_(this.m_20183_())) {
         this.m_146870_();
      } else if (this.getStuckToPos() == null) {
         super.m_8119_();
         Vec3 vector3d = this.m_20184_();
         HitResult raytraceresult = ProjectileUtil.m_278158_(this, newentity -> false);
         if (raytraceresult != null && raytraceresult.m_6662_() != Type.MISS) {
            this.onImpact(raytraceresult);
         }

         this.m_20101_();
         double d0 = this.m_20185_() + vector3d.f_82479_;
         double d1 = this.m_20186_() + vector3d.f_82480_;
         double d2 = this.m_20189_() + vector3d.f_82481_;
         this.updateRotation();
         this.m_20256_(vector3d.m_82490_(0.99));
         if (this.m_9236_().m_45556_(this.m_20191_()).noneMatch(BlockStateBase::m_60795_) && !this.m_20069_()) {
            this.m_20256_(Vec3.f_82478_);
         } else {
            this.m_6034_(d0, d1, d2);
         }

         if (!this.m_20068_()) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.1F, 0.0));
         }
      } else {
         BlockState state = this.m_9236_().m_8055_(this.getStuckToPos());
         Vec3 vec3 = new Vec3(this.getStuckToPos().m_123341_() + 0.5F, this.getStuckToPos().m_123342_() + 0.5F, this.getStuckToPos().m_123343_() + 0.5F);
         Vec3 offset = new Vec3(
            this.getAttachmentFacing().m_122429_() * 0.55F, this.getAttachmentFacing().m_122430_() * 0.55F, this.getAttachmentFacing().m_122431_() * 0.55F
         );
         this.m_146884_(vec3.m_82549_(offset));
         float targetX = this.m_146909_();
         float targetY = this.m_146908_();
         switch (this.getAttachmentFacing()) {
            case UP:
               targetX = 0.0F;
               break;
            case DOWN:
               targetX = 180.0F;
               break;
            case NORTH:
               targetX = -90.0F;
               targetY = 0.0F;
               break;
            case EAST:
               targetX = -90.0F;
               targetY = 90.0F;
               break;
            case SOUTH:
               targetX = -90.0F;
               targetY = 180.0F;
               break;
            case WEST:
               targetX = -90.0F;
               targetY = -90.0F;
         }

         this.m_146926_(targetX);
         this.m_146922_(targetY);
         if (entity != null && entity.m_20270_(this) > 2.0F) {
            float entitySwing = 1.0F;
            if (entity instanceof LivingEntity living) {
               float detract = living.f_20900_ * living.f_20900_ + living.f_20901_ * living.f_20901_ + living.f_20902_ * living.f_20902_;
               entitySwing = (float)(entitySwing - Math.min(1.0, Math.sqrt(detract) * 0.333F));
            }

            Vec3 move = new Vec3(
               this.m_20185_() - entity.m_20185_(), this.m_20186_() - entity.m_20192_() / 2.0 - entity.m_20186_(), this.m_20189_() - entity.m_20189_()
            );
            entity.m_20256_(entity.m_20184_().m_82549_(move.m_82541_().m_82490_(0.2 * entitySwing)));
            if (!entity.m_20096_()) {
               entity.f_19789_ = 0.0F;
            }
         }

         if (state.m_60795_()) {
            this.setWithdrawing(true);
         }
      }
   }

   protected float rotlerp(float in, float target, float maxShift) {
      float f = Mth.m_14177_(target - in);
      if (f > maxShift) {
         f = maxShift;
      }

      if (f < -maxShift) {
         f = -maxShift;
      }

      float f1 = in + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }

   private void updateRotation() {
   }

   protected void onImpact(HitResult result) {
      Type raytraceresult$type = result.m_6662_();
      if (!this.m_9236_().f_46443_ && raytraceresult$type == Type.BLOCK && this.getStuckToPos() == null) {
         this.m_20256_(Vec3.f_82478_);
         this.setStuckToPos(((BlockHitResult)result).m_82425_());
         this.setAttachmentFacing(((BlockHitResult)result).m_82434_());
      }
   }

   protected void m_7378_(CompoundTag compound) {
      if (this.getOwnerId() != null) {
         compound.m_128362_("OwnerUUID", this.getOwnerId());
      }
   }

   protected void m_7380_(CompoundTag compound) {
      if (compound.m_128403_("OwnerUUID")) {
         this.setOwnerId(compound.m_128342_("OwnerUUID"));
      }
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
