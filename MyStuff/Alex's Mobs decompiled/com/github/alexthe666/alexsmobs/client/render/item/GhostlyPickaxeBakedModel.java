package com.github.alexthe666.alexsmobs.client.render.item;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GhostlyPickaxeBakedModel extends BakedModelWrapper {
   public GhostlyPickaxeBakedModel(BakedModel bakedModel) {
      super(bakedModel);
   }

   public List<BakedQuad> m_213637_(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
      return transformQuads(super.m_213637_(state, side, rand));
   }

   public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
      return List.of(AMRenderTypes.getGhostPickaxe(TextureAtlas.f_118259_));
   }

   public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
      this.m_7442_().m_269404_(cameraTransformType).m_111763_(applyLeftHandTransform, poseStack);
      return this;
   }

   public List<BakedQuad> getQuads(
      @Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType
   ) {
      return transformQuads(this.originalModel.getQuads(state, side, rand, extraData, renderType));
   }

   private static List<BakedQuad> transformQuads(List<BakedQuad> oldQuads) {
      List<BakedQuad> quads = new ArrayList<>();

      for (BakedQuad quad : oldQuads) {
         quads.add(setFullbright(quad));
      }

      return quads;
   }

   private static BakedQuad setFullbright(BakedQuad quad) {
      int[] vertexData = (int[])quad.m_111303_().clone();
      int step = vertexData.length / 4;
      vertexData[6] = 15728880;
      vertexData[6 + step] = 15728880;
      vertexData[6 + 2 * step] = 15728880;
      vertexData[6 + 3 * step] = 15728880;
      return new BakedQuad(vertexData, quad.m_111305_(), quad.m_111306_(), quad.m_173410_(), quad.m_111307_());
   }

   public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
      return List.of(this);
   }
}
