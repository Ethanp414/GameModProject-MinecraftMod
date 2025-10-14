package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemCost(Holder<Item> item, int count, DataComponentExactPredicate components, ItemStack itemStack) {
   public static final Codec<ItemCost> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               Item.CODEC.fieldOf("id").forGetter(ItemCost::item),
               ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemCost::count),
               DataComponentExactPredicate.CODEC.optionalFieldOf("components", DataComponentExactPredicate.EMPTY).forGetter(ItemCost::components)
            )
            .apply($$0, ItemCost::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemCost> STREAM_CODEC = StreamCodec.composite(
      Item.STREAM_CODEC, ItemCost::item, ByteBufCodecs.VAR_INT, ItemCost::count, DataComponentExactPredicate.STREAM_CODEC, ItemCost::components, ItemCost::new
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<ItemCost>> OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);

   public ItemCost(ItemLike $$0) {
      this($$0, 1);
   }

   public ItemCost(ItemLike $$0, int $$1) {
      this($$0.asItem().builtInRegistryHolder(), $$1, DataComponentExactPredicate.EMPTY);
   }

   public ItemCost(Holder<Item> $$0, int $$1, DataComponentExactPredicate $$2) {
      this($$0, $$1, $$2, createStack($$0, $$1, $$2));
   }

   public ItemCost withComponents(UnaryOperator<DataComponentExactPredicate.Builder> $$0) {
      return new ItemCost(this.item, this.count, ((DataComponentExactPredicate.Builder)$$0.apply(DataComponentExactPredicate.builder())).build());
   }

   private static ItemStack createStack(Holder<Item> $$0, int $$1, DataComponentExactPredicate $$2) {
      return new ItemStack($$0, $$1, $$2.asPatch());
   }

   public boolean test(ItemStack $$0) {
      return $$0.is(this.item) && this.components.test((DataComponentGetter)$$0);
   }
}
