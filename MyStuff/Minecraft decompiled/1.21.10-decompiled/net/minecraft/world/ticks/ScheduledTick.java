package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public record ScheduledTick<T>(T type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
   public static final Comparator<ScheduledTick<?>> DRAIN_ORDER = ($$0, $$1) -> {
      int $$2 = Long.compare($$0.triggerTick, $$1.triggerTick);
      if ($$2 != 0) {
         return $$2;
      } else {
         $$2 = $$0.priority.compareTo($$1.priority);
         return $$2 != 0 ? $$2 : Long.compare($$0.subTickOrder, $$1.subTickOrder);
      }
   };
   public static final Comparator<ScheduledTick<?>> INTRA_TICK_DRAIN_ORDER = ($$0, $$1) -> {
      int $$2 = $$0.priority.compareTo($$1.priority);
      return $$2 != 0 ? $$2 : Long.compare($$0.subTickOrder, $$1.subTickOrder);
   };
   public static final Strategy<ScheduledTick<?>> UNIQUE_TICK_HASH = new Strategy<ScheduledTick<?>>() {
      public int hashCode(ScheduledTick<?> $$0) {
         return 31 * $$0.pos().hashCode() + $$0.type().hashCode();
      }

      public boolean equals(@Nullable ScheduledTick<?> $$0, @Nullable ScheduledTick<?> $$1) {
         if ($$0 == $$1) {
            return true;
         } else if ($$0 != null && $$1 != null) {
            return $$0.type() == $$1.type() && $$0.pos().equals($$1.pos());
         } else {
            return false;
         }
      }
   };

   public ScheduledTick(T $$0, BlockPos $$1, long $$2, long $$3) {
      this($$0, $$1, $$2, TickPriority.NORMAL, $$3);
   }

   public ScheduledTick(T param1, BlockPos param2, long param3, TickPriority param5, long param6) {
      $$1 = $$1.immutable();
      this.type = $$0;
      this.pos = $$1;
      this.triggerTick = $$2;
      this.priority = $$3;
      this.subTickOrder = $$4;
   }

   public static <T> ScheduledTick<T> probe(T $$0, BlockPos $$1) {
      return new ScheduledTick<>($$0, $$1, 0L, TickPriority.NORMAL, 0L);
   }

   public SavedTick<T> toSavedTick(long $$0) {
      return new SavedTick<>(this.type, this.pos, (int)(this.triggerTick - $$0), this.priority);
   }
}
