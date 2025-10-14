package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public final class ListTag extends AbstractList<Tag> implements CollectionTag {
   private static final String WRAPPER_MARKER = "";
   private static final int SELF_SIZE_IN_BYTES = 36;
   public static final TagType<ListTag> TYPE = new TagType.VariableSize<ListTag>() {
      public ListTag load(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.pushDepth();

         ListTag var3;
         try {
            var3 = loadList($$0, $$1);
         } finally {
            $$1.popDepth();
         }

         return var3;
      }

      private static ListTag loadList(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.accountBytes(36L);
         byte $$2 = $$0.readByte();
         int $$3 = readListCount($$0);
         if ($$2 == 0 && $$3 > 0) {
            throw new NbtFormatException("Missing type on ListTag");
         } else {
            $$1.accountBytes(4L, (long)$$3);
            TagType<?> $$4 = TagTypes.getType($$2);
            ListTag $$5 = new ListTag(new ArrayList($$3));

            for(int $$6 = 0; $$6 < $$3; ++$$6) {
               $$5.addAndUnwrap($$4.load($$0, $$1));
            }

            return $$5;
         }
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         $$2.pushDepth();

         StreamTagVisitor.ValueResult var4;
         try {
            var4 = parseList($$0, $$1, $$2);
         } finally {
            $$2.popDepth();
         }

         return var4;
      }

      private static StreamTagVisitor.ValueResult parseList(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         $$2.accountBytes(36L);
         TagType<?> $$3 = TagTypes.getType($$0.readByte());
         int $$4 = readListCount($$0);
         switch($$1.visitList($$3, $$4)) {
            case HALT:
               return StreamTagVisitor.ValueResult.HALT;
            case BREAK:
               $$3.skip($$0, $$4, $$2);
               return $$1.visitContainerEnd();
            default:
               $$2.accountBytes(4L, (long)$$4);
               int $$5 = 0;

               while(true) {
                  label41: {
                     if ($$5 < $$4) {
                        switch($$1.visitElement($$3, $$5)) {
                           case HALT:
                              return StreamTagVisitor.ValueResult.HALT;
                           case BREAK:
                              $$3.skip($$0, $$2);
                              break;
                           case SKIP:
                              $$3.skip($$0, $$2);
                              break label41;
                           default:
                              switch($$3.parse($$0, $$1, $$2)) {
                                 case HALT:
                                    return StreamTagVisitor.ValueResult.HALT;
                                 case BREAK:
                                    break;
                                 default:
                                    break label41;
                              }
                        }
                     }

                     int $$6 = $$4 - 1 - $$5;
                     if ($$6 > 0) {
                        $$3.skip($$0, $$6, $$2);
                     }

                     return $$1.visitContainerEnd();
                  }

                  ++$$5;
               }
         }
      }

      private static int readListCount(DataInput $$0) throws IOException {
         int $$1 = $$0.readInt();
         if ($$1 < 0) {
            throw new NbtFormatException("ListTag length cannot be negative: " + $$1);
         } else {
            return $$1;
         }
      }

      @Override
      public void skip(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.pushDepth();

         try {
            TagType<?> $$2 = TagTypes.getType($$0.readByte());
            int $$3 = $$0.readInt();
            $$2.skip($$0, $$3, $$1);
         } finally {
            $$1.popDepth();
         }
      }

      @Override
      public String getName() {
         return "LIST";
      }

      @Override
      public String getPrettyName() {
         return "TAG_List";
      }
   };
   private final List<Tag> list;

   public ListTag() {
      this(new ArrayList());
   }

   ListTag(List<Tag> $$0) {
      this.list = $$0;
   }

   private static Tag tryUnwrap(CompoundTag $$0) {
      if ($$0.size() == 1) {
         Tag $$1 = $$0.get("");
         if ($$1 != null) {
            return $$1;
         }
      }

      return $$0;
   }

   private static boolean isWrapper(CompoundTag $$0) {
      return $$0.size() == 1 && $$0.contains("");
   }

   private static Tag wrapIfNeeded(byte $$0, Tag $$1) {
      if ($$0 != 10) {
         return $$1;
      } else {
         if ($$1 instanceof CompoundTag $$2 && !isWrapper($$2)) {
            return $$2;
         }

         return wrapElement($$1);
      }
   }

   private static CompoundTag wrapElement(Tag $$0) {
      return new CompoundTag(Map.of("", $$0));
   }

   @Override
   public void write(DataOutput $$0) throws IOException {
      byte $$1 = this.identifyRawElementType();
      $$0.writeByte($$1);
      $$0.writeInt(this.list.size());

      for(Tag $$2 : this.list) {
         wrapIfNeeded($$1, $$2).write($$0);
      }
   }

   @VisibleForTesting
   byte identifyRawElementType() {
      byte $$0 = 0;

      for(Tag $$1 : this.list) {
         byte $$2 = $$1.getId();
         if ($$0 == 0) {
            $$0 = $$2;
         } else if ($$0 != $$2) {
            return 10;
         }
      }

      return $$0;
   }

   public void addAndUnwrap(Tag $$0) {
      if ($$0 instanceof CompoundTag $$1) {
         this.add(tryUnwrap($$1));
      } else {
         this.add($$0);
      }
   }

   @Override
   public int sizeInBytes() {
      int $$0 = 36;
      $$0 += 4 * this.list.size();

      for(Tag $$1 : this.list) {
         $$0 += $$1.sizeInBytes();
      }

      return $$0;
   }

   @Override
   public byte getId() {
      return 9;
   }

   @Override
   public TagType<ListTag> getType() {
      return TYPE;
   }

   @Override
   public String toString() {
      StringTagVisitor $$0 = new StringTagVisitor();
      $$0.visitList(this);
      return $$0.build();
   }

   @Override
   public Tag remove(int $$0) {
      return (Tag)this.list.remove($$0);
   }

   @Override
   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public Optional<CompoundTag> getCompound(int $$0) {
      Tag var3 = this.getNullable($$0);
      return var3 instanceof CompoundTag $$1 ? Optional.of($$1) : Optional.empty();
   }

   public CompoundTag getCompoundOrEmpty(int $$0) {
      return (CompoundTag)this.getCompound($$0).orElseGet(CompoundTag::new);
   }

   public Optional<ListTag> getList(int $$0) {
      Tag var3 = this.getNullable($$0);
      return var3 instanceof ListTag $$1 ? Optional.of($$1) : Optional.empty();
   }

   public ListTag getListOrEmpty(int $$0) {
      return (ListTag)this.getList($$0).orElseGet(ListTag::new);
   }

   public Optional<Short> getShort(int $$0) {
      return this.getOptional($$0).flatMap(Tag::asShort);
   }

   public short getShortOr(int $$0, short $$1) {
      Tag var4 = this.getNullable($$0);
      return var4 instanceof NumericTag $$2 ? $$2.shortValue() : $$1;
   }

   public Optional<Integer> getInt(int $$0) {
      return this.getOptional($$0).flatMap(Tag::asInt);
   }

   public int getIntOr(int $$0, int $$1) {
      Tag var4 = this.getNullable($$0);
      return var4 instanceof NumericTag $$2 ? $$2.intValue() : $$1;
   }

   public Optional<int[]> getIntArray(int $$0) {
      Tag var3 = this.getNullable($$0);
      return var3 instanceof IntArrayTag $$1 ? Optional.of($$1.getAsIntArray()) : Optional.empty();
   }

   public Optional<long[]> getLongArray(int $$0) {
      Tag var3 = this.getNullable($$0);
      return var3 instanceof LongArrayTag $$1 ? Optional.of($$1.getAsLongArray()) : Optional.empty();
   }

   public Optional<Double> getDouble(int $$0) {
      return this.getOptional($$0).flatMap(Tag::asDouble);
   }

   public double getDoubleOr(int $$0, double $$1) {
      Tag var5 = this.getNullable($$0);
      return var5 instanceof NumericTag $$2 ? $$2.doubleValue() : $$1;
   }

   public Optional<Float> getFloat(int $$0) {
      return this.getOptional($$0).flatMap(Tag::asFloat);
   }

   public float getFloatOr(int $$0, float $$1) {
      Tag var4 = this.getNullable($$0);
      return var4 instanceof NumericTag $$2 ? $$2.floatValue() : $$1;
   }

   public Optional<String> getString(int $$0) {
      return this.getOptional($$0).flatMap(Tag::asString);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public String getStringOr(int $$0, String $$1) {
      Tag $$2 = this.getNullable($$0);
      if ($$2 instanceof StringTag var4) {
         <unknown> var10000 = var4;

         try {
            var8 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         return var8;
      } else {
         return $$1;
      }
   }

   @Nullable
   private Tag getNullable(int $$0) {
      return $$0 >= 0 && $$0 < this.list.size() ? (Tag)this.list.get($$0) : null;
   }

   private Optional<Tag> getOptional(int $$0) {
      return Optional.ofNullable(this.getNullable($$0));
   }

   @Override
   public int size() {
      return this.list.size();
   }

   @Override
   public Tag get(int $$0) {
      return (Tag)this.list.get($$0);
   }

   public Tag set(int $$0, Tag $$1) {
      return (Tag)this.list.set($$0, $$1);
   }

   public void add(int $$0, Tag $$1) {
      this.list.add($$0, $$1);
   }

   @Override
   public boolean setTag(int $$0, Tag $$1) {
      this.list.set($$0, $$1);
      return true;
   }

   @Override
   public boolean addTag(int $$0, Tag $$1) {
      this.list.add($$0, $$1);
      return true;
   }

   public ListTag copy() {
      List<Tag> $$0 = new ArrayList(this.list.size());

      for(Tag $$1 : this.list) {
         $$0.add($$1.copy());
      }

      return new ListTag($$0);
   }

   @Override
   public Optional<ListTag> asList() {
      return Optional.of(this);
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else {
         return $$0 instanceof ListTag && Objects.equals(this.list, ((ListTag)$$0).list);
      }
   }

   public int hashCode() {
      return this.list.hashCode();
   }

   @Override
   public Stream<Tag> stream() {
      return super.stream();
   }

   public Stream<CompoundTag> compoundStream() {
      return this.stream().mapMulti(($$0, $$1) -> {
         if ($$0 instanceof CompoundTag $$2) {
            $$1.accept($$2);
         }
      });
   }

   @Override
   public void accept(TagVisitor $$0) {
      $$0.visitList(this);
   }

   @Override
   public void clear() {
      this.list.clear();
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
      byte $$1 = this.identifyRawElementType();
      switch($$0.visitList(TagTypes.getType($$1), this.list.size())) {
         case HALT:
            return StreamTagVisitor.ValueResult.HALT;
         case BREAK:
            return $$0.visitContainerEnd();
         default:
            int $$2 = 0;

            while($$2 < this.list.size()) {
               Tag $$3 = wrapIfNeeded($$1, (Tag)this.list.get($$2));
               switch($$0.visitElement($$3.getType(), $$2)) {
                  case HALT:
                     return StreamTagVisitor.ValueResult.HALT;
                  case BREAK:
                     return $$0.visitContainerEnd();
                  default:
                     switch($$3.accept($$0)) {
                        case HALT:
                           return StreamTagVisitor.ValueResult.HALT;
                        case BREAK:
                           return $$0.visitContainerEnd();
                     }
                  case SKIP:
                     ++$$2;
               }
            }

            return $$0.visitContainerEnd();
      }
   }
}
