package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelUnderminerDwarf;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerUnderminerItem;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;

public class RenderUnderminer extends MobRenderer<EntityUnderminer, EntityModel<EntityUnderminer>> {
   private static final ResourceLocation TEXTURE_DWARF = new ResourceLocation("alexsmobs:textures/entity/underminer_dwarf.png");
   private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/underminer_0.png");
   private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/underminer_1.png");
   public static final List<ResourceLocation> BREAKING_LOCATIONS = IntStream.range(0, 10)
      .mapToObj(destroyStage -> new ResourceLocation("alexsmobs:textures/block/ghostly_pickaxe/destroy_stage_" + destroyStage + ".png"))
      .collect(Collectors.toList());
   private static final ModelUnderminerDwarf DWARF_MODEL = new ModelUnderminerDwarf();
   private static HumanoidModel<EntityUnderminer> NORMAL_MODEL = null;
   private static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(AMRenderTypes::getGhostCrumbling).collect(Collectors.toList());
   public static boolean renderWithPickaxe = false;

   public RenderUnderminer(Context renderManagerIn) {
      super(renderManagerIn, DWARF_MODEL, 0.4F);
      NORMAL_MODEL = new HumanoidModel(Minecraft.m_91087_().m_167973_().m_171103_(AMModelLayers.UNDERMINER));
      this.m_115326_(new LayerUnderminerItem(this));
   }

   protected void scale(EntityUnderminer entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      matrixStackIn.m_85841_(0.925F, 0.925F, 0.925F);
   }

   public boolean shouldRender(EntityUnderminer livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
      if (super.m_5523_(livingEntityIn, camera, camX, camY, camZ)) {
         return true;
      } else {
         if (livingEntityIn.getMiningPos() != null) {
            BlockPos pos = livingEntityIn.getMiningPos();
            if (pos != null) {
               Vec3 vector3d = Vec3.m_82528_(pos);
               Vec3 vector3dCorner = Vec3.m_82528_(pos).m_82520_(1.0, 1.0, 1.0);
               return camera.m_113029_(
                  new AABB(vector3d.f_82479_, vector3d.f_82480_, vector3d.f_82481_, vector3dCorner.f_82479_, vector3dCorner.f_82480_, vector3dCorner.f_82481_)
               );
            }
         }

         return false;
      }
   }

   protected float getFlipDegrees(EntityUnderminer entityUnderminer) {
      return 0.0F;
   }

