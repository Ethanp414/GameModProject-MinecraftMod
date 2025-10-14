package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public abstract class ProjectileWeaponItem extends Item {
   public static final Predicate<ItemStack> ARROW_ONLY = $$0 -> $$0.is(ItemTags.ARROWS);
   public static final Predicate<ItemStack> ARROW_OR_FIREWORK = ARROW_ONLY.or($$0 -> $$0.is(Items.FIREWORK_ROCKET));

   public ProjectileWeaponItem(Item.Properties $$0) {
      super($$0);
   }

   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return this.getAllSupportedProjectiles();
   }

   public abstract Predicate<ItemStack> getAllSupportedProjectiles();

   public static ItemStack getHeldProjectile(LivingEntity $$0, Predicate<ItemStack> $$1) {
      if ($$1.test($$0.getItemInHand(InteractionHand.OFF_HAND))) {
         return $$0.getItemInHand(InteractionHand.OFF_HAND);
      } else {
         return $$1.test($$0.getItemInHand(InteractionHand.MAIN_HAND)) ? $$0.getItemInHand(InteractionHand.MAIN_HAND) : ItemStack.EMPTY;
      }
   }

   public abstract int getDefaultProjectileRange();

   protected void shoot(
      ServerLevel $$0,
      LivingEntity $$1,
      InteractionHand $$2,
      ItemStack $$3,
      List<ItemStack> $$4,
      float $$5,
      float $$6,
      boolean $$7,
      @Nullable LivingEntity $$8
   ) {
      float $$9 = EnchantmentHelper.processProjectileSpread($$0, $$3, $$1, 0.0F);
      float $$10 = $$4.size() == 1 ? 0.0F : 2.0F * $$9 / (float)($$4.size() - 1);
      float $$11 = (float)(($$4.size() - 1) % 2) * $$10 / 2.0F;
      float $$12 = 1.0F;

      for(int $$13 = 0; $$13 < $$4.size(); ++$$13) {
         ItemStack $$14 = (ItemStack)$$4.get($$13);
         if (!$$14.isEmpty()) {
            float $$15 = $$11 + $$12 * (float)(($$13 + 1) / 2) * $$10;
            $$12 = -$$12;
            int $$16 = $$13;
            Projectile.spawnProjectile(
               this.createProjectile($$0, $$1, $$3, $$14, $$7), $$0, $$14, $$6x -> this.shootProjectile($$1, $$6x, $$16, $$5, $$6, $$15, $$8)
            );
            $$3.hurtAndBreak(this.getDurabilityUse($$14), $$1, $$2.asEquipmentSlot());
            if ($$3.isEmpty()) {
               break;
            }
         }
      }
   }

   protected int getDurabilityUse(ItemStack $$0) {
      return 1;
   }

   protected abstract void shootProjectile(LivingEntity var1, Projectile var2, int var3, float var4, float var5, float var6, @Nullable LivingEntity var7);

   protected Projectile createProjectile(Level $$0, LivingEntity $$1, ItemStack $$2, ItemStack $$3, boolean $$4) {
      Item var8 = $$3.getItem();
      ArrowItem $$6 = var8 instanceof ArrowItem $$5 ? $$5 : (ArrowItem)Items.ARROW;
      AbstractArrow $$7 = $$6.createArrow($$0, $$3, $$1, $$2);
      if ($$4) {
         $$7.setCritArrow(true);
      }

      return $$7;
   }

   protected static List<ItemStack> draw(ItemStack $$0, ItemStack $$1, LivingEntity $$2) {
      if ($$1.isEmpty()) {
         return List.of();
      } else {
         Level $$6 = $$2.level();
         int $$4 = $$6 instanceof ServerLevel $$3 ? EnchantmentHelper.processProjectileCount($$3, $$0, $$2, 1) : 1;
         List<ItemStack> $$5 = new ArrayList($$4);
         ItemStack $$6x = $$1.copy();

         for(int $$7 = 0; $$7 < $$4; ++$$7) {
            ItemStack $$8 = useAmmo($$0, $$7 == 0 ? $$1 : $$6x, $$2, $$7 > 0);
            if (!$$8.isEmpty()) {
               $$5.add($$8);
            }
         }

         return $$5;
      }
   }

   protected static ItemStack useAmmo(ItemStack $$0, ItemStack $$1, LivingEntity $$2, boolean $$3) {
      int var10000;
      label28: {
         if (!$$3 && !$$2.hasInfiniteMaterials()) {
            Level $$8 = $$2.level();
            if ($$8 instanceof ServerLevel $$4) {
               var10000 = EnchantmentHelper.processAmmoUse($$4, $$0, $$1, 1);
               break label28;
            }
         }

         var10000 = 0;
      }

      int $$5 = var10000;
      if ($$5 > $$1.getCount()) {
         return ItemStack.EMPTY;
      } else if ($$5 == 0) {
         ItemStack $$6 = $$1.copyWithCount(1);
         $$6.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
         return $$6;
      } else {
         ItemStack $$7 = $$1.split($$5);
         if ($$1.isEmpty() && $$2 instanceof Player $$8) {
            $$8.getInventory().removeItem($$1);
         }

         return $$7;
      }
   }
}
