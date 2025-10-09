package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import java.util.ArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class BlockSkunkSpray extends MultifaceBlock implements SimpleWaterloggedBlock {
   public static final IntegerProperty AGE = BlockStateProperties.f_61407_;
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.f_61362_;

   public BlockSkunkSpray() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283916_).m_60955_().m_60977_().m_60910_().m_60966_().m_60918_(SoundType.f_222466_));
      this.m_49959_((BlockState)((BlockState)this.m_49966_().m_61124_(WATERLOGGED, false)).m_61124_(AGE, 0));
   }

   public BlockState m_7417_(BlockState state, Direction direction, BlockState state2, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos2) {
      if ((Boolean)state.m_61143_(WATERLOGGED)) {
         levelAccessor.m_186469_(pos, Fluids.f_76193_, Fluids.f_76193_.m_6718_(levelAccessor));
      }

      return super.m_7417_(state, direction, state2, levelAccessor, pos, pos2);
   }

   public void m_213898_(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
      this.m_213897_(state, level, pos, randomSource);
   }

   public void m_213897_(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (random.m_188503_(8) == 0) {
         MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

         for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.m_122159_(pos, direction);
            BlockState blockstate = level.m_8055_(blockpos$mutableblockpos);
            if (blockstate.m_60713_(this) && !this.incrementAge(blockstate, level, blockpos$mutableblockpos)) {
               level.m_186460_(blockpos$mutableblockpos, this, Mth.m_216271_(random, 50, 100));
            }
         }

         this.incrementAge(state, level, pos);
      } else {
         level.m_186460_(pos, this, Mth.m_216271_(random, 50, 100));
      }
   }

   protected void m_7926_(Builder<Block, BlockState> definition) {
      super.m_7926_(definition);
      definition.m_61104_(new Property[]{WATERLOGGED, AGE});
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      ItemStack itemStack = player.m_21120_(handIn);
      int setContent = -1;
      if (itemStack.m_150930_(Items.f_42590_)) {
         Direction dir = hit.m_82434_().m_122424_();
         if (m_153900_(state, dir)) {
            worldIn.m_46597_(pos, removeStinkFace(state, dir));
            ItemStack bottle = new ItemStack((ItemLike)AMItemRegistry.STINK_BOTTLE.get());
            if (!player.m_36356_(bottle)) {
               player.m_36176_(bottle, false);
            }

            if (!player.m_7500_()) {
               itemStack.m_41774_(1);
            }

            return InteractionResult.SUCCESS;
         }
      }

      return super.m_6227_(state, worldIn, pos, player, handIn, hit);
   }

   public static BlockState removeStinkFace(BlockState state, Direction faceProperty) {
      BlockState blockstate = (BlockState)state.m_61124_(m_153933_(faceProperty), false);
      return m_153960_(blockstate) ? blockstate : Blocks.f_50016_.m_49966_();
   }

   private boolean incrementAge(BlockState state, Level level, BlockPos pos) {
      int i = (Integer)state.m_61143_(AGE);
      if (i < 3) {
         level.m_7731_(pos, (BlockState)state.m_61124_(AGE, i + 1), 2);
         return false;
      } else {
         level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 2);
         return true;
      }
   }

   public boolean m_6864_(BlockState state, BlockPlaceContext context) {
      return !context.m_43722_().m_150930_(((Block)AMBlockRegistry.SKUNK_SPRAY.get()).m_5456_()) || super.m_6864_(state, context);
   }

   public FluidState m_5888_(BlockState state) {
      return state.m_61143_(WATERLOGGED) ? Fluids.f_76193_.m_76068_(false) : super.m_5888_(state);
   }

   public MultifaceSpreader m_213612_() {
      return null;
   }

   public boolean m_7420_(BlockState state, BlockGetter level, BlockPos pos) {
      return state.m_60819_().m_76178_();
   }

   public void m_214162_(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource) {
      if (randomSource.m_188503_(2) == 0) {
         ArrayList<Direction> faces = new ArrayList<>(m_221584_(blockState));
         Direction direction = null;
         if (faces.size() == 1) {
            direction = faces.get(0);
         } else if (faces.size() > 1) {
            direction = (Direction)Util.m_214621_(faces, randomSource);
         }

         if (direction != null) {
            double d0 = direction.m_122429_() == 0 ? randomSource.m_188500_() : 0.5 + direction.m_122429_() * 0.8;
            double d1 = direction.m_122430_() == 0 ? randomSource.m_188500_() : 0.5 + direction.m_122430_() * 0.8;
            double d2 = direction.m_122431_() == 0 ? randomSource.m_188500_() : 0.5 + direction.m_122431_() * 0.8;
            level.m_7106_((ParticleOptions)AMParticleRegistry.SMELLY.get(), pos.m_123341_() + d0, pos.m_123342_() + d1, pos.m_123343_() + d2, 0.0, 0.0, 0.0);
         }
      }
   }
}
