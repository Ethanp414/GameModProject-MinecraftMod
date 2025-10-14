package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;

public class ShieldItem extends Item {
   public ShieldItem(Item.Properties $$0) {
      super($$0);
   }

   @Override
   public Component getName(ItemStack $$0) {
      DyeColor $$1 = $$0.get(DataComponents.BASE_COLOR);
      return (Component)($$1 != null ? Component.translatable(this.descriptionId + "." + $$1.getName()) : super.getName($$0));
   }
}
