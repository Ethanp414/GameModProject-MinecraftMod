package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public class ArrayListDeque<T> extends AbstractList<T> implements ListAndDeque<T> {
   private static final int MIN_GROWTH = 1;
   private Object[] contents;
   private int head;
   private int size;

   public ArrayListDeque() {
      this(1);
   }

   public ArrayListDeque(int $$0) {
      this.contents = new Object[$$0];
      this.head = 0;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   @VisibleForTesting
   public int capacity() {
      return this.contents.length;
   }

   private int getIndex(int $$0) {
      return ($$0 + this.head) % this.contents.length;
   }

   public T get(int $$0) {
      this.verifyIndexInRange($$0);
      return this.getInner(this.getIndex($$0));
   }

   private static void verifyIndexInRange(int $$0, int $$1) {
      if ($$0 < 0 || $$0 >= $$1) {
         throw new IndexOutOfBoundsException($$0);
      }
   }

   private void verifyIndexInRange(int $$0) {
      verifyIndexInRange($$0, this.size);
   }

   private T getInner(int $$0) {
      return (T)this.contents[$$0];
   }

   public T set(int $$0, T $$1) {
      this.verifyIndexInRange($$0);
      Objects.requireNonNull($$1);
      int $$2 = this.getIndex($$0);
      T $$3 = this.getInner($$2);
      this.contents[$$2] = $$1;
      return $$3;
   }

   public void add(int $$0, T $$1) {
      verifyIndexInRange($$0, this.size + 1);
      Objects.requireNonNull($$1);
      if (this.size == this.contents.length) {
         this.grow();
      }

      int $$2 = this.getIndex($$0);
      if ($$0 == this.size) {
         this.contents[$$2] = $$1;
      } else if ($$0 == 0) {
         --this.head;
         if (this.head < 0) {
            this.head += this.contents.length;
         }

         this.contents[this.getIndex(0)] = $$1;
      } else {
         for(int $$3 = this.size - 1; $$3 >= $$0; --$$3) {
            this.contents[this.getIndex($$3 + 1)] = this.contents[this.getIndex($$3)];
         }

         this.contents[$$2] = $$1;
      }

      ++this.modCount;
      ++this.size;
   }

   private void grow() {
      int $$0 = this.contents.length + Math.max(this.contents.length >> 1, 1);
      Object[] $$1 = new Object[$$0];
      this.copyCount($$1, this.size);
      this.head = 0;
      this.contents = $$1;
   }

   public T remove(int $$0) {
      this.verifyIndexInRange($$0);
      int $$1 = this.getIndex($$0);
      T $$2 = this.getInner($$1);
      if ($$0 == 0) {
         this.contents[$$1] = null;
         ++this.head;
      } else if ($$0 == this.size - 1) {
         this.contents[$$1] = null;
      } else {
         for(int $$3 = $$0 + 1; $$3 < this.size; ++$$3) {
            this.contents[this.getIndex($$3 - 1)] = this.get($$3);
         }

         this.contents[this.getIndex(this.size - 1)] = null;
      }

      ++this.modCount;
      --this.size;
      return $$2;
   }

   public boolean removeIf(Predicate<? super T> $$0) {
      int $$1 = 0;

      for(int $$2 = 0; $$2 < this.size; ++$$2) {
         T $$3 = this.get($$2);
         if ($$0.test($$3)) {
            ++$$1;
         } else if ($$1 != 0) {
            this.contents[this.getIndex($$2 - $$1)] = $$3;
            this.contents[this.getIndex($$2)] = null;
         }
      }

      this.modCount += $$1;
      this.size -= $$1;
      return $$1 != 0;
   }

   private void copyCount(Object[] $$0, int $$1) {
      for(int $$2 = 0; $$2 < $$1; ++$$2) {
         $$0[$$2] = this.get($$2);
      }
   }

   public void replaceAll(UnaryOperator<T> $$0) {
      for(int $$1 = 0; $$1 < this.size; ++$$1) {
         int $$2 = this.getIndex($$1);
         this.contents[$$2] = Objects.requireNonNull($$0.apply(this.getInner($$1)));
      }
   }

   public void forEach(Consumer<? super T> $$0) {
      for(int $$1 = 0; $$1 < this.size; ++$$1) {
         $$0.accept(this.get($$1));
      }
   }

   @Override
   public void addFirst(T $$0) {
      this.add(0, $$0);
   }

   @Override
   public void addLast(T $$0) {
      this.add(this.size, $$0);
   }

   public boolean offerFirst(T $$0) {
      this.addFirst($$0);
      return true;
   }

   public boolean offerLast(T $$0) {
      this.addLast($$0);
      return true;
   }

   @Override
   public T removeFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.remove(0);
      }
   }

   @Override
   public T removeLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.size - 1);
      }
   }

   @Override
   public ListAndDeque<T> reversed() {
      return new ArrayListDeque.ReversedView(this);
   }

   @Nullable
   public T pollFirst() {
      return this.size == 0 ? null : this.removeFirst();
   }

   @Nullable
   public T pollLast() {
      return this.size == 0 ? null : this.removeLast();
   }

   @Override
   public T getFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.get(0);
      }
   }

   @Override
   public T getLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.size - 1);
      }
   }

   @Nullable
   public T peekFirst() {
      return this.size == 0 ? null : this.getFirst();
   }

   @Nullable
   public T peekLast() {
      return this.size == 0 ? null : this.getLast();
   }

   public boolean removeFirstOccurrence(Object $$0) {
      for(int $$1 = 0; $$1 < this.size; ++$$1) {
         T $$2 = this.get($$1);
         if (Objects.equals($$0, $$2)) {
            this.remove($$1);
            return true;
         }
      }

      return false;
   }

   public boolean removeLastOccurrence(Object $$0) {
      for(int $$1 = this.size - 1; $$1 >= 0; --$$1) {
         T $$2 = this.get($$1);
         if (Objects.equals($$0, $$2)) {
            this.remove($$1);
            return true;
         }
      }

      return false;
   }

   public Iterator<T> descendingIterator() {
      return new ArrayListDeque.DescendingIterator();
   }

   class DescendingIterator implements Iterator<T> {
      private int index = ArrayListDeque.this.size() - 1;

      public DescendingIterator() {
      }

      public boolean hasNext() {
         return this.index >= 0;
      }

      public T next() {
         return ArrayListDeque.this.get(this.index--);
      }

      public void remove() {
         ArrayListDeque.this.remove(this.index + 1);
      }
   }

   class ReversedView extends AbstractList<T> implements ListAndDeque<T> {
      private final ArrayListDeque<T> source;

      public ReversedView(final ArrayListDeque<T> param2) {
         this.source = $$0;
      }

      @Override
      public ListAndDeque<T> reversed() {
         return this.source;
      }

      @Override
      public T getFirst() {
         return this.source.getLast();
      }

      @Override
      public T getLast() {
         return this.source.getFirst();
      }

      @Override
      public void addFirst(T $$0) {
         this.source.addLast($$0);
      }

      @Override
      public void addLast(T $$0) {
         this.source.addFirst($$0);
      }

      public boolean offerFirst(T $$0) {
         return this.source.offerLast($$0);
      }

      public boolean offerLast(T $$0) {
         return this.source.offerFirst($$0);
      }

      public T pollFirst() {
         return this.source.pollLast();
      }

      public T pollLast() {
         return this.source.pollFirst();
      }

      public T peekFirst() {
         return this.source.peekLast();
      }

      public T peekLast() {
         return this.source.peekFirst();
      }

      @Override
      public T removeFirst() {
         return this.source.removeLast();
      }

      @Override
      public T removeLast() {
         return this.source.removeFirst();
      }

      public boolean removeFirstOccurrence(Object $$0) {
         return this.source.removeLastOccurrence($$0);
      }

      public boolean removeLastOccurrence(Object $$0) {
         return this.source.removeFirstOccurrence($$0);
      }

      public Iterator<T> descendingIterator() {
         return this.source.iterator();
      }

      public int size() {
         return this.source.size();
      }

      public boolean isEmpty() {
         return this.source.isEmpty();
      }

      public boolean contains(Object $$0) {
         return this.source.contains($$0);
      }

      public T get(int $$0) {
         return this.source.get(this.reverseIndex($$0));
      }

      public T set(int $$0, T $$1) {
         return this.source.set(this.reverseIndex($$0), $$1);
      }

      public void add(int $$0, T $$1) {
         this.source.add(this.reverseIndex($$0) + 1, $$1);
      }

      public T remove(int $$0) {
         return this.source.remove(this.reverseIndex($$0));
      }

      public int indexOf(Object $$0) {
         return this.reverseIndex(this.source.lastIndexOf($$0));
      }

      public int lastIndexOf(Object $$0) {
         return this.reverseIndex(this.source.indexOf($$0));
      }

      public List<T> subList(int $$0, int $$1) {
         return this.source.subList(this.reverseIndex($$1) + 1, this.reverseIndex($$0) + 1).reversed();
      }

      public Iterator<T> iterator() {
         return this.source.descendingIterator();
      }

      public void clear() {
         this.source.clear();
      }

      private int reverseIndex(int $$0) {
         return $$0 == -1 ? -1 : this.source.size() - 1 - $$0;
      }
   }
}
