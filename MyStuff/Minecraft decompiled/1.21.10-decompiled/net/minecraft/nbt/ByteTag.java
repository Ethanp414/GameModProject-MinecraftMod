package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record ByteTag(byte value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 9;
   public static final TagType<ByteTag> TYPE = new TagType.StaticSize<ByteTag>() {
      public ByteTag load(DataInput $$0, NbtAccounter $$1) throws IOException {
         return ByteTag.valueOf(readAccounted($$0, $$1));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         return $$1.visit(readAccounted($$0, $$2));
      }

      private static byte readAccounted(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.accountBytes(9L);
         return $$0.readByte();
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public String getName() {
         return "BYTE";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Byte";
      }
   };
   public static final ByteTag ZERO = valueOf((byte)0);
   public static final ByteTag ONE = valueOf((byte)1);

   @Deprecated(
      forRemoval = true
   )
   public ByteTag(byte param1) {
      this.value = $$0;
   }

   public static ByteTag valueOf(byte $$0) {
      return ByteTag.Cache.cache[128 + $$0];
   }

   public static ByteTag valueOf(boolean $$0) {
      return $$0 ? ONE : ZERO;
   }

   @Override
   public void write(DataOutput $$0) throws IOException {
      $$0.writeByte(this.value);
   }

   @Override
   public int sizeInBytes() {
      return 9;
   }

   @Override
   public byte getId() {
      return 1;
   }

   @Override
   public TagType<ByteTag> getType() {
      return TYPE;
   }

   public ByteTag copy() {
      return this;
   }

   @Override
   public void accept(TagVisitor $$0) {
      $$0.visitByte(this);
   }

   @Override
   public long longValue() {
      return (long)this.value;
   }

   @Override
   public int intValue() {
      return this.value;
   }

   @Override
   public short shortValue() {
      return (short)this.value;
   }

   @Override
   public byte byteValue() {
      return this.value;
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
      $$0.visitByte(this);
      return $$0.build();
   }

   static class Cache {
      static final ByteTag[] cache = new ByteTag[256];

      private Cache() {
      }

      static {
         for(int $$0 = 0; $$0 < cache.length; ++$$0) {
            cache[$$0] = new ByteTag((byte)($$0 - 128));
         }
      }
   }
}
