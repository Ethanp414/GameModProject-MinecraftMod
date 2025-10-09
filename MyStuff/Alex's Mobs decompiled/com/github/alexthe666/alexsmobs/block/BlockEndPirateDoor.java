package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateDoor;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockEndPirateDoor extends BaseEntityBlock {
   public static final DirectionProperty HORIZONTAL_FACING = HorizontalDirectionalBlock.f_54117_;
   public static final BooleanProperty OPEN = BooleanProperty.m_61465_("open");
   public static final BooleanProperty POWERED = BlockStateProperties.f_61448_;
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.f_61394_;
   public static final IntegerProperty SEGMENT = IntegerProperty.m_61631_("segment", 0, 2);
   protected static final float AABB_DOOR_THICKNESS = 3.0F;
   protected static final VoxelShape SOUTH_AABB = Block.m_49796_(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
   protected static final VoxelShape NORTH_AABB = Block.m_49796_(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape WEST_AABB = Block.m_49796_(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape EAST_AABB = Block.m_49796_(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);

   public BlockEndPirateDoor() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60955_().m_60918_(SoundType.f_56744_).m_60953_(state -> 3).m_60999_().m_60978_(1.5F));
      this.m_49959_(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(SEGMENT, 0)).m_61124_(OPEN, false))
               .m_61124_(HINGE, DoorHingeSide.RIGHT))
            .m_61124_(HORIZONTAL_FACING, Direction.NORTH)
      );
   }

   public VoxelShape m_5940_(BlockState p_52807_, BlockGetter p_52808_, BlockPos p_52809_, CollisionContext p_52810_) {
      Direction direction = (Direction)p_52807_.m_61143_(HORIZONTAL_FACING);
      boolean flag = !(Boolean)p_52807_.m_61143_(OPEN);
      boolean flag1 = p_52807_.m_61143_(HINGE) == DoorHingeSide.RIGHT;

      return switch (direction) {
         case SOUTH -> flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
         case WEST -> flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
         case NORTH -> flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
         default -> flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
      };
   }

   public BlockState m_7417_(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos p_52801_) {
      if ((Integer)state.m_61143_(SEGMENT) != 0) {
         return !state.m_60710_(level, pos) ? Blocks.f_50016_.m_49966_() : super.m_7417_(state, direction, state2, level, pos, p_52801_);
      } else {
         return state.m_60710_(level, pos) && level.m_8055_(pos.m_7494_()).m_60713_(this) && level.m_8055_(pos.m_6630_(2)).m_60713_(this)
            ? super.m_7417_(state, direction, state2, level, pos, p_52801_)
            : Blocks.f_50016_.m_49966_();
      }
   }

   public BlockState m_6843_(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.m_61124_(HORIZONTAL_FACING, p_185499_2_.m_55954_((Direction)p_185499_1_.m_61143_(HORIZONTAL_FACING)));
   }

   public BlockState m_6943_(BlockState state, Mirror mir) {
      return state.m_60717_(mir.m_54846_((Direction)state.m_61143_(HORIZONTAL_FACING)));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{SEGMENT, HORIZONTAL_FACING, OPEN, HINGE, POWERED});
   }

   public RenderShape m_7514_(BlockState state) {
      return state.m_61143_(SEGMENT) == 0 ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return state.m_61143_(SEGMENT) == 0 ? new TileEntityEndPirateDoor(pos, state) : null;
   }

   public void m_6861_(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_52780_, boolean p_52781_) {
      boolean flag = level.m_276867_(pos);

      flag = switch (state.m_61143_(SEGMENT)) {
         case 0 -> flag || level.m_276867_(pos.m_7494_()) || level.m_276867_(pos.m_6630_(2));
         case 1 -> flag || level.m_276867_(pos.m_7495_()) || level.m_276867_(pos.m_7494_());
         case 2 -> flag || level.m_276867_(pos.m_7495_()) || level.m_276867_(pos.m_6625_(2));
         default -> flag;
      };
      if (!this.m_49966_().m_60713_(block) && flag != (Boolean)state.m_61143_(POWERED)) {
         if (flag != (Boolean)state.m_61143_(OPEN)) {
         }

         Direction swap = state.m_61143_(HINGE) == DoorHingeSide.LEFT
            ? ((Direction)state.m_61143_(HORIZONTAL_FACING)).m_122427_()
            : ((Direction)state.m_61143_(HORIZONTAL_FACING)).m_122428_();
         BlockPos relative = pos.m_121945_(swap);
         BlockState neighbor = level.m_8055_(relative);
         if (neighbor.m_60734_() == this && state.m_61143_(HINGE) != neighbor.m_61143_(HINGE)) {
            openDoorAt(level, relative, flag, flag);
         }

         openDoorAt(level, pos, flag, flag);
      }
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      boolean open = (Boolean)state.m_61143_(OPEN);
      boolean powered = (Boolean)state.m_61143_(POWERED);
      Direction swap = state.m_61143_(HINGE) == DoorHingeSide.LEFT
         ? ((Direction)state.m_61143_(HORIZONTAL_FACING)).m_122427_()
         : ((Direction)state.m_61143_(HORIZONTAL_FACING)).m_122428_();
      BlockPos relative = pos.m_121945_(swap);
      BlockState neighbor = worldIn.m_8055_(relative);
      if (neighbor.m_60734_() == this && state.m_61143_(HINGE) != neighbor.m_61143_(HINGE)) {
         openDoorAt(worldIn, relative, !open, powered);
      }

      openDoorAt(worldIn, pos, !open, powered);
      return InteractionResult.m_19078_(worldIn.f_46443_);
   }

   public static void openDoorAt(Level worldIn, BlockPos pos, boolean open, boolean powered) {
      TileEntityEndPirateDoor te = getDoorTE(worldIn, pos);
      if (te != null) {
         BlockPos bottom = te.m_58899_();

         for (int i = 0; i <= 2; i++) {
            BlockPos up = bottom.m_6630_(i);
            if (worldIn.m_8055_(up).m_60734_() instanceof BlockEndPirateDoor) {
               worldIn.m_7731_(up, (BlockState)((BlockState)worldIn.m_8055_(up).m_61124_(OPEN, open)).m_61124_(POWERED, powered), 10);
            }
         }
      }
   }

   public static TileEntityEndPirateDoor getDoorTE(Level worldIn, BlockPos pos) {
      for (int i = 0; i <= 2; i++) {
         if (worldIn.m_7702_(pos.m_6625_(i)) instanceof TileEntityEndPirateDoor e) {
            return e;
         }
      }

      return null;
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext p_52739_) {
      BlockPos blockpos = p_52739_.m_8083_();
      Level level = p_52739_.m_43725_();
      if (blockpos.m_123342_() < level.m_151558_() - 1
         && level.m_8055_(blockpos.m_7494_()).m_60629_(p_52739_)
         && level.m_8055_(blockpos.m_6630_(2)).m_60629_(p_52739_)) {
         boolean flag = level.m_276867_(blockpos) || level.m_276867_(blockpos.m_7494_());
         return (BlockState)((BlockState)((BlockState)((BlockState)this.m_49966_().m_61124_(HORIZONTAL_FACING, p_52739_.m_8125_()))
                  .m_61124_(HINGE, this.getHinge(p_52739_)))
               .m_61124_(OPEN, flag))
            .m_61124_(SEGMENT, 0);
      } else {
         return null;
      }
   }

   public void m_6402_(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
      p_52749_.m_7731_(p_52750_.m_7494_(), (BlockState)p_52751_.m_61124_(SEGMENT, 1), 3);
      p_52749_.m_7731_(p_52750_.m_6630_(2), (BlockState)p_52751_.m_61124_(SEGMENT, 2), 3);
   }

   private DoorHingeSide getHinge(BlockPlaceContext p_52805_) {
      BlockGetter blockgetter = p_52805_.m_43725_();
      BlockPos blockpos = p_52805_.m_8083_();
      Direction direction = p_52805_.m_8125_();
      BlockPos blockpos1 = blockpos.m_7494_();
      Direction direction1 = direction.m_122428_();
      BlockPos blockpos2 = blockpos.m_121945_(direction1);
      BlockState blockstate = blockgetter.m_8055_(blockpos2);
      BlockPos blockpos3 = blockpos1.m_121945_(direction1);
      BlockState blockstate1 = blockgetter.m_8055_(blockpos3);
      Direction direction2 = direction.m_122427_();
      BlockPos blockpos4 = blockpos.m_121945_(direction2);
      BlockState blockstate2 = blockgetter.m_8055_(blockpos4);
      BlockPos blockpos5 = blockpos1.m_121945_(direction2);
      BlockState blockstate3 = blockgetter.m_8055_(blockpos5);
      int i = (blockstate.m_60838_(blockgetter, blockpos2) ? -1 : 0)
         + (blockstate1.m_60838_(blockgetter, blockpos3) ? -1 : 0)
         + (blockstate2.m_60838_(blockgetter, blockpos4) ? 1 : 0)
         + (blockstate3.m_60838_(blockgetter, blockpos5) ? 1 : 0);
      boolean flag = blockstate.m_60713_(this) && (Integer)blockstate.m_61143_(SEGMENT) == 0;
      boolean flag1 = blockstate2.m_60713_(this) && (Integer)blockstate2.m_61143_(SEGMENT) == 0;
      if ((!flag || flag1) && i <= 0) {
         if ((!flag1 || flag) && i >= 0) {
            int j = direction.m_122429_();
            int k = direction.m_122431_();
            Vec3 vec3 = p_52805_.m_43720_();
            double d0 = vec3.f_82479_ - blockpos.m_123341_();
            double d1 = vec3.f_82481_ - blockpos.m_123343_();
            return j < 0 && d1 < 0.5 || j > 0 && d1 > 0.5 || k < 0 && d0 > 0.5 || k > 0 && d0 < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
         } else {
            return DoorHingeSide.LEFT;
         }
      } else {
         return DoorHingeSide.RIGHT;
      }
   }

   public boolean m_7898_(BlockState state, LevelReader world, BlockPos pos) {
      return switch (state.m_61143_(SEGMENT)) {
         case 0 -> world.m_8055_(pos.m_7495_()).m_60783_(world, pos.m_7495_(), Direction.UP);
         case 1 -> world.m_8055_(pos.m_7495_()).m_60713_(this) && world.m_8055_(pos.m_7494_()).m_60713_(this);
         case 2 -> world.m_8055_(pos.m_7495_()).m_60713_(this) && world.m_8055_(pos.m_6625_(2)).m_60713_(this);
         default -> false;
      };
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_152180_, BlockState state, BlockEntityType<T> p_152182_) {
      return state.m_61143_(SEGMENT) == 0
         ? m_152132_(p_152182_, (BlockEntityType)AMTileEntityRegistry.END_PIRATE_DOOR.get(), TileEntityEndPirateDoor::commonTick)
         : null;
   }

   public List<ItemStack> m_49635_(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return state.m_61143_(SEGMENT) == 0 ? super.m_49635_(state, builder) : Collections.emptyList();
   }
}
