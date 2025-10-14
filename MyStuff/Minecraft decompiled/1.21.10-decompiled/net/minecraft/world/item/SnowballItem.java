package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;

public class SnowballItem extends Item implements ProjectileItem {
   public static float PROJECTILE_SHOOT_POWER = 1.5F;

   public SnowballItem(Item.Properties $$0) {
      super($$0);
   }

   @Override
   public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
      ItemStack $$3 = $$1.getItemInHand($$2);
      $$0.playSound(
         null, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / ($$0.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      if ($$0 instanceof ServerLevel $$4) {
         Projectile.spawnProjectileFromRotation(Snowball::new, $$4, $$3, $$1, 0.0F, PROJECTILE_SHOOT_POWER, 1.0F);
      }

      $$1.awardStat(Stats.ITEM_USED.get(this));
      $$3.consume(1, $$1);
      return InteractionResult.SUCCESS;
   }

   @Override
   public Projectile asProjectile(Level $$0, Position $$1, ItemStack $$2, Direction $$3) {
      return new Snowball($$0, $$1.x(), $$1.y(), $$1.z(), $$2);
   }
}
