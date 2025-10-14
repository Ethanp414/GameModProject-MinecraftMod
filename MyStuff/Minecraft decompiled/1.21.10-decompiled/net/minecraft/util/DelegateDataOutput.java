package net.minecraft.util;

import java.io.DataOutput;
import java.io.IOException;

public class DelegateDataOutput implements DataOutput {
   private final DataOutput parent;

   public DelegateDataOutput(DataOutput $$0) {
      this.parent = $$0;
   }

   public void write(int $$0) throws IOException {
      this.parent.write($$0);
   }

   public void write(byte[] $$0) throws IOException {
      this.parent.write($$0);
   }

   public void write(byte[] $$0, int $$1, int $$2) throws IOException {
      this.parent.write($$0, $$1, $$2);
   }

   public void writeBoolean(boolean $$0) throws IOException {
      this.parent.writeBoolean($$0);
   }

   public void writeByte(int $$0) throws IOException {
      this.parent.writeByte($$0);
   }

   public void writeShort(int $$0) throws IOException {
      this.parent.writeShort($$0);
   }

   public void writeChar(int $$0) throws IOException {
      this.parent.writeChar($$0);
   }

   public void writeInt(int $$0) throws IOException {
      this.parent.writeInt($$0);
   }

   public void writeLong(long $$0) throws IOException {
      this.parent.writeLong($$0);
   }

   public void writeFloat(float $$0) throws IOException {
      this.parent.writeFloat($$0);
   }

   public void writeDouble(double $$0) throws IOException {
      this.parent.writeDouble($$0);
   }

   public void writeBytes(String $$0) throws IOException {
      this.parent.writeBytes($$0);
   }

   public void writeChars(String $$0) throws IOException {
      this.parent.writeChars($$0);
   }

   public void writeUTF(String $$0) throws IOException {
      this.parent.writeUTF($$0);
   }
}
