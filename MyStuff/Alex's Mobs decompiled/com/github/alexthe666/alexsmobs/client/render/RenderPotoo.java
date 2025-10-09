package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelPotoo;
import com.github.alexthe666.alexsmobs.entity.EntityPotoo;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class RenderPotoo extends MobRenderer<EntityPotoo, ModelPotoo> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/potoo.png");

   public RenderPotoo(Context renderManagerIn) {
      super(renderManagerIn, new ModelPotoo(), 0.35F);
   }

   public boolean shouldRender(EntityPotoo bird, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      return bird.m_20159_()
            && bird.m_20202_() instanceof Player
            && Minecraft.m_91087_().f_91074_ == bird.m_20202_()
            && Minecraft.m_91087_().f_91066_.m_92176_() == CameraType.FIRST_PERSON
         ? false
         : super.m_5523_(bird, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_);
   }

   protected void scale(EntityPotoo eagle, PoseStack matrixStackIn, float partialTickTime) {
      if (eagle.m_20159_() && eagle.m_20202_() != null && eagle.m_20202_() instanceof Player) {
         Player mount = (Player)eagle.m_20202_();
         boolean leftHand = false;
         if (mount.m_21120_(InteractionHand.MAIN_HAND).m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()) {
            leftHand = mount.m_5737_() == HumanoidArm.LEFT;
         } else if (mount.m_21120_(InteractionHand.OFF_HAND).m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()) {
            leftHand = mount.m_5737_() != HumanoidArm.LEFT;
         }

         EntityRenderer playerRender = Minecraft.m_91087_().m_91290_().m_114382_(mount);
         if ((Minecraft.m_91087_().f_91074_ != mount || Minecraft.m_91087_().f_91066_.m_92176_() != CameraType.FIRST_PERSON)
            && playerRender instanceof LivingEntityRenderer
            && ((LivingEntityRenderer)playerRender).m_7200_() instanceof HumanoidModel) {
            if (leftHand) {
               matrixStackIn.m_252880_(-0.3F, -0.7F, 0.5F);
               ((HumanoidModel)((LivingEntityRenderer)playerRender).m_7200_()).f_102812_.m_104299_(matrixStackIn);
               matrixStackIn.m_252880_(-0.1F, 0.6F, -0.1F);
               matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(55.0F));
               matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(70.0F));
            } else {
               matrixStackIn.m_252880_(0.3F, -0.7F, 0.5F);
               ((HumanoidModel)((LivingEntityRenderer)playerRender).m_7200_()).f_102811_.m_104299_(matrixStackIn);
               matrixStackIn.m_252880_(0.1F, 0.6F, -0.1F);
               matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(55.0F));
               matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(-70.0F));
            }
         }
      }
   }

   public ResourceLocation getTextureLocation(EntityPotoo entity) {
      return TEXTURE;
   }
}
