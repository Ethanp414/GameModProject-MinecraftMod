package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;

public record DamagePredicate(MinMaxBounds.Ints durability, MinMaxBounds.Ints damage) implements DataComponentPredicate {
   public static final Codec<DamagePredicate> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               MinMaxBounds.Ints.CODEC.optionalFieldOf("durability", MinMaxBounds.Ints.ANY).forGetter(DamagePredicate::durability),
               MinMaxBounds.Ints.CODEC.optionalFieldOf("damage", MinMaxBounds.Ints.ANY).forGetter(DamagePredicate::damage)
            )
            .apply($$0, DamagePredicate::new)
   );

   @Override
   public boolean matches(DataComponentGetter $$0) {
      Integer $$1 = $$0.get(DataComponents.DAMAGE);
      if ($$1 == null) {
         return false;
      } else {
         int $$2 = $$0.getOrDefault(DataComponents.MAX_DAMAGE, 0);
         if (!this.durability.matches($$2 - $$1)) {
            return false;
         } else {
            return this.damage.matches($$1);
         }
      }
   }

   public static DamagePredicate durability(MinMaxBounds.Ints $$0) {
      return new DamagePredicate($$0, MinMaxBounds.Ints.ANY);
   }
}
