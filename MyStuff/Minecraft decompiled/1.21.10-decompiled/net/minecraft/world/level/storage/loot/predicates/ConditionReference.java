package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.slf4j.Logger;

public record ConditionReference(ResourceKey<LootItemCondition> name) implements LootItemCondition {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<ConditionReference> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(ResourceKey.codec(Registries.PREDICATE).fieldOf("name").forGetter(ConditionReference::name)).apply($$0, ConditionReference::new)
   );

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.REFERENCE;
   }

   @Override
   public void validate(ValidationContext $$0) {
      if (!$$0.allowsReferences()) {
         $$0.reportProblem(new ValidationContext.ReferenceNotAllowedProblem(this.name));
      } else if ($$0.hasVisitedElement(this.name)) {
         $$0.reportProblem(new ValidationContext.RecursiveReferenceProblem(this.name));
      } else {
         LootItemCondition.super.validate($$0);
         $$0.resolver()
            .get(this.name)
            .ifPresentOrElse(
               $$1 -> ((LootItemCondition)$$1.value()).validate($$0.enterElement(new ProblemReporter.ElementReferencePathElement(this.name), this.name)),
               () -> $$0.reportProblem(new ValidationContext.MissingReferenceProblem(this.name))
            );
      }
   }

   public boolean test(LootContext $$0) {
      LootItemCondition $$1 = (LootItemCondition)$$0.getResolver().get(this.name).map(Holder.Reference::value).orElse(null);
      if ($$1 == null) {
         LOGGER.warn("Tried using unknown condition table called {}", this.name.location());
         return false;
      } else {
         LootContext.VisitedEntry<?> $$2 = LootContext.createVisitedEntry($$1);
         if ($$0.pushVisitedElement($$2)) {
            boolean var4;
            try {
               var4 = $$1.test($$0);
            } finally {
               $$0.popVisitedElement($$2);
            }

            return var4;
         } else {
            LOGGER.warn("Detected infinite loop in loot tables");
            return false;
         }
      }
   }

   public static LootItemCondition.Builder conditionReference(ResourceKey<LootItemCondition> $$0) {
      return () -> new ConditionReference($$0);
   }
}
