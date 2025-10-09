package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.MultiTextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TexturingStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class AMRenderTypes extends RenderType {
   public static final ResourceLocation STATIC_TEXTURE = new ResourceLocation("alexsmobs:textures/static.png");
   private static boolean encounteredMultiConsumerError = false;
   protected static final TexturingStateShard RAINBOW_TEXTURING = new TexturingStateShard(
      "entity_glint_texturing", () -> setupRainbowTexturing(1.2F, 4L), () -> RenderSystem.resetTextureMatrix()
   );
   protected static final TexturingStateShard COMB_JELLY_TEXTURING = new TexturingStateShard(
      "entity_glint_texturing", () -> setupRainbowTexturing(2.0F, 16L), () -> RenderSystem.resetTextureMatrix()
   );
   protected static final TexturingStateShard RAINBOW_TEXTURING_LARGE = new TexturingStateShard(
      "entity_glint_texturing", () -> setupRainbowTexturing2(5.0F, 14L), () -> RenderSystem.resetTextureMatrix()
   );
   protected static final TexturingStateShard WEEZER_TEXTURING = new TexturingStateShard(
      "entity_glint_texturing", () -> setupRainbowTexturing2(7.0F, 16L), () -> RenderSystem.resetTextureMatrix()
   );
   protected static final TexturingStateShard STATIC_PORTAL_TEXTURING = new TexturingStateShard(
      "entity_glint_texturing", () -> setupStaticTexturing(1.1F, 12L), () -> RenderSystem.resetTextureMatrix()
   );
   protected static final TexturingStateShard STATIC_PARTICLE_TEXTURING = new TexturingStateShard(
      "entity_glint_texturing", () -> setupStaticTexturing(0.1F, 12L), () -> RenderSystem.resetTextureMatrix()
   );
   protected static final TexturingStateShard STATIC_ENTITY_TEXTURING = new TexturingStateShard(
      "entity_glint_texturing", () -> setupStaticTexturing(3.0F, 12L), () -> RenderSystem.resetTextureMatrix()
   );
   public static final RenderType COMBJELLY_RAINBOW_GLINT = m_173215_(
      "cj_rainbow_glint",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_rainbow.png"), true, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110685_(f_110134_)
         .m_110683_(COMB_JELLY_TEXTURING)
         .m_110691_(false)
   );
   public static final RenderType RAINBOW_GLINT = m_173215_(
      "rainbow_glint",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      true,
      true,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_rainbow.png"), true, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110685_(f_110137_)
         .m_110683_(RAINBOW_TEXTURING)
         .m_110677_(f_110154_)
         .m_110691_(true)
   );
   public static final RenderType TRANS_GLINT = m_173215_(
      "trans_glint",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_trans.png"), true, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110685_(f_110137_)
         .m_110683_(RAINBOW_TEXTURING)
         .m_110677_(f_110154_)
         .m_110691_(true)
   );
   public static final RenderType NONBI_GLINT = m_173215_(
      "nonbi_glint",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_nonbi.png"), true, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110685_(f_110137_)
         .m_110683_(RAINBOW_TEXTURING)
         .m_110677_(f_110154_)
         .m_110691_(true)
   );
   public static final RenderType BI_GLINT = m_173215_(
      "bi_glint",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_bi.png"), true, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110685_(f_110137_)
         .m_110683_(RAINBOW_TEXTURING)
         .m_110677_(f_110154_)
         .m_110691_(true)
   );
   public static final RenderType ACE_GLINT = m_173215_(
      "ace_glint",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_ace.png"), true, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110685_(f_110137_)
         .m_110683_(RAINBOW_TEXTURING)
         .m_110677_(f_110154_)
         .m_110691_(true)
   );
   public static final RenderType BRAZIL_GLINT = m_173215_(
      "brazil_glint",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_brazil.png"), true, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110685_(f_110137_)
         .m_110683_(RAINBOW_TEXTURING_LARGE)
         .m_110677_(f_110154_)
         .m_110691_(true)
   );
   public static final RenderType WEEZER_GLINT = m_173215_(
      "weezer_glint",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_weezer.png"), false, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110685_(f_110137_)
         .m_110683_(WEEZER_TEXTURING)
         .m_110677_(f_110154_)
         .m_110691_(true)
   );
   public static final RenderType STATIC_PORTAL = m_173215_(
      "static_portal",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(STATIC_TEXTURE, false, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110683_(STATIC_PORTAL_TEXTURING)
         .m_110677_(f_110154_)
         .m_110685_(f_110139_)
         .m_110691_(true)
   );
   public static final RenderType STATIC_PARTICLE = m_173215_(
      "static_particle",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(STATIC_TEXTURE, false, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110683_(STATIC_PARTICLE_TEXTURING)
         .m_110677_(f_110154_)
         .m_110685_(f_110139_)
         .m_110691_(true)
   );
   public static final RenderType STATIC_ENTITY = m_173215_(
      "static_entity",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173083_)
         .m_173290_(new TextureStateShard(STATIC_TEXTURE, false, false))
         .m_110687_(f_110114_)
         .m_110661_(f_110110_)
         .m_110663_(f_110112_)
         .m_110683_(STATIC_ENTITY_TEXTURING)
         .m_110677_(f_110154_)
         .m_110685_(f_110139_)
         .m_110691_(true)
   );
   public static final RenderType VOID_WORM_PORTAL_OVERLAY = m_173215_(
      "void_worm_portal_overlay",
      DefaultVertexFormat.f_85817_,
      Mode.QUADS,
      256,
      false,
      false,
      CompositeState.m_110628_()
         .m_173292_(f_173093_)
         .m_110663_(f_110112_)
         .m_110661_(f_110110_)
         .m_110685_(f_110134_)
         .m_173290_(
            MultiTextureStateShard.m_173127_()
               .m_173132_(TheEndPortalRenderer.f_112626_, false, false)
               .m_173132_(TheEndPortalRenderer.f_112627_, false, false)
               .m_173131_()
         )
         .m_110691_(false)
   );
   protected static final TransparencyStateShard WORM_TRANSPARANCY = new TransparencyStateShard("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard MIMICUBE_TRANSPARANCY = new TransparencyStateShard("mimicube_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard GHOST_TRANSPARANCY = new TransparencyStateShard("translucent_ghost_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });

   public AMRenderTypes(
      String p_173178_, VertexFormat p_173179_, Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_
   ) {
      super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
   }

   public static RenderType getTransparentMimicube(ResourceLocation texture) {
      CompositeState lvt_1_1_ = CompositeState.m_110628_()
         .m_173290_(new TextureStateShard(texture, false, false))
         .m_173292_(f_173065_)
         .m_110685_(f_110139_)
         .m_110677_(f_110154_)
         .m_110675_(f_110125_)
         .m_110661_(f_110158_)
         .m_110671_(f_110152_)
         .m_110677_(f_110154_)
         .m_110687_(RenderStateShard.f_110114_)
         .m_110663_(RenderStateShard.f_110113_)
         .m_110691_(true);
      return m_173215_("mimicube", DefaultVertexFormat.f_85812_, Mode.QUADS, 256, true, true, lvt_1_1_);
   }

   public static RenderType getEyesFlickering(ResourceLocation p_228652_0_, float lightLevel) {
      TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
      return m_173215_(
         "eye_flickering",
         DefaultVertexFormat.f_85812_,
         Mode.QUADS,
         256,
         false,
         true,
         CompositeState.m_110628_()
            .m_173290_(lvt_1_1_)
            .m_173292_(f_173065_)
            .m_110685_(f_110139_)
            .m_110661_(f_110110_)
            .m_110671_(f_110152_)
            .m_110677_(f_110154_)
            .m_110691_(false)
      );
   }

   public static RenderType getFullBright(ResourceLocation p_228652_0_) {
      TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
      return m_173215_(
         "full_bright",
         DefaultVertexFormat.f_85812_,
         Mode.QUADS,
         256,
         false,
         true,
         CompositeState.m_110628_()
            .m_173290_(lvt_1_1_)
            .m_173292_(f_173065_)
            .m_110685_(f_110139_)
            .m_110661_(f_110110_)
            .m_110671_(f_110152_)
            .m_110677_(f_110154_)
            .m_110691_(false)
      );
   }

   public static RenderType getFreddy(ResourceLocation p_228652_0_) {
      TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
      return m_173215_(
         "freddy",
         DefaultVertexFormat.f_85812_,
         Mode.QUADS,
         256,
         false,
         true,
         CompositeState.m_110628_()
            .m_173290_(lvt_1_1_)
            .m_173292_(f_173065_)
            .m_110685_(f_110139_)
            .m_110671_(RenderStateShard.f_110153_)
            .m_110661_(f_110110_)
            .m_110677_(f_110154_)
            .m_110691_(true)
      );
   }

   public static RenderType getFrilledSharkTeeth(ResourceLocation p_228652_0_) {
      TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
      return m_173215_(
         "sharkteeth",
         DefaultVertexFormat.f_85812_,
         Mode.QUADS,
         256,
         false,
         true,
         CompositeState.m_110628_()
            .m_173290_(lvt_1_1_)
            .m_173292_(f_173065_)
            .m_110685_(f_110134_)
            .m_110661_(f_110110_)
            .m_110671_(f_110152_)
            .m_110677_(f_110154_)
            .m_110691_(false)
      );
   }

   public static RenderType getEyesNoCull(ResourceLocation p_228652_0_) {
      TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
      return m_173215_(
         "eyes_no_cull",
         DefaultVertexFormat.f_85812_,
         Mode.QUADS,
         256,
         false,
         true,
         CompositeState.m_110628_().m_173290_(lvt_1_1_).m_173292_(f_173065_).m_110685_(f_110135_).m_110687_(f_110115_).m_110661_(f_110110_).m_110691_(false)
      );
   }

   public static RenderType getSpectreBones(ResourceLocation p_228652_0_) {
      TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
      return m_173215_(
         "spectre_bones",
         DefaultVertexFormat.f_85812_,
         Mode.QUADS,
         256,
         false,
         true,
         CompositeState.m_110628_()
            .m_173290_(lvt_1_1_)
            .m_173292_(f_173073_)
            .m_110685_(GHOST_TRANSPARANCY)
            .m_110663_(f_110113_)
            .m_110687_(f_110114_)
            .m_110661_(f_110110_)
            .m_110671_(f_110153_)
            .m_110677_(f_110154_)
            .m_110691_(false)
      );
   }

   public static RenderType getGhost(ResourceLocation p_228652_0_) {
      TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
      return m_173215_(
         "ghost_am",
         DefaultVertexFormat.f_85812_,
         Mode.QUADS,
         262144,
         false,
         true,
         CompositeState.m_110628_()
            .m_173290_(lvt_1_1_)
            .m_173292_(f_173073_)
            .m_110687_(f_110114_)
            .m_110663_(f_110112_)
            .m_110671_(f_110153_)
            .m_110677_(f_110154_)
            .m_110685_(GHOST_TRANSPARANCY)
            .m_110661_(RenderStateShard.f_110110_)
            .m_110691_(true)
      );
   }

   public static RenderType getEyesAlphaEnabled(ResourceLocation locationIn) {
      CompositeState rendertype$compositestate = CompositeState.m_110628_()
         .m_173292_(f_173073_)
         .m_173290_(new TextureStateShard(locationIn, false, false))
         .m_110685_(WORM_TRANSPARANCY)
         .m_110661_(f_110110_)
         .m_110671_(f_110152_)
         .m_110677_(f_110154_)
         .m_110663_(f_110112_)
         .m_110691_(true);
      return m_173215_("eye_alpha", DefaultVertexFormat.f_85812_, Mode.QUADS, 256, true, false, rendertype$compositestate);
   }

   public static RenderType getEyesNoFog(ResourceLocation locationIn) {
      TextureStateShard renderstateshard$texturestateshard = new TextureStateShard(locationIn, false, false);
      return m_173215_(
         "eyes_nofog",
         DefaultVertexFormat.f_85818_,
         Mode.QUADS,
         256,
         true,
         false,
         CompositeState.m_110628_()
            .m_173292_(f_173077_)
            .m_173290_(renderstateshard$texturestateshard)
            .m_110685_(f_110136_)
            .m_110687_(f_110114_)
            .m_110661_(f_110110_)
            .m_110663_(f_110113_)
            .m_110677_(f_110154_)
            .m_110691_(true)
      );
   }

   public static RenderType getSunbirdShine() {
      return m_173215_(
         "sunbird_shine",
         DefaultVertexFormat.f_85817_,
         Mode.QUADS,
         256,
         true,
         true,
         CompositeState.m_110628_()
            .m_173292_(f_173083_)
            .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/sunbird_shine.png"), true, true))
            .m_110671_(f_110152_)
            .m_110661_(RenderStateShard.f_110110_)
            .m_110685_(RenderStateShard.f_110139_)
            .m_110677_(f_110154_)
            .m_110663_(f_110113_)
            .m_110691_(true)
      );
   }

   public static RenderType getSkulkBoom() {
      CompositeState renderState = CompositeState.m_110628_()
         .m_173292_(f_173074_)
         .m_110661_(f_110110_)
         .m_173290_(new TextureStateShard(new ResourceLocation("alexsmobs:textures/particle/skulk_boom.png"), true, true))
         .m_110685_(f_110139_)
         .m_110671_(f_110152_)
         .m_110677_(f_110154_)
         .m_110687_(f_110115_)
         .m_110663_(f_110113_)
         .m_110669_(f_110119_)
         .m_110691_(false);
      return m_173215_("skulk_boom", DefaultVertexFormat.f_85812_, Mode.QUADS, 256, true, true, renderState);
   }

   public static RenderType getUnderminer(ResourceLocation texture) {
      CompositeState renderState = CompositeState.m_110628_()
         .m_173292_(f_173074_)
         .m_110661_(f_110110_)
         .m_173290_(new TextureStateShard(texture, false, false))
         .m_110685_(f_110139_)
         .m_110671_(f_110152_)
         .m_110677_(f_110154_)
         .m_110687_(f_110114_)
         .m_110663_(f_110113_)
         .m_110669_(f_110117_)
         .m_110691_(false);
      return m_173215_("underminer", DefaultVertexFormat.f_85812_, Mode.QUADS, 256, true, true, renderState);
   }

   public static RenderType getGhostPickaxe(ResourceLocation texture) {
      CompositeState renderState = CompositeState.m_110628_()
         .m_173292_(f_173064_)
         .m_110661_(f_110110_)
         .m_110675_(f_110129_)
         .m_173290_(new TextureStateShard(texture, false, false))
         .m_110685_(RenderStateShard.f_110136_)
         .m_110671_(f_110152_)
         .m_110677_(f_110154_)
         .m_110687_(f_110114_)
         .m_110663_(f_110113_)
         .m_110669_(f_110117_)
         .m_110691_(false);
      return m_173215_("ghost_pickaxe", DefaultVertexFormat.f_85812_, Mode.QUADS, 256, true, true, renderState);
   }

   public static RenderType getGhostCrumbling(ResourceLocation texture) {
      TextureStateShard lvt_1_1_ = new TextureStateShard(texture, false, false);
      return m_173215_(
         "ghost_crumbling_am",
         DefaultVertexFormat.f_85812_,
         Mode.QUADS,
         262144,
         false,
         true,
         CompositeState.m_110628_()
            .m_173290_(lvt_1_1_)
            .m_173292_(RenderStateShard.f_173074_)
            .m_110685_(f_110136_)
            .m_110661_(f_110110_)
            .m_110671_(f_110152_)
            .m_110677_(f_110154_)
            .m_110669_(f_110119_)
            .m_110663_(f_110113_)
            .m_110661_(RenderStateShard.f_110110_)
            .m_110691_(true)
      );
   }

   private static void setupRainbowTexturing(float in, long time) {
      long i = Util.m_137550_() * time;
      float f = (float)(i % 110000L) / 110000.0F;
      float f1 = (float)(i % 30000L) / 30000.0F;
      Matrix4f matrix4f = new Matrix4f().translation(0.0F, f1, 0.0F);
      matrix4f.scale(in);
      RenderSystem.setTextureMatrix(matrix4f);
   }

   private static void setupRainbowTexturing2(float in, long time) {
      long i = Util.m_137550_() * time;
      float f = (float)(i % 110000L) / 110000.0F;
      float f1 = (float)(i % 30000L) / 30000.0F;
      float f2 = (float)Math.sin((float)i / 30000.0F);
      Matrix4f matrix4f = new Matrix4f().translation(f1, f2, 0.0F);
      matrix4f.scale(in);
      RenderSystem.setTextureMatrix(matrix4f);
   }

   private static void setupStaticTexturing(float in, long time) {
      long i = Util.m_137550_() * time;
      float f = (float)(i % 110000L) / 110000.0F;
      float f1 = (float)(i % 30000L) / 30000.0F;
      float f2 = (float)Math.floor((float)(i % 3000L) / 3000.0F * 4.0F);
      float f3 = (float)Math.sin((float)i / 30000.0F) * 0.05F;
      Matrix4f matrix4f = new Matrix4f().translation(f1, f2 * 0.25F + f3, 0.0F);
      matrix4f.scale(in * 1.5F, in * 0.25F, in);
      RenderSystem.setTextureMatrix(matrix4f);
   }

   public static RenderType getFarseerBeam() {
      CompositeState renderState = CompositeState.m_110628_()
         .m_173292_(f_173074_)
         .m_110661_(f_110158_)
         .m_173290_(new TextureStateShard(STATIC_TEXTURE, false, false))
         .m_110685_(f_110139_)
         .m_110671_(f_110152_)
         .m_110677_(f_110154_)
         .m_110687_(f_110115_)
         .m_110663_(f_110113_)
         .m_110669_(f_110119_)
         .m_110691_(false);
      return m_173215_("farseer_beam", DefaultVertexFormat.f_85812_, Mode.QUADS, 256, true, true, renderState);
   }

   public static VertexConsumer createMergedVertexConsumer(VertexConsumer consumer1, VertexConsumer consumer2) {
      VertexConsumer vertexConsumer = consumer2;
      if (!encounteredMultiConsumerError) {
         try {
            vertexConsumer = VertexMultiConsumer.m_86168_(consumer1, consumer2);
         } catch (Exception var4) {
            AlexsMobs.LOGGER
               .warn(
                  "Encountered issue mixing two render types together. Likely an issue with Optifine or other rendering mod. This warning will only display once."
               );
            encounteredMultiConsumerError = true;
         }
      }

      return vertexConsumer;
   }
}
