package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBlock extends AbstractFurnaceBlock {
   public static final MapCodec<FurnaceBlock> CODEC = simpleCodec(FurnaceBlock::new);

   @Override
   public MapCodec<FurnaceBlock> codec() {
      return CODEC;
   }

   protected FurnaceBlock(BlockBehaviour.Properties $$0) {
      super($$0);
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
      return new FurnaceBlockEntity($$0, $$1);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
      return createFurnaceTicker($$0, $$2, BlockEntityType.FURNACE);
   }

   @Override
   protected void openContainer(Level $$0, BlockPos $$1, Player $$2) {
      BlockEntity $$3 = $$0.getBlockEntity($$1);
      if ($$3 instanceof FurnaceBlockEntity) {
         $$2.openMenu((MenuProvider)$$3);
         $$2.awardStat(Stats.INTERACT_WITH_FURNACE);
      }
   }

   @Override
   public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
      if ($$0.getValue(LIT)) {
         double $$4 = (double)$$2.getX() + 0.5;
         double $$5 = (double)$$2.getY();
         double $$6 = (double)$$2.getZ() + 0.5;
         if ($$3.nextDouble() < 0.1) {
            $$1.playLocalSound($$4, $$5, $$6, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction $$7 = $$0.getValue(FACING);
         Direction.Axis $$8 = $$7.getAxis();
         double $$9 = 0.52;
         double $$10 = $$3.nextDouble() * 0.6 - 0.3;
         double $$11 = $$8 == Direction.Axis.X ? (double)$$7.getStepX() * 0.52 : $$10;
         double $$12 = $$3.nextDouble() * 6.0 / 16.0;
         double $$13 = $$8 == Direction.Axis.Z ? (double)$$7.getStepZ() * 0.52 : $$10;
         $$1.addParticle(ParticleTypes.SMOKE, $$4 + $$11, $$5 + $$12, $$6 + $$13, 0.0, 0.0, 0.0);
         $$1.addParticle(ParticleTypes.FLAME, $$4 + $$11, $$5 + $$12, $$6 + $$13, 0.0, 0.0, 0.0);
      }
   }
}