   public void render(EntityUnderminer entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      if (!MinecraftForge.EVENT_BUS.post(new Pre(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn))) {
         matrixStackIn.m_85836_();
         this.f_115290_.f_102608_ = this.m_115342_(entityIn, partialTicks);
         boolean shouldSit = entityIn.m_20159_() && entityIn.m_20202_() != null && entityIn.m_20202_().shouldRiderSit();
         this.f_115290_.f_102609_ = shouldSit;
         this.f_115290_.f_102610_ = entityIn.m_6162_();
         float f = Mth.m_14189_(partialTicks, entityIn.f_20884_, entityIn.f_20883_);
         float f1 = Mth.m_14189_(partialTicks, entityIn.f_20886_, entityIn.f_20885_);
         float f2 = f1 - f;
         if (shouldSit && entityIn.m_20202_() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entityIn.m_20202_();
            f = Mth.m_14189_(partialTicks, livingentity.f_20884_, livingentity.f_20883_);
            f2 = f1 - f;
            float f3 = Mth.m_14177_(f2);
            if (f3 < -85.0F) {
               f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
               f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
               f += f3 * 0.2F;
            }

            f2 = f1 - f;
         }

         float f6 = Mth.m_14179_(partialTicks, entityIn.f_19860_, entityIn.m_146909_());
         if (entityIn.m_20089_() == Pose.SLEEPING) {
            Direction direction = entityIn.m_21259_();
            if (direction != null) {
               float f4 = entityIn.m_20236_(Pose.STANDING) - 0.1F;
               matrixStackIn.m_85837_(-direction.m_122429_() * f4, 0.0, -direction.m_122431_() * f4);
            }
         }

         float f7 = this.m_6930_(entityIn, partialTicks);
         this.m_7523_(entityIn, matrixStackIn, f7, f, partialTicks);
         matrixStackIn.m_85841_(-1.0F, -1.0F, 1.0F);
         this.scale(entityIn, matrixStackIn, partialTicks);
         matrixStackIn.m_85837_(0.0, -1.501F, 0.0);
         float f8 = 0.0F;
         float f5 = 0.0F;
         if (!shouldSit && entityIn.m_6084_()) {
            f8 = entityIn.f_267362_.m_267711_(partialTicks);
            f5 = entityIn.f_267362_.m_267590_(partialTicks);
            if (entityIn.m_6162_()) {
               f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
               f8 = 1.0F;
            }
         }

         if (entityIn.isDwarf()) {
            this.f_115290_ = DWARF_MODEL;
         } else {
            this.f_115290_ = NORMAL_MODEL;
         }

         this.f_115290_.m_6839_(entityIn, f5, f8, partialTicks);
         this.f_115290_.m_6973_(entityIn, f5, f8, f7, f2, f6);
         Minecraft minecraft = Minecraft.m_91087_();
         boolean flag = this.m_5933_(entityIn);
         boolean flag1 = !flag && !entityIn.m_20177_(minecraft.f_91074_);
         boolean flag2 = minecraft.m_91314_(entityIn);
         RenderType rendertype = this.getRenderType(entityIn, flag, flag1, flag2);
         if (rendertype != null && !entityIn.isFullyHidden()) {
            float hide = (entityIn.prevHidingProgress + (entityIn.hidingProgress - entityIn.prevHidingProgress) * partialTicks) * 0.1F;
            float alpha = (1.0F - hide) * 0.6F;
            this.f_114477_ = 0.9F * alpha;
            int i = m_115338_(entityIn, this.m_6931_(entityIn, partialTicks));
            this.renderUnderminerModel(
               matrixStackIn, bufferIn, rendertype, partialTicks, packedLightIn, i, flag1 ? 0.15F : Mth.m_14036_(alpha, 0.0F, 1.0F), entityIn
            );
         } else {
            this.f_114477_ = 0.0F;
         }

         if (!entityIn.m_5833_()) {
            for (RenderLayer layerrenderer : this.f_115291_) {
               layerrenderer.m_6494_(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
            }
         }

         matrixStackIn.m_85849_();
         RenderNameTagEvent renderNameplateEvent = new RenderNameTagEvent(
            entityIn, entityIn.m_5446_(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks
         );
         MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
         if (renderNameplateEvent.getResult() != Result.DENY && (renderNameplateEvent.getResult() == Result.ALLOW || this.m_6512_(entityIn))) {
            this.m_7649_(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
         }

         MinecraftForge.EVENT_BUS.post(new Post(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
         BlockPos miningPos = entityIn.getMiningPos();
         if (miningPos != null) {
            matrixStackIn.m_85836_();
            double d0 = Mth.m_14139_(partialTicks, entityIn.f_19854_, entityIn.m_20185_());
            double d1 = Mth.m_14139_(partialTicks, entityIn.f_19855_, entityIn.m_20186_());
            double d2 = Mth.m_14139_(partialTicks, entityIn.f_19856_, entityIn.m_20189_());
            matrixStackIn.m_85837_(miningPos.m_123341_() - d0, miningPos.m_123342_() - d1, miningPos.m_123343_() - d2);
            int progress = Math.round((DESTROY_TYPES.size() - 1) * Mth.m_14036_(entityIn.getMiningProgress(), 0.0F, 1.0F));
            com.mojang.blaze3d.vertex.PoseStack.Pose posestack$pose = matrixStackIn.m_85850_();
            VertexConsumer vertexconsumer1 = new SheetedDecalTextureGenerator(
               bufferIn.m_6299_(DESTROY_TYPES.get(progress)), posestack$pose.m_252922_(), posestack$pose.m_252943_(), 1.0F
            );
            ModelData modelData = entityIn.m_9236_().getModelDataManager().getAt(miningPos);
            Minecraft.m_91087_()
               .m_91289_()
               .renderBreakingTexture(
                  entityIn.m_9236_().m_8055_(miningPos),
                  miningPos,
                  entityIn.m_9236_(),
                  matrixStackIn,
                  vertexconsumer1,
                  modelData == null ? ModelData.EMPTY : modelData
               );
            matrixStackIn.m_85849_();
         }
      }
   }

   private void renderUnderminerModel(
      PoseStack matrixStackIn,
      MultiBufferSource source,
      RenderType defRenderType,
      float partialTicks,
      int packedLightIn,
      int overlayColors,
      float alphaIn,
      EntityUnderminer entityIn
   ) {
      boolean hurt = Math.max(entityIn.f_20916_, entityIn.f_20919_) > 0;
      this.f_115290_
         .m_7695_(
            matrixStackIn,
            source.m_6299_(defRenderType),
            packedLightIn,
            LivingEntityRenderer.m_115338_(entityIn, 0.0F),
            hurt ? 0.4F : 1.0F,
            hurt ? 0.8F : 1.0F,
            hurt ? 0.7F : 1.0F,
            alphaIn
         );
   }

   @Nullable
   protected RenderType getRenderType(EntityUnderminer farseer, boolean normal, boolean invis, boolean outline) {
      ResourceLocation resourcelocation = this.getTextureLocation(farseer);
      return outline ? RenderType.m_110491_(resourcelocation) : AMRenderTypes.getUnderminer(resourcelocation);
   }

   public ResourceLocation getTextureLocation(EntityUnderminer entity) {
      return entity.isDwarf() ? TEXTURE_DWARF : (entity.getVariant() == 0 ? TEXTURE_0 : TEXTURE_1);
   }
}
