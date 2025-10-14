package net.minecraft.core.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.block.DispenserBlock;

public class ProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   private final ProjectileItem projectileItem;
   private final ProjectileItem.DispenseConfig dispenseConfig;

   public ProjectileDispenseBehavior(Item $$0) {
      if ($$0 instanceof ProjectileItem $$1) {
         this.projectileItem = $$1;
         this.dispenseConfig = $$1.createDispenseConfig();
      } else {
         throw new IllegalArgumentException($$0 + " not instance of " + ProjectileItem.class.getSimpleName());
      }
   }

   @Override
   public ItemStack execute(BlockSource $$0, ItemStack $$1) {
      ServerLevel $$2 = $$0.level();
      Direction $$3 = $$0.state().getValue(DispenserBlock.FACING);
      Position $$4 = this.dispenseConfig.positionFunction().getDispensePosition($$0, $$3);
      Projectile.spawnProjectileUsingShoot(
         this.projectileItem.asProjectile($$2, $$4, $$1, $$3),
         $$2,
         $$1,
         (double)$$3.getStepX(),
         (double)$$3.getStepY(),
         (double)$$3.getStepZ(),
         this.dispenseConfig.power(),
         this.dispenseConfig.uncertainty()
      );
      $$1.shrink(1);
      return $$1;
   }

   @Override
   protected void playSound(BlockSource $$0) {
      $$0.level().levelEvent(this.dispenseConfig.overrideDispenseEvent().orElse(1002), $$0.pos(), 0);
   }
}
