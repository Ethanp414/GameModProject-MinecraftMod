package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleBunfungusTransformation extends TextureSheetParticle {
   private final SpriteSet sprites;

   public ParticleBunfungusTransformation(
      ClientLevel p_107483_, double p_107484_, double p_107485_, double p_107486_, double p_107487_, double p_107488_, double p_107489_, SpriteSet p_107490_
   ) {
      super(p_107483_, p_107484_, p_107485_, p_107486_, 0.0, 0.0, 0.0);
      this.f_172258_ = 0.96F;
      this.sprites = p_107490_;
      float f = 2.5F;
      this.f_107215_ *= 0.1F;
      this.f_107216_ *= 0.1F;
      this.f_107217_ *= 0.1F;
      this.f_107215_ += p_107487_;
      this.f_107216_ += p_107488_;
      this.f_107217_ += p_107489_;
      int i = (int)(8.0 / (Math.random() * 0.8 + 0.3));
      this.f_107225_ = (int)Math.max(i * 2.5F, 1.0F);
      this.f_107219_ = false;
      this.m_108339_(p_107490_);
      this.m_107271_((float)(Math.random() * 0.3F + 0.7F));
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public float m_5902_(float p_107504_) {
      return this.f_107663_ * Mth.m_14036_((this.f_107224_ + p_107504_) / this.f_107225_ * 32.0F, 0.0F, 1.0F);
   }

   public void m_5989_() {
      super.m_5989_();
      if (!this.f_107220_) {
         this.m_108339_(this.sprites);
         Player player = this.f_107208_.m_45924_(this.f_107212_, this.f_107213_, this.f_107214_, 2.0, false);
         if (player != null) {
            double d0 = player.m_20186_();
            if (this.f_107213_ > d0) {
               this.f_107213_ = this.f_107213_ + (d0 - this.f_107213_) * 0.2;
               this.f_107216_ = this.f_107216_ + (player.m_20184_().f_82480_ - this.f_107216_) * 0.2;
               this.m_107264_(this.f_107212_, this.f_107213_, this.f_107214_);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Factory(SpriteSet p_107528_) {
         this.sprites = p_107528_;
      }

      public Particle createParticle(
         SimpleParticleType p_107539_,
         ClientLevel p_107540_,
         double p_107541_,
         double p_107542_,
         double p_107543_,
         double p_107544_,
         double p_107545_,
         double p_107546_
      ) {
         ParticleBunfungusTransformation particle = new ParticleBunfungusTransformation(
            p_107540_, p_107541_, p_107542_, p_107543_, p_107544_, p_107545_, p_107546_, this.sprites
         );
         particle.m_107253_(0.427451F, 0.4470588F, 0.5764706F);
         return particle;
      }
   }
}
