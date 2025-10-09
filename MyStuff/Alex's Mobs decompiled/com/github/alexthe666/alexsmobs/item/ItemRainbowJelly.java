package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemRainbowJelly extends Item {
   public ItemRainbowJelly(Properties tab) {
      super(tab);
   }

   public InteractionResult m_6880_(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
      int i = RainbowUtil.getRainbowTypeFromStack(stack);
      if (RainbowUtil.getRainbowType(target) == i) {
         return InteractionResult.PASS;
      } else {
         RainbowUtil.setRainbowType(target, i);
         RandomSource random = playerIn.m_217043_();

         for (int j = 0; j < 6 + random.m_188503_(3); j++) {
            double d2 = random.m_188583_() * 0.02;
            double d0 = random.m_188583_() * 0.02;
            double d1 = random.m_188583_() * 0.02;
            playerIn.m_9236_()
               .m_7106_(
                  new ItemParticleOption(ParticleTypes.f_123752_, stack),
                  target.m_20185_() + random.m_188501_() * target.m_20205_() - target.m_20205_() * 0.5,
                  target.m_20186_() + target.m_20206_() * 0.5F + random.m_188501_() * target.m_20206_() * 0.5F,
                  target.m_20189_() + random.m_188501_() * target.m_20205_() - target.m_20205_() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }

         target.m_146850_(GameEvent.f_223698_);
         target.m_5496_(SoundEvents.f_12470_, 1.0F, target.m_6100_());
         if (!playerIn.m_7500_()) {
            stack.m_41774_(1);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public ItemStack m_5922_(ItemStack st, Level level, LivingEntity e) {
      RainbowUtil.setRainbowType(e, RainbowUtil.getRainbowTypeFromStack(st));
      return this.m_41472_() ? e.m_5584_(level, st) : st;
   }

   public int m_8105_(ItemStack stack) {
      return stack.m_41720_().m_41472_() ? 64 : 0;
   }

   public boolean m_5812_(ItemStack stack) {
      return super.m_5812_(stack) || RainbowUtil.getRainbowTypeFromStack(stack) > 1;
   }

   public static enum RainbowType {
      RAINBOW,
      TRANS,
      NONBI,
      BI,
      ACE,
      WEEZER,
      BRAZIL;

      public static ItemRainbowJelly.RainbowType getFromString(String name) {
         if (name.contains("nonbi") || name.contains("non-bi")) {
            return NONBI;
         } else if (name.contains("trans")) {
            return TRANS;
         } else if (name.contains("bi")) {
            return BI;
         } else if (name.contains("asexual") || name.contains("ace")) {
            return ACE;
         } else if (name.contains("weezer")) {
            return WEEZER;
         } else {
            return name.contains("brazil") ? BRAZIL : RAINBOW;
         }
      }
   }
}
