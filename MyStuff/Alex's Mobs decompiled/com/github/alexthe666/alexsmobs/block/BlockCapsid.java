package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockCapsid extends BaseEntityBlock {
   public static final DirectionProperty HORIZONTAL_FACING = HorizontalDirectionalBlock.f_54117_;

   public BlockCapsid() {
      super(
         Properties.m_284310_()
            .m_284180_(MapColor.f_283889_)
            .m_60955_()
            .m_60922_(BlockCapsid::spawnOption)
            .m_60924_(BlockCapsid::isntSolid)
            .m_60918_(SoundType.f_56744_)
            .m_60953_(state -> 5)
            .m_60999_()
            .m_60978_(1.5F)
      );
   }

   public BlockState m_6843_(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.m_61124_(HORIZONTAL_FACING, p_185499_2_.m_55954_((Direction)p_185499_1_.m_61143_(HORIZONTAL_FACING)));
   }

   public BlockState m_6943_(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.m_60717_(p_185471_2_.m_54846_((Direction)p_185471_1_.m_61143_(HORIZONTAL_FACING)));
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)this.m_49966_().m_61124_(HORIZONTAL_FACING, context.m_8125_());
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{HORIZONTAL_FACING});
   }

   private static Boolean spawnOption(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
      return false;
   }

   private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean m_6104_(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
      return p_200122_2_.m_60734_() == this ? true : super.m_6104_(p_200122_1_, p_200122_2_, p_200122_3_);
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      ItemStack heldItem = player.m_21120_(handIn);
      if (worldIn.m_7702_(pos) instanceof TileEntityCapsid && !player.m_6144_() && heldItem.m_41720_() != this.m_5456_()) {
         TileEntityCapsid capsid = (TileEntityCapsid)worldIn.m_7702_(pos);
         ItemStack copy = heldItem.m_41777_();
         copy.m_41764_(1);
         if (capsid.m_8020_(0).m_41619_()) {
            capsid.m_6836_(0, copy);
            if (!player.m_7500_()) {
               heldItem.m_41774_(1);
            }

            return InteractionResult.SUCCESS;
         } else if (ItemStack.m_41656_(capsid.m_8020_(0), copy) && capsid.m_8020_(0).m_41741_() > capsid.m_8020_(0).m_41613_() + copy.m_41613_()) {
            capsid.m_8020_(0).m_41769_(1);
            if (!player.m_7500_()) {
               heldItem.m_41774_(1);
            }

            return InteractionResult.SUCCESS;
         } else {
            m_49840_(worldIn, pos, capsid.m_8020_(0).m_41777_());
            capsid.m_6836_(0, ItemStack.f_41583_);
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public void m_6810_(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      BlockEntity tileentity = worldIn.m_7702_(pos);
      if (tileentity instanceof TileEntityCapsid) {
         Containers.m_19002_(worldIn, pos, (TileEntityCapsid)tileentity);
         worldIn.m_46717_(pos, this);
      }

      super.m_6810_(state, worldIn, pos, newState, isMoving);
   }

   public RenderShape m_7514_(BlockState p_149645_1_) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new TileEntityCapsid(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return m_152132_(p_152182_, (BlockEntityType)AMTileEntityRegistry.CAPSID.get(), TileEntityCapsid::commonTick);
   }
}
