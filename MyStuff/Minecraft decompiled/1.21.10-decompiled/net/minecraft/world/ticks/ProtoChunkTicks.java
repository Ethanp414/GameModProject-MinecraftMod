package net.minecraft.world.ticks;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;

public class ProtoChunkTicks<T> implements SerializableTickContainer<T>, TickContainerAccess<T> {
   private final List<SavedTick<T>> ticks = Lists.newArrayList();
   private final Set<SavedTick<?>> ticksPerPosition = new ObjectOpenCustomHashSet(SavedTick.UNIQUE_TICK_HASH);

   @Override
   public void schedule(ScheduledTick<T> $$0) {
      SavedTick<T> $$1 = new SavedTick<>($$0.type(), $$0.pos(), 0, $$0.priority());
      this.schedule($$1);
   }

   private void schedule(SavedTick<T> $$0) {
      if (this.ticksPerPosition.add($$0)) {
         this.ticks.add($$0);
      }
   }

   @Override
   public boolean hasScheduledTick(BlockPos $$0, T $$1) {
      return this.ticksPerPosition.contains(SavedTick.probe($$1, $$0));
   }

   @Override
   public int count() {
      return this.ticks.size();
   }

   @Override
   public List<SavedTick<T>> pack(long $$0) {
      return this.ticks;
   }

   public List<SavedTick<T>> scheduledTicks() {
      return List.copyOf(this.ticks);
   }

   public static <T> ProtoChunkTicks<T> load(List<SavedTick<T>> $$0) {
      ProtoChunkTicks<T> $$1 = new ProtoChunkTicks<>();
      $$0.forEach($$1::schedule);
      return $$1;
   }
}
