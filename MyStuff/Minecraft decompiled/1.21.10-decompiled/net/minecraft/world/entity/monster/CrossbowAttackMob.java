package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface CrossbowAttackMob extends RangedAttackMob {
   void setChargingCrossbow(boolean var1);

   @Nullable
   LivingEntity getTarget();

   void onCrossbowAttackPerformed();

   default void performCrossbowAttack(LivingEntity $$0, float $$1) {
      InteractionHand $$2 = ProjectileUtil.getWeaponHoldingHand($$0, Items.CROSSBOW);
      ItemStack $$3 = $$0.getItemInHand($$2);
      Item var6 = $$3.getItem();
      if (var6 instanceof CrossbowItem $$4) {
         $$4.performShooting($$0.level(), $$0, $$2, $$3, $$1, (float)(14 - $$0.level().getDifficulty().getId() * 4), this.getTarget());
      }

      this.onCrossbowAttackPerformed();
   }
}
