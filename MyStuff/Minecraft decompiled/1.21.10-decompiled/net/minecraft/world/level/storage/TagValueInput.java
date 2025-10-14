package net.minecraft.world.level.storage;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.DataResult.Success;
import java.lang.runtime.SwitchBootstraps;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.util.ProblemReporter;

public class TagValueInput implements ValueInput {
   private final ProblemReporter problemReporter;
   private final ValueInputContextHelper context;
   private final CompoundTag input;

   private TagValueInput(ProblemReporter $$0, ValueInputContextHelper $$1, CompoundTag $$2) {
      this.problemReporter = $$0;
      this.context = $$1;
      this.input = $$2;
   }

   public static ValueInput create(ProblemReporter $$0, HolderLookup.Provider $$1, CompoundTag $$2) {
      return new TagValueInput($$0, new ValueInputContextHelper($$1, NbtOps.INSTANCE), $$2);
   }

   public static ValueInput.ValueInputList create(ProblemReporter $$0, HolderLookup.Provider $$1, List<CompoundTag> $$2) {
      return new TagValueInput.CompoundListWrapper($$0, new ValueInputContextHelper($$1, NbtOps.INSTANCE), $$2);
   }

   @Override
   public <T> Optional<T> read(String $$0, Codec<T> $$1) {
      Tag $$2 = this.input.get($$0);
      if ($$2 == null) {
         return Optional.empty();
      } else {
         Object var10000;
         Objects.requireNonNull(var10000);
         Object var4 = var10000;
         byte var5 = 0;

         $$1.parse(this.context.ops(), $$2);
         return switch(SwitchBootstraps.typeSwitch<"typeSwitch",Success,Error>(var4, var5)) {
            case 0 -> {
               Success<T> $$3 = (Success)var4;
               yield Optional.of($$3.value());
            }
            case 1 -> {
               Error<T> $$4 = (Error)var4;
               this.problemReporter.report(new TagValueInput.DecodeFromFieldFailedProblem($$0, $$2, $$4));
               yield $$4.partialValue();
            }
            default -> throw new MatchException(null, null);
         };
      }
   }

   @Override
   public <T> Optional<T> read(MapCodec<T> $$0) {
      DynamicOps<Tag> $$1 = this.context.ops();
      Object var10000;
      Objects.requireNonNull(var10000);
      Object var3 = var10000;
      byte var4 = 0;

      $$1.getMap(this.input).flatMap($$2x -> $$0.decode($$1, $$2x));
      return switch(SwitchBootstraps.typeSwitch<"typeSwitch",Success,Error>(var3, var4)) {
         case 0 -> {
            Success<T> $$2 = (Success)var3;
            yield Optional.of($$2.value());
         }
         case 1 -> {
            Error<T> $$3 = (Error)var3;
            this.problemReporter.report(new TagValueInput.DecodeFromMapFailedProblem($$3));
            yield $$3.partialValue();
         }
         default -> throw new MatchException(null, null);
      };
   }

   @Nullable
   private <T extends Tag> T getOptionalTypedTag(String $$0, TagType<T> $$1) {
      Tag $$2 = this.input.get($$0);
      if ($$2 == null) {
         return null;
      } else {
         TagType<?> $$3 = $$2.getType();
         if ($$3 != $$1) {
            this.problemReporter.report(new TagValueInput.UnexpectedTypeProblem($$0, $$1, $$3));
            return null;
         } else {
            return (T)$$2;
         }
      }
   }

   @Nullable
   private NumericTag getNumericTag(String $$0) {
      Tag $$1 = this.input.get($$0);
      if ($$1 == null) {
         return null;
      } else if ($$1 instanceof NumericTag) {
         return (NumericTag)$$1;
      } else {
         this.problemReporter.report(new TagValueInput.UnexpectedNonNumberProblem($$0, $$1.getType()));
         return null;
      }
   }

   @Override
   public Optional<ValueInput> child(String $$0) {
      CompoundTag $$1 = this.getOptionalTypedTag($$0, CompoundTag.TYPE);
      return $$1 != null ? Optional.of(this.wrapChild($$0, $$1)) : Optional.empty();
   }

   @Override
   public ValueInput childOrEmpty(String $$0) {
      CompoundTag $$1 = this.getOptionalTypedTag($$0, CompoundTag.TYPE);
      return $$1 != null ? this.wrapChild($$0, $$1) : this.context.empty();
   }

