package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public record FloatTag(float value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 12;
   public static final FloatTag ZERO = new FloatTag(0.0F);
   public static final TagType<FloatTag> TYPE = new TagType.StaticSize<FloatTag>() {
      public FloatTag load(DataInput $$0, NbtAccounter $$1) throws IOException {
         return FloatTag.valueOf(readAccounted($$0, $$1));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         return $$1.visit(readAccounted($$0, $$2));
      }

      private static float readAccounted(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.accountBytes(12L);
         return $$0.readFloat();
      }

      @Override
      public int size() {
         return 4;
      }

      @Override
      public String getName() {
         return "FLOAT";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Float";
      }
   };

   @Deprecated(
      forRemoval = true
   )
   public FloatTag(float param1) {
      this.value = $$0;
   }

   public static FloatTag valueOf(float $$0) {
      return $$0 == 0.0F ? ZERO : new FloatTag($$0);
   }

   @Override
   public void write(DataOutput $$0) throws IOException {
      $$0.writeFloat(this.value);
   }

   @Override
   public int sizeInBytes() {
      return 12;
   }

   @Override
   public byte getId() {
      return 5;
   }

   @Override
   public TagType<FloatTag> getType() {
      return TYPE;
   }

   public FloatTag copy() {
      return this;
   }

   @Override
   public void accept(TagVisitor $$0) {
      $$0.visitFloat(this);
   }

   @Override
   public long longValue() {
      return (long)this.value;
   }

   @Override
   public int intValue() {
      return Mth.floor(this.value);
   }

   @Override
   public short shortValue() {
      return (short)(Mth.floor(this.value) & 65535);
   }

   @Override
   public byte byteValue() {
      return (byte)(Mth.floor(this.value) & 0xFF);
   }

   @Override
   public double doubleValue() {
      return (double)this.value;
   }

   @Override
   public float floatValue() {
      return this.value;
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
      $$0.visitFloat(this);
      return $$0.build();
   }
}
