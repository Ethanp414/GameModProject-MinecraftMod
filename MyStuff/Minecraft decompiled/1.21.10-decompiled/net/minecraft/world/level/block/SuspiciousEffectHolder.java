package net.minecraft.world.level.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.ItemLike;

public interface SuspiciousEffectHolder {
   SuspiciousStewEffects getSuspiciousEffects();

   static List<SuspiciousEffectHolder> getAllEffectHolders() {
      return (List<SuspiciousEffectHolder>)BuiltInRegistries.ITEM
         .stream()
         .map(SuspiciousEffectHolder::tryGet)
         .filter(Objects::nonNull)
         .collect(Collectors.toList());
   }

   @Nullable
   static SuspiciousEffectHolder tryGet(ItemLike $$0) {
      Item var3 = $$0.asItem();
      if (var3 instanceof BlockItem $$1) {
         Block var6 = $$1.getBlock();
         if (var6 instanceof SuspiciousEffectHolder $$2) {
            return $$2;
         }
      }

      Item $$2 = $$0.asItem();
      return $$2 instanceof SuspiciousEffectHolder $$3 ? $$3 : null;
   }
}
