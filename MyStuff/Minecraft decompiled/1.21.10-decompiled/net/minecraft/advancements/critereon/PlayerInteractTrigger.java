package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<PlayerInteractTrigger.TriggerInstance> {
   @Override
   public Codec<PlayerInteractTrigger.TriggerInstance> codec() {
      return PlayerInteractTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer $$0, ItemStack $$1, Entity $$2) {
      LootContext $$3 = EntityPredicate.createContext($$0, $$2);
      this.trigger($$0, $$2x -> $$2x.matches($$1, $$3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity)
      implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<PlayerInteractTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(PlayerInteractTrigger.TriggerInstance::player),
                  ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PlayerInteractTrigger.TriggerInstance::item),
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(PlayerInteractTrigger.TriggerInstance::entity)
               )
               .apply($$0, PlayerInteractTrigger.TriggerInstance::new)
      );

      public static Criterion<PlayerInteractTrigger.TriggerInstance> itemUsedOnEntity(
         Optional<ContextAwarePredicate> $$0, ItemPredicate.Builder $$1, Optional<ContextAwarePredicate> $$2
      ) {
         return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.createCriterion(new PlayerInteractTrigger.TriggerInstance($$0, Optional.of($$1.build()), $$2));
      }

      public static Criterion<PlayerInteractTrigger.TriggerInstance> equipmentSheared(
         Optional<ContextAwarePredicate> $$0, ItemPredicate.Builder $$1, Optional<ContextAwarePredicate> $$2
      ) {
         return CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.createCriterion(new PlayerInteractTrigger.TriggerInstance($$0, Optional.of($$1.build()), $$2));
      }

      public static Criterion<PlayerInteractTrigger.TriggerInstance> equipmentSheared(ItemPredicate.Builder $$0, Optional<ContextAwarePredicate> $$1) {
         return CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT
            .createCriterion(new PlayerInteractTrigger.TriggerInstance(Optional.empty(), Optional.of($$0.build()), $$1));
      }

      public static Criterion<PlayerInteractTrigger.TriggerInstance> itemUsedOnEntity(ItemPredicate.Builder $$0, Optional<ContextAwarePredicate> $$1) {
         return itemUsedOnEntity(Optional.empty(), $$0, $$1);
      }

      public boolean matches(ItemStack $$0, LootContext $$1) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test($$0)) {
            return false;
         } else {
            return this.entity.isEmpty() || ((ContextAwarePredicate)this.entity.get()).matches($$1);
         }
      }

      @Override
      public void validate(CriterionValidator $$0) {
         SimpleCriterionTrigger.SimpleInstance.super.validate($$0);
         $$0.validateEntity(this.entity, "entity");
      }
   }
}
