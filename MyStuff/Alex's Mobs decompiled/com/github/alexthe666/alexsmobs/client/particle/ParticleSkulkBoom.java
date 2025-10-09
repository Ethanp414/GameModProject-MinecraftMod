package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleSkulkBoom extends Particle {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/particle/skulk_boom.png");
   private float size;
   private float prevSize;
   private float prevAlpha;
   private final float alphaDecrease;

   private ParticleSkulkBoom(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.m_107250_(1.0F, 0.1F);
      this.f_107230_ = 1.0F;
      this.f_107226_ = 0.0F;
      this.f_107215_ = motionX;
      this.f_107216_ = motionY;
      this.f_107217_ = motionZ;
      this.f_107225_ = 20 + this.f_107223_.m_188503_(20);
      this.alphaDecrease = 1.0F / Math.max((float)this.f_107225_, 1.0F);
      this.size = 0.3F;
   }

   public void m_5989_() {
      super.m_5989_();
      this.prevSize = this.size;
      this.prevAlpha = this.f_107230_;
      this.size += 0.3F;
      this.f_107215_ *= 0.1;
      this.f_107216_ *= 0.8;
      this.f_107217_ *= 0.1;
      if (this.f_107230_ > 0.0F) {
         this.f_107230_ = Math.max(this.f_107230_ - this.alphaDecrease, 0.0F);
      }

      this.m_107250_(1.0F + this.size, 0.1F);
   }

   public void m_5744_(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
      Vec3 vec3 = camera.m_90583_();
      float f = (float)(Mth.m_14139_(partialTick, this.f_107209_, this.f_107212_) - vec3.m_7096_());
      float f1 = (float)(Mth.m_14139_(partialTick, this.f_107210_, this.f_107213_) - vec3.m_7098_());
      float f2 = (float)(Mth.m_14139_(partialTick, this.f_107211_, this.f_107214_) - vec3.m_7094_());
      Quaternionf quaternion = Axis.f_252529_.m_252977_(90.0F);
      BufferSource multibuffersource$buffersource = Minecraft.m_91087_().m_91269_().m_110104_();
      VertexConsumer portalStatic = multibuffersource$buffersource.m_6299_(AMRenderTypes.getSkulkBoom());
      PoseStack posestack = new PoseStack();
      Pose posestack$pose = posestack.m_85850_();
      Matrix4f matrix4f = posestack$pose.m_252922_();
      Matrix3f matrix3f = posestack$pose.m_252943_();
      float f4 = this.prevSize + partialTick * (this.size - this.prevSize);
      float alphaLerp = this.prevAlpha + partialTick * (this.f_107230_ - this.prevAlpha);
      Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
      vector3f1.rotate(quaternion);
      Vector3f[] avector3f = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };

      for (int i = 0; i < 4; i++) {
         Vector3f vector3f = avector3f[i];
         vector3f.rotate(quaternion);
         vector3f.mul(f4);
         vector3f.add(f, f1, f2);
      }

      float f7 = 0.0F;
      float f8 = 1.0F;
      float f5 = 0.0F;
      float f6 = 1.0F;
      int j = 240;
      portalStatic.m_5483_(avector3f[0].x(), avector3f[0].y(), avector3f[0].z())
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, alphaLerp)
         .m_7421_(f8, f6)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(j)
         .m_252939_(matrix3f, 0.0F, -1.0F, 0.0F)
         .m_5752_();
      portalStatic.m_5483_(avector3f[1].x(), avector3f[1].y(), avector3f[1].z())
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, alphaLerp)
         .m_7421_(f8, f5)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(j)
         .m_252939_(matrix3f, 0.0F, -1.0F, 0.0F)
         .m_5752_();
      portalStatic.m_5483_(avector3f[2].x(), avector3f[2].y(), avector3f[2].z())
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, alphaLerp)
         .m_7421_(f7, f5)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(j)
         .m_252939_(matrix3f, 0.0F, -1.0F, 0.0F)
         .m_5752_();
      portalStatic.m_5483_(avector3f[3].x(), avector3f[3].y(), avector3f[3].z())
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, alphaLerp)
         .m_7421_(f7, f6)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(j)
         .m_252939_(matrix3f, 0.0F, -1.0F, 0.0F)
         .m_5752_();
      multibuffersource$buffersource.m_109911_();
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107433_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleSkulkBoom(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}
