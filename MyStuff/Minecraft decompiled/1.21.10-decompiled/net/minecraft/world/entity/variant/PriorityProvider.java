package net.minecraft.world.entity.variant;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

public interface PriorityProvider<Context, Condition extends PriorityProvider.SelectorCondition<Context>> {
   List<PriorityProvider.Selector<Context, Condition>> selectors();

   static <C, T> Stream<T> select(Stream<T> $$0, Function<T, PriorityProvider<C, ?>> $$1, C $$2) {
      List<PriorityProvider.UnpackedEntry<C, T>> $$3 = new ArrayList();
      $$0.forEach(
         $$2x -> {
            PriorityProvider<C, ?> $$3xx = (PriorityProvider)$$1.apply($$2x);
   
            for(PriorityProvider.Selector<C, ?> $$4xx : $$3xx.selectors()) {
               $$3.add(
                  new PriorityProvider.UnpackedEntry(
                     $$2x, $$4xx.priority(), DataFixUtils.orElseGet($$4xx.condition(), PriorityProvider.SelectorCondition::alwaysTrue)
                  )
               );
            }
         }
      );
      $$3.sort(PriorityProvider.UnpackedEntry.HIGHEST_PRIORITY_FIRST);
      Iterator<PriorityProvider.UnpackedEntry<C, T>> $$4 = $$3.iterator();
      int $$5 = Integer.MIN_VALUE;

      while($$4.hasNext()) {
         PriorityProvider.UnpackedEntry<C, T> $$6 = (PriorityProvider.UnpackedEntry)$$4.next();
         if ($$6.priority < $$5) {
            $$4.remove();
         } else if ($$6.condition.test($$2)) {
            $$5 = $$6.priority;
         } else {
            $$4.remove();
         }
      }

      return $$3.stream().map(PriorityProvider.UnpackedEntry::entry);
   }

   static <C, T> Optional<T> pick(Stream<T> $$0, Function<T, PriorityProvider<C, ?>> $$1, RandomSource $$2, C $$3) {
      List<T> $$4 = select($$0, $$1, $$3).toList();
      return Util.getRandomSafe($$4, $$2);
   }

   static <Context, Condition extends PriorityProvider.SelectorCondition<Context>> List<PriorityProvider.Selector<Context, Condition>> single(
      Condition $$0, int $$1
   ) {
      return List.of(new PriorityProvider.Selector($$0, $$1));
   }

   static <Context, Condition extends PriorityProvider.SelectorCondition<Context>> List<PriorityProvider.Selector<Context, Condition>> alwaysTrue(int $$0) {
      return List.of(new PriorityProvider.Selector(Optional.empty(), $$0));
   }

   public static record Selector<Context, Condition extends PriorityProvider.SelectorCondition<Context>>(Optional<Condition> condition, int priority) {
      public Selector(Condition $$0, int $$1) {
         this(Optional.of($$0), $$1);
      }

      public Selector(int $$0) {
         this(Optional.empty(), $$0);
      }

      public static <Context, Condition extends PriorityProvider.SelectorCondition<Context>> Codec<PriorityProvider.Selector<Context, Condition>> codec(
         Codec<Condition> $$0
      ) {
         return RecordCodecBuilder.create(
            $$1 -> $$1.group(
                     $$0.optionalFieldOf("condition").forGetter(PriorityProvider.Selector::condition),
                     Codec.INT.fieldOf("priority").forGetter(PriorityProvider.Selector::priority)
                  )
                  .apply($$1, PriorityProvider.Selector::new)
         );
      }
   }

   @FunctionalInterface
   public interface SelectorCondition<C> extends Predicate<C> {
      static <C> PriorityProvider.SelectorCondition<C> alwaysTrue() {
         return $$0 -> true;
      }
   }

   public static record UnpackedEntry<C, T>(T entry, int priority, PriorityProvider.SelectorCondition<C> condition) {
      final int priority;
      final PriorityProvider.SelectorCondition<C> condition;
      public static final Comparator<PriorityProvider.UnpackedEntry<?, ?>> HIGHEST_PRIORITY_FIRST = Comparator.comparingInt(
            PriorityProvider.UnpackedEntry::priority
         )
         .reversed();
   }
}
