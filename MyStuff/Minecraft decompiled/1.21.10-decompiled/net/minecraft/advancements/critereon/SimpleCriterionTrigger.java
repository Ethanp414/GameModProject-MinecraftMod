package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class SimpleCriterionTrigger<T extends SimpleCriterionTrigger.SimpleInstance> implements CriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

   @Override
   public final void addPlayerListener(PlayerAdvancements $$0, CriterionTrigger.Listener<T> $$1) {
      ((Set)this.players.computeIfAbsent($$0, $$0x -> Sets.newHashSet())).add($$1);
   }

   @Override
   public final void removePlayerListener(PlayerAdvancements $$0, CriterionTrigger.Listener<T> $$1) {
      Set<CriterionTrigger.Listener<T>> $$2 = (Set)this.players.get($$0);
      if ($$2 != null) {
         $$2.remove($$1);
         if ($$2.isEmpty()) {
            this.players.remove($$0);
         }
      }
   }

   @Override
   public final void removePlayerListeners(PlayerAdvancements $$0) {
      this.players.remove($$0);
   }

   protected void trigger(ServerPlayer $$0, Predicate<T> $$1) {
      PlayerAdvancements $$2 = $$0.getAdvancements();
      Set<CriterionTrigger.Listener<T>> $$3 = (Set)this.players.get($$2);
      if ($$3 != null && !$$3.isEmpty()) {
         LootContext $$4 = EntityPredicate.createContext($$0, $$0);
         List<CriterionTrigger.Listener<T>> $$5 = null;

         for(CriterionTrigger.Listener<T> $$6 : $$3) {
            T $$7 = $$6.trigger();
            if ($$1.test($$7)) {
               Optional<ContextAwarePredicate> $$8 = $$7.player();
               if ($$8.isEmpty() || ((ContextAwarePredicate)$$8.get()).matches($$4)) {
                  if ($$5 == null) {
                     $$5 = Lists.newArrayList();
                  }

                  $$5.add($$6);
               }
            }
         }

         if ($$5 != null) {
            for(CriterionTrigger.Listener<T> $$9 : $$5) {
               $$9.run($$2);
            }
         }
      }
   }

   public interface SimpleInstance extends CriterionTriggerInstance {
      @Override
      default void validate(CriterionValidator $$0) {
         $$0.validateEntity(this.player(), "player");
      }

      Optional<ContextAwarePredicate> player();
   }
}
