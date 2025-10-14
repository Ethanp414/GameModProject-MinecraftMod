package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import org.slf4j.Logger;

public final class CompoundTag implements Tag {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<CompoundTag> CODEC = Codec.PASSTHROUGH
      .comapFlatMap(
         $$0 -> {
            Tag $$1 = $$0.convert(NbtOps.INSTANCE).getValue();
            return $$1 instanceof CompoundTag $$2
               ? DataResult.success($$2 == $$0.getValue() ? $$2.copy() : $$2)
               : DataResult.error(() -> "Not a compound tag: " + $$1);
         },
         $$0 -> new Dynamic<>(NbtOps.INSTANCE, $$0.copy())
      );
   private static final int SELF_SIZE_IN_BYTES = 48;
   private static final int MAP_ENTRY_SIZE_IN_BYTES = 32;
   public static final TagType<CompoundTag> TYPE = new TagType.VariableSize<CompoundTag>() {
      public CompoundTag load(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.pushDepth();

         CompoundTag var3;
         try {
            var3 = loadCompound($$0, $$1);
         } finally {
            $$1.popDepth();
         }

         return var3;
      }

      private static CompoundTag loadCompound(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.accountBytes(48L);
         Map<String, Tag> $$2 = Maps.newHashMap();

         byte $$3;
         while(($$3 = $$0.readByte()) != 0) {
            String $$4 = readString($$0, $$1);
            Tag $$5 = CompoundTag.readNamedTagData(TagTypes.getType($$3), $$4, $$0, $$1);
            if ($$2.put($$4, $$5) == null) {
               $$1.accountBytes(36L);
            }
         }

         return new CompoundTag($$2);
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         $$2.pushDepth();

         StreamTagVisitor.ValueResult var4;
         try {
            var4 = parseCompound($$0, $$1, $$2);
         } finally {
            $$2.popDepth();
         }

         return var4;
      }

      private static StreamTagVisitor.ValueResult parseCompound(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         $$2.accountBytes(48L);

         byte $$3;
         label35:
         while(($$3 = $$0.readByte()) != 0) {
            TagType<?> $$4 = TagTypes.getType($$3);
            switch($$1.visitEntry($$4)) {
               case HALT:
                  return StreamTagVisitor.ValueResult.HALT;
               case BREAK:
                  StringTag.skipString($$0);
                  $$4.skip($$0, $$2);
                  break label35;
               case SKIP:
                  StringTag.skipString($$0);
                  $$4.skip($$0, $$2);
                  break;
               default:
                  String $$5 = readString($$0, $$2);
                  switch($$1.visitEntry($$4, $$5)) {
                     case HALT:
                        return StreamTagVisitor.ValueResult.HALT;
                     case BREAK:
                        $$4.skip($$0, $$2);
                        break label35;
                     case SKIP:
                        $$4.skip($$0, $$2);
                        break;
                     default:
                        $$2.accountBytes(36L);
                        switch($$4.parse($$0, $$1, $$2)) {
                           case HALT:
                              return StreamTagVisitor.ValueResult.HALT;
                           case BREAK:
                        }
                  }
            }
         }

         if ($$3 != 0) {
            while(($$3 = $$0.readByte()) != false) {
               StringTag.skipString($$0);
               TagTypes.getType($$3).skip($$0, $$2);
            }
         }

         return $$1.visitContainerEnd();
      }

      private static String readString(DataInput $$0, NbtAccounter $$1) throws IOException {
         String $$2 = $$0.readUTF();
         $$1.accountBytes(28L);
         $$1.accountBytes(2L, (long)$$2.length());
         return $$2;
      }

      @Override
      public void skip(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.pushDepth();

         byte $$2;
         try {
            while(($$2 = $$0.readByte()) != 0) {
               StringTag.skipString($$0);
               TagTypes.getType($$2).skip($$0, $$1);
            }
         } finally {
            $$1.popDepth();
         }
      }

      @Override
      public String getName() {
         return "COMPOUND";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Compound";
      }
   };
   private final Map<String, Tag> tags;

   CompoundTag(Map<String, Tag> $$0) {
      this.tags = $$0;
   }

