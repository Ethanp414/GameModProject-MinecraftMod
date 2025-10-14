package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public abstract class ThrowablePotionItem extends PotionItem implements ProjectileItem {
   public static float PROJECTILE_SHOOT_POWER = 0.5F;

   public ThrowablePotionItem(Item.Properties $$0) {
      super($$0);
   }

   @Override
   public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
      ItemStack $$3 = $$1.getItemInHand($$2);
      if ($$0 instanceof ServerLevel $$4) {
         Projectile.spawnProjectileFromRotation(this::createPotion, $$4, $$3, $$1, -20.0F, PROJECTILE_SHOOT_POWER, 1.0F);
      }

      $$1.awardStat(Stats.ITEM_USED.get(this));
      $$3.consume(1, $$1);
      return InteractionResult.SUCCESS;
   }

   protected abstract AbstractThrownPotion createPotion(ServerLevel var1, LivingEntity var2, ItemStack var3);

   protected abstract AbstractThrownPotion createPotion(Level var1, Position var2, ItemStack var3);

   @Override
   public Projectile asProjectile(Level $$0, Position $$1, ItemStack $$2, Direction $$3) {
      return this.createPotion($$0, $$1, $$2);
   }

   @Override
   public ProjectileItem.DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.builder()
         .uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5F)
         .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25F)
         .build();
   }
}
