package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties $$0) {
      super($$0);
   }

   @Override
   public ItemStack getDefaultInstance() {
      ItemStack $$0 = super.getDefaultInstance();
      $$0.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
      return $$0;
   }

   @Override
   public Component getName(ItemStack $$0) {
      PotionContents $$1 = $$0.get(DataComponents.POTION_CONTENTS);
      return $$1 != null ? $$1.getName(this.descriptionId + ".effect.") : super.getName($$0);
   }
}
