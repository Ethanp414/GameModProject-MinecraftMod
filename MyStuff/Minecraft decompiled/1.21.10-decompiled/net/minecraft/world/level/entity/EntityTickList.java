package net.minecraft.world.level.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public class EntityTickList {
   private Int2ObjectMap<Entity> active = new Int2ObjectLinkedOpenHashMap<>();
   private Int2ObjectMap<Entity> passive = new Int2ObjectLinkedOpenHashMap<>();
   @Nullable
   private Int2ObjectMap<Entity> iterated;

   private void ensureActiveIsNotIterated() {
      if (this.iterated == this.active) {
         this.passive.clear();

         for(Entry<Entity> $$0 : Int2ObjectMaps.fastIterable(this.active)) {
            this.passive.put($$0.getIntKey(), (Entity)$$0.getValue());
         }

         Int2ObjectMap<Entity> $$1 = this.active;
         this.active = this.passive;
         this.passive = $$1;
      }
   }

   public void add(Entity $$0) {
      this.ensureActiveIsNotIterated();
      this.active.put($$0.getId(), $$0);
   }

   public void remove(Entity $$0) {
      this.ensureActiveIsNotIterated();
      this.active.remove($$0.getId());
   }

   public boolean contains(Entity $$0) {
      return this.active.containsKey($$0.getId());
   }

   public void forEach(Consumer<Entity> $$0) {
      if (this.iterated != null) {
         throw new UnsupportedOperationException("Only one concurrent iteration supported");
      } else {
         this.iterated = this.active;

         try {
            for(Entity $$1 : this.active.values()) {
               $$0.accept($$1);
            }
         } finally {
            this.iterated = null;
         }
      }
   }
}
