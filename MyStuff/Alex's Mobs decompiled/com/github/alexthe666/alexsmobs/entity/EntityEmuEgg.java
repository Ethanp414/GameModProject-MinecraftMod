package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class EntityEmuEgg extends ThrowableItemProjectile {
   public EntityEmuEgg(EntityType p_i50154_1_, Level p_i50154_2_) {
      super(p_i50154_1_, p_i50154_2_);
   }

   public EntityEmuEgg(Level worldIn, LivingEntity throwerIn) {
      super((EntityType)AMEntityRegistry.EMU_EGG.get(), throwerIn, worldIn);
   }

   public EntityEmuEgg(Level worldIn, double x, double y, double z) {
      super((EntityType)AMEntityRegistry.EMU_EGG.get(), x, y, z, worldIn);
   }

   public EntityEmuEgg(SpawnEntity spawnEntity, Level world) {
      this((EntityType)AMEntityRegistry.EMU_EGG.get(), world);
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      if (id == 3) {
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

   protected void m_6532_(HitResult result) {
      super.m_6532_(result);
      if (!this.m_9236_().f_46443_) {
         if (this.f_19796_.m_188503_(8) == 0) {
            int lvt_2_1_ = 1;
            if (this.f_19796_.m_188503_(32) == 0) {
               lvt_2_1_ = 4;
            }

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; lvt_3_1_++) {
               EntityEmu lvt_4_1_ = (EntityEmu)((EntityType)AMEntityRegistry.EMU.get()).m_20615_(this.m_9236_());
               if (this.f_19796_.m_188503_(50) == 0) {
                  lvt_4_1_.setVariant(2);
               } else if (this.f_19796_.m_188503_(3) == 0) {
                  lvt_4_1_.setVariant(1);
               }

               lvt_4_1_.m_146762_(-24000);
               lvt_4_1_.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_146908_(), 0.0F);
               this.m_9236_().m_7967_(lvt_4_1_);
            }
         }

         this.m_9236_().m_7605_(this, (byte)3);
         this.m_142687_(RemovalReason.DISCARDED);
      }
   }

   protected Item m_7881_() {
      return (Item)AMItemRegistry.EMU_EGG.get();
   }
}
