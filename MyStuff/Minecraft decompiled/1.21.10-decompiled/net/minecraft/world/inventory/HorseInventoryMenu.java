package net.minecraft.world.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HorseInventoryMenu extends AbstractContainerMenu {
   private static final ResourceLocation SADDLE_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/saddle");
   private static final ResourceLocation LLAMA_ARMOR_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/llama_armor");
   private static final ResourceLocation ARMOR_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/horse_armor");
   private final Container horseContainer;
   private final AbstractHorse horse;
   private static final int SLOT_SADDLE = 0;
   private static final int SLOT_BODY_ARMOR = 1;
   private static final int SLOT_HORSE_INVENTORY_START = 2;

   public HorseInventoryMenu(int $$0, Inventory $$1, Container $$2, final AbstractHorse $$3, int $$4) {
      super(null, $$0);
      this.horseContainer = $$2;
      this.horse = $$3;
      $$2.startOpen($$1.player);
      Container $$5 = $$3.createEquipmentSlotContainer(EquipmentSlot.SADDLE);
      this.addSlot(new ArmorSlot(this, $$5, $$3, EquipmentSlot.SADDLE, 0, 8, 18, SADDLE_SLOT_SPRITE) {
         @Override
         public boolean isActive() {
            return $$3.canUseSlot(EquipmentSlot.SADDLE) && $$3.getType().is(EntityTypeTags.CAN_EQUIP_SADDLE);
         }
      });
      final boolean $$6 = $$3 instanceof Llama;
      ResourceLocation $$7 = $$6 ? LLAMA_ARMOR_SLOT_SPRITE : ARMOR_SLOT_SPRITE;
      Container $$8 = $$3.createEquipmentSlotContainer(EquipmentSlot.BODY);
      this.addSlot(new ArmorSlot(this, $$8, $$3, EquipmentSlot.BODY, 0, 8, 36, $$7) {
         @Override
         public boolean isActive() {
            return $$3.canUseSlot(EquipmentSlot.BODY) && ($$3.getType().is(EntityTypeTags.CAN_WEAR_HORSE_ARMOR) || $$6);
         }
      });
      if ($$4 > 0) {
         for(int $$9 = 0; $$9 < 3; ++$$9) {
            for(int $$10 = 0; $$10 < $$4; ++$$10) {
               this.addSlot(new Slot($$2, $$10 + $$9 * $$4, 80 + $$10 * 18, 18 + $$9 * 18));
            }
         }
      }

      this.addStandardInventorySlots($$1, 8, 84);
   }

   @Override
   public boolean stillValid(Player $$0) {
      return !this.horse.hasInventoryChanged(this.horseContainer)
         && this.horseContainer.stillValid($$0)
         && this.horse.isAlive()
         && $$0.canInteractWithEntity(this.horse, 4.0);
   }

   @Override
   public ItemStack quickMoveStack(Player $$0, int $$1) {
      ItemStack $$2 = ItemStack.EMPTY;
      Slot $$3 = this.slots.get($$1);
      if ($$3 != null && $$3.hasItem()) {
         ItemStack $$4 = $$3.getItem();
         $$2 = $$4.copy();
         int $$5 = 2 + this.horseContainer.getContainerSize();
         if ($$1 < $$5) {
            if (!this.moveItemStackTo($$4, $$5, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(1).mayPlace($$4) && !this.getSlot(1).hasItem()) {
            if (!this.moveItemStackTo($$4, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(0).mayPlace($$4) && !this.getSlot(0).hasItem()) {
            if (!this.moveItemStackTo($$4, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.horseContainer.getContainerSize() == 0 || !this.moveItemStackTo($$4, 2, $$5, false)) {
            int $$6 = $$5 + 27;
            int $$8 = $$6 + 9;
            if ($$1 >= $$6 && $$1 < $$8) {
               if (!this.moveItemStackTo($$4, $$5, $$6, false)) {
                  return ItemStack.EMPTY;
               }
            } else if ($$1 >= $$5 && $$1 < $$6) {
               if (!this.moveItemStackTo($$4, $$6, $$8, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo($$4, $$6, $$6, false)) {
               return ItemStack.EMPTY;
            }

            return ItemStack.EMPTY;
         }

         if ($$4.isEmpty()) {
            $$3.setByPlayer(ItemStack.EMPTY);
         } else {
            $$3.setChanged();
         }
      }

      return $$2;
   }

   @Override
   public void removed(Player $$0) {
      super.removed($$0);
      this.horseContainer.stopOpen($$0);
   }
}
