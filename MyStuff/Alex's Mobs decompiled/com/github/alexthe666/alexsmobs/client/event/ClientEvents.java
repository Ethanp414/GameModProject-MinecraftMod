package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.model.ModelRockyChestplateRolling;
import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.LavaVisionFluidRenderer;
import com.github.alexthe666.alexsmobs.client.render.RenderVineLasso;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectPowerDown;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.IFalconry;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.github.alexthe666.alexsmobs.message.MessageUpdateEagleControls;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.client.event.EventGetFluidRenderType;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.citadel.client.event.EventGetStarBrightness;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.client.event.ViewportEvent.ComputeFogColor;
import net.minecraftforge.client.event.ViewportEvent.RenderFog;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {
   private static final ResourceLocation ROCKY_CHESTPLATE_TEXTURE = new ResourceLocation("alexsmobs:textures/armor/rocky_chestplate.png");
   private static final ModelRockyChestplateRolling ROCKY_CHESTPLATE_MODEL = new ModelRockyChestplateRolling();
   private boolean previousLavaVision = false;
   private LiquidBlockRenderer previousFluidRenderer;
   public long lastStaticTick = -1L;
   public static int renderStaticScreenFor = 0;

   @SubscribeEvent
   public void onOutlineEntityColor(EventGetOutlineColor event) {
      if (event.getEntityIn() instanceof Enemy
         && AlexsMobs.PROXY.getSingingBlueJayId() != -1
         && event.getEntityIn().m_9236_().m_6815_(AlexsMobs.PROXY.getSingingBlueJayId()) instanceof EntityBlueJay jay
         && jay.m_6084_()
         && jay.isMakingMonstersBlue()) {
         event.setColor(4953598);
         event.setResult(Result.ALLOW);
      }

      if (event.getEntityIn() instanceof ItemEntity && ((ItemEntity)event.getEntityIn()).m_32055_().m_204117_(AMTagRegistry.VOID_WORM_DROPS)) {
         int fromColor = 0;
         int toColor = 2221567;
         float startR = (fromColor >> 16 & 0xFF) / 255.0F;
         float startG = (fromColor >> 8 & 0xFF) / 255.0F;
         float startB = (fromColor & 0xFF) / 255.0F;
         float endR = (toColor >> 16 & 0xFF) / 255.0F;
         float endG = (toColor >> 8 & 0xFF) / 255.0F;
         float endB = (toColor & 0xFF) / 255.0F;
         float f = (float)(Math.cos(0.4F * (event.getEntityIn().f_19797_ + Minecraft.m_91087_().m_91296_())) + 1.0) * 0.5F;
         float r = (endR - startR) * f + startR;
         float g = (endG - startG) * f + startG;
         float b = (endB - startB) * f + startB;
         int j = ((int)(r * 255.0F) & 0xFF) << 16 | ((int)(g * 255.0F) & 0xFF) << 8 | ((int)(b * 255.0F) & 0xFF) << 0;
         event.setColor(j);
         event.setResult(Result.ALLOW);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onGetStarBrightness(EventGetStarBrightness event) {
      if (Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)AMEffectRegistry.POWER_DOWN.get())
         && Minecraft.m_91087_().f_91074_.m_21124_((MobEffect)AMEffectRegistry.POWER_DOWN.get()) != null) {
         MobEffectInstance instance = Minecraft.m_91087_().f_91074_.m_21124_((MobEffect)AMEffectRegistry.POWER_DOWN.get());
         EffectPowerDown powerDown = (EffectPowerDown)instance.m_19544_();
         int duration = instance.m_19557_();
         float partialTicks = Minecraft.m_91087_().m_91296_();
         float f = (Math.min(powerDown.getActiveTime(), duration) + partialTicks) * 0.1F;
         event.setBrightness(0.0F);
         event.setResult(Result.ALLOW);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onFogColor(ComputeFogColor event) {
      if (Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)AMEffectRegistry.POWER_DOWN.get())
         && Minecraft.m_91087_().f_91074_.m_21124_((MobEffect)AMEffectRegistry.POWER_DOWN.get()) != null) {
         event.setBlue(0.0F);
         event.setRed(0.0F);
         event.setGreen(0.0F);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   @OnlyIn(Dist.CLIENT)
   public void onFogDensity(RenderFog event) {
      FogType fogType = event.getCamera().m_167685_();
      if (Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)AMEffectRegistry.LAVA_VISION.get()) && fogType == FogType.LAVA) {
         event.setNearPlaneDistance(-8.0F);
         event.setFarPlaneDistance(50.0F);
         event.setCanceled(true);
      }

      if (Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)AMEffectRegistry.POWER_DOWN.get())
         && fogType == FogType.NONE
         && Minecraft.m_91087_().f_91074_.m_21124_((MobEffect)AMEffectRegistry.POWER_DOWN.get()) != null) {
         float initEnd = event.getFarPlaneDistance();
         MobEffectInstance instance = Minecraft.m_91087_().f_91074_.m_21124_((MobEffect)AMEffectRegistry.POWER_DOWN.get());
         EffectPowerDown powerDown = (EffectPowerDown)instance.m_19544_();
         int duration = instance.m_19557_();
         float partialTicks = Minecraft.m_91087_().m_91296_();
         float f = Math.min(20.0F, Math.min(powerDown.getActiveTime() + partialTicks, duration + partialTicks)) * 0.05F;
         event.setNearPlaneDistance(-8.0F);
         float f1 = 8.0F + (1.0F - f) * Math.max(0.0F, initEnd - 8.0F);
         event.setFarPlaneDistance(f1);
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onPreRenderEntity(Pre event) {
      if (RockyChestplateUtil.isRockyRolling(event.getEntity())) {
         event.setCanceled(true);
         event.getPoseStack().m_85836_();
         float limbSwing = event.getEntity().f_267362_.m_267756_() - event.getEntity().f_267362_.m_267731_() * (1.0F - event.getPartialTick());
         float limbSwingAmount = event.getEntity().f_267362_.m_267711_(event.getPackedLight());
         float yRot = event.getEntity().f_20884_ + (event.getEntity().f_20883_ - event.getEntity().f_20884_) * event.getPartialTick();
         float roll = event.getEntity().f_19867_ + (event.getEntity().f_19787_ - event.getEntity().f_19867_) * event.getPartialTick();
         VertexConsumer vertexconsumer = ItemRenderer.m_115184_(
            event.getMultiBufferSource(), RenderType.m_110431_(ROCKY_CHESTPLATE_TEXTURE), false, event.getEntity().m_6844_(EquipmentSlot.CHEST).m_41790_()
         );
         event.getPoseStack().m_85837_(0.0, event.getEntity().m_20206_() - event.getEntity().m_20206_() * 0.5F, 0.0);
         event.getPoseStack().m_252781_(Axis.f_252392_.m_252977_(180.0F + yRot));
         event.getPoseStack().m_252781_(Axis.f_252403_.m_252977_(180.0F));
         event.getPoseStack().m_252781_(Axis.f_252529_.m_252977_(100.0F * roll));
         ROCKY_CHESTPLATE_MODEL.setupAnim(event.getEntity(), limbSwing, limbSwingAmount, event.getEntity().f_19797_ + event.getPartialTick(), 0.0F, 0.0F);
         ROCKY_CHESTPLATE_MODEL.m_7695_(event.getPoseStack(), vertexconsumer, event.getPackedLight(), OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
         event.getPoseStack().m_85849_();
         MinecraftForge.EVENT_BUS
            .post(
               new Post(
                  event.getEntity(), event.getRenderer(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()
               )
            );
      } else {
         if (event.getEntity() instanceof WanderingTrader
            && event.getEntity().m_6095_() == EntityType.f_20494_
            && event.getEntity().m_20202_() instanceof EntityElephant
            && !(event.getRenderer().f_115290_ instanceof ModelWanderingVillagerRider)) {
            event.getRenderer().f_115290_ = new ModelWanderingVillagerRider(
               Minecraft.m_91087_().m_167973_().m_171103_(AMModelLayers.SITTING_WANDERING_VILLAGER)
            );
         }

         if (event.getEntity().m_21023_((MobEffect)AMEffectRegistry.CLINGING.get()) && event.getEntity().m_20192_() < event.getEntity().m_20206_() * 0.45F
            || event.getEntity().m_21023_((MobEffect)AMEffectRegistry.DEBILITATING_STING.get())
               && event.getEntity().m_6336_() == MobType.f_21642_
               && event.getEntity().m_20205_() > event.getEntity().m_20206_()) {
            event.getPoseStack().m_85836_();
            event.getPoseStack().m_85837_(0.0, event.getEntity().m_20206_() + 0.1F, 0.0);
            event.getPoseStack().m_252781_(Axis.f_252403_.m_252977_(180.0F));
            event.getEntity().f_20884_ = -event.getEntity().f_20884_;
            event.getEntity().f_20883_ = -event.getEntity().f_20883_;
            event.getEntity().f_20886_ = -event.getEntity().f_20886_;
            event.getEntity().f_20885_ = -event.getEntity().f_20885_;
         }

         if (event.getEntity().m_21023_((MobEffect)AMEffectRegistry.ENDER_FLU.get())) {
            event.getPoseStack().m_85836_();
            event.getPoseStack().m_252781_(Axis.f_252436_.m_252977_((float)(Math.cos(event.getEntity().f_19797_ * 7.0) * Math.PI * 1.2F)));
            float vibrate = 0.05F;
            event.getPoseStack()
               .m_252880_(
                  (event.getEntity().m_217043_().m_188501_() - 0.5F) * vibrate,
                  (event.getEntity().m_217043_().m_188501_() - 0.5F) * vibrate,
                  (event.getEntity().m_217043_().m_188501_() - 0.5F) * vibrate
               );
         }
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onPostRenderEntity(Post event) {
      if (!RockyChestplateUtil.isRockyRolling(event.getEntity())) {
         if (event.getEntity().m_21023_((MobEffect)AMEffectRegistry.ENDER_FLU.get())) {
            event.getPoseStack().m_85849_();
         }

         if (event.getEntity().m_21023_((MobEffect)AMEffectRegistry.CLINGING.get()) && event.getEntity().m_20192_() < event.getEntity().m_20206_() * 0.45F
            || event.getEntity().m_21023_((MobEffect)AMEffectRegistry.DEBILITATING_STING.get())
               && event.getEntity().m_6336_() == MobType.f_21642_
               && event.getEntity().m_20205_() > event.getEntity().m_20206_()) {
            event.getPoseStack().m_85849_();
            event.getEntity().f_20884_ = -event.getEntity().f_20884_;
            event.getEntity().f_20883_ = -event.getEntity().f_20883_;
            event.getEntity().f_20886_ = -event.getEntity().f_20886_;
            event.getEntity().f_20885_ = -event.getEntity().f_20885_;
         }

         if (VineLassoUtil.hasLassoData(event.getEntity()) && !(event.getEntity() instanceof Player)) {
            Entity lassoedOwner = VineLassoUtil.getLassoedTo(event.getEntity());
            if (lassoedOwner instanceof LivingEntity && lassoedOwner != event.getEntity()) {
               double d0 = Mth.m_14139_(event.getPartialTick(), event.getEntity().f_19790_, event.getEntity().m_20185_());
               double d1 = Mth.m_14139_(event.getPartialTick(), event.getEntity().f_19791_, event.getEntity().m_20186_());
               double d2 = Mth.m_14139_(event.getPartialTick(), event.getEntity().f_19792_, event.getEntity().m_20189_());
               event.getPoseStack().m_85836_();
               event.getPoseStack().m_85837_(-d0, -d1, -d2);
               RenderVineLasso.renderVine(
                  event.getEntity(),
                  event.getPartialTick(),
                  event.getPoseStack(),
                  event.getMultiBufferSource(),
                  (LivingEntity)lassoedOwner,
                  ((LivingEntity)lassoedOwner).m_5737_() == HumanoidArm.LEFT,
                  0.1F
               );
               event.getPoseStack().m_85849_();
            }
         }
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onPoseHand(EventPosePlayerHand event) {
      LivingEntity player = (LivingEntity)event.getEntityIn();
      float f = Minecraft.m_91087_().m_91296_();
      boolean leftHand = false;
      boolean usingLasso = player.m_6117_() && player.m_21211_().m_150930_((Item)AMItemRegistry.VINE_LASSO.get());
      if (player.m_21120_(InteractionHand.MAIN_HAND).m_41720_() == AMItemRegistry.VINE_LASSO.get()) {
         leftHand = player.m_5737_() == HumanoidArm.LEFT;
      } else if (player.m_21120_(InteractionHand.OFF_HAND).m_41720_() == AMItemRegistry.VINE_LASSO.get()) {
         leftHand = player.m_5737_() != HumanoidArm.LEFT;
      }

      if (leftHand && event.isLeftHand() && usingLasso) {
         event.setResult(Result.ALLOW);
         event.getModel().f_102812_.f_104203_ = Maths.rad(-120.0) + Mth.m_14031_(player.f_19797_ + f) * 0.5F;
         event.getModel().f_102812_.f_104204_ = Maths.rad(-20.0) + Mth.m_14089_(player.f_19797_ + f) * 0.5F;
      }

      if (!leftHand && !event.isLeftHand() && usingLasso) {
         event.setResult(Result.ALLOW);
         event.getModel().f_102811_.f_104203_ = Maths.rad(-120.0) + Mth.m_14031_(player.f_19797_ + f) * 0.5F;
         event.getModel().f_102811_.f_104204_ = Maths.rad(20.0) - Mth.m_14089_(player.f_19797_ + f) * 0.5F;
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRenderHand(RenderHandEvent event) {
      if (Minecraft.m_91087_().m_91288_() instanceof IFalconry) {
         event.setCanceled(true);
      }

      if (!Minecraft.m_91087_().f_91074_.m_20197_().isEmpty() && event.getHand() == InteractionHand.MAIN_HAND) {
         Player player = Minecraft.m_91087_().f_91074_;
         boolean leftHand = false;
         if (player.m_21120_(InteractionHand.MAIN_HAND).m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()) {
            leftHand = player.m_5737_() == HumanoidArm.LEFT;
         } else if (player.m_21120_(InteractionHand.OFF_HAND).m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()) {
            leftHand = player.m_5737_() != HumanoidArm.LEFT;
         }

         for (Entity entity : player.m_20197_()) {
            if (entity instanceof IFalconry falconry) {
               float yaw = player.f_20884_ + (player.f_20883_ - player.f_20884_) * event.getPartialTick();
               ClientProxy.currentUnrenderedEntities.remove(entity.m_20148_());
               PoseStack matrixStackIn = event.getPoseStack();
               matrixStackIn.m_85836_();
               matrixStackIn.m_85841_(0.5F, 0.5F, 0.5F);
               matrixStackIn.m_252880_(leftHand ? -falconry.getHandOffset() : falconry.getHandOffset(), -0.6F, -1.0F);
               matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(yaw));
               if (leftHand) {
                  matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(90.0F));
               } else {
                  matrixStackIn.m_252781_(Axis.f_252392_.m_252977_(90.0F));
               }

               this.renderEntity(entity, 0.0, 0.0, 0.0, 0.0F, event.getPartialTick(), matrixStackIn, event.getMultiBufferSource(), event.getPackedLight());
               matrixStackIn.m_85849_();
               ClientProxy.currentUnrenderedEntities.add(entity.m_20148_());
            }
         }
      }

      if (Minecraft.m_91087_().f_91074_.m_21211_().m_41720_() instanceof ItemDimensionalCarver
         && event.getItemStack().m_41720_() instanceof ItemDimensionalCarver) {
         PoseStack matrixStackIn = event.getPoseStack();
         matrixStackIn.m_85836_();
         ItemInHandRenderer renderer = Minecraft.m_91087_().m_91290_().m_234586_();
         InteractionHand hand = (InteractionHand)MoreObjects.firstNonNull(Minecraft.m_91087_().f_91074_.f_20912_, InteractionHand.MAIN_HAND);
         float f = Minecraft.m_91087_().f_91074_.m_21324_(event.getPartialTick());
         float f5 = -0.4F * Mth.m_14031_(Mth.m_14116_(f) * (float) Math.PI);
         float f6 = 0.2F * Mth.m_14031_(Mth.m_14116_(f) * (float) (Math.PI * 2));
         float f10 = -0.2F * Mth.m_14031_(f * (float) Math.PI);
         HumanoidArm handside = hand == InteractionHand.MAIN_HAND
            ? Minecraft.m_91087_().f_91074_.m_5737_()
            : Minecraft.m_91087_().f_91074_.m_5737_().m_20828_();
         boolean flag3 = handside == HumanoidArm.RIGHT;
         int l = flag3 ? 1 : -1;
         matrixStackIn.m_252880_(l * f5, f6, f10);
      }
   }

   public <E extends Entity> void renderEntity(
      E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight
   ) {
      EntityRenderer<? super E> render = null;
      EntityRenderDispatcher manager = Minecraft.m_91087_().m_91290_();

      try {
         render = manager.m_114382_(entityIn);
         if (render != null) {
            try {
               render.m_7392_(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
            } catch (Throwable var19) {
               throw new ReportedException(CrashReport.m_127521_(var19, "Rendering entity in world"));
            }
         }
      } catch (Throwable var20) {
         CrashReport crashreport = CrashReport.m_127521_(var20, "Rendering entity in world");
         CrashReportCategory crashreportcategory = crashreport.m_127514_("Entity being rendered");
         entityIn.m_7976_(crashreportcategory);
         CrashReportCategory crashreportcategory1 = crashreport.m_127514_("Renderer details");
         crashreportcategory1.m_128159_("Assigned renderer", render);
         crashreportcategory1.m_128159_("Rotation", yaw);
         crashreportcategory1.m_128159_("Delta", partialTicks);
         throw new ReportedException(crashreport);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRenderNameplate(RenderNameTagEvent event) {
      if (Minecraft.m_91087_().m_91288_() instanceof EntityBaldEagle && event.getEntity() == Minecraft.m_91087_().f_91074_ && Minecraft.m_91087_().m_91091_()) {
         event.setResult(Result.DENY);
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onRenderWorldLastEvent(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_SKY) {
         if (!AMConfig.shadersCompat) {
            if (Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)AMEffectRegistry.LAVA_VISION.get())) {
               if (!this.previousLavaVision) {
                  this.previousFluidRenderer = Minecraft.m_91087_().m_91289_().f_110901_;
                  Minecraft.m_91087_().m_91289_().f_110901_ = new LavaVisionFluidRenderer();
                  this.updateAllChunks();
               }
            } else if (this.previousLavaVision) {
               if (this.previousFluidRenderer != null) {
                  Minecraft.m_91087_().m_91289_().f_110901_ = this.previousFluidRenderer;
               }

               this.updateAllChunks();
            }

            this.previousLavaVision = Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)AMEffectRegistry.LAVA_VISION.get());
            if (AMConfig.clingingFlipEffect) {
               if (Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)AMEffectRegistry.CLINGING.get())
                  && Minecraft.m_91087_().f_91074_.m_20192_() < Minecraft.m_91087_().f_91074_.m_20206_() * 0.45F) {
                  Minecraft.m_91087_().f_91063_.m_109128_(new ResourceLocation("shaders/post/flip.json"));
               } else if (Minecraft.m_91087_().f_91063_.m_109149_() != null
                  && Minecraft.m_91087_().f_91063_.m_109149_().m_110022_().equals("minecraft:shaders/post/flip.json")) {
                  Minecraft.m_91087_().f_91063_.m_109086_();
               }
            }
         }

         if (Minecraft.m_91087_().m_91288_() instanceof EntityBaldEagle) {
            EntityBaldEagle eagle = (EntityBaldEagle)Minecraft.m_91087_().m_91288_();
            LocalPlayer playerEntity = Minecraft.m_91087_().f_91074_;
            if (!((EntityBaldEagle)Minecraft.m_91087_().m_91288_()).shouldHoodedReturn() && !eagle.m_213877_()) {
               float rotX = Mth.m_14177_(playerEntity.m_146908_() + playerEntity.f_20885_);
               float rotY = playerEntity.m_146909_();
               Entity over = null;
               if (Minecraft.m_91087_().f_91077_ instanceof EntityHitResult) {
                  over = ((EntityHitResult)Minecraft.m_91087_().f_91077_).m_82443_();
               } else {
                  Minecraft.m_91087_().f_91077_ = null;
               }

               boolean loadChunks = playerEntity.m_9236_().m_46468_() % 10L == 0L;
               ((EntityBaldEagle)Minecraft.m_91087_().m_91288_()).directFromPlayer(rotX, rotY, false, over);
               AlexsMobs.NETWORK_WRAPPER
                  .sendToServer(
                     new MessageUpdateEagleControls(Minecraft.m_91087_().m_91288_().m_19879_(), rotX, rotY, loadChunks, over == null ? -1 : over.m_19879_())
                  );
            } else {
               Minecraft.m_91087_().m_91118_(playerEntity);
               Minecraft.m_91087_().f_91066_.m_92157_(CameraType.values()[AlexsMobs.PROXY.getPreviousPOV()]);
            }
         }
      }
   }

   private void updateAllChunks() {
      if (Minecraft.m_91087_().f_91060_.f_109469_ != null) {
         int length = Minecraft.m_91087_().f_91060_.f_109469_.f_110843_.length;

         for (int i = 0; i < length; i++) {
            Minecraft.m_91087_().f_91060_.f_109469_.f_110843_[i].f_112792_ = true;
         }
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public void onGetFluidRenderType(EventGetFluidRenderType event) {
      if (Minecraft.m_91087_().f_91074_.m_21023_((MobEffect)AMEffectRegistry.LAVA_VISION.get())
         && (event.getFluidState().m_192917_(Fluids.f_76195_) || event.getFluidState().m_192917_(Fluids.f_76194_))) {
         event.setRenderType(RenderType.m_110466_());
         event.setResult(Result.ALLOW);
      }
   }

   @SubscribeEvent
   public void clientTick(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         AMItemstackRenderer.incrementTick();
      }
   }

   @SubscribeEvent
   public void onCameraSetup(ComputeCameraAngles event) {
      if (Minecraft.m_91087_().f_91074_.m_21124_((MobEffect)AMEffectRegistry.EARTHQUAKE.get()) != null && !Minecraft.m_91087_().m_91104_()) {
         int duration = Minecraft.m_91087_().f_91074_.m_21124_((MobEffect)AMEffectRegistry.EARTHQUAKE.get()).m_19557_();
         float f = (Math.min(10, duration) + Minecraft.m_91087_().m_91296_()) * 0.1F;
         double intensity = f * (Double)Minecraft.m_91087_().f_91066_.m_231924_().m_231551_();
         RandomSource rng = Minecraft.m_91087_().f_91074_.m_217043_();
         event.getCamera().m_90568_(rng.m_188501_() * 0.1F * intensity, rng.m_188501_() * 0.2F * intensity, rng.m_188501_() * 0.4F * intensity);
      }
   }

   @SubscribeEvent
   public void onPostGameOverlay(net.minecraftforge.client.event.RenderGuiOverlayEvent.Post event) {
      if (renderStaticScreenFor > 0) {
         if (Minecraft.m_91087_().f_91074_.m_6084_() && this.lastStaticTick != Minecraft.m_91087_().f_91073_.m_46467_()) {
            renderStaticScreenFor--;
         }

         float staticLevel = renderStaticScreenFor / 60.0F;
         if (event.getOverlay().id().equals(VanillaGuiOverlay.HELMET.id())) {
            float screenWidth = event.getWindow().m_85443_();
            float screenHeight = event.getWindow().m_85444_();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            float ageInTicks = (float)Minecraft.m_91087_().f_91073_.m_46467_() + event.getPartialTick();
            float staticIndexX = (float)Math.sin(ageInTicks * 0.2F) * 2.0F;
            float staticIndexY = (float)Math.cos(ageInTicks * 0.2F + 3.0F) * 2.0F;
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::m_172817_);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, staticLevel);
            RenderSystem.setShaderTexture(0, AMRenderTypes.STATIC_TEXTURE);
            Tesselator tesselator = Tesselator.m_85913_();
            BufferBuilder bufferbuilder = tesselator.m_85915_();
            bufferbuilder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85817_);
            float minU = 10.0F * staticIndexX * 0.125F;
            float maxU = 10.0F * (0.5F + staticIndexX * 0.125F);
            float minV = 10.0F * staticIndexY * 0.125F;
            float maxV = 10.0F * (0.125F + staticIndexY * 0.125F);
            bufferbuilder.m_5483_(0.0, screenHeight, -190.0).m_7421_(minU, maxV).m_5752_();
            bufferbuilder.m_5483_(screenWidth, screenHeight, -190.0).m_7421_(maxU, maxV).m_5752_();
            bufferbuilder.m_5483_(screenWidth, 0.0, -190.0).m_7421_(maxU, minV).m_5752_();
            bufferbuilder.m_5483_(0.0, 0.0, -190.0).m_7421_(minU, minV).m_5752_();
            tesselator.m_85914_();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }

         this.lastStaticTick = Minecraft.m_91087_().f_91073_.m_46467_();
      }
   }
}
