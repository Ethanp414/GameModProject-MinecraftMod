package net.minecraft.nbt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;

public class NbtOps implements DynamicOps<Tag> {
   public static final NbtOps INSTANCE = new NbtOps();

   private NbtOps() {
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   // $VF: Inserted dummy exception handlers to handle obfuscated exceptions
   public <U> U convertTo(DynamicOps<U> $$0, Tag $$1) {
      Objects.requireNonNull($$1);
      byte var4 = 0;
      Throwable var42;
      switch(SwitchBootstraps.typeSwitch<"typeSwitch",EndTag,ByteTag,ShortTag,IntTag,LongTag,FloatTag,DoubleTag,ByteArrayTag,StringTag,ListTag,CompoundTag,IntArrayTag,LongArrayTag>(
         $$1, var4
      )) {
         case 0:
            EndTag $$2 = (EndTag)$$1;
            return $$0.empty();
         case 1:
            ByteTag var6 = (ByteTag)$$1;
            ByteTag var54 = var6;

            try {
               var55 = var54.value();
            } catch (Throwable var33) {
               var42 = var33;
               boolean var61 = false;
               break;
            }

            byte var34 = var55;
            return $$0.createByte(var34);
         case 2:
            ShortTag var8 = (ShortTag)$$1;
            ShortTag var52 = var8;

            try {
               var53 = var52.value();
            } catch (Throwable var32) {
               var42 = var32;
               boolean var60 = false;
               break;
            }

            short var35 = var53;
            return $$0.createShort(var35);
         case 3:
            IntTag var10 = (IntTag)$$1;
            IntTag var50 = var10;

            try {
               var51 = var50.value();
            } catch (Throwable var31) {
               var42 = var31;
               boolean var59 = false;
               break;
            }

            int var36 = var51;
            return $$0.createInt(var36);
         case 4:
            LongTag var12 = (LongTag)$$1;
            LongTag var48 = var12;

            try {
               var49 = var48.value();
            } catch (Throwable var30) {
               var42 = var30;
               boolean var58 = false;
               break;
            }

            long var37 = var49;
            return $$0.createLong(var37);
         case 5:
            FloatTag var15 = (FloatTag)$$1;
            FloatTag var46 = var15;

            try {
               var47 = var46.value();
            } catch (Throwable var29) {
               var42 = var29;
               boolean var57 = false;
               break;
            }

            float var38 = var47;
            return $$0.createFloat(var38);
         case 6:
            DoubleTag var17 = (DoubleTag)$$1;
            DoubleTag var44 = var17;

            try {
               var45 = var44.value();
            } catch (Throwable var28) {
               var42 = var28;
               boolean var56 = false;
               break;
            }

            double var39 = var45;
            return $$0.createDouble(var39);
         case 7:
            ByteArrayTag $$9 = (ByteArrayTag)$$1;
            return $$0.createByteList(ByteBuffer.wrap($$9.getAsByteArray()));
         case 8:
            StringTag var21 = (StringTag)$$1;
            StringTag var41 = var21;

            try {
               var43 = var41.value();
            } catch (Throwable var27) {
               var42 = var27;
               boolean var10001 = false;
               break;
            }

            String var40 = var43;
            return $$0.createString(var40);
         case 9:
            ListTag $$11 = (ListTag)$$1;
            return this.convertList($$0, $$11);
         case 10:
            CompoundTag $$12 = (CompoundTag)$$1;
            return this.convertMap($$0, $$12);
         case 11:
            IntArrayTag $$13 = (IntArrayTag)$$1;
            return $$0.createIntList(Arrays.stream($$13.getAsIntArray()));
         case 12:
            LongArrayTag $$14 = (LongArrayTag)$$1;
            return $$0.createLongList(Arrays.stream($$14.getAsLongArray()));
         default:
            throw new MatchException(null, null);
      }

      Throwable var3 = var42;
      throw new MatchException(var3.toString(), var3);
   }

   public DataResult<Number> getNumberValue(Tag $$0) {
      return (DataResult<Number>)$$0.asNumber().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Not a number"));
   }

   public Tag createNumeric(Number $$0) {
      return DoubleTag.valueOf($$0.doubleValue());
   }

   public Tag createByte(byte $$0) {
      return ByteTag.valueOf($$0);
   }

   public Tag createShort(short $$0) {
      return ShortTag.valueOf($$0);
   }

   public Tag createInt(int $$0) {
      return IntTag.valueOf($$0);
   }

   public Tag createLong(long $$0) {
      return LongTag.valueOf($$0);
   }

   public Tag createFloat(float $$0) {
      return FloatTag.valueOf($$0);
   }

   public Tag createDouble(double $$0) {
      return DoubleTag.valueOf($$0);
   }

   public Tag createBoolean(boolean $$0) {
      return ByteTag.valueOf($$0);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public DataResult<String> getStringValue(Tag $$0) {
      if ($$0 instanceof StringTag var2) {
         <unknown> var10000 = var2;

         try {
            var6 = var10000.value();
         } catch (Throwable var5) {
            throw new MatchException(var5.toString(), var5);
         }

         String var4 = var6;
         return DataResult.success(var4);
      } else {
         return DataResult.error(() -> "Not a string");
      }
   }

   public Tag createString(String $$0) {
      return StringTag.valueOf($$0);
   }

   public DataResult<Tag> mergeToList(Tag $$0, Tag $$1) {
      return (DataResult<Tag>)createCollector($$0)
         .map($$1x -> DataResult.success($$1x.accept($$1).result()))
         .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + $$0, $$0));
   }

