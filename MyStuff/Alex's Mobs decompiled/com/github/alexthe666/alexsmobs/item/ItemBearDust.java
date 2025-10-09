package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemBearDust extends Item implements CustomTabBehavior {
   private final Random random = new Random();

   public ItemBearDust(Properties props) {
      super(props);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.m_21120_(handIn);
      playerIn.m_146850_(GameEvent.f_223698_);
      worldIn.m_6263_(
         null,
         playerIn.m_20185_(),
         playerIn.m_20186_(),
         playerIn.m_20189_(),
         (SoundEvent)AMSoundRegistry.BEAR_DUST.get(),
         SoundSource.PLAYERS,
         0.75F,
         this.random.nextFloat() * 0.2F + 0.9F
      );
      playerIn.m_36335_().m_41524_(this, 3);
      playerIn.m_36246_(Stats.f_12982_.m_12902_(this));
      return InteractionResultHolder.m_19090_(itemstack);
   }

   @Override
   public void fillItemCategory(Output contents) {
   }
}
