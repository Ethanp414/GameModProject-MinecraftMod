package net.minecraft.network;

import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface HashedStack {
   HashedStack EMPTY = new HashedStack() {
      public String toString() {
         return "<empty>";
      }

      @Override
      public boolean matches(ItemStack $$0, HashedPatchMap.HashGenerator $$1) {
         return $$0.isEmpty();
      }
   };
   StreamCodec<RegistryFriendlyByteBuf, HashedStack> STREAM_CODEC = ByteBufCodecs.optional(HashedStack.ActualItem.STREAM_CODEC)
      .map($$0 -> DataFixUtils.orElse($$0, EMPTY), $$0 -> $$0 instanceof HashedStack.ActualItem $$1 ? Optional.of($$1) : Optional.empty());

   boolean matches(ItemStack var1, HashedPatchMap.HashGenerator var2);

   static HashedStack create(ItemStack $$0, HashedPatchMap.HashGenerator $$1) {
      return (HashedStack)($$0.isEmpty()
         ? EMPTY
         : new HashedStack.ActualItem($$0.getItemHolder(), $$0.getCount(), HashedPatchMap.create($$0.getComponentsPatch(), $$1)));
   }

   public static record ActualItem(Holder<Item> item, int count, HashedPatchMap components) implements HashedStack {
      public static final StreamCodec<RegistryFriendlyByteBuf, HashedStack.ActualItem> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.holderRegistry(Registries.ITEM),
         HashedStack.ActualItem::item,
         ByteBufCodecs.VAR_INT,
         HashedStack.ActualItem::count,
         HashedPatchMap.STREAM_CODEC,
         HashedStack.ActualItem::components,
         HashedStack.ActualItem::new
      );

      @Override
      public boolean matches(ItemStack $$0, HashedPatchMap.HashGenerator $$1) {
         if (this.count != $$0.getCount()) {
            return false;
         } else {
            return !this.item.equals($$0.getItemHolder()) ? false : this.components.matches($$0.getComponentsPatch(), $$1);
         }
      }
   }
}
