package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityCockroachEgg;
import com.github.alexthe666.alexsmobs.entity.EntityEmuEgg;
import java.util.Random;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemAnimalEgg extends Item {
   private final Random random = new Random();

   public ItemAnimalEgg(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.m_21120_(handIn);
      playerIn.m_146850_(GameEvent.f_223698_);
      worldIn.m_6263_(
         (Player)null,
         playerIn.m_20185_(),
         playerIn.m_20186_(),
         playerIn.m_20189_(),
         SoundEvents.f_11877_,
         SoundSource.PLAYERS,
         0.5F,
         0.4F / (this.random.nextFloat() * 0.4F + 0.8F)
      );
      if (!worldIn.f_46443_) {
         ThrowableItemProjectile eggentity;
         if (this == AMItemRegistry.EMU_EGG.get()) {
            eggentity = new EntityEmuEgg(worldIn, playerIn);
         } else {
            eggentity = new EntityCockroachEgg(worldIn, playerIn);
         }

         eggentity.m_37446_(itemstack);
         eggentity.m_37251_(playerIn, playerIn.m_146909_(), playerIn.m_146908_(), 0.0F, 1.5F, 1.0F);
         worldIn.m_7967_(eggentity);
      }

      playerIn.m_36246_(Stats.f_12982_.m_12902_(this));
      if (!playerIn.m_150110_().f_35937_) {
         itemstack.m_41774_(1);
      }

      return InteractionResultHolder.m_19092_(itemstack, worldIn.m_5776_());
   }
}