   public DataResult<Tag> mergeToList(Tag $$0, List<Tag> $$1) {
      return (DataResult<Tag>)createCollector($$0)
         .map($$1x -> DataResult.success($$1x.acceptAll($$1).result()))
         .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + $$0, $$0));
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public DataResult<Tag> mergeToMap(Tag $$0, Tag $$1, Tag $$2) {
      if (!($$0 instanceof CompoundTag) && !($$0 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + $$0, $$0);
      } else if ($$1 instanceof StringTag $$6) {
         <unknown> var10000 = $$6;

         try {
            var10 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         String $$5 = var10;
         CompoundTag $$6x = $$0 instanceof CompoundTag $$5x ? $$5x.shallowCopy() : new CompoundTag();
         $$6x.put($$5, $$2);
         return DataResult.success($$6x);
      } else {
         return DataResult.error(() -> "key is not a string: " + $$1, $$0);
      }
   }

   public DataResult<Tag> mergeToMap(Tag $$0, MapLike<Tag> $$1) {
      if (!($$0 instanceof CompoundTag) && !($$0 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + $$0, $$0);
      } else {
         CompoundTag $$3 = $$0 instanceof CompoundTag $$2 ? $$2.shallowCopy() : new CompoundTag();
         List<Tag> $$4 = new ArrayList();
         $$1.entries().forEach($$2 -> {
            Tag $$3xx = (Tag)$$2.getFirst();
            if ($$3xx instanceof StringTag $$4xx) {
               StringTag var10000 = $$4xx;

               try {
                  var8 = var10000.value();
               } catch (Throwable var7) {
                  throw new MatchException(var7.toString(), var7);
               }

               String $$5 = var8;
               $$3.put($$5, (Tag)$$2.getSecond());
            } else {
               $$4.add($$3xx);
            }
         });
         return !$$4.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + $$4, $$3) : DataResult.success($$3);
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public DataResult<Tag> mergeToMap(Tag $$0, Map<Tag, Tag> $$1) {
      if (!($$0 instanceof CompoundTag) && !($$0 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + $$0, $$0);
      } else {
         CompoundTag $$3 = $$0 instanceof CompoundTag $$2 ? $$2.shallowCopy() : new CompoundTag();
         List<Tag> $$4 = new ArrayList();

         for(Entry<Tag, Tag> $$5 : $$1.entrySet()) {
            Tag $$6 = (Tag)$$5.getKey();
            if ($$6 instanceof StringTag var8) {
               <unknown> var13 = var8;

               try {
                  var14 = var13.value();
               } catch (Throwable var11) {
                  throw new MatchException(var11.toString(), var11);
               }

               String var10 = var14;
               $$3.put(var10, (Tag)$$5.getValue());
            } else {
               $$4.add($$6);
            }
         }

         return !$$4.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + $$4, $$3) : DataResult.success($$3);
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag $$0) {
      return $$0 instanceof CompoundTag $$1
         ? DataResult.success($$1.entrySet().stream().map($$0x -> Pair.of(this.createString((String)$$0x.getKey()), (Tag)$$0x.getValue())))
         : DataResult.error(() -> "Not a map: " + $$0);
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag $$0) {
      return $$0 instanceof CompoundTag $$1 ? DataResult.success((Consumer)$$1x -> {
         for(Entry<String, Tag> $$2 : $$1.entrySet()) {
            $$1x.accept(this.createString((String)$$2.getKey()), (Tag)$$2.getValue());
         }
      }) : DataResult.error(() -> "Not a map: " + $$0);
   }

   public DataResult<MapLike<Tag>> getMap(Tag $$0) {
      return $$0 instanceof CompoundTag $$1
         ? DataResult.success(
            new MapLike<Tag>() {
               // $VF: Could not properly define all variable types!
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               @Nullable
               public Tag get(Tag $$0) {
                  if ($$0 instanceof StringTag var2) {
                     <unknown> var10000 = var2;
      
                     try {
                        var6 = var10000.value();
                     } catch (Throwable var5) {
                        throw new MatchException(var5.toString(), var5);
                     }
      
                     String var4 = var6;
                     return $$1.get(var4);
                  } else {
                     throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + $$0);
                  }
               }
      
               @Nullable
               public Tag get(String $$0) {
                  return $$1.get($$0);
               }
      
               @Override
               public Stream<Pair<Tag, Tag>> entries() {
                  return $$1.entrySet().stream().map($$0 -> Pair.of(NbtOps.this.createString((String)$$0.getKey()), (Tag)$$0.getValue()));
               }
      
               public String toString() {
                  return "MapLike[" + $$1 + "]";
               }
            }
         )
         : DataResult.error(() -> "Not a map: " + $$0);
   }

   public Tag createMap(Stream<Pair<Tag, Tag>> $$0) {
      CompoundTag $$1 = new CompoundTag();
      $$0.forEach($$1x -> {
         Tag $$2 = (Tag)$$1x.getFirst();
         Tag $$3 = (Tag)$$1x.getSecond();
         if ($$2 instanceof StringTag $$4) {
            StringTag var10000 = $$4;

            try {
               var8 = var10000.value();
            } catch (Throwable var7) {
               throw new MatchException(var7.toString(), var7);
            }

            String $$5 = var8;
            $$1.put($$5, $$3);
         } else {
            throw new UnsupportedOperationException("Cannot create map with non-string key: " + $$2);
         }
      });
      return $$1;
   }

   public DataResult<Stream<Tag>> getStream(Tag $$0) {
      return $$0 instanceof CollectionTag $$1 ? DataResult.success($$1.stream()) : DataResult.error(() -> "Not a list");
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(Tag $$0) {
      return $$0 instanceof CollectionTag $$1 ? DataResult.success($$1::forEach) : DataResult.error(() -> "Not a list: " + $$0);
   }

   public DataResult<ByteBuffer> getByteBuffer(Tag $$0) {
      return $$0 instanceof ByteArrayTag $$1 ? DataResult.success(ByteBuffer.wrap($$1.getAsByteArray())) : DynamicOps.super.getByteBuffer($$0);
   }

   public Tag createByteList(ByteBuffer $$0) {
      ByteBuffer $$1 = $$0.duplicate().clear();
      byte[] $$2 = new byte[$$0.capacity()];
      $$1.get(0, $$2, 0, $$2.length);
      return new ByteArrayTag($$2);
   }

   public DataResult<IntStream> getIntStream(Tag $$0) {
      return $$0 instanceof IntArrayTag $$1 ? DataResult.success(Arrays.stream($$1.getAsIntArray())) : DynamicOps.super.getIntStream($$0);
   }

   public Tag createIntList(IntStream $$0) {
      return new IntArrayTag($$0.toArray());
   }

   public DataResult<LongStream> getLongStream(Tag $$0) {
      return $$0 instanceof LongArrayTag $$1 ? DataResult.success(Arrays.stream($$1.getAsLongArray())) : DynamicOps.super.getLongStream($$0);
   }

   public Tag createLongList(LongStream $$0) {
      return new LongArrayTag($$0.toArray());
   }

   public Tag createList(Stream<Tag> $$0) {
      return new ListTag((List<Tag>)$$0.collect(Util.toMutableList()));
   }

   public Tag remove(Tag $$0, String $$1) {
      if ($$0 instanceof CompoundTag $$2) {
         CompoundTag $$3 = $$2.shallowCopy();
         $$3.remove($$1);
         return $$3;
      } else {
         return $$0;
      }
   }

   public String toString() {
      return "NBT";
   }

   @Override
   public RecordBuilder<Tag> mapBuilder() {
      return new NbtOps.NbtRecordBuilder(this);
   }

   private static Optional<NbtOps.ListCollector> createCollector(Tag $$0) {
      if ($$0 instanceof EndTag) {
         return Optional.of(new NbtOps.GenericListCollector());
      } else if ($$0 instanceof CollectionTag $$1) {
         if ($$1.isEmpty()) {
            return Optional.of(new NbtOps.GenericListCollector());
         } else {
            Objects.requireNonNull($$1);
            byte var3 = 0;
            Optional var10000;
            switch(SwitchBootstraps.typeSwitch<"typeSwitch",ListTag,ByteArrayTag,IntArrayTag,LongArrayTag>($$1, var3)) {
               case 0:
                  ListTag $$2 = (ListTag)$$1;
                  var10000 = Optional.of(new NbtOps.GenericListCollector($$2));
                  break;
               case 1:
                  ByteArrayTag $$3 = (ByteArrayTag)$$1;
                  var10000 = Optional.of(new NbtOps.ByteListCollector($$3.getAsByteArray()));
                  break;
               case 2:
                  IntArrayTag $$4 = (IntArrayTag)$$1;
                  var10000 = Optional.of(new NbtOps.IntListCollector($$4.getAsIntArray()));
                  break;
               case 3:
                  LongArrayTag $$5 = (LongArrayTag)$$1;
                  var10000 = Optional.of(new NbtOps.LongListCollector($$5.getAsLongArray()));
                  break;
               default:
                  throw new MatchException(null, null);
            }

            return var10000;
         }
      } else {
         return Optional.empty();
      }
   }

   static class ByteListCollector implements NbtOps.ListCollector {
      private final ByteArrayList values = new ByteArrayList();

      public ByteListCollector(byte[] $$0) {
         this.values.addElements(0, $$0);
      }

      @Override
      public NbtOps.ListCollector accept(Tag $$0) {
         if ($$0 instanceof ByteTag $$1) {
            this.values.add($$1.byteValue());
            return this;
         } else {
            return new NbtOps.GenericListCollector(this.values).accept($$0);
         }
      }

      @Override
      public Tag result() {
         return new ByteArrayTag(this.values.toByteArray());
      }
   }

   static class GenericListCollector implements NbtOps.ListCollector {
      private final ListTag result = new ListTag();

      GenericListCollector() {
      }

      GenericListCollector(ListTag $$0) {
         this.result.addAll($$0);
      }

      public GenericListCollector(IntArrayList $$0) {
         $$0.forEach($$0x -> this.result.add(IntTag.valueOf($$0x)));
      }

      public GenericListCollector(ByteArrayList $$0) {
         $$0.forEach($$0x -> this.result.add(ByteTag.valueOf($$0x)));
      }

      public GenericListCollector(LongArrayList $$0) {
         $$0.forEach($$0x -> this.result.add(LongTag.valueOf($$0x)));
      }

      @Override
      public NbtOps.ListCollector accept(Tag $$0) {
         this.result.add($$0);
         return this;
      }

      @Override
      public Tag result() {
         return this.result;
      }
   }

   static class IntListCollector implements NbtOps.ListCollector {
      private final IntArrayList values = new IntArrayList();

      public IntListCollector(int[] $$0) {
         this.values.addElements(0, $$0);
      }

      @Override
      public NbtOps.ListCollector accept(Tag $$0) {
         if ($$0 instanceof IntTag $$1) {
            this.values.add($$1.intValue());
            return this;
         } else {
            return new NbtOps.GenericListCollector(this.values).accept($$0);
         }
      }

      @Override
      public Tag result() {
         return new IntArrayTag(this.values.toIntArray());
      }
   }

   interface ListCollector {
      NbtOps.ListCollector accept(Tag var1);

      default NbtOps.ListCollector acceptAll(Iterable<Tag> $$0) {
         NbtOps.ListCollector $$1 = this;

         for(Tag $$2 : $$0) {
            $$1 = $$1.accept($$2);
         }

         return $$1;
      }

      default NbtOps.ListCollector acceptAll(Stream<Tag> $$0) {
         return this.acceptAll($$0::iterator);
      }

      Tag result();
   }

   static class LongListCollector implements NbtOps.ListCollector {
      private final LongArrayList values = new LongArrayList();

      public LongListCollector(long[] $$0) {
         this.values.addElements(0, $$0);
      }

      @Override
      public NbtOps.ListCollector accept(Tag $$0) {
         if ($$0 instanceof LongTag $$1) {
            this.values.add($$1.longValue());
            return this;
         } else {
            return new NbtOps.GenericListCollector(this.values).accept($$0);
         }
      }

      @Override
      public Tag result() {
         return new LongArrayTag(this.values.toLongArray());
      }
   }

   class NbtRecordBuilder extends AbstractStringBuilder<Tag, CompoundTag> {
      protected NbtRecordBuilder(final NbtOps param1) {
         super(var1);
      }

      protected CompoundTag initBuilder() {
         return new CompoundTag();
      }

      protected CompoundTag append(String $$0, Tag $$1, CompoundTag $$2) {
         $$2.put($$0, $$1);
         return $$2;
      }

      protected DataResult<Tag> build(CompoundTag $$0, Tag $$1) {
         if ($$1 == null || $$1 == EndTag.INSTANCE) {
            return DataResult.success($$0);
         } else if (!($$1 instanceof CompoundTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + $$1, $$1);
         } else {
            CompoundTag $$2 = (CompoundTag)$$1;
            CompoundTag $$3 = $$2.shallowCopy();

            for(Entry<String, Tag> $$4 : $$0.entrySet()) {
               $$3.put((String)$$4.getKey(), (Tag)$$4.getValue());
            }

            return DataResult.success($$3);
         }
      }
   }
}
