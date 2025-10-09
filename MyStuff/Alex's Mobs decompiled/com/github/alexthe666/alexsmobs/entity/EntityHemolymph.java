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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class EntityHemolymph extends Entity {
   private UUID ownerUUID;
   private int ownerNetworkId;
   private boolean leftOwner;

   public EntityHemolymph(EntityType p_i50162_1_, Level p_i50162_2_) {
      super(p_i50162_1_, p_i50162_2_);
   }

   public EntityHemolymph(Level worldIn, EntityWarpedMosco p_i47273_2_) {
      this((EntityType)AMEntityRegistry.HEMOLYMPH.get(), worldIn);
      this.setShooter(p_i47273_2_);
      this.m_6034_(
         p_i47273_2_.m_20185_() - (p_i47273_2_.m_20205_() + 1.0F) * 0.35 * Mth.m_14031_(p_i47273_2_.f_20883_ * (float) (Math.PI / 180.0)),
         p_i47273_2_.m_20188_() + 0.2F,
         p_i47273_2_.m_20189_() + (p_i47273_2_.m_20205_() + 1.0F) * 0.35 * Mth.m_14089_(p_i47273_2_.f_20883_ * (float) (Math.PI / 180.0))
      );
   }

   public EntityHemolymph(Level worldIn, LivingEntity p_i47273_2_, boolean right) {
      this((EntityType)AMEntityRegistry.HEMOLYMPH.get(), worldIn);
      this.setShooter(p_i47273_2_);
      float rot = p_i47273_2_.f_20885_ + (right ? 60 : -60);
      this.m_6034_(
         p_i47273_2_.m_20185_() - p_i47273_2_.m_20205_() * 0.5 * Mth.m_14031_(rot * (float) (Math.PI / 180.0)),
         p_i47273_2_.m_20188_() - 0.2F,
         p_i47273_2_.m_20189_() + p_i47273_2_.m_20205_() * 0.5 * Mth.m_14089_(rot * (float) (Math.PI / 180.0))
      );
   }

   @OnlyIn(Dist.CLIENT)
   public EntityHemolymph(Level worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
      this((EntityType)AMEntityRegistry.HEMOLYMPH.get(), worldIn);
      this.m_6034_(x, y, z);
      this.m_20334_(p_i47274_8_, p_i47274_10_, p_i47274_12_);
   }

   public EntityHemolymph(SpawnEntity spawnEntity, Level world) {
      this((EntityType)AMEntityRegistry.HEMOLYMPH.get(), world);
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
      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      if (this.m_9236_().f_46443_) {
         float r1 = (this.f_19796_.m_188501_() - 0.5F) * 0.5F;
         float r2 = (this.f_19796_.m_188501_() - 0.5F) * 0.5F;
         float r3 = (this.f_19796_.m_188501_() - 0.5F) * 0.5F;
         this.m_9236_()
            .m_7106_(
               (ParticleOptions)AMParticleRegistry.HEMOLYMPH.get(),
               this.m_20185_() + r1,
               this.m_20186_() + r2,
               this.m_20189_() + r3,
               r1 * 0.1F,
               r2 * 0.1F,
               r3 * 0.1F
            );
      }

      super.m_8119_();
      Vec3 vector3d = this.m_20184_();
      HitResult raytraceresult = ProjectileUtil.m_278158_(this, this::canHitEntity);
      if (raytraceresult != null && raytraceresult.m_6662_() != Type.MISS) {
         this.onImpact(raytraceresult);
      }

      double d0 = this.m_20185_() + vector3d.f_82479_;
      double d1 = this.m_20186_() + vector3d.f_82480_;
      double d2 = this.m_20189_() + vector3d.f_82481_;
      this.updateRotation();
      if (this.m_9236_().m_45556_(this.m_20191_()).noneMatch(BlockStateBase::m_60795_)) {
         this.m_142687_(RemovalReason.DISCARDED);
      } else if (this.m_20072_()) {
         this.m_142687_(RemovalReason.DISCARDED);
      } else {
         this.m_20256_(vector3d.m_82490_(0.99F));
         if (!this.m_20068_()) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.02F, 0.0));
         }

         this.m_6034_(d0, d1, d2);
      }
   }

   protected void onEntityHit(EntityHitResult p_213868_1_) {
      Entity entity = this.getOwner();
      if (entity instanceof LivingEntity) {
         p_213868_1_.m_82443_().m_6469_(this.m_269291_().m_269299_(this, (LivingEntity)entity), 7.0F);
      }
   }

   protected void onHitBlock(BlockHitResult p_230299_1_) {
      BlockState blockstate = this.m_9236_().m_8055_(p_230299_1_.m_82425_());
      if (!this.m_9236_().f_46443_) {
         this.m_142687_(RemovalReason.DISCARDED);
      }
   }

   protected void m_8097_() {
   }

   public void setShooter(@Nullable Entity entityIn) {
      if (entityIn != null) {
         this.ownerUUID = entityIn.m_20148_();
         this.ownerNetworkId = entityIn.m_19879_();
      }
   }

   @Nullable
   public Entity getOwner() {
      if (this.ownerUUID != null && this.m_9236_() instanceof ServerLevel) {
         return ((ServerLevel)this.m_9236_()).m_8791_(this.ownerUUID);
      } else {
         return this.ownerNetworkId != 0 ? this.m_9236_().m_6815_(this.ownerNetworkId) : null;
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

   protected void m_7378_(CompoundTag compound) {
      if (compound.m_128403_("Owner")) {
         this.ownerUUID = compound.m_128342_("Owner");
      }

      this.leftOwner = compound.m_128471_("LeftOwner");
   }

   private boolean checkLeftOwner() {
      Entity entity = this.getOwner();
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
      this.m_146922_((float)(Mth.m_14136_(vector3d.f_82479_, vector3d.f_82481_) * 180.0F / (float)Math.PI));
      this.m_146926_((float)(Mth.m_14136_(vector3d.f_82480_, f) * 180.0F / (float)Math.PI));
      this.f_19859_ = this.m_146908_();
      this.f_19860_ = this.m_146909_();
   }

   public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
      float f = -Mth.m_14031_(p_234612_3_ * (float) (Math.PI / 180.0)) * Mth.m_14089_(p_234612_2_ * (float) (Math.PI / 180.0));
      float f1 = -Mth.m_14031_((p_234612_2_ + p_234612_4_) * (float) (Math.PI / 180.0));
      float f2 = Mth.m_14089_(p_234612_3_ * (float) (Math.PI / 180.0)) * Mth.m_14089_(p_234612_2_ * (float) (Math.PI / 180.0));
      this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
      Vec3 vector3d = p_234612_1_.m_20184_();
      this.m_20256_(this.m_20184_().m_82520_(vector3d.f_82479_, p_234612_1_.m_20096_() ? 0.0 : vector3d.f_82480_, vector3d.f_82481_));
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

   protected boolean canHitEntity(Entity p_230298_1_) {
      if (!p_230298_1_.m_5833_() && p_230298_1_.m_6084_() && p_230298_1_.m_6087_()) {
         Entity entity = this.getOwner();
         return entity == null || this.leftOwner || !entity.m_20365_(p_230298_1_);
      } else {
         return false;
      }
   }

   protected void updateRotation() {
      Vec3 vector3d = this.m_20184_();
      float f = Mth.m_14116_((float)(vector3d.f_82479_ * vector3d.f_82479_ + vector3d.f_82481_ * vector3d.f_82481_));
      this.m_146926_(lerpRotation(this.f_19860_, (float)(Mth.m_14136_(vector3d.f_82480_, f) * 180.0F / (float)Math.PI)));
      this.m_146922_(lerpRotation(this.f_19859_, (float)(Mth.m_14136_(vector3d.f_82479_, vector3d.f_82481_) * 180.0F / (float)Math.PI)));
   }
}
