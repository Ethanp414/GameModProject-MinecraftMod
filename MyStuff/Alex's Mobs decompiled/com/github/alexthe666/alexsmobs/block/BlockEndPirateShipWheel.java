package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateShipWheel;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEndPirateShipWheel extends BaseEntityBlock implements AMSpecialRenderBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;
   private static final VoxelShape SOUTH_AABB = Block.m_49796_(-2.0, -2.0, 0.0, 18.0, 18.0, 3.0);
   private static final VoxelShape NORTH_AABB = Block.m_49796_(-2.0, -2.0, 13.0, 18.0, 18.0, 16.0);
   private static final VoxelShape EAST_AABB = Block.m_49796_(0.0, -2.0, -2.0, 3.0, 18.0, 18.0);
   private static final VoxelShape WEST_AABB = Block.m_49796_(13.0, -2.0, -2.0, 16.0, 18.0, 18.0);
   private static final VoxelShape UP_AABB = Block.m_49796_(-2.0, 0.0, -2.0, 18.0, 3.0, 18.0);
   private static final VoxelShape DOWN_AABB = Block.m_49796_(-2.0, 13.0, -2.0, 16.0, 16.0, 18.0);

   public BlockEndPirateShipWheel() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283919_).m_60955_().m_60918_(SoundType.f_56726_).m_60978_(1.0F).m_60953_(i -> 3).m_60910_().m_60999_());
      this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(FACING, Direction.NORTH));
   }

   public BlockState m_7417_(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos p_52801_) {
      return !state.m_60710_(level, pos) ? Blocks.f_50016_.m_49966_() : super.m_7417_(state, direction, state2, level, pos, p_52801_);
   }

   public VoxelShape m_5940_(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
      return switch ((Direction)p_54561_.m_61143_(FACING)) {
         case NORTH -> NORTH_AABB;
         case SOUTH -> SOUTH_AABB;
         case EAST -> EAST_AABB;
         case WEST -> WEST_AABB;
         case UP -> UP_AABB;
         default -> DOWN_AABB;
      };
   }

   public boolean m_7898_(BlockState state, LevelReader world, BlockPos pos) {
      boolean remove = false;
      Direction dir = ((Direction)state.m_61143_(FACING)).m_122424_();
      BlockPos offset = pos.m_121945_(dir);
      return remove || world.m_8055_(offset).m_60783_(world, offset, dir.m_122424_());
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (worldIn.m_7702_(pos) instanceof TileEntityEndPirateShipWheel wheel) {
         boolean clockwise = false;
         Vec3 offset = hit.m_82450_().m_82492_(pos.m_123341_(), pos.m_123342_(), pos.m_123343_());
         switch ((Direction)state.m_61143_(FACING)) {
            case NORTH:
               clockwise = offset.f_82479_ <= 0.5;
               break;
            case SOUTH:
               clockwise = offset.f_82479_ >= 0.5;
               break;
            case EAST:
               clockwise = offset.f_82481_ <= 0.5;
               break;
            case WEST:
               clockwise = offset.f_82481_ >= 0.5;
         }

         wheel.rotate(clockwise);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new TileEntityEndPirateShipWheel(pos, state);
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)this.m_49966_().m_61124_(FACING, context.m_8125_().m_122424_());
   }

   public BlockState m_6843_(BlockState state, Rotation rot) {
      return (BlockState)state.m_61124_(FACING, rot.m_55954_((Direction)state.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState state, Mirror mirrorIn) {
      return state.m_60717_(mirrorIn.m_54846_((Direction)state.m_61143_(FACING)));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING});
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return m_152132_(p_152182_, (BlockEntityType)AMTileEntityRegistry.END_PIRATE_SHIP_WHEEL.get(), TileEntityEndPirateShipWheel::commonTick);
   }
}
