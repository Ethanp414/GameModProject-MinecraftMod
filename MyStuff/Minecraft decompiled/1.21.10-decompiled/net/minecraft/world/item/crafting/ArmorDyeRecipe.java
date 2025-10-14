package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe extends CustomRecipe {
   public ArmorDyeRecipe(CraftingBookCategory $$0) {
      super($$0);
   }

   public boolean matches(CraftingInput $$0, Level $$1) {
      if ($$0.ingredientCount() < 2) {
         return false;
      } else {
         boolean $$2 = false;
         boolean $$3 = false;

         for(int $$4 = 0; $$4 < $$0.size(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if (!$$5.isEmpty()) {
               if ($$5.is(ItemTags.DYEABLE)) {
                  if ($$2) {
                     return false;
                  }

                  $$2 = true;
               } else {
                  if (!($$5.getItem() instanceof DyeItem)) {
                     return false;
                  }

                  $$3 = true;
               }
            }
         }

         return $$3 && $$2;
      }
   }

   public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
      List<DyeItem> $$2 = new ArrayList();
      ItemStack $$3 = ItemStack.EMPTY;

      for(int $$4 = 0; $$4 < $$0.size(); ++$$4) {
         ItemStack $$5 = $$0.getItem($$4);
         if (!$$5.isEmpty()) {
            if ($$5.is(ItemTags.DYEABLE)) {
               if (!$$3.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               $$3 = $$5.copy();
            } else {
               Item var8 = $$5.getItem();
               if (!(var8 instanceof DyeItem)) {
                  return ItemStack.EMPTY;
               }

               DyeItem $$6 = (DyeItem)var8;
               $$2.add($$6);
            }
         }
      }

      return !$$3.isEmpty() && !$$2.isEmpty() ? DyedItemColor.applyDyes($$3, $$2) : ItemStack.EMPTY;
   }

   @Override
   public RecipeSerializer<ArmorDyeRecipe> getSerializer() {
      return RecipeSerializer.ARMOR_DYE;
   }
}
