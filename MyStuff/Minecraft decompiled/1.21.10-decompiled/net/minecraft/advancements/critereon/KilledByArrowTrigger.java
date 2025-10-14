package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledByArrowTrigger extends SimpleCriterionTrigger<KilledByArrowTrigger.TriggerInstance> {
   @Override
   public Codec<KilledByArrowTrigger.TriggerInstance> codec() {
      return KilledByArrowTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer $$0, Collection<Entity> $$1, @Nullable ItemStack $$2) {
      List<LootContext> $$3 = Lists.<LootContext>newArrayList();
      Set<EntityType<?>> $$4 = Sets.<EntityType<?>>newHashSet();

      for(Entity $$5 : $$1) {
         $$4.add($$5.getType());
         $$3.add(EntityPredicate.createContext($$0, $$5));
      }

      this.trigger($$0, $$3x -> $$3x.matches($$3, $$4.size(), $$2));
   }

   public static record TriggerInstance(
      Optional<ContextAwarePredicate> player,
      List<ContextAwarePredicate> victims,
      MinMaxBounds.Ints uniqueEntityTypes,
      Optional<ItemPredicate> firedFromWeapon
   ) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<KilledByArrowTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(KilledByArrowTrigger.TriggerInstance::player),
                  EntityPredicate.ADVANCEMENT_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(KilledByArrowTrigger.TriggerInstance::victims),
                  MinMaxBounds.Ints.CODEC
                     .optionalFieldOf("unique_entity_types", MinMaxBounds.Ints.ANY)
                     .forGetter(KilledByArrowTrigger.TriggerInstance::uniqueEntityTypes),
                  ItemPredicate.CODEC.optionalFieldOf("fired_from_weapon").forGetter(KilledByArrowTrigger.TriggerInstance::firedFromWeapon)
               )
               .apply($$0, KilledByArrowTrigger.TriggerInstance::new)
      );

      public static Criterion<KilledByArrowTrigger.TriggerInstance> crossbowKilled(HolderGetter<Item> $$0, EntityPredicate.Builder... $$1) {
         return CriteriaTriggers.KILLED_BY_ARROW
            .createCriterion(
               new KilledByArrowTrigger.TriggerInstance(
                  Optional.empty(),
                  EntityPredicate.wrap($$1),
                  MinMaxBounds.Ints.ANY,
                  Optional.of(ItemPredicate.Builder.item().of($$0, Items.CROSSBOW).build())
               )
            );
      }

      public static Criterion<KilledByArrowTrigger.TriggerInstance> crossbowKilled(HolderGetter<Item> $$0, MinMaxBounds.Ints $$1) {
         return CriteriaTriggers.KILLED_BY_ARROW
            .createCriterion(
               new KilledByArrowTrigger.TriggerInstance(
                  Optional.empty(), List.of(), $$1, Optional.of(ItemPredicate.Builder.item().of($$0, Items.CROSSBOW).build())
               )
            );
      }

      public boolean matches(Collection<LootContext> $$0, int $$1, @Nullable ItemStack $$2) {
         if (!this.firedFromWeapon.isPresent() || $$2 != null && ((ItemPredicate)this.firedFromWeapon.get()).test($$2)) {
            if (!this.victims.isEmpty()) {
               List<LootContext> $$3 = Lists.<LootContext>newArrayList($$0);

               for(ContextAwarePredicate $$4 : this.victims) {
                  boolean $$5 = false;
                  Iterator<LootContext> $$6 = $$3.iterator();

                  while($$6.hasNext()) {
                     LootContext $$7 = (LootContext)$$6.next();
                     if ($$4.matches($$7)) {
                        $$6.remove();
                        $$5 = true;
                        break;
                     }
                  }

                  if (!$$5) {
                     return false;
                  }
               }
            }

            return this.uniqueEntityTypes.matches($$1);
         } else {
            return false;
         }
      }

      @Override
      public void validate(CriterionValidator $$0) {
         SimpleCriterionTrigger.SimpleInstance.super.validate($$0);
         $$0.validateEntities(this.victims, "victims");
      }
   }
}
