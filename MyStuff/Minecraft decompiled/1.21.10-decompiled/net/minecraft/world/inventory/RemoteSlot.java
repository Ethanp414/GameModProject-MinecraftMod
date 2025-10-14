package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.world.item.ItemStack;

public interface RemoteSlot {
   RemoteSlot PLACEHOLDER = new RemoteSlot() {
      @Override
      public void receive(HashedStack $$0) {
      }

      @Override
      public void force(ItemStack $$0) {
      }

      @Override
      public boolean matches(ItemStack $$0) {
         return true;
      }
   };

   void force(ItemStack var1);

   void receive(HashedStack var1);

   boolean matches(ItemStack var1);

   public static class Synchronized implements RemoteSlot {
      private final HashedPatchMap.HashGenerator hasher;
      @Nullable
      private ItemStack remoteStack = null;
      @Nullable
      private HashedStack remoteHash = null;

      public Synchronized(HashedPatchMap.HashGenerator $$0) {
         this.hasher = $$0;
      }

      @Override
      public void force(ItemStack $$0) {
         this.remoteStack = $$0.copy();
         this.remoteHash = null;
      }

      @Override
      public void receive(HashedStack $$0) {
         this.remoteStack = null;
         this.remoteHash = $$0;
      }

      @Override
      public boolean matches(ItemStack $$0) {
         if (this.remoteStack != null) {
            return ItemStack.matches(this.remoteStack, $$0);
         } else if (this.remoteHash != null && this.remoteHash.matches($$0, this.hasher)) {
            this.remoteStack = $$0.copy();
            return true;
         } else {
            return false;
         }
      }

      public void copyFrom(RemoteSlot.Synchronized $$0) {
         this.remoteStack = $$0.remoteStack;
         this.remoteHash = $$0.remoteHash;
      }
   }
}
