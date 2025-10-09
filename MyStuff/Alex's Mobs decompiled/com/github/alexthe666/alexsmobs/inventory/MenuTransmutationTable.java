package com.github.alexthe666.alexsmobs.inventory;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.message.MessageTransmuteFromMenu;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class MenuTransmutationTable extends AbstractContainerMenu {
   private final ContainerLevelAccess access;
   private long lastSoundTime;
   private final Player player;
   private final Slot transmuteSlot;
   private TileEntityTransmutationTable table;
   public final Container container = new SimpleContainer(1) {
      public void m_6596_() {
         MenuTransmutationTable.this.m_6199_(this);
         super.m_6596_();
      }
   };

   public MenuTransmutationTable(int i, Inventory inventory) {
      this(i, inventory, ContainerLevelAccess.f_39287_, AlexsMobs.PROXY.getClientSidePlayer(), null);
   }

   public MenuTransmutationTable(int id, Inventory inventory, ContainerLevelAccess access, Player player, TileEntityTransmutationTable table) {
      super((MenuType)AMMenuRegistry.TRANSMUTATION_TABLE.get(), id);
      this.table = table;
      this.player = player;
      this.access = access;
      this.m_38897_(this.transmuteSlot = new Slot(this.container, 0, 83, 83) {
         public boolean m_5857_(ItemStack stack) {
            ResourceLocation name = ForgeRegistries.ITEMS.getKey(stack.m_41720_());
            return stack.m_41741_() > 1 && (name == null || !AMConfig.transmutationBlacklist.contains(name.toString()));
         }
      });

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.m_38897_(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 119 + i * 18));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.m_38897_(new Slot(inventory, k, 8 + k * 18, 177));
      }

      if (table != null && player != null && !table.hasPossibilities()) {
         table.setRerollPlayerUUID(player.m_20148_());
      }
   }

   public boolean m_6875_(Player player) {
      return m_38889_(this.access, player, (Block)AMBlockRegistry.TRANSMUTATION_TABLE.get());
   }

   public void m_6199_(Container container) {
      if (this.table != null && !this.table.hasPossibilities()) {
         this.table.setRerollPlayerUUID(this.player.m_20148_());
      }
   }

   public ItemStack m_7648_(Player player, int slotIndex) {
      ItemStack itemstack = ItemStack.f_41583_;
      Slot slot = (Slot)this.f_38839_.get(slotIndex);
      if (slot != null && slot.m_6657_()) {
         ItemStack itemstack1 = slot.m_7993_();
         itemstack = itemstack1.m_41777_();
         if (slotIndex != 0) {
            if (!this.m_38903_(itemstack1, 0, 1, false)) {
               return ItemStack.f_41583_;
            }
         } else if (!this.m_38903_(itemstack1, 1, 36, false)) {
            return ItemStack.f_41583_;
         }

         if (itemstack1.m_41619_()) {
            slot.m_5852_(ItemStack.f_41583_);
         }

         slot.m_6654_();
         if (itemstack1.m_41613_() == itemstack.m_41613_()) {
            return ItemStack.f_41583_;
         }

         slot.m_142406_(player, itemstack1);
         this.m_38946_();
      }

      return itemstack;
   }

   public boolean m_6366_(Player player, int buttonId) {
      if (player.m_9236_().f_46443_) {
         AlexsMobs.sendMSGToServer(new MessageTransmuteFromMenu(player.m_19879_(), buttonId));
      }

      return true;
   }

   public void transmute(Player player, int buttonId) {
      ItemStack from = this.transmuteSlot.m_7993_();
      int cost = AMConfig.transmutingExperienceCost;
      ItemStack setTo = this.table.getPossibility(buttonId).m_41777_();
      double divisible = (double)from.m_41741_() / setTo.m_41741_();
      if (!player.m_9236_().f_46443_
         && this.table != null
         && divisible > 0.0
         && this.table.hasPossibilities()
         && !from.m_41619_()
         && (player.f_36078_ >= cost || player.m_150110_().f_35937_)) {
         int newStackSize = (int)Math.floor(from.m_41613_() / divisible);
         setTo.m_41764_(Math.max(newStackSize, 1));
         this.transmuteSlot.m_5852_(setTo);
         player.m_6749_(-cost);
         this.table.postTransmute(player, from, setTo);
      }
   }

   public void m_6877_(Player player) {
      super.m_6877_(player);
      this.access.m_39292_((p_39152_, p_39153_) -> this.m_150411_(player, this.container));
   }
}