   public CompoundTag() {
      this(new HashMap());
   }

   @Override
   public void write(DataOutput $$0) throws IOException {
      for(String $$1 : this.tags.keySet()) {
         Tag $$2 = (Tag)this.tags.get($$1);
         writeNamedTag($$1, $$2, $$0);
      }

      $$0.writeByte(0);
   }

   @Override
   public int sizeInBytes() {
      int $$0 = 48;

      for(Entry<String, Tag> $$1 : this.tags.entrySet()) {
         $$0 += 28 + 2 * ((String)$$1.getKey()).length();
         $$0 += 36;
         $$0 += ((Tag)$$1.getValue()).sizeInBytes();
      }

      return $$0;
   }

   public Set<String> keySet() {
      return this.tags.keySet();
   }

   public Set<Entry<String, Tag>> entrySet() {
      return this.tags.entrySet();
   }

   public Collection<Tag> values() {
      return this.tags.values();
   }

   public void forEach(BiConsumer<String, Tag> $$0) {
      this.tags.forEach($$0);
   }

   @Override
   public byte getId() {
      return 10;
   }

   @Override
   public TagType<CompoundTag> getType() {
      return TYPE;
   }

   public int size() {
      return this.tags.size();
   }

   @Nullable
   public Tag put(String $$0, Tag $$1) {
      return (Tag)this.tags.put($$0, $$1);
   }

   public void putByte(String $$0, byte $$1) {
      this.tags.put($$0, ByteTag.valueOf($$1));
   }

   public void putShort(String $$0, short $$1) {
      this.tags.put($$0, ShortTag.valueOf($$1));
   }

   public void putInt(String $$0, int $$1) {
      this.tags.put($$0, IntTag.valueOf($$1));
   }

   public void putLong(String $$0, long $$1) {
      this.tags.put($$0, LongTag.valueOf($$1));
   }

   public void putFloat(String $$0, float $$1) {
      this.tags.put($$0, FloatTag.valueOf($$1));
   }

   public void putDouble(String $$0, double $$1) {
      this.tags.put($$0, DoubleTag.valueOf($$1));
   }

   public void putString(String $$0, String $$1) {
      this.tags.put($$0, StringTag.valueOf($$1));
   }

   public void putByteArray(String $$0, byte[] $$1) {
      this.tags.put($$0, new ByteArrayTag($$1));
   }

   public void putIntArray(String $$0, int[] $$1) {
      this.tags.put($$0, new IntArrayTag($$1));
   }

   public void putLongArray(String $$0, long[] $$1) {
      this.tags.put($$0, new LongArrayTag($$1));
   }

   public void putBoolean(String $$0, boolean $$1) {
      this.tags.put($$0, ByteTag.valueOf($$1));
   }

   @Nullable
   public Tag get(String $$0) {
      return (Tag)this.tags.get($$0);
   }

   public boolean contains(String $$0) {
      return this.tags.containsKey($$0);
   }

   private Optional<Tag> getOptional(String $$0) {
      return Optional.ofNullable((Tag)this.tags.get($$0));
   }

   public Optional<Byte> getByte(String $$0) {
      return this.getOptional($$0).flatMap(Tag::asByte);
   }

   public byte getByteOr(String $$0, byte $$1) {
      Object var4 = this.tags.get($$0);
      return var4 instanceof NumericTag $$2 ? $$2.byteValue() : $$1;
   }

   public Optional<Short> getShort(String $$0) {
      return this.getOptional($$0).flatMap(Tag::asShort);
   }

   public short getShortOr(String $$0, short $$1) {
      Object var4 = this.tags.get($$0);
      return var4 instanceof NumericTag $$2 ? $$2.shortValue() : $$1;
   }

   public Optional<Integer> getInt(String $$0) {
      return this.getOptional($$0).flatMap(Tag::asInt);
   }

   public int getIntOr(String $$0, int $$1) {
      Object var4 = this.tags.get($$0);
      return var4 instanceof NumericTag $$2 ? $$2.intValue() : $$1;
   }

   public Optional<Long> getLong(String $$0) {
      return this.getOptional($$0).flatMap(Tag::asLong);
   }

