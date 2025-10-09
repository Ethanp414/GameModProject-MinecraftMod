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

public class ParticleSmelly extends SimpleAnimatedParticle {
   private ParticleSmelly(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites) {
      super(world, x, y, z, sprites, 0.0F);
      this.f_107215_ = (float)motionX;
      this.f_107216_ = (float)motionY;
      this.f_107217_ = (float)motionZ;
      this.f_107663_ = this.f_107663_ * (0.7F + this.f_107223_.m_188501_() * 0.6F);
      this.f_107225_ = 15 + this.f_107223_.m_188503_(15);
      this.f_107226_ = -0.1F;
      this.m_108339_(sprites);
   }

   public void m_5989_() {
      super.m_5989_();
      this.f_107204_ = this.f_107231_;
      this.f_107215_ = this.f_107215_ + (this.f_107223_.m_188501_() - this.f_107223_.m_188501_()) * 0.05F;
      this.f_107216_ = this.f_107216_ + (this.f_107223_.m_188501_() - this.f_107223_.m_188501_()) * 0.05F;
      this.f_107217_ = this.f_107217_ + (this.f_107223_.m_188501_() - this.f_107223_.m_188501_()) * 0.05F;
      this.m_108339_(this.f_107644_);
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
         return new ParticleSmelly(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
