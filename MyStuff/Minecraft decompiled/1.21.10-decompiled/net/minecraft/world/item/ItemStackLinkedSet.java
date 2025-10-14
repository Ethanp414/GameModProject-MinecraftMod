package net.minecraft.world.item;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class ItemStackLinkedSet {
   private static final Strategy<? super ItemStack> TYPE_AND_TAG = new Strategy<ItemStack>() {
      public int hashCode(@Nullable ItemStack $$0) {
         return ItemStack.hashItemAndComponents($$0);
      }

      public boolean equals(@Nullable ItemStack $$0, @Nullable ItemStack $$1) {
         return $$0 == $$1 || $$0 != null && $$1 != null && $$0.isEmpty() == $$1.isEmpty() && ItemStack.isSameItemSameComponents($$0, $$1);
      }
   };

   public static Set<ItemStack> createTypeAndComponentsSet() {
      return new ObjectLinkedOpenCustomHashSet<>(TYPE_AND_TAG);
   }
}
