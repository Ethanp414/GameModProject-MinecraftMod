package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import com.github.alexthe666.alexsmobs.message.MessageUpdateTransmutablesToDisplay;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockTransmutationTable extends BaseEntityBlock implements AMSpecialRenderBlock {
   private static final Component CONTAINER_TITLE = Component.m_237115_("alexsmobs.container.transmutation_table");
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;
   private static final VoxelShape BASE_AABB = Block.m_49796_(1.0, 0.0, 1.0, 15.0, 5.0, 15.0);
   private static final VoxelShape ARMS_NS = Block.m_49796_(1.0, 5.0, 5.5, 15.0, 16.0, 10.5);
   private static final VoxelShape ARMS_EW = Block.m_49796_(5.5, 5.0, 1.0, 10.5, 16.0, 15.0);
   private static final VoxelShape NS_AABB = Shapes.m_83110_(BASE_AABB, ARMS_NS);
   private static final VoxelShape EW_AABB = Shapes.m_83110_(BASE_AABB, ARMS_EW);

   public BlockTransmutationTable() {
      super(
         Properties.m_284310_()
            .m_278166_(PushReaction.BLOCK)
            .m_284268_(DyeColor.BLACK)
            .m_60955_()
            .m_60953_(block -> 2)
            .m_60991_((block, world, pos) -> true)
            .m_60918_(SoundType.f_56742_)
            .m_60978_(1.0F)
            .m_60999_()
      );
      this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(FACING, Direction.NORTH));
   }

   public VoxelShape m_5940_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return ((Direction)state.m_61143_(FACING)).m_122434_() == Axis.Z ? NS_AABB : EW_AABB;
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new TileEntityTransmutationTable(pos, state);
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

   public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
      if (level.f_46443_) {
         return InteractionResult.SUCCESS;
      } else {
         player.m_5893_(state.m_60750_(level, pos));
         player.m_36220_(Stats.f_12977_);
         if (level.m_7702_(pos) instanceof TileEntityTransmutationTable table) {
            AlexsMobs.sendMSGToAll(
               new MessageUpdateTransmutablesToDisplay(player.m_19879_(), table.getPossibility(0), table.getPossibility(1), table.getPossibility(2))
            );
         }

         return InteractionResult.CONSUME;
      }
   }

   public MenuProvider m_7246_(BlockState state, Level level, BlockPos pos) {
      BlockEntity te = level.m_7702_(pos);
      return new SimpleMenuProvider(
         (i, inv, player) -> new MenuTransmutationTable(
            i, inv, ContainerLevelAccess.m_39289_(level, pos), player, te instanceof TileEntityTransmutationTable ? (TileEntityTransmutationTable)te : null
         ),
         CONTAINER_TITLE
      );
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return m_152132_(p_152182_, (BlockEntityType)AMTileEntityRegistry.TRANSMUTATION_TABLE.get(), TileEntityTransmutationTable::commonTick);
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      if (AMConfig.transmutingTableExplodes) {
         level.m_255391_(null, pos.m_123341_() + 0.5, pos.m_123342_() + 0.5, pos.m_123343_() + 0.5, 3.0F, false, ExplosionInteraction.BLOCK);
      }

      return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
   }
}
