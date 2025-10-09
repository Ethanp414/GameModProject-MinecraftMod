package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class EntityGust extends Entity {
   protected static final EntityDataAccessor<Boolean> VERTICAL = SynchedEntityData.m_135353_(EntityGust.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Float> X_DIR = SynchedEntityData.m_135353_(EntityGust.class, EntityDataSerializers.f_135029_);
   protected static final EntityDataAccessor<Float> Y_DIR = SynchedEntityData.m_135353_(EntityGust.class, EntityDataSerializers.f_135029_);
   protected static final EntityDataAccessor<Float> Z_DIR = SynchedEntityData.m_135353_(EntityGust.class, EntityDataSerializers.f_135029_);
   private Entity pushedEntity = null;

   public EntityGust(EntityType p_i50162_1_, Level p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public EntityGust(Level worldIn) {
      this((EntityType)AMEntityRegistry.GUST.get(), worldIn);
   }

   public EntityGust(SpawnEntity spawnEntity, Level world) {
      this((EntityType)AMEntityRegistry.GUST.get(), world);
   }

   public void m_7334_(Entity entityIn) {
   }

   protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
      while (p_234614_1_ - p_234614_0_ < -180.0F) {
         p_234614_0_ -= 360.0F;
      }

      while (p_234614_1_ - p_234614_0_ >= 180.0F) {
         p_234614_0_ += 360.0F;
      }

      return Mth.m_14179_(0.2F, p_234614_0_, p_234614_1_);
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ > 300) {
         this.m_142687_(RemovalReason.DISCARDED);
      }

      for (int i = 0; i < 1 + this.f_19796_.m_188503_(1); i++) {
         this.m_9236_()
            .m_7106_(
               (ParticleOptions)AMParticleRegistry.GUSTER_SAND_SPIN.get(),
               this.m_20185_() + 0.5F * (this.f_19796_.m_188501_() - 0.5F),
               this.m_20186_() + 0.5F * (this.f_19796_.m_188501_() - 0.5F),
               this.m_20189_() + 0.5F * (this.f_19796_.m_188501_() - 0.5F),
               this.m_20185_(),
               this.m_20186_() + 0.5,
               this.m_20189_()
            );
      }

      Vec3 vector3d = new Vec3(
         ((Float)this.f_19804_.m_135370_(X_DIR)).floatValue(),
         ((Float)this.f_19804_.m_135370_(Y_DIR)).floatValue(),
         ((Float)this.f_19804_.m_135370_(Z_DIR)).floatValue()
      );
      HitResult raytraceresult = ProjectileUtil.m_278158_(this, this::canHitEntity);
      if (raytraceresult != null && raytraceresult.m_6662_() != Type.MISS && this.f_19797_ > 4) {
         this.onImpact(raytraceresult);
      }

      List<Entity> list = this.m_9236_().m_45976_(Entity.class, this.m_20191_().m_82400_(0.1));
      if (this.pushedEntity != null && this.m_20270_(this.pushedEntity) > 2.0F) {
         this.pushedEntity = null;
      }

      double d0 = this.m_20185_() + vector3d.f_82479_;
      double d1 = this.m_20186_() + vector3d.f_82480_;
      double d2 = this.m_20189_() + vector3d.f_82481_;
      if (this.m_20186_() > this.m_9236_().m_151558_()) {
         this.m_142687_(RemovalReason.DISCARDED);
      }

      this.updateRotation();
      if (this.m_20072_()) {
         this.m_142687_(RemovalReason.DISCARDED);
      } else {
         this.m_20256_(vector3d);
         this.m_20256_(this.m_20184_().m_82520_(0.0, -0.06F, 0.0));
         this.m_6034_(d0, d1, d2);
         if (this.pushedEntity != null) {
            this.pushedEntity.m_20256_(this.m_20184_().m_82520_(0.0, 0.063, 0.0));
         }

         for (Entity e : list) {
            e.m_20256_(this.m_20184_().m_82520_(0.0, 0.068, 0.0));
            if (e.m_20184_().f_82480_ < 0.0) {
               e.m_20256_(e.m_20184_().m_82542_(1.0, 0.0, 1.0));
            }

            e.f_19789_ = 0.0F;
         }
      }
   }

   public void setGustDir(float x, float y, float z) {
      this.f_19804_.m_135381_(X_DIR, x);
      this.f_19804_.m_135381_(Y_DIR, y);
      this.f_19804_.m_135381_(Z_DIR, z);
   }

   public float getGustDir(int xyz) {
      return (Float)this.f_19804_.m_135370_(xyz == 2 ? Z_DIR : (xyz == 1 ? Y_DIR : X_DIR));
   }

   protected void onEntityHit(EntityHitResult result) {
      Entity entity = result.m_82443_();
      if (entity instanceof EntityGust other) {
         double avgX = (other.m_20185_() + this.m_20185_()) / 2.0;
         double avgY = (other.m_20186_() + this.m_20186_()) / 2.0;
         double avgZ = (other.m_20189_() + this.m_20189_()) / 2.0;
         other.m_6034_(avgX, avgY, avgZ);
         other.setGustDir(other.getGustDir(0) + this.getGustDir(0), other.getGustDir(1) + this.getGustDir(1), other.getGustDir(2) + this.getGustDir(2));
         if (this.m_6084_() && other.m_6084_()) {
            this.m_142687_(RemovalReason.DISCARDED);
         }
      } else if (entity != null) {
         this.pushedEntity = entity;
      }
   }

   protected boolean canHitEntity(Entity p_230298_1_) {
      return !p_230298_1_.m_5833_();
   }

   protected void onHitBlock(BlockHitResult p_230299_1_) {
      if (p_230299_1_.m_82425_() != null) {
         BlockPos pos = p_230299_1_.m_82425_();
         if (this.m_9236_().m_46801_(pos) && !this.m_9236_().f_46443_) {
            this.m_142687_(RemovalReason.DISCARDED);
         }
      }
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(VERTICAL, false);
      this.f_19804_.m_135372_(X_DIR, 0.0F);
      this.f_19804_.m_135372_(Y_DIR, 0.0F);
      this.f_19804_.m_135372_(Z_DIR, 0.0F);
   }

   protected void m_7380_(CompoundTag compound) {
      compound.m_128379_("VerticalTornado", this.getVertical());
      compound.m_128350_("GustDirX", (Float)this.f_19804_.m_135370_(X_DIR));
      compound.m_128350_("GustDirY", (Float)this.f_19804_.m_135370_(Y_DIR));
      compound.m_128350_("GustDirZ", (Float)this.f_19804_.m_135370_(Z_DIR));
   }

   protected void m_7378_(CompoundTag compound) {
      this.f_19804_.m_135381_(X_DIR, compound.m_128457_("GustDirX"));
      this.f_19804_.m_135381_(Y_DIR, compound.m_128457_("GustDirX"));
      this.f_19804_.m_135381_(Z_DIR, compound.m_128457_("GustDirX"));
      this.setVertical(compound.m_128471_("VerticalTornado"));
   }

   public void setVertical(boolean vertical) {
      this.f_19804_.m_135381_(VERTICAL, vertical);
   }

   public boolean getVertical() {
      return (Boolean)this.f_19804_.m_135370_(VERTICAL);
   }

   protected void onImpact(HitResult result) {
      Type raytraceresult$type = result.m_6662_();
      if (raytraceresult$type == Type.ENTITY) {
         this.onEntityHit((EntityHitResult)result);
      } else if (raytraceresult$type == Type.BLOCK) {
         this.onHitBlock((BlockHitResult)result);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void m_6001_(double x, double y, double z) {
      this.m_20334_(x, y, z);
      if (this.f_19860_ == 0.0F && this.f_19859_ == 0.0F) {
         float f = Mth.m_14116_((float)(x * x + z * z));
         this.m_146926_((float)(Mth.m_14136_(y, f) * 180.0F / (float)Math.PI));
         this.m_146922_((float)(Mth.m_14136_(x, z) * 180.0F / (float)Math.PI));
         this.f_19860_ = this.m_146909_();
         this.f_19859_ = this.m_146908_();
         this.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_146908_(), this.m_146909_());
      }
   }

   protected void updateRotation() {
      Vec3 vector3d = this.m_20184_();
      float f = Mth.m_14116_((float)(vector3d.f_82479_ * vector3d.f_82479_ + vector3d.f_82481_ * vector3d.f_82481_));
      this.m_146926_(lerpRotation(this.f_19860_, (float)(Mth.m_14136_(vector3d.f_82480_, f) * 180.0F / (float)Math.PI)));
      this.m_146922_(lerpRotation(this.f_19859_, (float)(Mth.m_14136_(vector3d.f_82479_, vector3d.f_82481_) * 180.0F / (float)Math.PI)));
   }
}