   @Override
   public Optional<ValueInput.ValueInputList> childrenList(String $$0) {
      ListTag $$1 = this.getOptionalTypedTag($$0, ListTag.TYPE);
      return $$1 != null ? Optional.of(this.wrapList($$0, this.context, $$1)) : Optional.empty();
   }

   @Override
   public ValueInput.ValueInputList childrenListOrEmpty(String $$0) {
      ListTag $$1 = this.getOptionalTypedTag($$0, ListTag.TYPE);
      return $$1 != null ? this.wrapList($$0, this.context, $$1) : this.context.emptyList();
   }

   @Override
   public <T> Optional<ValueInput.TypedInputList<T>> list(String $$0, Codec<T> $$1) {
      ListTag $$2 = this.getOptionalTypedTag($$0, ListTag.TYPE);
      return $$2 != null ? Optional.of(this.wrapTypedList($$0, $$2, $$1)) : Optional.empty();
   }

   @Override
   public <T> ValueInput.TypedInputList<T> listOrEmpty(String $$0, Codec<T> $$1) {
      ListTag $$2 = this.getOptionalTypedTag($$0, ListTag.TYPE);
      return $$2 != null ? this.wrapTypedList($$0, $$2, $$1) : this.context.emptyTypedList();
   }

   @Override
   public boolean getBooleanOr(String $$0, boolean $$1) {
      NumericTag $$2 = this.getNumericTag($$0);
      return $$2 != null ? $$2.byteValue() != 0 : $$1;
   }

   @Override
   public byte getByteOr(String $$0, byte $$1) {
      NumericTag $$2 = this.getNumericTag($$0);
      return $$2 != null ? $$2.byteValue() : $$1;
   }

   @Override
   public int getShortOr(String $$0, short $$1) {
      NumericTag $$2 = this.getNumericTag($$0);
      return $$2 != null ? $$2.shortValue() : $$1;
   }

   @Override
   public Optional<Integer> getInt(String $$0) {
      NumericTag $$1 = this.getNumericTag($$0);
      return $$1 != null ? Optional.of($$1.intValue()) : Optional.empty();
   }

   @Override
   public int getIntOr(String $$0, int $$1) {
      NumericTag $$2 = this.getNumericTag($$0);
      return $$2 != null ? $$2.intValue() : $$1;
   }

   @Override
   public long getLongOr(String $$0, long $$1) {
      NumericTag $$2 = this.getNumericTag($$0);
      return $$2 != null ? $$2.longValue() : $$1;
   }

   @Override
   public Optional<Long> getLong(String $$0) {
      NumericTag $$1 = this.getNumericTag($$0);
      return $$1 != null ? Optional.of($$1.longValue()) : Optional.empty();
   }

   @Override
   public float getFloatOr(String $$0, float $$1) {
      NumericTag $$2 = this.getNumericTag($$0);
      return $$2 != null ? $$2.floatValue() : $$1;
   }

   @Override
   public double getDoubleOr(String $$0, double $$1) {
      NumericTag $$2 = this.getNumericTag($$0);
      return $$2 != null ? $$2.doubleValue() : $$1;
   }

   @Override
   public Optional<String> getString(String $$0) {
      StringTag $$1 = this.getOptionalTypedTag($$0, StringTag.TYPE);
      return $$1 != null ? Optional.of($$1.value()) : Optional.empty();
   }

   @Override
   public String getStringOr(String $$0, String $$1) {
      StringTag $$2 = this.getOptionalTypedTag($$0, StringTag.TYPE);
      return $$2 != null ? $$2.value() : $$1;
   }

   @Override
   public Optional<int[]> getIntArray(String $$0) {
      IntArrayTag $$1 = this.getOptionalTypedTag($$0, IntArrayTag.TYPE);
      return $$1 != null ? Optional.of($$1.getAsIntArray()) : Optional.empty();
   }

   @Override
   public HolderLookup.Provider lookup() {
      return this.context.lookup();
   }

   private ValueInput wrapChild(String $$0, CompoundTag $$1) {
      return (ValueInput)($$1.isEmpty()
         ? this.context.empty()
         : new TagValueInput(this.problemReporter.forChild(new ProblemReporter.FieldPathElement($$0)), this.context, $$1));
   }

   static ValueInput wrapChild(ProblemReporter $$0, ValueInputContextHelper $$1, CompoundTag $$2) {
      return (ValueInput)($$2.isEmpty() ? $$1.empty() : new TagValueInput($$0, $$1, $$2));
   }

