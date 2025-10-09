package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class EntityTossedItem extends ThrowableItemProjectile {
   protected static final EntityDataAccessor<Boolean> DART = SynchedEntityData.m_135353_(EntityTossedItem.class, EntityDataSerializers.f_135035_);

   public EntityTossedItem(EntityType p_i50154_1_, Level p_i50154_2_) {
      super(p_i50154_1_, p_i50154_2_);
   }

   public EntityTossedItem(Level worldIn, LivingEntity throwerIn) {
      super((EntityType)AMEntityRegistry.TOSSED_ITEM.get(), throwerIn, worldIn);
   }

   public EntityTossedItem(Level worldIn, double x, double y, double z) {
      super((EntityType)AMEntityRegistry.TOSSED_ITEM.get(), x, y, z, worldIn);
   }

   public EntityTossedItem(SpawnEntity spawnEntity, Level world) {
      this((EntityType)AMEntityRegistry.TOSSED_ITEM.get(), world);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DART, false);
   }

   public boolean isDart() {
      return (Boolean)this.f_19804_.m_135370_(DART);
   }

   public void setDart(boolean dart) {
      this.f_19804_.m_135381_(DART, dart);
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      if (id == 3) {
         double d0 = 0.08;

         for (int i = 0; i < 8; i++) {
            this.m_9236_()
               .m_7106_(
                  new ItemParticleOption(ParticleTypes.f_123752_, this.m_7846_()),
                  this.m_20185_(),
                  this.m_20186_(),
                  this.m_20189_(),
                  (this.f_19796_.m_188501_() - 0.5) * 0.08,
                  (this.f_19796_.m_188501_() - 0.5) * 0.08,
                  (this.f_19796_.m_188501_() - 0.5) * 0.08
               );
         }
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

   public void m_8119_() {
      super.m_8119_();
      Vec3 vector3d = this.m_20184_();
      float f = Mth.m_14116_((float)vector3d.m_165925_());
      this.m_146926_(m_37273_(this.f_19860_, (float)(Mth.m_14136_(vector3d.f_82480_, f) * 180.0F / (float)Math.PI)));
      this.m_146922_(m_37273_(this.f_19859_, (float)(Mth.m_14136_(vector3d.f_82479_, vector3d.f_82481_) * 180.0F / (float)Math.PI)));
   }

   protected static float m_37273_(float p_234614_0_, float p_234614_1_) {
      while (p_234614_1_ - p_234614_0_ < -180.0F) {
         p_234614_0_ -= 360.0F;
      }

      while (p_234614_1_ - p_234614_0_ >= 180.0F) {
         p_234614_0_ += 360.0F;
      }

      return Mth.m_14179_(0.2F, p_234614_0_, p_234614_1_);
   }

   protected void m_5790_(EntityHitResult p_213868_1_) {
      super.m_5790_(p_213868_1_);
      if (this.m_19749_() instanceof EntityCapuchinMonkey) {
         EntityCapuchinMonkey boss = (EntityCapuchinMonkey)this.m_19749_();
         if (!boss.m_7307_(p_213868_1_.m_82443_()) || !boss.m_21824_() && !(p_213868_1_.m_82443_() instanceof EntityCapuchinMonkey)) {
            p_213868_1_.m_82443_().m_6469_(this.m_269291_().m_269390_(this, boss), this.isDart() ? 8.0F : 4.0F);
         }
      }
   }

   public void m_7380_(CompoundTag compound) {
      compound.m_128379_("Dart", this.isDart());
   }

   public void m_7378_(CompoundTag compound) {
      this.setDart(compound.m_128471_("Dart"));
   }

   protected void m_6532_(HitResult result) {
      super.m_6532_(result);
      if (!this.m_9236_().f_46443_ && (!this.isDart() || result.m_6662_() == Type.BLOCK)) {
         this.m_9236_().m_7605_(this, (byte)3);
         this.m_142687_(RemovalReason.DISCARDED);
      }
   }

   protected Item m_7881_() {
      return this.isDart() ? (Item)AMItemRegistry.ANCIENT_DART.get() : Items.f_42594_;
   }
}
