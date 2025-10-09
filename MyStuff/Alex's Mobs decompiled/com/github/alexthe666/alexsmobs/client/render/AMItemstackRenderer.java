package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateShipWheel;
import com.github.alexthe666.alexsmobs.client.model.ModelMysteriousWorm;
import com.github.alexthe666.alexsmobs.client.model.ModelShieldOfTheDeep;
import com.github.alexthe666.alexsmobs.client.model.ModelTransmutationTable;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemStinkRay;
import com.github.alexthe666.alexsmobs.item.ItemTabIcon;
import com.github.alexthe666.alexsmobs.item.ItemVineLasso;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;

public class AMItemstackRenderer extends BlockEntityWithoutLevelRenderer {
   public static int ticksExisted = 0;
   private static final ModelShieldOfTheDeep SHIELD_OF_THE_DEEP_MODEL = new ModelShieldOfTheDeep();
   private static final ResourceLocation SHIELD_OF_THE_DEEP_TEXTURE = new ResourceLocation("alexsmobs:textures/armor/shield_of_the_deep.png");
   private static final ModelMysteriousWorm MYTERIOUS_WORM_MODEL = new ModelMysteriousWorm();
   private static final ResourceLocation MYTERIOUS_WORM_TEXTURE = new ResourceLocation("alexsmobs:textures/item/mysterious_worm_model.png");
   private static final ModelEndPirateAnchor ANCHOR_MODEL = new ModelEndPirateAnchor();
   private static final ResourceLocation ANCHOR_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/anchor.png");
   private static final ModelEndPirateAnchorWinch WINCH_MODEL = new ModelEndPirateAnchorWinch();
   private static final ResourceLocation WINCH_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/anchor_winch.png");
   private static final ModelEndPirateShipWheel SHIP_WHEEL_MODEL = new ModelEndPirateShipWheel();
   private static final ResourceLocation SHIP_WHEEL_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/end_pirate/ship_wheel.png");
   private static final ResourceLocation TRANSMUTATION_TABLE_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/farseer/transmutation_table.png");
   private static final ResourceLocation TRANSMUTATION_TABLE_GLOW_TEXTURE = new ResourceLocation(
      "alexsmobs:textures/entity/farseer/transmutation_table_glow.png"
   );
   private static final ResourceLocation TRANSMUTATION_TABLE_OVERLAY = new ResourceLocation("alexsmobs:textures/entity/farseer/transmutation_table_overlay.png");
   private static final ModelTransmutationTable TRANSMUTATION_TABLE_MODEL = new ModelTransmutationTable(0.0F);
   private static final ModelTransmutationTable TRANSMUTATION_TABLE_OVERLAY_MODEL = new ModelTransmutationTable(0.01F);
   private static List<ItemStack> DIMENSIONAL_CARVER_SHARDS;
   private final Map<String, Entity> renderedEntites = new HashMap<>();
   private final List<EntityType> blockedRenderEntities = new ArrayList<>();

   public AMItemstackRenderer() {
      super(null, null);
   }

   public static void incrementTick() {
      ticksExisted++;
   }

   private static float getScaleFor(EntityType type, List<Pair<EntityType, Float>> mobIcons) {
      for (Pair<EntityType, Float> pair : mobIcons) {
         if (pair.getFirst() == type) {
            return (Float)pair.getSecond();
         }
      }

      return 1.0F;
   }

