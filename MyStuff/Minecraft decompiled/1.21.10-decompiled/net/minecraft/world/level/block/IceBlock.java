package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class IceBlock extends HalfTransparentBlock {
   public static final MapCodec<IceBlock> CODEC = simpleCodec(IceBlock::new);

   @Override
   public MapCodec<? extends IceBlock> codec() {
      return CODEC;
   }

   public IceBlock(BlockBehaviour.Properties $$0) {
      super($$0);
   }

   public static BlockState meltsInto() {
      return Blocks.WATER.defaultBlockState();
   }

   @Override
   public void playerDestroy(Level $$0, Player $$1, BlockPos $$2, BlockState $$3, @Nullable BlockEntity $$4, ItemStack $$5) {
      super.playerDestroy($$0, $$1, $$2, $$3, $$4, $$5);
      if (!EnchantmentHelper.hasTag($$5, EnchantmentTags.PREVENTS_ICE_MELTING)) {
         if ($$0.dimensionType().ultraWarm()) {
            $$0.removeBlock($$2, false);
            return;
         }

         BlockState $$6 = $$0.getBlockState($$2.below());
         if ($$6.blocksMotion() || $$6.liquid()) {
            $$0.setBlockAndUpdate($$2, meltsInto());
         }
      }
   }

   @Override
   protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
      if ($$1.getBrightness(LightLayer.BLOCK, $$2) > 11 - $$0.getLightBlock()) {
         this.melt($$0, $$1, $$2);
      }
   }

   protected void melt(BlockState $$0, Level $$1, BlockPos $$2) {
      if ($$1.dimensionType().ultraWarm()) {
         $$1.removeBlock($$2, false);
      } else {
         $$1.setBlockAndUpdate($$2, meltsInto());
         $$1.neighborChanged($$2, meltsInto().getBlock(), null);
      }
   }
}
