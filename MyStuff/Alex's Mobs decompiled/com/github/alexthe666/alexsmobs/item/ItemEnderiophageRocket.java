package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityEnderiophageRocket;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemEnderiophageRocket extends Item {
   public ItemEnderiophageRocket(Properties group) {
      super(group);
   }

   public InteractionResult m_6225_(UseOnContext context) {
      Level world = context.m_43725_();
      if (!world.f_46443_) {
         ItemStack itemstack = context.m_43722_();
         Vec3 vector3d = context.m_43720_();
         Direction direction = context.m_43719_();
         FireworkRocketEntity fireworkrocketentity = new EntityEnderiophageRocket(
            world,
            context.m_43723_(),
            vector3d.f_82479_ + direction.m_122429_() * 0.15,
            vector3d.f_82480_ + direction.m_122430_() * 0.15,
            vector3d.f_82481_ + direction.m_122431_() * 0.15,
            itemstack
         );
         world.m_7967_(fireworkrocketentity);
         if (!context.m_43723_().m_7500_()) {
            itemstack.m_41774_(1);
         }
      }

      return InteractionResult.m_19078_(world.f_46443_);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player playerIn, InteractionHand handIn) {
      if (playerIn.m_21255_()) {
         ItemStack itemstack = playerIn.m_21120_(handIn);
         if (!worldIn.f_46443_) {
            worldIn.m_7967_(new EntityEnderiophageRocket(worldIn, itemstack, playerIn));
            if (!playerIn.m_150110_().f_35937_) {
               itemstack.m_41774_(1);
            }
         }

         return InteractionResultHolder.m_19092_(playerIn.m_21120_(handIn), worldIn.m_5776_());
      } else {
         return InteractionResultHolder.m_19098_(playerIn.m_21120_(handIn));
      }
   }
}