   private static List<ItemStack> getDimensionalCarverShards() {
      if (DIMENSIONAL_CARVER_SHARDS == null || DIMENSIONAL_CARVER_SHARDS.isEmpty()) {
         DIMENSIONAL_CARVER_SHARDS = (List<ItemStack>)Util.m_137469_(Lists.newArrayList(), list -> {
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_0"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_1"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_2"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_3"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_4"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_5"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_6"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_7"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_8"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_9"))));
            list.add(new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("alexsmobs:dimensional_carver_shard_10"))));
         });
      }

      return DIMENSIONAL_CARVER_SHARDS;
   }

   public static void drawEntityOnScreen(
      PoseStack matrixstack, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity
   ) {
      float f = (float)Math.atan(-mouseX / 40.0F);
      float f1 = (float)Math.atan(mouseY / 40.0F);
      matrixstack.m_85841_(scale, scale, scale);
      entity.m_6853_(false);
      float partialTicks = Minecraft.m_91087_().m_91296_();
      Quaternionf quaternion = Axis.f_252403_.m_252977_(180.0F);
      Quaternionf quaternion1 = Axis.f_252529_.m_252977_(20.0F);
      float partialTicksForRender = !Minecraft.m_91087_().m_91104_() && !(entity instanceof EntityMimicOctopus) ? partialTicks : 0.0F;
      int tick;
      if (Minecraft.m_91087_().f_91074_ != null && !Minecraft.m_91087_().m_91104_()) {
         tick = Minecraft.m_91087_().f_91074_.f_19797_;
      } else {
         tick = ticksExisted;
      }

      if (follow) {
         float yaw = f * 45.0F;
         entity.m_146922_(yaw);
         entity.f_19797_ = tick;
         if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).f_20883_ = yaw;
            ((LivingEntity)entity).f_20884_ = yaw;
            ((LivingEntity)entity).f_20885_ = yaw;
            ((LivingEntity)entity).f_20886_ = yaw;
         }

         quaternion1 = Axis.f_252529_.m_252977_(f1 * 20.0F);
         quaternion.mul(quaternion1);
      }

      matrixstack.m_252781_(quaternion);
      matrixstack.m_252781_(Axis.f_252529_.m_252977_((float)(-xRot)));
      matrixstack.m_252781_(Axis.f_252436_.m_252977_((float)yRot));
      matrixstack.m_252781_(Axis.f_252403_.m_252977_((float)zRot));
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.m_91087_().m_91290_();
      quaternion1.conjugate();
      entityrenderdispatcher.m_252923_(quaternion1);
      entityrenderdispatcher.m_114468_(false);
      BufferSource multibuffersource$buffersource = Minecraft.m_91087_().m_91269_().m_110104_();
      RenderSystem.runAsFancy(
         () -> entityrenderdispatcher.m_114384_(entity, 0.0, 0.0, 0.0, 0.0F, partialTicksForRender, matrixstack, multibuffersource$buffersource, 15728880)
      );
      multibuffersource$buffersource.m_109911_();
      entityrenderdispatcher.m_114468_(true);
      entity.m_146922_(0.0F);
      entity.m_146926_(0.0F);
      if (entity instanceof LivingEntity) {
         ((LivingEntity)entity).f_20883_ = 0.0F;
         ((LivingEntity)entity).f_20886_ = 0.0F;
         ((LivingEntity)entity).f_20885_ = 0.0F;
      }

      RenderSystem.applyModelViewMatrix();
      Lighting.m_84931_();
   }

   public void m_108829_(
      ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      int tick;
      if (Minecraft.m_91087_().f_91074_ != null && !Minecraft.m_91087_().m_91104_()) {
         tick = Minecraft.m_91087_().f_91074_.f_19797_;
      } else {
         tick = ticksExisted;
      }

      Level level = Minecraft.m_91087_().f_91073_;
      if (itemStackIn.m_41720_() == AMItemRegistry.SHIELD_OF_THE_DEEP.get()) {
         matrixStackIn.m_85836_();
         matrixStackIn.m_252880_(0.4F, -0.75F, 0.5F);
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(-180.0F));
         VertexConsumer vertexconsumer = ItemRenderer.m_115184_(bufferIn, RenderType.m_110431_(SHIELD_OF_THE_DEEP_TEXTURE), false, itemStackIn.m_41790_());
         SHIELD_OF_THE_DEEP_MODEL.m_7695_(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.m_85849_();
      }

      if (itemStackIn.m_41720_() == AMItemRegistry.MYSTERIOUS_WORM.get()) {
         matrixStackIn.m_85836_();
         matrixStackIn.m_252880_(0.0F, -2.0F, 0.0F);
         matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(-180.0F));
         MYTERIOUS_WORM_MODEL.animateStack(itemStackIn);
         MYTERIOUS_WORM_MODEL.m_7695_(
            matrixStackIn, bufferIn.m_6299_(RenderType.m_110458_(MYTERIOUS_WORM_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F
         );
         matrixStackIn.m_85849_();
      }

      if (itemStackIn.m_41720_() == AMItemRegistry.FALCONRY_GLOVE.get()) {
         matrixStackIn.m_252880_(0.5F, 0.5F, 0.5F);
         if (transformType != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            && transformType != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  new ItemStack((ItemLike)AMItemRegistry.FALCONRY_GLOVE_INVENTORY.get()),
                  transformType,
                  transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
                  combinedOverlayIn,
                  matrixStackIn,
                  bufferIn,
                  level,
                  0
               );
         } else {
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  new ItemStack((ItemLike)AMItemRegistry.FALCONRY_GLOVE_HAND.get()),
                  transformType,
                  combinedLightIn,
                  combinedOverlayIn,
                  matrixStackIn,
                  bufferIn,
                  level,
                  0
               );
         }
      }

      if (itemStackIn.m_41720_() == AMItemRegistry.VINE_LASSO.get()) {
         matrixStackIn.m_252880_(0.5F, 0.5F, 0.5F);
         if (transformType != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            && transformType != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO_INVENTORY.get()),
                  transformType,
                  transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
                  combinedOverlayIn,
                  matrixStackIn,
                  bufferIn,
                  level,
                  0
               );
         } else {
            if (ItemVineLasso.isItemInUse(itemStackIn)) {
               if (transformType.m_269069_()) {
                  matrixStackIn.m_252880_(transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.0F, -0.5F);
               }

               matrixStackIn.m_252781_(Axis.f_252436_.m_252961_(tick + Minecraft.m_91087_().m_91296_()));
            }

            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO_HAND.get()),
                  transformType,
                  combinedLightIn,
                  combinedOverlayIn,
                  matrixStackIn,
                  bufferIn,
                  level,
                  0
               );
         }
      }

      if (itemStackIn.m_41720_() == AMItemRegistry.SKELEWAG_SWORD.get()) {
         matrixStackIn.m_252880_(0.5F, 0.5F, 0.5F);
         ItemStack spriteItem = new ItemStack((ItemLike)AMItemRegistry.SKELEWAG_SWORD_INVENTORY.get());
         ItemStack handItem = new ItemStack((ItemLike)AMItemRegistry.SKELEWAG_SWORD_HAND.get());
         spriteItem.m_41751_(itemStackIn.m_41783_());
         handItem.m_41751_(itemStackIn.m_41783_());
         if (transformType != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            && transformType != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  spriteItem,
                  transformType,
                  transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
                  combinedOverlayIn,
                  matrixStackIn,
                  bufferIn,
                  level,
                  0
               );
         } else {
            Minecraft.m_91087_().m_91291_().m_269128_(handItem, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
         }
      }

      if (itemStackIn.m_41720_() == ((Block)AMBlockRegistry.TRANSMUTATION_TABLE.get()).m_5456_()) {
         matrixStackIn.m_85836_();
         matrixStackIn.m_252880_(0.5F, 1.6F, 0.5F);
         matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-180.0F));
         TRANSMUTATION_TABLE_MODEL.resetToDefaultPose();
         TRANSMUTATION_TABLE_MODEL.m_7695_(
            matrixStackIn, bufferIn.m_6299_(RenderType.m_110458_(TRANSMUTATION_TABLE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F
         );
         TRANSMUTATION_TABLE_MODEL.m_7695_(
            matrixStackIn, bufferIn.m_6299_(RenderType.m_234338_(TRANSMUTATION_TABLE_GLOW_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F
         );
         TRANSMUTATION_TABLE_OVERLAY_MODEL.resetToDefaultPose();
         VertexConsumer staticyOverlay = bufferIn.m_6299_(RenderType.m_110488_(TRANSMUTATION_TABLE_OVERLAY));
         TRANSMUTATION_TABLE_OVERLAY_MODEL.m_7695_(matrixStackIn, staticyOverlay, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.m_85849_();
      }

      if (itemStackIn.m_41720_() == AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER.get()) {
         matrixStackIn.m_252880_(0.5F, 0.5F, 0.5F);
         float f = tick + Minecraft.m_91087_().m_91296_();
         List<ItemStack> shards = getDimensionalCarverShards();
         matrixStackIn.m_85836_();
         if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            matrixStackIn.m_252880_(-0.2F, 0.0F, 0.0F);
            matrixStackIn.m_85841_(1.3F, 1.3F, 1.3F);
            matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(180.0F));
            matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(60.0F));
         }

         for (int i = 0; i < shards.size(); i++) {
            matrixStackIn.m_85836_();
            ItemStack shard = shards.get(i);
            matrixStackIn.m_252880_(
               (float)Math.sin(f * 0.15F + i * 1.0F) * 0.035F,
               -((float)Math.cos(f * 0.15F + i * 1.0F)) * 0.035F,
               (float)Math.cos(f * 0.15F + i * 0.5F + (Math.PI / 2)) * 0.025F
            );
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  shard,
                  transformType,
                  transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
                  combinedOverlayIn,
                  matrixStackIn,
                  bufferIn,
                  level,
                  0
               );
            matrixStackIn.m_85849_();
         }

         matrixStackIn.m_85849_();
      }

      if (itemStackIn.m_41720_() == AMItemRegistry.STINK_RAY.get()) {
         matrixStackIn.m_252880_(0.5F, 0.5F, 0.5F);
         ItemStack hand = new ItemStack(
            ItemStinkRay.isUsable(itemStackIn) ? (ItemLike)AMItemRegistry.STINK_RAY_HAND.get() : (ItemLike)AMItemRegistry.STINK_RAY_EMPTY_HAND.get()
         );
         ItemStack inventory = new ItemStack(
            ItemStinkRay.isUsable(itemStackIn) ? (ItemLike)AMItemRegistry.STINK_RAY_INVENTORY.get() : (ItemLike)AMItemRegistry.STINK_RAY_EMPTY_INVENTORY.get()
         );
         if (transformType != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            && transformType != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  inventory,
                  transformType,
                  transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
                  combinedOverlayIn,
                  matrixStackIn,
                  bufferIn,
                  level,
                  0
               );
         } else {
            Minecraft.m_91087_().m_91291_().m_269128_(hand, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
         }
      }

      if (itemStackIn.m_41720_() == AMItemRegistry.TAB_ICON.get()) {
         Entity fakeEntity = null;
         List<Pair<EntityType, Float>> mobIcons = AMMobIcons.getMobIcons();
         int entityIndex = tick / 40 % mobIcons.size();
         float scale = 1.0F;
         int flags = 0;
         if (level != null) {
            if (ItemTabIcon.hasCustomEntityDisplay(itemStackIn)) {
               flags = itemStackIn.m_41783_().m_128451_("DisplayMobFlags");
               String index = ItemTabIcon.getCustomDisplayEntityString(itemStackIn);
               EntityType local = ItemTabIcon.getEntityType(itemStackIn.m_41783_());
               scale = getScaleFor(local, mobIcons);
               if (itemStackIn.m_41783_().m_128457_("DisplayMobScale") > 0.0F) {
                  scale = itemStackIn.m_41783_().m_128457_("DisplayMobScale");
               }

               if (this.renderedEntites.get(index) == null && !this.blockedRenderEntities.contains(local)) {
                  try {
                     Entity entity = local.m_20615_(level);
                     if (entity instanceof EntityBlobfish) {
                        ((EntityBlobfish)entity).setDepressurized(true);
                     }

                     this.renderedEntites.put(local.m_20675_(), entity);
                     fakeEntity = entity;
                  } catch (Exception var22) {
                     this.blockedRenderEntities.add(local);
                     AlexsMobs.LOGGER.error("Could not render item for entity: " + local);
                  }
               } else {
                  fakeEntity = this.renderedEntites.get(local.m_20675_());
               }
            } else {
               EntityType type = (EntityType)mobIcons.get(entityIndex).getFirst();
               scale = (Float)mobIcons.get(entityIndex).getSecond();
               if (type != null) {
                  if (this.renderedEntites.get(type.m_20675_()) == null && !this.blockedRenderEntities.contains(type)) {
                     try {
                        Entity entity = type.m_20615_(level);
                        if (entity instanceof EntityBlobfish) {
                           ((EntityBlobfish)entity).setDepressurized(true);
                        }

                        this.renderedEntites.put(type.m_20675_(), entity);
                        fakeEntity = entity;
                     } catch (Exception var21) {
                        this.blockedRenderEntities.add(type);
                        AlexsMobs.LOGGER.error("Could not render item for entity: " + type);
                     }
                  } else {
                     fakeEntity = this.renderedEntites.get(type.m_20675_());
                  }
               }
            }
         }

         if (fakeEntity instanceof EntityCockroach) {
            if (flags == 99) {
               matrixStackIn.m_252880_(0.0F, 0.25F, 0.0F);
               matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(-80.0F));
               ((EntityCockroach)fakeEntity).setMaracas(true);
            } else {
               ((EntityCockroach)fakeEntity).setMaracas(false);
            }
         }

         if (fakeEntity instanceof EntityElephant) {
            if (flags == 99) {
               ((EntityElephant)fakeEntity).setTusked(true);
               ((EntityElephant)fakeEntity).setColor(null);
            } else if (flags == 98) {
               ((EntityElephant)fakeEntity).setTusked(false);
               ((EntityElephant)fakeEntity).setColor(DyeColor.BROWN);
            } else {
               ((EntityElephant)fakeEntity).setTusked(false);
               ((EntityElephant)fakeEntity).setColor(null);
            }
         }

         if (fakeEntity instanceof EntityBaldEagle) {
            if (flags == 98) {
               ((EntityBaldEagle)fakeEntity).setCap(true);
            } else {
               ((EntityBaldEagle)fakeEntity).setCap(false);
            }
         }

         if (fakeEntity instanceof EntityVoidWorm) {
            matrixStackIn.m_252880_(0.0F, 0.5F, 0.0F);
         }

         if (fakeEntity instanceof EntityMimicOctopus) {
            matrixStackIn.m_252880_(0.0F, 0.5F, 0.0F);
         }

         if (fakeEntity instanceof EntityLaviathan) {
            RenderLaviathan.renderWithoutShaking = true;
            matrixStackIn.m_252880_(0.0F, 0.3F, 0.0F);
         }

         if (fakeEntity instanceof EntityCosmaw) {
            matrixStackIn.m_252880_(0.0F, 0.2F, 0.0F);
         }

         if (fakeEntity instanceof EntityGiantSquid) {
            matrixStackIn.m_252880_(0.0F, 0.5F, 0.3F);
         }

         if (fakeEntity instanceof EntityUnderminer) {
            RenderUnderminer.renderWithPickaxe = true;
         }

         if (fakeEntity instanceof EntityMurmur) {
            RenderMurmurBody.renderWithHead = true;
            matrixStackIn.m_252880_(0.0F, -0.2F, 0.0F);
         }

         if (fakeEntity != null) {
            MouseHandler mouseHelper = Minecraft.m_91087_().f_91067_;
            double mouseX = mouseHelper.m_91589_() * Minecraft.m_91087_().m_91268_().m_85445_() / Minecraft.m_91087_().m_91268_().m_85443_();
            double mouseY = mouseHelper.m_91594_() * Minecraft.m_91087_().m_91268_().m_85446_() / Minecraft.m_91087_().m_91268_().m_85444_();
            matrixStackIn.m_252880_(0.5F, 0.0F, 0.0F);
            matrixStackIn.m_252781_(Axis.f_252529_.m_252977_(180.0F));
            matrixStackIn.m_252781_(Axis.f_252436_.m_252977_(180.0F));
            if (transformType != ItemDisplayContext.GUI) {
               mouseX = 0.0;
               mouseY = 0.0;
            }

            try {
               drawEntityOnScreen(matrixStackIn, 0, 0, scale, true, 0.0, -45.0, 0.0, (float)mouseX, (float)mouseY, fakeEntity);
            } catch (Exception var20) {
            }
         }

         if (fakeEntity instanceof EntityLaviathan) {
            RenderLaviathan.renderWithoutShaking = false;
         }

         if (fakeEntity instanceof EntityUnderminer) {
            RenderUnderminer.renderWithPickaxe = false;
         }

         if (fakeEntity instanceof EntityMurmur) {
            RenderMurmurBody.renderWithHead = false;
         }
      }
   }
}
