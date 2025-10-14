package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FilteredFunction extends LootItemConditionalFunction {
   public static final MapCodec<FilteredFunction> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> commonFields($$0)
            .and(
               $$0.group(
                  ItemPredicate.CODEC.fieldOf("item_filter").forGetter($$0x -> $$0x.filter),
                  LootItemFunctions.ROOT_CODEC.fieldOf("modifier").forGetter($$0x -> $$0x.modifier)
               )
            )
            .apply($$0, FilteredFunction::new)
   );
   private final ItemPredicate filter;
   private final LootItemFunction modifier;

   private FilteredFunction(List<LootItemCondition> $$0, ItemPredicate $$1, LootItemFunction $$2) {
      super($$0);
      this.filter = $$1;
      this.modifier = $$2;
   }

   @Override
   public LootItemFunctionType<FilteredFunction> getType() {
      return LootItemFunctions.FILTERED;
   }

   @Override
   public ItemStack run(ItemStack $$0, LootContext $$1) {
      return this.filter.test($$0) ? (ItemStack)this.modifier.apply($$0, $$1) : $$0;
   }

   @Override
   public void validate(ValidationContext $$0) {
      super.validate($$0);
      this.modifier.validate($$0.forChild(new ProblemReporter.FieldPathElement("modifier")));
   }
}
