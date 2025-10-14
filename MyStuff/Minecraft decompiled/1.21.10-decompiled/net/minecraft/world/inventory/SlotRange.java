package net.minecraft.world.inventory;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.StringRepresentable;

public interface SlotRange extends StringRepresentable {
   IntList slots();

   default int size() {
      return this.slots().size();
   }

   static SlotRange of(final String $$0, final IntList $$1) {
      return new SlotRange() {
         @Override
         public IntList slots() {
            return $$1;
         }

         @Override
         public String getSerializedName() {
            return $$0;
         }

         public String toString() {
            return $$0;
         }
      };
   }
}