   public long getLongOr(String $$0, long $$1) {
      Object var5 = this.tags.get($$0);
      return var5 instanceof NumericTag $$2 ? $$2.longValue() : $$1;
   }

   public Optional<Float> getFloat(String $$0) {
      return this.getOptional($$0).flatMap(Tag::asFloat);
   }

   public float getFloatOr(String $$0, float $$1) {
      Object var4 = this.tags.get($$0);
      return var4 instanceof NumericTag $$2 ? $$2.floatValue() : $$1;
   }

   public Optional<Double> getDouble(String $$0) {
      return this.getOptional($$0).flatMap(Tag::asDouble);
   }

   public double getDoubleOr(String $$0, double $$1) {
      Object var5 = this.tags.get($$0);
      return var5 instanceof NumericTag $$2 ? $$2.doubleValue() : $$1;
   }

   public Optional<String> getString(String $$0) {
      return this.getOptional($$0).flatMap(Tag::asString);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public String getStringOr(String $$0, String $$1) {
      Object var5 = this.tags.get($$0);
      if (var5 instanceof StringTag var3) {
         <unknown> var10000 = var3;

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

   public Optional<byte[]> getByteArray(String $$0) {
      Object var3 = this.tags.get($$0);
      return var3 instanceof ByteArrayTag $$1 ? Optional.of($$1.getAsByteArray()) : Optional.empty();
   }

   public Optional<int[]> getIntArray(String $$0) {
      Object var3 = this.tags.get($$0);
      return var3 instanceof IntArrayTag $$1 ? Optional.of($$1.getAsIntArray()) : Optional.empty();
   }

   public Optional<long[]> getLongArray(String $$0) {
      Object var3 = this.tags.get($$0);
      return var3 instanceof LongArrayTag $$1 ? Optional.of($$1.getAsLongArray()) : Optional.empty();
   }

   public Optional<CompoundTag> getCompound(String $$0) {
      Object var3 = this.tags.get($$0);
      return var3 instanceof CompoundTag $$1 ? Optional.of($$1) : Optional.empty();
   }

   public CompoundTag getCompoundOrEmpty(String $$0) {
      return (CompoundTag)this.getCompound($$0).orElseGet(CompoundTag::new);
   }

   public Optional<ListTag> getList(String $$0) {
      Object var3 = this.tags.get($$0);
      return var3 instanceof ListTag $$1 ? Optional.of($$1) : Optional.empty();
   }

   public ListTag getListOrEmpty(String $$0) {
      return (ListTag)this.getList($$0).orElseGet(ListTag::new);
   }

   public Optional<Boolean> getBoolean(String $$0) {
      return this.getOptional($$0).flatMap(Tag::asBoolean);
   }

   public boolean getBooleanOr(String $$0, boolean $$1) {
      return this.getByteOr($$0, (byte)($$1 ? 1 : 0)) != 0;
   }

   public void remove(String $$0) {
      this.tags.remove($$0);
   }

   @Override
   public String toString() {
      StringTagVisitor $$0 = new StringTagVisitor();
      $$0.visitCompound(this);
      return $$0.build();
   }

   public boolean isEmpty() {
      return this.tags.isEmpty();
   }

   protected CompoundTag shallowCopy() {
      return new CompoundTag(new HashMap(this.tags));
   }

   public CompoundTag copy() {
      HashMap<String, Tag> $$0 = new HashMap();
      this.tags.forEach(($$1, $$2) -> $$0.put($$1, $$2.copy()));
      return new CompoundTag($$0);
   }

   @Override
   public Optional<CompoundTag> asCompound() {
      return Optional.of(this);
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else {
         return $$0 instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)$$0).tags);
      }
   }

   public int hashCode() {
      return this.tags.hashCode();
   }

   private static void writeNamedTag(String $$0, Tag $$1, DataOutput $$2) throws IOException {
      $$2.writeByte($$1.getId());
      if ($$1.getId() != 0) {
         $$2.writeUTF($$0);
         $$1.write($$2);
      }
   }

