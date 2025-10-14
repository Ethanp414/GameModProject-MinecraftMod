package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record UseRemainder(ItemStack convertInto) {
   public static final Codec<UseRemainder> CODEC = ItemStack.CODEC.xmap(UseRemainder::new, UseRemainder::convertInto);
   public static final StreamCodec<RegistryFriendlyByteBuf, UseRemainder> STREAM_CODEC = StreamCodec.composite(
      ItemStack.STREAM_CODEC, UseRemainder::convertInto, UseRemainder::new
   );

   public ItemStack convertIntoRemainder(ItemStack $$0, int $$1, boolean $$2, UseRemainder.OnExtraCreatedRemainder $$3) {
      if ($$2) {
         return $$0;
      } else if ($$0.getCount() >= $$1) {
         return $$0;
      } else {
         ItemStack $$4 = this.convertInto.copy();
         if ($$0.isEmpty()) {
            return $$4;
         } else {
            $$3.apply($$4);
            return $$0;
         }
      }
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else if ($$0 != null && this.getClass() == $$0.getClass()) {
         UseRemainder $$1 = (UseRemainder)$$0;
         return ItemStack.matches(this.convertInto, $$1.convertInto);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return ItemStack.hashItemAndComponents(this.convertInto);
   }

   @FunctionalInterface
   public interface OnExtraCreatedRemainder {
      void apply(ItemStack var1);
   }
}
