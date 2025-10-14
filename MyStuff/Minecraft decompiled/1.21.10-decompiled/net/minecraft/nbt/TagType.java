package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface TagType<T extends Tag> {
   T load(DataInput var1, NbtAccounter var2) throws IOException;

   StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException;

   default void parseRoot(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
      switch($$1.visitRootEntry(this)) {
         case CONTINUE:
            this.parse($$0, $$1, $$2);
         case HALT:
         default:
            break;
         case BREAK:
            this.skip($$0, $$2);
      }
   }

   void skip(DataInput var1, int var2, NbtAccounter var3) throws IOException;

   void skip(DataInput var1, NbtAccounter var2) throws IOException;

   String getName();

   String getPrettyName();

   static TagType<EndTag> createInvalid(final int $$0) {
      return new TagType<EndTag>() {
         private IOException createException() {
            return new IOException("Invalid tag id: " + $$0);
         }

         public EndTag load(DataInput $$0x, NbtAccounter $$1) throws IOException {
            throw this.createException();
         }

         @Override
         public StreamTagVisitor.ValueResult parse(DataInput $$0x, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
            throw this.createException();
         }

         @Override
         public void skip(DataInput $$0x, int $$1, NbtAccounter $$2) throws IOException {
            throw this.createException();
         }

         @Override
         public void skip(DataInput $$0x, NbtAccounter $$1) throws IOException {
            throw this.createException();
         }

         @Override
         public String getName() {
            return "INVALID[" + $$0 + "]";
         }

         @Override
         public String getPrettyName() {
            return "UNKNOWN_" + $$0;
         }
      };
   }

   public interface StaticSize<T extends Tag> extends TagType<T> {
      @Override
      default void skip(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$0.skipBytes(this.size());
      }

      @Override
      default void skip(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
         $$0.skipBytes(this.size() * $$1);
      }

      int size();
   }

   public interface VariableSize<T extends Tag> extends TagType<T> {
      @Override
      default void skip(DataInput $$0, int $$1, NbtAccounter $$2) throws IOException {
         for(int $$3 = 0; $$3 < $$1; ++$$3) {
            this.skip($$0, $$2);
         }
      }
   }
}