   private ValueInput.ValueInputList wrapList(String $$0, ValueInputContextHelper $$1, ListTag $$2) {
      return (ValueInput.ValueInputList)($$2.isEmpty() ? $$1.emptyList() : new TagValueInput.ListWrapper(this.problemReporter, $$0, $$1, $$2));
   }

   private <T> ValueInput.TypedInputList<T> wrapTypedList(String $$0, ListTag $$1, Codec<T> $$2) {
      return (ValueInput.TypedInputList<T>)($$1.isEmpty()
         ? this.context.emptyTypedList()
         : new TagValueInput.TypedListWrapper<>(this.problemReporter, $$0, this.context, $$2, $$1));
   }

   static class CompoundListWrapper implements ValueInput.ValueInputList {
      private final ProblemReporter problemReporter;
      private final ValueInputContextHelper context;
      private final List<CompoundTag> list;

      public CompoundListWrapper(ProblemReporter $$0, ValueInputContextHelper $$1, List<CompoundTag> $$2) {
         this.problemReporter = $$0;
         this.context = $$1;
         this.list = $$2;
      }

      ValueInput wrapChild(int $$0, CompoundTag $$1) {
         return TagValueInput.wrapChild(this.problemReporter.forChild(new ProblemReporter.IndexedPathElement($$0)), this.context, $$1);
      }

      @Override
      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      @Override
      public Stream<ValueInput> stream() {
         return Streams.mapWithIndex(this.list.stream(), ($$0, $$1) -> this.wrapChild((int)$$1, $$0));
      }

      public Iterator<ValueInput> iterator() {
         final ListIterator<CompoundTag> $$0 = this.list.listIterator();
         return new AbstractIterator<ValueInput>() {
            @Nullable
            protected ValueInput computeNext() {
               if ($$0.hasNext()) {
                  int $$0x = $$0.nextIndex();
                  CompoundTag $$1 = (CompoundTag)$$0.next();
                  return CompoundListWrapper.this.wrapChild($$0x, $$1);
               } else {
                  return this.endOfData();
               }
            }
         };
      }
   }

   public static record DecodeFromFieldFailedProblem(String name, Tag tag, Error<?> error) implements ProblemReporter.Problem {
      @Override
      public String description() {
         return "Failed to decode value '" + this.tag + "' from field '" + this.name + "': " + this.error.message();
      }
   }

   public static record DecodeFromListFailedProblem(String name, int index, Tag tag, Error<?> error) implements ProblemReporter.Problem {
      @Override
      public String description() {
         return "Failed to decode value '" + this.tag + "' from field '" + this.name + "' at index " + this.index + "': " + this.error.message();
      }
   }

   public static record DecodeFromMapFailedProblem(Error<?> error) implements ProblemReporter.Problem {
      @Override
      public String description() {
         return "Failed to decode from map: " + this.error.message();
      }
   }

   static class ListWrapper implements ValueInput.ValueInputList {
      private final ProblemReporter problemReporter;
      private final String name;
      final ValueInputContextHelper context;
      private final ListTag list;

      ListWrapper(ProblemReporter $$0, String $$1, ValueInputContextHelper $$2, ListTag $$3) {
         this.problemReporter = $$0;
         this.name = $$1;
         this.context = $$2;
         this.list = $$3;
      }

      @Override
      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      ProblemReporter reporterForChild(int $$0) {
         return this.problemReporter.forChild(new ProblemReporter.IndexedFieldPathElement(this.name, $$0));
      }

      void reportIndexUnwrapProblem(int $$0, Tag $$1) {
         this.problemReporter.report(new TagValueInput.UnexpectedListElementTypeProblem(this.name, $$0, CompoundTag.TYPE, $$1.getType()));
      }

      @Override
      public Stream<ValueInput> stream() {
         return Streams.mapWithIndex(this.list.stream(), ($$0, $$1) -> {
            if ($$0 instanceof CompoundTag $$2) {
               return TagValueInput.wrapChild(this.reporterForChild((int)$$1), this.context, $$2);
            } else {
               this.reportIndexUnwrapProblem((int)$$1, $$0);
               return null;
            }
         }).filter(Objects::nonNull);
      }

