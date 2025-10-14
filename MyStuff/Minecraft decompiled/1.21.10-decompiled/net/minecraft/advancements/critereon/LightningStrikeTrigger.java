package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<LightningStrikeTrigger.TriggerInstance> {
   @Override
   public Codec<LightningStrikeTrigger.TriggerInstance> codec() {
      return LightningStrikeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer $$0, LightningBolt $$1, List<Entity> $$2) {
      List<LootContext> $$3 = (List)$$2.stream().map($$1x -> EntityPredicate.createContext($$0, $$1x)).collect(Collectors.toList());
      LootContext $$4 = EntityPredicate.createContext($$0, $$1);
      this.trigger($$0, $$2x -> $$2x.matches($$4, $$3));
   }

   public static record TriggerInstance(
      Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> lightning, Optional<ContextAwarePredicate> bystander
   ) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<LightningStrikeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(LightningStrikeTrigger.TriggerInstance::player),
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("lightning").forGetter(LightningStrikeTrigger.TriggerInstance::lightning),
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("bystander").forGetter(LightningStrikeTrigger.TriggerInstance::bystander)
               )
               .apply($$0, LightningStrikeTrigger.TriggerInstance::new)
      );

      public static Criterion<LightningStrikeTrigger.TriggerInstance> lightningStrike(Optional<EntityPredicate> $$0, Optional<EntityPredicate> $$1) {
         return CriteriaTriggers.LIGHTNING_STRIKE
            .createCriterion(new LightningStrikeTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap($$0), EntityPredicate.wrap($$1)));
      }

      public boolean matches(LootContext $$0, List<LootContext> $$1) {
         if (this.lightning.isPresent() && !((ContextAwarePredicate)this.lightning.get()).matches($$0)) {
            return false;
         } else {
            return !this.bystander.isPresent() || !$$1.stream().noneMatch(((ContextAwarePredicate)this.bystander.get())::matches);
         }
      }

      @Override
      public void validate(CriterionValidator $$0) {
         SimpleCriterionTrigger.SimpleInstance.super.validate($$0);
         $$0.validateEntity(this.lightning, "lightning");
         $$0.validateEntity(this.bystander, "bystander");
      }
   }
}
