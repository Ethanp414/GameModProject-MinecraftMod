package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record ShortTag(short value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 10;
   public static final TagType<ShortTag> TYPE = new TagType.StaticSize<ShortTag>() {
      public ShortTag load(DataInput $$0, NbtAccounter $$1) throws IOException {
         return ShortTag.valueOf(readAccounted($$0, $$1));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         return $$1.visit(readAccounted($$0, $$2));
      }

      private static short readAccounted(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.accountBytes(10L);
         return $$0.readShort();
      }

      @Override
      public int size() {
         return 2;
      }

      @Override
      public String getName() {
         return "SHORT";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Short";
      }
   };

   @Deprecated(
      forRemoval = true
   )
   public ShortTag(short param1) {
      this.value = $$0;
   }

   public static ShortTag valueOf(short $$0) {
      return $$0 >= -128 && $$0 <= 1024 ? ShortTag.Cache.cache[$$0 - -128] : new ShortTag($$0);
   }

   @Override
   public void write(DataOutput $$0) throws IOException {
      $$0.writeShort(this.value);
   }

   @Override
   public int sizeInBytes() {
      return 10;
   }

   @Override
   public byte getId() {
      return 2;
   }

   @Override
   public TagType<ShortTag> getType() {
      return TYPE;
   }

   public ShortTag copy() {
      return this;
   }

   @Override
   public void accept(TagVisitor $$0) {
      $$0.visitShort(this);
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
      return this.value;
   }

   @Override
   public byte byteValue() {
      return (byte)(this.value & 255);
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
      $$0.visitShort(this);
      return $$0.build();
   }

   static class Cache {
      private static final int HIGH = 1024;
      private static final int LOW = -128;
      static final ShortTag[] cache = new ShortTag[1153];

      private Cache() {
      }

      static {
         for(int $$0 = 0; $$0 < cache.length; ++$$0) {
            cache[$$0] = new ShortTag((short)(-128 + $$0));
         }
      }
   }
}
