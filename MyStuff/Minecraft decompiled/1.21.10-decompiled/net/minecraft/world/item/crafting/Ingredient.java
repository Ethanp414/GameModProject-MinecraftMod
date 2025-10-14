package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements StackedContents.IngredientInfo<Holder<Item>>, Predicate<ItemStack> {
   public static final StreamCodec<RegistryFriendlyByteBuf, Ingredient> CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM)
      .map(Ingredient::new, $$0 -> $$0.values);
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> OPTIONAL_CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM)
      .map(
         $$0 -> $$0.size() == 0 ? Optional.empty() : Optional.of(new Ingredient($$0)),
         $$0 -> (HolderSet)$$0.map($$0x -> $$0x.values).orElse(HolderSet.direct())
      );
   public static final Codec<HolderSet<Item>> NON_AIR_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.ITEM, Item.CODEC, false);
   public static final Codec<Ingredient> CODEC = ExtraCodecs.nonEmptyHolderSet(NON_AIR_HOLDER_SET_CODEC).xmap(Ingredient::new, $$0 -> $$0.values);
   private final HolderSet<Item> values;

   private Ingredient(HolderSet<Item> $$0) {
      $$0.unwrap().ifRight($$0x -> {
         if ($$0x.isEmpty()) {
            throw new UnsupportedOperationException("Ingredients can't be empty");
         } else if ($$0x.contains(Items.AIR.builtInRegistryHolder())) {
            throw new UnsupportedOperationException("Ingredient can't contain air");
         }
      });
      this.values = $$0;
   }

   public static boolean testOptionalIngredient(Optional<Ingredient> $$0, ItemStack $$1) {
      return $$0.map($$1x -> $$1x.test($$1)).orElseGet($$1::isEmpty);
   }

   @Deprecated
   public Stream<Holder<Item>> items() {
      return this.values.stream();
   }

   public boolean isEmpty() {
      return this.values.size() == 0;
   }

   public boolean test(ItemStack $$0) {
      return $$0.is(this.values);
   }

   public boolean acceptsItem(Holder<Item> $$0) {
      return this.values.contains($$0);
   }

   public boolean equals(Object $$0) {
      return $$0 instanceof Ingredient $$1 ? Objects.equals(this.values, $$1.values) : false;
   }

   public static Ingredient of(ItemLike $$0) {
      return new Ingredient(HolderSet.direct($$0.asItem().builtInRegistryHolder()));
   }

   public static Ingredient of(ItemLike... $$0) {
      return of(Arrays.stream($$0));
   }

   public static Ingredient of(Stream<? extends ItemLike> $$0) {
      return new Ingredient(HolderSet.direct($$0.map($$0x -> $$0x.asItem().builtInRegistryHolder()).toList()));
   }

   public static Ingredient of(HolderSet<Item> $$0) {
      return new Ingredient($$0);
   }

   public SlotDisplay display() {
      return this.values
         .unwrap()
         .map(SlotDisplay.TagSlotDisplay::new, $$0 -> new SlotDisplay.Composite($$0.stream().map(Ingredient::displayForSingleItem).toList()));
   }

   public static SlotDisplay optionalIngredientToDisplay(Optional<Ingredient> $$0) {
      return (SlotDisplay)$$0.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE);
   }

   private static SlotDisplay displayForSingleItem(Holder<Item> $$0) {
      SlotDisplay $$1 = new SlotDisplay.ItemSlotDisplay($$0);
      ItemStack $$2 = $$0.value().getCraftingRemainder();
      if (!$$2.isEmpty()) {
         SlotDisplay $$3 = new SlotDisplay.ItemStackSlotDisplay($$2);
         return new SlotDisplay.WithRemainder($$1, $$3);
      } else {
         return $$1;
      }
   }
}
