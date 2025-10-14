package net.minecraft.world.item.component;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class ItemContainerContents implements TooltipProvider {
   private static final int NO_SLOT = -1;
   private static final int MAX_SIZE = 256;
   public static final ItemContainerContents EMPTY = new ItemContainerContents(NonNullList.create());
   public static final Codec<ItemContainerContents> CODEC = ItemContainerContents.Slot.CODEC
      .sizeLimitedListOf(256)
      .xmap(ItemContainerContents::fromSlots, ItemContainerContents::asSlots);
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemContainerContents> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
      .apply(ByteBufCodecs.list(256))
      .map(ItemContainerContents::new, $$0 -> $$0.items);
   private final NonNullList<ItemStack> items;
   private final int hashCode;

   private ItemContainerContents(NonNullList<ItemStack> $$0) {
      if ($$0.size() > 256) {
         throw new IllegalArgumentException("Got " + $$0.size() + " items, but maximum is 256");
      } else {
         this.items = $$0;
         this.hashCode = ItemStack.hashStackList($$0);
      }
   }

   private ItemContainerContents(int $$0) {
      this(NonNullList.withSize($$0, ItemStack.EMPTY));
   }

   private ItemContainerContents(List<ItemStack> $$0) {
      this($$0.size());

      for(int $$1 = 0; $$1 < $$0.size(); ++$$1) {
         this.items.set($$1, (ItemStack)$$0.get($$1));
      }
   }

   private static ItemContainerContents fromSlots(List<ItemContainerContents.Slot> $$0) {
      OptionalInt $$1 = $$0.stream().mapToInt(ItemContainerContents.Slot::index).max();
      if ($$1.isEmpty()) {
         return EMPTY;
      } else {
         ItemContainerContents $$2 = new ItemContainerContents($$1.getAsInt() + 1);

         for(ItemContainerContents.Slot $$3 : $$0) {
            $$2.items.set($$3.index(), $$3.item());
         }

         return $$2;
      }
   }

   public static ItemContainerContents fromItems(List<ItemStack> $$0) {
      int $$1 = findLastNonEmptySlot($$0);
      if ($$1 == -1) {
         return EMPTY;
      } else {
         ItemContainerContents $$2 = new ItemContainerContents($$1 + 1);

         for(int $$3 = 0; $$3 <= $$1; ++$$3) {
            $$2.items.set($$3, ((ItemStack)$$0.get($$3)).copy());
         }

         return $$2;
      }
   }

   private static int findLastNonEmptySlot(List<ItemStack> $$0) {
      for(int $$1 = $$0.size() - 1; $$1 >= 0; --$$1) {
         if (!((ItemStack)$$0.get($$1)).isEmpty()) {
            return $$1;
         }
      }

      return -1;
   }

   private List<ItemContainerContents.Slot> asSlots() {
      List<ItemContainerContents.Slot> $$0 = new ArrayList();

      for(int $$1 = 0; $$1 < this.items.size(); ++$$1) {
         ItemStack $$2 = this.items.get($$1);
         if (!$$2.isEmpty()) {
            $$0.add(new ItemContainerContents.Slot($$1, $$2));
         }
      }

      return $$0;
   }

   public void copyInto(NonNullList<ItemStack> $$0) {
      for(int $$1 = 0; $$1 < $$0.size(); ++$$1) {
         ItemStack $$2 = $$1 < this.items.size() ? this.items.get($$1) : ItemStack.EMPTY;
         $$0.set($$1, $$2.copy());
      }
   }

   public ItemStack copyOne() {
      return this.items.isEmpty() ? ItemStack.EMPTY : this.items.get(0).copy();
   }

   public Stream<ItemStack> stream() {
      return this.items.stream().map(ItemStack::copy);
   }

   public Stream<ItemStack> nonEmptyStream() {
      return this.items.stream().filter($$0 -> !$$0.isEmpty()).map(ItemStack::copy);
   }

   public Iterable<ItemStack> nonEmptyItems() {
      return Iterables.filter(this.items, $$0 -> !$$0.isEmpty());
   }

   public Iterable<ItemStack> nonEmptyItemsCopy() {
      return Iterables.transform(this.nonEmptyItems(), ItemStack::copy);
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else {
         if ($$0 instanceof ItemContainerContents $$1 && ItemStack.listMatches(this.items, $$1.items)) {
            return true;
         }

         return false;
      }
   }

   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
      int $$4 = 0;
      int $$5 = 0;

      for(ItemStack $$6 : this.nonEmptyItems()) {
         ++$$5;
         if ($$4 <= 4) {
            ++$$4;
            $$1.accept(Component.translatable("item.container.item_count", $$6.getHoverName(), $$6.getCount()));
         }
      }

      if ($$5 - $$4 > 0) {
         $$1.accept(Component.translatable("item.container.more_items", $$5 - $$4).withStyle(ChatFormatting.ITALIC));
      }
   }

   static record Slot(int index, ItemStack item) {
      public static final Codec<ItemContainerContents.Slot> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  Codec.intRange(0, 255).fieldOf("slot").forGetter(ItemContainerContents.Slot::index),
                  ItemStack.CODEC.fieldOf("item").forGetter(ItemContainerContents.Slot::item)
               )
               .apply($$0, ItemContainerContents.Slot::new)
      );
   }
}
