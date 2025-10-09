package com.github.alexthe666.alexsmobs.effect;

import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

public class ProperBrewingRecipe extends BrewingRecipe {
   private final Ingredient input;
   private final Ingredient ingredient;
   private final ItemStack output;

   public ProperBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
      super(input, ingredient, output);
      this.input = input;
      this.ingredient = ingredient;
      this.output = output;
   }

   public boolean isInput(@Nonnull ItemStack stack) {
      if (stack == null) {
         return false;
      } else {
         ItemStack[] matchingStacks = this.input.m_43908_();
         if (matchingStacks.length == 0) {
            return stack.m_41619_();
         } else {
            for (ItemStack itemstack : matchingStacks) {
               if (ItemStack.m_41656_(stack, itemstack) && ItemStack.m_150942_(itemstack, stack)) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}
