package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemCosmicCodBucket extends ItemModFishBucket {
   public ItemCosmicCodBucket(Properties builder) {
      super(AMEntityRegistry.COSMIC_COD, Fluids.f_76191_, builder.m_41487_(1));
   }

   @Nonnull
   public InteractionResultHolder<ItemStack> m_7203_(@Nonnull Level level, Player player, @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      BlockHitResult blockhitresult = m_41435_(level, player, Fluid.NONE);
      InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(player, level, itemstack, blockhitresult);
      if (ret != null) {
         return ret;
      } else if (blockhitresult.m_6662_() == Type.MISS) {
         return InteractionResultHolder.m_19098_(itemstack);
      } else if (blockhitresult.m_6662_() != Type.BLOCK) {
         return InteractionResultHolder.m_19098_(itemstack);
      } else {
         BlockPos blockpos = blockhitresult.m_82425_();
         Direction direction = blockhitresult.m_82434_();
         BlockPos blockpos1 = blockpos.m_121945_(direction);
         if (level.m_7966_(player, blockpos) && player.m_36204_(blockpos1, direction, itemstack)) {
            this.m_142131_(player, level, itemstack, blockpos1);
            player.m_36246_(Stats.f_12982_.m_12902_(this));
            return InteractionResultHolder.m_19092_(m_40699_(itemstack, player), level.m_5776_());
         } else {
            return super.m_7203_(level, player, hand);
         }
      }
   }
}
