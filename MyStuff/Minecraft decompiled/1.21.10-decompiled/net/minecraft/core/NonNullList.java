package net.minecraft.core;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NonNullList<E> extends AbstractList<E> {
   private final List<E> list;
   @Nullable
   private final E defaultValue;

   public static <E> NonNullList<E> create() {
      return new NonNullList<>(Lists.<E>newArrayList(), (E)null);
   }

   public static <E> NonNullList<E> createWithCapacity(int $$0) {
      return new NonNullList<>(Lists.<E>newArrayListWithCapacity($$0), (E)null);
   }

   public static <E> NonNullList<E> withSize(int $$0, E $$1) {
      Objects.requireNonNull($$1);
      Object[] $$2 = new Object[$$0];
      Arrays.fill($$2, $$1);
      return new NonNullList<>(Arrays.asList($$2), $$1);
   }

   @SafeVarargs
   public static <E> NonNullList<E> of(E $$0, E... $$1) {
      return new NonNullList<>(Arrays.asList($$1), $$0);
   }

   protected NonNullList(List<E> $$0, @Nullable E $$1) {
      this.list = $$0;
      this.defaultValue = $$1;
   }

   @Nonnull
   public E get(int $$0) {
      return (E)this.list.get($$0);
   }

   public E set(int $$0, E $$1) {
      Objects.requireNonNull($$1);
      return (E)this.list.set($$0, $$1);
   }

   public void add(int $$0, E $$1) {
      Objects.requireNonNull($$1);
      this.list.add($$0, $$1);
   }

   public E remove(int $$0) {
      return (E)this.list.remove($$0);
   }

   public int size() {
      return this.list.size();
   }

   public void clear() {
      if (this.defaultValue == null) {
         super.clear();
      } else {
         for(int $$0 = 0; $$0 < this.size(); ++$$0) {
            this.set($$0, this.defaultValue);
         }
      }
   }
}
