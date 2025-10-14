package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

public class MeleeAttack {
   public static <T extends Mob> OneShot<T> create(int $$0) {
      return create($$0x -> true, $$0);
   }

   public static <T extends Mob> OneShot<T> create(Predicate<T> $$0, int $$1) {
      return BehaviorBuilder.create(
         $$2 -> $$2.<MemoryAccessor, MemoryAccessor, MemoryAccessor, MemoryAccessor>group(
                  $$2.registered(MemoryModuleType.LOOK_TARGET),
                  $$2.present(MemoryModuleType.ATTACK_TARGET),
                  $$2.absent(MemoryModuleType.ATTACK_COOLING_DOWN),
                  $$2.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
               )
               .apply(
                  $$2,
                  ($$3, $$4, $$5, $$6) -> ($$7, $$8, $$9) -> {
                        LivingEntity $$10 = $$2.get($$4);
                        if ($$0.test($$8)
                           && !isHoldingUsableProjectileWeapon($$8)
                           && $$8.isWithinMeleeAttackRange($$10)
                           && $$2.<NearestVisibleLivingEntities>get($$6).contains($$10)) {
                           $$3.set(new EntityTracker($$10, true));
                           $$8.swing(InteractionHand.MAIN_HAND);
                           $$8.doHurtTarget($$7, $$10);
                           $$5.setWithExpiry(true, (long)$$1);
                           return true;
                        } else {
                           return false;
                        }
                     }
               )
      );
   }

   private static boolean isHoldingUsableProjectileWeapon(Mob $$0) {
      return $$0.isHolding($$1 -> {
         Item $$2 = $$1.getItem();
         return $$2 instanceof ProjectileWeaponItem && $$0.canFireProjectileWeapon((ProjectileWeaponItem)$$2);
      });
   }
}
