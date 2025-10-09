package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.ForgeHooksClient;
import org.joml.Quaternionf;

public class LayerKangarooArmor extends RenderLayer<EntityKangaroo, ModelKangaroo> {
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
   private final HumanoidModel defaultBipedModel;
   private final RenderKangaroo renderer;

   public LayerKangarooArmor(RenderKangaroo render, Context context) {
      super(render);
      this.defaultBipedModel = new HumanoidModel(context.m_174023_(ModelLayers.f_171261_));
      this.renderer = render;
   }

   public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
      ArmorItem item = (ArmorItem)stack.m_41720_();
      String texture = item.m_40401_().m_6082_();
      String domain = "minecraft";
      int idx = texture.indexOf(58);
      if (idx != -1) {
         domain = texture.substring(0, idx);
         texture = texture.substring(idx + 1);
      }

      String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, 1, type == null ? "" : String.format("_%s", type));
      s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
      ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);
      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(s1);
         ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
      }

      return resourcelocation;
   }

   public void render(
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      EntityKangaroo roo,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      matrixStackIn.m_85836_();
      if (roo.isRoger()) {
         ItemStack haloStack = new ItemStack((ItemLike)AMItemRegistry.HALO.get());
         matrixStackIn.m_85836_();
         this.translateToHead(matrixStackIn);
         float f = 0.1F * (float)Math.sin((roo.f_19797_ + partialTicks) * 0.1F) + (roo.m_6162_() ? 0.2F : 0.0F);
         matrixStackIn.m_252880_(0.0F, -0.75F - f, -0.2F);
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         matrixStackIn.m_85841_(1.3F, 1.3F, 1.3F);
         ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
         renderer.m_269530_(roo, haloStack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
         matrixStackIn.m_85849_();
      }

      if (!roo.m_6162_()) {
         matrixStackIn.m_85836_();
         ItemStack itemstack = roo.m_6844_(EquipmentSlot.HEAD);
         if (itemstack.m_41720_() instanceof ArmorItem) {
            ArmorItem armoritem = (ArmorItem)itemstack.m_41720_();
            if (itemstack.canEquip(EquipmentSlot.HEAD, roo)) {
               HumanoidModel a = this.defaultBipedModel;
               a = this.getArmorModelHook(roo, itemstack, EquipmentSlot.HEAD, a);
               boolean notAVanillaModel = a != this.defaultBipedModel;
               this.setModelSlotVisible(a, EquipmentSlot.HEAD);
               this.translateToHead(matrixStackIn);
               matrixStackIn.m_252880_(0.0F, 0.015F, -0.05F);
               if (itemstack.m_41720_() == AMItemRegistry.FEDORA.get()) {
                  matrixStackIn.m_252880_(0.0F, 0.05F, 0.0F);
               }

               matrixStackIn.m_85841_(0.7F, 0.7F, 0.7F);
               boolean flag1 = itemstack.m_41790_();
               if (armoritem instanceof DyeableLeatherItem) {
                  int i = ((DyeableLeatherItem)armoritem).m_41121_(itemstack);
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;
                  this.renderHelmet(
                     roo,
                     matrixStackIn,
                     bufferIn,
                     packedLightIn,
                     flag1,
                     a,
                     f,
                     f1,
                     f2,
                     getArmorResource(roo, itemstack, EquipmentSlot.HEAD, null),
                     notAVanillaModel
                  );
                  this.renderHelmet(
                     roo,
                     matrixStackIn,
                     bufferIn,
                     packedLightIn,
                     flag1,
                     a,
                     1.0F,
                     1.0F,
                     1.0F,
                     getArmorResource(roo, itemstack, EquipmentSlot.HEAD, "overlay"),
                     notAVanillaModel
                  );
               } else {
                  this.renderHelmet(
                     roo,
                     matrixStackIn,
                     bufferIn,
                     packedLightIn,
                     flag1,
                     a,
                     1.0F,
                     1.0F,
                     1.0F,
                     getArmorResource(roo, itemstack, EquipmentSlot.HEAD, null),
                     notAVanillaModel
                  );
               }
            }
         } else {
            this.translateToHead(matrixStackIn);
            matrixStackIn.m_85837_(0.0, -0.2, -0.1F);
            matrixStackIn.m_252781_(new Quaternionf().rotateX((float) Math.PI));
            matrixStackIn.m_252781_(new Quaternionf().rotateY((float) Math.PI));
            matrixStackIn.m_85841_(1.0F, 1.0F, 1.0F);
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(itemstack, ItemDisplayContext.FIXED, packedLightIn, OverlayTexture.f_118083_, matrixStackIn, bufferIn, roo.m_9236_(), 0);
         }

         matrixStackIn.m_85849_();
         matrixStackIn.m_85836_();
         itemstack = roo.m_6844_(EquipmentSlot.CHEST);
         if (itemstack.m_41720_() instanceof ArmorItem) {
            ArmorItem armoritem = (ArmorItem)itemstack.m_41720_();
            if (armoritem.m_40402_() == EquipmentSlot.CHEST) {
               HumanoidModel ax = this.defaultBipedModel;
               ax = this.getArmorModelHook(roo, itemstack, EquipmentSlot.CHEST, ax);
               boolean notAVanillaModelx = ax != this.defaultBipedModel;
               this.setModelSlotVisible(ax, EquipmentSlot.CHEST);
               this.translateToChest(matrixStackIn);
               matrixStackIn.m_252880_(0.0F, 0.25F, 0.0F);
               matrixStackIn.m_85841_(1.0F, 1.0F, 1.0F);
               boolean flag1 = itemstack.m_41790_();
               if (armoritem instanceof DyeableLeatherItem) {
                  int i = ((DyeableLeatherItem)armoritem).m_41121_(itemstack);
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;
                  this.renderChestplate(
                     roo,
                     matrixStackIn,
                     bufferIn,
                     packedLightIn,
                     flag1,
                     ax,
                     f,
                     f1,
                     f2,
                     getArmorResource(roo, itemstack, EquipmentSlot.CHEST, null),
                     notAVanillaModelx
                  );
                  this.renderChestplate(
                     roo,
                     matrixStackIn,
                     bufferIn,
                     packedLightIn,
                     flag1,
                     ax,
                     1.0F,
                     1.0F,
                     1.0F,
                     getArmorResource(roo, itemstack, EquipmentSlot.CHEST, "overlay"),
                     notAVanillaModelx
                  );
               } else {
                  this.renderChestplate(
                     roo,
                     matrixStackIn,
                     bufferIn,
                     packedLightIn,
                     flag1,
                     ax,
                     1.0F,
                     1.0F,
                     1.0F,
                     getArmorResource(roo, itemstack, EquipmentSlot.CHEST, null),
                     notAVanillaModelx
                  );
               }
            }
         }

         matrixStackIn.m_85849_();
      }

      matrixStackIn.m_85849_();
   }

   private void translateToHead(PoseStack matrixStackIn) {
      this.translateToChest(matrixStackIn);
      ((ModelKangaroo)this.renderer.m_7200_()).neck.translateAndRotate(matrixStackIn);
      ((ModelKangaroo)this.renderer.m_7200_()).head.translateAndRotate(matrixStackIn);
   }

   private void translateToChest(PoseStack matrixStackIn) {
      ((ModelKangaroo)this.renderer.m_7200_()).root.translateAndRotate(matrixStackIn);
      ((ModelKangaroo)this.renderer.m_7200_()).body.translateAndRotate(matrixStackIn);
      ((ModelKangaroo)this.renderer.m_7200_()).chest.translateAndRotate(matrixStackIn);
   }

   private void renderChestplate(
      EntityKangaroo entity,
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      boolean glintIn,
      HumanoidModel modelIn,
      float red,
      float green,
      float blue,
      ResourceLocation armorResource,
      boolean notAVanillaModel
   ) {
      VertexConsumer ivertexbuilder = ItemRenderer.m_115211_(bufferIn, RenderType.m_110458_(armorResource), false, glintIn);
      ((ModelKangaroo)this.renderer.m_7200_()).m_102624_(modelIn);
      float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * Minecraft.m_91087_().m_91296_();
      modelIn.f_102810_.f_104203_ = (float) (Math.PI / 2);
      modelIn.f_102810_.f_104204_ = 0.0F;
      modelIn.f_102810_.f_104205_ = 0.0F;
      modelIn.f_102810_.f_104200_ = 0.0F;
      modelIn.f_102810_.f_104201_ = 0.25F;
      modelIn.f_102810_.f_104202_ = -7.6F;
      modelIn.f_102811_.f_104200_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_right.rotationPointX;
      modelIn.f_102811_.f_104201_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_right.rotationPointY;
      modelIn.f_102811_.f_104202_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_right.rotationPointZ;
      modelIn.f_102811_.f_104203_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_right.rotateAngleX;
      modelIn.f_102811_.f_104204_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_right.rotateAngleY;
      modelIn.f_102811_.f_104205_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_right.rotateAngleZ;
      modelIn.f_102812_.f_104200_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_left.rotationPointX;
      modelIn.f_102812_.f_104201_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_left.rotationPointY;
      modelIn.f_102812_.f_104202_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_left.rotationPointZ;
      modelIn.f_102812_.f_104203_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_left.rotateAngleX;
      modelIn.f_102812_.f_104204_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_left.rotateAngleY;
      modelIn.f_102812_.f_104205_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_left.rotateAngleZ;
      modelIn.f_102812_.f_104201_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_left.rotationPointY - 4.0F + sitProgress * 0.25F;
      modelIn.f_102811_.f_104201_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_right.rotationPointY - 4.0F + sitProgress * 0.25F;
      modelIn.f_102812_.f_104202_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_left.rotationPointZ - 0.5F;
      modelIn.f_102811_.f_104202_ = ((ModelKangaroo)this.renderer.m_7200_()).arm_right.rotationPointZ - 0.5F;
      modelIn.f_102810_.f_104207_ = false;
      modelIn.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, red, green, blue, 1.0F);
      modelIn.f_102810_.f_104207_ = true;
      modelIn.f_102811_.f_104207_ = false;
      modelIn.f_102812_.f_104207_ = false;
      matrixStackIn.m_85836_();
      matrixStackIn.m_85841_(1.1F, 1.65F, 1.1F);
      modelIn.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, red, green, blue, 1.0F);
      matrixStackIn.m_85849_();
      modelIn.f_102811_.f_104207_ = true;
      modelIn.f_102812_.f_104207_ = true;
   }

   private void renderHelmet(
      EntityKangaroo entity,
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int packedLightIn,
      boolean glintIn,
      HumanoidModel modelIn,
      float red,
      float green,
      float blue,
      ResourceLocation armorResource,
      boolean notAVanillaModel
   ) {
      VertexConsumer ivertexbuilder = ItemRenderer.m_115211_(bufferIn, RenderType.m_110458_(armorResource), false, glintIn);
      ((ModelKangaroo)this.renderer.m_7200_()).m_102624_(modelIn);
      modelIn.f_102808_.f_104203_ = 0.0F;
      modelIn.f_102808_.f_104204_ = 0.0F;
      modelIn.f_102808_.f_104205_ = 0.0F;
      modelIn.f_102809_.f_104203_ = 0.0F;
      modelIn.f_102809_.f_104204_ = 0.0F;
      modelIn.f_102809_.f_104205_ = 0.0F;
      modelIn.f_102808_.f_104200_ = 0.0F;
      modelIn.f_102808_.f_104201_ = 0.0F;
      modelIn.f_102808_.f_104202_ = 0.0F;
      modelIn.f_102809_.f_104200_ = 0.0F;
      modelIn.f_102809_.f_104201_ = 0.0F;
      modelIn.f_102809_.f_104202_ = 0.0F;
      modelIn.m_7695_(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.f_118083_, red, green, blue, 1.0F);
   }

   protected void setModelSlotVisible(HumanoidModel p_188359_1_, EquipmentSlot slotIn) {
      this.setModelVisible(p_188359_1_);
      switch (slotIn) {
         case HEAD:
            p_188359_1_.f_102808_.f_104207_ = true;
            p_188359_1_.f_102809_.f_104207_ = true;
            break;
         case CHEST:
            p_188359_1_.f_102810_.f_104207_ = true;
            p_188359_1_.f_102811_.f_104207_ = true;
            p_188359_1_.f_102812_.f_104207_ = true;
            break;
         case LEGS:
            p_188359_1_.f_102810_.f_104207_ = true;
            p_188359_1_.f_102813_.f_104207_ = true;
            p_188359_1_.f_102814_.f_104207_ = true;
            break;
         case FEET:
            p_188359_1_.f_102813_.f_104207_ = true;
            p_188359_1_.f_102814_.f_104207_ = true;
      }
   }

   protected void setModelVisible(HumanoidModel model) {
      model.m_8009_(false);
   }

   protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
      Model basicModel = ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
      return basicModel instanceof HumanoidModel ? (HumanoidModel)basicModel : model;
   }
}
