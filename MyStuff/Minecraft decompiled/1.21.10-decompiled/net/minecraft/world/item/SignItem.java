package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignItem extends StandingAndWallBlockItem {
   public SignItem(Block $$0, Block $$1, Item.Properties $$2) {
      super($$0, $$1, Direction.DOWN, $$2);
   }

   public SignItem(Item.Properties $$0, Block $$1, Block $$2, Direction $$3) {
      super($$1, $$2, $$3, $$0);
   }

   @Override
   protected boolean updateCustomBlockEntityTag(BlockPos $$0, Level $$1, @Nullable Player $$2, ItemStack $$3, BlockState $$4) {
      boolean $$5 = super.updateCustomBlockEntityTag($$0, $$1, $$2, $$3, $$4);
      if (!$$1.isClientSide() && !$$5 && $$2 != null) {
         BlockEntity var9 = $$1.getBlockEntity($$0);
         if (var9 instanceof SignBlockEntity $$6) {
            Block var10 = $$1.getBlockState($$0).getBlock();
            if (var10 instanceof SignBlock $$7) {
               $$7.openTextEdit($$2, $$6, true);
            }
         }
      }

      return $$5;
   }
}
