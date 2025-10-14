package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record LongTag(long value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 16;
   public static final TagType<LongTag> TYPE = new TagType.StaticSize<LongTag>() {
      public LongTag load(DataInput $$0, NbtAccounter $$1) throws IOException {
         return LongTag.valueOf(readAccounted($$0, $$1));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         return $$1.visit(readAccounted($$0, $$2));
      }

      private static long readAccounted(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.accountBytes(16L);
         return $$0.readLong();
      }

      @Override
      public int size() {
         return 8;
      }

      @Override
      public String getName() {
         return "LONG";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Long";
      }
   };

   @Deprecated(
      forRemoval = true
   )
   public LongTag(long param1) {
      this.value = $$0;
   }

   public static LongTag valueOf(long $$0) {
      return $$0 >= -128L && $$0 <= 1024L ? LongTag.Cache.cache[(int)$$0 - -128] : new LongTag($$0);
   }

   @Override
   public void write(DataOutput $$0) throws IOException {
      $$0.writeLong(this.value);
   }

   @Override
   public int sizeInBytes() {
      return 16;
   }

   @Override
   public byte getId() {
      return 4;
   }

   @Override
   public TagType<LongTag> getType() {
      return TYPE;
   }

   public LongTag copy() {
      return this;
   }

   @Override
   public void accept(TagVisitor $$0) {
      $$0.visitLong(this);
   }

   @Override
   public long longValue() {
      return this.value;
   }

   @Override
   public int intValue() {
      return (int)(this.value & -1L);
   }

   @Override
   public short shortValue() {
      return (short)((int)(this.value & 65535L));
   }

   @Override
   public byte byteValue() {
      return (byte)((int)(this.value & 255L));
   }

   @Override
   public double doubleValue() {
      return (double)this.value;
   }

   @Override
   public float floatValue() {
      return (float)this.value;
   }

   @Override
   public Number box() {
      return this.value;
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
      return $$0.visit(this.value);
   }

   @Override
   public String toString() {
      StringTagVisitor $$0 = new StringTagVisitor();
      $$0.visitLong(this);
      return $$0.build();
   }

   static class Cache {
      private static final int HIGH = 1024;
      private static final int LOW = -128;
      static final LongTag[] cache = new LongTag[1153];

      private Cache() {
      }

      static {
         for(int $$0 = 0; $$0 < cache.length; ++$$0) {
            cache[$$0] = new LongTag((long)(-128 + $$0));
         }
      }
   }
}
