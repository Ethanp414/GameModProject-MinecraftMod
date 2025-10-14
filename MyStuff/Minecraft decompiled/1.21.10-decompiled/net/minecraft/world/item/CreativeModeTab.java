package net.minecraft.world.item;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;

public class CreativeModeTab {
   static final ResourceLocation DEFAULT_BACKGROUND = createTextureLocation("items");
   private final Component displayName;
   ResourceLocation backgroundTexture = DEFAULT_BACKGROUND;
   boolean canScroll = true;
   boolean showTitle = true;
   boolean alignedRight = false;
   private final CreativeModeTab.Row row;
   private final int column;
   private final CreativeModeTab.Type type;
   @Nullable
   private ItemStack iconItemStack;
   private Collection<ItemStack> displayItems = ItemStackLinkedSet.createTypeAndComponentsSet();
   private Set<ItemStack> displayItemsSearchTab = ItemStackLinkedSet.createTypeAndComponentsSet();
   private final Supplier<ItemStack> iconGenerator;
   private final CreativeModeTab.DisplayItemsGenerator displayItemsGenerator;

   CreativeModeTab(
      CreativeModeTab.Row $$0, int $$1, CreativeModeTab.Type $$2, Component $$3, Supplier<ItemStack> $$4, CreativeModeTab.DisplayItemsGenerator $$5
   ) {
      this.row = $$0;
      this.column = $$1;
      this.displayName = $$3;
      this.iconGenerator = $$4;
      this.displayItemsGenerator = $$5;
      this.type = $$2;
   }

   public static ResourceLocation createTextureLocation(String $$0) {
      return ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tab_" + $$0 + ".png");
   }

