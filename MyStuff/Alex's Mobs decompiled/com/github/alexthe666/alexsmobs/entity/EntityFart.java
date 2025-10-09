package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

public class EntityFart extends Entity {
   private UUID ownerUUID;
   private int ownerNetworkId;
   private boolean leftOwner;

   public EntityFart(EntityType p_i50162_1_, Level p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public EntityFart(Level worldIn, LivingEntity p_i47273_2_, boolean right) {
      this((EntityType)AMEntityRegistry.FART.get(), worldIn);
      this.setShooter(p_i47273_2_);
      float rot = p_i47273_2_.f_20885_ + (right ? 60 : -60);
      this.m_6034_(
         p_i47273_2_.m_20185_() - p_i47273_2_.m_20205_() * 0.5 * Mth.m_14031_(rot * (float) (Math.PI / 180.0)),
         p_i47273_2_.m_20188_() - 0.2F,
         p_i47273_2_.m_20189_() + p_i47273_2_.m_20205_() * 0.5 * Mth.m_14089_(rot * (float) (Math.PI / 180.0))
      );
   }

   public EntityFart(SpawnEntity spawnEntity, Level world) {
      this((EntityType)AMEntityRegistry.FART.get(), world);
   }

   public void m_8119_() {
      super.m_8119_();
      Vec3 vector3d = this.m_20184_();
      HitResult raytraceresult = ProjectileUtil.m_278158_(this, this::canHitEntity);
      if (raytraceresult != null && raytraceresult.m_6662_() != Type.MISS) {
         this.onImpact(raytraceresult);
      }

      this.updateRotation();
      double d0 = this.m_20185_() + vector3d.f_82479_;
      double d1 = this.m_20186_() + vector3d.f_82480_;
      double d2 = this.m_20189_() + vector3d.f_82481_;
      this.m_20256_(vector3d.m_82490_(0.95F));
      this.m_6034_(d0, d1, d2);
      if (this.f_19797_ > 30) {
         this.m_142687_(RemovalReason.DISCARDED);
      }
   }

   public boolean m_20068_() {
      return true;
   }

   protected void onImpact(HitResult result) {
      Type raytraceresult$type = result.m_6662_();
      if (raytraceresult$type == Type.ENTITY) {
         this.onEntityHit((EntityHitResult)result);
      } else if (raytraceresult$type == Type.BLOCK) {
         this.onHitBlock((BlockHitResult)result);
      }
   }

   protected void onEntityHit(EntityHitResult result) {
      if (result.m_82443_() instanceof LivingEntity living) {
         living.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 300, 0));

         for (int i = 0; i < 10 + this.f_19796_.m_188503_(6); i++) {
            this.m_9236_()
               .m_7106_((ParticleOptions)AMParticleRegistry.SMELLY.get(), living.m_20208_(1.0), living.m_20187_(), living.m_20262_(1.0), 0.0, 0.0, 0.0);
         }

         for (Mob nearby : this.m_9236_().m_45976_(Mob.class, living.m_20191_().m_82400_(15.0))) {
            if (nearby != living
               && nearby.m_19879_() != living.m_19879_()
               && !nearby.m_20148_().equals(living.m_20148_())
               && !nearby.m_7307_(living)
               && !living.m_7307_(nearby)
               && !(living instanceof IHurtableMultipart)) {
               nearby.m_6703_(living);
               nearby.m_6710_(living);
            }
         }
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      this.m_142687_(RemovalReason.DISCARDED);
   }

   protected boolean canHitEntity(Entity hit) {
      if (!hit.m_5833_() && hit.m_6084_() && hit.m_6087_()) {
         Entity entity = this.getShooter();
         return entity == null || this.leftOwner || !entity.m_20365_(hit);
      } else {
         return false;
      }
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

   protected void updateRotation() {
      Vec3 vector3d = this.m_20184_().m_82541_();
      float f = Mth.m_14116_((float)vector3d.m_165925_());
      this.m_146926_(lerpRotation(this.f_19860_, (float)(Mth.m_14136_(vector3d.f_82480_, f) * 180.0F / (float)Math.PI)));
      this.m_146922_(lerpRotation(this.f_19859_, (float)(Mth.m_14136_(vector3d.f_82479_, vector3d.f_82481_) * 180.0F / (float)Math.PI)));
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

   protected void m_7380_(CompoundTag compound) {
      if (this.ownerUUID != null) {
         compound.m_128362_("Owner", this.ownerUUID);
      }

      if (this.leftOwner) {
         compound.m_128379_("LeftOwner", true);
      }
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void m_8097_() {
   }

   protected void m_7378_(CompoundTag compound) {
      if (compound.m_128403_("Owner")) {
         this.ownerUUID = compound.m_128342_("Owner");
      }

      this.leftOwner = compound.m_128471_("LeftOwner");
   }

   @Nullable
   public Entity getShooter() {
      if (this.ownerUUID != null && this.m_9236_() instanceof ServerLevel) {
         return ((ServerLevel)this.m_9236_()).m_8791_(this.ownerUUID);
      } else {
         return this.ownerNetworkId != 0 ? this.m_9236_().m_6815_(this.ownerNetworkId) : null;
      }
   }

   public void setShooter(@Nullable Entity entityIn) {
      if (entityIn != null) {
         this.ownerUUID = entityIn.m_20148_();
         this.ownerNetworkId = entityIn.m_19879_();
      }
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
      float f = Mth.m_14116_((float)vector3d.m_165925_());
      this.m_146922_((float)(Mth.m_14136_(vector3d.f_82479_, vector3d.f_82481_) * 180.0F / (float)Math.PI));
      this.m_146926_((float)(Mth.m_14136_(vector3d.f_82480_, f) * 180.0F / (float)Math.PI));
      this.f_19859_ = this.m_146908_();
      this.f_19860_ = this.m_146909_();
   }

   private boolean checkLeftOwner() {
      Entity entity = this.getShooter();
      if (entity != null) {
         for (Entity entity1 : this.m_9236_()
            .m_6249_(this, this.m_20191_().m_82369_(this.m_20184_()).m_82400_(1.0), p_234613_0_ -> !p_234613_0_.m_5833_() && p_234613_0_.m_6087_())) {
            if (entity1.m_20201_() == entity.m_20201_()) {
               return false;
            }
         }
      }

      return true;
   }
}
