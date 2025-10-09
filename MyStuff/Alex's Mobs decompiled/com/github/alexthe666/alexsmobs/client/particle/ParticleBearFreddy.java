package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderGrizzlyBear;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleBearFreddy extends Particle {
   private final ModelGrizzlyBear model = new ModelGrizzlyBear();

   ParticleBearFreddy(ClientLevel lvl, double x, double y, double z) {
      super(lvl, x, y, z);
      this.m_107250_(2.0F, 2.0F);
      this.f_107226_ = 0.0F;
      this.f_107225_ = 15;
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107433_;
   }

   public void m_5744_(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
      float fogBefore = RenderSystem.getShaderFogEnd();
      RenderSystem.setShaderFogEnd(40.0F);
      float f = (this.f_107224_ + partialTick) / this.f_107225_;
      float initalFlip = Math.min(f, 0.1F) / 0.1F;
      float laterFlip = Mth.m_14036_(f - 0.1F, 0.0F, 0.1F) / 0.1F;
      float scale = 1.0F;
      PoseStack posestack = new PoseStack();
      posestack.m_252781_(camera.m_253121_());
      posestack.m_85837_(0.0, -1.0, 0.0);
      posestack.m_252781_(Axis.f_252529_.m_252977_(10.0F - laterFlip * 35.0F));
      posestack.m_85841_(-scale, -scale, scale);
      posestack.m_85837_(0.0, 0.5, 2.0F + (1.0F - initalFlip));
      BufferSource multibuffersource$buffersource = Minecraft.m_91087_().m_91269_().m_110104_();
      VertexConsumer vertexconsumer = multibuffersource$buffersource.m_6299_(AMRenderTypes.getFreddy(RenderGrizzlyBear.TEXTURE_FREDDY));
      posestack.m_252781_(Axis.f_252529_.m_252977_(initalFlip * 20.0F - 5.0F));
      float swing = laterFlip * (float)Math.sin((this.f_107224_ + partialTick) * 0.3F) * 20.0F;
      posestack.m_252781_(Axis.f_252403_.m_252977_((1.0F - initalFlip) * 45.0F + swing));
      boolean baby = this.model.f_102610_;
      this.model.f_102610_ = false;
      this.model.positionForParticle(partialTick, this.f_107224_ + partialTick);
      this.model.m_7695_(posestack, vertexconsumer, 240, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
      this.model.f_102610_ = baby;
      multibuffersource$buffersource.m_109911_();
      RenderSystem.setShaderFogEnd(fogBefore);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleBearFreddy(worldIn, x, y, z);
      }
   }
}