   public static CreativeModeTab.Builder builder(CreativeModeTab.Row $$0, int $$1) {
      return new CreativeModeTab.Builder($$0, $$1);
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public ItemStack getIconItem() {
      if (this.iconItemStack == null) {
         this.iconItemStack = (ItemStack)this.iconGenerator.get();
      }

      return this.iconItemStack;
   }

   public ResourceLocation getBackgroundTexture() {
      return this.backgroundTexture;
   }

   public boolean showTitle() {
      return this.showTitle;
   }

   public boolean canScroll() {
      return this.canScroll;
   }

   public int column() {
      return this.column;
   }

   public CreativeModeTab.Row row() {
      return this.row;
   }

   public boolean hasAnyItems() {
      return !this.displayItems.isEmpty();
   }

   public boolean shouldDisplay() {
      return this.type != CreativeModeTab.Type.CATEGORY || this.hasAnyItems();
   }

   public boolean isAlignedRight() {
      return this.alignedRight;
   }

   public CreativeModeTab.Type getType() {
      return this.type;
   }

   public void buildContents(CreativeModeTab.ItemDisplayParameters $$0) {
      CreativeModeTab.ItemDisplayBuilder $$1 = new CreativeModeTab.ItemDisplayBuilder(this, $$0.enabledFeatures);
      ResourceKey<CreativeModeTab> $$2 = (ResourceKey)BuiltInRegistries.CREATIVE_MODE_TAB
         .getResourceKey(this)
         .orElseThrow(() -> new IllegalStateException("Unregistered creative tab: " + this));
      this.displayItemsGenerator.accept($$0, $$1);
      this.displayItems = $$1.tabContents;
      this.displayItemsSearchTab = $$1.searchTabContents;
   }

   public Collection<ItemStack> getDisplayItems() {
      return this.displayItems;
   }

   public Collection<ItemStack> getSearchTabDisplayItems() {
      return this.displayItemsSearchTab;
   }

   public boolean contains(ItemStack $$0) {
      return this.displayItemsSearchTab.contains($$0);
   }

   public static class Builder {
      private static final CreativeModeTab.DisplayItemsGenerator EMPTY_GENERATOR = ($$0, $$1) -> {
      };
      private final CreativeModeTab.Row row;
      private final int column;
      private Component displayName = Component.empty();
      private Supplier<ItemStack> iconGenerator = () -> ItemStack.EMPTY;
      private CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = EMPTY_GENERATOR;
      private boolean canScroll = true;
      private boolean showTitle = true;
      private boolean alignedRight = false;
      private CreativeModeTab.Type type = CreativeModeTab.Type.CATEGORY;
      private ResourceLocation backgroundTexture = CreativeModeTab.DEFAULT_BACKGROUND;

      public Builder(CreativeModeTab.Row $$0, int $$1) {
         this.row = $$0;
         this.column = $$1;
      }

      public CreativeModeTab.Builder title(Component $$0) {
         this.displayName = $$0;
         return this;
      }

      public CreativeModeTab.Builder icon(Supplier<ItemStack> $$0) {
         this.iconGenerator = $$0;
         return this;
      }

      public CreativeModeTab.Builder displayItems(CreativeModeTab.DisplayItemsGenerator $$0) {
         this.displayItemsGenerator = $$0;
         return this;
      }

      public CreativeModeTab.Builder alignedRight() {
         this.alignedRight = true;
         return this;
      }

      public CreativeModeTab.Builder hideTitle() {
         this.showTitle = false;
         return this;
      }

      public CreativeModeTab.Builder noScrollBar() {
         this.canScroll = false;
         return this;
      }

      protected CreativeModeTab.Builder type(CreativeModeTab.Type $$0) {
         this.type = $$0;
         return this;
      }

      public CreativeModeTab.Builder backgroundTexture(ResourceLocation $$0) {
         this.backgroundTexture = $$0;
         return this;
      }

      public CreativeModeTab build() {
         if ((this.type == CreativeModeTab.Type.HOTBAR || this.type == CreativeModeTab.Type.INVENTORY) && this.displayItemsGenerator != EMPTY_GENERATOR) {
            throw new IllegalStateException("Special tabs can't have display items");
         } else {
            CreativeModeTab $$0 = new CreativeModeTab(this.row, this.column, this.type, this.displayName, this.iconGenerator, this.displayItemsGenerator);
            $$0.alignedRight = this.alignedRight;
            $$0.showTitle = this.showTitle;
            $$0.canScroll = this.canScroll;
            $$0.backgroundTexture = this.backgroundTexture;
            return $$0;
         }
      }
   }

   @FunctionalInterface
   public interface DisplayItemsGenerator {
      void accept(CreativeModeTab.ItemDisplayParameters var1, CreativeModeTab.Output var2);
   }

   static class ItemDisplayBuilder implements CreativeModeTab.Output {
      public final Collection<ItemStack> tabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
      public final Set<ItemStack> searchTabContents = ItemStackLinkedSet.createTypeAndComponentsSet();
      private final CreativeModeTab tab;
      private final FeatureFlagSet featureFlagSet;

      public ItemDisplayBuilder(CreativeModeTab $$0, FeatureFlagSet $$1) {
         this.tab = $$0;
         this.featureFlagSet = $$1;
      }

      @Override
      public void accept(ItemStack $$0, CreativeModeTab.TabVisibility $$1) {
         if ($$0.getCount() != 1) {
            throw new IllegalArgumentException("Stack size must be exactly 1");
         } else {
            boolean $$2 = this.tabContents.contains($$0) && $$1 != CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
            if ($$2) {
               throw new IllegalStateException(
                  "Accidentally adding the same item stack twice "
                     + $$0.getDisplayName().getString()
                     + " to a Creative Mode Tab: "
                     + this.tab.getDisplayName().getString()
               );
            } else {
               if ($$0.getItem().isEnabled(this.featureFlagSet)) {
                  switch($$1.ordinal()) {
                     case 0:
                        this.tabContents.add($$0);
                        this.searchTabContents.add($$0);
                        break;
                     case 1:
                        this.tabContents.add($$0);
                        break;
                     case 2:
                        this.searchTabContents.add($$0);
                  }
               }
            }
         }
      }
   }

   public static record ItemDisplayParameters(FeatureFlagSet enabledFeatures, boolean hasPermissions, HolderLookup.Provider holders) {
      final FeatureFlagSet enabledFeatures;

      public boolean needsUpdate(FeatureFlagSet $$0, boolean $$1, HolderLookup.Provider $$2) {
         return !this.enabledFeatures.equals($$0) || this.hasPermissions != $$1 || this.holders != $$2;
      }
   }

   public interface Output {
      void accept(ItemStack var1, CreativeModeTab.TabVisibility var2);

      default void accept(ItemStack $$0) {
         this.accept($$0, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void accept(ItemLike $$0, CreativeModeTab.TabVisibility $$1) {
         this.accept(new ItemStack($$0), $$1);
      }

      default void accept(ItemLike $$0) {
         this.accept(new ItemStack($$0), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void acceptAll(Collection<ItemStack> $$0, CreativeModeTab.TabVisibility $$1) {
         $$0.forEach($$1x -> this.accept($$1x, $$1));
      }

      default void acceptAll(Collection<ItemStack> $$0) {
         this.acceptAll($$0, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }

   public static enum Row {
      TOP,
      BOTTOM;
   }

   protected static enum TabVisibility {
      PARENT_AND_SEARCH_TABS,
      PARENT_TAB_ONLY,
      SEARCH_TAB_ONLY;
   }

   public static enum Type {
      CATEGORY,
      INVENTORY,
      HOTBAR,
      SEARCH;
   }
}