      public Iterator<ValueInput> iterator() {
         final Iterator<Tag> $$0 = this.list.iterator();
         return new AbstractIterator<ValueInput>() {
            private int index;

            @Nullable
            protected ValueInput computeNext() {
               while($$0.hasNext()) {
                  Tag $$0x = (Tag)$$0.next();
                  int $$1 = this.index++;
                  if ($$0x instanceof CompoundTag $$2) {
                     return TagValueInput.wrapChild(ListWrapper.this.reporterForChild($$1), ListWrapper.this.context, $$2);
                  }

                  ListWrapper.this.reportIndexUnwrapProblem($$1, $$0x);
               }

               return this.endOfData();
            }
         };
      }
   }

   static class TypedListWrapper<T> implements ValueInput.TypedInputList<T> {
      private final ProblemReporter problemReporter;
      private final String name;
      final ValueInputContextHelper context;
      final Codec<T> codec;
      private final ListTag list;

      TypedListWrapper(ProblemReporter $$0, String $$1, ValueInputContextHelper $$2, Codec<T> $$3, ListTag $$4) {
         this.problemReporter = $$0;
         this.name = $$1;
         this.context = $$2;
         this.codec = $$3;
         this.list = $$4;
      }

      @Override
      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      void reportIndexUnwrapProblem(int $$0, Tag $$1, Error<?> $$2) {
         this.problemReporter.report(new TagValueInput.DecodeFromListFailedProblem(this.name, $$0, $$1, $$2));
      }

      @Override
      public Stream<T> stream() {
         return Streams.mapWithIndex(this.list.stream(), ($$0, $$1) -> {
            Object var10000;
            Objects.requireNonNull(var10000);
            DataResult $$2 = (DataResult)var10000;
            int $$3 = 0;

            this.codec.parse(this.context.ops(), $$0);
            return switch(SwitchBootstraps.typeSwitch<"typeSwitch",Success,Error>($$2, $$3)) {
               case 0 -> {
                  Success<T> $$4 = (Success)$$2;
                  yield $$4.value();
               }
               case 1 -> {
                  Error<T> $$5 = (Error)$$2;
                  this.reportIndexUnwrapProblem((int)$$1, $$0, $$5);
                  yield $$5.partialValue().orElse(null);
               }
               default -> throw new MatchException(null, null);
            };
         }).filter(Objects::nonNull);
      }

      public Iterator<T> iterator() {
         final ListIterator<Tag> $$0 = this.list.listIterator();
         return new AbstractIterator<T>() {
            @Nullable
            @Override
            protected T computeNext() {
               while($$0.hasNext()) {
                  int $$0x = $$0.nextIndex();
                  Tag $$1 = (Tag)$$0.next();
                  DataResult var10000 = TypedListWrapper.this.codec.parse(TypedListWrapper.this.context.ops(), $$1);
                  Objects.requireNonNull(var10000);
                  DataResult var3 = var10000;
                  byte var4 = 0;
                  switch(SwitchBootstraps.typeSwitch<"typeSwitch",Success,Error>(var3, var4)) {
                     case 0:
                        Success<T> $$2 = (Success)var3;
                        return $$2.value();
                     case 1:
                        Error<T> $$3 = (Error)var3;
                        TypedListWrapper.this.reportIndexUnwrapProblem($$0x, $$1, $$3);
                        if (!$$3.partialValue().isPresent()) {
                           break;
                        }

                        return (T)$$3.partialValue().get();
                     default:
                        throw new MatchException(null, null);
                  }
               }

               return (T)this.endOfData();
            }
         };
      }
   }

   public static record UnexpectedListElementTypeProblem(String name, int index, TagType<?> expected, TagType<?> actual) implements ProblemReporter.Problem {
      @Override
      public String description() {
         return "Expected list '"
            + this.name
            + "' to contain at index "
            + this.index
            + " value of type "
            + this.expected.getName()
            + ", but got "
            + this.actual.getName();
      }
   }

   public static record UnexpectedNonNumberProblem(String name, TagType<?> actual) implements ProblemReporter.Problem {
      @Override
      public String description() {
         return "Expected field '" + this.name + "' to contain number, but got " + this.actual.getName();
      }
   }

   public static record UnexpectedTypeProblem(String name, TagType<?> expected, TagType<?> actual) implements ProblemReporter.Problem {
      @Override
      public String description() {
         return "Expected field '" + this.name + "' to contain value of type " + this.expected.getName() + ", but got " + this.actual.getName();
      }
   }
}
