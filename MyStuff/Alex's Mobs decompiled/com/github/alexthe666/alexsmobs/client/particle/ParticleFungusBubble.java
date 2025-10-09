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

public class ParticleFungusBubble extends SimpleAnimatedParticle {
   private ParticleFungusBubble(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites) {
      super(world, x, y, z, sprites, 0.0F);
      this.f_107215_ = (float)motionX;
      this.f_107216_ = (float)motionY + 0.04;
      this.f_107217_ = (float)motionZ;
      this.f_107663_ = this.f_107663_ * (0.5F + this.f_107223_.m_188501_() * 0.5F);
      this.f_107225_ = 15 + this.f_107223_.m_188503_(20);
      this.f_107226_ = -0.1F;
      this.m_108337_(this.f_107644_.m_5819_(0, this.f_107225_));
   }

   public void m_5989_() {
      super.m_5989_();
      int halflife = this.f_107225_ / 2;
      int spriteMod = halflife / 6;
      boolean flag = false;
      if (this.f_107224_ < halflife) {
         flag = true;
         this.m_108337_(this.f_107644_.m_5819_(0, 6));
      } else if (this.f_107224_ < halflife + spriteMod) {
         this.m_108337_(this.f_107644_.m_5819_(1, 6));
      } else if (this.f_107224_ < halflife + spriteMod * 2) {
         this.m_108337_(this.f_107644_.m_5819_(2, 6));
      } else if (this.f_107224_ < halflife + spriteMod * 3) {
         this.m_108337_(this.f_107644_.m_5819_(3, 6));
      } else if (this.f_107224_ < halflife + spriteMod * 4) {
         this.m_108337_(this.f_107644_.m_5819_(4, 6));
      } else if (this.f_107224_ < halflife + spriteMod * 5) {
         this.m_108337_(this.f_107644_.m_5819_(5, 6));
      } else {
         this.m_108337_(this.f_107644_.m_5819_(6, 6));
      }

      if (!flag) {
         this.f_107216_ = 0.0;
      }
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107430_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleFungusBubble(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
