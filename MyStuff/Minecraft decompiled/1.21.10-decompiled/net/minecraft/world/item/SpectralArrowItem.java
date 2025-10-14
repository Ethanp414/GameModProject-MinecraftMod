package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;

public class SpectralArrowItem extends ArrowItem {
   public SpectralArrowItem(Item.Properties $$0) {
      super($$0);
   }

   @Override
   public AbstractArrow createArrow(Level $$0, ItemStack $$1, LivingEntity $$2, @Nullable ItemStack $$3) {
      return new SpectralArrow($$0, $$2, $$1.copyWithCount(1), $$3);
   }

   @Override
   public Projectile asProjectile(Level $$0, Position $$1, ItemStack $$2, Direction $$3) {
      SpectralArrow $$4 = new SpectralArrow($$0, $$1.x(), $$1.y(), $$1.z(), $$2.copyWithCount(1), null);
      $$4.pickup = AbstractArrow.Pickup.ALLOWED;
      return $$4;
   }
}
