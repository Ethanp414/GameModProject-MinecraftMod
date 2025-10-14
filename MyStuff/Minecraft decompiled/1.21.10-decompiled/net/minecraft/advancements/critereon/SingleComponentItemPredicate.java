package net.minecraft.advancements.critereon;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;

public interface SingleComponentItemPredicate<T> extends DataComponentPredicate {
   @Override
   default boolean matches(DataComponentGetter $$0) {
      T $$1 = $$0.get(this.componentType());
      return $$1 != null && this.matches($$1);
   }

   DataComponentType<T> componentType();

   boolean matches(T var1);
}
