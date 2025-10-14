package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class DebugBuffer<T> {
   private final AtomicReferenceArray<T> data;
   private final AtomicInteger index;

   public DebugBuffer(int $$0) {
      this.data = new AtomicReferenceArray($$0);
      this.index = new AtomicInteger(0);
   }

   public void push(T $$0) {
      int $$1 = this.data.length();

      int $$2;
      int $$3;
      do {
         $$2 = this.index.get();
         $$3 = ($$2 + 1) % $$1;
      } while(!this.index.compareAndSet($$2, $$3));

      this.data.set($$3, $$0);
   }

   public List<T> dump() {
      int $$0 = this.index.get();
      Builder<T> $$1 = ImmutableList.builder();

      for(int $$2 = 0; $$2 < this.data.length(); ++$$2) {
         int $$3 = Math.floorMod($$0 - $$2, this.data.length());
         T $$4 = (T)this.data.get($$3);
         if ($$4 != null) {
            $$1.add($$4);
         }
      }

      return $$1.build();
   }
}