   static Tag readNamedTagData(TagType<?> $$0, String $$1, DataInput $$2, NbtAccounter $$3) {
      try {
         return $$0.load($$2, $$3);
      } catch (IOException var7) {
         CrashReport $$5 = CrashReport.forThrowable(var7, "Loading NBT data");
         CrashReportCategory $$6 = $$5.addCategory("NBT Tag");
         $$6.setDetail("Tag name", $$1);
         $$6.setDetail("Tag type", $$0.getName());
         throw new ReportedNbtException($$5);
      }
   }

   public CompoundTag merge(CompoundTag $$0) {
      for(String $$1 : $$0.tags.keySet()) {
         Tag $$2 = (Tag)$$0.tags.get($$1);
         if ($$2 instanceof CompoundTag $$3) {
            Object var7 = this.tags.get($$1);
            if (var7 instanceof CompoundTag $$4) {
               $$4.merge($$3);
               continue;
            }
         }

         this.put($$1, $$2.copy());
      }

      return this;
   }

   @Override
   public void accept(TagVisitor $$0) {
      $$0.visitCompound(this);
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
      for(Entry<String, Tag> $$1 : this.tags.entrySet()) {
         Tag $$2 = (Tag)$$1.getValue();
         TagType<?> $$3 = $$2.getType();
         StreamTagVisitor.EntryResult $$4 = $$0.visitEntry($$3);
         switch($$4) {
            case HALT:
               return StreamTagVisitor.ValueResult.HALT;
            case BREAK:
               return $$0.visitContainerEnd();
            case SKIP:
               break;
            default:
               $$4 = $$0.visitEntry($$3, (String)$$1.getKey());
               switch($$4) {
                  case HALT:
                     return StreamTagVisitor.ValueResult.HALT;
                  case BREAK:
                     return $$0.visitContainerEnd();
                  case SKIP:
                     break;
                  default:
                     StreamTagVisitor.ValueResult $$5 = $$2.accept($$0);
                     switch($$5) {
                        case HALT:
                           return StreamTagVisitor.ValueResult.HALT;
                        case BREAK:
                           return $$0.visitContainerEnd();
                     }
               }
         }
      }

      return $$0.visitContainerEnd();
   }

   public <T> void store(String $$0, Codec<T> $$1, T $$2) {
      this.store($$0, $$1, NbtOps.INSTANCE, $$2);
   }

   public <T> void storeNullable(String $$0, Codec<T> $$1, @Nullable T $$2) {
      if ($$2 != null) {
         this.store($$0, $$1, $$2);
      }
   }

   public <T> void store(String $$0, Codec<T> $$1, DynamicOps<Tag> $$2, T $$3) {
      this.put($$0, $$1.encodeStart($$2, $$3).getOrThrow());
   }

   public <T> void storeNullable(String $$0, Codec<T> $$1, DynamicOps<Tag> $$2, @Nullable T $$3) {
      if ($$3 != null) {
         this.store($$0, $$1, $$2, $$3);
      }
   }

   public <T> void store(MapCodec<T> $$0, T $$1) {
      this.store($$0, NbtOps.INSTANCE, $$1);
   }

   public <T> void store(MapCodec<T> $$0, DynamicOps<Tag> $$1, T $$2) {
      this.merge((CompoundTag)$$0.encoder().encodeStart($$1, $$2).getOrThrow());
   }

   public <T> Optional<T> read(String $$0, Codec<T> $$1) {
      return this.read($$0, $$1, NbtOps.INSTANCE);
   }

   public <T> Optional<T> read(String $$0, Codec<T> $$1, DynamicOps<Tag> $$2) {
      Tag $$3 = this.get($$0);
      return $$3 == null ? Optional.empty() : $$1.parse($$2, $$3).resultOrPartial($$2x -> LOGGER.error("Failed to read field ({}={}): {}", $$0, $$3, $$2x));
   }

   public <T> Optional<T> read(MapCodec<T> $$0) {
      return this.read($$0, NbtOps.INSTANCE);
   }

   public <T> Optional<T> read(MapCodec<T> $$0, DynamicOps<Tag> $$1) {
      return $$0.decode($$1, $$1.getMap(this).getOrThrow()).resultOrPartial($$0x -> LOGGER.error("Failed to read value ({}): {}", this, $$0x));
   }
}
