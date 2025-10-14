package net.minecraft.world.entity;

import net.minecraft.world.item.ItemStack;

public class Crackiness {
   public static final Crackiness GOLEM = new Crackiness(0.75F, 0.5F, 0.25F);
   public static final Crackiness WOLF_ARMOR = new Crackiness(0.95F, 0.69F, 0.32F);
   private final float fractionLow;
   private final float fractionMedium;
   private final float fractionHigh;

   private Crackiness(float $$0, float $$1, float $$2) {
      this.fractionLow = $$0;
      this.fractionMedium = $$1;
      this.fractionHigh = $$2;
   }

   public Crackiness.Level byFraction(float $$0) {
      if ($$0 < this.fractionHigh) {
         return Crackiness.Level.HIGH;
      } else if ($$0 < this.fractionMedium) {
         return Crackiness.Level.MEDIUM;
      } else {
         return $$0 < this.fractionLow ? Crackiness.Level.LOW : Crackiness.Level.NONE;
      }
   }

   public Crackiness.Level byDamage(ItemStack $$0) {
      return !$$0.isDamageableItem() ? Crackiness.Level.NONE : this.byDamage($$0.getDamageValue(), $$0.getMaxDamage());
   }

   public Crackiness.Level byDamage(int $$0, int $$1) {
      return this.byFraction((float)($$1 - $$0) / (float)$$1);
   }

   public static enum Level {
      NONE,
      LOW,
      MEDIUM,
      HIGH;
   }
}
