package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleWormPortal extends SimpleAnimatedParticle {
   private ParticleWormPortal(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites) {
      super(world, x, y, z, sprites, 0.0F);
      this.f_107215_ = (float)motionX;
      this.f_107216_ = (float)motionY;
      this.f_107217_ = (float)motionZ;
      this.f_107663_ = 0.35F;
      this.f_107225_ = 10 + this.f_107223_.m_188503_(12);
      this.f_107226_ = 0.0F;
      this.m_108339_(sprites);
   }

   public int m_6355_(float p_189214_1_) {
      int lvt_2_1_ = super.m_6355_(p_189214_1_);
      int lvt_4_1_ = lvt_2_1_ >> 16 & 0xFF;
      return 240 | lvt_4_1_ << 16;
   }

   public void m_5989_() {
      super.m_5989_();
      this.f_107204_ = this.f_107231_;
      this.f_107215_ *= 0.8;
      this.f_107216_ *= 0.8;
      this.f_107217_ *= 0.8;
      this.f_107231_ += 0.25F;
      this.m_108339_(this.f_107644_);
      this.f_107663_ = 0.35F * (1.0F - (float)this.f_107224_ / this.f_107225_);
      this.m_107271_(1.0F - (float)this.f_107224_ / this.f_107225_);
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleWormPortal(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
