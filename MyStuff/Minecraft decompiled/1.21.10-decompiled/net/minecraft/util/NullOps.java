package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractUniversalBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class NullOps implements DynamicOps<Unit> {
   public static final NullOps INSTANCE = new NullOps();
   private static final MapLike<Unit> EMPTY_MAP = new MapLike<Unit>() {
      @Nullable
      public Unit get(Unit $$0) {
         return null;
      }

      @Nullable
      public Unit get(String $$0) {
         return null;
      }

      @Override
      public Stream<Pair<Unit, Unit>> entries() {
         return Stream.empty();
      }
   };

   private NullOps() {
   }

   public <U> U convertTo(DynamicOps<U> $$0, Unit $$1) {
      return $$0.empty();
   }

   public Unit empty() {
      return Unit.INSTANCE;
   }

   public Unit emptyMap() {
      return Unit.INSTANCE;
   }

   public Unit emptyList() {
      return Unit.INSTANCE;
   }

   public Unit createNumeric(Number $$0) {
      return Unit.INSTANCE;
   }

   public Unit createByte(byte $$0) {
      return Unit.INSTANCE;
   }

   public Unit createShort(short $$0) {
      return Unit.INSTANCE;
   }

   public Unit createInt(int $$0) {
      return Unit.INSTANCE;
   }

   public Unit createLong(long $$0) {
      return Unit.INSTANCE;
   }

   public Unit createFloat(float $$0) {
      return Unit.INSTANCE;
   }

   public Unit createDouble(double $$0) {
      return Unit.INSTANCE;
   }

   public Unit createBoolean(boolean $$0) {
      return Unit.INSTANCE;
   }

   public Unit createString(String $$0) {
      return Unit.INSTANCE;
   }

   public DataResult<Number> getNumberValue(Unit $$0) {
      return DataResult.success(0);
   }

   public DataResult<Boolean> getBooleanValue(Unit $$0) {
      return DataResult.success(false);
   }

   public DataResult<String> getStringValue(Unit $$0) {
      return DataResult.success("");
   }

   public DataResult<Unit> mergeToList(Unit $$0, Unit $$1) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Unit> mergeToList(Unit $$0, List<Unit> $$1) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Unit> mergeToMap(Unit $$0, Unit $$1, Unit $$2) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Unit> mergeToMap(Unit $$0, Map<Unit, Unit> $$1) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Unit> mergeToMap(Unit $$0, MapLike<Unit> $$1) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Stream<Pair<Unit, Unit>>> getMapValues(Unit $$0) {
      return DataResult.success(Stream.empty());
   }

   public DataResult<Consumer<BiConsumer<Unit, Unit>>> getMapEntries(Unit $$0) {
      return DataResult.success((Consumer)$$0x -> {
      });
   }

   public DataResult<MapLike<Unit>> getMap(Unit $$0) {
      return DataResult.success(EMPTY_MAP);
   }

   public DataResult<Stream<Unit>> getStream(Unit $$0) {
      return DataResult.success(Stream.empty());
   }

   public DataResult<Consumer<Consumer<Unit>>> getList(Unit $$0) {
      return DataResult.success((Consumer)$$0x -> {
      });
   }

   public DataResult<ByteBuffer> getByteBuffer(Unit $$0) {
      return DataResult.success(ByteBuffer.wrap(new byte[0]));
   }

   public DataResult<IntStream> getIntStream(Unit $$0) {
      return DataResult.success(IntStream.empty());
   }

   public DataResult<LongStream> getLongStream(Unit $$0) {
      return DataResult.success(LongStream.empty());
   }

   public Unit createMap(Stream<Pair<Unit, Unit>> $$0) {
      return Unit.INSTANCE;
   }

   public Unit createMap(Map<Unit, Unit> $$0) {
      return Unit.INSTANCE;
   }

   public Unit createList(Stream<Unit> $$0) {
      return Unit.INSTANCE;
   }

   public Unit createByteList(ByteBuffer $$0) {
      return Unit.INSTANCE;
   }

   public Unit createIntList(IntStream $$0) {
      return Unit.INSTANCE;
   }

   public Unit createLongList(LongStream $$0) {
      return Unit.INSTANCE;
   }

   public Unit remove(Unit $$0, String $$1) {
      return $$0;
   }

   @Override
   public RecordBuilder<Unit> mapBuilder() {
      return new NullOps.NullMapBuilder(this);
   }

   @Override
   public ListBuilder<Unit> listBuilder() {
      return new NullOps.NullListBuilder(this);
   }

   public String toString() {
      return "Null";
   }

   static final class NullListBuilder extends AbstractListBuilder<Unit, Unit> {
      public NullListBuilder(DynamicOps<Unit> $$0) {
         super($$0);
      }

      protected Unit initBuilder() {
         return Unit.INSTANCE;
      }

      protected Unit append(Unit $$0, Unit $$1) {
         return $$0;
      }

      protected DataResult<Unit> build(Unit $$0, Unit $$1) {
         return DataResult.success($$0);
      }
   }

   static final class NullMapBuilder extends AbstractUniversalBuilder<Unit, Unit> {
      public NullMapBuilder(DynamicOps<Unit> $$0) {
         super($$0);
      }

      protected Unit initBuilder() {
         return Unit.INSTANCE;
      }

      protected Unit append(Unit $$0, Unit $$1, Unit $$2) {
         return $$2;
      }

      protected DataResult<Unit> build(Unit $$0, Unit $$1) {
         return DataResult.success($$1);
      }
   }
}
