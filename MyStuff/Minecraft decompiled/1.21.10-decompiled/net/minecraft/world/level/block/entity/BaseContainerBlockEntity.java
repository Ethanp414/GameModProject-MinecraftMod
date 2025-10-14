package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class BaseContainerBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
   private LockCode lockKey = LockCode.NO_LOCK;
   @Nullable
   private Component name;

   protected BaseContainerBlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
      super($$0, $$1, $$2);
   }

   @Override
   protected void loadAdditional(ValueInput $$0) {
      super.loadAdditional($$0);
      this.lockKey = LockCode.fromTag($$0);
      this.name = parseCustomNameSafe($$0, "CustomName");
   }

   @Override
   protected void saveAdditional(ValueOutput $$0) {
      super.saveAdditional($$0);
      this.lockKey.addToTag($$0);
      $$0.storeNullable("CustomName", ComponentSerialization.CODEC, this.name);
   }

   @Override
   public Component getName() {
      return this.name != null ? this.name : this.getDefaultName();
   }

   @Override
   public Component getDisplayName() {
      return this.getName();
   }

   @Nullable
   @Override
   public Component getCustomName() {
      return this.name;
   }

   protected abstract Component getDefaultName();

   public boolean canOpen(Player $$0) {
      return canUnlock($$0, this.lockKey, this.getDisplayName());
   }

   public static boolean canUnlock(Player $$0, LockCode $$1, Component $$2) {
      if (!$$0.isSpectator() && !$$1.unlocksWith($$0.getMainHandItem())) {
         $$0.displayClientMessage(Component.translatable("container.isLocked", $$2), true);
         $$0.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   public boolean isLocked() {
      return !this.lockKey.equals(LockCode.NO_LOCK);
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> var1);

   @Override
   public boolean isEmpty() {
      for(ItemStack $$0 : this.getItems()) {
         if (!$$0.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public ItemStack getItem(int $$0) {
      return this.getItems().get($$0);
   }

   @Override
   public ItemStack removeItem(int $$0, int $$1) {
      ItemStack $$2 = ContainerHelper.removeItem(this.getItems(), $$0, $$1);
      if (!$$2.isEmpty()) {
         this.setChanged();
      }

      return $$2;
   }

   @Override
   public ItemStack removeItemNoUpdate(int $$0) {
      return ContainerHelper.takeItem(this.getItems(), $$0);
   }

   @Override
   public void setItem(int $$0, ItemStack $$1) {
      this.getItems().set($$0, $$1);
      $$1.limitSize(this.getMaxStackSize($$1));
      this.setChanged();
   }

   @Override
   public boolean stillValid(Player $$0) {
      return Container.stillValidBlockEntity(this, $$0);
   }

   @Override
   public void clearContent() {
      this.getItems().clear();
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
      return this.canOpen($$2) ? this.createMenu($$0, $$1) : null;
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);

   @Override
   protected void applyImplicitComponents(DataComponentGetter $$0) {
      super.applyImplicitComponents($$0);
      this.name = $$0.get(DataComponents.CUSTOM_NAME);
      this.lockKey = $$0.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
      $$0.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getItems());
   }

   @Override
   protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
      super.collectImplicitComponents($$0);
      $$0.set(DataComponents.CUSTOM_NAME, this.name);
      if (this.isLocked()) {
         $$0.set(DataComponents.LOCK, this.lockKey);
      }

      $$0.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
   }

   @Override
   public void removeComponentsFromTag(ValueOutput $$0) {
      $$0.discard("CustomName");
      $$0.discard("lock");
      $$0.discard("Items");
   }
}
