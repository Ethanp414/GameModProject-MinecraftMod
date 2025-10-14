package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.Util;

public class ClassInstanceMultiMap<T> extends AbstractCollection<T> {
   private final Map<Class<?>, List<T>> byClass = Maps.newHashMap();
   private final Class<T> baseClass;
   private final List<T> allInstances = Lists.<T>newArrayList();

   public ClassInstanceMultiMap(Class<T> $$0) {
      this.baseClass = $$0;
      this.byClass.put($$0, this.allInstances);
   }

   public boolean add(T $$0) {
      boolean $$1 = false;

      for(Entry<Class<?>, List<T>> $$2 : this.byClass.entrySet()) {
         if (((Class)$$2.getKey()).isInstance($$0)) {
            $$1 |= ((List)$$2.getValue()).add($$0);
         }
      }

      return $$1;
   }

   public boolean remove(Object $$0) {
      boolean $$1 = false;

      for(Entry<Class<?>, List<T>> $$2 : this.byClass.entrySet()) {
         if (((Class)$$2.getKey()).isInstance($$0)) {
            List<T> $$3 = (List)$$2.getValue();
            $$1 |= $$3.remove($$0);
         }
      }

      return $$1;
   }

   public boolean contains(Object $$0) {
      return this.find($$0.getClass()).contains($$0);
   }

   public <S> Collection<S> find(Class<S> $$0) {
      if (!this.baseClass.isAssignableFrom($$0)) {
         throw new IllegalArgumentException("Don't know how to search for " + $$0);
      } else {
         List<? extends T> $$1 = (List)this.byClass
            .computeIfAbsent($$0, $$0x -> (List)this.allInstances.stream().filter($$0x::isInstance).collect(Util.toMutableList()));
         return Collections.unmodifiableCollection($$1);
      }
   }

   public Iterator<T> iterator() {
      return (Iterator<T>)(this.allInstances.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allInstances.iterator()));
   }

   public List<T> getAllInstances() {
      return ImmutableList.copyOf(this.allInstances);
   }

   public int size() {
      return this.allInstances.size();
   }
}
