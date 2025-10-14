package net.minecraft.world.entity;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface SlotAccess {
   SlotAccess NULL = new SlotAccess() {
      @Override
      public ItemStack get() {
         return ItemStack.EMPTY;
      }

      @Override
      public boolean set(ItemStack $$0) {
         return false;
      }
   };

   static SlotAccess of(final Supplier<ItemStack> $$0, final Consumer<ItemStack> $$1) {
      return new SlotAccess() {
         @Override
         public ItemStack get() {
            return (ItemStack)$$0.get();
         }

         @Override
         public boolean set(ItemStack $$0x) {
            $$1.accept($$0);
            return true;
         }
      };
   }

   static SlotAccess forContainer(final Container $$0, final int $$1, final Predicate<ItemStack> $$2) {
      return new SlotAccess() {
         @Override
         public ItemStack get() {
            return $$0.getItem($$1);
         }

         @Override
         public boolean set(ItemStack $$0x) {
            if (!$$2.test($$0)) {
               return false;
            } else {
               $$0.setItem($$1, $$0);
               return true;
            }
         }
      };
   }

   static SlotAccess forContainer(Container $$0, int $$1) {
      return forContainer($$0, $$1, $$0x -> true);
   }

   static SlotAccess forEquipmentSlot(final LivingEntity $$0, final EquipmentSlot $$1, final Predicate<ItemStack> $$2) {
      return new SlotAccess() {
         @Override
         public ItemStack get() {
            return $$0.getItemBySlot($$1);
         }

         @Override
         public boolean set(ItemStack $$0x) {
            if (!$$2.test($$0)) {
               return false;
            } else {
               $$0.setItemSlot($$1, $$0);
               return true;
            }
         }
      };
   }

   static SlotAccess forEquipmentSlot(LivingEntity $$0, EquipmentSlot $$1) {
      return forEquipmentSlot($$0, $$1, $$0x -> true);
   }

   ItemStack get();

   boolean set(ItemStack var1);
}
