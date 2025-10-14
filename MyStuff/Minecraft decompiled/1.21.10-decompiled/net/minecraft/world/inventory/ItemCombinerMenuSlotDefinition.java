package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerMenuSlotDefinition {
   private final List<ItemCombinerMenuSlotDefinition.SlotDefinition> slots;
   private final ItemCombinerMenuSlotDefinition.SlotDefinition resultSlot;

   ItemCombinerMenuSlotDefinition(List<ItemCombinerMenuSlotDefinition.SlotDefinition> $$0, ItemCombinerMenuSlotDefinition.SlotDefinition $$1) {
      if (!$$0.isEmpty() && !$$1.equals(ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY)) {
         this.slots = $$0;
         this.resultSlot = $$1;
      } else {
         throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
      }
   }

   public static ItemCombinerMenuSlotDefinition.Builder create() {
      return new ItemCombinerMenuSlotDefinition.Builder();
   }

   public ItemCombinerMenuSlotDefinition.SlotDefinition getSlot(int $$0) {
      return (ItemCombinerMenuSlotDefinition.SlotDefinition)this.slots.get($$0);
   }

   public ItemCombinerMenuSlotDefinition.SlotDefinition getResultSlot() {
      return this.resultSlot;
   }

   public List<ItemCombinerMenuSlotDefinition.SlotDefinition> getSlots() {
      return this.slots;
   }

   public int getNumOfInputSlots() {
      return this.slots.size();
   }

   public int getResultSlotIndex() {
      return this.getNumOfInputSlots();
   }

   public static class Builder {
      private final List<ItemCombinerMenuSlotDefinition.SlotDefinition> inputSlots = new ArrayList();
      private ItemCombinerMenuSlotDefinition.SlotDefinition resultSlot = ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY;

      public ItemCombinerMenuSlotDefinition.Builder withSlot(int $$0, int $$1, int $$2, Predicate<ItemStack> $$3) {
         this.inputSlots.add(new ItemCombinerMenuSlotDefinition.SlotDefinition($$0, $$1, $$2, $$3));
         return this;
      }

      public ItemCombinerMenuSlotDefinition.Builder withResultSlot(int $$0, int $$1, int $$2) {
         this.resultSlot = new ItemCombinerMenuSlotDefinition.SlotDefinition($$0, $$1, $$2, $$0x -> false);
         return this;
      }

      public ItemCombinerMenuSlotDefinition build() {
         int $$0 = this.inputSlots.size();

         for(int $$1 = 0; $$1 < $$0; ++$$1) {
            ItemCombinerMenuSlotDefinition.SlotDefinition $$2 = (ItemCombinerMenuSlotDefinition.SlotDefinition)this.inputSlots.get($$1);
            if ($$2.slotIndex != $$1) {
               throw new IllegalArgumentException("Expected input slots to have continous indexes");
            }
         }

         if (this.resultSlot.slotIndex != $$0) {
            throw new IllegalArgumentException("Expected result slot index to follow last input slot");
         } else {
            return new ItemCombinerMenuSlotDefinition(this.inputSlots, this.resultSlot);
         }
      }
   }

   public static record SlotDefinition(int slotIndex, int x, int y, Predicate<ItemStack> mayPlace) {
      final int slotIndex;
      static final ItemCombinerMenuSlotDefinition.SlotDefinition EMPTY = new ItemCombinerMenuSlotDefinition.SlotDefinition(0, 0, 0, $$0 -> true);
   }
}
