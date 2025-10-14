package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SpyglassItem extends Item {
   public static final int USE_DURATION = 1200;
   public static final float ZOOM_FOV_MODIFIER = 0.1F;

   public SpyglassItem(Item.Properties $$0) {
      super($$0);
   }

   @Override
   public int getUseDuration(ItemStack $$0, LivingEntity $$1) {
      return 1200;
   }

   @Override
   public ItemUseAnimation getUseAnimation(ItemStack $$0) {
      return ItemUseAnimation.SPYGLASS;
   }

   @Override
   public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
      $$1.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
      $$1.awardStat(Stats.ITEM_USED.get(this));
      return ItemUtils.startUsingInstantly($$0, $$1, $$2);
   }

   @Override
   public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
      this.stopUsing($$2);
      return $$0;
   }

   @Override
   public boolean releaseUsing(ItemStack $$0, Level $$1, LivingEntity $$2, int $$3) {
      this.stopUsing($$2);
      return true;
   }

   private void stopUsing(LivingEntity $$0) {
      $$0.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
   }
}
