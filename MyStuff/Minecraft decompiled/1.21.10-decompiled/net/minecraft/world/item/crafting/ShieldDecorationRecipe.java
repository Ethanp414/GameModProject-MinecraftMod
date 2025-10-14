package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class ShieldDecorationRecipe extends CustomRecipe {
   public ShieldDecorationRecipe(CraftingBookCategory $$0) {
      super($$0);
   }

   public boolean matches(CraftingInput $$0, Level $$1) {
      if ($$0.ingredientCount() != 2) {
         return false;
      } else {
         boolean $$2 = false;
         boolean $$3 = false;

         for(int $$4 = 0; $$4 < $$0.size(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if (!$$5.isEmpty()) {
               if ($$5.getItem() instanceof BannerItem) {
                  if ($$3) {
                     return false;
                  }

                  $$3 = true;
               } else {
                  if (!$$5.is(Items.SHIELD)) {
                     return false;
                  }

                  if ($$2) {
                     return false;
                  }

                  BannerPatternLayers $$6 = $$5.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
                  if (!$$6.layers().isEmpty()) {
                     return false;
                  }

                  $$2 = true;
               }
            }
         }

         return $$2 && $$3;
      }
   }

   public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
      ItemStack $$2 = ItemStack.EMPTY;
      ItemStack $$3 = ItemStack.EMPTY;

      for(int $$4 = 0; $$4 < $$0.size(); ++$$4) {
         ItemStack $$5 = $$0.getItem($$4);
         if (!$$5.isEmpty()) {
            if ($$5.getItem() instanceof BannerItem) {
               $$2 = $$5;
            } else if ($$5.is(Items.SHIELD)) {
               $$3 = $$5.copy();
            }
         }
      }

      if ($$3.isEmpty()) {
         return $$3;
      } else {
         $$3.set(DataComponents.BANNER_PATTERNS, $$2.get(DataComponents.BANNER_PATTERNS));
         $$3.set(DataComponents.BASE_COLOR, ((BannerItem)$$2.getItem()).getColor());
         return $$3;
      }
   }

   @Override
   public RecipeSerializer<ShieldDecorationRecipe> getSerializer() {
      return RecipeSerializer.SHIELD_DECORATION;
   }
}
