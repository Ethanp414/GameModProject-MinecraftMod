package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record InstrumentComponent(EitherHolder<Instrument> instrument) implements TooltipProvider {
   public static final Codec<InstrumentComponent> CODEC = EitherHolder.codec(Registries.INSTRUMENT, Instrument.CODEC)
      .xmap(InstrumentComponent::new, InstrumentComponent::instrument);
   public static final StreamCodec<RegistryFriendlyByteBuf, InstrumentComponent> STREAM_CODEC = EitherHolder.streamCodec(
         Registries.INSTRUMENT, Instrument.STREAM_CODEC
      )
      .map(InstrumentComponent::new, InstrumentComponent::instrument);

   public InstrumentComponent(Holder<Instrument> $$0) {
      this(new EitherHolder($$0));
   }

   @Deprecated
   public InstrumentComponent(ResourceKey<Instrument> $$0) {
      this(new EitherHolder($$0));
   }

   @Override
   public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
      HolderLookup.Provider $$4 = $$0.registries();
      if ($$4 != null) {
         Optional<Holder<Instrument>> $$5 = this.unwrap($$4);
         if ($$5.isPresent()) {
            MutableComponent $$6 = ((Instrument)((Holder)$$5.get()).value()).description().copy();
            ComponentUtils.mergeStyles($$6, Style.EMPTY.withColor(ChatFormatting.GRAY));
            $$1.accept($$6);
         }
      }
   }

   public Optional<Holder<Instrument>> unwrap(HolderLookup.Provider $$0) {
      return this.instrument.unwrap($$0);
   }
}
