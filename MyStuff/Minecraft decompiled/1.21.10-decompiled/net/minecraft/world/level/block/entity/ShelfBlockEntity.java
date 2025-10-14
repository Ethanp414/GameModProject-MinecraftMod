package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ShelfBlockEntity extends BlockEntity implements ItemOwner, ListBackedContainer {
   public static final int MAX_ITEMS = 3;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String ALIGN_ITEMS_TO_BOTTOM_TAG = "align_items_to_bottom";
   private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   private boolean alignItemsToBottom;

   public ShelfBlockEntity(BlockPos $$0, BlockState $$1) {
      super(BlockEntityType.SHELF, $$0, $$1);
   }

   @Override
   protected void loadAdditional(ValueInput $$0) {
      super.loadAdditional($$0);
      this.items.clear();
      ContainerHelper.loadAllItems($$0, this.items);
      this.alignItemsToBottom = $$0.getBooleanOr("align_items_to_bottom", false);
   }

   @Override
   protected void saveAdditional(ValueOutput $$0) {
      super.saveAdditional($$0);
      ContainerHelper.saveAllItems($$0, this.items, true);
      $$0.putBoolean("align_items_to_bottom", this.alignItemsToBottom);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
      CompoundTag var4;
      try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0);
         ContainerHelper.saveAllItems($$2, this.items, true);
         $$2.putBoolean("align_items_to_bottom", this.alignItemsToBottom);
         var4 = $$2.buildResult();
      }

      return var4;
   }

   @Override
   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   public boolean stillValid(Player $$0) {
      return Container.stillValidBlockEntity(this, $$0);
   }

   public ItemStack swapItemNoUpdate(int $$0, ItemStack $$1) {
      ItemStack $$2 = this.removeItemNoUpdate($$0);
      this.setItemNoUpdate($$0, $$1);
      return $$2;
   }

   public void setChanged(Holder.Reference<GameEvent> $$0) {
      super.setChanged();
      if (this.level != null) {
         this.level.gameEvent($$0, this.worldPosition, GameEvent.Context.of(this.getBlockState()));
         this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   @Override
   public void setChanged() {
      this.setChanged(GameEvent.BLOCK_ACTIVATE);
   }

   @Override
   protected void applyImplicitComponents(DataComponentGetter $$0) {
      super.applyImplicitComponents($$0);
      $$0.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
   }

   @Override
   protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
      super.collectImplicitComponents($$0);
      $$0.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
   }

   @Override
   public void removeComponentsFromTag(ValueOutput $$0) {
      $$0.discard("Items");
   }

   @Override
   public Level level() {
      return this.level;
   }

   @Override
   public Vec3 position() {
      return this.getBlockPos().getCenter();
   }

   @Override
   public float getVisualRotationYInDegrees() {
      return ((Direction)this.getBlockState().getValue(ShelfBlock.FACING)).getOpposite().toYRot();
   }

   public boolean getAlignItemsToBottom() {
      return this.alignItemsToBottom;
   }
}
