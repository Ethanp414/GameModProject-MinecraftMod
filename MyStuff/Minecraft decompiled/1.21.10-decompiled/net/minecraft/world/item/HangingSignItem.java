package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HangingSignItem extends SignItem {
   public HangingSignItem(Block $$0, Block $$1, Item.Properties $$2) {
      super($$2, $$0, $$1, Direction.UP);
   }

   @Override
   protected boolean canPlace(LevelReader $$0, BlockState $$1, BlockPos $$2) {
      Block var5 = $$1.getBlock();
      if (var5 instanceof WallHangingSignBlock $$3 && !$$3.canPlace($$1, $$0, $$2)) {
         return false;
      }

      return super.canPlace($$0, $$1, $$2);
   }
}
