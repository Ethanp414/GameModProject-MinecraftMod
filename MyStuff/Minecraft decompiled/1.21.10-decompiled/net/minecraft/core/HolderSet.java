package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.VisibleForTesting;

public interface HolderSet<T> extends Iterable<Holder<T>> {
   Stream<Holder<T>> stream();

   int size();

   boolean isBound();

   Either<TagKey<T>, List<Holder<T>>> unwrap();

   Optional<Holder<T>> getRandomElement(RandomSource var1);

   Holder<T> get(int var1);

   boolean contains(Holder<T> var1);

   boolean canSerializeIn(HolderOwner<T> var1);

   Optional<TagKey<T>> unwrapKey();

   @Deprecated
   @VisibleForTesting
   static <T> HolderSet.Named<T> emptyNamed(HolderOwner<T> $$0, TagKey<T> $$1) {
      return new HolderSet.Named<T>($$0, $$1) {
         @Override
         protected List<Holder<T>> contents() {
            throw new UnsupportedOperationException("Tag " + this.key() + " can't be dereferenced during construction");
         }
      };
   }

   static <T> HolderSet<T> empty() {
      return HolderSet.Direct.EMPTY;
   }

   @SafeVarargs
   static <T> HolderSet.Direct<T> direct(Holder<T>... $$0) {
      return new HolderSet.Direct<>(List.of($$0));
   }

   static <T> HolderSet.Direct<T> direct(List<? extends Holder<T>> $$0) {
      return new HolderSet.Direct<>(List.copyOf($$0));
   }

   @SafeVarargs
   static <E, T> HolderSet.Direct<T> direct(Function<E, Holder<T>> $$0, E... $$1) {
      return direct(Stream.of($$1).map($$0).toList());
   }

   static <E, T> HolderSet.Direct<T> direct(Function<E, Holder<T>> $$0, Collection<E> $$1) {
      return direct($$1.stream().map($$0).toList());
   }

   public static final class Direct<T> extends HolderSet.ListBacked<T> {
      static final HolderSet.Direct<?> EMPTY = new HolderSet.Direct(List.of());
      private final List<Holder<T>> contents;
      @Nullable
      private Set<Holder<T>> contentsSet;

      Direct(List<Holder<T>> $$0) {
         this.contents = $$0;
      }

      @Override
      protected List<Holder<T>> contents() {
         return this.contents;
      }

      @Override
      public boolean isBound() {
         return true;
      }

      @Override
      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.right(this.contents);
      }

      @Override
      public Optional<TagKey<T>> unwrapKey() {
         return Optional.empty();
      }

      @Override
      public boolean contains(Holder<T> $$0) {
         if (this.contentsSet == null) {
            this.contentsSet = Set.copyOf(this.contents);
         }

         return this.contentsSet.contains($$0);
      }

      public String toString() {
         return "DirectSet[" + this.contents + "]";
      }

      public boolean equals(Object $$0) {
         if (this == $$0) {
            return true;
         } else {
            if ($$0 instanceof HolderSet.Direct $$1 && this.contents.equals($$1.contents)) {
               return true;
            }

            return false;
         }
      }

      public int hashCode() {
         return this.contents.hashCode();
      }
   }

   public abstract static class ListBacked<T> implements HolderSet<T> {
      protected abstract List<Holder<T>> contents();

      @Override
      public int size() {
         return this.contents().size();
      }

      public Spliterator<Holder<T>> spliterator() {
         return this.contents().spliterator();
      }

      public Iterator<Holder<T>> iterator() {
         return this.contents().iterator();
      }

      @Override
      public Stream<Holder<T>> stream() {
         return this.contents().stream();
      }

      @Override
      public Optional<Holder<T>> getRandomElement(RandomSource $$0) {
         return Util.getRandomSafe(this.contents(), $$0);
      }

      @Override
      public Holder<T> get(int $$0) {
         return (Holder<T>)this.contents().get($$0);
      }

      @Override
      public boolean canSerializeIn(HolderOwner<T> $$0) {
         return true;
      }
   }

   public static class Named<T> extends HolderSet.ListBacked<T> {
      private final HolderOwner<T> owner;
      private final TagKey<T> key;
      @Nullable
      private List<Holder<T>> contents;

      Named(HolderOwner<T> $$0, TagKey<T> $$1) {
         this.owner = $$0;
         this.key = $$1;
      }

      void bind(List<Holder<T>> $$0) {
         this.contents = List.copyOf($$0);
      }

      public TagKey<T> key() {
         return this.key;
      }

      @Override
      protected List<Holder<T>> contents() {
         if (this.contents == null) {
            throw new IllegalStateException("Trying to access unbound tag '" + this.key + "' from registry " + this.owner);
         } else {
            return this.contents;
         }
      }

      @Override
      public boolean isBound() {
         return this.contents != null;
      }

      @Override
      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.left(this.key);
      }

      @Override
      public Optional<TagKey<T>> unwrapKey() {
         return Optional.of(this.key);
      }

      @Override
      public boolean contains(Holder<T> $$0) {
         return $$0.is(this.key);
      }

      public String toString() {
         return "NamedSet(" + this.key + ")[" + this.contents + "]";
      }

      @Override
      public boolean canSerializeIn(HolderOwner<T> $$0) {
         return this.owner.canSerializeIn($$0);
      }
   }
}
