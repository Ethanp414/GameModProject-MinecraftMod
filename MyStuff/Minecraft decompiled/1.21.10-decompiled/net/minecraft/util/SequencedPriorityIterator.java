package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.util.Deque;
import javax.annotation.Nullable;

public final class SequencedPriorityIterator<T> extends AbstractIterator<T> {
   private static final int MIN_PRIO = Integer.MIN_VALUE;
   @Nullable
   private Deque<T> highestPrioQueue = null;
   private int highestPrio = Integer.MIN_VALUE;
   private final Int2ObjectMap<Deque<T>> queuesByPriority = new Int2ObjectOpenHashMap();

   public void add(T $$0, int $$1) {
      if ($$1 == this.highestPrio && this.highestPrioQueue != null) {
         this.highestPrioQueue.addLast($$0);
      } else {
         Deque<T> $$2 = (Deque)this.queuesByPriority.computeIfAbsent($$1, (Int2ObjectFunction)($$0x -> Queues.newArrayDeque()));
         $$2.addLast($$0);
         if ($$1 >= this.highestPrio) {
            this.highestPrioQueue = $$2;
            this.highestPrio = $$1;
         }
      }
   }

   @Nullable
   @Override
   protected T computeNext() {
      if (this.highestPrioQueue == null) {
         return this.endOfData();
      } else {
         T $$0 = (T)this.highestPrioQueue.removeFirst();
         if ($$0 == null) {
            return this.endOfData();
         } else {
            if (this.highestPrioQueue.isEmpty()) {
               this.switchCacheToNextHighestPrioQueue();
            }

            return $$0;
         }
      }
   }

   private void switchCacheToNextHighestPrioQueue() {
      int $$0 = Integer.MIN_VALUE;
      Deque<T> $$1 = null;

      for(Entry<Deque<T>> $$2 : Int2ObjectMaps.fastIterable(this.queuesByPriority)) {
         Deque<T> $$3 = (Deque)$$2.getValue();
         int $$4 = $$2.getIntKey();
         if ($$4 > $$0 && !$$3.isEmpty()) {
            $$0 = $$4;
            $$1 = $$3;
            if ($$4 == this.highestPrio - 1) {
               break;
            }
         }
      }

      this.highestPrio = $$0;
      this.highestPrioQueue = $$1;
   }
}
